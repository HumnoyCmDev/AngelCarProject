package com.dollarandtrump.angelcar.manager.http;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;


import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Class OkHttpManager ใช้สำหรับ @ส่งMessage @ส่งรูป
 */
public class OkHttpManager {

    public interface CallBackMainThread {
        void onResponse(Response response);
//    void onFailure();
    }

    private OkHttpClient okHttpClient = new OkHttpClient();
    private Request request;

    private OkHttpManager(SendMessageBuilder sendMessageBuilder) {
        this.request = sendMessageBuilder.request;
    }

    public OkHttpManager(UploadFileBuilder uploadFileBuilder) {

    }
     //Deprecated
    public void callEnqueue(final CallBackMainThread mainThread){
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Message-", "onFailure: ", e);
            }

            @Override
            public void onResponse(Call call, final Response response) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (response.isSuccessful()){
                            mainThread.onResponse(response);
                        }
                    }
                });
            }
        });
    }

    public void putMessage(){
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Message-", "onFailure: ", e);
            }

            @Override
            public void onResponse(Call call, final Response response) {

            }
        });
    }


    public static class SendMessageBuilder {
        private String url = "http://angelcar.com/ios/api/ga_chatcar.php?operation=new&message=";
        private Request request;

        public SendMessageBuilder() {
        }

        public SendMessageBuilder setMessage(String message){
            this.url += message;
            Request.Builder builder = new Request.Builder();
            request = builder.url(url).build();
            return this;
        }
        public OkHttpManager build() {
            return new OkHttpManager(this);
        }


    }

    public static class UploadFileBuilder {
        private String urlMessage = "http://angelcar.com/ga_chatcar.php?operation=new&message=%s";
        private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
        private OkHttpClient okHttpClient ;
        private static final String TAG = "UploadFileBuilder";

        public UploadFileBuilder(String id,String messageFromUser,String messageBy) {
            okHttpClient = new OkHttpClient();
            urlMessage = String.format(urlMessage,id+"||"+messageFromUser+"||%s||"+messageBy);
            Log.i(TAG, "UploadFileBuilder: "+urlMessage);
        }

        public UploadFileBuilder putImage(final File fileImage){

            new AsyncTask<Void,Void,String>(){

                @Override
                protected String doInBackground(Void... params) {

                    try {
                        // upload image

                        RequestBody requestBody = new MultipartBody.Builder()
                                .setType(MultipartBody.FORM)
                                .addFormDataPart("userfile", fileImage.getName(),
                                        RequestBody.create(MEDIA_TYPE_PNG, fileImage))
                                .build();

                        Request request = new Request.Builder()
                                .url("http://angelcar.com/ios/data/gadata/gachatcarimageupload.php")
                                .post(requestBody)
                                .build();
                        Response responseFile =  okHttpClient.newCall(request).execute();
                        if (responseFile.isSuccessful()){ // upload success
                            // Send message
                            String response = responseFile.body().string();
                            Log.i(TAG, "onResponse: up load image success : "+response);
                                Request.Builder builder = new Request.Builder();
                                Request requestMsg = builder.url(String.format(urlMessage,response)).build();
                                Response responseMassage = okHttpClient.newCall(requestMsg).execute();
                                if (responseMassage.isSuccessful()){ // send message success
                                    Log.i(TAG, "doInBackground: send image success");

                                    return "send image success";

                                } else {
                                    Log.i(TAG, "doInBackground: send image fail");
                                }

                        }else {
                            Log.i(TAG, "onResponse: up load image fail");
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e(TAG, "doInBackground: ", e);
                    }

                    return null;
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);
                }
            }.execute();//AsyncTask.THREAD_POOL_EXECUTOR

            return this;
        }
        public OkHttpManager build() {
            return new OkHttpManager(this);
        }
    }

}
