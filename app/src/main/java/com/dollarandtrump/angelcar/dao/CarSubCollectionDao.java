package com.dollarandtrump.angelcar.dao;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

/**
 * Created by humnoyDeveloper on 19/4/59. 13:22
 */
@Parcel
public class CarSubCollectionDao {
    @SerializedName("rows") @Expose List<CarSubDao> carSubDao;

    public List<CarSubDao> getCarSubDao() {
        return carSubDao;
    }

    public void setCarSubDao(List<CarSubDao> carSubDao) {
        this.carSubDao = carSubDao;
    }
}
