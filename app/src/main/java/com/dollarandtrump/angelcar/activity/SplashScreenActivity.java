package com.dollarandtrump.angelcar.activity;

import android.accounts.AccountManager;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.dollarandtrump.angelcar.MainApplication;
import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.dao.ProvinceCollectionDao;
import com.dollarandtrump.angelcar.dao.ProvinceDao;
import com.dollarandtrump.angelcar.dao.RegisterResultDao;
import com.dollarandtrump.angelcar.dao.ShopCollectionDao;
import com.dollarandtrump.angelcar.dao.ResponseDao;
import com.dollarandtrump.angelcar.manager.Registration;
import com.dollarandtrump.angelcar.manager.http.HttpManager;
import com.dollarandtrump.angelcar.model.ConversationCache;
import com.dollarandtrump.angelcar.model.LoadConversation;
import com.dollarandtrump.angelcar.module.Register;
import com.google.android.gms.common.AccountPicker;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class SplashScreenActivity extends AppCompatActivity{
    private  final int EMAIL_RESOLUTION_REQUEST = 333;
    Handler handler;
    Runnable runnable;
    long delay_time;
    long time = 2000L;

    boolean isFirstApp;


    @Bind(R.id.button_start) TextView mStart;

    @Inject Register register;
    @Inject
    LoadConversation loadConversation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);
        ButterKnife.bind(this);

        ((MainApplication) getApplication()).getStoreComponent().inject(this);






//        Log.d("Dagger2", ""+register.getUser() +" , "+register.getShop());
        mStart.setVisibility(View.GONE);

        isFirstApp = Registration.getInstance().isFirstApp();
        time = isFirstApp ? 500L : 2000L;

        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                if (isFirstApp){
                    Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    loadConversation.load();
                }else {
                    mStart.setVisibility(View.VISIBLE);
                }

            }
        };

    }

    @OnClick(R.id.button_start)
    public void onClickStart(){
        registration();
    }


    private void registration(){
        if (!Registration.getInstance().isFirstApp()){
            Intent googlePicker =
                    AccountPicker.newChooseAccountIntent(null, null,
                            new String[]{"com.google"}, true, null, null, null, null);
            startActivityForResult(googlePicker, EMAIL_RESOLUTION_REQUEST);
        }else {
            // กรณีลงทะเบียนแล้วให้ เช็ค cache // หากไม่พบ ให้ Registration Email
            String cache_User = Registration.getInstance().getUserId();
            if (cache_User != null){
                Toast.makeText(SplashScreenActivity.this,cache_User,Toast.LENGTH_LONG).show();
                Toast.makeText(SplashScreenActivity.this,Registration.getInstance().getShopRef(),Toast.LENGTH_LONG).show();
            }

        }
    }

    //  googlePicker
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EMAIL_RESOLUTION_REQUEST && resultCode == RESULT_OK) {
            String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            Toast.makeText(SplashScreenActivity.this,accountName,Toast.LENGTH_LONG).show();
            Call<RegisterResultDao> call = HttpManager.getInstance().getService().registrationEmail(accountName);
            call.enqueue(callbackRegistrationEmail);
        }
    }


    public void onResume() {
        super.onResume();
        delay_time = time;
        handler.postDelayed(runnable, delay_time);
        time = System.currentTimeMillis();
    }

    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
        time = delay_time - (System.currentTimeMillis() - time);
    }


    Callback<RegisterResultDao> callbackRegistrationEmail = new Callback<RegisterResultDao>() {
        @Override
        public void onResponse(Call<RegisterResultDao> call, Response<RegisterResultDao> response) {
            if (response.isSuccessful()) {
                // Save Cache
                Registration.getInstance().save(response.body());
                Toast.makeText(SplashScreenActivity.this, "ลงทะเบียนเรียบร้อยแล้ว", Toast.LENGTH_LONG).show();
                String user = Registration.getInstance().getUserId();
                String shop = Registration.getInstance().getShopRef();

                register.registerUser(response.body());

                Log.d("register",register.getUser());



                loadProvince();
                loadShop(user, shop);

                sendToken(user, shop);
            } else {
                Toast.makeText(SplashScreenActivity.this, "" + response.errorBody(), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Call<RegisterResultDao> call, Throwable t) {
            Snackbar.make(getWindow().getDecorView(),"เกิดข้อผิดพลาด",Snackbar.LENGTH_INDEFINITE).setAction("ลงทะเบียนใหม่", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getIntent());
                    startActivity(intent);
                    finish();
                }
            }).show();
        }
    };

    private void loadShop(String user, String shop) {
        HttpManager.getInstance().getService()
                .observableLoadShop(user, shop)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ShopCollectionDao>() {
                    @Override
                    public void call(ShopCollectionDao shopCollectionDao) {
                        shopCollectionDao.deleteAll();
                        shopCollectionDao.insertAll();
                    }
                });

    }

    private void loadProvince() {
        List<ProvinceDao> mListProvince = new Select().from(ProvinceDao.class).execute();
        if (mListProvince.size() <= 0){
            new Delete().from(ProvinceDao.class).execute();
            // load province
            HttpManager.getInstance().getService().observableProvince()
                    .subscribeOn(Schedulers.newThread())
                    .subscribe(new Action1<ProvinceCollectionDao>() {
                        @Override
                        public void call(ProvinceCollectionDao provinceCollectionDao) {
                            ActiveAndroid.beginTransaction();
                            try {
                                for (ProvinceDao province : provinceCollectionDao.getDao()){
                                    province.save();
                                }
                                ActiveAndroid.setTransactionSuccessful();
                            } finally {
                                ActiveAndroid.endTransaction();
                            }
                        }
                    });
        }

    }

    public void sendToken(String user, String shop) {
        HttpManager.getInstance().getService().sendTokenRegistration(user,shop, FirebaseInstanceId.getInstance().getToken())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ResponseDao>() {
                    @Override
                    public void call(ResponseDao responseDao) {
                        Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
    }


    //Update

//    PackageInfo pInfo = null;
//    try {
//        pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
//    } catch (PackageManager.NameNotFoundException e) {
//        e.printStackTrace();
//    }
//    String version = pInfo.versionName;
//    int verCode = pInfo.versionCode;
//
//    Log.d("SP", "onCreate: "+version +" , "+verCode);


//    final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
//    try {
//        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
//    } catch (android.content.ActivityNotFoundException anfe) {
//        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
//    }
}
