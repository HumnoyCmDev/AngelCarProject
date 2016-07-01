package com.dollarandtrump.angelcar.manager;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.dollarandtrump.angelcar.dao.PostCarCollectionDao;
import com.dollarandtrump.angelcar.dao.PostCarDao;
import com.dollarandtrump.angelcar.utils.Cache;

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
    private Cache mCache;

    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat dateFormat =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public PostCarManager() {
        mCache = new Cache();
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

            if (dao.getListCar().size() > 0){
                for (int i = 0; i < newDao.getListCar().size(); i++) {
                    for (int j = 0; j < dao.getListCar().size(); j++) {
                        if (dao.getListCar().get(j).getCarId() ==
                                newDao.getListCar().get(i).getCarId()){
                            dao.getListCar().remove(j);
                        }
                    }
                }

            }

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

   /* public int getMaximumId(){
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

//            //cache object
//            CacheFeed.delete(CacheFeed.class,1);
//            ActiveAndroid.beginTransaction();
//            try {
//                for (PostCarDao d : cacheDao.getListCar()) {
//                    d.save();
//                    CacheFeed feed = new CacheFeed();
//                    feed.setPostCarDao(d);
//                    feed.save();
//                }
//                ActiveAndroid.setTransactionSuccessful();
//            } finally {
//                ActiveAndroid.endTransaction();
//            }
            mCache.save("postDao",cacheDao);
        }



    }

    private void loadCache(){
//        load cache Object
        if(mCache.isFile("postDao")){
            dao = mCache.load("postDao",PostCarCollectionDao.class);
        }

    }

}
