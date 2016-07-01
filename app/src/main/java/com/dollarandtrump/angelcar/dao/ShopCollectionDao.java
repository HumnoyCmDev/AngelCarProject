package com.dollarandtrump.angelcar.dao;

import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.dollarandtrump.angelcar.model.CacheShop;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by humnoyDeveloper on 28/3/59. 15:19
 */
@Parcel
public class ShopCollectionDao /*implements Serializable*/ {
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

    public PostCarCollectionDao getPostCarCollection(){
        PostCarCollectionDao dao = new PostCarCollectionDao();
        dao.setListCar(postCarDao);
        return dao;
    }

    /*Insert all data*/
    public void insertAll(){
        if (profileDao != null){
            profileDao.save();
        }
        if (postCarDao != null) {
            ActiveAndroid.beginTransaction();
            try {
                for (PostCarDao d : getPostCarDao()) {
                    d.save();
                    CacheShop cacheShop = new CacheShop();
                    cacheShop.setPostCarDao(d);
                    cacheShop.setProfileDao(profileDao);
                    cacheShop.save();
                }
                ActiveAndroid.setTransactionSuccessful();
            } finally {
                ActiveAndroid.endTransaction();
            }
        }
    }
    public void deleteAll(){
        new Delete().from(CacheShop.class).execute();
        new Delete().from(PostCarDao.class).execute();
        new Delete().from(ProfileDao.class).execute();
    }

    public PostCarCollectionDao queryPostCar(){//all
        List<PostCarDao> model = new Select().from(PostCarDao.class)
                .orderBy("BrandName ASC").execute();
        PostCarCollectionDao newDao = new PostCarCollectionDao();
        newDao.setListCar(model);
        return newDao;
    }

    public PostCarCollectionDao queryFindBrandDuplicates(){
        List<PostCarDao> model = new Select().from(PostCarDao.class)
                .groupBy("BrandName").having("COUNT(BrandName) > 0")
                .orderBy("BrandName ASC").execute();
        PostCarCollectionDao newDao = new PostCarCollectionDao();
        newDao.setListCar(model);
        return newDao;
    }

    public PostCarCollectionDao findPostCar(String brandName){
        List<PostCarDao> model = new Select().from(PostCarDao.class)
                .where("BrandName LIKE ?",brandName).execute();
        PostCarCollectionDao newDao = new PostCarCollectionDao();
        newDao.setListCar(model);
        return newDao;
    }
}



