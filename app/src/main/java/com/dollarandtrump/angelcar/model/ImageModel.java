package com.dollarandtrump.angelcar.model;

import java.io.File;

/********************************************
 * Created by HumNoy Developer on 13/6/2559.
 * @AngelCarProject
 * ผู้คร่ำหวอดในกวงการ Android มากกว่า 1 ปี
 ********************************************/
public class ImageModel {
    private File mFileImage;
    private String mIndex;

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
