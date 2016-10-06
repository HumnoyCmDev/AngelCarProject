package com.dollarandtrump.angelcar.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.dollarandtrump.angelcar.Adapter.ViewMessageAdapter;
import com.dollarandtrump.angelcar.MainApplication;
import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.dao.MessageCollectionDao;
import com.dollarandtrump.angelcar.dao.MessageDao;
import com.dollarandtrump.angelcar.dao.PictureCollectionDao;
import com.dollarandtrump.angelcar.dao.PostCarDao;
import com.dollarandtrump.angelcar.dao.ResponseDao;
import com.dollarandtrump.angelcar.interfaces.OnItemChatClickListener;
import com.dollarandtrump.angelcar.manager.MessageManager;
import com.dollarandtrump.angelcar.manager.Permission;
import com.dollarandtrump.angelcar.manager.Registration;
import com.dollarandtrump.angelcar.manager.WaitMessageObservable;
import com.dollarandtrump.angelcar.manager.bus.MainThreadBus;
import com.dollarandtrump.angelcar.manager.http.HttpManager;
import com.dollarandtrump.angelcar.manager.http.RxSendMessage;
import com.dollarandtrump.angelcar.network.Connectivity;
import com.dollarandtrump.angelcar.rx_picker.RxImagePicker;
import com.dollarandtrump.angelcar.rx_picker.RxLocationPicker;
import com.dollarandtrump.angelcar.rx_picker.Sources;
import com.dollarandtrump.angelcar.utils.CacheData;
import com.dollarandtrump.angelcar.utils.FileUtils;
import com.dollarandtrump.angelcar.view.BarStatusView;
import com.dollarandtrump.angelcar.view.ItemCarDetails;
import com.google.android.gms.location.places.Place;
import com.google.gson.Gson;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxbinding.widget.TextViewTextChangeEvent;
import com.squareup.otto.Subscribe;

import org.parceler.Parcels;

import java.io.IOException;

import javax.inject.Inject;

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


public class ChatCarActivity extends BaseAppCompat implements ItemCarDetails.OnClickItemHeaderChatListener {
    private static final String TAG = "Chat";

    @Bind(R.id.linear_layout_group_chat) LinearLayout groupChat;
    @Bind(R.id.edit_text_input_chat) EditText mInputChat;
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.message_button_send) Button mSend;
    @Bind(R.id.recycler_chat) RecyclerView mListChat;
    @Bind(R.id.bar_status) BarStatusView mBarStatus;
    private ViewMessageAdapter mAdapter;

    private PictureCollectionDao mPictureCollectionDao;
    private MessageManager mMessageManager;
    private String mMessageBy = "shop";// shop & user
    private PostCarDao mPostCarDao;
    private String mMessageFromUser;
    private LinearLayoutManager mLinearLayoutManager;

    private WaitMessageObservable mWaitMessage;
    private Subscription mSubscription;
//    private String mKeyMessage;

    @Inject Gson mGson;
    @Inject CacheData cacheData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_detail);
        ButterKnife.bind(this);
        ((MainApplication) getApplication()).getApplicationComponent().inject(this);
        initToolbar();
        initInstance();


        if (savedInstanceState == null) {
            // load message
             /*save message json [message_json_[carId]_[messageFromUser]]*/
//            if (cacheData.isFile(mKeyMessage)) {
//                String json = cacheData.load(mKeyMessage, String.class);
//                MessageCollectionDao messageDao = mGson.fromJson(json, MessageCollectionDao.class);
//                mMessageManager.setMessageDao(messageDao);
//
//                mAdapter.setMessageDao(mMessageManager.getMessageDao());
//                mAdapter.notifyDataSetChanged();
////                mLinearLayoutManager.smoothScrollToPosition(mListChat,null,mViewMessageAdapter.getItemCount());
//                loadMoreMessage(mMessageManager.getMaximumId());
//            } else {
                loadMoreMessage(0);
//            }
            Log.i(TAG, "loadMessage: :: "+ mPostCarDao.getCarId()+"||"+ mMessageFromUser +"||0");
        }
    }

    private void initInstance() {
        // getIntent
        /*init detail chat*/
        mPostCarDao = Parcels.unwrap(
                getIntent().getParcelableExtra("PostCarDao"));
        mMessageFromUser = getIntent().getStringExtra("messageFromUser");
        int intentForm = getIntent().getIntExtra("intentForm", 1);

//        mKeyMessage = String.format("message_json_%s_%s",
//                mPostCarDao.getCarId(),mMessageFromUser);

        /**title**/
//        String carBrand = mPostCarDao.getCarName() + " " +
//                mPostCarDao.getCarSub() + " " + mPostCarDao.getCarSubDetail();
//        if (getSupportActionBar() != null)
//        getSupportActionBar().setTitle(carBrand);

        /*check user*/
            mMessageBy = mPostCarDao.getShopRef().contains(Registration.getInstance().getShopRef()) ? "shop" : "user";

        if (intentForm == 0 && mMessageBy.contains("shop"))
            groupChat.setVisibility(View.GONE);

        /*load image*/
        Call<PictureCollectionDao> callLoadPictureAll =
                HttpManager.getInstance().getService().loadAllPicture(String.valueOf(mPostCarDao.getCarId()));
        callLoadPictureAll.enqueue(loadPictureCallback);


        mMessageManager = new MessageManager();



        //TODO-GREEN Test new adapter InitInstance
        mLinearLayoutManager = new LinearLayoutManager(this);
//        mLinearLayoutManager.setStackFromEnd(true);
        mListChat.setLayoutManager(mLinearLayoutManager);
        mAdapter = new ViewMessageAdapter(this,mMessageBy);

        mAdapter.setCarInfo(mPostCarDao);

        mAdapter.setMessageDao(mMessageManager.getMessageDao());
        mListChat.setAdapter(mAdapter);
        mAdapter.setOnClickItemBannerListener(this);

        mAdapter.setOnItemChatClickListener(new OnItemChatClickListener() {
            @Override
            public void onClickImageChat(String imageUrl, int position) {
                // Intent View Image
                    if (position > 1) {
                            Intent intent = new Intent(ChatCarActivity.this,
                                    SingleViewImageActivity.class);
                            intent.putExtra("url", imageUrl);
                            startActivity(intent);
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
                Dialog dialog = personnelDialog();
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

    private Dialog personnelDialog() {
        return new AlertDialog.Builder(ChatCarActivity.this)
                            .setTitle("เชิญเจ้าหน้าที่")
                            .setMessage(R.string.alert_message_personnel)
                            .setNegativeButton("Ok", listenerDialogConfirm)
                            .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog1, int which) {
                                    dialog1.dismiss();
                                }
                            })
                            .create();
    }


    private void sendImageMessage(final String user, final Uri uri) {

        addMessageToAdapter("<img>"+ FileUtils.getFile(getBaseContext(),uri).getPath()+"</img>");

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
        addMessageToAdapter(messageText);

        RxSendMessage rxSendMessage = new RxSendMessage(String.valueOf(mPostCarDao.getCarId()),
                mMessageFromUser, messageText, mMessageBy, user);
        Observable.create(rxSendMessage)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();

    }

    private void addMessageToAdapter(String messageText) {
        mMessageManager.addMessageMe(mMessageBy,messageText);
        mAdapter.setMessageDao(mMessageManager.getMessageDao());
        mAdapter.notifyItemInserted(mAdapter.getItemCount()-1);
        mLinearLayoutManager.smoothScrollToPosition(mListChat,null,mAdapter.getItemCount());
    }


    @Override
    public void onItemClickBanner(int position) {
        Intent intent = new Intent(ChatCarActivity.this, ViewPictureActivity.class);
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

    @Override
    public void onItemClickViewShop(PostCarDao post) {
        Intent i = new Intent(ChatCarActivity.this, ShopActivity.class);
        i.putExtra("user",post.getUser());
        i.putExtra("shop",post.getShopRef());
        startActivity(i);
    }

    boolean isLoadingMessageOld = true;
    @Override
    public boolean onItemClickLoadMessageBefore() {
        if (isLoadingMessageOld) {
            isLoadingMessageOld = mMessageManager.addMessageStepAtTopPosition();
            mAdapter.setMessageDao(mMessageManager.getMessageDao());

            int firstVisibleItems = mLinearLayoutManager.findFirstVisibleItemPosition();
            View c = mLinearLayoutManager.getChildAt(2);
            int top = c == null ? 0 : c.getTop();
            mAdapter.notifyDataSetChanged();
            mLinearLayoutManager.scrollToPositionWithOffset(firstVisibleItems + mMessageManager.getAdditionalSize() ,top);
        }
        return isLoadingMessageOld;
    }

    @Override
    public void onAddPersonnel() {
        Dialog dialog = personnelDialog();
        dialog.show();
    }

    public void loadMoreMessage(int currentMessageId){
//        Log.d(TAG, "loadMessageNewer: "+mPostCarDao.getCarId()+"||"+ mMessageFromUser +"||0");
        HttpManager.getInstance().getService()
                .observableViewMessage(mPostCarDao.getCarId()+"||"+ mMessageFromUser +"||"+currentMessageId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<MessageCollectionDao>() {
                    @Override
                    public void call(MessageCollectionDao dao) {
                        initMessageAdapter(dao);

//                        String gson = mGson.toJson(dao);
//                        cacheData.save(mKeyMessage, gson);

                    /*start Time Out 1000L wait message */
                        /**observable wait message **/
                        mWaitMessage = new WaitMessageObservable(WaitMessageObservable.Type.CHAT_CAR,mMessageManager.getMaximumId(),
                                String.valueOf(mPostCarDao.getCarId()), mMessageFromUser,mMessageManager.getCurrentIdStatus());
                        mSubscription = Observable.create(mWaitMessage)
                                .subscribeOn(Schedulers.newThread()).subscribe();
                    }
                });
    }

    boolean isFirstSubList = true;
    private void initMessageAdapter(MessageCollectionDao dao) {
        mMessageManager.appendDataToBottomPosition(dao);
        if (isFirstSubList){
            mAdapter.setMessageDao(mMessageManager.messageSubList());
            isFirstSubList = false;
        }
        mAdapter.setMessageDao(mMessageManager.getMessageDao());
        mAdapter.notifyDataSetChanged();
//        mLinearLayoutManager.smoothScrollToPosition(mListChat,null,mViewMessageAdapter.getItemCount());
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
                        mAdapter.notifyDataSetChanged();
//                        mLinearLayoutManager.smoothScrollToPosition(mListChat,null,mViewMessageAdapter.getItemCount());
                    } else { //chat them
                        mMessageManager.updateMessageThem(message);
                        mAdapter.notifyDataSetChanged();
                    }
                } else { // อ่านแล้ว
                    mMessageManager.updateMessageMe(countMessage,message);
                    mAdapter.notifyDataSetChanged();
                }
            }
        }

        // scroll to bottom
        int lastPosition = mLinearLayoutManager.findLastVisibleItemPosition();
        if (lastPosition >= mAdapter.getItemCount() - 2){
            mLinearLayoutManager.smoothScrollToPosition(mListChat,null,mAdapter.getItemCount());
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
            HttpManager.getInstance().getService().observableRegOfficer(mPostCarDao.getCarId()+"||"+mMessageFromUser)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnError(new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            Log.e(TAG,"ERROR CHAT CAR", throwable);
                        }
                    })
                    .doOnNext(new Action1<ResponseDao>() {
                        @Override
                        public void call(ResponseDao responseDao) {
                            RxSendMessage rxSendMessage = new RxSendMessage(String.valueOf(mPostCarDao.getCarId()),
                                    mMessageFromUser,"กรุณารอสักครู่ ขณะนี้กำลังดำเนินการเรียกพนักงาน", "office");
                            Observable.create(rxSendMessage)
                                    .subscribeOn(Schedulers.newThread())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe();
                        }
                    })
                    .subscribe();
               /* Call<ResponseDao> call =
                        HttpManager.getInstance().getService().regOfficer(mPostCarDao.getCarId()+"||"+mMessageFromUser);
                call.enqueue(new Callback<ResponseDao>() {
                    @Override
                    public void onResponse(Call<ResponseDao> call, Response<ResponseDao> response) {
                        if (response.isSuccessful()){



//                            Toast.makeText(ChatCarActivity.this,"success ::"+response.body().getResult(),Toast.LENGTH_SHORT).show();
                        }else {
//                            try {
//                                Toast.makeText(ChatCarActivity.this,"success"+response.errorBody().string(),Toast.LENGTH_SHORT).show();
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseDao> call, Throwable t) {
                        Log.e(TAG, "onFailure: ", t);
                    }
                });*/
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
                mAdapter.setImageCar(mPictureCollectionDao);
                mAdapter.setCarInfo(mPostCarDao);
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


    @Override
    public void internetConnectivity(Boolean isConnectedToInternet) {
        super.internetConnectivity(isConnectedToInternet);
        if (isConnectedToInternet){
            // hide status bar
            mBarStatus.setVisibility(View.GONE);
            groupChat.setVisibility(View.VISIBLE);
        }else {
            // Show status bar
            mBarStatus.setVisibility(View.VISIBLE);
            groupChat.setVisibility(View.GONE);
        }
    }

    @Override
    public void onNetworkConnectivity(Connectivity connectivity) {
        super.onNetworkConnectivity(connectivity);
        final NetworkInfo.State state = connectivity.getState();
       if (state.equals("CONNECTED") && !isConnectInternet()){
           // Show status bar
           mBarStatus.setVisibility(View.VISIBLE);
       }else {
           // hide status bar
           mBarStatus.setVisibility(View.GONE);
       }
    }
}
