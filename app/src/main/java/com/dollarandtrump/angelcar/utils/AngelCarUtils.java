package com.dollarandtrump.angelcar.utils;

import android.graphics.Color;
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

    public static Post getPostCollection(String s){
        Post post = new Post();
        post.setTopic(subTopic(s));
        post.setDetail(convertLineUp(subDetail(s)));
        return post;
    }

    public static String textFormatHtml(String codeColor, String text){
        String textColor = "<font color=\"%s\">%s</font>";
        return String.format(textColor,codeColor,text);
    }

    public static class Post{
        private String topic;
        private String detail;
        public String getTopic() {
            return topic;
        }

        public void setTopic(String topic) {
            this.topic = topic;
        }

        public String getDetail() {
            return detail;
        }

        public void setDetail(String detail) {
            this.detail = detail;
        }
    }
}
