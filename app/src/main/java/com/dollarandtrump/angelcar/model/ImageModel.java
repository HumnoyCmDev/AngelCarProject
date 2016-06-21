package com.dollarandtrump.angelcar.model;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.dollarandtrump.angelcar.utils.FileUtils;

import org.parceler.Parcel;

import java.io.File;

/********************************************
 * Created by HumNoy Developer on 13/6/2559.
 * @AngelCarProject
 * ผู้คร่ำหวอดในกวงการ Android มากกว่า 1 ปี
 ********************************************/
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

    public ImageModel(File fileImage, String index) {
        this.mFileImage = fileImage;
        this.mIndex = index;
    }

    public File getFileImage() {
        return mFileImage;
    }

    public void setFileImage(File fileImage) {
        this.mFileImage = fileImage;
    }

    public String getIndex() {
        return mIndex;
    }

    public void setIndex(String index) {
        this.mIndex = index;
    }

}
