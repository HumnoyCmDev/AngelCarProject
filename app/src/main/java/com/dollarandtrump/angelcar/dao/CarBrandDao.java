package com.dollarandtrump.angelcar.dao;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by humnoyDeveloper on 19/4/59. 11:55
 */
@Parcel
public class CarBrandDao {
    @SerializedName("brandId") @Expose  int brandId;
    @SerializedName("brandName") @Expose  String brandName;

    public int getBrandId() {
        return brandId;
    }

    public void setBrandId(int brandId) {
        this.brandId = brandId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }
}
