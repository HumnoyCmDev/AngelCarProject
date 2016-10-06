package com.dollarandtrump.angelcar.manager.http;

import android.content.Context;
import android.net.Uri;

import com.dollarandtrump.angelcar.manager.Contextor;
import com.dollarandtrump.angelcar.utils.FileUtils;
import com.dollarandtrump.angelcar.utils.ReduceSizeImage;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import rx.Observable;
import rx.Subscriber;

/********************************************
 * Created by HumNoy Developer on 27/6/2559.
 * ผู้คร่ำหวอดในกวงการ Android มากกว่า 1 ปี
 * AngelCarProject
 ********************************************/
public class RxSendMessage implements Observable.OnSubscribe<String> {

    public enum SendMessageType {
        SEND,UPLOAD_FILES
    }

    private OkHttpClient mClient;

    private Uri mUri;
    private SendMessageType type;
    private String mBastUrl ="http://angelcar.com/ios/api/ga_chatcar.php?operation=new&message=%s||%s||%s||%s||%s";
    private String mMessage;
    private String mId;
    private String mMessageBy;
    private String mUser;
    private String mMessageFromUser;
    private Context mContext = Contextor.getInstance().getContext();

    /**Constructor Send Message File image**/
    public RxSendMessage(Uri uri,
                         String id,String messageFromUser,String messageBy,String user) {
        this.type = SendMessageType.UPLOAD_FILES;
        mId = id;
        mUri = uri;
        mMessage = "%s";
        mMessageFromUser = messageFromUser;
        mMessageBy = messageBy;
        mUser = user;
        mClient = new OkHttpClient();
        mBastUrl = String.format(mBastUrl,mId,mMessageFromUser,mMessage,mMessageBy,mUser);
    }

    /**Constructor Send Message text**/
    public RxSendMessage(String id,String messageFromUser,String message,String messageBy,String user) {
        this.type = SendMessageType.SEND;
        mId = id;
        mMessage = message;
        mMessageFromUser = messageFromUser;
        mMessageBy = messageBy;
        mUser = user;
        mClient = new OkHttpClient();
        mBastUrl = String.format(mBastUrl,mId,mMessageFromUser,mMessage,mMessageBy,mUser);
    }

    /**Constructor Send Message status wait**/
    public RxSendMessage(String id,String messageFromUser,String message,String messageBy) {
        this.type = SendMessageType.SEND;
        mId = id;
        mMessage = message;
        mMessageFromUser = messageFromUser;
        mMessageBy = messageBy;
        mClient = new OkHttpClient();
        mBastUrl = String.format("http://angelcar.com/ios/api/ga_chatcar.php?operation=new&message=%s||%s||%s||%s",
                mId,mMessageFromUser,mMessage,mMessageBy);
    }

    @Override
    public void call(Subscriber<? super String> subscriber) {
        switch (type){
            case SEND:
                sendMessage(subscriber);
                break;
            case UPLOAD_FILES:
                sendMessageFile(subscriber);
                break;
        }

    }



    /** Method Send Massage**/
    private void sendMessage(final Subscriber<? super String> subscriber){
        Request.Builder builder = new Request.Builder();
        Request request = builder.url(mBastUrl).build();
        mClient.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                subscriber.onError(e);
            }
            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) {
                try {
                    subscriber.onNext(response.body().string());
                    subscriber.onCompleted();
                } catch (IOException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        });
    }
/** Send message หลัง upload file image **/
    void sendMessage(String mMessage,final Subscriber<? super String> subscriber){
        mBastUrl = String.format(mBastUrl,mMessage);
        Request.Builder builder = new Request.Builder();
        Request request = builder.url(mBastUrl).build();
        mClient.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                subscriber.onError(e);
            }
            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) {
                try {
                    subscriber.onNext(response.body().string());
                    subscriber.onCompleted();
                } catch (IOException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        });
    }

    /** Method Send Massage**/
    private void sendMessageFile(Subscriber<? super String> subscriber){
        File f = new ReduceSizeImage(FileUtils.getFile(mContext, mUri)).resizeImageFile(ReduceSizeImage.SIZE_SMALL);

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("userfile", f.getName(),
                        RequestBody.create(MediaType.parse("image/png"), f))
                .build();
        Request request = new Request.Builder()
                .url("http://angelcar.com/ios/data/gadata/gachatcarimageupload.php")
                .post(requestBody)
                .build();
        try {
            okhttp3.Response responseFile = mClient.newCall(request).execute();
            if (responseFile.isSuccessful()) {
//                subscriber.onNext(responseFile.body().string());
//                subscriber.onCompleted();
                sendMessage(responseFile.body().string(),subscriber);
            }
        } catch (IOException e) {
            e.printStackTrace();
            subscriber.onError(e);
        }
    }

}
