package com.dollarandtrump.angelcar.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dollarandtrump.angelcar.Adapter.TopicAdapter;
import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.dao.TopicCollectionDao;
import com.dollarandtrump.angelcar.dao.TopicDao;
import com.dollarandtrump.angelcar.manager.Registration;
import com.dollarandtrump.angelcar.manager.bus.MainThreadBus;
import com.dollarandtrump.angelcar.manager.http.HttpManager;
import com.dollarandtrump.angelcar.utils.Log;

import org.parceler.Parcels;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


@SuppressWarnings("unused")
public class TopicFragment extends Fragment {

   public enum Type {
        FINANCE{
            @Override
            public String toString() {
                return "finance";
            }
        },REFINANCE {
            @Override
            public String toString() {
                return "refinance";
            }
        },PAWN {
            @Override
            public String toString() {
                return "pawn";
            }
        }
    }

    @Bind(R.id.recycler_list_chat) RecyclerView mListChat;
    @Bind(R.id.refresh) SwipeRefreshLayout mRefresh;

    private TopicAdapter mAdapter;
    private Type mType;
    private Subscription mSubscription;
    private TopicCollectionDao mTopicCollectionDao;
    public TopicFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static TopicFragment newInstance(Type type) {
        TopicFragment fragment = new TopicFragment();
        Bundle args = new Bundle();
        args.putInt("type",type.ordinal());
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
        View rootView = inflater.inflate(R.layout.fragment_finance, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
       mType = Type.values()[getArguments().getInt("type")];
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        ButterKnife.bind(this,rootView);
        mListChat.setLayoutManager(new LinearLayoutManager(getContext()));

        mRefresh.setColorSchemeColors(
                Color.parseColor("#104F94"),
                Color.parseColor("#104F94"),
                Color.parseColor("#FFC11E"),
                Color.parseColor("#FFC11E"));

        final String user = Registration.getInstance().getUserId();
        if (user != null) {
            mAdapter = new TopicAdapter(getContext(), user);
            mListChat.setAdapter(mAdapter);
            mAdapter.setTopicDao(mTopicCollectionDao);
            mAdapter.setOnClickItemChatListener(new TopicAdapter.OnClickItemChatListener() {
                @Override
                public void onSelectItem(TopicDao topicDao, int position) {
                    Log.d("click"+position);
                    MainThreadBus.getInstance().post(topicDao);
                }
            });

            mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    // load newer
                    loadFeed(user);
                }
            });

            loadFeed(user);
        }

    }

    void loadFeed(String user){
        Log.d(user + "||" + mType.toString());
        mSubscription = HttpManager.getInstance().getService()
                .observableFeedTopic(user + "||" + mType.toString())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<TopicCollectionDao>() {
                    @Override
                    public void onCompleted() {
                        if (Log.isLoggable(Log.DEBUG)) Log.d("onCompleted");
                        mRefresh.setRefreshing(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mRefresh.setRefreshing(false);
                        if (Log.isLoggable(Log.ERROR)) Log.e("error finance",e);
                    }
                    @Override
                    public void onNext(TopicCollectionDao topicCollectionDao) {
                        mRefresh.setRefreshing(false);
                        mTopicCollectionDao = topicCollectionDao;
                        mAdapter.setTopicDao(topicCollectionDao);
                        mAdapter.notifyDataSetChanged();
                    }
                });
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
    public void onDestroy() {
        super.onDestroy();
        if (mSubscription != null)
            mSubscription.unsubscribe();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("topic", Parcels.wrap(mTopicCollectionDao));
    }


    @SuppressWarnings("UnusedParameters")
    private void onRestoreInstanceState(Bundle savedInstanceState) {
        mTopicCollectionDao = Parcels.unwrap(savedInstanceState.getParcelable("topic"));
    }

}
