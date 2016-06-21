package com.dollarandtrump.angelcar.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.SpannableString;

import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.manager.Contextor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by humnoy on 10/3/59.
 */
public class AngelCarUtils {

    private static final int TIME_HOURS_24 = 24 * 60 * 60 * 1000;
    private static final SimpleDateFormat DAY_OF_WEEK = new SimpleDateFormat("EEE dd LLL", new Locale("th","TH"));

    private AngelCarUtils() {
    }

    public static String formatLineUp(String s){
        return s.replaceAll("\n","<n>");
    }

    public static String convertLineUp(String s){
        return s.replaceAll("<n>","\n");
    }

    public static String convertSpace(String s){
        return s.replaceAll("<n>"," ");
    }

    @NonNull
    public static String subTopic(@NonNull String s){
        if (!s.contains("``")) return s;//false return string
        return s.substring(0,s.indexOf("``"));
    }

    @NonNull
    public static String subDetail(@NonNull String s){
        if (!s.contains("``")) return "";
        return convertLineUp(s.substring(s.indexOf("``")+2,s.length()));
    }

    public static String append(String topic, String detail){
        return topic+"``"+ formatLineUp(detail);
    }

    public static String textFormatHtml(String codeColor, String text){
        String textColor = "<font color=\"%s\">%s</font>";
        return String.format(textColor,codeColor,text);
    }

    public static String getFilesPath(Context context,Intent data){
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Uri selectedImage = data.getData();
        Cursor cursor = context.getContentResolver()
                .query(selectedImage, filePathColumn, null, null, null);
        assert cursor != null;
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        return picturePath;
    }

    public static String formatTimeDay(Context context, Date date) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        long todayMidnight = cal.getTimeInMillis();
        long yesterdayMidnight = todayMidnight - TIME_HOURS_24;
        long weekAgoMidnight = todayMidnight - TIME_HOURS_24 * 7;

        String timeBarDayText;
        if (date.getTime() > todayMidnight) {
            timeBarDayText = context.getString(R.string.time_today);
        } else if (date.getTime() > yesterdayMidnight) {
            timeBarDayText = context.getString(R.string.time_yesterday);
        } else if (date.getTime() > weekAgoMidnight) {
            cal.setTime(date);
            timeBarDayText = context.getResources().getStringArray(R.array.time_days_of_week)[cal.get(Calendar.DAY_OF_WEEK) - 1];
        } else {
            timeBarDayText = DAY_OF_WEEK.format(date);
        }
        return timeBarDayText;
    }

    public static String formatTimeAndDay(Context context, Date date) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        long todayMidnight = cal.getTimeInMillis();
        long yesterdayMidnight = todayMidnight - TIME_HOURS_24;
        long weekAgoMidnight = todayMidnight - TIME_HOURS_24 * 7;

        String timeBarDayText;
        if (date.getTime() > todayMidnight) {
            timeBarDayText = context.getString(R.string.time_today);
        } else if (date.getTime() > yesterdayMidnight) {
            timeBarDayText = context.getString(R.string.time_yesterday);
        } else if (date.getTime() > weekAgoMidnight) {
            cal.setTime(date);
            timeBarDayText = context.getResources().getStringArray(R.array.time_days_of_week)[cal.get(Calendar.DAY_OF_WEEK) - 1];
        } else {
            timeBarDayText = DAY_OF_WEEK.format(date);
        }
        @SuppressLint("SimpleDateFormat") String time = new SimpleDateFormat("HH:mm:ss").format(date)+" น.";
        return timeBarDayText+" "+time;
    }

    private Bitmap decodeFile(File f) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);
            // The new size we want to scale to
            final int REQUIRED_SIZE = 50;
            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while(o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }
            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {}
        return null;
    }

    public static boolean isConnectingToInternet(Context context){
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }

        }
        return false;
    }

    public static String IpAddress(Context context) {
        WifiManager wifiMan = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInf = wifiMan.getConnectionInfo();
        int ipAddress = wifiInf.getIpAddress();
        return String.format("%d.%d.%d.%d", (ipAddress & 0xff),(ipAddress >> 8 & 0xff),(ipAddress >> 16 & 0xff),(ipAddress >> 24 & 0xff));
    }
}
