package com.dollarandtrump.angelcar.fragment;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.dollarandtrump.angelcar.Adapter.FeedPostCarAdapter;
import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.activity.ChatCarActivity;
import com.dollarandtrump.angelcar.activity.EditPostActivity;
import com.dollarandtrump.angelcar.activity.ShopActivity;
import com.dollarandtrump.angelcar.activity.ViewCarActivity;
import com.dollarandtrump.angelcar.anim.ResizeHeight;
import com.dollarandtrump.angelcar.dao.CarBrandDao;
import com.dollarandtrump.angelcar.dao.CarSubDao;
import com.dollarandtrump.angelcar.dao.CountCarCollectionDao;
import com.dollarandtrump.angelcar.dao.PostCarCollectionDao;
import com.dollarandtrump.angelcar.dao.PostCarDao;
import com.dollarandtrump.angelcar.datatype.MutableInteger;
import com.dollarandtrump.angelcar.dialog.DetailAlertDialog;
import com.dollarandtrump.angelcar.dialog.FilterBrandDialog;
import com.dollarandtrump.angelcar.dialog.FilterSubDetailDialog;
import com.dollarandtrump.angelcar.dialog.FilterSubDialog;
import com.dollarandtrump.angelcar.dialog.YearDialog;
import com.dollarandtrump.angelcar.manager.Contextor;
import com.dollarandtrump.angelcar.manager.PostCarManager;
import com.dollarandtrump.angelcar.manager.Registration;
import com.dollarandtrump.angelcar.manager.bus.MainThreadBus;
import com.dollarandtrump.angelcar.manager.http.HttpManager;
import com.dollarandtrump.angelcar.model.InfoCarModel;
import com.hndev.library.view.AngelCarPost;

import org.parceler.Parcels;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


public class FeedPostFragment extends Fragment{
    private static final String TAG = "FeedPostFragment";

    public static final int REQUEST_CODE_BRAND = 1;
    public static final int REQUEST_CODE_SUB = 2;
    public static final int REQUEST_CODE_SUB_DETAIL = 3;
    public static final int REQUEST_CODE_YEAR = 4;

    public static final String ARG_BRAND = "brand";
    public static final String ARG_SUB = "sub";
    public static final String ARG_SUB_DETAIL = "subDetail";


    @Bind(R.id.swipe_container) SwipeRefreshLayout mSwipeRefresh;
    @Bind(R.id.text_view_count_car_all) TextView mCountCarAll;
    @Bind(R.id.scroll_view_filter) ScrollView mScrollFilter;
    @Bind(R.id.text_view_new_post) TextView mTextNewPost;
    @Bind(R.id.list_view) ListView mListView;

    //Filter
    @Bind(R.id.edit_text_price_start) EditText mEditTextPriceStart;
    @Bind(R.id.edit_text_price_end) EditText mEditTextPriceEnd;
    @Bind(R.id.text_view_sub_detail) TextView mTextSubDetail;
    @Bind(R.id.text_view_brand) TextView mTextBrand;
    @Bind(R.id.text_view_year) TextView mTextYear;
    @Bind(R.id.text_view_sub) TextView mTextSub;

    private MutableInteger mLastPositionInteger;
    private InfoCarModel mCopyInformLoadMore;
    private InfoCarModel mInfoCarModel;

    private boolean isStopLoadingMore = false; // หยุดการ LoadMore
    private boolean isLoadingMore = false;

    private PostCarManager mPostManager;
    private FeedPostCarAdapter mAdapter;

    private boolean isFilter = false;

    private Subscription subscribeCountCar;

    public FeedPostFragment() {
        super();
    }

    public static FeedPostFragment newInstance() {
//        Bundle args = new Bundle();
//        fragment.setArguments(args);
        return new FeedPostFragment();
    }
    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(savedInstanceState);

        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_feed_post_car, container, false);
        initInstances(rootView,savedInstanceState);
        return rootView;

    }

    @SuppressWarnings("UnusedParameters")
    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
        mInfoCarModel = new InfoCarModel();
        mPostManager = new PostCarManager();
        mLastPositionInteger = new MutableInteger(-1);
//        dao = new PostCarCollectionDao();
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        ButterKnife.bind(this,rootView);

        mAdapter = new FeedPostCarAdapter(getContext(),mLastPositionInteger);
        mAdapter.setDao(mPostManager.getDao());
        mListView.setAdapter(mAdapter);
        mSwipeRefresh.setColorSchemeColors(
                Color.parseColor("#104F94"),
                Color.parseColor("#104F94"),
                Color.parseColor("#FFC11E"),
                Color.parseColor("#FFC11E"));

        mSwipeRefresh.setOnRefreshListener(pullRefresh);
        mListView.setOnItemClickListener(onItemClickListViewListener);
        mListView.setOnItemLongClickListener(onItemLongClickListener);
        mListView.setOnScrollListener(onScrollListener);

        if (savedInstanceState == null) {
            refreshData();
        }

        // view shop
        mAdapter.setOnClickImageProfile(new AngelCarPost.OnClickImageProfile() {
            @Override
            public void onClickImageProfileListener(int position) {
                String user = mPostManager.getDao().getListCar().get(position).getUser();
                String shop = mPostManager.getDao().getListCar().get(position).getShopRef();
                Intent intentShop = new Intent(getActivity(), ShopActivity.class);
                intentShop.putExtra("user", user);
                intentShop.putExtra("shop", shop);
                startActivity(intentShop);
            }
        });

    }

    private void refreshData(){
        isStopLoadingMore = false;
        if (mPostManager.getCount() == 0)
            reloadData();
        else
            reloadDataNewer();
    }

    private void reloadDataNewer() {
        loadCountCar();
        // load post newer
        Call<PostCarCollectionDao> call =
                HttpManager.getInstance().getService().loadNewerPostCar(mPostManager.firstDateDao());
        call.enqueue(new FeedCallback(FeedCallback.MODE_RELOAD_NEWER));
    }

    private void loadCountCar() {
        // load count car
//        Call<CountCarCollectionDao> callLoadCountCar =
//                HttpManager.getInstance().getService().loadCountCar();
//        callLoadCountCar.enqueue(countCarCallback);

        subscribeCountCar = HttpManager.getInstance().getService().observableLoadCountCar()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<CountCarCollectionDao>() {
                    @Override
                    public void call(CountCarCollectionDao countCarCollectionDao) {
                        CountCarCollectionDao.CountCarGao countCar = countCarCollectionDao.getRows().get(0);
                        String textAll = countCar.getCountAll() + "/" +
                                countCar.getCountMonth() + "/" + countCar.getCountDay() + " คัน"; // "ทั้งหมด " + countCarGao.getCountAll() + " คัน";
                        mCountCarAll.setText(textAll);
                    }
                });

    }

    private void reloadData() {
        loadCountCar();
        //load Post
        Call<PostCarCollectionDao> call = HttpManager.getInstance().getService().loadPostCar();
        call.enqueue(new FeedCallback(FeedCallback.MODE_RELOAD));
    }

    private void loadMoreData(){
//        if (isStopLoadingMore) return;
//        Log.d(TAG, "loadMoreData 1: "+isStopLoadingMore +" "+isLoadingMore);
        if (isStopLoadingMore || isLoadingMore) return;

        isLoadingMore = true;
        mAdapter.setLoading(true);
        Call<PostCarCollectionDao> callLoadingMore = HttpManager.getInstance()
                .getService().loadMorePostCar(mPostManager.lastDateDao());
        callLoadingMore.enqueue(new FeedCallback(FeedCallback.MODE_LOAD_MORE));
        Log.d(TAG, "loadMoreData 2: "+isStopLoadingMore +" "+isLoadingMore);

    }

    private void loadMoreFilterData(){
//        if (isStopLoadingMore) return;
        if (isStopLoadingMore || isLoadingMore) return;

        isLoadingMore = true;
        mAdapter.setLoading(true);
        if (mCopyInformLoadMore == null) return;
        // get date
        mCopyInformLoadMore.setDateMore(mPostManager.lastDateDao());
        Call<PostCarCollectionDao> call = HttpManager.getInstance()
                .getService().loadFilterFeed(mCopyInformLoadMore.getMapFilter());
        call.enqueue(new FilterCallback(1));
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        outState.putParcelable(SAVE_STATE_GAO,Parcels.wrap(dao));
          mPostManager.onSaveInstanceState();
          outState.putBundle("lastPositionInteger",
                  mLastPositionInteger.onSaveInstanceState());
    }

    @SuppressWarnings("UnusedParameters")
    private void onRestoreInstanceState(Bundle savedInstanceState) {
          mPostManager.onRestoreInstanceState(savedInstanceState);
        mLastPositionInteger.onRestoreInstanceState(
                savedInstanceState.getBundle("lastPositionInteger"));
    }

    @OnClick(R.id.btFilter)
    public void ShowHideFilter(){
        if (mScrollFilter.getVisibility() == View.GONE) {
            ResizeHeight resizeHeight = new ResizeHeight(mScrollFilter,1000,true);
            resizeHeight.setInterpolator(new OvershootInterpolator());//(new BounceInterpolator());
            resizeHeight.setDuration(1000);
            mScrollFilter.startAnimation(resizeHeight);
            mScrollFilter.setVisibility(View.VISIBLE);
        } else {
            ResizeHeight resizeHeight = new ResizeHeight(mScrollFilter,0,false);
            resizeHeight.setDuration(300);
            mScrollFilter.startAnimation(resizeHeight);
        }
    }

    @OnClick({R.id.text_view_brand,R.id.text_view_sub,R.id.text_view_sub_detail,R.id.text_view_year,R.id.button_search})
    public void OnclickFilter(View v){
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        switch (v.getId()){

            case R.id.button_search:
                loadFilter();
                ShowHideFilter();
                mTextBrand.setText("All");
                clearObject();

                break;

            case R.id.text_view_brand:
                //Clear Object
                clearObject();
                Fragment fragment = getChildFragmentManager().findFragmentByTag("FilterBrandDialog");
                if (fragment != null){
                    ft.remove(fragment);
                }
                ft.addToBackStack(null);

                FilterBrandDialog dialog = new FilterBrandDialog();
                dialog.setTargetFragment(this,REQUEST_CODE_BRAND);
                dialog.show(getChildFragmentManager(),"FilterBrandDialog");
                break;

            case R.id.text_view_sub:
                if (isNullFilter(mInfoCarModel,0)) {
                    Fragment fragmentSub = getChildFragmentManager().findFragmentByTag("FilterSubDialog");
                    if (fragmentSub != null) {
                        ft.remove(fragmentSub);
                    }
                    ft.addToBackStack(null);
                    FilterSubDialog dialogSub = FilterSubDialog
                            .newInstance(mInfoCarModel);
                    dialogSub.setTargetFragment(this, REQUEST_CODE_SUB);
                    dialogSub.show(getChildFragmentManager(), "FilterSubDialog");
                }
                break;

            case R.id.text_view_sub_detail:
                if (isNullFilter(mInfoCarModel,1)){
                    Fragment fragmentSubDetail = getChildFragmentManager().findFragmentByTag("FilterSubDialog");
                    if (fragmentSubDetail != null) {
                        ft.remove(fragmentSubDetail);
                    }
                    ft.addToBackStack(null);
                    FilterSubDetailDialog dialogSub = FilterSubDetailDialog
                            .newInstance(mInfoCarModel);
                    dialogSub.setTargetFragment(this, REQUEST_CODE_SUB_DETAIL);
                    dialogSub.show(getChildFragmentManager(), "FilterSubDialog");
                }
                break;

            case R.id.text_view_year:
                Fragment fragmentYear = getChildFragmentManager().findFragmentByTag("YearDialog");
                if (fragmentYear != null){
                    ft.remove(fragmentYear);
                }
                ft.addToBackStack(null);
                YearDialog dialogYear = new YearDialog();
//                dialogYear.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
                dialogYear.setTargetFragment(this,REQUEST_CODE_YEAR);
                dialogYear.show(getChildFragmentManager(),"YearDialog");
                break;
            default: // button Search
                break;
        }

    }

    private void loadFilter() {
        isFilter = true;
        if (mInfoCarModel != null) {
            /* **set Price */
            mInfoCarModel.setPriceStart(mEditTextPriceStart.getText().toString());
            mInfoCarModel.setPriceEnd(mEditTextPriceEnd.getText().toString());
            mCopyInformLoadMore = mInfoCarModel;
            Call<PostCarCollectionDao> callFilter =
                    HttpManager.getInstance().getService()
                            .loadFilterFeed(mInfoCarModel.getMapFilter());
            callFilter.enqueue(new FilterCallback(0));
        }
    }

    @OnClick({R.id.radioGearAll,R.id.radioGearAt,R.id.radioGearMt})
    void radioButtonGear(View view){
        boolean checked = ((RadioButton) view).isChecked();
        if (view.getId() == R.id.radioGearMt){
            mInfoCarModel.setGear(checked ? "1" : "0");
        }else if (view.getId() == R.id.radioGearAt){
            mInfoCarModel.setGear(checked ? "2" : "0");
        }else {// all
            mInfoCarModel.setGear("gear");
        }
        Log.i(TAG, "radioButtonGear: "+ mInfoCarModel.getGear());
    }

    private boolean isNullFilter(InfoCarModel infoCarModel, int mode){
        if (mode == 0){
            if (infoCarModel.getBrandDao() != null && !infoCarModel.getBrandDao().getBrandName().isEmpty())
            return true;
        }else {
            if (infoCarModel.getBrandDao() != null && infoCarModel.getSubDao() != null &&
                    !infoCarModel.getBrandDao().getBrandName().isEmpty() &&
                    !infoCarModel.getSubDao().getSubName().isEmpty())
            return true;
        }
        return false;
    }

    private void clearObject() {
        if (mInfoCarModel != null) {
//            mInfoCarModel.clear();
//            mInfoCarModel = null;
            mInfoCarModel = new InfoCarModel();
        }
    }

    private void showTextNewPost(){
        mTextNewPost.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(
                Contextor.getInstance().getContext(),
                R.anim.zoom_face_in);
        mTextNewPost.startAnimation(animation);
    }

    private void hideTextNewPost(){
        if (mTextNewPost.getVisibility() == View.VISIBLE) {
            mTextNewPost.setVisibility(View.GONE);
            Animation animation = AnimationUtils.loadAnimation(
                    Contextor.getInstance().getContext(),
                    R.anim.zoom_face_out);
            mTextNewPost.startAnimation(animation);
        }
    }

    @OnClick(R.id.text_view_new_post)
    public void showButtonNewPost(){
        mListView.smoothScrollToPosition(0);
        hideTextNewPost();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {

                case REQUEST_CODE_BRAND: // brand
                    CarBrandDao model = Parcels.unwrap(data.getParcelableExtra(ARG_BRAND));
                    mInfoCarModel.setResIdLogo(data.getIntExtra("logo",0));
                    mInfoCarModel.setBrandDao(model);
                    mTextBrand.setText(model.getBrandName());
                    mTextSub.setText("All");
                    mTextSubDetail.setText("All");
                    break;

                case REQUEST_CODE_SUB: // sub
                    CarSubDao modelSub = Parcels.unwrap(data.getParcelableExtra(ARG_SUB));
                    mInfoCarModel.setSubDao(modelSub);
                    mTextSub.setText(modelSub.getSubName());
                    mTextSubDetail.setText("All");
                    break;

                case REQUEST_CODE_SUB_DETAIL: // sub detail
                    CarSubDao modelSubDetail = Parcels.unwrap(data.getParcelableExtra(ARG_SUB_DETAIL));
                    mInfoCarModel.setSubDetailDao(modelSubDetail);
                    mTextSubDetail.setText(modelSubDetail.getSubName());
                    break;

                case REQUEST_CODE_YEAR: // year
                    int resultYear = data.getIntExtra("TAG_YEAR",2016);
                    mInfoCarModel.setYear(resultYear);
                    mTextYear.setText(String.valueOf(resultYear));
                    break;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);

        if (subscribeCountCar != null){
            subscribeCountCar.unsubscribe();
        }
    }

    /**************
    *Listener Zone*
    ***************/
    SwipeRefreshLayout.OnRefreshListener pullRefresh = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            isStopLoadingMore = false;
            if (isFilter) {
                isFilter = false;
                mPostManager.clearCache();
                isLoadingMore = false;
                mAdapter.setLoading(true);
                reloadData();
                return;
            }
            refreshData();
        }
    };


    int last = 0;
    boolean control = true;
    AbsListView.OnScrollListener onScrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }
        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (view == mListView) {
                mSwipeRefresh.setEnabled(firstVisibleItem == 0);
                if (firstVisibleItem + visibleItemCount >= totalItemCount) {
                    if (mPostManager.getCount() > 0) {

                        if (!isFilter)
                            //Load more
                            loadMoreData();
                        else
                            // load more filter
                            loadMoreFilterData();
                    }
                }
            }
            if (firstVisibleItem < 1)
                hideTextNewPost();

            if (firstVisibleItem < last && !control){
                //scrollUp
                MainThreadBus.getInstance().post(new Scrolling(Scrolling.SCROLL_UP));
                control = true;
            }else if(firstVisibleItem > last && control){
                //scrollDown
                MainThreadBus.getInstance().post(new Scrolling(Scrolling.SCROLL_DOWN));
                control = false;

            }
            last = firstVisibleItem;

        }
    };

    AdapterView.OnItemLongClickListener onItemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            //TODO-GREEN Handel Show Detail Car
            if (Registration.getInstance().getUserId() != null) {
                PostCarDao modelCar = mPostManager.getDao().getListCar().get(position);
                boolean isShop = modelCar.getShopRef().contains(Registration.getInstance().getShopRef());
                Intent inViewDetail = new Intent(getActivity(), ViewCarActivity.class);
                inViewDetail.putExtra("is_shop",isShop);
                inViewDetail.putExtra("dao",Parcels.wrap(modelCar));
                startActivity(inViewDetail);
                return true;
            }
            return false;
        }
    };

    DetailAlertDialog.OnClickEdit onClickEditListener = new DetailAlertDialog.OnClickEdit() {
        @Override
        public void onClickEdit(PostCarDao modelCar) {
            Intent intent = new Intent(getActivity(),EditPostActivity.class);
            intent.putExtra("postCarDao", Parcels.wrap(modelCar));
            startActivity(intent);
        }
    };

    AdapterView.OnItemClickListener onItemClickListViewListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (Registration.getInstance().getUserId() != null) {
                if (mPostManager.getDao().getListCar().size() > position) {
                    final PostCarDao item = mPostManager.getDao().getListCar().get(position);
                    boolean isShop = item.getShopRef().contains(Registration.getInstance().getShopRef());
                    Intent intent ;
                    if (isShop){
                        intent = new Intent(getActivity(), ViewCarActivity.class);
                        intent.putExtra("is_shop",isShop);
                        intent.putExtra("dao",Parcels.wrap(item));
                    }else {
                        intent = new Intent(getActivity(), ChatCarActivity.class);
                        intent.putExtra("PostCarDao", Parcels.wrap(item));
                        intent.putExtra("intentForm", 0);
                        intent.putExtra("messageFromUser", Registration.getInstance().getUserId());
                    }
                    startActivity(intent);

                }
            }else {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment prev = getFragmentManager().findFragmentByTag("RegistrationAlertFragment");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);
                RegistrationAlertFragment fragment = RegistrationAlertFragment.newInstance();
                fragment.setCancelable(false);
                fragment.show(ft, "RegistrationAlertFragment");
            }

        }
    };


//    Callback<CountCarCollectionDao> countCarCallback = new Callback<CountCarCollectionDao>() {
//        @Override
//        public void onResponse(Call<CountCarCollectionDao> call, Response<CountCarCollectionDao> response) {
//            if (response.isSuccessful()) {
//                CountCarCollectionDao.CountCarGao countCarGao = response.body().getRows().get(0);
//                String textAll = countCarGao.getCountAll() +"/"+
//                        countCarGao.getCountMonth() +"/"+ countCarGao.getCountDay()+" คัน"; // "ทั้งหมด " + countCarGao.getCountAll() + " คัน";
//                mCountCarAll.setText(textAll);
//            } else {
//                try {
//                    Log.i(TAG, "onResponse: " + response.errorBody().string());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        @Override
//        public void onFailure(Call<CountCarCollectionDao> call, Throwable t) {
//            Log.e(TAG, "onFailure: ", t);
//        }
//    };


    /*****************
    *Inner Class Zone*
    ******************/
    public class Scrolling { // Event Bus Produce To MainActivity
        public static final int SCROLL_UP = 0;
        public static final int SCROLL_DOWN = 1;
        int scroll;
        public Scrolling(int scroll) {
            this.scroll = scroll;
        }

        public int getScroll() {
            return scroll;
        }
    }

    private class FeedCallback implements Callback<PostCarCollectionDao> {
        public static final int MODE_RELOAD = 1;
        public static final int MODE_RELOAD_NEWER = 2;
        public static final int MODE_LOAD_MORE = 3;
        int mode;

        public FeedCallback(int mode) {
            this.mode = mode;
        }

        @Override
        public void onResponse(Call<PostCarCollectionDao> call, Response<PostCarCollectionDao> response) {
            if(mSwipeRefresh != null)
                mSwipeRefresh.setRefreshing(false);

            mAdapter.setLoading(false);

            if (response.isSuccessful()) {
                PostCarCollectionDao dao = response.body();
                int firstVisiblePosition = 0;
                int top = 0;
                if (mListView != null) {
                    firstVisiblePosition = mListView.getFirstVisiblePosition();
                    View c = mListView.getChildAt(0);
                    top = c == null ? 0 : c.getTop();
                }

                if (mode == MODE_RELOAD_NEWER) {
                    mPostManager.insertDaoAtTopPosition(dao);
                    if (dao != null && dao.getListCar() != null) {
                        String textNewPost = "โพสใหม่";// + dao.getListCar().size();
                        mTextNewPost.setText(textNewPost);
                    }
                } else if (mode == MODE_LOAD_MORE) {
                    mPostManager.appendDataToBottomPosition(dao);
                    if (dao.getListCar() == null){
                        isStopLoadingMore = true;
                    }

                } else {
                    mPostManager.setDao(dao);
                }
                clearLoadingMoreFlagIfCapable(mode);
                mAdapter.setDao(mPostManager.getDao());
                mAdapter.notifyDataSetChanged();

                if (mode == MODE_RELOAD_NEWER){
                    int additionalSize =
                            (dao != null && dao.getListCar()!= null)
                                    ? dao.getListCar().size() : 0;
                    mAdapter.increaseLastPosition(additionalSize);
                    mListView.setSelectionFromTop(firstVisiblePosition+additionalSize,top);

                    if (additionalSize > 0){
                        showTextNewPost();
                    }
                }

            } else {
                isStopLoadingMore = true;
                clearLoadingMoreFlagIfCapable(mode);
                Log.i(TAG, "onResponse: " + response.errorBody().toString());
            }
        }

        @Override
        public void onFailure(Call<PostCarCollectionDao> call, Throwable t) {
            isStopLoadingMore = true;
            mSwipeRefresh.setRefreshing(false);
            mAdapter.setLoading(false);
            clearLoadingMoreFlagIfCapable(mode);
            Log.e(TAG, "onFailure: ", t);
        }

        private void clearLoadingMoreFlagIfCapable(int mode){
            if (mode == MODE_LOAD_MORE) {
                isLoadingMore = false;
                Log.i(TAG, "clearLoadingMoreFlagIfCapable:");
            }
        }
    }

    // Callback Filter
    private class FilterCallback implements Callback<PostCarCollectionDao> {
        int mode = 0; // 0 = load , 1 = loadMore

        public FilterCallback(int mode) {
            this.mode = mode;
        }

        @Override
        public void onResponse(Call<PostCarCollectionDao> call, Response<PostCarCollectionDao> response) {
            mAdapter.setLoading(false);
            if (response.isSuccessful()) {
                if (mode == 0) {
                    if (response.body() != null && response.body().getListCar() != null)
                    mPostManager.setDao(response.body());
                }else if (mode == 1){
                    mPostManager.appendDataToBottomPosition(response.body());
                    if (response.body().getListCar() == null){
                        isStopLoadingMore = true;
                    }
                }
                mAdapter.setDao(mPostManager.getDao());
                mAdapter.notifyDataSetChanged();
                mListView.smoothScrollToPosition(0);
            } else {
                mAdapter.setLoading(false);
                isStopLoadingMore = true;
                Log.e(TAG, "onResponse: " + response.errorBody().toString());
            }
        }

        @Override
        public void onFailure(Call<PostCarCollectionDao> call, Throwable t) {
            Log.e(TAG, "onFailure: ", t);
            isStopLoadingMore = true;
            mAdapter.setLoading(false);
        }
    }
}