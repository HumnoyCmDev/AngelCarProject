package com.dollarandtrump.angelcar.module;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.dollarandtrump.angelcar.utils.CacheData;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class StoreModule {

    @Provides
    @Singleton
    SharedPreferences providesPreferences(Application application){
        return application.getSharedPreferences("user", Context.MODE_PRIVATE);
    }

    @Provides
    @Singleton
    @Named("default")
    SharedPreferences providesDefaultSharedPreferences(Application application){
        return PreferenceManager.getDefaultSharedPreferences(application.getBaseContext());
    }

    @Provides
    @Singleton
    Register providesRegister(SharedPreferences sharedPreferences){
        return new Register(sharedPreferences);
    }

//    @Provides
//    @Singleton
//    LoadConversation providesLoadConversation(){
//        return new LoadConversation();
//    }

    @Provides
    @Singleton
    CacheData providesCacheData(){
        return new CacheData();
    }
}
