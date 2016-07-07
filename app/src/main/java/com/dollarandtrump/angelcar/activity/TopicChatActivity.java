package com.dollarandtrump.angelcar.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;

import com.dollarandtrump.angelcar.Adapter.TopicViewMessageAdapter;
import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.dao.MessageCollectionDao;
import com.dollarandtrump.angelcar.dao.Results;
import com.dollarandtrump.angelcar.dao.TopicDao;
import com.dollarandtrump.angelcar.interfaces.WaitMessageOnBackground;
import com.dollarandtrump.angelcar.manager.MessageManager;
import com.dollarandtrump.angelcar.manager.Registration;
import com.dollarandtrump.angelcar.manager.WaitMessageObservable;
import com.dollarandtrump.angelcar.manager.WaitMessageSynchronous;
import com.dollarandtrump.angelcar.manager.bus.MainThreadBus;
import com.dollarandtrump.angelcar.manager.http.HttpManager;
import com.dollarandtrump.angelcar.manager.http.RxSendTopicMessage;
import com.dollarandtrump.angelcar.utils.Log;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxbinding.widget.TextViewTextChangeEvent;
import com.squareup.otto.Subscribe;

import org.parceler.Parcels;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/***************************************
 * สร้างสรรค์ผลงานดีๆ
 * โดย humnoy Android Developer
 * ลงวันที่ 27/1/59. เวลา 13:42
 ***************************************/
public class TopicChatActivity extends AppCompatActivity {

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.edit_text_input_chat) EditText messageText;
    @Bind(R.id.recycler_list) RecyclerView list;
    @Bind(R.id.message_button_send) Button mButtonSend;

    private MessageManager messageManager;
    private TopicViewMessageAdapter messageAdapter;
    private String mRoomId;
    private String mUserId;
    private String mTopicMessage = null;

    private WaitMessageObservable mWaitMessage;
    private Subscription mSubscription;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_chat);
        ButterKnife.bind(this);

        mUserId = Registration.getInstance().getUserId();
        TopicDao mTopic = null;
        Bundle arg = getIntent().getExtras();
        if (arg != null) {
            mTopic = Parcels.unwrap(arg.getParcelable("topic"));
            String room = "ห้องสนทนา "+arg.getString("room");
            mTopicMessage = arg.getString("topic_message",null);

            toolbar.setTitle(room);
            setSupportActionBar(toolbar);
            if (Log.isLoggable(Log.DEBUG)) Log.d("Room -->"+room);
        }


        messageManager = new MessageManager();
        messageAdapter = new TopicViewMessageAdapter(TopicChatActivity.this, "user");
        messageAdapter.setMessageDao(messageManager.getMessageDao());
        LinearLayoutManager linearManager = new LinearLayoutManager(this);
        linearManager.setStackFromEnd(true);
        list.setLayoutManager(linearManager);
        list.setAdapter(messageAdapter);

        RxTextView.textChangeEvents(messageText).map(new Func1<TextViewTextChangeEvent, Boolean>() {
            @Override
            public Boolean call(TextViewTextChangeEvent textViewTextChangeEvent) {
                return textViewTextChangeEvent.text().length() > 0;
            }
        }).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                mButtonSend.setEnabled(aBoolean);
            }
        });

        if (mTopicMessage == null) {// view message
            mRoomId = String.valueOf(mTopic.getId());
            String message = mRoomId+"||"+mTopic.getUserId()+"||0";
            loadMessage(message);
        }

    }

    private void loadMessage(String message) {
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
                        messageAdapter.setMessageDao(dao);
                        messageAdapter.notifyDataSetChanged();
                        waitMessage();
                    }
                });
    }

    @OnClick(R.id.message_button_send)
    public void buttonSendMessage(){
        if (mTopicMessage != null){ // Create Topic
           String message = Registration.getInstance().getUserId()+"||"+messageText.getText().toString()+"||"+mTopicMessage;
           HttpManager.getInstance().getService().observableCreateTopic(message)
                   .subscribeOn(Schedulers.newThread())
                   .observeOn(AndroidSchedulers.mainThread())
                   .subscribe(new Observer<Results>() {

                       @Override
                       public void onCompleted() {
                            if (Log.isLoggable(Log.DEBUG)) Log.d("Topic -> onCompleted");
                           mTopicMessage = null;
                       }

                       @Override
                       public void onError(Throwable e) {
                           if (Log.isLoggable(Log.ERROR)) Log.e("Topic -> error",e);
                       }

                       @Override
                       public void onNext(Results results) {
                           mRoomId = results.getResult();
                           String message = mRoomId+"||"+mUserId+"||0";
                           loadMessage(message);
                       }

                   });
        }else { // Send message Room
            RxSendTopicMessage topicMessage = new RxSendTopicMessage(mRoomId,mUserId,messageText.getText().toString());
            Observable.create(topicMessage).subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<String>() {
                        @Override
                        public void call(String s) {
                            if (Log.isLoggable(Log.DEBUG)) Log.d("Topic Chat --> Complete");
                        }
                    });
        }

        messageText.setText(null);
    }


    @OnClick({R.id.button_image})


    public void onResume() {
        super.onResume();
        MainThreadBus.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MainThreadBus.getInstance().unregister(this);
    }

    @Subscribe
    public void produceMessage(MessageCollectionDao messageGa){ //รับภายใน WaitMessageObservable
        if (messageGa.getListMessage().size() > 0) {
            messageManager.appendDataToBottomPosition(messageGa);
            messageAdapter.setMessageDao(messageManager.getMessageDao());
            messageAdapter.notifyDataSetChanged();
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
                mRoomId, mUserId);
        mSubscription = Observable.create(mWaitMessage)
                .subscribeOn(Schedulers.newThread()).subscribe();
    }

}
