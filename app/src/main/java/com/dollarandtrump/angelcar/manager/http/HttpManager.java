package com.dollarandtrump.angelcar.manager.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Modifier;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Class OkHttpManager ใช้สำหรับ @ส่งMessage @ส่งรูป
 */
public class HttpManager {

    private static HttpManager instance;
    private ApiService service;
    private Retrofit.Builder builder;
    public static HttpManager getInstance() {
        if (instance == null)
            instance = new HttpManager();
        return instance;
    }

    private HttpManager() {
        Gson gson = new GsonBuilder()
//                .serializeNulls()
//                .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                .excludeFieldsWithoutExposeAnnotation()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();
       builder = new Retrofit.Builder()
                .baseUrl("http://angelcar.com/")
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson));
    }

    public ApiService getService(long TimeOutMillis) { // Milliseconds
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(TimeOutMillis, TimeUnit.MILLISECONDS).build();
        Retrofit retrofit = builder.client(okHttpClient).build();
        service = retrofit.create(ApiService.class);
        return service;
    }

    public ApiService getService() {
        Retrofit retrofit = builder.build();
        service = retrofit.create(ApiService.class);
        return service;
    }
}


//        Gson gson = new GsonBuilder()
//                .setDateFormat("yyyy-MM-dd HH:mm:ss")
//                .create();
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("http://angelcar.com/")
//                .addConverterFactory(GsonConverterFactory.create(gson))
//                .build();
//        service = retrofit.create(ApiService.class);