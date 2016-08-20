package com.dollarandtrump.angelcar.module;

import com.dollarandtrump.angelcar.manager.http.ApiService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class NetworkModule {

    @Provides
    @Singleton
    OkHttpClient.Builder providesOkHttpBuilder(){
        return new OkHttpClient.Builder();
    }
    @Provides
    @Singleton
    OkHttpClient providesOkHttpClient(){
        return new OkHttpClient();
    }
    @Provides
    @Singleton
    Gson providesGson(){
        return new GsonBuilder()
//                .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
//                .serializeNulls()
                .excludeFieldsWithoutExposeAnnotation()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();
    }
    @Provides
    @Singleton
    Retrofit.Builder providesRetrofit(Gson gson){
        return new Retrofit.Builder()
                .baseUrl("http://angelcar.com/")
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson));
    }
    @Provides
    @Singleton
    @Named("apitimeout")
    ApiService providesApiServiceTimeout(OkHttpClient.Builder okHttpBuilder, Retrofit.Builder retrofitBuilder) {
        OkHttpClient client = okHttpBuilder.readTimeout(60*1000, TimeUnit.MILLISECONDS).build();
        ApiService service = retrofitBuilder.client(client).build().create(ApiService.class);
        return service;
    }
    @Provides
    @Singleton
    ApiService providesApiService(OkHttpClient client, Retrofit.Builder retrofitBuilder) {
        ApiService service = retrofitBuilder.client(client).build().create(ApiService.class);
        return service;
    }



}
