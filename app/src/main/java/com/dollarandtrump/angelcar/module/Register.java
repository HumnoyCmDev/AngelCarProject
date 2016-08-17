package com.dollarandtrump.angelcar.module;


import android.content.SharedPreferences;

import com.dollarandtrump.angelcar.dao.RegisterResultDao;

import javax.inject.Inject;

public class Register {
    SharedPreferences sharedPreferences;

    public Register() {
    }

    public Register(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public void registerUser(RegisterResultDao dao){
       sharedPreferences.edit().putString("user",dao.getUserId()).apply();
       sharedPreferences.edit().putString("shop",dao.getShopId()).apply();
       sharedPreferences.edit().putBoolean("first_app",true).apply();
    }

    public boolean isFirstApp(){
        return sharedPreferences.getBoolean("first_app",false);
    }

    public String getUser(){
        return sharedPreferences.getString("user",null);
    }

    public String getShop(){
        return sharedPreferences.getString("shop",null);
    }

}
