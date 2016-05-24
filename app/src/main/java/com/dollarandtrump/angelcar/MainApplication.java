package com.dollarandtrump.angelcar;

import android.app.Application;

import com.dollarandtrump.angelcar.manager.Contextor;


/**
 * Created by humnoy on 27/1/59.
 */
public class MainApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

        Contextor.getInstance().init(getApplicationContext());

    }

    @Override
    public void onTerminate() {
        super.onTerminate();

    }
}
