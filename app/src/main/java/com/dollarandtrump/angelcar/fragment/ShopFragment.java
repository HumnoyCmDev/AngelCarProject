package com.dollarandtrump.angelcar.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dollarandtrump.angelcar.Adapter.ShopAdapter;
import com.dollarandtrump.angelcar.Adapter.ShopHashTagAdapter;
import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.activity.EditPostActivity;
import com.dollarandtrump.angelcar.dao.PostCarCollectionDao;
import com.dollarandtrump.angelcar.dao.PostCarDao;
import com.dollarandtrump.angelcar.dao.ProfileDao;
import com.dollarandtrump.angelcar.dao.ShopCollectionDao;
import com.dollarandtrump.angelcar.dialog.DetailAlertDialog;
import com.dollarandtrump.angelcar.manager.Cache;
import com.dollarandtrump.angelcar.manager.Contextor;
import com.dollarandtrump.angelcar.manager.Registration;
import com.dollarandtrump.angelcar.manager.http.HttpManager;
import com.dollarandtrump.angelcar.view.ListHashTag;
import com.dollarandtrump.angelcar.view.snappy.SnappyLinearLayoutManager;
import com.dollarandtrump.angelcar.view.snappy.SnappyRecyclerView;
import com.github.clans.fab.FloatingActionMenu;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


/***************************************
 * สร้างสรรค์ผลงานดีๆ
 * โดย Humnoy Android Developer
 * ลงวันที่ 11/16/2014. เวลา 11:42
 ***************************************/
@SuppressWarnings("unused")
public class ShopFragment extends Fragment {
    private static final String TAG = "ShopFragment";

    @Bind(R.id.recycler_car) RecyclerView recyclerCar;
    @Bind(R.id.pictureShopProfile) ImageView shopProfilePicture;
    @Bind(R.id.tvShopName) TextView shopName;
    @Bind(R.id.tvShopNumber) TextView shopNumber;
    @Bind(R.id.tvShopDescription) TextView shopDescription;
    @Bind(R.id.appBarLayout) AppBarLayout appBarLayout;
    @Bind(R.id.recyclerHeaderShop) RecyclerView recyclerHeaderShop;

    @Bind(R.id.menu_fab) FloatingActionMenu menuFab;

    @Bind(R.id.listHashTag) ListHashTag listHashTag;

    @Bind(R.id.snappy) SnappyRecyclerView snappy;

    private GridLayoutManager manager;
    private ShopAdapter adapter;
    private PostCarCollectionDao dao;
    private Subscription subscription;

    private ShopHashTagAdapter shopHashTag;

    Cache daoCacheManager = new Cache();
    int last = -100;
    int lastRecycler = 0;
    boolean controlRecycler = true;
    boolean control = true;


    public ShopFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static ShopFragment newInstance() {
        ShopFragment fragment = new ShopFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(savedInstanceState);

        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_shop, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        dao = new PostCarCollectionDao();
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        ButterKnife.bind(this,rootView);

        menuFab.setClosedOnTouchOutside(true);

        //Header Shop
        recyclerHeaderShop.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        HeaderShopAdapter headerShopAdapter = new HeaderShopAdapter();
        recyclerHeaderShop.setAdapter(headerShopAdapter);
        headerShopAdapter.setOnItemClickListener(new HeaderShopAdapter.HeaderShop() {
            @Override
            public void OnClickItemListener(View view, int position) {
                Log.i(TAG, "OnClickItemListener: "+position);
            }
        });


        snappy.setLayoutManager(new SnappyLinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        snappy.setAdapter(headerShopAdapter);


        manager = new GridLayoutManager(getActivity(),3);
        recyclerCar.setLayoutManager(manager);
        adapter = new ShopAdapter();
        adapter.setDao(dao);
        recyclerCar.setAdapter(adapter);
        adapter.setOnclickListener(recyclerOnItemClickListener);
        manager.setSpanSizeLookup(spanSizeLookupManager);


        shopHashTag = new ShopHashTagAdapter();
        shopHashTag.setDao(dao);
        listHashTag.setAdapter(shopHashTag);
        shopHashTag.setOnItemClickHashTagListener(new ShopHashTagAdapter.OnItemClickHashTagListener() {
            @Override
            public void onItemClick(boolean isSelected, int position, String brand) {
                adapter.getFilter().filter(brand);
            }
        });




        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset < last && !control){
                    //scrollUp
                    if (verticalOffset < -100) {
//                    Log.i(TAG, "onScrolled: scrollUp");
                        hidePicProfile();
                        control = true;
                    }
                }else if(verticalOffset > last && control){
                    //scrollDown
                    if (verticalOffset >= -200) {
//                    Log.i(TAG, "onScrolled: scrollDown");
                        showPicProfile();
                        control = false;
                    }

                }
                last = verticalOffset;
            }
        });

        recyclerCar.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy < lastRecycler && !controlRecycler){
                    //scrollUp
                    showFabButton();
                    controlRecycler = true;
                    lastRecycler = 0;
                }else if(dy > lastRecycler && controlRecycler){
                    //scrollDown
                    hideFabButton();
                    controlRecycler = false;
                    lastRecycler = 0;
                }

                if ((controlRecycler && dy > 0) || (!controlRecycler && dy < 0))
                    lastRecycler += dy;
            }
        });

        if (savedInstanceState == null){
            loadData();
        }


    }

    private void loadData() {
//        if(!daoCacheManager.isFile("shop")) {
            String userRef = Registration.getInstance().getUserId();
            String shopRef = Registration.getInstance().getShopRef();
            Log.i(TAG, "loadData: " + userRef + "," + shopRef);

//        Call<ShopCollectionDao> call = HttpManager.getInstance()
//                .getService().loadDataShop(userRef, shopRef);
//        call.enqueue(shopCollectionDaoCallback);

//        //Sample Rx android
            Observable<ShopCollectionDao> rxCall = HttpManager.getInstance().getService()
                    .observableLoadShop(userRef, shopRef)
//                    .observableLoadShop("2016050200001", "68")
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread());
            subscription = rxCall.subscribe(shopCollectionDaoAction1);
//        }else {
//            ShopCollectionDao shopCollectionDao = daoCacheManager.load("shop", ShopCollectionDao.class);
//            initData(shopCollectionDao);
//            Log.i(TAG, "loadCache: Cache");
//        }
    }

    private void initData(ShopCollectionDao shopCollectionDao){
        initProfile(shopCollectionDao.getProfileDao());
        dao.setListCar(shopCollectionDao.getPostCarDao());
        adapter.setDao(dao);
        adapter.notifyDataSetChanged();

        //Todo HashTag
        shopHashTag.setDao(dao);
        shopHashTag.notifyDataSetChanged();
    }

    private void initProfile(ProfileDao profileDao){
        //TODO Image Profile
        Glide.with(this).load("http://cls.paiyannoi.me/profileimages/default.png")
                .placeholder(com.hndev.library.R.drawable.loading)
                .bitmapTransform(new CropCircleTransformation(getActivity()))
                .into(shopProfilePicture);

        shopName.setText(profileDao.getShopName());
        shopDescription.setText(profileDao.getShopDescription());
        shopNumber.setText(profileDao.getShopNumber());
    }

    public void intentEditPost(PostCarDao modelCar) {
        Intent intent = new Intent(getActivity(),EditPostActivity.class);
        intent.putExtra("postCarDao", Parcels.wrap(modelCar));
        startActivity(intent);
    }

    private void showPicProfile(){
        if (shopProfilePicture.getVisibility() == View.GONE) {
            shopProfilePicture.setVisibility(View.VISIBLE);
            Animation animation = AnimationUtils.loadAnimation(
                    Contextor.getInstance().getContext(),
                    R.anim.shop_zoom_face_in);
            shopProfilePicture.startAnimation(animation);
        }

        if (recyclerHeaderShop.getVisibility() == View.GONE){
            Animation animationHeader = AnimationUtils.loadAnimation(
                    Contextor.getInstance().getContext(),
                    R.anim.activity_slide_left_in);
            recyclerHeaderShop.setVisibility(View.VISIBLE);
            recyclerHeaderShop.startAnimation(animationHeader);
        }
    }

    private void hidePicProfile(){
        if (shopProfilePicture.getVisibility() == View.VISIBLE) {
            Animation animation = AnimationUtils.loadAnimation(
                    Contextor.getInstance().getContext(),
                    R.anim.shop_zoom_face_out);
            shopProfilePicture.setVisibility(View.GONE);
            shopProfilePicture.startAnimation(animation);

        }

        if (recyclerHeaderShop.getVisibility() == View.VISIBLE){
            Animation animationHeader = AnimationUtils.loadAnimation(
                    Contextor.getInstance().getContext(),
                    R.anim.activity_slide_right_out);
            recyclerHeaderShop.setVisibility(View.GONE);
            recyclerHeaderShop.startAnimation(animationHeader);
        }
    }

    @OnClick(R.id.pictureShopProfile)
    public void clickProfile(){
        shopHashTag.notifyDataSetChanged();
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("dao",Parcels.wrap(dao));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        if (subscription != null)
            subscription.unsubscribe();
    }

    @SuppressWarnings("UnusedParameters")
    private void onRestoreInstanceState(Bundle savedInstanceState) {
        dao = Parcels.unwrap(savedInstanceState.getParcelable("dao"));
    }

    private void showFabButton(){
        menuFab.showMenuButton(true);
    }

    private void hideFabButton(){
        menuFab.hideMenuButton(false);
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
            initData(shopCollectionDao);
            // save cache Dao
            boolean b = daoCacheManager.save("shop", shopCollectionDao);
            Log.i(TAG, "Call Success: Rx android  "+b);

        }
    };

    ShopAdapter.RecyclerOnItemClickListener recyclerOnItemClickListener = new ShopAdapter.RecyclerOnItemClickListener() {
        @Override
        public void OnClickItemListener(View v, int position) {
            PostCarDao modelCar = dao.getListCar().get(position);
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            Fragment fragment = getChildFragmentManager().findFragmentByTag("DetailAlertDialog");
            if (fragment != null){
                ft.remove(fragment);
            }
            ft.addToBackStack(null);
            DetailAlertDialog dialog = DetailAlertDialog.newInstance(modelCar,"shop");
            dialog.setOnClickEditListener(onClickEditListener);
//            dialog.setTargetFragment(this,REQUEST_CODE_BRAND);
            dialog.show(getChildFragmentManager(),"DetailAlertDialog");
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

    public static class HeaderShopAdapter extends RecyclerView.Adapter<HeaderShopAdapter.ViewHolder>{

        private List<String> itemPic = new ArrayList<>();

        public interface HeaderShop{
            void OnClickItemListener(View view,int position);
        }

        private HeaderShop itemClickListener;

        public void setOnItemClickListener(HeaderShop itemClickListener){
            this.itemClickListener = itemClickListener;
        }

        @Override
        public int getItemCount() {
            return 15;//itemPic.size();
        }

        @Override
        public HeaderShopAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_picture_header_shop,parent,false);
            ViewHolder vh = new ViewHolder(view);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            @Bind(R.id.header_shop_image) ImageView headerShopImage;
            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);
                headerShopImage.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                if (itemClickListener != null){
                    itemClickListener.OnClickItemListener(v,getAdapterPosition());
                }
            }
        }
    }

}
