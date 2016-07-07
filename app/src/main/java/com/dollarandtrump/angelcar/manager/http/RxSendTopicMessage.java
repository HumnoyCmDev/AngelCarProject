package com.dollarandtrump.angelcar.manager.http;

import android.content.Context;
import android.net.Uri;

import com.dollarandtrump.angelcar.manager.Contextor;
import com.dollarandtrump.angelcar.utils.FileUtils;

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
public class RxSendTopicMessage implements Observable.OnSubscribe<String> {

    public enum SendMessageType {
        SEND,UPLOAD_FILES
    }

    private OkHttpClient mClient;

    private Uri mUri;
    private SendMessageType type;
    private String mBastUrl ="http://angelcar.com/ios/api/ga_chatadmin.php?operation=new&message=%s||%s||%s||%s";
    private Context mContext = Contextor.getInstance().getContext();

    /**Constructor Send Message File image**/
    @Deprecated
    public RxSendTopicMessage(Uri uri,
                              String id, String messageFromUser) {
        this.mUri = uri;
        this.type = SendMessageType.UPLOAD_FILES;
        mClient = new OkHttpClient();
        mBastUrl = String.format(mBastUrl,id,messageFromUser,"%s","user");

    }

    /**Constructor Send Message text**/
    public RxSendTopicMessage(String id, String messageFromUser, String message) {
        this.type = SendMessageType.SEND;
        mClient = new OkHttpClient();
        mBastUrl = String.format(mBastUrl,id,messageFromUser,message,"user");
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
        File f = FileUtils.getFile(mContext, mUri);
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
            subscriber.onCompleted();
        }
    }

}
