package com.dollarandtrump.angelcar.dao;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kotlin
 **/
public class ShopDealerDao  {

    @SerializedName("shopid")           @Expose int shopId;
    @SerializedName("shopname")         @Expose String shopName;
    @SerializedName("shopdescription")  @Expose String shopDescription;
    @SerializedName("shopnumber")       @Expose String shopNumber;
    @SerializedName("shoprank")         @Expose String shopRank;
    @SerializedName("shoplogo")         @Expose String shopLogo;
    @SerializedName("shopstatus")       @Expose String shopStatus;
    @SerializedName("userref")          @Expose String userRef;
    @SerializedName("shopcreate")       @Expose String shopCreate;
    @SerializedName("shopview")         @Expose int shopView;
    @SerializedName("shopfollow")       @Expose int shopFollow;
    @SerializedName("shopviewtype")     @Expose String shopViewType;
    @SerializedName("car")              @Expose List<String> car = new ArrayList<>();

    public String getShopCreate() {
        return shopCreate;
    }

    public void setShopCreate(String shopCreate) {
        this.shopCreate = shopCreate;
    }

    public String getShopDescription() {
        return shopDescription;
    }

    public void setShopDescription(String shopDescription) {
        this.shopDescription = shopDescription;
    }

    public int getShopFollow() {
        return shopFollow;
    }

    public void setShopFollow(int shopFollow) {
        this.shopFollow = shopFollow;
    }

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public String getShopLogo() {
        return shopLogo;
    }

    public void setShopLogo(String shopLogo) {
        this.shopLogo = shopLogo;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
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

    public String getShopStatus() {
        return shopStatus;
    }

    public void setShopStatus(String shopStatus) {
        this.shopStatus = shopStatus;
    }

    public String getUserRef() {
        return userRef;
    }

    public void setUserRef(String shopUserRef) {
        this.userRef = shopUserRef;
    }

    public int getShopView() {
        return shopView;
    }

    public void setShopView(int shopView) {
        this.shopView = shopView;
    }

    public String getShopViewType() {
        return shopViewType;
    }

    public void setShopViewType(String shopViewType) {
        this.shopViewType = shopViewType;
    }

    public int getCurrentCar(){
        return car.size();
    }
}
