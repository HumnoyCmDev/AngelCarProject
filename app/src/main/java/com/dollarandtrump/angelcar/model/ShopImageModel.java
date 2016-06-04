package com.dollarandtrump.angelcar.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * สร้างสรรค์ผลงานโดย humnoyDeveloper ลงวันที่ 3/6/59.11:36น.
 *
 * @AngelCarProject
 */
public class ShopImageModel {
    private int max = 3;
    private List<ImageModel> imageModels = new ArrayList<>();

    public ShopImageModel() {

    }

    public List<ImageModel> getImageModels() {
        return imageModels;
    }

    public void setImageModels(List<ImageModel> imageModels) {
        this.imageModels = imageModels;
    }

    public static class ImageModel {
        private File fileImage;
        private String index;

        public ImageModel() {
        }

        public ImageModel(File fileImage, String index) {
            this.fileImage = fileImage;
            this.index = index;
        }

        public File getFileImage() {
            return fileImage;
        }

        public void setFileImage(File fileImage) {
            this.fileImage = fileImage;
        }

        public String getIndex() {
            return index;
        }

        public void setIndex(String index) {
            this.index = index;
        }

//        public ShopImageModel get(){
//            return new ShopImageModel();
//        }
    }

}
