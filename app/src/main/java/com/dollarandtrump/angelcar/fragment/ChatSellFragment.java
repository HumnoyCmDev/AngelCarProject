package com.dollarandtrump.angelcar.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.ListView;

import com.dollarandtrump.angelcar.Adapter.ConversationAdapter;
import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.dao.MessageCollectionDao;
import com.dollarandtrump.angelcar.dao.MessageDao;
import com.dollarandtrump.angelcar.interfaces.OnClickItemMessageListener;
import com.dollarandtrump.angelcar.manager.MessageManager;
import com.dollarandtrump.angelcar.manager.bus.MainThreadBus;
import com.squareup.otto.Subscribe;

import org.parceler.Parcels;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by humnoy on 26/1/59.
 */
public class ChatSellFragment extends Fragment {
//    public static final String ARGS_MESSAGE_BY = "user";
    @Bind(R.id.list_view) ListView mListView;
    @Bind(R.id.stub_text_view)
    ViewStub mStubTextNoResult;

    ConversationAdapter adapter;
//    MessageCollectionDao dao;
    MessageManager messageManager;


    private static final String TAG = "ChatSellFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(savedInstanceState);

        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.list_view_layout, container, false);
        initInstances(v,savedInstanceState);
        return v;
    }

    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
//        dao = new MessageCollectionDao();
        messageManager = new MessageManager();
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here
        ButterKnife.bind(this, rootView);
        adapter = new ConversationAdapter();
        if (messageManager.getMessageDao() !=null)
            adapter.setDao(messageManager.getMessageDao().getListMessage());
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(onItemClickListener);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save Instance State here
        outState.putParcelable("sellMsgDao", Parcels.wrap(messageManager.getMessageDao()));
    }

    @SuppressWarnings("UnusedParameters")
    private void onRestoreInstanceState(Bundle savedInstanceState) {
        MessageCollectionDao messageDao =
                Parcels.unwrap(savedInstanceState.getParcelable("sellMsgDao"));
        messageManager.setMessageDao(messageDao);
    }

    @Override
    public void onStart() {
        super.onStart();
        MainThreadBus.getInstance().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        MainThreadBus.getInstance().unregister(this);
    }

    @Subscribe
    public void subScribeMessage(MessageManager msgManager){
        Log.i(TAG, "subScribeMessage: Sell"+msgManager.getMessageBuyDao().getListMessage().size());
        messageManager.setMessageDao(msgManager.getMessageBuyDao());
        if (messageManager.getCount()>0) {
            adapter.setDao(messageManager.getMessageDao().getListMessage());
            adapter.notifyDataSetChanged();
            mListView.setVisibility(View.VISIBLE);
            mStubTextNoResult.setVisibility(View.GONE);
        }else {
            mListView.setVisibility(View.GONE);
            mStubTextNoResult.inflate();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    /***************
     *Listener Zone*
     ***************/
    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            MessageDao messageDao = messageManager.getMessageDao().getListMessage().get(position);
            OnClickItemMessageListener onClickItemMessageListener =
                    (OnClickItemMessageListener) getActivity();
            onClickItemMessageListener.onClickItemMessage(messageDao);
        }
    };

    /*****************
    *Inner Class Zone*
    ******************/

}
