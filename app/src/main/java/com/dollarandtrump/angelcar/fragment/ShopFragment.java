package com.dollarandtrump.angelcar.fragment;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dollarandtrump.angelcar.Adapter.ShopAdapter;
import com.dollarandtrump.angelcar.Adapter.ShopHashTagAdapter;
import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.activity.PostActivity;
import com.dollarandtrump.angelcar.activity.SingleViewImageActivity;
import com.dollarandtrump.angelcar.activity.ViewDetailActivity;
import com.dollarandtrump.angelcar.dao.PostCarDao;
import com.dollarandtrump.angelcar.dao.ProfileDao;
import com.dollarandtrump.angelcar.dao.ShopCollectionDao;
import com.dollarandtrump.angelcar.dialog.ShopEditDialog;
import com.dollarandtrump.angelcar.dialog.ShopUpLoadDialog;
import com.dollarandtrump.angelcar.listener.AppBarStateChangeListener;
import com.dollarandtrump.angelcar.manager.Contextor;
import com.dollarandtrump.angelcar.manager.Registration;
import com.dollarandtrump.angelcar.manager.http.HttpManager;
import com.dollarandtrump.angelcar.view.ImageViewGlide;
import com.dollarandtrump.angelcar.view.ListHashTag;
import com.github.clans.fab.FloatingActionMenu;
import com.hndev.library.view.AngelCarHashTag;

import org.parceler.Parcels;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
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

    @Bind(R.id.recycler_image_header_shop) RecyclerView mListImageHeader;
    @Bind(R.id.recycler_car) RecyclerView mList;

    @Bind(R.id.image_button_up_and_down) ImageView mImageUpDown;
    @Bind(R.id.image_background_shop) ImageView mImageBgShop;
    @Bind(R.id.text_view_shop_decription) TextView mTextShopDescription;
    @Bind(R.id.text_view_shop_number) TextView mTextShopNumber;
    @Bind(R.id.text_view_shop_name) TextView mTextShopName;
    @Bind(R.id.text_view_view_shop) TextView mTextViewShop;

    @Bind(R.id.frame_layout_progressbar) FrameLayout mGroupProgressbar;
    @Bind(R.id.progressbar_load) ProgressBar mProgressbarLoad;
    @Bind(R.id.image_view_glide_profile) ImageViewGlide mImageProfile;
    @Bind(R.id.app_barLayout) AppBarLayout mAppBarLayout;
    @Bind(R.id.floating_action_menu_fab) FloatingActionMenu mMenuFab;
    @Bind(R.id.list_hash_tag) ListHashTag mListHashTag;


    private ImageHeaderAdapter mImageHeaderAdapter;
    private ShopHashTagAdapter mHashTagAdapter;
    private GridLayoutManager mManager;
    private Subscription mSubscription;
    private ShopAdapter mAdapter;

    private ShopCollectionDao mShopCollectionDao;
    private ProfileDao mProfileDao;

    private boolean isControlRecycler = true;
    private boolean isControl = true;
    boolean isShow = false;
    boolean isShop;

    private int mIdImageBackground = 0;
    private int mLastRecycler = 0;
    private int mLast = -100;


    public ShopFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static ShopFragment newInstance(String user,String shop) {
        ShopFragment fragment = new ShopFragment();
        Bundle args = new Bundle();
        args.putString("user",user);
        args.putString("shop",shop);
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
        isShop = getArguments().getString("user").equals(Registration.getInstance().getUserId());
        mShopCollectionDao = new ShopCollectionDao();
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        ButterKnife.bind(this,rootView);
        mMenuFab.setVisibility(isShop ? View.VISIBLE : View.GONE);

        mMenuFab.setClosedOnTouchOutside(true);
        mAppBarLayout.setExpanded(false,true);

        if (savedInstanceState == null){
            loadData();
        }

        //Header Shop
        mListImageHeader.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        mImageHeaderAdapter = new ImageHeaderAdapter();
        mListImageHeader.setAdapter(mImageHeaderAdapter);
        mImageHeaderAdapter.setOnItemClickListener(new ImageHeaderAdapter.HeaderShop() {
            @Override
            public void OnClickItemListener(View view, int position) {
                if (mProfileDao != null)
                    mIdImageBackground = position;
                    initProfileShopBg(mProfileDao,position);
            }
        });


        mManager = new GridLayoutManager(getActivity(),3);
        mList.setLayoutManager(mManager);
        mAdapter = new ShopAdapter();
        mAdapter.setShop(isShop);
        mAdapter.setDao(mShopCollectionDao.getPostCarCollection());
        mList.setAdapter(mAdapter);
        mAdapter.setOnclickListener(recyclerOnItemClickListener);
        mManager.setSpanSizeLookup(spanSizeLookupManager);


        mHashTagAdapter = new ShopHashTagAdapter();
        mListHashTag.setAdapter(mHashTagAdapter);
        mHashTagAdapter.setOnItemClickHashTagListener(new ShopHashTagAdapter.OnItemClickHashTagListener() {
            @Override
            public void onItemClick(boolean isSelected, int position, String brand) {
                if (isSelected && position != 0) {
                     mAdapter.setDao(mShopCollectionDao.findPostCar(brand));
                     mAdapter.notifyDataSetChanged();
                }else{
                    mAdapter.setDao(mShopCollectionDao.queryPostCar());
                    mAdapter.notifyDataSetChanged();
                }
                for (int i = 0; i < mListHashTag.getChildCount(); i++) {
                    if (i != position) {
                        if (mListHashTag.getChildAt(i) instanceof AngelCarHashTag) {
                            AngelCarHashTag v = (AngelCarHashTag) mListHashTag.getChildAt(i);
                            v.hideChildSubCar();
                            Log.d(TAG, "onItemClick: "+i);
                        }
                    }
                }
            }
        });

        initScroll();

    }

    private void initScroll() {
        mAppBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                if (state == State.EXPANDED){
                    showProfile();
                    imageUpDownIcon();
                    isShow = true;
                }else if (state == State.IDLE){
                    hideProfile();
                }else {
                    imageUpDownIcon();
                    isShow = false;
                }
            }
        });

        mList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy < mLastRecycler && !isControlRecycler){
                    //scrollUp
                    showFabButton();
                    isControlRecycler = true;
                    mLastRecycler = 0;
                }else if(dy > mLastRecycler && isControlRecycler){
                    //scrollDown
                    hideFabButton();
                    isControlRecycler = false;
                    mLastRecycler = 0;
                }

                if ((isControlRecycler && dy > 0) || (!isControlRecycler && dy < 0))
                    mLastRecycler += dy;
            }
        });
    }

    private void loadData() {
        visibilityGroupProgress(View.VISIBLE);
        String userRef = getArguments().getString("user");
        String shopRef = getArguments().getString("shop");

            Observable<ShopCollectionDao> rxCall = HttpManager.getInstance().getService()
                    .observableLoadShop(userRef, shopRef)
//                    .observableLoadShop("2016050200001", "68")
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread());
        mSubscription = rxCall.subscribe(shopCollectionDaoObserver);
    }

    private void initData(ShopCollectionDao shopCollectionDao){
        initProfile(shopCollectionDao.getProfileDao());
        this.mShopCollectionDao = shopCollectionDao;
        this.mShopCollectionDao.deleteAll();
        this.mShopCollectionDao.insertAll();

        /*Query PostCar*/
        //Todo HashTag
        mHashTagAdapter.setDao(this.mShopCollectionDao.queryFindBrandDuplicates());
        mHashTagAdapter.setChildBrand(this.mShopCollectionDao.queryPostCar().getListCar());
        mHashTagAdapter.notifyDataSetChanged();

        mAdapter.setDao(this.mShopCollectionDao.getPostCarCollection());
        mAdapter.notifyDataSetChanged();
    }


    private void initProfile(ProfileDao profileDao){
        this.mProfileDao = profileDao;
        String strViewShop = profileDao.getShopView()+" View | "+profileDao.getShopFollow()+" Follow";
        mTextViewShop.setText(strViewShop);
        mImageProfile.setImageUrl(getActivity(),profileDao.getUrlShopLogo());

        mTextShopName.setText(profileDao.getShopName());
        mTextShopDescription.setText(profileDao.getShopDescription());
        mTextShopNumber.setText(profileDao.getShopNumber());

        initProfileShopBg(profileDao, mIdImageBackground);
        mImageHeaderAdapter.setUrlPath(profileDao.getProfilePath());
        mImageHeaderAdapter.notifyDataSetChanged();
    }

    private void initProfileShopBg(ProfileDao dao,int id){
        Glide.with(this).load(dao.getUrlShopBackground(id))
                .into(mImageBgShop);
    }

    public void intentEditPost(PostCarDao modelCar) {
        Intent intent = new Intent(getActivity(),PostActivity.class);
        intent.putExtra("isEdit",true);
        intent.putExtra("carModel", Parcels.wrap(modelCar));
        startActivity(intent);
    }

    private void showProfile(){
            mImageProfile.setVisibility(View.VISIBLE);

            mListImageHeader.setVisibility(View.VISIBLE);
            Animation animationHeader = AnimationUtils.loadAnimation(
                    Contextor.getInstance().getContext(),
                    R.anim.activity_slide_left_in);
            mListImageHeader.startAnimation(animationHeader);
    }

    private void hideProfile(){
            mImageProfile.setVisibility(View.GONE);

        if (mListImageHeader.getVisibility() == View.VISIBLE){
            mListImageHeader.setVisibility(View.GONE);
            Log.d(TAG, "hideProfile: "+mListImageHeader.isAnimating());
            Animation animationHeader = AnimationUtils.loadAnimation(
                    Contextor.getInstance().getContext(),
                    R.anim.activity_slide_right_out);
            mListImageHeader.startAnimation(animationHeader);
        }
    }

    @OnClick(R.id.image_button_up_and_down)
    public void onClickShowAppBar(){
        imageUpDownIcon();
        mAppBarLayout.setExpanded(!isShow,true);
    }

    void imageUpDownIcon(){
        mImageUpDown.setImageResource(!isShow ? R.drawable.ic_shop_hide_back:R.drawable.ic_shop_show_back);
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
        args.putString("shopName", mTextShopName.getText().toString());
        args.putString("shopDescription",mTextShopDescription.getText().toString());
        args.putString("shopNumber",mTextShopNumber.getText().toString());
        args.putString("logoShop", mProfileDao.getUrlShopLogo());
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

        if (mMenuFab.isOpened()){
            mMenuFab.close(true);
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

    @OnClick(R.id.image_background_shop)
    public void viewPictureShop(View view) {
        Intent intent = new Intent(getActivity(),
                SingleViewImageActivity.class);
        intent.putExtra("url", mProfileDao.getUrlShopBackground(mIdImageBackground));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity(), view, "image").toBundle());
        }else {
            getActivity().startActivity(intent);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("dao",Parcels.wrap(mShopCollectionDao));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        if (mSubscription != null)
            mSubscription.unsubscribe();
    }

    void visibilityGroupProgress(int view){
        mGroupProgressbar.setVisibility(view);
        mProgressbarLoad.setVisibility(view);
    }

    @SuppressWarnings("UnusedParameters")
    private void onRestoreInstanceState(Bundle savedInstanceState) {
        mShopCollectionDao = Parcels.unwrap(savedInstanceState.getParcelable("dao"));
    }

    private void showFabButton(){
        mMenuFab.showMenuButton(true);
    }

    private void hideFabButton(){
        mMenuFab.hideMenuButton(false);
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
            visibilityGroupProgress(View.GONE);
        }

        @Override
        public void onError(Throwable e) {
            Log.e(TAG, "onError: ", e);
            visibilityGroupProgress(View.GONE);
        }

        @Override
        public void onNext(ShopCollectionDao shopCollectionDao) {
            initData(shopCollectionDao);
            visibilityGroupProgress(View.GONE);
        }
    };

    ShopAdapter.RecyclerOnItemClickListener recyclerOnItemClickListener = new ShopAdapter.RecyclerOnItemClickListener() {
        @Override
        public void OnClickItemListener(View v, int position) {
            PostCarDao modelCar = mShopCollectionDao.getPostCarDao().get(position);
            Intent inViewDetail = new Intent(getActivity(), ViewDetailActivity.class);
            inViewDetail.putExtra("is_shop",isShop);
            inViewDetail.putExtra("dao",Parcels.wrap(modelCar));
            startActivity(inViewDetail);
        }

        @Override
        public void OnClickSettingListener(View v, int position) {
            PostCarDao modelCar = mShopCollectionDao.getPostCarDao().get(position);
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
