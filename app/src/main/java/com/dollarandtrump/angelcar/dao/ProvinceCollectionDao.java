package com.dollarandtrump.angelcar.dao;

import com.activeandroid.annotation.Column;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class ProvinceCollectionDao {

    @SerializedName("rows") @Expose List<ProvinceDao> dao = new ArrayList<>();

    public List<ProvinceDao> getDao() {
        return dao;
    }

    public void setDao(List<ProvinceDao> dao) {
        this.dao = dao;
    }
}
