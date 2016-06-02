package com.dollarandtrump.angelcar.manager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.dollarandtrump.angelcar.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * สร้างสรรค์ผลงานโดย humnoyDeveloper ลงวันที่ 31/5/59.16:26น.
 *
 * @AngelCarProject
 */
public class PhotoLoad {

    private PhotoCache photoCache;

    public PhotoLoad() {
        photoCache = new PhotoCache();
    }

    public void loadBitmap(String pathImage,Observer<Bitmap> bitmapObserver) {
        final File f = new File(pathImage);
        final String imageKey = f.getName();

        final Bitmap bitmap = photoCache.getBitmapFromCache(imageKey);
        if (bitmap != null) {
            if (bitmapObserver != null)
                bitmapObserver.onNext(bitmap);
        } else {
            Observable<Bitmap> observer = Observable.create(new Observable.OnSubscribe<Bitmap>() {
                @Override
                public void call(Subscriber<? super Bitmap> subscriber) {
                    try {
                        // Decode image size
                        BitmapFactory.Options o = new BitmapFactory.Options();
                        o.inJustDecodeBounds = true;
                        BitmapFactory.decodeStream(new FileInputStream(f), null, o);
                        // The new size we want to scale to
                        final int REQUIRED_SIZE = 50;
                        // Find the correct scale value. It should be the power of 2.
                        int scale = 1;
                        while (o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                                o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                            scale *= 2;
                        }
                        // Decode with inSampleSize
                        BitmapFactory.Options o2 = new BitmapFactory.Options();
                        o2.inSampleSize = scale;

                        Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
                        photoCache.addBitmapToCache(imageKey,b);
                        subscriber.onNext(b);
                        subscriber.onCompleted();
                    } catch (FileNotFoundException e) {
                        subscriber.onError(e);
                    }
                }
            }).subscribeOn(Schedulers.newThread())
              .observeOn(AndroidSchedulers.mainThread());

            if (bitmapObserver != null)
                observer.subscribe(bitmapObserver);

        }
    }
}
