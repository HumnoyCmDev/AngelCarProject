package com.dollarandtrump.angelcar.dao;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
@Deprecated
public class CarDetailDao {

    @SerializedName("cardetail_sub")
    @Expose
    String carDetailSub;

    public String getCarDetailSub() {
        return carDetailSub;
    }

    public void setCarDetailSub(String carDetailSub) {
        this.carDetailSub = carDetailSub;
    }
}
