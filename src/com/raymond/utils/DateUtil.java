package com.raymond.utils;


import com.raymond.enums.DateStyle;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 时间工具类
 *
 * @author :  raymond
 * @version :  V1.0
 * @date :  2019-12-11 12:48
 */
public class DateUtil {
    private static Map<String, SimpleDateFormat> map = new HashMap<String, SimpleDateFormat>();
    static {
        DateStyle[] values = DateStyle.values();
        SimpleDateFormat dateFormat;
        for (DateStyle style : values) {
            dateFormat = new SimpleDateFormat(style.getValue());
            map.put(style.getValue(), dateFormat);
        }
    }

    /**
     * 获取SimpleDateFormat
     *
     * @param style
     *            日期格式
     * @return SimpleDateFormat对象
     * @throws RuntimeException
     *             异常：非法日期格式
     */
    public static SimpleDateFormat getDateFormat(DateStyle style) {
        return map.get(style.getValue());
    }

    /**
     * 获取日期中的某数值。如获取月份
     *
     * @param date
     *            日期
     * @param dateType
     *            日期格式
     * @return 数值
     */
    public static int getInteger(Date date, int dateType) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(dateType);
    }

    /**
     * 使用yyyy-MM-dd HH:mm:ss格式提取字符串日期
     *
     * @param strDate
     *            日期字符串
     * @return date
     */
    public static Date parse(String strDate) throws Exception{
        return parse(strDate, DateStyle.YYYY_MM_DD_HH_MM_SS);
    }

    /**
     * 使用指定格式提取字符串日期
     *
     * @param strDate
     *            日期字符串
     * @param style
     *            日期格式
     * @return date
     */
    public static Date parse(String strDate, DateStyle style) throws Exception{
        return getDateFormat(style).parse(strDate);
    }

    /**
     * 和当前时间比较大小
     *
     * @param date date
     * @return
     */
    public static int compareDateWithNow(Date date) throws Exception{
        Date nowDate = new Date();
        return date.compareTo(nowDate);
    }

    /**
     * 获取系统当前时间
     *
     * @return 指定格式的字符串
     */
    public static String getNowTime() throws Exception{
        return getNowTime(DateStyle.YYYY_MM_DD_HH_MM_SS);
    }

    /**
     * 获取系统当前时间
     *
     * @return 指定格式的字符串
     */
    public static String getNowTime(DateStyle pattern) throws Exception{
        SimpleDateFormat formatter = getDateFormat(pattern);
        return formatter.format(new Date());
    }

    /**
     * 将时间转换为指定格式的字符串
     * @param date date
     * @return 指定格式的字符串
     */
    public static String dateToStr(Date date) throws Exception{
        return dateToStr(date, DateStyle.YYYY_MM_DD_HH_MM_SS);
    }

    /**
     * 将时间转换为指定格式的字符串
     * @param date date
     * @param pattern 字符串格式
     * @return 指定格式的字符串
     */
    public static String dateToStr(Date date,DateStyle pattern) {
        return getDateFormat(pattern).format(date);
    }

    /**
     * 时间格式字符串转化成另一种时间格式字符串
     * @param timeStr
     * @return
     * @throws Exception
     */
    public static String parseTime(String timeStr) throws Exception{
        return parseTime(timeStr,DateStyle.YYYY_MM_DD_HH_MM_SS);
    }

    /**
     * 时间格式字符串转化成另一种时间格式字符串
     * @param timeStr
     * @return
     * @throws Exception
     */
    public static String parseTime(String timeStr ,DateStyle pattern) throws Exception{
        SimpleDateFormat formatter = getDateFormat(pattern);
        return formatter.format(formatter.parse(timeStr));
    }
}
