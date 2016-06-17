package com.dollarandtrump.angelcar.model;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

/********************************************
 * Created by HumNoy Developer on 13/6/2559.
 * @AngelCarProject
 * ผู้คร่ำหวอดในกวงการ Android มากกว่า 1 ปี
 ********************************************/
@Parcel
public class Gallery {
    private List<ImageModel> listGallery = new ArrayList<>();

    public List<ImageModel> getListGallery() {
        return listGallery;
    }

    public void setListGallery(ImageModel modelGallery) {
        this.listGallery.add(getCount(),modelGallery);
    }

    public boolean removeItem(int position){
        if (listGallery.size() > 0){
            listGallery.remove(position);
            return true;
        }
        return false;
    }

    public void addItem(ImageModel model){
       if (listGallery.size() > 0){
        listGallery.add(listGallery.size(),model);
       }
    }

    public int getCount(){
        if (listGallery.size() > 0 ){
            return listGallery.size();
        }
        return 0;
    }

    public void clearGallery(){
        if (listGallery.size() > 0){
            listGallery.clear();
        }
    }
}
