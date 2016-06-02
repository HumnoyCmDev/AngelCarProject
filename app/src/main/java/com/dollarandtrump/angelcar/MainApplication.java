package com.dollarandtrump.angelcar;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.activeandroid.ActiveAndroid;
import com.dollarandtrump.angelcar.manager.Contextor;
import com.dollarandtrump.daogenerator.DaoMaster;
import com.dollarandtrump.daogenerator.DaoSession;


/**
 * สร้างสรรค์ผลงานโดย humnoy ลงวันที่ 27/1/59.10:56น.
 * @AngelCarProject
 */
public class MainApplication extends Application{

    DaoSession mDaoSession;

    @Override
    public void onCreate() {
        super.onCreate();

        Contextor.getInstance().init(getApplicationContext());
        ActiveAndroid.initialize(this);

        setUpDatabase();

    }

    private void setUpDatabase() {
        DaoMaster.DevOpenHelper helper =
                new DaoMaster.DevOpenHelper(this,"AngelCarDB.db",null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        mDaoSession = daoMaster.newSession();
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

    }
}
