package com.dollarandtrump.angelcar.dao;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by humnoyDeveloper on 28/3/59. 15:12
 */
@Parcel(value = Parcel.Serialization.FIELD, analyze = ProfileDao.class)
@Table(name = "Profile")
public class ProfileDao extends Model /*implements Serializable*/ {

    @SerializedName("shopid")           @Expose @Column(name = "ShopId") int shopId;
    @SerializedName("shopname")         @Expose @Column(name = "ShopName") String shopName;
    @SerializedName("shopdescription")  @Expose @Column(name = "ShopDescription") String shopDescription;
    @SerializedName("shopnumber")       @Expose @Column(name = "ShopNumber") String shopNumber;
    @SerializedName("shoprank")         @Expose @Column(name = "ShopRank") String shopRank;
    @SerializedName("shoplogo")         @Expose @Column(name = "ShopLogo") String shopLogo;
    @SerializedName("shopstatus")       @Expose @Column(name = "ShopStatus") String shopStatus;
    @SerializedName("userref")          @Expose @Column(name = "ShopUserRef") String shopUserRef;
    @SerializedName("shopcreate")       @Expose @Column(name = "ShopCreate") String shopCreate;
    @SerializedName("shopview")         @Expose @Column(name = "ShopView") int shopView;
    @SerializedName("shopfollow")       @Expose @Column(name = "ShopFollow") int shopFollow;
    @SerializedName("profile_path")     @Expose @Column(name = "ProfilePathImage") List<String> profilePath = new ArrayList<>();


    public List<String> getProfilePath() {
        return profilePath;
    }

    public void setProfilePath(List<String> profilePath) {
        this.profilePath = profilePath;
    }

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

    public String getUrlShopLogo() {
        return "http://angelcar.com/ios/data/clsdata/"+shopLogo;
    }

    public String getUrlShopBackground(int id){
        return "http://angelcar.com/ios/data/clsdata/"+profilePath.get(id);
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
