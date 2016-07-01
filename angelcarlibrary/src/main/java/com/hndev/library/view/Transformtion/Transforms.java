package com.hndev.library.view.Transformtion;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;

/********************************************
 * Created by HumNoy Developer on 29/6/2559.
 * ผู้คร่ำหวอดในกวงการ Android มากกว่า 1 ปี
 * AngelCarProject
 ********************************************/
public class Transforms {
    public static Bitmap circle(Bitmap source){
        int srcWidth = source.getWidth();
        int srcHeight = source.getHeight();
        float srcRadius = (float) Math.min(srcWidth, srcHeight) / 2f;
        Bitmap out = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(out);
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, out.getWidth(), out.getHeight());

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.WHITE);
        canvas.drawCircle(srcRadius, srcRadius, srcRadius, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(source, rect, rect, paint);
        source.recycle();
        return out;
    }
}
