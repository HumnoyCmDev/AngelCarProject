package com.dollarandtrump.angelcar.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.dollarandtrump.angelcar.Adapter.MessageAdapter;
import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.dao.CarIdDao;
import com.dollarandtrump.angelcar.dao.MessageAdminCollectionDao;
import com.dollarandtrump.angelcar.dao.MessageCollectionDao;
import com.dollarandtrump.angelcar.dao.MessageDao;
import com.dollarandtrump.angelcar.dao.PostCarCollectionDao;
import com.dollarandtrump.angelcar.dialog.DeleteChatDialog;
import com.dollarandtrump.angelcar.interfaces.OnClickItemMessageListener;
import com.dollarandtrump.angelcar.manager.MessageManager;
import com.dollarandtrump.angelcar.manager.Registration;
import com.dollarandtrump.angelcar.manager.bus.BusProvider;
import com.dollarandtrump.angelcar.manager.http.HttpManager;
import com.squareup.otto.Subscribe;

import org.parceler.Parcels;


import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by humnoy on 26/1/59.
 */
public class ChatAllFragment extends Fragment {

    @Bind(R.id.list_view) ListView listView;
    private static final String TAG = "ChatAllFragment";

    private MessageManager messageManager;
    private MessageAdapter adapter;

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
        messageManager = new MessageManager();
    }
    private void initInstances(View rootView, Bundle savedInstanceState) {
        ButterKnife.bind(this,rootView);

        adapter = new MessageAdapter();

        if (messageManager.getMessageDao() !=null)
            adapter.setDao(messageManager.getMessageDao().getListMessage());

        listView.setAdapter(adapter);
        listView.setOnItemLongClickListener(onItemLongClickListener);
        listView.setOnItemClickListener(onItemClickListener);

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("allMsgDao", Parcels.wrap(messageManager.getMessageDao()));

    }


    @SuppressWarnings("UnusedParameters")
    private void onRestoreInstanceState(Bundle savedInstanceState) {
        MessageCollectionDao messageDao =
                Parcels.unwrap(savedInstanceState.getParcelable("allMsgDao"));
        messageManager.setMessageDao(messageDao);
    }


    @Subscribe
    public void subScribeMessage(MessageManager msgManager){
        Log.i(TAG, "subScribeMessage: All");
        messageManager.setMessageDao(msgManager.getMessageDao());
        adapter.setDao(messageManager.getMessageDao().getListMessage());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        super.onStart();
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void deleteDialog(MessageDao dao) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment fragment = getFragmentManager().findFragmentByTag("DeleteChatDialog");
        if (fragment != null){
            ft.remove(fragment);
        }
        ft.addToBackStack(null);
        DeleteChatDialog deleteChatDialog =
                DeleteChatDialog.newInstance(dao.getMessageFromUser());
        deleteChatDialog.show(getFragmentManager(),"DeleteChatDialog");
    }

    /**************
     *Listener Zone*
     ***************/
    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            MessageDao dao = messageManager.getMessageDao().getListMessage().get(position);
            OnClickItemMessageListener onClickItemMessageListener =
                    (OnClickItemMessageListener) getActivity();
            onClickItemMessageListener.onClickItemMessage(dao);

        }

    };

    AdapterView.OnItemLongClickListener onItemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            Toast.makeText(getContext(), "OnItem Long Click" + position, Toast.LENGTH_LONG).show();
//            MessageDao dao = messageManager.getMessageDao().getListMessage().get(position);
//            deleteDialog(dao);
            return true;
        }
    };


    /*****************
     *Inner Class Zone*
     ******************/


}
