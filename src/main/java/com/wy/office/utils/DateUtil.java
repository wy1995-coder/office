package com.wy.office.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    private DateUtil(){}

    private static SimpleDateFormat sdf = null;

    /**
     * 日期字符串转date
     * @param source 时间字符串
     * @param regex 格式化格式
     * @return
     */
    public static Date StringToDate(String source, String regex) {
        Date date =  null;
        sdf = new SimpleDateFormat(regex);
        try {
            date = sdf.parse(source);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * date转字符串日期
     * @param date 日期
     * @param regex 格式
     * @return
     */
    public static String DateToString(Date date, String regex) {
        sdf = new SimpleDateFormat(regex);
        return sdf.format(date);
    }
}
