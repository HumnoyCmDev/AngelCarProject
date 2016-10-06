package com.dollarandtrump.angelcar.model;

import com.dollarandtrump.angelcar.dao.CarBrandDao;
import com.dollarandtrump.angelcar.dao.CarSubDao;
import com.dollarandtrump.angelcar.dao.PostCarDao;

import org.parceler.Parcel;

import java.util.HashMap;

/**
 * Created by humnoyDeveloper on 19/4/59. 13:45
 */
@Parcel
public class InfoCarModel {

    /**
    *กำหนด price ทุกครั้ง
    * */
    // edit post
    boolean isEditInfo = false;
    int category =-1 ;//0 = owner,1 = delegate,2 = dealers
    int resIdLogo; //logo
    int year = 0;//2016
    String gear = "NULL"; // mt = 1 , at = 2
    String priceStart = "0";
    String priceEnd = "0";
    String dateMore = "dateMore";

    //brand
    CarBrandDao brandDao;
    //sub
    CarSubDao subDao;
    //sub detail
    CarSubDao subDetailDao;

    Gallery gallery;

    PostCarDao postCarDao;

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public PostCarDao getPostCarDao() {
        return postCarDao;
    }

    public void setPostCarDao(PostCarDao postCarDao) {
        this.postCarDao = postCarDao;
    }

    public boolean isEditInfo() {
        return isEditInfo;
    }

    public void setEditInfo(boolean editInfo) {
        isEditInfo = editInfo;
    }

    public Gallery getGallery() {
        return gallery;
    }

    public void setGallery(Gallery gallery) {
        this.gallery = gallery;
    }

    public String getPriceStart() {
        return priceStart;
    }

    public void setPriceStart(String priceStart) {
        this.priceStart = priceStart.contentEquals("") ? "0" : priceStart;
    }

    public String getPriceEnd() {
        return priceEnd;
    }

    public void setPriceEnd(String priceEnd) {
        this.priceEnd = priceEnd.contentEquals("") ? "0" : priceEnd;
    }

    public String getDateMore() {
        return dateMore;
    }

    public void setDateMore(String dateMore) {
        this.dateMore = dateMore;
    }

    public int getResIdLogo() {
        return resIdLogo;
    }

    public void setResIdLogo(int resIdLogo) {
        this.resIdLogo = resIdLogo;
    }

    public String getGear() {
        return gear;
    }

    public void setGear(String gear) {
        this.gear = gear;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public CarBrandDao getBrandDao() {
        return brandDao;
    }

    public void setBrandDao(CarBrandDao brandDao) {
        this.brandDao = brandDao;
    }

    public CarSubDao getSubDao() {
        return subDao;
    }

    public void setSubDao(CarSubDao subDao) {
        this.subDao = subDao;
    }

    public CarSubDao getSubDetailDao() {
        return subDetailDao;
    }

    public void setSubDetailDao(CarSubDao subDetailDao) {
        this.subDetailDao = subDetailDao;
    }

    public void clear(){
        gear = "NULL";
        priceStart = "0";
        priceEnd = "0";
        dateMore = "dateMore";
        brandDao = null;
        subDao = null;
        subDetailDao = null;
    }

    //check case Filter
    public HashMap<String,String> getMapFilter(){
        HashMap<String,String> map = new HashMap<>();

        if (brandDao != null){
            map.put("carname",String.valueOf(brandDao.getBrandId()));
        }
        if (subDao != null){
            map.put("cartype_sub",String.valueOf(subDao.getSubId()));
        }
        if (subDetailDao != null){
            map.put("cardetail_sub",String.valueOf(subDetailDao.getSubId()));
        }
        // check price
        if (!priceStart.contains("0") && !priceEnd.contains("0")){
            map.put("pricestart",priceStart);
            map.put("priceend",priceEnd);
        }
        // check year
        if (year > 0){
            map.put("year",String.valueOf(year));
        }
        // check gear
        if (!gear.contains("NULL")){
            map.put("gear",gear);
        }

        if (!dateMore.contains("dateMore")){
            map.put("datemore",dateMore);
        }
        return map;
    }

}
