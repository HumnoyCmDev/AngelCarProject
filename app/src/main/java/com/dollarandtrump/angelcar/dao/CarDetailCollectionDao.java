package com.dollarandtrump.angelcar.dao;

/**
 package com.beta.cls.angelcar;

 /**
 * Created by ABaD on 12/29/2015.
 */


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

@Parcel
@Deprecated
public class CarDetailCollectionDao {

    @SerializedName("rows") @Expose
    List<CarDetailDao> rows;


    public List<CarDetailDao> getRows() {
        return rows;
    }

    public void setPosts(List<CarDetailDao> rows) {
        this.rows = rows;
    }
}