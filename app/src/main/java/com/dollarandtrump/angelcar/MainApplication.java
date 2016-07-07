package com.dollarandtrump.angelcar;

import android.app.Application;

import com.activeandroid.ActiveAndroid;
import com.dollarandtrump.angelcar.manager.Contextor;
import com.dollarandtrump.angelcar.utils.Log;

/**
 * สร้างสรรค์ผลงานโดย humnoy ลงวันที่ 27/1/59.10:56น.
 * @AngelCarProject
 */
public class MainApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        Log.setLoggingEnabled(true);
        Contextor.getInstance().init(getApplicationContext());
        ActiveAndroid.initialize(this);

    }

    @Override
    public void onTerminate() {
        super.onTerminate();

    }
}
