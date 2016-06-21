package com.dollarandtrump.angelcar.manager.http;

import android.content.Context;
import android.util.Log;

import com.dollarandtrump.angelcar.model.Gallery;
import com.dollarandtrump.angelcar.model.ImageModel;

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
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/********************************************
 * Created by HumNoy Developer on 18/6/2559.
 * AngelCarProject
 * ผู้คร่ำหวอดในกวงการ Android มากกว่า 1 ปี
 ********************************************/
public class RxUploadFile {

    public static RxUploadFile with(Context context){
        return new RxUploadFile(context);
    }

    private Context mContext;
    private RxUploadFile(Context mContext){
        this.mContext = mContext;
    }

    public UploadImagePostCar postCar(){
        return new UploadImagePostCar(mContext);
    }

    /** Image Post Car **/

    /** Image Post Car **/
    public static class UploadImagePostCar {
        Gallery mGallery;
        String id = null;
        Context mContext;

        public UploadImagePostCar(Context context) {
            mContext = context;
        }

        public UploadImagePostCar setId(String id) {
            this.id = id;
            return this;
        }

        public SubscribeImagePostCar setGallery(Gallery mGallery) {
            this.mGallery = mGallery;
            return new SubscribeImagePostCar(this);
        }


        public static class SubscribeImagePostCar {
            OkHttpClient okHttpClient = new OkHttpClient();
            Gallery mGallery;
            String id;
            Context mContext;

            public SubscribeImagePostCar(UploadImagePostCar postCar) {
                this.mGallery = postCar.mGallery;
                this.id = postCar.id;
                this.mContext = postCar.mContext;
            }

            public void subscriber(Subscriber<String> subscriber) {
                createObserver().subscribe(subscriber);
            }

            public void subscriber() {
                createObserver().subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        Log.d("Rx Upload", "Completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("Rx Upload", "RxUpload Error: ", e);
                    }

                    @Override
                    public void onNext(String s) {
                        Log.d("Rx Upload", s);
                    }
                });
            }

            private Observable<String> createObserver() {
                if (id == null) throw new NullPointerException("RxUpload class ::-> id is null");
                return Observable.from(mGallery.getListGallery()).map(new Func1<ImageModel, String>() {
                    @Override
                    public String call(final ImageModel imageModel) {
                        RequestBody requestBody = new MultipartBody.Builder()
                                .setType(MultipartBody.FORM)
                                .addFormDataPart("carid", id)
                                .addFormDataPart(
                                        "userfile",
                                        imageModel.convertToFile(mContext).getName(),
                                        RequestBody.create(MediaType.parse("image/png"), imageModel.convertToFile(mContext))).build();
                        Request request = new Request.Builder()
                                .url("http://www.angelcar.com/ios/data/gadata/imgupload.php")
                                .post(requestBody)
                                .build();
                        try {
                            Response responseFile = okHttpClient.newCall(request).execute();
                            if (responseFile.isSuccessful()) {
                                return responseFile.body().string();
                            }
                        } catch (IOException e) {
                            return "fail";
                        }
                        return "fail";
                    }
                }).subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        }
    }

}
