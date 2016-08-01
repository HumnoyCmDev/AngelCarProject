package com.dollarandtrump.angelcar.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dollarandtrump.angelcar.Adapter.ViewMessageAdapter;
import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.dao.FollowCollectionDao;
import com.dollarandtrump.angelcar.dao.FollowDao;
import com.dollarandtrump.angelcar.dao.MessageCollectionDao;
import com.dollarandtrump.angelcar.dao.MessageDao;
import com.dollarandtrump.angelcar.dao.PictureCollectionDao;
import com.dollarandtrump.angelcar.dao.PostCarDao;
import com.dollarandtrump.angelcar.dao.Results;
import com.dollarandtrump.angelcar.manager.MessageManager;
import com.dollarandtrump.angelcar.manager.Permission;
import com.dollarandtrump.angelcar.manager.Registration;
import com.dollarandtrump.angelcar.manager.WaitMessageObservable;
import com.dollarandtrump.angelcar.manager.bus.MainThreadBus;
import com.dollarandtrump.angelcar.manager.http.HttpManager;
import com.dollarandtrump.angelcar.manager.http.RxSendMessage;
import com.dollarandtrump.angelcar.rx_picker.RxImagePicker;
import com.dollarandtrump.angelcar.rx_picker.RxLocationPicker;
import com.dollarandtrump.angelcar.rx_picker.Sources;
import com.dollarandtrump.angelcar.utils.Cache;
import com.dollarandtrump.angelcar.view.ItemCarDetails;
import com.google.android.gms.location.places.Place;
import com.google.gson.Gson;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxbinding.widget.TextViewTextChangeEvent;
import com.squareup.otto.Subscribe;

import org.parceler.Parcels;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


public class CarDetailActivity extends AppCompatActivity implements ItemCarDetails.OnClickItemHeaderChatListener {
    private static final String TAG = "CarDetailActivity";

    @Bind(R.id.linear_layout_group_chat) LinearLayout groupChat;
    @Bind(R.id.edit_text_input_chat) EditText mInputChat;
    @Bind(R.id.toolbar) Toolbar toolbar;

    @Bind(R.id.message_button_send)
    Button mSend;

    // TODO-ORANGE Test new adapter chat(1)
    @Bind(R.id.recycler_chat)
    RecyclerView mListChat;
    ViewMessageAdapter mViewMessageAdapter;

    private PictureCollectionDao mPictureCollectionDao;
    private MessageManager mMessageManager;
    private String mMessageBy = "shop";// shop & user
    private PostCarDao mPostCarDao;
    private String mMessageFromUser;
    private LinearLayoutManager mLinearLayoutManager;

    private WaitMessageObservable mWaitMessage;
    private Subscription mSubscription;
    private String mKeyMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_detail);
        ButterKnife.bind(this);
        initToolbar();
        initInstance();


        if (savedInstanceState == null) {
            // load message
            Cache cache = new Cache();
            if (cache.isFile(mKeyMessage)) {
                String json = cache.load(mKeyMessage, String.class);
                MessageCollectionDao messageDao = new Gson().fromJson(json, MessageCollectionDao.class);
                mMessageManager.setMessageDao(messageDao);

                mViewMessageAdapter.setMessageDao(mMessageManager.getMessageDao());
                mViewMessageAdapter.notifyDataSetChanged();
                mLinearLayoutManager.scrollToPosition(mViewMessageAdapter.getItemCount());
                loadMoreMessage(mMessageManager.getMaximumId());
            } else {
                loadMessageNewer();
            }
        }
    }

    private void loadFollow() {
        //init on//off Button Follow
        Call<FollowCollectionDao> call =
                HttpManager.getInstance().getService()
                        .loadFollow(Registration.getInstance().getShopRef());
        call.enqueue(followCollectionDaoCallback);
    }

    private void initInstance() {
        // getIntent
        /*init detail chat*/
        mPostCarDao = Parcels.unwrap(
                getIntent().getParcelableExtra("PostCarDao"));
        mMessageFromUser = getIntent().getStringExtra("messageFromUser");
        int intentForm = getIntent().getIntExtra("intentForm", 1);

        mKeyMessage = String.format(getResources().getString(R.string.key_message),
                mPostCarDao.getCarId(),mMessageFromUser);


        /*check user*/
            mMessageBy = mPostCarDao.getShopRef().contains(Registration.getInstance().getShopRef()) ? "shop" : "user";
//            Toast.makeText(CarDetailActivity.this, mPostCarDao.getCarId()+","+ mMessageBy, Toast.LENGTH_LONG).show();

        if (intentForm == 0 && mMessageBy.contains("shop"))
            groupChat.setVisibility(View.GONE);

        /*load image*/
        Call<PictureCollectionDao> callLoadPictureAll =
                HttpManager.getInstance().getService().loadAllPicture(String.valueOf(mPostCarDao.getCarId()));
        callLoadPictureAll.enqueue(loadPictureCallback);


        mMessageManager = new MessageManager();

       /* mAdapter = new ChatAdapter(mMessageBy);
        mListView.setAdapter(mAdapter);
        mAdapter.setOnClickItemBannerListener(this);*/


        //TODO-GREEN Test new adapter InitInstance
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
        mListChat.setLayoutManager(mLinearLayoutManager);
        mViewMessageAdapter = new ViewMessageAdapter(this,mMessageBy);
        mViewMessageAdapter.setMessageDao(mMessageManager.getMessageDao());
        mListChat.setAdapter(mViewMessageAdapter);
        mViewMessageAdapter.setOnClickItemBannerListener(this);

        mViewMessageAdapter.setOnItemChatClickListener(new ViewMessageAdapter.OnItemChatClickListener() {
            @Override
            public void onClickItemChat(MessageDao message, int position) {
                // Intent View Image
                if (message != null) {
                    if (position > 1) {
                        if (message.getMessageText().contains("<img>") &&
                                message.getMessageText().contains("</img>")) {
                            String url = message.getMessageText()
                                    .substring("<img>".length(),
                                            message.getMessageText().lastIndexOf("</img>"));
                            Intent intent = new Intent(CarDetailActivity.this,
                                    SingleViewImageActivity.class);
                            intent.putExtra("url", url);
                            startActivity(intent);
                        }
                    }
                }
            }
        });

        RxTextView.textChangeEvents(mInputChat).map(new Func1<TextViewTextChangeEvent, Boolean>() {
            @Override
            public Boolean call(TextViewTextChangeEvent textViewTextChangeEvent) {
                return textViewTextChangeEvent.text().length() > 0;
            }
        }).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                mSend.setEnabled(aBoolean);
                mSend.setVisibility(aBoolean ? View.VISIBLE : View.INVISIBLE);
            }
        });

    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @OnClick({R.id.message_button_send,R.id.button_personnel,R.id.button_image,
    R.id.button_camera,R.id.button_place})
    public void onClick(View v){
        final String user = mMessageBy.contains("shop") ? mMessageManager.getMessageDao()
                .getListMessage().get(0).getMessageFromUser() : mPostCarDao.getShopRef();
        switch (v.getId()){
            case R.id.message_button_send:
                sendMessage(user, mInputChat.getText().toString());
                mInputChat.setText(null);

            break;

            case R.id.button_personnel:
                Dialog dialog = new AlertDialog.Builder(CarDetailActivity.this)
                        .setTitle("Message!")
                        .setMessage("เชิญเจ้าหน้าที่")
                        .setNegativeButton("Ok", listenerDialogConfirm)
                        .create();
                dialog.show();
            break;

            case R.id.button_image:
                RxImagePicker.with(this).requestImage(Sources.GALLERY).subscribe(new Action1<Uri>() {
                    @Override
                    public void call(final Uri uri) {
                        sendImageMessage(user,uri);
                    }
                });
                break;

            case R.id.button_camera:
                RxImagePicker.with(this).requestImage(Sources.CAMERA).subscribe(new Action1<Uri>() {
                    @Override
                    public void call(Uri uri) {
                        sendImageMessage(user,uri);
                    }
                });
                break;
            case R.id.button_place:
                RxLocationPicker.with(this).requestLocation().subscribe(new Action1<Place>() {
                    @Override
                    public void call(Place place) {
                        Snackbar.make(getWindow().getDecorView(),place.getAddress()+"",Snackbar.LENGTH_LONG).show();
                    }
                });
                break;
        }

    }


    private void sendImageMessage(final String user, final Uri uri) {
        RxSendMessage rxSendMessage = new RxSendMessage(uri,
                ""+ mPostCarDao.getCarId(),mMessageFromUser, mMessageBy,user);
        Observable.create(rxSendMessage)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.d(TAG, "send image: "+s);
                    }
                });
    }

    private void sendMessage(final String user, final String messageText) {
        //add model message
//        mMessageManager.addMessageMe(mMessageBy,messageText);
        mMessageManager.addMessageMe(mMessageBy,messageText);
        mViewMessageAdapter.setMessageDao(mMessageManager.getMessageDao());
        mViewMessageAdapter.notifyItemInserted(mViewMessageAdapter.getItemCount()-1);
        mLinearLayoutManager.smoothScrollToPosition(mListChat,null,mViewMessageAdapter.getItemCount());

        RxSendMessage rxSendMessage = new RxSendMessage(String.valueOf(mPostCarDao.getCarId()),
                mMessageFromUser, messageText, mMessageBy, user);
        Observable.create(rxSendMessage)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.d(TAG, "send message: " + s);
                    }
                });

    }


    @Override
    public void onItemClickBanner(int position) {
        Intent intent = new Intent(CarDetailActivity.this, ViewPictureActivity.class);
        intent.putExtra("PICTURE_DAO", Parcels.wrap(mPictureCollectionDao));
        intent.putExtra("POSITION",position);
        startActivity(intent);
    }

    @Override
    public void onItemClickPhone(String phone) {
        if (phone != null && phone.equals("NULL")) return;
        if (Permission.callPhone(this)) {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone.trim().replaceAll(" ", "")));
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            startActivity(intent);
        }
    }


    //    @Override
//    public void onItemClickFollow(boolean isFollow) {
//        Snackbar.make(mSend,"Follow :"+isFollow,Snackbar.LENGTH_LONG).show();
//        if (isFollow) {
//            //Add Follow
//            Call<Results> callAddFollow = HttpManager.getInstance().getService()
//                    .follow("add",String.valueOf(mPostCarDao.getCarId()),
//                    Registration.getInstance().getShopRef());
//            callAddFollow.enqueue(callbackAddOrRemoveFollow);
//        }else {
//            //Delete Follow
//            Call<Results> callDelete = HttpManager.getInstance().getService()
//                    .follow("delete",String.valueOf(mPostCarDao.getCarId()),
//                    Registration.getInstance().getShopRef());
//            callDelete.enqueue(callbackAddOrRemoveFollow);
//
//        }
//    }

    private void loadMessageNewer() {

//        RxMessageObservable.with().publishSubject()
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Action1<MessageCollectionDao>() {
//                    @Override
//                    public void call(MessageCollectionDao dao) {
//                        if (dao != null) {
//                            mMessageManager.appendDataToBottomPosition(dao);
//                            // คอมเม้นไว้ หาก list เด้งลงมา Last Position
//                            mAdapter.setMessages(mMessageManager.getMessageDao().getListMessage());
//                            mAdapter.notifyDataSetChanged();
//                        }else {
//                            Log.d(TAG, "call: null");
//                        }
//                    }
//                });

        /*save message json [message_json_[carId]_[messageFromUser]]*/
        Log.i(TAG, "loadMessage: :: "+ mPostCarDao.getCarId()+"||"+ mMessageFromUser +"||0");

        Call<MessageCollectionDao> call =
                HttpManager.getInstance().getService()
                        .viewMessage(mPostCarDao.getCarId()+"||"+ mMessageFromUser +"||0");
        call.enqueue(new Callback<MessageCollectionDao>() {
            @Override
            public void onResponse(Call<MessageCollectionDao> call, Response<MessageCollectionDao> response) {
                if (response.isSuccessful()){

                    initMessageAdapter(response.body());

                    String gson = new Gson().toJson(response.body());
                    boolean isSuccess = new Cache().save(mKeyMessage, gson);

                    /*start Time Out 1000L wait message */
                    /**observable wait message **/
                    mWaitMessage = new WaitMessageObservable(WaitMessageObservable.Type.CHAT_CAR,mMessageManager.getMaximumId(),
                            String.valueOf(mPostCarDao.getCarId()), mMessageFromUser,mMessageManager.getCurrentIdStatus());
                    mSubscription = Observable.create(mWaitMessage)
                            .subscribeOn(Schedulers.newThread()).subscribe();
                    
                }else {
                    try {
                        Toast.makeText(CarDetailActivity.this,response.errorBody().string(),Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<MessageCollectionDao> call, Throwable t) {
                Toast.makeText(CarDetailActivity.this,"Failure LogCat!!",Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onFailure: ", t);
            }
        });

    }

    public void loadMoreMessage(int currentMessageId){
        HttpManager.getInstance().getService()
                .observableViewMessage(mPostCarDao.getCarId()+"||"+ mMessageFromUser +"||"+currentMessageId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<MessageCollectionDao>() {
                    @Override
                    public void call(MessageCollectionDao dao) {
                        initMessageAdapter(dao);
                    }
                });
    }

    private void initMessageAdapter(MessageCollectionDao dao) {
        mMessageManager.appendDataToBottomPosition(dao);
        mViewMessageAdapter.setMessageDao(mMessageManager.getMessageDao());
        mViewMessageAdapter.notifyDataSetChanged();
        mLinearLayoutManager.scrollToPosition(mViewMessageAdapter.getItemCount());
    }

    @Override
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
    public void produceMessage(MessageCollectionDao messageDao){ //รับภายใน WaitMessageObservable
        if (messageDao.getListMessage().size() > 0) {

            for (int countMessage = 0; countMessage < messageDao.getListMessage().size(); countMessage++) {
                MessageDao message = messageDao.getListMessage().get((messageDao.getListMessage().size()-1) - countMessage);
                if (message.getMessageStatus() == 0 ){ // ข้อความใหม่
                    if (message.getMessageBy().equals(mMessageBy)){ //Chat me แชทเก่าออกก่อน
                       mMessageManager.updateMessageMe(countMessage,message);
                        mViewMessageAdapter.notifyDataSetChanged();
                        mListChat.smoothScrollToPosition(mViewMessageAdapter.getItemCount());
                    } else { //chat them
                        mMessageManager.updateMessageThem(message);
                        mViewMessageAdapter.notifyDataSetChanged();
                    }
                } else { // อ่านแล้ว
                    mMessageManager.updateMessageMe(countMessage,message);
                    mViewMessageAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("pic",Parcels.wrap(mPictureCollectionDao));
        outState.putParcelable("postcar",Parcels.wrap(mPostCarDao));
        outState.putParcelable("message",Parcels.wrap(mMessageManager.getMessageDao()));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mPictureCollectionDao = Parcels.unwrap(savedInstanceState.getParcelable("pic"));
        mPostCarDao = Parcels.unwrap(savedInstanceState.getParcelable("postcar"));
        MessageCollectionDao message = Parcels.unwrap(savedInstanceState.getParcelable("message"));
        mMessageManager.setMessageDao(message);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mWaitMessage != null)
            mWaitMessage.unsubscribe();

        if (mSubscription != null)
            mSubscription.unsubscribe();
//            RxMessageObservable.with().onDestroy();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*********************
     *Listener Class Zone
     *********************/
    DialogInterface.OnClickListener listenerDialogConfirm = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
                Call<Results> call =
                        HttpManager.getInstance().getService().regOfficer(mPostCarDao.getCarId()+"||"+mMessageFromUser);
                call.enqueue(new Callback<Results>() {
                    @Override
                    public void onResponse(Call<Results> call, Response<Results> response) {
                        if (response.isSuccessful()){
                            Toast.makeText(CarDetailActivity.this,"success ::"+response.body().getResult(),Toast.LENGTH_SHORT).show();
                        }else {
                            try {
                                Toast.makeText(CarDetailActivity.this,"success"+response.errorBody().string(),Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Results> call, Throwable t) {
                        Log.e(TAG, "onFailure: ", t);
                    }
                });
        }
    };

    Callback<PictureCollectionDao> loadPictureCallback = new Callback<PictureCollectionDao>() {
        @Override
        public void onResponse(Call<PictureCollectionDao> call, Response<PictureCollectionDao> response) {
            if (response.isSuccessful()) {
                mPictureCollectionDao = response.body();

               /* mAdapter.setPictureDao(mPictureCollectionDao);
                mAdapter.setPostCarDao(mPostCarDao);
                mAdapter.notifyDataSetChanged();*/

                // TODO-GREEN Test new adapter set images
                mViewMessageAdapter.setImageCar(mPictureCollectionDao);
                mViewMessageAdapter.setCarInfo(mPostCarDao);
                mViewMessageAdapter.notifyDataSetChanged();

            } else {
                try {
                    Log.i(TAG, "onResponse: " + response.errorBody().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onFailure(Call<PictureCollectionDao> call, Throwable t) {
            Log.e(TAG, "onFailure: ", t);
        }
    };

    Callback<FollowCollectionDao> followCollectionDaoCallback = new Callback<FollowCollectionDao>() {
        @Override
        public void onResponse(Call<FollowCollectionDao> call, Response<FollowCollectionDao> response) {
            if (response.isSuccessful()) {
                if (response.body().getRows() != null) {
                    for (FollowDao dao : response.body().getRows()) {
                        if (dao.getCarRef().
                                contains(String.valueOf(mPostCarDao.getCarId()))) {
                            mViewMessageAdapter.setFollow(true);
                            mViewMessageAdapter.notifyDataSetChanged();
                        }
                    }
                }

            } else {
                try {
                    Log.i(TAG, "onResponse: " + response.errorBody().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onFailure(Call<FollowCollectionDao> call, Throwable t) {
            Log.e(TAG, "onFailure: ", t);
        }
    };

    Callback<Results> callbackAddOrRemoveFollow = new Callback<Results>() {
        @Override
        public void onResponse(Call<Results> call, Response<Results> response) {
            if (response.isSuccessful()) {
                Log.i(TAG, "onResponse:" + response.body().success);
            } else {
                try {
                    Log.i(TAG, "onResponse: " + response.errorBody().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        @Override
        public void onFailure(Call<Results> call, Throwable t) {
            Log.e(TAG, "onFailure: ", t);
        }
    };

    /****************
    *Inner Class Zone
    *****************/

//    private class WaitMessage extends WaitMessageOnBackground {
//        Response<MessageCollectionDao> response;
//        MessageManager manager;
//
//        public WaitMessage(MessageManager manager) {
//            this.manager = manager;
//        }
//
//        @Override
//        public void onBackground() {
////            Log.i(TAG, "doInBackground: loop");
//            int maxId = manager.getMaximumId();
//            Call<MessageCollectionDao> call =
//                    HttpManager.getInstance()
//                            .getService(60 * 1000) // Milliseconds (1 นาที)
//                            .waitMessage(mPostCarDao.getCarId()+"||"+ mMessageFromUser +"||" + maxId);
//            try {
//                response = call.execute(); // ทำงานเสร็จ จะเข้า onMainThread() ต่อ
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        @Override
//        public void onMainThread() {
//            if (response.isSuccessful()){
//                MessageCollectionDao messageDao = response.body();
////                RxMessageObservable.with().onInitMessage(messageDao);
//                MainThreadBus.getInstance().post(messageDao);
//            }else {
//                Log.i(TAG, "doInBackground: "+response.errorBody());
//            }
//        }
//    }

}
