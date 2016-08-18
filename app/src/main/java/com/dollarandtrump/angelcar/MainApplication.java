package com.dollarandtrump.angelcar;

import android.app.Application;

import com.activeandroid.ActiveAndroid;
import com.crashlytics.android.Crashlytics;
import com.dollarandtrump.angelcar.component.DaggerStoreComponent;
import com.dollarandtrump.angelcar.component.StoreComponent;
import com.dollarandtrump.angelcar.manager.Contextor;
import com.dollarandtrump.angelcar.module.AppModule;
import com.dollarandtrump.angelcar.module.StoreModule;
import com.dollarandtrump.angelcar.utils.Log;

import io.fabric.sdk.android.Fabric;

public class MainApplication extends Application{

    private StoreComponent storeComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        Log.setLoggingEnabled(true);
        Contextor.getInstance().init(getApplicationContext());
        ActiveAndroid.initialize(this);

        storeComponent = DaggerStoreComponent.builder().appModule(new AppModule(this))
                .storeModule(new StoreModule()).build();
    }

    public StoreComponent getStoreComponent() {
        return storeComponent;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

    }

}
