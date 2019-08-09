package com.example.xyzreader.utils;

import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public final class FormatUtils {

    private static final String LOG_TAG = FormatUtils.class.getSimpleName();

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss");
    // Use default locale format
    private static final SimpleDateFormat outputFormat = new SimpleDateFormat();
    // Most time functions can only handle 1902 - 2037
    private static final GregorianCalendar START_OF_EPOCH = new GregorianCalendar(2,1,1);

    public static String formatDate(String date) {
        Date publishedDate = parseDate(date);

        if (!publishedDate.before(START_OF_EPOCH.getTime())) {
            return Html.fromHtml(
                    DateUtils.getRelativeTimeSpanString(
                            publishedDate.getTime(),
                            System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                            DateUtils.FORMAT_ABBREV_ALL).toString()).toString();
        } else {
            // If date is before 1902, just show the string
            return outputFormat.format(publishedDate);
        }
    }

    private static Date parseDate(String date) {
        try {
            return dateFormat.parse(date);
        } catch (ParseException exception) {
            Log.e(LOG_TAG, exception.getMessage());
            Log.i(LOG_TAG, "passing today's date");
            return new Date();
        }
    }
}