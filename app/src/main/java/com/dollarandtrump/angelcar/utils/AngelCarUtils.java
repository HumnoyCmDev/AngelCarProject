package com.dollarandtrump.angelcar.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.SpannableString;

import com.dollarandtrump.angelcar.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by humnoy on 10/3/59.
 */
public class AngelCarUtils {

    private static final int TIME_HOURS_24 = 24 * 60 * 60 * 1000;
    private static final SimpleDateFormat DAY_OF_WEEK = new SimpleDateFormat("EEE, LLL dd,", Locale.US);

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
        @SuppressLint("SimpleDateFormat") String time = new SimpleDateFormat("HH:mm:ss").format(date)+".";
        return timeBarDayText+time;
    }
}
