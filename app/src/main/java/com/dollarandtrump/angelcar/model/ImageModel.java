package com.dollarandtrump.angelcar.model;

import android.content.Context;
import android.net.Uri;

import com.dollarandtrump.angelcar.utils.FileUtils;

import org.parceler.Parcel;

import java.io.File;


@Parcel
public class ImageModel {
    File mFileImage; // ตัดออก
    String mIndex;

    Uri uri;

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public File convertToFile(Context mContext){
        return FileUtils.getFile(mContext,uri);
    }

    public ImageModel() {
    }

    public ImageModel(Uri uri, String index) {
        this.uri = uri;
        this.mIndex = index;
    }

//    public File getFileImage() {
//        return mFileImage;
//    }

//    public void setFileImage(File fileImage) {
//        this.mFileImage = fileImage;
//    }

    public String getIndex() {
        return mIndex;
    }

    public void setIndex(String index) {
        this.mIndex = index;
    }

}
