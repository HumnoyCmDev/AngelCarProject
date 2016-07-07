package com.dollarandtrump.angelcar.cache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import rx.Observable;
import rx.Subscriber;

/**
 * สร้างสรรค์ผลงานโดย humnoyDeveloper ลงวันที่ 31/5/59.16:26น.
 *
 * @AngelCarProject
 */
public class PhotoLoadCache {

    private static PhotoLoadCache instance;

    public static PhotoLoadCache getInstance() {
        if (instance == null)
            instance = new PhotoLoadCache();
        return instance;
    }




    private PhotoCache photoCache;
    private PhotoLoadCache() {
        photoCache = new PhotoCache();
    }

    public Observable<Bitmap> loadBitmap(final File file, final int requiredSize) {
        return Observable.create(new Observable.OnSubscribe<Bitmap>() {
            @Override
            public void call(Subscriber<? super Bitmap> subscriber) {
                final String imageKey = file.getName();
                Bitmap bitmap = photoCache.getBitmapFromCache(imageKey);
                if (bitmap != null) {
                        Log.d("c", "loadBitmap: not null");
                    subscriber.onNext(bitmap);
                    subscriber.onCompleted();
                } else {
                    try {
                        // Decode image size
                        BitmapFactory.Options o = new BitmapFactory.Options();
                        o.inJustDecodeBounds = true;
                        BitmapFactory.decodeStream(new FileInputStream(file), null, o);
                        // The new size we want to scale to
//                        final int REQUIRED_SIZE = 300;
                        // Find the correct scale value. It should be the power of 2.
                        int scale = 1;
                        while (o.outWidth / scale / 2 >= requiredSize &&
                                o.outHeight / scale / 2 >= requiredSize) {
                            scale *= 2;
                        }
                        // Decode with inSampleSize
                        BitmapFactory.Options o2 = new BitmapFactory.Options();
                        o2.inSampleSize = scale;

                        Bitmap b = BitmapFactory.decodeStream(new FileInputStream(file), null, o2);
                        photoCache.addBitmapToCache(imageKey,b);
                        Log.d("c", "loadBitmap: null");
                        subscriber.onNext(b);
                        subscriber.onCompleted();
                    } catch (FileNotFoundException e) {
                        subscriber.onError(e);
                    }
                }

            }
        });





    }
}
