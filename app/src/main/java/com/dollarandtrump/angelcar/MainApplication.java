package com.dollarandtrump.angelcar;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.dollarandtrump.angelcar.manager.Contextor;
import com.google.firebase.messaging.FirebaseMessagingService;

/**
 * สร้างสรรค์ผลงานโดย humnoy ลงวันที่ 27/1/59.10:56น.
 * @AngelCarProject
 */
public class MainApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

        Contextor.getInstance().init(getApplicationContext());
        ActiveAndroid.initialize(this);

    }

    @Override
    public void onTerminate() {
        super.onTerminate();

    }
}
