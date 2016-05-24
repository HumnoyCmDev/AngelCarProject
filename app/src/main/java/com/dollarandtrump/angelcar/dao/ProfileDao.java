package com.dollarandtrump.angelcar.dao;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by humnoyDeveloper on 28/3/59. 15:12
 */
public class ProfileDao implements Serializable {

    @SerializedName("shopid")           @Expose int shopId;
    @SerializedName("shopname")         @Expose String shopName;
    @SerializedName("shopdescription")  @Expose String shopDescription;
    @SerializedName("shopnumber")       @Expose String shopNumber;
    @SerializedName("shoprank")         @Expose String shopRank;
    @SerializedName("shoplogo")         @Expose String shopLogo;
    @SerializedName("shopstatus")       @Expose String shopStatus;
    @SerializedName("userref")          @Expose String shopUserRef;
    @SerializedName("shopcreate")       @Expose String shopCreate;
    @SerializedName("shopview")         @Expose int shopView;
    @SerializedName("shopfollow")         @Expose int shopFollow;

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopDescription() {
        return shopDescription;
    }

    public void setShopDescription(String shopDescription) {
        this.shopDescription = shopDescription;
    }

    public String getShopNumber() {
        return shopNumber;
    }

    public void setShopNumber(String shopNumber) {
        this.shopNumber = shopNumber;
    }

    public String getShopRank() {
        return shopRank;
    }

    public void setShopRank(String shopRank) {
        this.shopRank = shopRank;
    }

    public String getShopLogo() {
        return shopLogo;
    }

    public void setShopLogo(String shopLogo) {
        this.shopLogo = shopLogo;
    }

    public String getShopStatus() {
        return shopStatus;
    }

    public void setShopStatus(String shopStatus) {
        this.shopStatus = shopStatus;
    }

    public String getShopUserRef() {
        return shopUserRef;
    }

    public void setShopUserRef(String shopUserRef) {
        this.shopUserRef = shopUserRef;
    }

    public String getShopCreate() {
        return shopCreate;
    }

    public void setShopCreate(String shopCreate) {
        this.shopCreate = shopCreate;
    }

    public int getShopView() {
        return shopView;
    }

    public void setShopView(int shopView) {
        this.shopView = shopView;
    }

    public int getShopFollow() {
        return shopFollow;
    }

    public void setShopFollow(int shopFollow) {
        this.shopFollow = shopFollow;
    }
}
