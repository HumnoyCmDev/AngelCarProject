package com.dollarandtrump.angelcar.dao;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by humnoyDeveloper on 28/3/59. 15:19
 */
public class ShopCollectionDao implements Serializable {
    @SerializedName("profile")  @Expose ProfileDao profileDao;
    @SerializedName("car")      @Expose List<PostCarDao> postCarDao = new ArrayList<>();

    public ProfileDao getProfileDao() {
        return profileDao;
    }

    public void setProfileDao(ProfileDao profileDao) {
        this.profileDao = profileDao;
    }

    public List<PostCarDao> getPostCarDao() {
        return postCarDao;
    }

    public void setPostCarDao(List<PostCarDao> postCarDao) {
        this.postCarDao = postCarDao;
    }
}



