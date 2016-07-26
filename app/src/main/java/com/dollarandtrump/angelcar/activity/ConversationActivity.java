package com.dollarandtrump.angelcar.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.dao.CarIdDao;
import com.dollarandtrump.angelcar.dao.MessageAdminCollectionDao;
import com.dollarandtrump.angelcar.dao.MessageCollectionDao;
import com.dollarandtrump.angelcar.dao.MessageDao;
import com.dollarandtrump.angelcar.dao.PostCarCollectionDao;
import com.dollarandtrump.angelcar.dao.PostCarDao;
import com.dollarandtrump.angelcar.fragment.conversation.ConversationAllFragment;
import com.dollarandtrump.angelcar.fragment.conversation.ConversationBuyFragment;
import com.dollarandtrump.angelcar.fragment.conversation.ConversationSellFragment;
import com.dollarandtrump.angelcar.interfaces.OnClickItemMessageListener;
import com.dollarandtrump.angelcar.manager.MessageManager;
import com.dollarandtrump.angelcar.manager.Registration;
import com.dollarandtrump.angelcar.manager.bus.MainThreadBus;
import com.dollarandtrump.angelcar.manager.http.HttpManager;
import com.flyco.tablayout.SlidingTabLayout;
import com.google.firebase.messaging.RemoteMessage;
import com.squareup.otto.Produce;
import com.squareup.otto.Subscribe;

import org.parceler.Parcels;

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
import rx.functions.Func2;
import rx.schedulers.Schedulers;

public class ConversationActivity extends AppCompatActivity implements OnClickItemMessageListener {
    @Bind(R.id.viewpager) ViewPager viewPager;
    @Bind(R.id.tl_8) SlidingTabLayout slidingTabLayout;
    private Subscription subscriptionMessage;
    private Subscription subscriptionCarId;
    private MessageManager mgsManager;

    private final String[] mTitles = {"ทั้งหมด", "คุยกับคนซื้อ", "คุยกับคนขาย"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_chat_all);
        ButterKnife.bind(this);
        initViewPager();

        if (savedInstanceState == null)
            loadAllCarId();


        Bundle intentNotification = getIntent().getExtras();
        if(intentNotification != null) {
            String carId = intentNotification.getString("carid");
            String messageFromUser = intentNotification.getString("roomid");
            findPostCar(carId,messageFromUser);
        }

//        MainThreadBus.getInstance().register(this);
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

    private void loadAllMessage(CarIdDao carIdDao) {
        Log.d("view", "loadAllMessage: "+carIdDao.getAllCarId());
        Observable<MessageAdminCollectionDao> rxCallLoadMsg1 =
                HttpManager.getInstance().getService()
                        .observableMessageAdmin(carIdDao.getAllCarId());

        Observable<MessageCollectionDao> rxCallLoadMsg2 = HttpManager.getInstance()
                .getService().observableMessageClient(Registration.getInstance().getUserId());

        Observable<MessageManager> observableManager =
                Observable.zip(rxCallLoadMsg1, rxCallLoadMsg2, new Func2<MessageAdminCollectionDao, MessageCollectionDao, MessageManager>() {
                    @Override
                    public MessageManager call(MessageAdminCollectionDao messageAdminCollectionDao, MessageCollectionDao messageCollectionDao) {
                        MessageManager manager = new MessageManager();
                        manager.unifyDao(messageAdminCollectionDao
                                ,messageCollectionDao);
                        return manager;
                    }
                });

        subscriptionMessage = observableManager
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<MessageManager>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(MessageManager messageManager) {
                         mgsManager = messageManager;
                        MainThreadBus.getInstance().post(produceMsgManager());
                    }
                });
    }


    @Subscribe
    public void onMessageReceived(RemoteMessage remoteMessage){
        remoteMessage.getData();
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
                        Intent intent = new Intent(ConversationActivity.this, CarDetailActivity.class);
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
        findPostCar(messageDao.getMessageCarId(),messageDao.getMessageFromUser());
//        Call<PostCarCollectionDao> call =
//            HttpManager.getInstance().getService().loadCarModel(messageDao.getMessageCarId());
//        call.enqueue(new Callback<PostCarCollectionDao>() {
//            @Override
//            public void onResponse(Call<PostCarCollectionDao> call, Response<PostCarCollectionDao> response) {
//                if (response.isSuccessful()) {
//                    if (response.body().getListCar() != null) {
//                        PostCarDao item = response.body().getListCar().get(0);
//                        Intent intent = new Intent(ConversationActivity.this, CarDetailActivity.class);
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
                case 0: return new ConversationAllFragment();
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
