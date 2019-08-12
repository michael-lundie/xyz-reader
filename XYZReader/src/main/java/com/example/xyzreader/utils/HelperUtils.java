package com.example.xyzreader.utils;

import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public final class HelperUtils {

    private static final String LOG_TAG = HelperUtils.class.getSimpleName();

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

    /**
     *  Generates a semi-opaque version of a base color
     * @param baseColor Initial color from which to generate transparent version
     * @param transparency Value of opacity
     * @return integer representation of color value
     */
    public static int generateSemiOpaque(int baseColor, int transparency) {
        // Return RGB values (we are replacing alpha, so no need for that.
        // Docs: https://developer.android.com/reference/android/graphics/Color
        // Note that we can't reliably use Color api methods, since current minimum API is 19
        int R = (baseColor >> 16) & 0xff;
        int G = (baseColor >> 8) & 0xff;
        int B = (baseColor) & 0xff;

        // Create a new base color with same values, but applying semi-opaque alpha value

        return (transparency & 0xff) << 24 | (R & 0xff) << 16 | (G & 0xff) << 8 | (B & 0xff);
    }
}