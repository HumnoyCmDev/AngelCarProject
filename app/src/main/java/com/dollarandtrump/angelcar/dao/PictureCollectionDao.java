
package com.dollarandtrump.angelcar.dao;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
public class PictureCollectionDao {

    @SerializedName("rows") @Expose
    public List<PictureDao> listPicture = new ArrayList<PictureDao>();

    public List<PictureDao> getListPicture() {
        return listPicture;
    }

    public void setListPicture(List<PictureDao> rows) {
        this.listPicture = rows;
    }

}
