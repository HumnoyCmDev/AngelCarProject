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

import com.bumptech.glide.Glide;
import com.dollarandtrump.angelcar.Adapter.ShopAdapter;
import com.dollarandtrump.angelcar.Adapter.ShopHashTagAdapter;
import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.activity.EditPostActivity;
import com.dollarandtrump.angelcar.activity.SingleViewImageActivity;
import com.dollarandtrump.angelcar.dao.PostCarDao;
import com.dollarandtrump.angelcar.dao.ProfileDao;
import com.dollarandtrump.angelcar.dao.ShopCollectionDao;
import com.dollarandtrump.angelcar.dialog.DetailAlertDialog;
import com.dollarandtrump.angelcar.dialog.ShopEditDialog;
import com.dollarandtrump.angelcar.dialog.ShopUpLoadDialog;
import com.dollarandtrump.angelcar.manager.Contextor;
import com.dollarandtrump.angelcar.manager.Registration;
import com.dollarandtrump.angelcar.manager.http.HttpManager;
import com.dollarandtrump.angelcar.view.ListHashTag;
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

    @Bind(R.id.recycler_car) RecyclerView listCar;
    @Bind(R.id.recyclerImageHeaderShop) RecyclerView listImageHeader;
    @Bind(R.id.pictureShopProfile) CircularImageView shopProfilePicture;
    @Bind(R.id.tvShopName) TextView shopName;
    @Bind(R.id.tvShopNumber) TextView shopNumber;
    @Bind(R.id.tvShopDescription) TextView shopDescription;
    @Bind(R.id.appBarLayout) AppBarLayout appBarLayout;
    @Bind(R.id.tvViewShop) TextView viewShop;
    @Bind(R.id.imageShopBackground) ImageView imgShopBg;
    @Bind(R.id.menu_fab) FloatingActionMenu menuFab;
//    @Bind(R.id.swipeRefreshShop) SwipeRefreshLayout swipeRefresh;
    @Bind(R.id.listHashTag) ListHashTag listHashTag;

//    @Bind(R.id.snappy) SnappyRecyclerView snappy;

    private GridLayoutManager mManager;
    private ShopAdapter mAdapter;
//    private PostCarCollectionDao dao;
    private ShopCollectionDao shopCollectionDao;
    private Subscription subscription;

    private ShopHashTagAdapter shopHashTag;
    private ImageHeaderAdapter imageHeaderAdapter;
    private int idImageBackground = 0;
    private int last = -100;
    private int lastRecycler = 0;
    private boolean controlRecycler = true;
    private boolean control = true;
    private ProfileDao profileDao;

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
//        dao = new PostCarCollectionDao();
        shopCollectionDao = new ShopCollectionDao();


    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        ButterKnife.bind(this,rootView);
/*
        swipeRefresh.setColorSchemeColors(
                Color.parseColor("#104F94"),
                Color.parseColor("#104F94"),
                Color.parseColor("#FFC11E"),
                Color.parseColor("#FFC11E"));
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });*/

        menuFab.setClosedOnTouchOutside(true);

        //Header Shop
        listImageHeader.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        imageHeaderAdapter = new ImageHeaderAdapter();
        listImageHeader.setAdapter(imageHeaderAdapter);
        imageHeaderAdapter.setOnItemClickListener(new ImageHeaderAdapter.HeaderShop() {
            @Override
            public void OnClickItemListener(View view, int position) {
                if (profileDao != null)
                    idImageBackground = position;
                    initProfileShopBg(profileDao,position);
            }
        });


//        snappy.setLayoutManager(new SnappyLinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
//        snappy.setAdapter(imageHeaderAdapter);


        mManager = new GridLayoutManager(getActivity(),3);
        listCar.setLayoutManager(mManager);
        mAdapter = new ShopAdapter();
        mAdapter.setDao(shopCollectionDao.getPostCarCellection());
        listCar.setAdapter(mAdapter);
        mAdapter.setOnclickListener(recyclerOnItemClickListener);
        mManager.setSpanSizeLookup(spanSizeLookupManager);


        shopHashTag = new ShopHashTagAdapter();
//        shopHashTag.setDao(dao);
        listHashTag.setAdapter(shopHashTag);
        shopHashTag.setOnItemClickHashTagListener(new ShopHashTagAdapter.OnItemClickHashTagListener() {
            @Override
            public void onItemClick(boolean isSelected, int position, String brand) {
                if (isSelected && position != 0) {
                     mAdapter.setDao(shopCollectionDao.findPostCar(brand));
                     mAdapter.notifyDataSetChanged();
                }else{
                    mAdapter.setDao(shopCollectionDao.queryPostCar());
                    mAdapter.notifyDataSetChanged();
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

        listCar.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
            String userRef = Registration.getInstance().getUserId();
            String shopRef = Registration.getInstance().getShopRef();
            Log.i(TAG, "loadData: " + userRef + "," + shopRef);

//        //Sample Rx android
            Observable<ShopCollectionDao> rxCall = HttpManager.getInstance().getService()
                    .observableLoadShop(userRef, shopRef)
//                    .observableLoadShop("2016050200001", "68")
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread());
        subscription = rxCall.subscribe(shopCollectionDaoObserver);
    }

    private void initData(ShopCollectionDao shopCollectionDao){
        initProfile(shopCollectionDao.getProfileDao());
//        dao.setListCar(shopCollectionDao.getPostCarDao());
        this.shopCollectionDao = shopCollectionDao;
        this.shopCollectionDao.deleteAll();
        this.shopCollectionDao.insertAll();
//        dao.deleteAll();
//        dao.insertAll();
//        save2db(dao);

        /*Query PostCar*/
        //Todo HashTag
        shopHashTag.setDao(this.shopCollectionDao.queryFindBrandDuplicates());
        shopHashTag.setChildBrand(this.shopCollectionDao.queryPostCar().getListCar());
        shopHashTag.notifyDataSetChanged();

        mAdapter.setDao(this.shopCollectionDao.getPostCarCellection());
        mAdapter.notifyDataSetChanged();
    }


    private void initProfile(ProfileDao profileDao){
        this.profileDao = profileDao;
        String strViewShop = profileDao.getShopView()+" View | "+profileDao.getShopFollow()+" Follow";
        viewShop.setText(strViewShop);
        //TODO Image Profile
        Glide.with(this).load(profileDao.getUrlShopLogo())
                .placeholder(com.hndev.library.R.drawable.loading)
                .bitmapTransform(new CropCircleTransformation(getActivity()))
                .into(shopProfilePicture);

        shopName.setText(profileDao.getShopName());
        shopDescription.setText(profileDao.getShopDescription());
        shopNumber.setText(profileDao.getShopNumber());

        initProfileShopBg(profileDao,idImageBackground);
        imageHeaderAdapter.setUrlPath(profileDao.getProfilePath());
        imageHeaderAdapter.notifyDataSetChanged();
    }

    private void initProfileShopBg(ProfileDao dao,int id){
        Glide.with(this).load(dao.getUrlShopBackground(id))
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

        if (listImageHeader.getVisibility() == View.GONE){
            Animation animationHeader = AnimationUtils.loadAnimation(
                    Contextor.getInstance().getContext(),
                    R.anim.activity_slide_left_in);
            listImageHeader.setVisibility(View.VISIBLE);
            listImageHeader.startAnimation(animationHeader);
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

        if (listImageHeader.getVisibility() == View.VISIBLE){
            Animation animationHeader = AnimationUtils.loadAnimation(
                    Contextor.getInstance().getContext(),
                    R.anim.activity_slide_right_out);
            listImageHeader.setVisibility(View.GONE);
            listImageHeader.startAnimation(animationHeader);
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
        args.putString("logoShop",profileDao.getUrlShopLogo());
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

    @OnClick({R.id.pictureShopProfile,R.id.imageShopBackground})
    public void viewPictureShop(View view){
        int id = view.getId();
        switch (id){
            case R.id.pictureShopProfile:
                viewSingleImage(profileDao.getUrlShopLogo());
                break;
            default:
                viewSingleImage(profileDao.getUrlShopBackground(idImageBackground));
                break;
        }

    }

    private void viewSingleImage(String url){
        Intent intent = new Intent(getActivity(),
                SingleViewImageActivity.class);
        intent.putExtra(SingleViewImageActivity.ARGS_PICTURE, url);
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
        outState.putParcelable("dao",Parcels.wrap(shopCollectionDao));
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
        shopCollectionDao = Parcels.unwrap(savedInstanceState.getParcelable("dao"));
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
            return mAdapter.isHeader(position) ? mManager.getSpanCount() : 1;
        }
    };

    Observer<ShopCollectionDao> shopCollectionDaoObserver = new Observer<ShopCollectionDao>() {
        @Override
        public void onCompleted() {
            Log.i(TAG, "onCompleted: ");
        }

        @Override
        public void onError(Throwable e) {
//            swipeRefresh.setRefreshing(false);
            Log.e(TAG, "onError: ", e);
        }

        @Override
        public void onNext(ShopCollectionDao shopCollectionDao) {
//            swipeRefresh.setRefreshing(false);
            initData(shopCollectionDao);
            // save cache Dao
//            boolean b = daoCacheManager.save("shop", shopCollectionDao);
//            Log.i(TAG, "Call Success: Rx android  "+b);
        }
    };

    ShopAdapter.RecyclerOnItemClickListener recyclerOnItemClickListener = new ShopAdapter.RecyclerOnItemClickListener() {
        @Override
        public void OnClickItemListener(View v, int position) {
            PostCarDao modelCar = shopCollectionDao.getPostCarDao().get(position);
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
            PostCarDao modelCar = shopCollectionDao.getPostCarDao().get(position);
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
