package com.dollarandtrump.angelcar.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.dollarandtrump.angelcar.Adapter.ChatAdapter;
import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.dao.FollowCollectionDao;
import com.dollarandtrump.angelcar.dao.FollowDao;
import com.dollarandtrump.angelcar.dao.MessageCollectionDao;
import com.dollarandtrump.angelcar.dao.MessageDao;
import com.dollarandtrump.angelcar.dao.PictureCollectionDao;
import com.dollarandtrump.angelcar.dao.PostCarDao;
import com.dollarandtrump.angelcar.dao.Results;
import com.dollarandtrump.angelcar.manager.MessageManager;
import com.dollarandtrump.angelcar.manager.Registration;
import com.dollarandtrump.angelcar.manager.RxMessageObservable;
import com.dollarandtrump.angelcar.manager.WaitMessageObservable;
import com.dollarandtrump.angelcar.manager.bus.MainThreadBus;
import com.dollarandtrump.angelcar.manager.http.HttpManager;
import com.dollarandtrump.angelcar.manager.http.RxSendMessage;
import com.dollarandtrump.angelcar.rx_picker.RxImagePicker;
import com.dollarandtrump.angelcar.rx_picker.RxLocationPicker;
import com.dollarandtrump.angelcar.rx_picker.Sources;
import com.dollarandtrump.angelcar.view.HeaderChatCar;
import com.google.android.gms.location.places.Place;
import com.google.firebase.messaging.RemoteMessage;
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
import rx.schedulers.Schedulers;

public class DetailCarActivity extends AppCompatActivity implements HeaderChatCar.OnClickItemHeaderChatListener {
    private static final String TAG = "DetailCarActivity";

    @Bind(R.id.linear_layout_group_button_chat) LinearLayout mGroupChatIconButton;
    @Bind(R.id.linear_layout_group_chat) LinearLayout groupChat;
    @Bind(R.id.edit_text_input_chat) EditText mInputChat;
    @Bind(R.id.list_view) ListView mListView;
    @Bind(R.id.toolbar) Toolbar toolbar;

//    private View decorView;
    private PictureCollectionDao mPictureCollectionDao;
//    private WaitMessageSynchronous mSynchronous;
    private MessageManager mMessageManager;
    private String mMessageBy = "shop";// shop & user
    private ChatAdapter mAdapter;
    private PostCarDao mPostCarDao;
    private String mMessageFromUser;

    private WaitMessageObservable mWaitMessage;
    private Subscription mSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_car);
        ButterKnife.bind(this);
//        decorView = getWindow().getDecorView();
        initToolbar();
        initInstance();
        loadDataMessage();
        loadFollow();

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


        /*check user*/
            mMessageBy = mPostCarDao.getShopRef().contains(Registration.getInstance().getShopRef()) ? "shop" : "user";
            Toast.makeText(DetailCarActivity.this, mPostCarDao.getCarId()+","+ mMessageBy, Toast.LENGTH_LONG).show();

        if (intentForm == 0 && mMessageBy.contains("shop"))
            groupChat.setVisibility(View.GONE);

        /*load image*/
        Call<PictureCollectionDao> callLoadPictureAll =
                HttpManager.getInstance().getService().loadAllPicture(String.valueOf(mPostCarDao.getCarId()));
        callLoadPictureAll.enqueue(loadPictureCallback);


        mMessageManager = new MessageManager();

        mAdapter = new ChatAdapter(mMessageBy);
        mListView.setAdapter(mAdapter);
        mAdapter.setOnClickItemBannerListener(this);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Intent View Image
                if (mMessageManager != null &&
                        mMessageManager.getMessageDao() != null &&
                        mMessageManager.getMessageDao().getListMessage() != null) {

                    if (position > 1) {
                        MessageDao item = mMessageManager.getMessageDao()
                                .getListMessage().get(position - 2);
                        if (item.getMessageText().contains("<img>") &&
                                item.getMessageText().contains("</img>")) {
                            String url = item.getMessageText()
                                    .substring("<img>".length(),
                                            item.getMessageText().lastIndexOf("</img>"));
                            Intent intent = new Intent(DetailCarActivity.this,
                                    SingleViewImageActivity.class);
                            intent.putExtra("url", url);
                            startActivity(intent);
                        }
                        mAdapter.setShowDate(position);
                        mAdapter.notifyDataSetChanged();
                    }
                }

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
                Dialog dialog = new AlertDialog.Builder(DetailCarActivity.this)
                        .setTitle("Message!")
                        .setMessage("เชิญบุคคลที่ 3")
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
    
    @OnClick(R.id.show_icon_button_chat)
    public void showIconButtonChat(){
        if (mGroupChatIconButton.getVisibility() == View.GONE){
            mGroupChatIconButton.setVisibility(View.VISIBLE);
            Animation anim = AnimationUtils.loadAnimation(this, R.anim.view_group_slide_up);
            mGroupChatIconButton.startAnimation(anim);
        }else {
            mGroupChatIconButton.setVisibility(View.GONE);
        }
    }

    private void sendImageMessage(final String user, final Uri uri) {
        RxSendMessage rxSendMessage = new RxSendMessage(RxSendMessage.SendMessageType.UPLOAD_FILES,
                uri,
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

    private void sendMessage(final String user, final String s) {
        RxSendMessage rxSendMessage = new RxSendMessage(RxSendMessage.SendMessageType.SEND,
                "" + mPostCarDao.getCarId(), mMessageFromUser, s, mMessageBy, user);
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
        Intent intent = new Intent(DetailCarActivity.this, ViewPictureActivity.class);
        intent.putExtra("PICTURE_DAO", Parcels.wrap(mPictureCollectionDao));
        intent.putExtra("POSITION",position);
        startActivity(intent);
    }

    @Override
    public void onItemClickFollow(boolean isFollow) {
        Snackbar.make(mListView,"Follow :"+isFollow,Snackbar.LENGTH_LONG).show();
        if (isFollow) {
            //Add Follow
            Call<Results> callAddFollow = HttpManager.getInstance().getService()
                    .follow("add",String.valueOf(mPostCarDao.getCarId()),
                    Registration.getInstance().getShopRef());
            callAddFollow.enqueue(callbackAddOrRemoveFollow);
        }else {
            //Delete Follow
            Call<Results> callDelete = HttpManager.getInstance().getService()
                    .follow("delete",String.valueOf(mPostCarDao.getCarId()),
                    Registration.getInstance().getShopRef());
            callDelete.enqueue(callbackAddOrRemoveFollow);

        }
    }

    private void loadDataMessage() {

        RxMessageObservable.with().publishSubject()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<MessageCollectionDao>() {
                    @Override
                    public void call(MessageCollectionDao dao) {
                        if (dao != null) {
                            mMessageManager.appendDataToBottomPosition(dao);
                            // คอมเม้นไว้ หาก list เด้งลงมา Last Position
                            mAdapter.setMessages(mMessageManager.getMessageDao().getListMessage());
                            mAdapter.notifyDataSetChanged();
                        }else {
                            Log.d(TAG, "call: null");
                        }
                    }
                });

        Log.i(TAG, "loadDataMessage: :: "+ mPostCarDao.getCarId()+"||"+ mMessageFromUser +"||0");
        Call<MessageCollectionDao> call =
                HttpManager.getInstance().getService()
                        .viewMessage(mPostCarDao.getCarId()+"||"+ mMessageFromUser +"||0");
        call.enqueue(new Callback<MessageCollectionDao>() {
            @Override
            public void onResponse(Call<MessageCollectionDao> call, Response<MessageCollectionDao> response) {
                if (response.isSuccessful()){

                    mMessageManager.appendDataToBottomPosition(response.body());
                    mAdapter.setMessages(mMessageManager.getMessageDao().getListMessage());
                    mAdapter.notifyDataSetChanged();

                    /*start Time Out 1000L wait message */
//                    mSynchronous = new WaitMessageSynchronous(
//                            new WaitMessage(mMessageManager));
//                    mSynchronous.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                    /**observable wait message **/
                    mWaitMessage = new WaitMessageObservable(mMessageManager.getMaximumId(),
                            String.valueOf(mPostCarDao.getCarId()), mMessageFromUser);
                    mSubscription = Observable.create(mWaitMessage)
                            .subscribeOn(Schedulers.newThread()).subscribe();
                }else {
                    try {
                        Toast.makeText(DetailCarActivity.this,response.errorBody().string(),Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<MessageCollectionDao> call, Throwable t) {
                Toast.makeText(DetailCarActivity.this,"Failure LogCat!!",Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onFailure: ", t);
            }
        });


    }

    /**push Notification**/
    @Subscribe
    public void onNotification(RemoteMessage remoteMessage){
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
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
    public void produceMessage(MessageCollectionDao messageGa){ //รับภายในคลาส
        Log.i(TAG, "produceMessage: mainthread");
        if (messageGa.getListMessage().size() > 0) {
            mMessageManager.appendDataToBottomPosition(messageGa);
            // คอมเม้นไว้ หาก list เด้งลงมา Last Position
            mAdapter.setMessages(mMessageManager.getMessageDao().getListMessage());
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (mSynchronous != null){
//            mSynchronous.killTask();
//        }

        if (mWaitMessage != null)
            mWaitMessage.unSubscribe();

        if (mSubscription != null)
            mSubscription.unsubscribe();
            RxMessageObservable.with().onDestroy();

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
                            Toast.makeText(DetailCarActivity.this,"success ::"+response.body().getResult(),Toast.LENGTH_SHORT).show();
                        }else {
                            try {
                                Toast.makeText(DetailCarActivity.this,"success"+response.errorBody().string(),Toast.LENGTH_SHORT).show();
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

                mAdapter.setPictureDao(mPictureCollectionDao);
                mAdapter.setPostCarDao(mPostCarDao);
                mAdapter.notifyDataSetChanged();

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
                            mAdapter.setFollow(true);
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
