package com.imedia.util;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public final class DateUtil {
    private static final DateTimeFormatter SHORT_DATE_TIME_FORMATTER = DateTimeFormat
            .forPattern("yyyyMMddHHmmss");

    private static final DateTimeFormatter SHORT_DATE_FORMATTER = DateTimeFormat
            .forPattern("yyyyMMdd");

    private static final DateTimeFormatter SHORT_MONTH_FORMATTER = DateTimeFormat
            .forPattern("yyyyMM");

    private static final DateTimeFormatter LONG_DATE_TIME_FORMATTER = DateTimeFormat
            .forPattern("yyyy/MM/dd HH:mm:ss");

    private static final DateTimeFormatter LONG_DATE_FORMATTER = DateTimeFormat
            .forPattern("yyyy/MM/dd");

    private static final DateTimeFormatter LONG_DATE_TIME_FORMATTER2 = DateTimeFormat
            .forPattern("yyyy-MM-dd HH:mm:ss");

    private static final DateTimeFormatter LONG_DATE_FORMATTER2 = DateTimeFormat
            .forPattern("yyyy-MM-dd");

    private static final DateTimeFormatter LONG_DATEVI_TIME_FORMATTER = DateTimeFormat
            .forPattern("dd/MM/yyyy HH:mm:ss");

    private static final DateTimeFormatter LONG_DATEVI_FORMATTER = DateTimeFormat
            .forPattern("dd/MM/yyyy");


    private DateUtil() {

    }

    /**
     * Parses a Date from a String using pattern yyyyMMddHHmmss.
     *
     * @throws IllegalArgumentException if the string to parse is invalid
     */
    public static Date parseShortDateTime(String s) {
        if (s == null) {
            return null;
        }

        return SHORT_DATE_TIME_FORMATTER.parseDateTime(s).toDate();
    }

    /**
     * Format a Date to a String using pattern yyyyMMddHHmmss.
     */
    public static String formatShortDateTime(Date d) {
        if (d == null) {
            return null;
        }

        return SHORT_DATE_TIME_FORMATTER.print(d.getTime());
    }

    /**
     * Format a Date to a String using pattern yyyyMMddHHmmss. Default: new Date()
     */
    public static String formatShortDateTime() {
        return formatShortDateTime(new Date());
    }

    /**
     * Parses a Date from a String using pattern yyyyMMdd.
     *
     * @throws IllegalArgumentException if the string to parse is invalid
     */
    public static Date parseShortDate(String s) {
        if (s == null) {
            return null;
        }

        return SHORT_DATE_FORMATTER.parseDateTime(s).toDate();
    }

    /**
     * Format a Date to a String using pattern yyyyMMdd.
     */
    public static String formatShortDate(Date d) {
        if (d == null) {
            return null;
        }

        return SHORT_DATE_FORMATTER.print(d.getTime());
    }

    /**
     * Format a Date to a String using pattern yyyyMMdd. Default: new Date()
     */
    public static String formatShortDate() {
        return formatShortDate(new Date());
    }

    /**
     * Format a Date to a String using pattern yyyyMM.
     */
    public static String formatShortMonth(Date d) {
        if (d == null) {
            return null;
        }

        return SHORT_MONTH_FORMATTER.print(d.getTime());
    }

    /**
     * Format a Date to a String using pattern yyyyMM. Default: new Date()
     */
    public static String formatShortMonth() {
        return formatShortMonth(new Date());
    }

    /**
     * Parses a Date from a String using pattern yyyy/MM/dd HH:mm:ss.
     *
     * @throws IllegalArgumentException if the string to parse is invalid
     */
    public static Date parseLongDateTime(String s) {
        if (s == null) {
            return null;
        }

        return LONG_DATE_TIME_FORMATTER.parseDateTime(s).toDate();
    }

    /**
     * Parses a Date from a String using pattern yyyy-MM-dd HH:mm:ss.
     *
     * @throws IllegalArgumentException if the string to parse is invalid
     */
    public static Date parseLongDateTime2(String s) {
        if (s == null) {
            return null;
        }

        return LONG_DATE_TIME_FORMATTER2.parseDateTime(s).toDate();
    }

    /**
     * Format a Date to a String using pattern yyyy/MM/dd HH:mm:ss.
     */
    public static String formatLongDateTime(Date d) {
        if (d == null) {
            return null;
        }

        return LONG_DATE_TIME_FORMATTER.print(d.getTime());
    }

    /**
     * Format a Date to a String using pattern yyyy-MM-dd HH:mm:ss.
     */
    public static String formatLongDateTime2(Date d) {
        if (d == null) {
            return null;
        }

        return LONG_DATE_TIME_FORMATTER2.print(d.getTime());
    }

    /**
     * Format a Date to a String using pattern yyyy/MM/dd HH:mm:ss. Default: new Date()
     */
    public static String formatLongDateTime() {
        return formatLongDateTime(new Date());
    }

    /**
     * Format a Date to a String using pattern yyyy-MM-dd HH:mm:ss. Default: new Date()
     */
    public static String formatLongDateTime2() {
        return formatLongDateTime2(new Date());
    }

    /**
     * Parses a Date from a String using pattern yyyy/MM/dd.
     *
     * @throws IllegalArgumentException if the string to parse is invalid
     */
    public static Date parseLongDate(String s) {
        if (s == null) {
            return null;
        }

        return LONG_DATE_FORMATTER.parseDateTime(s).toDate();
    }

    /**
     * Parses a Date from a String using pattern yyyy-MM-dd.
     *
     * @throws IllegalArgumentException if the string to parse is invalid
     */
    public static Date parseLongDate2(String s) {
        if (s == null) {
            return null;
        }

        return LONG_DATE_FORMATTER2.parseDateTime(s).toDate();
    }

    /**
     * Format a Date to a String using pattern yyyy/MM/dd
     */
    public static String formatLongDate(Date d) {
        if (d == null) {
            return null;
        }

        return LONG_DATE_FORMATTER.print(d.getTime());
    }

    /**
     * Format a Date to a String using pattern yyyy-MM-dd
     */
    public static String formatLongDate2(Date d) {
        if (d == null) {
            return null;
        }

        return LONG_DATE_FORMATTER2.print(d.getTime());
    }

    /**
     * Format a Date to a String using pattern yyyy/MM/dd. Default: new Date()
     */
    public static String formatLongDate() {
        return formatLongDate(new Date());
    }

    /**
     * Format a Date to a String using pattern yyyy-MM-dd. Default: new Date()
     */
    public static String formatLongDate2() {
        return formatLongDate2(new Date());
    }

    /**
     * Parses a Date from a String using pattern dd/MM/yyyy HH:mm:ss.
     *
     * @throws IllegalArgumentException if the string to parse is invalid
     */
    public static Date parseLongDateViTime(String s) {
        if (s == null) {
            return null;
        }

        return LONG_DATEVI_TIME_FORMATTER.parseDateTime(s).toDate();
    }

    /**
     * Format a Date to a String using pattern dd/MM/yyyy HH:mm:ss.
     */
    public static String formatLongDateViTime(Date d) {
        if (d == null) {
            return null;
        }

        return LONG_DATEVI_TIME_FORMATTER.print(d.getTime());
    }

    /**
     * Format a Date to a String using pattern dd/MM/yyyy HH:mm:ss. Default: new Date()
     */
    public static String formatLongDateViTime() {
        return formatLongDateViTime(new Date());
    }

    /**
     * Parses a Date from a String using pattern dd/MM/yyyy.
     *
     * @throws IllegalArgumentException if the string to parse is invalid
     */
    public static Date parseLongDateVi(String s) {
        if (s == null) {
            return null;
        }

        return LONG_DATEVI_FORMATTER.parseDateTime(s).toDate();
    }

    /**
     * Format a Date to a String using pattern dd/MM/yyyy.
     */
    public static String formatLongDateVi(Date d) {
        if (d == null) {
            return null;
        }

        return LONG_DATEVI_FORMATTER.print(d.getTime());
    }

    /**
     * Format a Date to a String using pattern dd/MM/yyyy. Default: new Date()
     */
    public static String formatLongDateVi() {
        return formatLongDateVi(new Date());
    }

    /**
     * Parses a Date from a String using a pattern
     *
     * @throws IllegalArgumentException if the string to parse is invalid
     */
    public static Date parseLongDateTime(String s, String strPattern) {
        if (s == null) {
            return null;
        }
        DateTimeFormatter format = DateTimeFormat.forPattern(strPattern);
        return format.parseDateTime(s).toDate();
    }

    /**
     * Format a Date to a String using a pattern
     */
    public static String formatDateTime(Date d, String strPattern) {
        if (d == null) {
            return null;
        }
        DateTimeFormatter format = DateTimeFormat.forPattern(strPattern);
        return format.print(d.getTime());
    }

    ///////////////////////////////////////////////////////////////////////////////////////

    public static Date addMonths(Date date, int amount) {
        if (date == null)
            return null;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, amount);
        return calendar.getTime();
    }

    public static Date addWeeks(Date date, int amount) {
        if (date == null)
            return null;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.WEEK_OF_YEAR, amount);
        return calendar.getTime();
    }

    public static Date addHours(Date date, int amount) {
        if (date == null)
            return null;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR, amount);
        return calendar.getTime();
    }

    public static Date addMins(Date date, int amount) {
        if (date == null)
            return null;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, amount);
        return calendar.getTime();
    }

    public static Date addSeconds(Date date, int amount) {
        if (date == null)
            return null;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.SECOND, amount);
        return calendar.getTime();
    }

    public static int monthsBetween(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException("The date must not be null");
        }

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        int month1 = cal1.get(Calendar.MONTH);
        int month2 = cal2.get(Calendar.MONTH);

        return (year2 - year1) * 12 + (month2 - month1);
    }

    //////////////////////////////////////////////////////////////////////////////
    public static int getYear(Date date) {
        if (date == null)
            throw new NullPointerException("date");

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }

    public static int getMonth(Date date) {
        if (date == null)
            throw new NullPointerException("date");

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH) + 1;
    }

    public static int getDayOfMonth(Date date) {
        if (date == null)
            throw new NullPointerException("date");

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static int getHourOfTheDay(Date date) {
        if (date == null)
            throw new NullPointerException("date");

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    public static int getMinute(Date date) {
        if (date == null)
            throw new NullPointerException("date");

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MINUTE);
    }

    public static int getSecond(Date date) {
        if (date == null)
            throw new NullPointerException("date");

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.SECOND);
    }

    /**
     * Return a Date to begin of its day
     */
    public static Date trunc(Date date) {
        if (date == null)
            throw new NullPointerException("date");

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * Return a Date to end of its day
     */
    public static Date ceil(Date date) {
        if (date == null)
            throw new NullPointerException("date");

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }

    /**
     * Return a Date to begin of its month
     */
    public static Date firstDayOfMonth(Date date) {
        if (date == null)
            throw new NullPointerException("date");

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * Return a Date to begin of its month
     */
    public static Date lastDayOfMonth(Date date) {
        if (date == null)
            throw new NullPointerException("date");

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }

    public static Date addDay(int numberDay) {
        Date today = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        cal.add(Calendar.DATE, numberDay);
        return cal.getTime();
    }

    public static Date addDay(Date date, int numberDay) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, numberDay);
        return cal.getTime();
    }

    public static boolean isSameDay(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA)
                && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1
                .get(Calendar.DAY_OF_YEAR) == cal2
                .get(Calendar.DAY_OF_YEAR));
    }

    public static Date dateOf(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        return cal.getTime();
    }

    public static long[] getHMS(long millis) {
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
        return new long[]{hours, minutes, seconds};
    }

    public static int dateDiff(Date startDate, Date endDate) {
        //DateTime d1 = new DateTime(startDate.getTime());
        //DateTime d2 = new DateTime(endDate.getTime());
        //Days d = Days.daysBetween(d1, d2);
        long diff = endDate.getTime() - startDate.getTime();
        return (int) (diff / (1000 * 60 * 60 * 24));
    }

    public static int hourDiff(Date startDate, Date endDate) {
        long diff = endDate.getTime() - startDate.getTime();
        return (int) (diff / (1000 * 60 * 60));
    }

    public static int getRemainSecondsTilNextDay(int nexDayHour) {
        DateTime now = DateTime.now();
        DateTime tomorrowStart = now.plusDays(1).withTimeAtStartOfDay().plusHours(nexDayHour);
        long remainSeconds = (tomorrowStart.toDate().getTime() - new Date().getTime()) / 1000;
        return Math.toIntExact(remainSeconds);
    }

}
