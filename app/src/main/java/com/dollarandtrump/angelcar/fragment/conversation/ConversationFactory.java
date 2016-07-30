package com.dollarandtrump.angelcar.fragment.conversation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.dollarandtrump.angelcar.Adapter.ConversationAdapter;
import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.dao.MessageCollectionDao;
import com.dollarandtrump.angelcar.dao.MessageDao;
import com.dollarandtrump.angelcar.interfaces.OnClickItemMessageListener;
import com.dollarandtrump.angelcar.manager.MessageManager;
import com.dollarandtrump.angelcar.manager.bus.MainThreadBus;
import com.dollarandtrump.angelcar.utils.Log;

import org.parceler.Parcels;

import butterknife.Bind;
import butterknife.ButterKnife;


/***************************************
 * สร้างสรรค์ผลงานดีๆ
 * โดย Humnoy Android Developer
 * ลงวันที่ 11/16/2014. เวลา 11:42
 ***************************************/
@SuppressWarnings("unused")
public abstract class ConversationFactory extends Fragment {

    @Bind(R.id.list_view) ListView mListView;
    @Bind(R.id.stub_text_view) ViewStub mStubTextNoResult;

    private static final String TAG = "ChatAllFragment";
    private MessageManager mManager;
    private ConversationAdapter mAdapter;

    public ConversationFactory(){}

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
        View v = inflater.inflate(R.layout.list_view_layout,container,false);
        initInstances(v,savedInstanceState);
        return v;
    }

    private void init(Bundle savedInstanceState) {
        mManager = new MessageManager();
    }
    private void initInstances(View rootView, Bundle savedInstanceState) {
        ButterKnife.bind(this,rootView);
        mAdapter = new ConversationAdapter();
        if (mManager.getMessageDao() != null)
            mAdapter.setDao(mManager.getMessageDao().getListMessage());
        mListView.setAdapter(mAdapter);
        mListView.setOnItemLongClickListener(onItemLongClickListener);
        mListView.setOnItemClickListener(onItemClickListener);
    }

    public void onSubScribeMessage(MessageManager msgManager){
        mManager.setMessageDao(getMessageManager(msgManager));
        mManager.setProductIds(msgManager.getProductIds());
        if (mManager.getCount() > 0) {
            mAdapter.setDao(mManager.getMessageDao().getListMessage());
            mAdapter.setProduct(mManager.getProductIds());
            mAdapter.notifyDataSetChanged();
            mListView.setVisibility(View.VISIBLE);
            mStubTextNoResult.setVisibility(View.GONE);
        }else {
            mListView.setVisibility(View.GONE);
            mStubTextNoResult.setVisibility(View.VISIBLE);
        }
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("message", Parcels.wrap(mManager.getMessageDao()));
    }


    @SuppressWarnings("UnusedParameters")
    private void onRestoreInstanceState(Bundle savedInstanceState) {
        MessageCollectionDao messageDao =
                Parcels.unwrap(savedInstanceState.getParcelable("message"));
        mManager.setMessageDao(messageDao);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    /**************
     *Listener Zone*
     ***************/
    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            MessageDao dao = mManager.getMessageDao().getListMessage().get(position);
            OnClickItemMessageListener onClickItemMessageListener =
                    (OnClickItemMessageListener) getActivity();
            onClickItemMessageListener.onClickItemMessage(dao);

        }

    };

    AdapterView.OnItemLongClickListener onItemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            Toast.makeText(getContext(), "OnItem Long Click" + position, Toast.LENGTH_LONG).show();
            return true;
        }
    };


    /*****************
     *Inner Class Zone*
     ******************/
    abstract public MessageCollectionDao getMessageManager(MessageManager manager);
//    abstract public void subScribeMessage(MessageManager manager);
//    abstract public void onItemClickListener(MessageDao dao);

}
