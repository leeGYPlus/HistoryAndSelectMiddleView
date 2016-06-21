package com.salmonzhg.histogramview_demo.utils;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Salmon on 2016/6/21 0021.
 */
public class DateUtils {
    /**
     * 上个月最后一天是几号
     *
     * @return
     */
    public static int dateInLastDayInLastMonth() {
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.DATE, 1);
        calendar.add(Calendar.DATE, -1);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 28天前是上个月几号
     *
     * @return
     */
    public static int dateBefore28Days() {
        Calendar calendar = new GregorianCalendar();
        calendar.add(Calendar.DATE, -28 + 1);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 28天前的日期
     * 格式如："2016-5-25 00:00:00"
     *
     * @return
     */
    public static String datesBefore28Days() {
        Calendar calendar = new GregorianCalendar();
        calendar.add(Calendar.DATE, -28 + 1);
        return calendarToString(calendar);
    }

    /**
     * 今天的日期
     * 格式如："2016-5-25 00:00:00"
     *
     * @return
     */
    public static String datesToday() {
        Calendar calendar = new GregorianCalendar();
        return calendarToString(calendar);
    }

    /**
     * calendar转字符串
     *
     * @param calendar
     * @return 格式如："2016-5-25 00:00:00"的字符串
     */
    public static String calendarToString(Calendar calendar) {
        String s = calendar.get(Calendar.YEAR) + "-" +
                (calendar.get(Calendar.MONTH) + 1) + "-" +
                calendar.get(Calendar.DAY_OF_MONTH) + " 00:00:00";
        return s;
    }

    /**
     * 今天的日期
     *
     * @return
     */
    public static int dateToday() {
        Calendar calendar = new GregorianCalendar();
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 整形转换星期
     *
     * @param index
     * @return
     */
    public static String intToWeek(int index) {
        String result = "";
        switch (index) {
            case 0:
                result = "一";
                break;
            case 1:
                result = "二";
                break;
            case 2:
                result = "三";
                break;
            case 3:
                result = "四";
                break;
            case 4:
                result = "五";
                break;
            case 5:
                result = "六";
                break;
            case 6:
                result = "日";
                break;
        }
        return result;
    }
}
