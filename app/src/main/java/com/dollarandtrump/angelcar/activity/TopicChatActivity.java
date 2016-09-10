package com.dollarandtrump.angelcar.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dollarandtrump.angelcar.Adapter.TopicViewMessageAdapter;
import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.dao.MessageCollectionDao;
import com.dollarandtrump.angelcar.dao.MessageDao;
import com.dollarandtrump.angelcar.dao.ResponseDao;
import com.dollarandtrump.angelcar.dao.TopicDao;
import com.dollarandtrump.angelcar.interfaces.OnItemChatClickListener;
import com.dollarandtrump.angelcar.manager.MessageManager;
import com.dollarandtrump.angelcar.manager.Registration;
import com.dollarandtrump.angelcar.manager.WaitMessageObservable;
import com.dollarandtrump.angelcar.manager.bus.MainThreadBus;
import com.dollarandtrump.angelcar.manager.http.HttpManager;
import com.dollarandtrump.angelcar.manager.http.RxSendTopicMessage;
import com.dollarandtrump.angelcar.rx_picker.RxImagePicker;
import com.dollarandtrump.angelcar.rx_picker.Sources;
import com.dollarandtrump.angelcar.utils.FileUtils;
import com.dollarandtrump.angelcar.utils.Log;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxbinding.widget.TextViewTextChangeEvent;
import com.squareup.otto.Subscribe;

import org.parceler.Parcels;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import xyz.kotlindev.lib.network.ReactiveNetwork;
import xyz.kotlindev.lib.view.BarStatusView;

/***************************************
 * สร้างสรรค์ผลงานดีๆ
 * โดย humnoy Android Developer
 * ลงวันที่ 27/1/59. เวลา 13:42
 ***************************************/
public class TopicChatActivity extends AppCompatActivity implements OnItemChatClickListener {

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.edit_text_input_chat) EditText messageText;
    @Bind(R.id.recycler_list) RecyclerView list;
    @Bind(R.id.message_button_send) Button mButtonSend;
    @Bind(R.id.linear_layout_group_button_chat) LinearLayout mGroupButtChat;
    @Bind(R.id.text_status)TextView mStatus;

//    @Bind(R.id.bar_status) BarStatusView mBarStatus;


    private MessageManager messageManager;
    private TopicViewMessageAdapter mAdapter;
    private String mRoomId;
    private String mUserId;
    private String mTopicMessage = null;

    private WaitMessageObservable mWaitMessage;
    private Subscription mSubscription;
    private LinearLayoutManager linearManager;

    private boolean isLoadingMessageOld = false;
    private Subscription internetConnectivitySubscription;
    InputMethodManager imm;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_chat);
        ButterKnife.bind(this);
        initToolbar();

        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        mUserId = Registration.getInstance().getUserId();
        TopicDao mTopic = null;
        Bundle arg = getIntent().getExtras();
        if (arg != null) {
            mTopic = Parcels.unwrap(arg.getParcelable("topic"));
            String room = "ห้องสนทนา "+arg.getString("room");
            mTopicMessage = arg.getString("topic_message",null);
            if(getSupportActionBar() != null)
                getSupportActionBar().setTitle(room);
            if (Log.isLoggable(Log.DEBUG)) Log.d("Room -->"+room);
        }


        messageManager = new MessageManager();
        mAdapter = new TopicViewMessageAdapter(TopicChatActivity.this, "user");
        mAdapter.setMessageDao(messageManager.getMessageDao());
        linearManager = new LinearLayoutManager(this);
        linearManager.setStackFromEnd(true);
        list.setLayoutManager(linearManager);
        list.setAdapter(mAdapter);

        RxTextView.textChangeEvents(messageText).map(new Func1<TextViewTextChangeEvent, Boolean>() {
            @Override
            public Boolean call(TextViewTextChangeEvent textViewTextChangeEvent) {
                return textViewTextChangeEvent.text().length() > 0;
            }
        }).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                mButtonSend.setEnabled(aBoolean);
                mButtonSend.setVisibility(aBoolean ? View.VISIBLE : View.INVISIBLE);
            }
        });


        if (mTopicMessage == null) {// view message
            mStatus.setVisibility(View.GONE);
            Animation animOut = AnimationUtils.loadAnimation(this,R.anim.zoom_face_out);
            mStatus.setAnimation(animOut);

            mGroupButtChat.setVisibility(View.VISIBLE);
            mRoomId = String.valueOf(mTopic.getId());
            String message = mRoomId+"||"+mTopic.getUserId()+"||0";
            Log.d(message);
            loadMessage(message);

//            getWindow().setSoftInputMode(
//                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            View v = getCurrentFocus();
            if (v != null)
            imm.hideSoftInputFromWindow(v.getWindowToken(),InputMethodManager.HIDE_IMPLICIT_ONLY);

        }else {
            // create toppic set max line 1
            messageText.setSingleLine(true);

            mStatus.setVisibility(View.VISIBLE);
            Animation animOut = AnimationUtils.loadAnimation(this,R.anim.zoom_face_in);
            mStatus.setAnimation(animOut);
        }

        /**scroll list**/
        list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int visibleItemCount = linearManager.getChildCount();
                int totalItemCount = linearManager.getItemCount();
                int firstVisibleItems = linearManager.findFirstVisibleItemPosition();
                int lastVisibleItems = linearManager.findLastVisibleItemPosition();
//                if (firstVisibleItems + visibleItemCount >= totalItemCount) {}

                if (firstVisibleItems <= 0){
                    if (isLoadingMessageOld) {
                        isLoadingMessageOld = messageManager.addMessageStepAtTopPosition();
                        mAdapter.setMessageDao(messageManager.getMessageDao());

                        View c = linearManager.getChildAt(0);
                        int top = c == null ? 0 : c.getTop();
                        mAdapter.notifyDataSetChanged();
                        linearManager.scrollToPositionWithOffset(firstVisibleItems+messageManager.getAdditionalSize(),top);
                        Log.d("Load message old");
                    }
                }

            }
        });

//        mBarStatus.start();
//        connectivity();

    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void connectivity() {
        internetConnectivitySubscription = ReactiveNetwork.observeInternetConnectivity()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean isConnectedToInternet) {
                        if (isConnectedToInternet){
//                            mBarStatus.hide();
                        }else{
//                            mBarStatus.show();
//                            mBarStatus.setTitle(R.string.status_network);
                        }
                    }
                });
    }

    private void loadMessage(String message) {
//        mBarStatus.expand();
        HttpManager.getInstance().getService().observableViewMessageTopic(message)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MessageCollectionDao>() {
                    @Override
                    public void onCompleted() {
                        if (Log.isLoggable(Log.DEBUG))
                            Log.d("Chat Topic: onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (Log.isLoggable(Log.ERROR))
                            Log.e("Chat Topic: ", e);
                    }

                    @Override
                    public void onNext(MessageCollectionDao dao) {
                        messageManager.setMessageDao(dao);

                        /**Message SubList 20**/
                        mAdapter.setMessageDao(messageManager.messageSubList());
//                        mAdapter.setMessageDao(messageManager.getMessageDao());
                        mAdapter.notifyDataSetChanged();

                        isLoadingMessageOld = true;
//                        mBarStatus.close();

//                        topicViewMessageBaseAdapter.setMessageDao(messageManager.getMessageDao());
//                        topicViewMessageBaseAdapter.notifyDataSetChanged();

                        waitMessage();
                    }
                });
    }

    @OnClick(R.id.message_button_send)
    public void buttonSendMessage(){
        if (mTopicMessage != null){ // Create Topic
            messageText.setSingleLine(false);
            createTopic(messageText.getText().toString());
        }else { // Send message Room
            sendMessageRoom(messageText.getText().toString());
        }
        addMessageToAdapter(messageText.getText().toString());
    }

    private void addMessageToAdapter(String message) {
        messageManager.addMessageMe("user",message);
        mAdapter.notifyDataSetChanged();
        linearManager.smoothScrollToPosition(list,null,mAdapter.getItemCount());
        messageText.setText(null);
    }

    private void sendMessageRoom(String messageText) {
        RxSendTopicMessage topicMessage = new RxSendTopicMessage(mRoomId,mUserId,messageText);
        Observable.create(topicMessage).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        if (Log.isLoggable(Log.DEBUG)) Log.d("Topic Chat --> Complete");
                    }
                });
    }

    private void createTopic(String messageText) {
        String message = Registration.getInstance().getUserId()+"||"+messageText+"||"+mTopicMessage;
        HttpManager.getInstance().getService().observableCreateTopic(message)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseDao>() {

                    @Override
                    public void onCompleted() {
                         if (Log.isLoggable(Log.DEBUG)) Log.d("Topic -> onCompleted");
                        mTopicMessage = null;
                        mGroupButtChat.setVisibility(View.VISIBLE);
                        mStatus.setVisibility(View.GONE);
                        Animation animOut = AnimationUtils.loadAnimation(TopicChatActivity.this,R.anim.zoom_face_out);
                        mStatus.setAnimation(animOut);
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (Log.isLoggable(Log.ERROR)) Log.e("Topic -> error",e);
                    }

                    @Override
                    public void onNext(ResponseDao responseDao) {
                        mRoomId = responseDao.getResult();
                        String message = mRoomId+"||"+mUserId+"||0";
                        loadMessage(message);
                    }

                });
    }


    @OnClick({R.id.button_image,R.id.button_camera,R.id.button_place})
    public void onGroupButtonChat(View v){ //Send Image,camera,map
        switch (v.getId()){
            case R.id.button_image :
                onImagePicker(Sources.GALLERY);
                break;
            case R.id.button_camera :
                onImagePicker(Sources.CAMERA);
                break;
            default:
                onImagePicker(Sources.LOCATION);
                break;
        }
    }

//    @OnClick({R.id.button_test_anim})
//    public void onTestingAnim(){
//        if (mBarStatus.isOpened()) {
//            mBarStatus.hide();
//            Log.d("hide");
//        }else {
//            mBarStatus.show();
//            Log.d("show");
//        }
//    }

    private void onImagePicker(Sources sources){
        RxImagePicker.with(this).requestImage(sources).subscribe(new Action1<Uri>() {
            @Override
            public void call(Uri uri) {
            //TODO-RED Send Files To Chat Room
                if (mTopicMessage == null) {
                    addMessageToAdapter("<img>"+FileUtils.getFile(getBaseContext(),uri).getPath()+"</img>");
                    RxSendTopicMessage imageTopicMessage = new RxSendTopicMessage(uri,mRoomId,mUserId);
                    Observable.create(imageTopicMessage).subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action1<String>() {
                                @Override
                                public void call(String s) {
                                    if (Log.isLoggable(Log.DEBUG)) Log.d("Topic image Chat --> Complete");
                                }
                            });
                }
            }
        });
    }

    public void onResume() {
        super.onResume();
        MainThreadBus.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MainThreadBus.getInstance().unregister(this);

        if (internetConnectivitySubscription != null && internetConnectivitySubscription.isUnsubscribed()){
            internetConnectivitySubscription.unsubscribe();
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

    @Subscribe
    public void produceMessage(MessageCollectionDao messageDao){ //รับภายใน WaitMessageObservable
        if (messageDao.getListMessage().size() > 0) {

            for (int countMessage = 0; countMessage < messageDao.getListMessage().size(); countMessage++) {
                MessageDao message = messageDao.getListMessage().get((messageDao.getListMessage().size()-1) - countMessage);
                if (message.getMessageStatus() == 0 ){ // ข้อความใหม่
                    if (message.getMessageBy().equals("user")){ //Chat me แชทเก่าออกก่อน
                        messageManager.updateMessageMe(countMessage,message);
                        mAdapter.notifyDataSetChanged();
                        linearManager.smoothScrollToPosition(list,null,mAdapter.getItemCount());
                    } else { //chat them
                        messageManager.updateMessageThem(message);
                        mAdapter.notifyDataSetChanged();
                    }
                } else { // อ่านแล้ว
                    messageManager.updateMessageMe(countMessage,message);
                    mAdapter.notifyDataSetChanged();
                }
            }



            // scroll to bottom
            int lastPosition = linearManager.findLastVisibleItemPosition();
            if (lastPosition >= mAdapter.getItemCount() - 2){
                linearManager.smoothScrollToPosition(list,null,mAdapter.getItemCount());
            }


        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mWaitMessage != null) mWaitMessage.unsubscribe();
        if (mSubscription != null) mSubscription.unsubscribe();
    }


    private void waitMessage(){
        /**observable wait message **/
        mWaitMessage = new WaitMessageObservable(WaitMessageObservable.Type.CHAT_TOPIC,messageManager.getMaximumId(),
                mRoomId, mUserId,messageManager.getCurrentIdStatus());
        mSubscription = Observable.create(mWaitMessage)
                .subscribeOn(Schedulers.newThread()).subscribe();
    }

    @Override
    public void onClickImageChat(String imageUrl, int position) {
        Intent intent = new Intent(TopicChatActivity.this,
                SingleViewImageActivity.class);
        intent.putExtra("url", imageUrl);
        startActivity(intent);

    }
}
