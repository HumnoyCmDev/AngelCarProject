package com.dollarandtrump.angelcar.dao;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by humnoy on 16/2/59.
 */
@Parcel
@Deprecated
public class CarDataTypeDao {

    @SerializedName("cartype_sub")
    @Expose
    String carTypeSub;

    public String getCarTypeSub() {
        return carTypeSub;
    }

    public void setCarTypeSub(String carTypeSub) {
        this.carTypeSub = carTypeSub;
    }
}
