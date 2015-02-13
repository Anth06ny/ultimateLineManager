package com.formation.utils;

import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by amonteiro on 13/02/2015.
 */
public class DateUtils {

    public enum DATE_FORMAT {
        ddMMyyyy, ddMMyyyy_HHmm, HHmm
    }

    /**
     *
     * @param context
     * @param date_format
     * @return le string pour le format voulu en fonction du context (et donc de la langue)
     */
    public static String getFormat(Context context, DATE_FORMAT date_format) {
        switch (date_format) {

            case ddMMyyyy:
                return context.getString(R.string.date_ddMMyyyy);
            case ddMMyyyy_HHmm:
                return context.getString(R.string.date_ddMMyyyy);
            case HHmm:
                return context.getString(R.string.date_ddMMyyyy);
            default:
                return context.getString(R.string.date_ddMMyyyy);
        }
    }

    /**
     * Parse un String en date
     * @param dateString
     * @return
     */
    public static Date stringToDate(String dateString, String dateFormat) {
        try {
            return new SimpleDateFormat(dateFormat).parse(dateString);
        }
        catch (ParseException e) {
            return null;
        }
    }

    /**
     * Affiche une date dans le format voulu
     * @param date
     * @param dateFormat
     * @return
     */
    public static String dateToString(Date date, String dateFormat) {
        return new SimpleDateFormat(dateFormat).format(date);
    }

    /**
     *
     * @param d1
     * @param d2
     * @return true si les 2 date sont du même jour
     */
    public static boolean isSameDay(Date d1, Date d2) {
        {
            if (d1 == null || d2 == null) {
                return false;
            }

            Calendar cal1 = Calendar.getInstance();
            Calendar cal2 = Calendar.getInstance();
            cal1.setTime(d1);
            cal2.setTime(d2);
            return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);

        }

    }
}
