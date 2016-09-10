package com.dollarandtrump.angelcar.utils;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;

import com.dollarandtrump.angelcar.manager.Contextor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReduceSizeImage {
    public static final int SIZE_BIG = 960;
    public static final int SIZE_SMALL = 640;
    private File file;
    private Context mContext;

    public ReduceSizeImage(File file) {
        this.file = file;

        mContext = Contextor.getInstance().getContext();
    }

    public File resizeImageFile(int requestSize){
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inScaled = false;
        o.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap decodeFile = BitmapFactory.decodeFile(file.getPath(), o);
        // resize
            int width = decodeFile.getWidth();
            int height = decodeFile.getHeight();
            Size newSize = solution(requestSize, width, height);
            Bitmap newBitmap = Bitmap.createScaledBitmap(decodeFile, newSize.getWidth(), newSize.getHeight(), true);
            Log.d("old width = "+ width +" ,old height = "+height);
            Log.d("new size width = "+ newBitmap.getWidth() +" , new size height = "+newBitmap.getHeight());

        try {

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            File dir = mContext.getCacheDir();
            File fileImage = File.createTempFile(timeStamp, ".jpg", dir);

//            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(fileImage);
            newBitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
            outputStream.flush();
            outputStream.close();
//            MediaStore.Images.Media.insertImage(mContext.getContentResolver(),fileImage.getAbsolutePath(),fileImage.getName(),fileImage.getName());
            return fileImage;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }

    public static Size solution(int baseSize,int width,int height){
        if (width > baseSize || height > baseSize) {
            int solution;
            if (width > height){ //แนวนอน
                solution = (height * baseSize)/width;
                Log.d("solution width > height = "+baseSize +" , "+solution);
//                return new int[]{baseSize,solution};
                return new Size(baseSize,solution);
            }else if (height > width){ // แนวตั้ง
                solution = (width * baseSize)/height;
                Log.d("solution width < height = "+solution +" , "+baseSize);
//                return new int[]{solution,baseSize};
                return new Size(solution,baseSize);
            }else { //สี่เหลียม
                Log.d("solution 1 : 1 = "+baseSize +" , "+baseSize);
//                return new int[]{baseSize,baseSize};
                return new Size(baseSize,baseSize);
            }
        }
        return new Size(width, height);
    }

    public static class Size {
        private int width;
        private int height;

        public Size(int width,int height) {
            this.height = height;
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }
    }

    /*public static Bitmap decodeFileToBitmap(File file) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            FileInputStream inputStream = new FileInputStream(file);
            BitmapFactory.decodeStream(inputStream, null, o);
            inputStream.close();

            // The new size we want to scale to
            final int REQUIRED_SIZE = 100;
            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while(o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }
            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            inputStream = new FileInputStream(file);
            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
            inputStream.close();
            return selectedBitmap;
        } catch (FileNotFoundException ignored) {} catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }*/
}
