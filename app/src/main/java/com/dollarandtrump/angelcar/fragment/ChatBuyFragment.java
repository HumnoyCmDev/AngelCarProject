package com.dollarandtrump.angelcar.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.dollarandtrump.angelcar.Adapter.MessageAdapter;
import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.dao.CarIdDao;
import com.dollarandtrump.angelcar.dao.MessageAdminCollectionDao;
import com.dollarandtrump.angelcar.dao.MessageCollectionDao;
import com.dollarandtrump.angelcar.dao.MessageDao;
import com.dollarandtrump.angelcar.dao.PostCarCollectionDao;
import com.dollarandtrump.angelcar.interfaces.OnClickItemMessageListener;
import com.dollarandtrump.angelcar.manager.MessageManager;
import com.dollarandtrump.angelcar.manager.Registration;
import com.dollarandtrump.angelcar.manager.bus.BusProvider;
import com.dollarandtrump.angelcar.manager.http.HttpManager;
import com.squareup.otto.Subscribe;

import org.parceler.Parcels;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by humnoy on 26/1/59.
 */
public class ChatBuyFragment extends Fragment {
//    public static final String ARGS_MESSAGE_BY = "shop";

    @Bind(R.id.list_view) ListView listView;

    MessageAdapter adapter;
    MessageManager messageManager;

    private static final String TAG = "ChatBuyFragment";


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
        messageManager = new MessageManager();
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        ButterKnife.bind(this, rootView);
        adapter = new MessageAdapter();
        if (messageManager.getMessageDao() !=null)
            adapter.setDao(messageManager.getMessageDao().getListMessage());
        listView.setAdapter(adapter);
//        loadMessage();
        initListener();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("buyMsgDao", Parcels.wrap(messageManager.getMessageDao()));
    }


    @SuppressWarnings("UnusedParameters")
    private void onRestoreInstanceState(Bundle savedInstanceState) {
        MessageCollectionDao messageDao =
                Parcels.unwrap(savedInstanceState.getParcelable("buyMsgDao"));
        messageManager.setMessageDao(messageDao);
    }

//    private void loadMessage() {
//        Call<CarIdDao> loadCarId =
//                HttpManager.getInstance().getService().loadCarId(
//                        Registration.getInstance().getShopRef());
//        loadCarId.enqueue(carIdDaoCallback);
//    }

    private void initListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MessageDao messageDao =
                        messageManager.getMessageDao().getListMessage().get(position);
                OnClickItemMessageListener onClickItemMessageListener =
                        (OnClickItemMessageListener) getActivity();
                onClickItemMessageListener.onClickItemMessage(messageDao);
            }
        });
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
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Subscribe
    public void subScribeMessage(MessageManager msgManager){
        Log.i(TAG, "subScribeMessage: Buy");
        messageManager.setMessageDao(msgManager.getMessageSellDao());
        adapter.setDao(messageManager.getMessageDao().getListMessage());
        adapter.notifyDataSetChanged();
    }

    /**************
    *Listener Zone*
    ***************/
//    Callback<CarIdDao> carIdDaoCallback = new Callback<CarIdDao>() {
//        @Override
//        public void onResponse(Call<CarIdDao> call, Response<CarIdDao> response) {
//            if (response.isSuccessful()) {
//                Call<MessageAdminCollectionDao> callMessageAdmin =
//                        HttpManager.getInstance().getService()
//                                .messageAdmin(response.body().getAllCarId());
//                callMessageAdmin.enqueue(adminCollectionDaoCallback);
//            }else {
//                try {
//                    Log.i(TAG, "onResponse: "+response.errorBody().string());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        @Override
//        public void onFailure(Call<CarIdDao> call, Throwable t) {
//            Log.e(TAG, "onFailure: ", t);
//        }
//    };

//    Callback<MessageAdminCollectionDao> adminCollectionDaoCallback = new Callback<MessageAdminCollectionDao>() {
//        @Override
//        public void onResponse(Call<MessageAdminCollectionDao> call, Response<MessageAdminCollectionDao> response) {
//            if (response.isSuccessful()) {
//                messageManager.setMessageDao(response.body()
//                        .getMessageAdminDao()
//                        .convertToMessageCollectionDao());
//                adapter.setDao(messageManager.getMessageDao().getListMessage());
//                adapter.notifyDataSetChanged();
//
//            } else {
//                try {
//                    Log.i(TAG, "onResponse: error" + response.errorBody().string());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        @Override
//        public void onFailure(Call<MessageAdminCollectionDao> call, Throwable t) {
//            Log.e(TAG, "onFailure: ", t);
//        }
//    };

    /******************
     *Inner Class Zone*
     ******************/

}
