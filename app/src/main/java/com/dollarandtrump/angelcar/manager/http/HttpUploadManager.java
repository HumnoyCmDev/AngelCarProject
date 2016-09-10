package com.dollarandtrump.angelcar.manager.http;

import android.content.Context;

import com.dollarandtrump.angelcar.model.Gallery;
import com.dollarandtrump.angelcar.model.ImageModel;
import com.dollarandtrump.angelcar.utils.ReduceSizeImage;

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
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Win8.1Pro on 2/6/2559.
 */
public class HttpUploadManager {

    private HttpUploadManager(){

    }

    public static Observable<String> uploadLogoShop(final File file, final String shopId) {
                        final OkHttpClient okHttpClient = new OkHttpClient();
                     return   Observable.create(new Observable.OnSubscribe<String>() {
                            @Override
                            public void call(Subscriber<? super String> subscriber) {

                                File newFile = new ReduceSizeImage(file)
                                        .resizeImageFile(ReduceSizeImage.SIZE_SMALL);

                                RequestBody requestBody = new MultipartBody.Builder()
                                        .setType(MultipartBody.FORM)
                                        .addFormDataPart("shopid", shopId)
                                        .addFormDataPart(
                                                "userfile",
                                                newFile.getName(),
                                                RequestBody.create(MediaType.parse("image/png"), newFile)).build();

                                Request request = new Request.Builder()
                                        .url("http://angelcar.com/ios/data/clsdata/clsshopupload.php")
                                        .post(requestBody)
                                        .build();
                                try {
                                    Response responseFile = okHttpClient.newCall(request).execute();
                                    if (responseFile.isSuccessful()) {
                                        subscriber.onNext(responseFile.body().string());
                                        subscriber.onCompleted();
                                    }
                                } catch (IOException e) {
                                    subscriber.onError(e);
                                }
                            }
                        }).subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread());
    }

    public static void uploadFileShop(final Context context, Gallery gallery, final String shopId, final Subscriber<String> subscriber) {
        final OkHttpClient okHttpClient = new OkHttpClient();
        Observable.from(gallery.getListGallery()).subscribe(new Action1<ImageModel>() {
            @Override
            public void call(final ImageModel imageModel) {
                Observable.create(new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> subscriber) {

                        File newFile = new ReduceSizeImage(imageModel.convertToFile(context)).resizeImageFile(ReduceSizeImage.SIZE_BIG);

                        RequestBody requestBody = new MultipartBody.Builder()
                                .setType(MultipartBody.FORM)
                                .addFormDataPart("index",imageModel.getIndex())
                                .addFormDataPart("shopid", shopId)
                                .addFormDataPart(
                                        "userfile",
                                        newFile.getName(),
                                        RequestBody.create(MediaType.parse("image/png"), newFile)).build();

                        Request request = new Request.Builder()
                                .url("http://angelcar.com/ios/data/clsdata/shopprofileupload.php")
                                .post(requestBody)
                                .build();
                        try {
                            Response responseFile = okHttpClient.newCall(request).execute();
                            if (responseFile.isSuccessful()) {
                                subscriber.onNext(responseFile.body().string());
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
        });
    }

//    public static void uploadFilePostCar(List<File> fileList, final String id, final Subscriber<String> subscriber) {
//        final OkHttpClient okHttpClient = new OkHttpClient();
//        Observable.from(fileList)
//                .subscribe(new Action1<File>() {
//                    @Override
//                    public void call(final File file) {
//                        Observable.create(new Observable.OnSubscribe<String>() {
//                            @Override
//                            public void call(Subscriber<? super String> subscriber) {
//                                RequestBody requestBody = new MultipartBody.Builder()
//                                        .setType(MultipartBody.FORM)
//                                        .addFormDataPart("carid", id)
//                                        .addFormDataPart(
//                                                "userfile",
//                                                file.getName(),
//                                                RequestBody.create(MediaType.parse("image/png"), file)).build();
//                                Request request = new Request.Builder()
//                                        .url("http://www.angelcar.com/ios/data/gadata/imgupload.php")
//                                        .post(requestBody)
//                                        .build();
//                                try {
//                                    Response responseFile = okHttpClient.newCall(request).execute();
//                                    if (responseFile.isSuccessful()) {
//                                        subscriber.onNext(responseFile.body().string());
//                                        subscriber.onCompleted();
//                                    }
//                                } catch (IOException e) {
//                                    subscriber.onError(e);
//                                }
//                            }
//                        }).subscribeOn(Schedulers.newThread())
//                                .observeOn(AndroidSchedulers.mainThread())
//                                .subscribe(subscriber);
//                    }
//                });
//    }

    public static void uploadFilePostCar(final Context mContext, Gallery gallery, final String id, final Subscriber<String> subscriber) {
        final OkHttpClient okHttpClient = new OkHttpClient();
        Observable.from(gallery.getListGallery()).map(new Func1<ImageModel, String>() {
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
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }
}


