package com.dollarandtrump.angelcar.module;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.dollarandtrump.angelcar.model.LoadConversation;

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
    Register providesRegister(SharedPreferences sharedPreferences){
        return new Register(sharedPreferences);
    }

    @Provides
    @Singleton
    LoadConversation providesLoadConversation(){
        return new LoadConversation();
    }
}
