package com.dollarandtrump.angelcar.fragment;

import android.content.Context;
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

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.bumptech.glide.Glide;
import com.dollarandtrump.angelcar.Adapter.ShopAdapter;
import com.dollarandtrump.angelcar.Adapter.ShopHashTagAdapter;
import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.activity.EditPostActivity;
import com.dollarandtrump.angelcar.activity.SingleViewImageActivity;
import com.dollarandtrump.angelcar.dao.PostCarCollectionDao;
import com.dollarandtrump.angelcar.dao.PostCarDao;
import com.dollarandtrump.angelcar.dao.ProfileDao;
import com.dollarandtrump.angelcar.dao.ShopCollectionDao;
import com.dollarandtrump.angelcar.dialog.DetailAlertDialog;
import com.dollarandtrump.angelcar.dialog.ShopEditDialog;
import com.dollarandtrump.angelcar.dialog.ShopUpLoadDialog;
import com.dollarandtrump.angelcar.manager.Cache;
import com.dollarandtrump.angelcar.manager.Contextor;
import com.dollarandtrump.angelcar.manager.Registration;
import com.dollarandtrump.angelcar.manager.http.HttpManager;
import com.dollarandtrump.angelcar.view.ListHashTag;
import com.dollarandtrump.angelcar.view.snappy.SnappyLinearLayoutManager;
import com.dollarandtrump.angelcar.view.snappy.SnappyRecyclerView;
import com.github.clans.fab.FloatingActionMenu;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.hndev.library.view.AngelCarHashTag;

import org.parceler.Parcels;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/***************************************
 * สร้างสรรค์ผลงานดีๆ
 * โดย Humnoy Android Developer
 * ลงวันที่ 11/16/2014. เวลา 11:42
 ***************************************/
//TODO Error Api 23 (Database ActiveAndroid)
@SuppressWarnings("unused")
public class ShopFragment extends Fragment {
    private static final String TAG = "ShopFragment";

    @Bind(R.id.recycler_car) RecyclerView recyclerCar;
    @Bind(R.id.pictureShopProfile) CircularImageView shopProfilePicture;
    @Bind(R.id.tvShopName) TextView shopName;
    @Bind(R.id.tvShopNumber) TextView shopNumber;
    @Bind(R.id.tvShopDescription) TextView shopDescription;
    @Bind(R.id.appBarLayout) AppBarLayout appBarLayout;
    @Bind(R.id.recyclerImageHeaderShop) RecyclerView recyclerImageHeaderShop;
    @Bind(R.id.tvViewShop) TextView viewShop;
    @Bind(R.id.imageShopBackground) ImageView imgShopBg;
    @Bind(R.id.menu_fab) FloatingActionMenu menuFab;

    @Bind(R.id.listHashTag) ListHashTag listHashTag;

//    @Bind(R.id.snappy) SnappyRecyclerView snappy;

    private GridLayoutManager manager;
    private ShopAdapter adapter;
    private PostCarCollectionDao dao;
    private Subscription subscription;

    private ShopHashTagAdapter shopHashTag;
    private String urlShopLogo;
    private ImageHeaderAdapter imageHeaderAdapter;
//    Cache daoCacheManager = new Cache();
    int last = -100;
    int lastRecycler = 0;
    boolean controlRecycler = true;
    boolean control = true;
    private List<String> listImageBg;

    /*GreenDao*/
   /* MainApplication application;
    DaoSession mDaoSession;
    PostCarDBDao mPostCarDBDao;*/


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
      /*  application = (MainApplication) getActivity().getApplication();
        mDaoSession = application.getDaoSession();*/
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        ButterKnife.bind(this,rootView);

        menuFab.setClosedOnTouchOutside(true);

        //Header Shop
        recyclerImageHeaderShop.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        imageHeaderAdapter = new ImageHeaderAdapter();
        recyclerImageHeaderShop.setAdapter(imageHeaderAdapter);
        imageHeaderAdapter.setOnItemClickListener(new ImageHeaderAdapter.HeaderShop() {
            @Override
            public void OnClickItemListener(View view, int position) {
                if (listImageBg != null)
                    initProfileShopBg(listImageBg,position);
            }
        });


//        snappy.setLayoutManager(new SnappyLinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
//        snappy.setAdapter(imageHeaderAdapter);


        manager = new GridLayoutManager(getActivity(),3);
        recyclerCar.setLayoutManager(manager);
        adapter = new ShopAdapter();
        adapter.setDao(dao);
        recyclerCar.setAdapter(adapter);
        adapter.setOnclickListener(recyclerOnItemClickListener);
        manager.setSpanSizeLookup(spanSizeLookupManager);


        shopHashTag = new ShopHashTagAdapter();
//        shopHashTag.setDao(dao);
        listHashTag.setAdapter(shopHashTag);
        shopHashTag.setOnItemClickHashTagListener(new ShopHashTagAdapter.OnItemClickHashTagListener() {
            @Override
            public void onItemClick(boolean isSelected, int position, String brand) {
                if (isSelected && position != 0) {
                     adapter.setDao(findPostCar(brand));
                     adapter.notifyDataSetChanged();
                }else{
                    adapter.setDao(queryPostCar());
                    adapter.notifyDataSetChanged();
                }
                for (int i = 0; i < listHashTag.getChildCount(); i++) {
                    if (i != position) {
                        if (listHashTag.getChildAt(i) instanceof AngelCarHashTag) {
                            AngelCarHashTag v = (AngelCarHashTag) listHashTag.getChildAt(i);
                            v.hideChildSubCar();
                            Log.d(TAG, "onItemClick: "+i);
                        }
                    }
                }
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
        subscription = rxCall.subscribe(shopCollectionDaoObserver);
//        }else {
//            ShopCollectionDao shopCollectionDao = daoCacheManager.load("shop", ShopCollectionDao.class);
//            initData(shopCollectionDao);
//            Log.i(TAG, "loadCache: Cache");
//        }
    }

    private void initData(ShopCollectionDao shopCollectionDao){
        initProfile(shopCollectionDao.getProfileDao());
        dao.setListCar(shopCollectionDao.getPostCarDao());
        save2db(dao);

        /*Query PostCar*/
        //Todo HashTag
        shopHashTag.setDao(queryFindBrandDuplicates());
        shopHashTag.setChildBrand(queryPostCar().getListCar());
        shopHashTag.notifyDataSetChanged();

        adapter.setDao(dao);
        adapter.notifyDataSetChanged();
    }

    private void save2db(PostCarCollectionDao dao){
        new Delete().from(PostCarDao.class).execute();
        //TODO SAVE To DB
        ActiveAndroid.beginTransaction();
        try {
            for (PostCarDao d : dao.getListCar()){
                    d.save();
            }
            ActiveAndroid.setTransactionSuccessful();
        }
        finally {
            ActiveAndroid.endTransaction();
        }
    }

    private PostCarCollectionDao queryPostCar(){//all
        List<PostCarDao> model = new Select().from(PostCarDao.class)
                .orderBy("BrandName ASC").execute();
        PostCarCollectionDao newDao = new PostCarCollectionDao();
        newDao.setListCar(model);
        return newDao;
    }

    private PostCarCollectionDao queryFindBrandDuplicates(){
        List<PostCarDao> model = new Select().from(PostCarDao.class)
                .groupBy("BrandName").having("COUNT(BrandName) > 0")
                .orderBy("BrandName ASC").execute();
        PostCarCollectionDao newDao = new PostCarCollectionDao();
        newDao.setListCar(model);
        return newDao;
    }

    private PostCarCollectionDao findPostCar(String brandName){
        List<PostCarDao> model = new Select().from(PostCarDao.class)
                .where("BrandName LIKE ?",brandName).execute();
        PostCarCollectionDao newDao = new PostCarCollectionDao();
        newDao.setListCar(model);
        return newDao;
    }

    private void initProfile(ProfileDao profileDao){
        String strViewShop = profileDao.getShopView()+" View | "+profileDao.getShopFollow()+" Follow";
        viewShop.setText(strViewShop);
        //TODO Image Profile
        urlShopLogo = profileDao.getUrlShopLogo();
        Glide.with(this).load(urlShopLogo)
                .placeholder(com.hndev.library.R.drawable.loading)
                .bitmapTransform(new CropCircleTransformation(getActivity()))
                .into(shopProfilePicture);

        shopName.setText(profileDao.getShopName());
        shopDescription.setText(profileDao.getShopDescription());
        shopNumber.setText(profileDao.getShopNumber());

        listImageBg = profileDao.getProfilePath();
        initProfileShopBg(listImageBg,0);
        imageHeaderAdapter.setUrlPath(profileDao.getProfilePath());
        imageHeaderAdapter.notifyDataSetChanged();
    }

    private void initProfileShopBg(List<String> listImageBg,int id){
        Glide.with(this).load("http://angelcar.com/ios/data/clsdata/"+listImageBg.get(id))
//                .placeholder(com.hndev.library.R.drawable.loading)
                .into(imgShopBg);
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

        if (recyclerImageHeaderShop.getVisibility() == View.GONE){
            Animation animationHeader = AnimationUtils.loadAnimation(
                    Contextor.getInstance().getContext(),
                    R.anim.activity_slide_left_in);
            recyclerImageHeaderShop.setVisibility(View.VISIBLE);
            recyclerImageHeaderShop.startAnimation(animationHeader);
        }
    }

    private void hidePicProfile(){
        if (shopProfilePicture.getVisibility() == View.VISIBLE) {
//            Animation animation = AnimationUtils.loadAnimation(
//                    Contextor.getInstance().getContext(),
//                    R.anim.shop_zoom_face_out);
            shopProfilePicture.setVisibility(View.GONE);
//            shopProfilePicture.startAnimation(animation);

        }

        if (recyclerImageHeaderShop.getVisibility() == View.VISIBLE){
            Animation animationHeader = AnimationUtils.loadAnimation(
                    Contextor.getInstance().getContext(),
                    R.anim.activity_slide_right_out);
            recyclerImageHeaderShop.setVisibility(View.GONE);
            recyclerImageHeaderShop.startAnimation(animationHeader);
        }
    }

    @OnClick({R.id.fab_editShop})
    public void clickProfile(){
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        Fragment fragment = getChildFragmentManager().findFragmentByTag("ShopEditDialog");
        if (fragment != null){
            ft.remove(fragment);
        }
//        ft.addToBackStack(null);
        ShopEditDialog dialog = new ShopEditDialog();
        Bundle args = new Bundle();
        args.putString("shopName", shopName.getText().toString());
        args.putString("shopDescription",shopDescription.getText().toString());
        args.putString("shopNumber",shopNumber.getText().toString());
        args.putString("logoShop",urlShopLogo);
        dialog.setArguments(args);
        dialog.show(getChildFragmentManager(),"ShopEditDialog");
        dialog.setEditShopCallback(new ShopEditDialog.EditShopCallback() {
            @Override
            public void onSuccess() {
                loadData();
            }

            @Override
            public void onFail() {

            }
        });

        if (menuFab.isOpened()){
            menuFab.close(true);
        }
    }

    @OnClick(R.id.fab_upLoadCover)
    public void upLoadCover(){
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        Fragment fragment = getFragmentManager().findFragmentByTag("ShopUpLoadDialog");
        if (fragment != null){
            ft.remove(fragment);
        }
        ShopUpLoadDialog upLoadDialog = new ShopUpLoadDialog();
        upLoadDialog.show(getChildFragmentManager(),"ShopUpLoadDialog");
    }

    @OnClick(R.id.pictureShopProfile)
    public void viewPictureShop(){
        Intent intent = new Intent(getActivity(),
                SingleViewImageActivity.class);
        intent.putExtra(SingleViewImageActivity.ARGS_PICTURE, urlShopLogo);
        getActivity().startActivity(intent);
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

    Observer<ShopCollectionDao> shopCollectionDaoObserver = new Observer<ShopCollectionDao>() {
        @Override
        public void onCompleted() {
            Log.i(TAG, "onCompleted: ");
        }

        @Override
        public void onError(Throwable e) {
            Log.e(TAG, "onError: ", e);
        }

        @Override
        public void onNext(ShopCollectionDao shopCollectionDao) {
            initData(shopCollectionDao);
            // save cache Dao
//            boolean b = daoCacheManager.save("shop", shopCollectionDao);
//            Log.i(TAG, "Call Success: Rx android  "+b);
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

    public static class ImageHeaderAdapter extends RecyclerView.Adapter<ImageHeaderAdapter.ViewHolder>{

        private List<String> urlPath;
        private Context mContext;

        public interface HeaderShop{
            void OnClickItemListener(View view,int position);
        }

        private HeaderShop itemClickListener;


        public void setUrlPath(List<String> urlPath) {
            this.urlPath = urlPath;
        }

        public void setOnItemClickListener(HeaderShop itemClickListener){
            this.itemClickListener = itemClickListener;
        }

        @Override
        public int getItemCount() {
            if (urlPath == null) return 0;
            return urlPath.size();
        }

        @Override
        public ImageHeaderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_picture_header_shop,parent,false);
            ViewHolder vh = new ViewHolder(view);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Glide.with(mContext)
                    .load("http://angelcar.com/ios/data/clsdata/"+urlPath.get(position))
                    .bitmapTransform(new RoundedCornersTransformation(mContext,15,5))
                    .into(holder.headerShopImage);
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
