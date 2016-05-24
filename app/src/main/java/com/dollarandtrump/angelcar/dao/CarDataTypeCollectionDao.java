package com.dollarandtrump.angelcar.dao;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

/**
 * Created by humnoy on 16/2/59.
 */
@Parcel
@Deprecated
public class CarDataTypeCollectionDao {
    @SerializedName("rows")
    @Expose
    List<CarDataTypeDao> rows;

    public List<CarDataTypeDao> getRows() {
        return rows;
    }

    public void setRows(List<CarDataTypeDao> rows) {
        this.rows = rows;
    }
}
