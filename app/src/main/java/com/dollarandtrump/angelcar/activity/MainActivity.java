package com.dollarandtrump.angelcar.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
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
import com.dollarandtrump.angelcar.utils.Log;
import com.dollarandtrump.angelcar.utils.TabEntity;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.messaging.RemoteMessage;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseAppCompat {

    @Bind(R.id.toolbar_top) Toolbar toolbar;
    @Bind(R.id.viewpager) ViewPager viewPager;
    @Bind(R.id.floating_action_menu_fab) FloatingActionMenu menuFab;

    @Inject SharedPreferences sharedPreferences;
    @Inject @Named("default") SharedPreferences preferencesDefault;

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

//        setBadge(this,50);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ShopUpLoadDialog.REQUEST_CODE && resultCode == RESULT_OK){
           // requestCode
            MainThreadBus.getInstance()
                   .post(new ActivityResultEvent(requestCode,resultCode,data));
        }
        if (requestCode == ShopEditDialog.REQUEST_CODE && resultCode == RESULT_OK){
            MainThreadBus.getInstance()
                    .post(new ActivityResultEvent(requestCode,resultCode,data));
        }
        Log.d(""+requestCode);
    }


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
        setIconMessage(mMenu);
    }

    @Override
    protected void onStop() {
        super.onStop();
        MainThreadBus.getInstance().unregister(this);
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
        if (isConnectInternet()) {
            ProfileDao mProfile = SQLiteUtils.rawQuerySingle(ProfileDao.class, "SELECT * FROM Profile", null);
            if (mProfile == null || mProfile.getShopName() == null || mProfile.getUrlShopLogo().contains("default.png")) {
                alertDialogEditShop();
            } else {
                switch (v.getId()) {

                    case R.id.fab_dealers:
                        String name = preferencesDefault.getString("pre_name", null);
                        String tel = preferencesDefault.getString("pre_phone", null);
                        if (name == null || tel == null || name.trim().equals("") || name.trim().equals("")) {
                            alertDialogInformation();
                        } else {
                            startActivity(postCar(2, PostActivity.class));
                        }
                        break;
                    case R.id.fab_delegate:
                        startActivity(postCar(1, PostActivity.class));
                        break;
                    case R.id.fab_owner:
                        startActivity(postCar(0, PostActivity.class));
                        break;
                    default:
                        break;
                }
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
            menu.getItem(0).setIcon(R.drawable.ic_alert_message);
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
        viewPager.addOnPageChangeListener(onPageChangeListener);

        for (int i = 0; i < mTitles.length; i++) {
            mTabEntities.add(new TabEntity(mTitles[i], mIconSelectIds[i],mIconUnSelectIds[i]));
        }
        mTabLayout.setTabData(mTabEntities);
        mTabLayout.setOnTabSelectListener(onTabSelectListener);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (isConnectInternet()) {
            int id = item.getItemId();
            if (id == R.id.action_chat) {
                startActivity(initIntent(ConversationActivity.class));
                overridePendingTransition(R.anim.activity_slide_left_in, R.anim.activity_out);
                return true;
            } else if (id == R.id.action_follow) {
                startActivity(initIntent(FollowActivity.class));
                overridePendingTransition(R.anim.activity_slide_left_in, R.anim.activity_out);
                return true;
            } else if (id == R.id.action_search) {
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
                overridePendingTransition(R.anim.activity_slide_left_in, R.anim.activity_out);
                return true;
            }
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



    public static void setBadge(Context context, int count) {
        String launcherClassName = getLauncherClassName(context);
        if (launcherClassName == null) {
            return;
        }
        Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        intent.putExtra("badge_count", count);
        intent.putExtra("badge_count_package_name", context.getPackageName());
        intent.putExtra("badge_count_class_name", launcherClassName);
        context.sendBroadcast(intent);
    }

    public static String getLauncherClassName(Context context) {

        PackageManager pm = context.getPackageManager();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resolveInfos) {
            String pkgName = resolveInfo.activityInfo.applicationInfo.packageName;
            if (pkgName.equalsIgnoreCase(context.getPackageName())) {
                String className = resolveInfo.activityInfo.name;
                return className;
            }
        }
        return null;
    }

    @Override
    public Snackbar onCreateSnackBar() {
        return Snackbar.make(toolbar, R.string.status_network, Snackbar.LENGTH_INDEFINITE);
    }


}
