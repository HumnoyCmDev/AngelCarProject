package com.dollarandtrump.angelcar.manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.dollarandtrump.angelcar.dao.RegisterResultDao;

/**
 * Created by humnoy on 5/2/59.
 * - ข้อควรระวัง Singleton ผูกติดกับ Application
 * - ข้อมูลไม่ควรจะใหญ่ เกินไป
 * -**** Out Of Memory , Memory Leak ****
 */
public class Registration {

    private static Registration instance;

    public static Registration getInstance() {
        if (instance == null)
            instance = new Registration();
        return instance;
    }

    private  String REGISTRATION = "REGISTRATION";
    private  String REGISTRATION_FIRST_APP = "REGISTRATION_FIRST_APP";
    private  String REGISTRATION_USER_ID = "REGISTRATION_USER_ID";
    private  String REGISTRATION_SHOP_ID = "REGISTRATION_SHOP_ID";

    private Context mContext;
    private SharedPreferences preferences ;

    private Registration() {
        mContext = Contextor.getInstance().getContext();
        preferences = mContext.getSharedPreferences(REGISTRATION,mContext.MODE_PRIVATE);
    }

    public boolean isFirstApp() {
        return preferences.getBoolean(REGISTRATION_FIRST_APP,false);
    }

    public void save(RegisterResultDao gao){
        preferences.edit().putString(REGISTRATION_USER_ID, gao.getUserId()).apply();
        preferences.edit().putString(REGISTRATION_SHOP_ID, gao.getShopId()).apply();
        preferences.edit().putBoolean(REGISTRATION_FIRST_APP, true).apply();
    }

    public String getUserId(){
        return preferences.getString(REGISTRATION_USER_ID,null);
    }

    public String getShopRef(){
       return preferences.getString(REGISTRATION_SHOP_ID,null);
    }
}
