package com.dollarandtrump.angelcar.dao;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by humnoyDeveloper on 19/4/59. 13:23
 */
@Parcel
public class CarSubDao {
    @SerializedName("subId") @Expose  int subId;
    @SerializedName("subName") @Expose  String subName;

    public CarSubDao() {
    }

    public CarSubDao(int subId, String subName) {
        this.subId = subId;
        this.subName = subName;
    }

    public int getSubId() {
        return subId;
    }

    public void setSubId(int subId) {
        this.subId = subId;
    }

    public String getSubName() {
        return subName;
    }

    public void setSubName(String subName) {
        this.subName = subName;
    }
}
