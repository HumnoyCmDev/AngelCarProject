package com.dollarandtrump.angelcar.utils;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.SpannableString;

/**
 * Created by humnoy on 10/3/59.
 */
public class AngelCarUtils {

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
}
