package com.dollarandtrump.angelcar.dao;

import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by humnoy on 10/3/59.
 */
@Parcel
public class PostCarCollectionDao implements Serializable {
    @SerializedName("rows") @Expose List<PostCarDao> listCar;


    public List<PostCarDao> getListCar() {
        return listCar;
    }

    public void setListCar(List<PostCarDao> rows) {
        this.listCar = rows;
//        if ((this.listCar != null)
//                && this.listCar.size() > 0) {
//            listBrand = new ArrayList<>();
//            for (PostCarDao d : this.listCar){
//                listBrand.add(d.getCarName());
//            }
//        }

    }

    /*--------*/
    public void sortBrand(){
        if ((this.listCar != null)
                && this.listCar.size() > 0){
            Collections.sort(this.listCar, new Comparator<PostCarDao>() {
                @Override
                public int compare(PostCarDao lhs, PostCarDao rhs) {
                    return lhs.getCarName().compareTo(rhs.getCarName());
                }
            });
        }
    }


    // หาตำแหน่งหัวข้อยี่ห้อ
    public @Nullable List<Integer> findPositionHeader(){
        if ((this.listCar != null)
                && this.listCar.size() > 0) {
            ArrayList<String> listBrand = new ArrayList<>();
            for (PostCarDao d : this.listCar) {
                listBrand.add(d.getCarName());
            }
            if (listBrand.size() > 0) {
                Set<String> setToReturn = new HashSet<String>();
                List<Integer> set1 = new ArrayList<>();
                int i = 0;
                int next = 1;
                for (String s : listBrand) {
                    if (setToReturn.add(s)) {
                        if (i == 0) {
                            set1.add(i);// Header อันแรกต้องมีหัว
                            Log.d("PostDao", "findPositionHeader: " + i);
                        } else {
                            Log.d("PostDao", "findPositionHeader: " + (i + next));
                            set1.add(i + next);
                            next++;
                        }
                    }
                    i++;
                }
                return set1;
            }
        }
        return null;
    }

    public @Nullable List<String> findDuplicates() {
        if ((this.listCar != null)
                && this.listCar.size() > 0) {
            ArrayList<String> listBrand = new ArrayList<>();
            for (PostCarDao d : this.listCar) {
                listBrand.add(d.getCarName());
            }
            if (listBrand.size() > 0) {
                Set<String> setToReturn = new HashSet<String>();
                for (String s : listBrand) {
                    setToReturn.add(s);
//                    Log.d("PostDao", "findDuplicates: "+s);
                }
                return new ArrayList<>(setToReturn);
            }
        }
//        throw new NullPointerException("listBrand is null");
        return null;
    }



}
