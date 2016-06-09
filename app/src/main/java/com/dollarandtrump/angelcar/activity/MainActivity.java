package com.dollarandtrump.angelcar.activity;

import android.Manifest;
import android.accounts.AccountManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dollarandtrump.angelcar.Adapter.MainViewPagerAdapter;
import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.dao.RegisterResultDao;
import com.dollarandtrump.angelcar.dao.Results;
import com.dollarandtrump.angelcar.dialog.ShopEditDialog;
import com.dollarandtrump.angelcar.dialog.ShopUpLoadDialog;
import com.dollarandtrump.angelcar.fragment.FeedPostFragment;
import com.dollarandtrump.angelcar.fragment.RegistrationAlertFragment;
import com.dollarandtrump.angelcar.manager.ActivityResultEvent;
import com.dollarandtrump.angelcar.manager.Registration;
import com.dollarandtrump.angelcar.manager.bus.BusProvider;
import com.dollarandtrump.angelcar.manager.bus.MainThreadBus;
import com.dollarandtrump.angelcar.manager.http.HttpManager;
import com.dollarandtrump.angelcar.utils.RegistrationResult;
import com.dollarandtrump.angelcar.utils.TabEntity;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.RemoteMessage;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private  final int EMAIL_RESOLUTION_REQUEST = 333;
    private  final int REQUEST_CODE_ASK_PERMISSIONS = 123;


    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    @Bind(R.id.toolbar_top) Toolbar toolbar;
    @Bind(R.id.viewpager) ViewPager viewPager;
    @Bind(R.id.menu_fab) FloatingActionMenu menuFab;
    @Bind(R.id.fab_ac_Deposit) FloatingActionButton fabAcDeposit;
    @Bind(R.id.fab_ac_Dealer) FloatingActionButton fabAcDealer;



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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_main);
        ButterKnife.bind(this);

        initInstance();
        initToolbars();
        initViewPager();

        fabAcDealer.setEnabled(false);
        fabAcDeposit.setEnabled(false);
        menuFab.setClosedOnTouchOutside(true);
    }

//  googlePicker
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EMAIL_RESOLUTION_REQUEST && resultCode == RESULT_OK) {
            String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            Toast.makeText(MainActivity.this,accountName,Toast.LENGTH_LONG).show();
            Call<RegisterResultDao> call = HttpManager.getInstance().getService().registrationEmail(accountName);
            call.enqueue(callbackRegistrationEmail);
        }

        if (requestCode == ShopUpLoadDialog.REQUEST_CODE && resultCode == RESULT_OK){
           // requestCode ShopUpLoadDialog
           Log.i(TAG, "onActivityResult: "+requestCode+" , "+resultCode);
           BusProvider.getInstance()
                   .post(new ActivityResultEvent(requestCode,resultCode,data));
        }

        if (requestCode == ShopEditDialog.REQUEST_CODE && resultCode == RESULT_OK){
            BusProvider.getInstance()
                    .post(new ActivityResultEvent(requestCode,resultCode,data));
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Toast.makeText(MainActivity.this,"RequestPermission ::"+requestCode,Toast.LENGTH_LONG).show();
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
//                    dialogConfirmFragment();
                } else {
                    // Permission Denied
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void initInstance() {
        boolean first = Registration.getInstance().isFirstApp();
        checkRegistrationEmail(first);
    }

    private void checkRegistrationEmail(boolean first_init) {
        if (!first_init){
//            if (!checkPermissionAccountApi23()){
//                dialogConfirmFragment();
//            }
            Intent googlePicker =
                    AccountPicker.newChooseAccountIntent(null, null,
                            new String[]{"com.google"}, true, null, null, null, null);
            startActivityForResult(googlePicker, EMAIL_RESOLUTION_REQUEST);
        }else {
            // กรณีลงทะเบียนแล้วให้ เช็ค cache // หากไม่พบ ให้ Registration Email
            String cache_User = Registration.getInstance().getUserId();
            if (cache_User != null){
                Toast.makeText(MainActivity.this,cache_User,Toast.LENGTH_LONG).show();
                Toast.makeText(MainActivity.this,Registration.getInstance().getShopRef(),Toast.LENGTH_LONG).show();
            }
        }
    }

    private void dialogConfirmFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("RegistrationAlertFragment");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        RegistrationAlertFragment fragment = RegistrationAlertFragment.newInstance();
        fragment.setCancelable(false);
        fragment.show(ft, "RegistrationAlertFragment");
    }

    private boolean checkPermissionAccountApi23(){
        if (Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.GET_ACCOUNTS)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.GET_ACCOUNTS)) {
                    showMessageOKCancel("AngelCar ต้องการสิทธิ์ในการเข้าถึงบัญชี", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.GET_ACCOUNTS},
                                    REQUEST_CODE_ASK_PERMISSIONS);
                        }
                    });

                }else {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.GET_ACCOUNTS},
                            REQUEST_CODE_ASK_PERMISSIONS);
                }

            }else {
                dialogConfirmFragment();
            }
        return true;
        }
        return false;
    }



    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }


    @Subscribe //Produce form RegistrationAlertFragment.java
    public void onRegistrationEmail(RegistrationResult result){
        if (result.getResult() == RegistrationAlertFragment.REGISTRATION_OK){
            // ติดต่อ server
            Call<RegisterResultDao> call = HttpManager.getInstance().getService().registrationEmail(result.getEmail());
            call.enqueue(callbackRegistrationEmail);


        }
    }

    @Subscribe
    public void subscribeNotification(RemoteMessage remoteMessage){
        if (remoteMessage != null && remoteMessage.getData() != null){
            final Map<String, String> data = remoteMessage.getData();
            Log.i(TAG, "subscribeNotification: "+ data.get("type"));
            Log.i(TAG, "subscribeNotification: "+ data.get("roomid"));
                    Toast.makeText(MainActivity.this,data.get("type")+" , "+data.get("roomid"),
                            Toast.LENGTH_SHORT).show();
        }else {
            Log.e(TAG, "subscribeNotification: error sub");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        registerReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        unregisterReceiver();
    }

    @Override
    protected void onStart() {
        super.onStart();
        BusProvider.getInstance().register(this);
        MainThreadBus.getInstance().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        BusProvider.getInstance().unregister(this);
        MainThreadBus.getInstance().unregister(this );
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

    @OnClick({R.id.fab_ac_Deposit,R.id.fab_ac_Dealer,R.id.fab_ac_PostSell})
    public void fabButtonAction(View v){
        switch (v.getId()){
            case R.id.fab_ac_Deposit:
                Snackbar.make(getWindow().getDecorView(),"กำลังพัฒนาระบบ",Snackbar.LENGTH_SHORT).show();
                break;
            case R.id.fab_ac_Dealer:
                Snackbar.make(getWindow().getDecorView(),"กำลังพัฒนาระบบ",Snackbar.LENGTH_SHORT).show();
                break;
            case R.id.fab_ac_PostSell:
                startActivity(initIntent(PostActivity.class));
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mian,menu);
        return true;
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

    private View tabViewCustom(String title, int drawable){
        TextView textView = new TextView(this);
        textView.setText(title);
        textView.setCompoundDrawablesWithIntrinsicBounds( 0 ,drawable, 0, 0);
        return textView;
    }

    private void initToolbars() {
        setSupportActionBar(toolbar);
//        drawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.hello_world, R.string.hello_world);
//        drawerLayout.setDrawerListener(drawerToggle);

    }
//    public void onPostCreate(Bundle savedInstanceState) { //ตัว fix Hamburger
//        super.onPostCreate(savedInstanceState);
//        drawerToggle.syncState();
//    }

//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        drawerToggle.onConfigurationChanged(newConfig);
//    }

    public boolean onOptionsItemSelected(MenuItem item) { //ตัว select เรียก slide จาก Hamburger
//        if (drawerToggle.onOptionsItemSelected(item))
//            return true;
        int id = item.getItemId();
        if (id == R.id.action_chat) {
            startActivity(initIntent(ViewChatAllActivity.class));
            overridePendingTransition(R.anim.hash_tag_slide_left_in,R.anim.activity_out);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private Intent initIntent(Class<?> cls){
        return new Intent(MainActivity.this,cls);
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            return false;
        }
        return true;
    }

    /**************
    *Listener Zone*
    ***************/
    Callback<RegisterResultDao> callbackRegistrationEmail = new Callback<RegisterResultDao>() {
        @Override
        public void onResponse(Call<RegisterResultDao> call, Response<RegisterResultDao> response) {
            if (response.isSuccessful()) {
                // Save Cache
                Registration.getInstance().save(response.body());
                Toast.makeText(MainActivity.this, "ลงทะเบียนเรียบร้อยแล้ว "+response.body().getUserId() +" "+response.body().getShopId(), Toast.LENGTH_LONG).show();
                String user = Registration.getInstance().getUserId();
                HttpManager.getInstance().getService().sendTokenRegistration(user, FirebaseInstanceId.getInstance().getToken())
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<Results>() {
                            @Override
                            public void onCompleted() {
                                Log.i(TAG, "onCompleted: ");
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "onError: ",e);
                            }

                            @Override
                            public void onNext(Results results) {
                                Log.i(TAG, "onNext: "+results);
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
    };

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
