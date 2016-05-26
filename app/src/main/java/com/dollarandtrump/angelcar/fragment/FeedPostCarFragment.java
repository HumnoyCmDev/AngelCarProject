package com.dollarandtrump.angelcar.fragment;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.dollarandtrump.angelcar.Adapter.FeedPostCarAdapter;
import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.activity.DetailCarActivity;
import com.dollarandtrump.angelcar.activity.EditPostActivity;
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
import com.dollarandtrump.angelcar.manager.bus.BusProvider;
import com.dollarandtrump.angelcar.manager.http.HttpManager;
import com.dollarandtrump.angelcar.model.InformationCarModel;

import org.parceler.Parcels;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FeedPostCarFragment extends Fragment {

//    public static final String SAVE_STATE_GAO = "SAVE_STATE_GAO";
    public static final int REQUEST_CODE_BRAND = 1;
    public static final int REQUEST_CODE_SUB = 2;
    public static final int REQUEST_CODE_SUB_DETAIL = 3;
    public static final int REQUEST_CODE_YEAR = 4;

//    public static final String ARG_DRAWABLE_LOGO = "logo";
    public static final String ARG_BRAND = "brand";
    public static final String ARG_SUB = "sub";
    public static final String ARG_SUB_DETAIL = "subDetail";


    @Bind(R.id.swipe_container) SwipeRefreshLayout mSwipeRefresh;
//    @Bind(R.id.countCarMonth) TextView countCarMonth;
    @Bind(R.id.countCarAll) TextView countCarAll;
//    @Bind(R.id.countCarDay) TextView countCarDay;
    @Bind(R.id.list_view) ListView listView;
    @Bind(R.id.ctFilter) ScrollView ctFilter;
    @Bind(R.id.btFilter) ImageView btFilter;
    @Bind(R.id.tvNewPost) TextView tvNewPost;

    //Filter
    @Bind(R.id.filterSubDetail) TextView tvSubDetail;
    @Bind(R.id.filterBrand) TextView tvBrand;
    @Bind(R.id.filterYear) TextView tvYear;
    @Bind(R.id.filterSub) TextView tvSub;
    @Bind(R.id.etPriceStart) EditText etPriceStart;
    @Bind(R.id.etPriceEnd) EditText etPriceEnd;

    private static final String TAG = "FeedPostCarFragment";
    private InformationCarModel informationCarModel;
    private InformationCarModel copyInformLoadMore;
    private MutableInteger lastPositionInteger;
    private boolean isLoadingMore = false;
    private boolean isStopLoadingMore = false; // หยุดการ LoadMore
    private PostCarManager carManager;
    private FeedPostCarAdapter adapter;

    private boolean isFilter = false;

    public FeedPostCarFragment() {
        super();
    }

    public static FeedPostCarFragment newInstance() {
//        Bundle args = new Bundle();
//        fragment.setArguments(args);
        return new FeedPostCarFragment();
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
        informationCarModel = new InformationCarModel();
        carManager = new PostCarManager();
        lastPositionInteger = new MutableInteger(-1);
//        dao = new PostCarCollectionDao();
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here
        ButterKnife.bind(this,rootView);

        adapter = new FeedPostCarAdapter(lastPositionInteger);
        adapter.setDao(carManager.getDao());
        listView.setAdapter(adapter);
        mSwipeRefresh.setColorSchemeColors(
                Color.parseColor("#104F94"),
                Color.parseColor("#104F94"),
                Color.parseColor("#FFC11E"),
                Color.parseColor("#FFC11E"));

        mSwipeRefresh.setOnRefreshListener(pullRefresh);
        listView.setOnItemClickListener(onItemClickListViewListener);
        listView.setOnItemLongClickListener(onItemLongClickListener);
        listView.setOnScrollListener(onScrollListener);

//        btFilter.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (ctFilter.getVisibility() == View.GONE) {
//                    ResizeHeight resizeHeight = new ResizeHeight(ctFilter,1000,true);
//                    resizeHeight.setInterpolator(new BounceInterpolator());
//                    resizeHeight.setDuration(1000);
//                    ctFilter.startAnimation(resizeHeight);
//                    ctFilter.setVisibility(View.VISIBLE);
//                } else {
//                    ResizeHeight resizeHeight = new ResizeHeight(ctFilter,0,false);
//                    resizeHeight.setDuration(500);
//                    ctFilter.startAnimation(resizeHeight);
//                }
//
//            }
//        });

//        etPriceStart.addTextChangedListener(new TextWatcherListener(etPriceStart));
//        etPriceEnd.addTextChangedListener(new TextWatcherListener(etPriceEnd));

        if (savedInstanceState == null) {
            refreshData();
        }
    }


    private void refreshData(){
        isStopLoadingMore = false;
        if (carManager.getCount() == 0)
            reloadData();
        else
            reloadDataNewer();
    }

    private void reloadDataNewer() {
        loadCountCar();
        // load post newer
        Log.i(TAG, "reloadDataNewer: "+carManager.firstDateDao());
        Call<PostCarCollectionDao> call =
                HttpManager.getInstance().getService().loadNewerPostCar(carManager.firstDateDao());
        call.enqueue(new PostCarCallback(PostCarCallback.MODE_RELOAD_NEWER));
    }

    private void loadCountCar() {
        // load count car
        Call<CountCarCollectionDao> callLoadCountCar =
                HttpManager.getInstance().getService().loadCountCar();
        callLoadCountCar.enqueue(countCarCallback);
    }

    private void reloadData() {
        loadCountCar();
        //load Post
        Call<PostCarCollectionDao> call =
                HttpManager.getInstance().getService().loadPostCar();
        call.enqueue(new PostCarCallback(PostCarCallback.MODE_RELOAD));
    }

    private void loadMoreData(){
//        if (isStopLoadingMore) return;
        if (isStopLoadingMore || isLoadingMore) return;

        isLoadingMore = true;
        adapter.setLoading(true);
        Log.i(TAG, "LoadMore1 : "+ isStopLoadingMore +" load = "+isLoadingMore);
        Call<PostCarCollectionDao> callLoadingMore = HttpManager.getInstance()
                .getService().loadMorePostCar(carManager.lastDateDao());
        callLoadingMore.enqueue(new PostCarCallback(PostCarCallback.MODE_LOAD_MORE));

    }

    private void loadMoreFilterData(){
//        if (isStopLoadingMore) return;
        if (isStopLoadingMore || isLoadingMore) return;

        isLoadingMore = true;
        adapter.setLoading(true);
        if (copyInformLoadMore == null) return;
        // get date
        copyInformLoadMore.setDateMore(carManager.lastDateDao());
        Call<PostCarCollectionDao> call = HttpManager.getInstance()
                .getService().loadFilterFeed(copyInformLoadMore.getMapFilter());
        call.enqueue(new FilterCallback(1));
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        outState.putParcelable(SAVE_STATE_GAO,Parcels.wrap(dao));
          carManager.onSaveInstanceState();
          outState.putBundle("lastPositionInteger",
                lastPositionInteger.onSaveInstanceState());
    }

    @SuppressWarnings("UnusedParameters")
    private void onRestoreInstanceState(Bundle savedInstanceState) {
//        dao = Parcels.unwrap(savedInstanceState.getParcelable(SAVE_STATE_GAO));
          carManager.onRestoreInstanceState(savedInstanceState);
          lastPositionInteger.onRestoreInstanceState(
                savedInstanceState.getBundle("lastPositionInteger"));
//        adapter.setDao(dao);
//        adapter.notifyDataSetChanged();
    }

    @OnClick(R.id.btFilter)
    public void ShowHideFilter(){
        if (ctFilter.getVisibility() == View.GONE) {
            ResizeHeight resizeHeight = new ResizeHeight(ctFilter,1000,true);
            resizeHeight.setInterpolator(new OvershootInterpolator());//(new BounceInterpolator());
            resizeHeight.setDuration(1000);
            ctFilter.startAnimation(resizeHeight);
            ctFilter.setVisibility(View.VISIBLE);
        } else {
            ResizeHeight resizeHeight = new ResizeHeight(ctFilter,0,false);
            resizeHeight.setDuration(300);
            ctFilter.startAnimation(resizeHeight);
        }
    }

    @OnClick({R.id.filterBrand,R.id.filterSub,R.id.filterSubDetail,R.id.filterYear,R.id.buttonSearch})
    public void OnclickFilter(View v){
        @SuppressLint("CommitTransaction") FragmentTransaction ft = getFragmentManager().beginTransaction();
        switch (v.getId()){

            case R.id.buttonSearch:
                loadFilter();
                ShowHideFilter();
                tvBrand.setText("All");
                clearObject();

                break;

            case R.id.filterBrand:
                //Clear Object
                clearObject();
                Fragment fragment = getFragmentManager().findFragmentByTag("FilterBrandDialog");
                if (fragment != null){
                    ft.remove(fragment);
                }
                ft.addToBackStack(null);

                FilterBrandDialog dialog = new FilterBrandDialog();
                dialog.setTargetFragment(this,REQUEST_CODE_BRAND);
                dialog.show(getFragmentManager(),"FilterBrandDialog");
                break;

            case R.id.filterSub:
                if (isNullFilter(informationCarModel,0)) {
                    Fragment fragmentSub = getFragmentManager().findFragmentByTag("FilterSubDialog");
                    if (fragmentSub != null) {
                        ft.remove(fragmentSub);
                    }
                    ft.addToBackStack(null);
                    FilterSubDialog dialogSub = FilterSubDialog
                            .newInstance(informationCarModel);
                    dialogSub.setTargetFragment(this, REQUEST_CODE_SUB);
                    dialogSub.show(getFragmentManager(), "FilterSubDialog");
                }
                break;

            case R.id.filterSubDetail:
                if (isNullFilter(informationCarModel,1)){
                    Fragment fragmentSubDetail = getFragmentManager().findFragmentByTag("FilterSubDialog");
                    if (fragmentSubDetail != null) {
                        ft.remove(fragmentSubDetail);
                    }
                    ft.addToBackStack(null);
                    FilterSubDetailDialog dialogSub = FilterSubDetailDialog
                            .newInstance(informationCarModel);
                    dialogSub.setTargetFragment(this, REQUEST_CODE_SUB_DETAIL);
                    dialogSub.show(getFragmentManager(), "FilterSubDialog");
                }
                break;

            case R.id.filterYear:
                Fragment fragmentYear = getFragmentManager().findFragmentByTag("YearDialog");
                if (fragmentYear != null){
                    ft.remove(fragmentYear);
                }
                ft.addToBackStack(null);
                YearDialog dialogYear = new YearDialog();
//                dialogYear.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
                dialogYear.setTargetFragment(this,REQUEST_CODE_YEAR);
                dialogYear.show(getFragmentManager(),"YearDialog");
                break;
            default: // button Search
                break;
        }

    }

    private void loadFilter() {
        isFilter = true;
        if (informationCarModel != null) {
            /* **set Price */
            informationCarModel.setPriceStart(etPriceStart.getText().toString());
            informationCarModel.setPriceEnd(etPriceEnd.getText().toString());
            copyInformLoadMore = informationCarModel;
            Call<PostCarCollectionDao> callFilter =
                    HttpManager.getInstance().getService()
                            .loadFilterFeed(informationCarModel.getMapFilter());
            callFilter.enqueue(new FilterCallback(0));
        }
    }

    @OnClick({R.id.radioGearAll,R.id.radioGearAt,R.id.radioGearMt})
    void radioButtonGear(View view){
        boolean checked = ((RadioButton) view).isChecked();
        if (view.getId() == R.id.radioGearMt){
            informationCarModel.setGear(checked ? "1" : "0");
        }else if (view.getId() == R.id.radioGearAt){
            informationCarModel.setGear(checked ? "2" : "0");
        }else {// all
            informationCarModel.setGear("gear");
        }
        Log.i(TAG, "radioButtonGear: "+ informationCarModel.getGear());
    }

    private boolean isNullFilter(InformationCarModel informationCarModel, int mode){
        if (mode == 0){
            if (informationCarModel.getBrandDao() != null && !informationCarModel.getBrandDao().getBrandName().isEmpty())
            return true;
        }else {
            if (informationCarModel.getBrandDao() != null && informationCarModel.getSubDao() != null &&
                    !informationCarModel.getBrandDao().getBrandName().isEmpty() &&
                    !informationCarModel.getSubDao().getSubName().isEmpty())
            return true;
        }
        return false;
    }

    private void clearObject() {
        if (informationCarModel != null) {
            informationCarModel.clear();
            informationCarModel = null;
            informationCarModel = new InformationCarModel();
        }
    }

    private void showTextNewPost(){
        tvNewPost.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(
                Contextor.getInstance().getContext(),
                R.anim.zoom_face_in);
        tvNewPost.startAnimation(animation);
    }

    private void hideTextNewPost(){
        if (tvNewPost.getVisibility() == View.VISIBLE) {
            tvNewPost.setVisibility(View.GONE);
            Animation animation = AnimationUtils.loadAnimation(
                    Contextor.getInstance().getContext(),
                    R.anim.zoom_face_out);
            tvNewPost.startAnimation(animation);
        }
    }

    @OnClick(R.id.tvNewPost)
    public void showButtonNewPost(){
        listView.smoothScrollToPosition(0);
        hideTextNewPost();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {

                case REQUEST_CODE_BRAND: // brand
                    CarBrandDao model = Parcels.unwrap(data.getParcelableExtra(ARG_BRAND));
//                    int drawableLogo = data.getIntExtra(ARG_DRAWABLE_LOGO,R.drawable.toyota);
//                    typedArray.recycle();
                    informationCarModel.setBrandDao(model);
                    tvBrand.setText(model.getBrandName());
                    tvSub.setText("All");
                    tvSubDetail.setText("All");
                    break;

                case REQUEST_CODE_SUB: // sub
                    CarSubDao modelSub = Parcels.unwrap(data.getParcelableExtra(ARG_SUB));
                    informationCarModel.setSubDao(modelSub);
                    tvSub.setText(modelSub.getSubName());
                    tvSubDetail.setText("All");
                    break;

                case REQUEST_CODE_SUB_DETAIL: // sub detail
                    CarSubDao modelSubDetail = Parcels.unwrap(data.getParcelableExtra(ARG_SUB_DETAIL));
                    informationCarModel.setSubDetailDao(modelSubDetail);
                    tvSubDetail.setText(modelSubDetail.getSubName());
                    break;

                case REQUEST_CODE_YEAR: // year
                    int resultYear = data.getIntExtra("TAG_YEAR",2016);
                    informationCarModel.setYear(resultYear);
                    tvYear.setText(String.valueOf(resultYear));
                    break;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }



    /**************
    *Listener Zone*
    ***************/
    SwipeRefreshLayout.OnRefreshListener pullRefresh = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {

            if (isFilter) {
                isFilter = false;
                reloadData();
                return;
            }
            isStopLoadingMore = false;
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
            if (view == listView) {
                mSwipeRefresh.setEnabled(firstVisibleItem == 0);
                if (firstVisibleItem + visibleItemCount >= totalItemCount) {
                    if (carManager.getCount() > 0) {

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
                BusProvider.getInstance().post(new Scrolling(Scrolling.SCROLL_UP));
                control = true;
            }else if(firstVisibleItem > last && control){
                //scrollDown
                BusProvider.getInstance().post(new Scrolling(Scrolling.SCROLL_DOWN));
                control = false;

            }
            last = firstVisibleItem;

        }
    };

    AdapterView.OnItemLongClickListener onItemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            if (Registration.getInstance().getUserId() != null) {
                PostCarDao modelCar = carManager.getDao().getListCar().get(position);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment fragment = getFragmentManager().findFragmentByTag("DetailAlertDialog");
                if (fragment != null){
                    ft.remove(fragment);
                }
                ft.addToBackStack(null);
                String user = modelCar.getShopRef().contains(Registration.getInstance().getShopRef()) ? "shop" : "user";
                DetailAlertDialog dialog = DetailAlertDialog.newInstance(modelCar,user);
                dialog.setOnClickEditListener(onClickEditListener);
                dialog.show(getFragmentManager(),"DetailAlertDialog");
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
                if (carManager.getDao().getListCar().size() > position) {
                    PostCarDao item = carManager.getDao().getListCar().get(position);
                    Intent intent = new Intent(getActivity(), DetailCarActivity.class);
                    intent.putExtra("PostCarDao", Parcels.wrap(item));
                    intent.putExtra("intentForm", 0);
                    intent.putExtra("messageFromUser", Registration.getInstance().getUserId());
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

   /* Callback<PostCarCollectionDao> callbackLoadPostCar = new Callback<PostCarCollectionDao>() {
        @Override
        public void onResponse(Call<PostCarCollectionDao> call, Response<PostCarCollectionDao> response) {
            mSwipeRefresh.setRefreshing(false);
            if (response.isSuccessful()) {
                carManager.setDao(response.body());
                Log.i(TAG, "onResponse: AfterDate"+carManager.getAfterDate());
                Log.i(TAG, "onResponse: AfterDate"+carManager.getBeforeDate());
                adapter.setDao(carManager.getDao());
                adapter.notifyDataSetChanged();
//                adapter.getFilter().filter("toyota");
            } else {
                Toast.makeText(getActivity(),
                        response.errorBody().toString(),
                        Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Call<PostCarCollectionDao> call, Throwable t) {
            mSwipeRefresh.setRefreshing(false);
            Toast.makeText(getActivity(),
                    t.toString(),
                    Toast.LENGTH_SHORT).show();
        }
    };*/

    Callback<CountCarCollectionDao> countCarCallback = new Callback<CountCarCollectionDao>() {
        @Override
        public void onResponse(Call<CountCarCollectionDao> call, Response<CountCarCollectionDao> response) {
            if (response.isSuccessful()) {
                CountCarCollectionDao.CountCarGao countCarGao = response.body().getRows().get(0);
                String textAll = countCarGao.getCountAll() +"/"+
                        countCarGao.getCountMonth() +"/"+ countCarGao.getCountDay()+" คัน"; // "ทั้งหมด " + countCarGao.getCountAll() + " คัน";
//                String textMonth = "เดือนนี้ " + countCarGao.getCountMonth() + " คัน";
//                String textDay = "วันนี้ " + countCarGao.getCountDay() + " คัน";
                countCarAll.setText(textAll);
//                countCarMonth.setText(textMonth);
//                countCarDay.setText(textDay);

            } else {
                try {
                    Log.i(TAG, "onResponse: " + response.errorBody().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onFailure(Call<CountCarCollectionDao> call, Throwable t) {
            Log.e(TAG, "onFailure: ", t);
        }
    };


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

    private class PostCarCallback implements Callback<PostCarCollectionDao> {
        public static final int MODE_RELOAD = 1;
        public static final int MODE_RELOAD_NEWER = 2;
        public static final int MODE_LOAD_MORE = 3;
        int mode;

        public PostCarCallback(int mode) {
            this.mode = mode;
        }

        @Override
        public void onResponse(Call<PostCarCollectionDao> call, Response<PostCarCollectionDao> response) {
            if(mSwipeRefresh != null)
                mSwipeRefresh.setRefreshing(false);

            adapter.setLoading(false);

            if (response.isSuccessful()) {
                PostCarCollectionDao dao = response.body();

                int firstVisiblePosition = listView.getFirstVisiblePosition();
                View c = listView.getChildAt(0);
                int top = c == null ? 0 : c.getTop();

                if (mode == MODE_RELOAD_NEWER) {
                    carManager.insertDaoAtTopPosition(dao);
                    if (dao != null && dao.getListCar() != null) {
                        String textNewPost = "โพสใหม่+" + dao.getListCar().size();
                        tvNewPost.setText(textNewPost);
                    }
                } else if (mode == MODE_LOAD_MORE) {
                    carManager.appendDataToBottomPosition(dao);
                    if (dao.getListCar() == null){
                        isStopLoadingMore = true;
                    }

                } else {
                    carManager.setDao(dao);
                }
                clearLoadingMoreFlagIfCapable(mode);
                adapter.setDao(carManager.getDao());
                adapter.notifyDataSetChanged();

                if (mode == MODE_RELOAD_NEWER){
                    int additionalSize =
                            (dao != null && dao.getListCar()!= null)
                                    ? dao.getListCar().size() : 0;
                    adapter.increaseLastPosition(additionalSize);
                    listView.setSelectionFromTop(firstVisiblePosition+additionalSize,top);

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
            adapter.setLoading(false);
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
    private class FilterCallback implements Callback<PostCarCollectionDao>{
        int mode = 0; // 0 = load , 1 = loadMore
        public FilterCallback(int mode) {
            this.mode = mode;
        }
        @Override
        public void onResponse(Call<PostCarCollectionDao> call, Response<PostCarCollectionDao> response) {
            adapter.setLoading(false);
            if (response.isSuccessful()) {
                if (mode == 0) {
                    if (response.body() != null && response.body().getListCar() != null)
                    Log.i(TAG, "onResponse: size = "+response.body().getListCar().size());
                    carManager.setDao(response.body());
                }else if (mode == 1){
                    carManager.appendDataToBottomPosition(response.body());
                    if (response.body().getListCar() == null){
                        isStopLoadingMore = true;
                    }
                }
                adapter.setDao(carManager.getDao());
                adapter.notifyDataSetChanged();
            } else {
                adapter.setLoading(false);
                isStopLoadingMore = true;
                Log.e(TAG, "onResponse: " + response.errorBody().toString());
            }
        }

        @Override
        public void onFailure(Call<PostCarCollectionDao> call, Throwable t) {
            Log.e(TAG, "onFailure: ", t);
            isStopLoadingMore = true;
            adapter.setLoading(false);
        }
    }

    private class TextWatcherListener implements TextWatcher{
        EditText editText;

        public TextWatcherListener(EditText editText) {
            this.editText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            editText.removeTextChangedListener(this);
            try {
                String givenString = s.toString();
                if (givenString.contains(",")) {
                    givenString = givenString.replaceAll(",", "");
                }
//                double doubleValue = Double.parseDouble(givenString);

                if (editText.getId() == R.id.etPriceStart){
                    informationCarModel.setPriceStart(givenString);
                }else {
                    informationCarModel.setPriceEnd(givenString);
                }
            } catch (NumberFormatException e) {
            }
            editText.addTextChangedListener(this);
        }
    }
}