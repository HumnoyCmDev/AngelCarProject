package com.dollarandtrump.angelcar.utils;

import android.graphics.Bitmap;
import android.util.LruCache;

public class PhotoCache {



    //Get max available memory, stored in KB
    private final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
    private final int cacheSize = maxMemory / 16;

    private LruCache<String, Bitmap> mPhotoCache;

    public PhotoCache(){
        this.mPhotoCache = new LruCache<>(cacheSize);
    }

    public void addBitmapToCache(String key, Bitmap bitmap){
        if(getBitmapFromCache(key) == null){
            mPhotoCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromCache(String key){
        return mPhotoCache.get(key);
    }

    public boolean isCached(String key){
        if(key != null && mPhotoCache.get(key) != null){
                return true;
            } else {
                return false;
            }
    }
}
