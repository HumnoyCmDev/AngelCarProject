package com.dollarandtrump.angelcar.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.dollarandtrump.angelcar.Adapter.ChatAdapter;
import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.dao.MessageCollectionDao;
import com.dollarandtrump.angelcar.dao.MessageDao;
import com.dollarandtrump.angelcar.interfaces.WaitMessageOnBackground;
import com.dollarandtrump.angelcar.manager.MessageManager;
import com.dollarandtrump.angelcar.manager.WaitMessageSynchronous;
import com.dollarandtrump.angelcar.manager.bus.BusProvider;
import com.dollarandtrump.angelcar.manager.http.HttpManager;
import com.dollarandtrump.angelcar.manager.http.OkHttpManager;
import com.squareup.otto.Subscribe;

import org.parceler.Parcels;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

/***************************************
 * สร้างสรรค์ผลงานดีๆ
 * โดย humnoy Android Developer
 * ลงวันที่ 27/1/59. เวลา 13:42
 ***************************************/
public class ChatActivity extends AppCompatActivity {
    @Bind(R.id.list_view) ListView listView;

    @Bind(R.id.input_chat) EditText messageText;

    private ChatAdapter chatAdapter;

    private static final String TAG = "ChatActivity";

    MessageDao messageDao;
    private String messageBy;

    MessageManager messageManager;
    WaitMessageSynchronous synchronous;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view_chat_layout);
        ButterKnife.bind(this);

        Intent getIntent = getIntent();
        if (getIntent != null){
            messageBy = getIntent.getStringExtra("messageBy");
            messageDao = Parcels.unwrap(getIntent.getParcelableExtra("MessageDao"));
            Toast.makeText(ChatActivity.this,"-"+messageBy,Toast.LENGTH_SHORT).show();
        }

        chatAdapter = new ChatAdapter(messageBy);
        listView.setAdapter(chatAdapter);

        messageManager = new MessageManager();
        loadMessage();
    }

    private void loadMessage() {
        Call<MessageCollectionDao> call =
                HttpManager.getInstance().getService().viewMessage(
                messageDao.getMessageCarId() + "||" +
                        messageDao.getMessageFromUser() + "||" +
                        "1");
        call.enqueue(new retrofit2.Callback<MessageCollectionDao>() {
            @Override
            public void onResponse(Call<MessageCollectionDao> call, Response<MessageCollectionDao> response) {
                    if (response.isSuccessful()){

                        messageManager.setMessageDao(response.body());
                        chatAdapter.setMessages(messageManager.getMessageDao().getListMessage());
                        chatAdapter.notifyDataSetChanged();

                        synchronous
                                = new WaitMessageSynchronous(waitMessageOnBackground);
                        synchronous.execute();

                    }else {
                        try {
                            Toast.makeText(ChatActivity.this,response.errorBody().string(),Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
            }

            @Override
            public void onFailure(Call<MessageCollectionDao> call, Throwable t) {
                Toast.makeText(ChatActivity.this,"Failure LogCat!!",Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onFailure: ", t);
            }
        });

    }

    @OnClick(R.id.message_button_send)
    public void buttonSendMessage(){
        sendMessage(messageText.getText().toString().trim());
    }

    private void sendMessage(String message){
        if (message.equals("")) {
            OkHttpManager aPi = new OkHttpManager.SendMessageBuilder()
                    .setMessage(messageDao.getMessageCarId()+"||"+ messageDao.getMessageFromUser()+"||"+message+"||"+messageBy).build();
            aPi.callEnqueue(new OkHttpManager.CallBackMainThread() {
                @Override
                public void onResponse(okhttp3.Response response) {
                    Toast.makeText(ChatActivity.this,"success",Toast.LENGTH_SHORT).show();
                }
            });
            messageText.setText("");
        }
    }


    public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    @Subscribe
    public void produceMessage(MessageManager message){
        chatAdapter.setMessages(messageManager.getMessageDao().getListMessage());
        chatAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(synchronous != null)
        synchronous.killTask();
    }


    /*************
     *Inner Class
     **************/
    WaitMessageOnBackground waitMessageOnBackground = new WaitMessageOnBackground() {
        Response<MessageCollectionDao> response;
        @Override
        public void onBackground() {
            int maxId = messageManager.getMaximumId();
            Call<MessageCollectionDao> cell = HttpManager.getInstance()
                    .getService(60 * 1000).waitMessage(messageDao.getMessageCarId() + "||" +
                            messageDao.getMessageFromUser() + "||" +maxId);
            try {
                response = cell.execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onMainThread() {
            if (response.isSuccessful()) {
                messageManager.appendDataToBottomPosition(response.body());
                BusProvider.getInstance().post(messageManager);
//                for (MessageDao g : response.body().getListMessage()) {
//                    Log.i(TAG, "doInBackground: " + g.getMessageId());
//                    Log.i(TAG, "doInBackground: " + g.getMessageText());
//                }
            }
        }
    };

}
