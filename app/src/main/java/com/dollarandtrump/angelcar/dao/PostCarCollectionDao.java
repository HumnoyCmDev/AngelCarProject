package com.dollarandtrump.angelcar.dao;

import android.support.annotation.Nullable;
import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by humnoy on 10/3/59.
 */

@Parcel
public class PostCarCollectionDao implements Serializable {

    @SerializedName("rows") @Expose List<PostCarDao> listCar = new ArrayList<>();

    public List<PostCarDao> getListCar() {
        return listCar;
    }

    public void setListCar(List<PostCarDao> rows) {
        this.listCar = rows;
    }

    /*Insert all data*/
    public void insertAll(){
        if (getListCar() != null) {
            ActiveAndroid.beginTransaction();
            try {
                for (PostCarDao d : getListCar()) {
                    d.save();
                }
                ActiveAndroid.setTransactionSuccessful();
            } finally {
                ActiveAndroid.endTransaction();
            }
        }
    }

    public void deleteAll(){
       new Delete().from(PostCarDao.class).execute();
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

    // หาตำแหน่งหัวข้อยี่ห้อ
    public @Nullable List<Integer> findPositionHeader(){
        if ((this.listCar != null)
                && this.listCar.size() > 0) {
            ArrayList<String> listBrand = new ArrayList<>();
            for (PostCarDao d : this.listCar) {
                listBrand.add(d.getCarName());
            }
            if (listBrand.size() > 0) {
                Set<String> setToReturn = new HashSet<String>();
                List<Integer> set1 = new ArrayList<>();
                int i = 0;
                int next = 1;
                for (String s : listBrand) {
                    if (setToReturn.add(s)) {
                        if (i == 0) {
                            set1.add(i);// Header อันแรกต้องมีหัว
                            Log.d("PostDao", "findPositionHeader: " + i);
                        } else {
                            Log.d("PostDao", "findPositionHeader: " + (i + next));
                            set1.add(i + next);
                            next++;
                        }
                    }
                    i++;
                }
                return set1;
            }
        }
        return null;
    }
}
