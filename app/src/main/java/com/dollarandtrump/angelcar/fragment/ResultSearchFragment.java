package com.dollarandtrump.angelcar.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.dollarandtrump.angelcar.Adapter.FeedPostCarAdapter;
import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.activity.ChatCarActivity;
import com.dollarandtrump.angelcar.activity.ViewCarActivity;
import com.dollarandtrump.angelcar.dao.PostCarCollectionDao;
import com.dollarandtrump.angelcar.dao.PostCarDao;
import com.dollarandtrump.angelcar.datatype.MutableInteger;
import com.dollarandtrump.angelcar.interfaces.InterNetInterface;
import com.dollarandtrump.angelcar.manager.PostCarManager;
import com.dollarandtrump.angelcar.manager.Registration;
import com.dollarandtrump.angelcar.manager.bus.MainThreadBus;
import com.dollarandtrump.angelcar.manager.http.HttpManager;
import com.dollarandtrump.angelcar.model.InfoCarModel;

import org.parceler.Parcels;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


@SuppressWarnings("unused")
public class ResultSearchFragment extends Fragment {
    private static String TAG = ResultSearchFragment.class.getSimpleName();

    @Bind(R.id.list_view) ListView mListView;
    @Bind(R.id.text_result_status) TextView mTextStatus;

    private FeedPostCarAdapter mAdapter;
    private MutableInteger mLastPositionInteger;
    private InfoCarModel mInfoCarModel;
    private PostCarManager mPostManager;
    private boolean isLoadingMore = false;
    private InfoCarModel mCopyInformLoadMore;

    public ResultSearchFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static ResultSearchFragment newInstance(InfoCarModel mInfoCarModel) {
        ResultSearchFragment fragment = new ResultSearchFragment();
        Bundle args = new Bundle();
        args.putParcelable("infocar", Parcels.wrap(mInfoCarModel));
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
        View rootView = inflater.inflate(R.layout.fragment_result_search, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        mInfoCarModel = Parcels.unwrap(getArguments().getParcelable("infocar"));
        mPostManager = new PostCarManager();
        mLastPositionInteger = new MutableInteger(-1);
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        ButterKnife.bind(this,rootView);

        mAdapter = new FeedPostCarAdapter(getContext(),mLastPositionInteger);
        mAdapter.setDao(mPostManager.getDao());
        mListView.setAdapter(mAdapter);
        mListView.setOnScrollListener(onScrollListener);
        mListView.setOnItemClickListener(onItemClickListener);

        loadFilter();

    }

    private void loadFilter(){
//        mCopyInformLoadMore = mInfoCarModel;
        Call<PostCarCollectionDao> callFilter =
                HttpManager.getInstance().getService()
                        .loadFilterFeed(mInfoCarModel.getMapFilter());
        callFilter.enqueue(new FilterCallback(FilterCallback.FILTER_LOAD_NEWER));

        Log.d(TAG, "loadFilter: "+mInfoCarModel.getMapFilter());
    }

    private void loadMoreFilter(){
       if (isLoadingMore) return;
        isLoadingMore = true;
        mAdapter.setLoading(true);
        if (mCopyInformLoadMore == null) return;
        // get date
        mCopyInformLoadMore.setDateMore(mPostManager.lastDateDao());
        Call<PostCarCollectionDao> callPost = HttpManager.getInstance()
                .getService().loadFilterFeed(mCopyInformLoadMore.getMapFilter());
        callPost.enqueue(new FilterCallback(FilterCallback.FILTER_LOAD_MORE));
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
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    /*
         * Save Instance State Here
         */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save Instance State here
    }

    /*
     * Restore Instance State Here
     */
    @SuppressWarnings("UnusedParameters")
    private void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore Instance State here
    }

    /*Listener*/
    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (((InterNetInterface) getActivity()).isConnectInternet()) {
                if (mPostManager.getDao().getListCar().size() > position) {
                    final PostCarDao item = mPostManager.getDao().getListCar().get(position);
                    final boolean isShop = item.getShopRef().contains(Registration.getInstance().getShopRef());
                    Intent intent;
                    if (isShop) {
                        intent = new Intent(getActivity(), ViewCarActivity.class);
                        intent.putExtra("is_shop", true);
                        intent.putExtra("dao", Parcels.wrap(item));
                    } else {
                        intent = new Intent(getActivity(), ChatCarActivity.class);
                        intent.putExtra("PostCarDao", Parcels.wrap(item));
                        intent.putExtra("intentForm", 0);
                        intent.putExtra("messageFromUser", Registration.getInstance().getUserId());
                    }
                    startActivity(intent);
                }
            }
        }
    };

    AbsListView.OnScrollListener onScrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }
        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (view == mListView) {
                if (firstVisibleItem + visibleItemCount >= totalItemCount) {
                    if (mPostManager.getCount() > 0) {
                        if (!isLoadingMore) {
                            loadMoreFilter();
                            Log.d(TAG, "load more");
                        }
                    }
                }
            }
        }
    };

    // Callback Filter
    private class FilterCallback implements Callback<PostCarCollectionDao> {
        static final int FILTER_LOAD_NEWER = 0;
        static final int FILTER_LOAD_MORE = 1;

        int mode;

        FilterCallback(int mode) {
            this.mode = mode;
        }

        @Override
        public void onResponse(Call<PostCarCollectionDao> call, Response<PostCarCollectionDao> response) {
            mAdapter.setLoading(false);
            mAdapter.notifyDataSetChanged();
            if (response.isSuccessful()) {
                if (mode == 0) {
                    if (response.body() != null && response.body().getListCar() != null && response.body().getListCar().size() > 0) {
                        mPostManager.setDao(response.body());
                    }else {
                        mTextStatus.setText("ไม่พบข้อมูล");
                        mTextStatus.setVisibility(View.VISIBLE);
                    }
                }else if (mode == 1){
                    mPostManager.appendDataToBottomPosition(response.body());
                }
                mAdapter.setDao(mPostManager.getDao());
                mListView.smoothScrollToPosition(0);
                Log.d(TAG,"Success");
            } else {
                mAdapter.setLoading(false);
                mAdapter.notifyDataSetChanged();
                Log.e(TAG, "onResponse: " + response.errorBody().toString());
            }
        }

        @Override
        public void onFailure(Call<PostCarCollectionDao> call, Throwable t) {
            Log.e(TAG, "onFailure: ", t);
            mAdapter.setLoading(false);
        }
    }
}
