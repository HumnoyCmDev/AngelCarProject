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
import com.dollarandtrump.angelcar.fragment.ChatAllFragment;
import com.dollarandtrump.angelcar.fragment.ChatBuyFragment;
import com.dollarandtrump.angelcar.fragment.ChatSellFragment;
import com.dollarandtrump.angelcar.interfaces.OnClickItemMessageListener;
import com.dollarandtrump.angelcar.manager.MessageManager;
import com.dollarandtrump.angelcar.manager.Registration;
import com.dollarandtrump.angelcar.manager.bus.MainThreadBus;
import com.dollarandtrump.angelcar.manager.http.HttpManager;
import com.flyco.tablayout.SlidingTabLayout;
import com.squareup.otto.Produce;

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
//    private MessageManager mgsManager;

    private final String[] mTitles = {"ทั้งหมด", "คุยกับคนซื้อ", "คุยกับคนขาย"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_chat_all);
        ButterKnife.bind(this);
        initViewPager();

        if (savedInstanceState == null)
            loadAllCarId();

        /**
        * TODO รับ Event bus หน้า Detail เพื่อเช็คค่าจาก push Notification
        * ค่าที่ได้มา ตรงกับห้องที่กำลังแชทอยู่หรอืไม่ ถ้าไม่ ก็แสดงการแจ้งเตือน ถ้าใช่ไม่ต้องแสดง*
         * กรณีที่ ไม่ได้อยู่ในการแชท ก็แสดงการแจ้งเตือนทั้งหมด
        * */

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
//                         mgsManager = messageManager;
                        MainThreadBus.getInstance().post(messageManager);
                    }
                });
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

//    @Override
//    public void onStart() {
//        super.onStart();
//        MainThreadBus.getInstance().register(this);
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        MainThreadBus.getInstance().unregister(this);
//    }

    /**************
    *Listener Zone*
    ***************/
   /* Callback<CarIdDao> carIdDaoCallback = new Callback<CarIdDao>() {
        @Override
        public void onResponse(Call<CarIdDao> call, Response<CarIdDao> response) {
            if (response.isSuccessful()) {
                Log.i("ViewChat", ""+response.body().getAllCarId());
                String allCarId = response.body().getAllCarId();
                if (allCarId.contains(""))
                    allCarId = "null";
//                //Sample Rx android
                Observable<MessageAdminCollectionDao> rxCallLoadMsg1 =
                        HttpManager.getInstance().getService()
                                .observableMessageAdmin(allCarId);
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
                                Log.i("ViewChat", "onCompleted: ");
                            }

                            @Override
                            public void onError(Throwable e) {
//                                Log.e("ViewChat","error ",e);
                                e.printStackTrace();
                            }

                            @Override
                            public void onNext(MessageManager messageManager) {
//                                mgsManager = messageManager;
                                BusProvider.getInstance().post(messageManager);
                            }
                        });

                //-----------//

            }else {
                try {
                    Log.e("ViewChat", "onResponse: "+response.errorBody().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onFailure(Call<CarIdDao> call, Throwable t) {
            Log.e("ViewChat", "onFailure: ", t);
        }
    };
*/
//    @Produce
//    public MessageManager produceMsgManager(){
//        return mgsManager;
//    }

    @Override
    public void onClickItemMessage(final MessageDao messageDao) {
        Call<PostCarCollectionDao> call =
            HttpManager.getInstance().getService().loadCarModel(messageDao.getMessageCarId());
        call.enqueue(new Callback<PostCarCollectionDao>() {
            @Override
            public void onResponse(Call<PostCarCollectionDao> call, Response<PostCarCollectionDao> response) {
                if (response.isSuccessful()) {
                    if (response.body().getListCar() != null) {
                        PostCarDao item = response.body().getListCar().get(0);
                        Intent intent = new Intent(ConversationActivity.this, DetailCarActivity.class);
                        intent.putExtra("PostCarDao", Parcels.wrap(item));
                        intent.putExtra("intentForm", 1);
                        intent.putExtra("messageFromUser", messageDao.getMessageFromUser());
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
                case 0: return new ChatAllFragment();
                case 1: return new ChatBuyFragment();
                case 2: return new ChatSellFragment();
            }
            return null;
        }
        @Override
        public int getCount() {
            return 3;
        }
    }

}
