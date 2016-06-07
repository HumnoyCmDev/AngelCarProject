package com.dollarandtrump.angelcar.manager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

import com.dollarandtrump.angelcar.dao.PostCarCollectionDao;
import com.dollarandtrump.angelcar.dao.PostCarDao;

import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;

/***************************************
 * สร้างสรรค์ผลงานดีๆ
 * โดย humnoy Android Developer
 * ลงวันที่ 29/2/59. เวลา 11:24
 ***************************************/
public class PostCarManager {

    private PostCarCollectionDao dao;
    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat dateFormat =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public PostCarManager() {
        //Load Cache
        loadCache();
    }

    public PostCarCollectionDao getDao() {
        return dao;
    }

    public void setDao(PostCarCollectionDao messageDao) {
        this.dao = messageDao;
        //Save Cache
        saveCache();
    }

    public int getCount(){
        if (dao == null) return 0;
        if (dao.getListCar() == null) return 0;

        return dao.getListCar().size();
    }
    
    public void insertDaoAtTopPosition(PostCarCollectionDao newDao){
        if (newDao != null && newDao.getListCar() != null) {

            if (dao == null)
                dao = new PostCarCollectionDao();
            if (dao.getListCar() == null)
                dao.setListCar(new ArrayList<PostCarDao>());
            dao.getListCar().addAll(0, newDao.getListCar());
            //Save Cache
            saveCache();
        }
    }

    public void appendDataToBottomPosition(PostCarCollectionDao newDao){
        if (newDao != null && newDao.getListCar() != null) {
            if (dao == null)
                dao = new PostCarCollectionDao();
            if (dao.getListCar() == null)
                dao.setListCar(new ArrayList<PostCarDao>());
            dao.getListCar().addAll(getCount(), newDao.getListCar());
            //Save Cache
            saveCache();
        }
    }

    public int getMaximumId(){
        if (dao == null)
            return 0;
        if (dao.getListCar().size() == 0)
            return 0;
        int maxId = dao.getListCar().get(0).getCarId();
        for (int i = 0; i < dao.getListCar().size(); i++)
            maxId = Math.max(maxId, dao.getListCar().get(i).getCarId());
        return maxId;
    }

    public int getMinimumId(){
        if (dao == null)
            return 0;
        if (dao.getListCar() == null)
            return 0;
        if (dao.getListCar().size() == 0)
            return 0;

        int minId = dao.getListCar().get(0).getCarId();
        for (int i = 0; i < dao.getListCar().size(); i++)
            minId = Math.min(minId, dao.getListCar().get(i).getCarId());
        return minId;
    }

   /* public String getAfterDate(){ //ของที่เก่ากว่า
        Date d = new Date();
        if (dao == null)
            return dateFormat.format(d);
        if (dao.getListCar().size() == 0)
            return dateFormat.format(d);

        Date afterDate = dao.getListCar().get(getCount()-1).getCarModifyTime();
        for (int i = 0; i < dao.getListCar().size(); i++) {
            if (afterDate.after(dao.getListCar().get(i).getCarModifyTime()))
                afterDate = dao.getListCar().get(i).getCarModifyTime();
        }
        return dateFormat.format(afterDate);
    }

    public String getBeforeDate(){ //ของที่ใหม่กว่า

        Date d = new Date();
        if (dao == null)
            return dateFormat.format(d);
        if (dao.getListCar().size() == 0)
            return dateFormat.format(d);

        Date beforeDate = dao.getListCar().get(0).getCarModifyTime();
        for (int i = 0; i < dao.getListCar().size(); i++) {
            if (beforeDate.before(dao.getListCar().get(i).getCarModifyTime()))
                beforeDate = dao.getListCar().get(i).getCarModifyTime();
        }

        return dateFormat.format(beforeDate);
    }*/

    public String firstDateDao(){
        if (dao == null)
            return "null";
        if (dao.getListCar().size() == 0)
            return "null";
        return dateFormat.format(dao.getListCar().get(0).getCarModifyTime());
    }

    public String lastDateDao(){
        if (dao == null)
            return "null";
        if (dao.getListCar().size() == 0)
            return "null";
        return dateFormat.format(dao.getListCar().get(getCount()-1).getCarModifyTime());
    }


    public Bundle onSaveInstanceState(){
        Bundle bundle = new Bundle();
        bundle.putParcelable("dao", Parcels.wrap(dao));
        return bundle;
    }

    public void onRestoreInstanceState(Bundle saveInstanceState){
        dao = Parcels.unwrap(saveInstanceState.getParcelable("dao"));
    }

    private void saveCache(){

        PostCarCollectionDao cacheDao =
                new PostCarCollectionDao();
        if (dao != null && dao.getListCar() != null) {
            LinkedList<PostCarDao> sub = new LinkedList<>(dao.getListCar().subList(0,
                    Math.min(20, dao.getListCar().size())));
            cacheDao.setListCar(sub);
        }

        //cache object
    }

    private void loadCache(){
//        load cache Object

    }

}
