package com.dollarandtrump.angelcar.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.activeandroid.util.SQLiteUtils;
import com.dollarandtrump.angelcar.Adapter.MainViewPagerAdapter;
import com.dollarandtrump.angelcar.MainApplication;
import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.dao.ProfileDao;
import com.dollarandtrump.angelcar.dialog.InformationDialog;
import com.dollarandtrump.angelcar.dialog.ShopEditDialog;
import com.dollarandtrump.angelcar.dialog.ShopUpLoadDialog;
import com.dollarandtrump.angelcar.fragment.FeedPostFragment;
import com.dollarandtrump.angelcar.manager.bus.MainThreadBus;
import com.dollarandtrump.angelcar.model.ActivityResultEvent;
import com.dollarandtrump.angelcar.model.ConversationCache;
import com.dollarandtrump.angelcar.utils.TabEntity;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.messaging.RemoteMessage;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity{

    private  final int REQUEST_CODE_ASK_PERMISSIONS = 123;

    @Bind(R.id.toolbar_top) Toolbar toolbar;
    @Bind(R.id.viewpager) ViewPager viewPager;
    @Bind(R.id.floating_action_menu_fab) FloatingActionMenu menuFab;
//    @Bind(R.id.fab_dealers) FloatingActionButton fabAcDeposit;
//    @Bind(R.id.fab_delegate) FloatingActionButton fabAcDealer;

    @Inject SharedPreferences sharedPreferences;
    @Inject
    @Named("default")
    SharedPreferences preferencesDefault;

    private static final String TAG = "MainActivity";

    @Bind(R.id.tl_1) CommonTabLayout mTabLayout;
    private String[] mTitles = {"Home", "Finance", "Shop", "Menu"};
    private int[] mIconSelectIds = {
            R.drawable.ic_tab_select_home, R.drawable.ic_tab_select_finance,
            R.drawable.ic_tab_select_shop, R.drawable.ic_tab_select_menu};
    private int[] mIconUnSelectIds = {
            R.drawable.ic_tab_home, R.drawable.ic_tab_finance,
            R.drawable.ic_tab_shop, R.drawable.ic_tab_menu};
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    private Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
//        initInstance();
        initViewPager();

        ((MainApplication) getApplication()).getApplicationComponent().inject(this);

        menuFab.setClosedOnTouchOutside(true);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ShopUpLoadDialog.REQUEST_CODE && resultCode == RESULT_OK){
           // requestCode ShopUpLoadDialog
            MainThreadBus.getInstance()
                   .post(new ActivityResultEvent(requestCode,resultCode,data));
        }
        if (requestCode == ShopEditDialog.REQUEST_CODE && resultCode == RESULT_OK){
            MainThreadBus.getInstance()
                    .post(new ActivityResultEvent(requestCode,resultCode,data));
        }
    }


//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        switch (requestCode) {
//            case REQUEST_CODE_ASK_PERMISSIONS:
//                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    // Permission Granted
////                    dialogConfirmFragment();
//                } else {
//                    // Permission Denied
//                }
//                break;
//            default:
//                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        }
//    }

//    @Subscribe //Produce form RegistrationAlertFragment.java
//    public void onRegistrationEmail(RegistrationResult result){
//        if (result.getResult() == RegistrationAlertFragment.REGISTRATION_OK){
//            // ติดต่อ server
//            Call<RegisterResultDao> call = HttpManager.getInstance().getService().registrationEmail(result.getEmail());
//            call.enqueue(callbackRegistrationEmail);
//
//
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mian, menu);
        mMenu = menu;
        //init notification
        setIconMessage(menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        MainThreadBus.getInstance().register(this);

        /*RxNotification.with(this)
                .observableNotification().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                if (aBoolean) { // new
                    mMenu.getItem(0).setIcon(R.drawable.ic_alert_message);
                }else {
                    mMenu.getItem(0).setIcon(R.drawable.ic_message);
                    sharedPreferences.edit().putBoolean("notification_chat",false).apply();
                }
            }
        });*/

        setIconMessage(mMenu);
    }

    @Override
    protected void onStop() {
        super.onStop();
        MainThreadBus.getInstance().unregister(this );

        /*RxNotification.with(this).onDestroy();*/
    }

    @Subscribe
    public void scrollingShowAndHide(FeedPostFragment.Scrolling scrolling){
        if (scrolling.getScroll() == FeedPostFragment.Scrolling.SCROLL_UP){
            showFabButton();
        }else {
            hideFabButton();
        }
    }

    private void showFabButton(){
             menuFab.showMenuButton(true);
    }

    private void hideFabButton(){
            menuFab.hideMenuButton(false);
    }

    @OnClick({R.id.fab_dealers,R.id.fab_delegate,R.id.fab_owner})
    public void fabButtonAction(View v){
        ProfileDao mProfile = SQLiteUtils.rawQuerySingle(ProfileDao.class,"SELECT * FROM Profile",null);
        if (mProfile == null || mProfile.getShopName() == null || mProfile.getUrlShopLogo().contains("default.png")){
            alertDialogEditShop();
        }else {
            switch (v.getId()) {

                case R.id.fab_dealers:
                    String name = preferencesDefault.getString("pre_name",null);
                    String tel = preferencesDefault.getString("pre_phone",null);
                    if (name == null || tel == null || name.trim().equals("") || name.trim().equals("")){
                        alertDialogInformation();
                    }else {
                        startActivity(postCar(2, PostActivity.class));
                    }
                    break;
                case R.id.fab_delegate:
                    startActivity(postCar(1,PostActivity.class));
                    break;
                case R.id.fab_owner:
                    startActivity(postCar(0,PostActivity.class));
                    break;
                default:
                    break;
            }
        }
    }

    private void alertDialogEditShop() {
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(R.string.alert)
                .setMessage(R.string.alert_edit_shop)
                .setPositiveButton(R.string.goto_shop, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        viewPager.setCurrentItem(2,true);
                    }
                })
                .setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void alertDialogInformation(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("InformationDialog");
        if (fragment != null){
            ft.remove(fragment);
        }
        InformationDialog dialog = new InformationDialog();
        dialog.show(getSupportFragmentManager(),"InformationDialog");
    }

    public void setIconMessage(Menu menu) {
        if (menu == null) return;
        if (findMessageNotRead("Topic","officer") != null || findMessageNotRead("Buy","shop") != null ||
                findMessageNotRead("Sell","user") != null){
            menu.getItem(0).setIcon(R.drawable.ic_alert_message2);
        }else {
            menu.getItem(0).setIcon(R.drawable.ic_message);
        }
    }

    private ConversationCache findMessageNotRead(String type,String messageBy){
        ConversationCache cache = SQLiteUtils.rawQuerySingle(ConversationCache.class,"SELECT * FROM Conversation INNER JOIN MessageDao ON Conversation.Message = MessageDao.Id WHERE Conversation.ConversationType = '"+type+"' And MessageDao.MessageBy = '"+messageBy+"'  AND MessageDao.MessageStatus = 0",null);
    return cache;
    }

    @Subscribe
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String type = remoteMessage.getData().get("type");
        if (type.equals("chatfinance") || type.equals("chatrefinance") || type.equals("chatpawn") || type.equals("chatcar")){
            setIconMessage(mMenu);
        }
    }


    private void initViewPager() {
        viewPager.setAdapter(new MainViewPagerAdapter(getSupportFragmentManager()));
//        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(onPageChangeListener);

        for (int i = 0; i < mTitles.length; i++) {
            mTabEntities.add(new TabEntity(mTitles[i], mIconSelectIds[i],mIconUnSelectIds[i]));
        }
        mTabLayout.setTabData(mTabEntities);
//        mTabLayout.showDot(2);
//        mTabLayout.showMsg(0,10);
//        mTabLayout.setMsgMargin(0,-5,0);
        mTabLayout.setOnTabSelectListener(onTabSelectListener);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_chat) {
            startActivity(initIntent(ConversationActivity.class));
            overridePendingTransition(R.anim.activity_slide_left_in,R.anim.activity_out);
            return true;
        }else if (id == R.id.action_follow){
            startActivity(initIntent(FollowActivity.class));
            overridePendingTransition(R.anim.activity_slide_left_in,R.anim.activity_out);
        }
        return super.onOptionsItemSelected(item);
    }


    private Intent initIntent(Class<?> cls){
        return new Intent(MainActivity.this,cls);
    }

    private Intent postCar(int category,Class<?> cls){
        Intent intent = new Intent(MainActivity.this,cls);
        intent.putExtra("category",category);
        return intent;
    }

    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        if(menuFab.isOpened()) {
            menuFab.close(true);
        }else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
//            Snackbar.make(getWindow().getDecorView(), "Please click BACK again to exit", Snackbar.LENGTH_SHORT).show();
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    /**************
    *Listener Zone*
    ***************/
   /* Callback<RegisterResultDao> callbackRegistrationEmail = new Callback<RegisterResultDao>() {
        @Override
        public void onResponse(Call<RegisterResultDao> call, Response<RegisterResultDao> response) {
            if (response.isSuccessful()) {
                // Save Cache
                Registration.getInstance().save(response.body());
                Toast.makeText(MainActivity.this, "ลงทะเบียนเรียบร้อยแล้ว "+response.body().getUserId() +" "+response.body().getShopId(), Toast.LENGTH_LONG).show();
                String user = Registration.getInstance().getUserId();
                String shop = Registration.getInstance().getShopRef();
                HttpManager.getInstance().getService().sendTokenRegistration(user,shop,FirebaseInstanceId.getInstance().getToken())
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<ResponseDao>() {
                            @Override
                            public void onCompleted() {
                            }
                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "onError: ",e);
                            }

                            @Override
                            public void onNext(ResponseDao responseDao) {
                                Log.i(TAG, "onNext: "+ responseDao);
                            }
                        });
            } else {
                Toast.makeText(MainActivity.this, "" + response.errorBody(), Toast.LENGTH_SHORT).show();
            }
        }


        @Override
        public void onFailure(Call<RegisterResultDao> call, Throwable t) {
            Toast.makeText(MainActivity.this, "" + t.toString(), Toast.LENGTH_SHORT).show();
        }
    };*/

    OnTabSelectListener onTabSelectListener = new OnTabSelectListener() {
        @Override
        public void onTabSelect(int position) {
             viewPager.setCurrentItem(position);
            if (position == 0)
                showFabButton();
            else
                hideFabButton();
        }
        @Override
        public void onTabReselect(int position) {

        }
    };

    ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            mTabLayout.setCurrentTab(position);
            if (position == 0)
                showFabButton();
            else
                hideFabButton();
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

}
