package com.dollarandtrump.angelcar.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dollarandtrump.angelcar.Adapter.ShopAdapter;
import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.dao.PostCarCollectionDao;
import com.dollarandtrump.angelcar.dao.PostCarDao;
import com.dollarandtrump.angelcar.dao.ProfileDao;
import com.dollarandtrump.angelcar.dao.ShopCollectionDao;
import com.dollarandtrump.angelcar.dialog.DetailAlertDialog;
import com.dollarandtrump.angelcar.manager.Cache;
import com.dollarandtrump.angelcar.manager.Registration;
import com.dollarandtrump.angelcar.manager.http.HttpManager;

import org.parceler.Parcels;

import butterknife.Bind;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class ShopActivity extends AppCompatActivity {
    private static final String TAG = "ShopActivity";

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.recycler_car) RecyclerView recyclerCar;
    @Bind(R.id.pictureShopProfile) ImageView shopProfilePicture;
    @Bind(R.id.tvShopName) TextView shopName;
    @Bind(R.id.tvShopNumber) TextView shopNumber;
    @Bind(R.id.tvShopDescription) TextView shopDescription;

    private GridLayoutManager manager;
    private ShopAdapter adapter;
    private PostCarCollectionDao dao;
    private Subscription subscription;

//    private SharedPreferences preferences;
    Cache daoCacheManager = new Cache();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        ButterKnife.bind(this);
        initToolbar();
        initInstance();
        loadData();

//        preferences = getSharedPreferences("shopDao",MODE_PRIVATE);
    }

    private void loadData() {
        if(!daoCacheManager.isFile("cacheShop")) {
            String userRef = Registration.getInstance().getUserId();
            String shopRef = Registration.getInstance().getShopRef();
            Log.i(TAG, "loadData: " + userRef + "," + shopRef);

//        Call<ShopCollectionDao> call = HttpManager.getInstance()
//                .getService().loadDataShop(userRef, shopRef);
//        call.enqueue(shopCollectionDaoCallback);

//        //Sample Rx android
            Observable<ShopCollectionDao> rxCall = HttpManager.getInstance().getService()
                    .observableLoadShop(userRef, shopRef)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread());
            subscription = rxCall.subscribe(shopCollectionDaoAction1);
        }else {
            ShopCollectionDao shopCollectionDao =
                    daoCacheManager.load("cacheShop",ShopCollectionDao.class);
            initData(shopCollectionDao);

        }
    }

    private void initData(ShopCollectionDao shopCollectionDao){
        initProfile(shopCollectionDao.getProfileDao());
        dao = new PostCarCollectionDao();
        dao.setListCar(shopCollectionDao.getPostCarDao());
        adapter.setDao(dao);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (subscription != null)
            subscription.unsubscribe();
    }

    private void initProfile(ProfileDao profileDao){
        //TODO Image Profile
//        Picasso.with(ShopActivity.this)
////                .load(""+profileDao.getShopLogo())
//                .load("http://cls.paiyannoi.me/profileimages/default.png")
//                .placeholder(R.drawable.loading)
//                .into(shopProfilePicture);
        Glide.with(ShopActivity.this).load("http://cls.paiyannoi.me/profileimages/default.png")
                .placeholder(com.hndev.library.R.drawable.loading)
                .bitmapTransform(new CropCircleTransformation(ShopActivity.this))
                .into(shopProfilePicture);

        shopName.setText(profileDao.getShopName());
        shopDescription.setText(profileDao.getShopDescription());
        shopNumber.setText(profileDao.getShopNumber());
    }


    private void initInstance() {
       manager = new GridLayoutManager(this,3);
        recyclerCar.setLayoutManager(manager);
        adapter = new ShopAdapter();
        adapter.setOnclickListener(recyclerOnItemClickListener);

        recyclerCar.setAdapter(adapter);
        manager.setSpanSizeLookup(spanSizeLookupManager);
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

//    @OnClick(R.id.buttonBack)
//    public void onClickBackHome() {
//        finish();
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void intentEditPost(PostCarDao modelCar) {
        Intent intent = new Intent(ShopActivity.this,EditPostActivity.class);
        intent.putExtra("postCarDao", Parcels.wrap(modelCar));
        startActivity(intent);
    }

    /***************
     *Listener Zone*
     ***************/
    GridLayoutManager.SpanSizeLookup spanSizeLookupManager = new GridLayoutManager.SpanSizeLookup() {
        @Override
        public int getSpanSize(int position) {
            //กำหนด จำนวน item ใน grid
            return adapter.isHeader(position) ? manager.getSpanCount() : 1;
        }
    };

//    Callback<ShopCollectionDao> shopCollectionDaoCallback = new Callback<ShopCollectionDao>() {
//        @Override
//        public void onResponse(Call<ShopCollectionDao> call, Response<ShopCollectionDao> response) {
//            if (response.isSuccessful()) {
//                initProfile(response.body().getProfileDao());
//                dao = new PostCarCollectionDao();
//                dao.setListCar(response.body().getPostCarDao());
//                adapter.setDao(dao);
//
//                Log.i(TAG, "onResponse: Okkkkkk");
//
//            } else {
//                try {
//                    Log.e(TAG, "onResponse:" + response.errorBody().string());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        @Override
//        public void onFailure(Call<ShopCollectionDao> call, Throwable t) {
//            Log.e(TAG, "onFailure: ", t);
//        }
//    };

    Action1<ShopCollectionDao> shopCollectionDaoAction1 = new Action1<ShopCollectionDao>() {
        @Override
        public void call(ShopCollectionDao shopCollectionDao) {
            Log.i(TAG, "Call Success: Rx android");
            initData(shopCollectionDao);
            // save cache Dao
            daoCacheManager.save("cacheShop",shopCollectionDao);
//          preferences.edit().putBoolean("isUpdateShop",true).apply();

        }
    };

    ShopAdapter.RecyclerOnItemClickListener recyclerOnItemClickListener = new ShopAdapter.RecyclerOnItemClickListener() {
        @Override
        public void OnClickItemListener(View v, int position) {
            PostCarDao modelCar = dao.getListCar().get(position);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Fragment fragment = getSupportFragmentManager().findFragmentByTag("DetailAlertDialog");
            if (fragment != null){
                ft.remove(fragment);
            }
            ft.addToBackStack(null);
            DetailAlertDialog dialog = DetailAlertDialog.newInstance(modelCar,"shop");
            dialog.setOnClickEditListener(onClickEditListener);
//            dialog.setTargetFragment(this,REQUEST_CODE_BRAND);
            dialog.show(getSupportFragmentManager(),"DetailAlertDialog");
        }

        @Override
        public void OnClickSettingListener(View v, int position) {
            PostCarDao modelCar = dao.getListCar().get(position);
            intentEditPost(modelCar);
        }
    };

    DetailAlertDialog.OnClickEdit onClickEditListener = new DetailAlertDialog.OnClickEdit() {
        @Override
        public void onClickEdit(PostCarDao modelCar) {
            intentEditPost(modelCar);
        }
    };
    /*****************
    *Inner Class Zone*
    ******************/

}
