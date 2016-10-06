package com.dollarandtrump.angelcar.dao;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kotlin
 **/
public class DealerCollection {
    @SerializedName("shop") @Expose List<ShopDealerDao> dealers = new ArrayList<>();

    public List<ShopDealerDao> getDealers() {
        return dealers;
    }

    public void setDealers(List<ShopDealerDao> dealers) {
        this.dealers = dealers;
    }
}
