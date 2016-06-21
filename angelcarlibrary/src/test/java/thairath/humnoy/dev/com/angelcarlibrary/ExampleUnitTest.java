package thairath.humnoy.dev.com.angelcarlibrary;

import android.content.Context;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
//    private static final int TIME_HOURS_24 = 24 * 60 * 60 * 1000;
//    private static final SimpleDateFormat DAY_OF_WEEK = new SimpleDateFormat("EEE, LLL dd,", Locale.US);
    static String[] days = {"อา.",
            "จ.",
            "อ.",
            "พ.",
            "พฤ.",
            "ศ.",
            "ส."};

    @Test
    public void addition_isCorrect() throws Exception {
//        assertEquals("วันนี้", formatTimeDay(new Date()));
        assertEquals(false,"s".equals("s"));
    }

//    public static String formatTimeDay(Date date) {
//        Calendar cal = Calendar.getInstance();
//        cal.set(Calendar.HOUR_OF_DAY, 0);
//        cal.set(Calendar.MINUTE, 0);
//        cal.set(Calendar.SECOND, 0);
//        long todayMidnight = cal.getTimeInMillis();
//        long yesterdayMidnight = todayMidnight - TIME_HOURS_24;
//        long weekAgoMidnight = todayMidnight - TIME_HOURS_24 * 7;
//
//        String timeBarDayText;
//        if (date.getTime() > todayMidnight) {
//            timeBarDayText = "วันนี้";
//        } else if (date.getTime() > yesterdayMidnight) {
//            timeBarDayText = "เมื่อวาน";
//        } else if (date.getTime() > weekAgoMidnight) {
//            cal.setTime(date);
//            timeBarDayText = days[cal.get(Calendar.DAY_OF_WEEK) - 1];
//        } else {
//            timeBarDayText = DAY_OF_WEEK.format(date);
//        }
//        return timeBarDayText;
//    }
}