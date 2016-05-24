package com.dollarandtrump.angelcar.dao;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by humnoyDeveloper on 23/3/59. 13:48
 */
public class RegisterResultDao {
    @SerializedName("userid") @Expose String userId;
    @SerializedName("shopid") @Expose String shopId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }
}
