package com.dollarandtrump.angelcar.manager.http;

import android.util.Log;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Win8.1Pro on 2/6/2559.
 */
public class HttpSendManager {

    public static void message(final String message, Subscriber<String> subscriber){
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                OkHttpClient okHttpClient = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(String.format("http://angelcar.com/ios/api/ga_chatcar.php?operation=new&message=%s",message)).build();
                try {
                    Response response = okHttpClient.newCall(request).execute();
                    if (response.isSuccessful()){
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


    }

    public static void messageFile(String message, final File file, Subscriber<String> subscriber){
        final String urlMessage = String.format("http://angelcar.com/ga_chatcar.php?operation=new&message=%s",message);
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
                    Response responseFile =  okHttpClient.newCall(request).execute();
                    if (responseFile.isSuccessful()){ // upload success
                        // Send message
                        String response = responseFile.body().string();
                        Request.Builder builder = new Request.Builder();
                        Request requestMsg = builder.url(String.format(urlMessage,response)).build();
                        Response responseMassage = okHttpClient.newCall(requestMsg).execute();
                        if (responseMassage.isSuccessful()){ // send message success
                            subscriber.onNext("send image success : " + responseMassage.body().string());
                        }
                    }else {
                        Log.d("HttpConnect", "onResponse: up load image fail");
                    }

                } catch (IOException e) {
                    subscriber.onError(e);
                }

            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

//    public static void

//    private String requestOkHttp(){
//
//    }

}
