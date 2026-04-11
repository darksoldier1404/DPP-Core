package com.darksoldier1404.dppc.utils;

import com.darksoldier1404.dppc.annotation.DPPCoreVersion;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@DPPCoreVersion(since = "5.4.0")
public class TimeUtils {
    private static final String[] SUPPORTED_FORMATS = new String[]{
            "yyyy-MM-dd HH:mm:ss",
            "yyyy/MM/dd HH:mm:ss",
            "yyyy-MM-dd HH:mm",
            "yyyy/MM/dd HH:mm",
            "yyyy-MM-dd",
            "yyyy/MM/dd",
            "yyyyMMdd",
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-dd'T'HH:mm:ss.SSS",
            "HH:mm:ss",
            "HH:mm",
            "HHmmss",
            "HHmm"
    };
    private static final SimpleDateFormat sdf = new SimpleDateFormat(SUPPORTED_FORMATS[0]);

    /**
     * Convert a string to a Date object (default formats).
     *
     * @param dateStr Date string (yyyy-MM-dd HH:mm:ss)
     * @return Date object
     * @throws Exception When parsing fails
     */
    public static Date parseDate(String dateStr) throws Exception {
        Exception last = null;
        for (String format : SUPPORTED_FORMATS) {
            try {
                return new SimpleDateFormat(format).parse(dateStr);
            } catch (Exception e) {
                last = e;
            }
        }
        if (last != null) {
            throw last;
        }
        throw new Exception("Unsupported date format");
    }

    /**
     * Convert a Date object to a string (default format).
     *
     * @param date Date object
     * @return Formatted string
     */
    public static String formatDate(Date date) {
        return sdf.format(date);
    }

    /**
     * Create a Calendar instance for calculating differences between two Date objects.
     *
     * @param date Date object
     * @return Calendar instance
     */
    private static Calendar toCalendar(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    /**
     * Calculate years passed between two instants.
     *
     * @param start Start instant
     * @param end   End instant (e.g., current time)
     * @return Years passed (can be negative if start > end)
     */
    public static int getYearsBetween(Date start, Date end) {
        Calendar calStart = toCalendar(start);
        Calendar calEnd = toCalendar(end);
        return calEnd.get(Calendar.YEAR) - calStart.get(Calendar.YEAR);
    }

    /**
     * Calculate months passed between two instants (including years).
     *
     * @param start Start instant
     * @param end   End instant
     * @return Months passed (can be negative)
     */
    public static int getMonthsBetween(Date start, Date end) {
        Calendar calStart = toCalendar(start);
        Calendar calEnd = toCalendar(end);
        int years = getYearsBetween(start, end);
        int months = calEnd.get(Calendar.MONTH) - calStart.get(Calendar.MONTH);
        return years * 12 + months;
    }

    /**
     * Calculate days passed between two instants (including months).
     *
     * @param start Start instant
     * @param end   End instant
     * @return Days passed (can be negative)
     */
    public static long getDaysBetween(Date start, Date end) {
        long diffMillis = end.getTime() - start.getTime();
        return diffMillis / (1000 * 60 * 60 * 24);
    }

    /**
     * Calculate hours passed between two instants (including days).
     *
     * @param start Start instant
     * @param end   End instant
     * @return Hours passed (can be negative)
     */
    public static long getHoursBetween(Date start, Date end) {
        long diffMillis = end.getTime() - start.getTime();
        return diffMillis / (1000 * 60 * 60);
    }

    /**
     * Calculate minutes passed between two instants (including hours).
     *
     * @param start Start instant
     * @param end   End instant
     * @return Minutes passed (can be negative)
     */
    public static long getMinutesBetween(Date start, Date end) {
        long diffMillis = end.getTime() - start.getTime();
        return diffMillis / (1000 * 60);
    }

    /**
     * Calculate seconds passed between two instants (including minutes).
     *
     * @param start Start instant
     * @param end   End instant
     * @return Seconds passed (can be negative)
     */
    public static long getSecondsBetween(Date start, Date end) {
        long diffMillis = end.getTime() - start.getTime();
        return diffMillis / 1000;
    }

    /**
     * Return years passed between the input instant and now.
     *
     * @param input Input instant
     * @return Years passed
     */
    public static int getYearsPassed(Date input) {
        return getYearsBetween(input, new Date());
    }

    /**
     * Return months passed between the input instant and now.
     *
     * @param input Input instant
     * @return Months passed
     */
    public static int getMonthsPassed(Date input) {
        return getMonthsBetween(input, new Date());
    }

    /**
     * Return days passed between the input instant and now.
     *
     * @param input Input instant
     * @return Days passed
     */
    public static long getDaysPassed(Date input) {
        return getDaysBetween(input, new Date());
    }

    /**
     * Return hours passed between the input instant and now.
     *
     * @param input Input instant
     * @return Hours passed
     */
    public static long getHoursPassed(Date input) {
        return getHoursBetween(input, new Date());
    }

    /**
     * Return minutes passed between the input instant and now.
     *
     * @param input Input instant
     * @return Minutes passed
     */
    public static long getMinutesPassed(Date input) {
        return getMinutesBetween(input, new Date());
    }

    /**
     * Return seconds passed between the input instant and now.
     *
     * @param input Input instant
     * @return Seconds passed
     */
    public static long getSecondsPassed(Date input) {
        return getSecondsBetween(input, new Date());
    }
}
