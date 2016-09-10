package com.dollarandtrump.angelcar;

import android.app.Application;

import com.activeandroid.ActiveAndroid;
import com.crashlytics.android.Crashlytics;
import com.dollarandtrump.angelcar.component.ApplicationComponent;
import com.dollarandtrump.angelcar.component.DaggerApplicationComponent;
import com.dollarandtrump.angelcar.manager.Contextor;
import com.dollarandtrump.angelcar.module.AppModule;
import com.dollarandtrump.angelcar.module.NetworkModule;
import com.dollarandtrump.angelcar.module.StoreModule;
import com.dollarandtrump.angelcar.utils.Log;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MainApplication extends Application{

    private ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
//        Fabric.with(this, new Crashlytics());
        Log.setLoggingEnabled(true);
        Contextor.getInstance().init(getApplicationContext());
        ActiveAndroid.initialize(this);

        applicationComponent = DaggerApplicationComponent.builder().appModule(new AppModule(this))
                .networkModule(new NetworkModule())
                .storeModule(new StoreModule()).build();

        /** Realm Config **/
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this).build();
        Realm.setDefaultConfiguration(realmConfiguration);
    }

    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

    }

}
