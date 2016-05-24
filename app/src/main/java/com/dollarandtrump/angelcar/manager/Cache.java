package com.dollarandtrump.angelcar.manager;

import android.content.Context;

import com.dollarandtrump.angelcar.manager.Contextor;
import com.dollarandtrump.angelcar.utils.T;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.UUID;

/**
 * Created by humnoyDeveloper on 25/4/59. 11:19
 */
public class Cache {
    private Context mContext;

    public Cache() {
        mContext = Contextor.getInstance().getContext();
    }

    public String getUUIdCode(){
        return UUID.randomUUID().toString();
    }

    public boolean save(String key,Object object){
        File suspend_f = new File(mContext.getCacheDir(),key);
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        boolean keep = true;
        try {
            fos = new FileOutputStream(suspend_f);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(object);
        } catch (IOException e) {
            e.printStackTrace();
            keep = false;
        } finally {
            try {
                if (oos != null) oos.close();
                if (fos != null) fos.close();
                if (!keep) suspend_f.delete();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return keep;
    }



    public <T> T load(String key,Class<T> clazz) {
        File suspend_f = new File(mContext.getCacheDir(),key);
        Object object = null;
        FileInputStream fis = null;
        ObjectInputStream is = null;

        try {
            fis = new FileInputStream(suspend_f);
            is = new ObjectInputStream(fis);
            object = is.readObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (OptionalDataException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) fis.close();
                if (is != null) is.close();
            }catch (Exception e){}
        }
        return clazz.cast(object);
    }

    // clear cache
    public void clearCache(){
        File cacheDir = mContext.getCacheDir();
        File[] files = cacheDir.listFiles();
        if (files != null) {
            for (File file : files) {
                file.delete();
            }
        }
    }

    public void deleteCache(String key){
        File cacheDir = mContext.getCacheDir();
        File[] files = cacheDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getName().equals(key)){
                    file.delete();
                }
            }
        }
    }

    public File[] getFiles(){
        File cacheDir = mContext.getCacheDir();
        return cacheDir.listFiles();
    }

    public boolean isFile(String fileName){
        for (File f : getFiles()){
            if (f.getName().equals(fileName))
                return true;

        }
        return false;
    }

}
