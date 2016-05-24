
package com.dollarandtrump.angelcar.dao;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
public class PictureDao {

    @SerializedName("carimagepath")
    @Expose
    String carImagePath;

    public String getCarImagePath() {
        return carImagePath;
    }
    public String getCarImageThumbnailPath(){
        return "http://angelcar.com/ios/data/gadata/thumbnailcarimages/"+carImagePath;
    }
    public String getCarImageFullHDPath(){
        return "http://angelcar.com/ios/data/gadata/carimages/"+carImagePath;
    }

    public void setCarImagePath(String carImagePath) {
        this.carImagePath = carImagePath;
    }
}
