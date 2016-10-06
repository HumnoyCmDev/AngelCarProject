package com.dollarandtrump.angelcar.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.activeandroid.util.SQLiteUtils;
import com.dollarandtrump.angelcar.MainApplication;
import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.dao.MessageDao;
import com.dollarandtrump.angelcar.dao.PostCarCollectionDao;
import com.dollarandtrump.angelcar.dao.PostCarDao;
import com.dollarandtrump.angelcar.dao.TopicDao;
import com.dollarandtrump.angelcar.fragment.conversation.ConversationTopicFragment;
import com.dollarandtrump.angelcar.fragment.conversation.ConversationBuyFragment;
import com.dollarandtrump.angelcar.fragment.conversation.ConversationSellFragment;
import com.dollarandtrump.angelcar.interfaces.OnClickItemMessageListener;
import com.dollarandtrump.angelcar.manager.MessageManager;
import com.dollarandtrump.angelcar.manager.bus.MainThreadBus;
import com.dollarandtrump.angelcar.manager.http.HttpManager;
import com.dollarandtrump.angelcar.manager.LoadConversation;
import com.flyco.tablayout.SlidingTabLayout;
import com.google.firebase.messaging.RemoteMessage;
import com.squareup.otto.Produce;
import com.squareup.otto.Subscribe;

import org.parceler.Parcels;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConversationActivity extends BaseAppCompat implements OnClickItemMessageListener {
    @Bind(R.id.viewpager) ViewPager viewPager;
    @Bind(R.id.tl_8) SlidingTabLayout slidingTabLayout;
    @Bind(R.id.toolbar) Toolbar mToolbar;
    private MessageManager mManager;

    @Inject SharedPreferences sharedPreferences;

    LoadConversation loadConversation;
    private final String[] mTitles = {"คุยกับเจ้าหน้าที่", "คุยกับคนซื้อ", "คุยกับคนขาย"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        initViewPager();
        ((MainApplication) getApplication()).getApplicationComponent().inject(this);


        Bundle intentNotification = getIntent().getExtras();
        if(intentNotification != null) {
            String carId = intentNotification.getString("carid");
            String messageFromUser = intentNotification.getString("roomid");
            findPostCar(carId,messageFromUser);
        }

        loadConversation = new LoadConversation();

    }


    @Subscribe
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String type = remoteMessage.getData().get("type");
        if (type.equals("chatfinance") || type.equals("chatrefinance") || type.equals("chatpawn") || type.equals("chatcar")){
            loadConversation.load(callBackManager);
        }
    }

    @Produce
    public MessageManager produceMsgManager(){
        return mManager;
    }

    private void initViewPager() {
        TotalChatViewPagerAdapter pagerAdapter = new TotalChatViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        slidingTabLayout.setViewPager(viewPager,mTitles);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        loadConversation.unSubscribe();
    }


    @Override
    public void onResume() {
        super.onResume();
        MainThreadBus.getInstance().register(this);
//        if (isConnectInternet()) {
            loadConversation.load(callBackManager);
//        }

        //show dot
        /**Topic**/
        List<MessageDao> topic = findMessageNotRead("Topic","officer");
        if (topic != null && topic.size() > 0){
            slidingTabLayout.showMsg(0,topic.size());
            slidingTabLayout.setMsgMargin(0,19,15);
        }
        List<MessageDao> buy = findMessageNotRead("Buy","shop");
        if (buy != null && buy.size() > 0){
            slidingTabLayout.showMsg(2,buy.size());
            slidingTabLayout.setMsgMargin(1,25,15);
        }
        List<MessageDao> sell = findMessageNotRead("Sell","user");
        if (sell != null && sell.size() > 0){
            slidingTabLayout.showMsg(1,sell.size());
            slidingTabLayout.setMsgMargin(2,22,15);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();
        MainThreadBus.getInstance().unregister(this);
    }

    private List<MessageDao> findMessageNotRead(String type,String messageBy) {
        return SQLiteUtils.rawQuery(MessageDao.class, "SELECT * FROM Conversation " +
                "INNER JOIN MessageDao " +
                "ON Conversation.Message = MessageDao.Id " +
                "WHERE Conversation.ConversationType = '" + type + "' " +
                "And MessageDao.MessageBy = '" + messageBy + "'  " +
                "AND MessageDao.MessageStatus = 0", null);

    }

    private void findPostCar(String carId, final String messageFromUser){
        Log.d("Conversation", "findPostCar: "+carId);
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

    @Override
    public Snackbar onCreateSnackBar() {
        return Snackbar.make(mToolbar, R.string.status_network, Snackbar.LENGTH_INDEFINITE);
    }

    /**************
    *Listener Zone*
    ***************/

    @Override
    public void onClickItemMessage(final MessageDao messageDao) {
        if (isConnectInternet()) {
            if (!messageDao.isTopic()) {
                findPostCar(messageDao.getMessageCarId(), messageDao.getMessageFromUser());
            } else {
                TopicDao topic = new TopicDao();
                topic.setId(Integer.valueOf(messageDao.getMessageCarId()));
                topic.setUserId(messageDao.getMessageFromUser());
                Intent intent = new Intent(ConversationActivity.this, TopicChatActivity.class);
                intent.putExtra("topic", Parcels.wrap(topic));
                intent.putExtra("room", "");
                startActivity(intent);
            }
        }
    }

    LoadConversation.CallBack callBackManager = new LoadConversation.CallBack() {
        @Override
        public void call(MessageManager manager) {
            mManager = manager;
            MainThreadBus.getInstance().post(produceMsgManager());
        }
    };

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
