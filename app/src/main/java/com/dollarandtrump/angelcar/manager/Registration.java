package com.dollarandtrump.angelcar.manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.dollarandtrump.angelcar.dao.RegisterResultDao;


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

    public void save(RegisterResultDao dao){
        if (dao != null) {
            preferences.edit().putString(REGISTRATION_USER_ID, dao.getUserId()).apply();
            preferences.edit().putString(REGISTRATION_SHOP_ID, dao.getShopId()).apply();
            preferences.edit().putBoolean(REGISTRATION_FIRST_APP, true).apply();
        }
    }

    public void setUserShop(String user,String shop){
        preferences.edit().putString(REGISTRATION_USER_ID, user).apply();
        preferences.edit().putString(REGISTRATION_SHOP_ID, shop).apply();
    }

    public void setUserOld(){
        String userOld = getUserId();
        preferences.edit().putString("userold",userOld).apply();
    }

    public String getUserOld(){
      return preferences.getString("userold",null);
    }

    public void firstApp(boolean b){
        preferences.edit().putBoolean(REGISTRATION_FIRST_APP, b).apply();
    }

//    public void saveToken(String token){
//        preferences.edit().putString("token", token).apply();
//    }

    public void setIsSignIn(boolean isSignIn){
        preferences.edit().putBoolean("signin", isSignIn).apply();
    }
    public boolean isSignIn(){
        return preferences.getBoolean("signin",false);
    }

//    public String getToken(){
//        return preferences.getString("token",null);
//    }

    public void clear(){
//        preferences.edit().clear().apply();
        preferences.edit().remove(REGISTRATION_USER_ID).apply();
        preferences.edit().remove(REGISTRATION_SHOP_ID).apply();
        preferences.edit().remove(REGISTRATION_FIRST_APP).apply();
        preferences.edit().remove("signin").apply();
        preferences.edit().remove("userold").apply();
    }

    public String getUserId(){
        return preferences.getString(REGISTRATION_USER_ID,null);
    }

    public String getShopRef(){
       return preferences.getString(REGISTRATION_SHOP_ID,null);
    }
}
