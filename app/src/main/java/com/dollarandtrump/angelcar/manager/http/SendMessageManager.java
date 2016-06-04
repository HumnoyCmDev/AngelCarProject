package com.dollarandtrump.angelcar.manager.http;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Win8.1Pro on 2/6/2559.
 */
public class SendMessageManager {
    private final static String BASE_URL = "http://angelcar.com/ios/api/ga_chatcar.php?operation=new&message=%s";
    private SubscriberMessage subscriberMessage;
    private SubscriberMessageFile subscriberMessageFile;
    public SendMessageManager(SubscriberMessage subscriberMessage) {
        this.subscriberMessage = subscriberMessage;
    }

    public SendMessageManager(SubscriberMessageFile subscriberMessageFile) {
        this.subscriberMessageFile = subscriberMessageFile;
    }


    public void subscriber(Subscriber<String> subscriber){
        if (subscriberMessage != null)
            subscriberMessage.subscriber(subscriber);
        if (subscriberMessageFile != null)
            subscriberMessageFile.subscriber(subscriber);
    }

    public void unSubscription(){
        if (subscriberMessage != null)
            subscriberMessage.unSubscription();
    }


    public static class Builder{
        private String message ;
        private File file;

        public SubscriberMessage message(String message){
            this.message = message;
             return new SubscriberMessage(this);
        }

        public SubscriberMessageFile messageFile(String id, String messageFromUser, String messageBy, File file){
            this.message = String.format(BASE_URL,id+"||"+messageFromUser+"||%s||"+messageBy);
            this.file = file;

            return new SubscriberMessageFile(this);
        }

    }

    public static class SubscriberMessageFile{
        private String message ;
        private File file;
        public SubscriberMessageFile(Builder builder) {
            this.file = builder.file;
            this.message = builder.message;
        }

        public SubscriberMessageFile subscriber(Subscriber<String> subscriber){
            final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
            Observable.create(new Observable.OnSubscribe<String>() {
                @Override
                public void call(Subscriber<? super String> subscriber) {
                    OkHttpClient okHttpClient = new OkHttpClient();
                    try {
                        // upload image
                        RequestBody requestBody = new MultipartBody.Builder()
                                .setType(MultipartBody.FORM)
                                .addFormDataPart("userfile", file.getName(),
                                        RequestBody.create(MEDIA_TYPE_PNG, file))
                                .build();
                        Request request = new Request.Builder()
                                .url("http://angelcar.com/ios/data/gadata/gachatcarimageupload.php")
                                .post(requestBody)
                                .build();
                        Response responseFile = okHttpClient.newCall(request).execute();
                        if (responseFile.isSuccessful()) { // upload success
                            // Send message
                            String response = responseFile.body().string();
                            Request.Builder builder = new Request.Builder();
                            Request requestMsg = builder.url(String.format(message, response)).build();
                            Response responseMassage = okHttpClient.newCall(requestMsg).execute();
                            if (responseMassage.isSuccessful()) { // send message success
                                subscriber.onNext("send image success : " + responseMassage.body().string());
                                subscriber.onCompleted();
                            }
                        } else {
                            Log.d("HttpConnect", "onResponse: up load image fail");
                        }

                    } catch (IOException e) {
                        subscriber.onError(e);
                    }

                }
            }).subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(subscriber);

            return this;
        }

        public SendMessageManager build(){
            return new SendMessageManager(this);
        }
    }

    public static class SubscriberMessage {
        private String message ;
        private Subscription subscription;
        public SubscriberMessage(Builder builder) {
            this.message = builder.message;
        }

        public SubscriberMessage subscriber(Subscriber<String> subscriber){
            subscription = Observable.create(new Observable.OnSubscribe<String>() {
                @Override
                public void call(Subscriber<? super String> subscriber) {
                    OkHttpClient okHttpClient = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(String.format(BASE_URL, message)).build();
                    try {
                        Response response = okHttpClient.newCall(request).execute();
                        if (response.isSuccessful()) {
                            subscriber.onNext(response.body().string());
                            subscriber.onCompleted();
                        }
                    } catch (IOException e) {
                        subscriber.onError(e);
                    }
                }
            }).subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(subscriber);
            return this;
        }

        public void unSubscription(){
            subscription.unsubscribe();
        }

        public SendMessageManager build(){
            return new SendMessageManager(this);
        }

    }


}


