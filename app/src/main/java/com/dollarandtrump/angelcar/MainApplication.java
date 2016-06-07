package com.dollarandtrump.angelcar;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.dollarandtrump.angelcar.manager.Contextor;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.SaveCallback;


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

//        Parse.enableLocalDatastore(this);

        Parse.initialize(this,getString(R.string.parse_app_id),
                getString(R.string.parse_client_key));
//        Parse.initialize(this);

//        ParseObject testObject = new ParseObject("Player");
//        testObject.put("name","06-06-2559#3 LG");
//        testObject.saveInBackground();

        ParseInstallation.getCurrentInstallation().saveInBackground();
        ParsePush.subscribeInBackground("Installation", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Log.e("Parse", "done", e);
            }
        });

//
//        ParseUser.enableAutomaticUser();
//        ParseACL defaultACL = new ParseACL();
//        ParseACL.setDefaultACL(defaultACL, true);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

    }
}
