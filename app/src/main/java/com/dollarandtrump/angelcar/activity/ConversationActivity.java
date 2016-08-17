package com.dollarandtrump.angelcar.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Update;
import com.dollarandtrump.angelcar.MainApplication;
import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.dao.CarIdDao;
import com.dollarandtrump.angelcar.dao.MessageAdminCollectionDao;
import com.dollarandtrump.angelcar.dao.MessageCollectionDao;
import com.dollarandtrump.angelcar.dao.MessageDao;
import com.dollarandtrump.angelcar.dao.PostCarCollectionDao;
import com.dollarandtrump.angelcar.dao.PostCarDao;
import com.dollarandtrump.angelcar.dao.TopicDao;
import com.dollarandtrump.angelcar.fragment.conversation.ConversationTopicFragment;
import com.dollarandtrump.angelcar.fragment.conversation.ConversationBuyFragment;
import com.dollarandtrump.angelcar.fragment.conversation.ConversationSellFragment;
import com.dollarandtrump.angelcar.interfaces.OnClickItemMessageListener;
import com.dollarandtrump.angelcar.manager.MessageManager;
import com.dollarandtrump.angelcar.manager.Registration;
import com.dollarandtrump.angelcar.manager.bus.MainThreadBus;
import com.dollarandtrump.angelcar.manager.http.HttpManager;
import com.dollarandtrump.angelcar.model.ConversationCache;
import com.dollarandtrump.angelcar.utils.RxNotification;
import com.flyco.tablayout.SlidingTabLayout;
import com.google.firebase.messaging.RemoteMessage;
import com.squareup.otto.Produce;
import com.squareup.otto.Subscribe;

import org.parceler.Parcels;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func3;
import rx.schedulers.Schedulers;

public class ConversationActivity extends AppCompatActivity implements OnClickItemMessageListener {
    @Bind(R.id.viewpager) ViewPager viewPager;
    @Bind(R.id.tl_8) SlidingTabLayout slidingTabLayout;
    private Subscription subscriptionMessage;
    private Subscription subscriptionCarId;
    private MessageManager mgsManager;
    @Inject
    SharedPreferences sharedPreferences;
    private final String[] mTitles = {"คุยกับเจ้าหน้าที่", "คุยกับคนซื้อ", "คุยกับคนขาย"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_chat_all);
        ButterKnife.bind(this);
        initViewPager();
        ((MainApplication) getApplication()).getStoreComponent().inject(this);

//        if (savedInstanceState == null) {
//            loadAllCarId();
//        }

        /*RxNotification.with(getBaseContext())
                .isNotification(false);*/


        Bundle intentNotification = getIntent().getExtras();
        if(intentNotification != null) {
            String carId = intentNotification.getString("carid");
            String messageFromUser = intentNotification.getString("roomid");
            findPostCar(carId,messageFromUser);
        }



    }

    private void loadAllCarId() {
        //Rx android
        subscriptionCarId = HttpManager.getInstance().getService()
                .observableLoadCarId(Registration.getInstance().getShopRef())
                .subscribeOn(Schedulers.newThread())
       .subscribe(new Action1<CarIdDao>() {
           @Override
           public void call(CarIdDao carIdDao) {
               loadAllMessage(carIdDao);
           }
       });

    }

    private void loadAllMessage(final CarIdDao carIdDao) {
        Log.d("view", "loadAllMessage: "+carIdDao.getAllCarId());
        //topic
        Observable<MessageAdminCollectionDao> rxCallLoadTopic =
                HttpManager.getInstance().getService().observableConversationTopic(carIdDao.getTopicId());

        Observable<MessageAdminCollectionDao> rxCallLoadMessageSell =
                HttpManager.getInstance().getService()
                        .observableMessageAdmin(carIdDao.getAllCarId());

        Observable<MessageCollectionDao> rxCallLoadMessageBuy = HttpManager.getInstance()
                .getService().observableMessageClient(Registration.getInstance().getUserId());

        Observable<MessageManager> observableManager =
                Observable.zip(rxCallLoadMessageSell, rxCallLoadMessageBuy, rxCallLoadTopic, new Func3<MessageAdminCollectionDao,
                        MessageCollectionDao, MessageAdminCollectionDao, MessageManager>() {
                    @Override
                    public MessageManager call(MessageAdminCollectionDao messageAdminCollectionDao, MessageCollectionDao dao, MessageAdminCollectionDao messageAdminCollectionDao2) {
                        MessageManager manager = new MessageManager();
                        manager.setConversationSell(messageAdminCollectionDao.convertToMessageCollectionDao());
                        manager.setConversationBuy(dao);
                        manager.setConversationTopic(messageAdminCollectionDao2.convertToMessageCollectionDao());
                        manager.setProductIds(carIdDao);

                        insertConversation(messageAdminCollectionDao, dao, messageAdminCollectionDao2);

                        return manager;
                    }
                });

        subscriptionMessage = observableManager
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<MessageManager>() {
                    @Override
                    public void onCompleted() {
                        Log.d("load conversation", "onCompleted: ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(MessageManager messageManager) {
                        Log.d("load conversation", "next");
                         mgsManager = messageManager;
                        MainThreadBus.getInstance().post(produceMsgManager());
                    }
                });
    }

    private void insertConversation(MessageAdminCollectionDao messageAdminCollectionDao, MessageCollectionDao dao, MessageAdminCollectionDao messageAdminCollectionDao2) {
        new Delete().from(ConversationCache.class).execute();
        new Delete().from(MessageDao.class).execute();

        MessageManager managerSqlite = new MessageManager();
        managerSqlite.setMessageDao(messageAdminCollectionDao2.convertToMessageCollectionDao());
        managerSqlite.appendDataToBottomPosition(messageAdminCollectionDao.convertToMessageCollectionDao());
        managerSqlite.appendDataToBottomPosition(dao);

        //test insert to db
        ActiveAndroid.beginTransaction();
        try {
            for( MessageDao m :managerSqlite.getMessageDao().getListMessage()){

//                new Update(MessageDao.class).set()


                m.save();
                new ConversationCache(m.getMessageCarId(),m,m.getMessageFromUser()).save();
            }
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
    }

    @Subscribe
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String type = remoteMessage.getData().get("type");
        if (type.equals("chatfinance") || type.equals("chatrefinance") || type.equals("chatpawn") || type.equals("chatcar")){
            loadAllCarId();
        }
    }

    @Produce
    public MessageManager produceMsgManager(){
        return mgsManager;
    }

    private void initViewPager() {
        TotalChatViewPagerAdapter pagerAdapter = new TotalChatViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        slidingTabLayout.setViewPager(viewPager,mTitles);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (subscriptionMessage != null)
            subscriptionMessage.unsubscribe();
        if (subscriptionCarId != null)
            subscriptionCarId.unsubscribe();
    }


    @Override
    protected void onResume() {
        super.onResume();
        MainThreadBus.getInstance().register(this);
//        if (getWindow().getDecorView() != null){
            loadAllCarId();

//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MainThreadBus.getInstance().unregister(this);
    }

    private void findPostCar(String carId, final String messageFromUser){
        Call<PostCarCollectionDao> call =
                HttpManager.getInstance().getService().loadCarModel(carId);
        call.enqueue(new Callback<PostCarCollectionDao>() {
            @Override
            public void onResponse(Call<PostCarCollectionDao> call, Response<PostCarCollectionDao> response) {
                if (response.isSuccessful()) {
                    if (response.body().getListCar() != null) {
                        PostCarDao item = response.body().getListCar().get(0);
                        Intent intent = new Intent(ConversationActivity.this, ChatCarActivity.class);
                        intent.putExtra("PostCarDao", Parcels.wrap(item));
                        intent.putExtra("intentForm", 1);
                        intent.putExtra("messageFromUser", messageFromUser);
                        startActivity(intent);
                    }else {
                        //TODO Check Null กรณีไม่มี รถอยู่หรือลบออกไปแล้ว
                        Log.i("ViewChat", "ไม่มีข้อมูลรถ หรือรถถูกลบไปแล้ว");
                    }
                } else {
                    Log.e("ViewChatAll", "onResponse: "+response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<PostCarCollectionDao> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }


    /**************
    *Listener Zone*
    ***************/

    @Override
    public void onClickItemMessage(final MessageDao messageDao) {
        if (!messageDao.isTopic()) {
            findPostCar(messageDao.getMessageCarId(), messageDao.getMessageFromUser());
        }else {
            TopicDao topic = new TopicDao();
            topic.setId(Integer.valueOf(messageDao.getMessageCarId()));
            topic.setUserId(messageDao.getMessageFromUser());
            Intent intent = new Intent(ConversationActivity.this, TopicChatActivity.class);
            intent.putExtra("topic",Parcels.wrap(topic));
            intent.putExtra("room","");
            startActivity(intent);
        }


//        Call<PostCarCollectionDao> call =
//            HttpManager.getInstance().getService().loadCarModel(messageDao.getMessageCarId());
//        call.enqueue(new Callback<PostCarCollectionDao>() {
//            @Override
//            public void onResponse(Call<PostCarCollectionDao> call, Response<PostCarCollectionDao> response) {
//                if (response.isSuccessful()) {
//                    if (response.body().getListCar() != null) {
//                        PostCarDao item = response.body().getListCar().get(0);
//                        Intent intent = new Intent(ConversationActivity.this, ChatCarActivity.class);
//                        intent.putExtra("PostCarDao", Parcels.wrap(item));
//                        intent.putExtra("intentForm", 1);
//                        intent.putExtra("messageFromUser", messageDao.getMessageFromUser());
//                        startActivity(intent);
//                    }else {
//                        //TODO Check Null กรณีไม่มี รถอยู่หรือลบออกไปแล้ว
//                        Log.i("ViewChat", "ไม่มีข้อมูลรถ หรือรถถูกลบไปแล้ว");
//                    }
//                } else {
//                    Log.e("ViewChatAll", "onResponse: "+response.errorBody().toString());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<PostCarCollectionDao> call, Throwable t) {
//                   t.printStackTrace();
//            }
//        });

    }

    /************
    *Inner Class*
    *************/
    public class TotalChatViewPagerAdapter extends FragmentPagerAdapter {
        public TotalChatViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0: return new ConversationTopicFragment();
                case 1: return new ConversationBuyFragment();
                case 2: return new ConversationSellFragment();
            }
            return null;
        }
        @Override
        public int getCount() {
            return 3;
        }
    }

}
