package com.dollarandtrump.angelcar.dao;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by humnoyDeveloper on 26/3/59. 11:31
 */
public class FollowDao {
    @SerializedName("followid") @Expose int followId;
    @SerializedName("carref")   @Expose String carRef;
    @SerializedName("userref")  @Expose String userRef;

    public int getFollowId() {
        return followId;
    }

    public void setFollowId(int followId) {
        this.followId = followId;
    }

    public String getCarRef() {
        return carRef;
    }

    public void setCarRef(String carRef) {
        this.carRef = carRef;
    }

    public String getUserRef() {
        return userRef;
    }

    public void setUserRef(String userRef) {
        this.userRef = userRef;
    }
}
