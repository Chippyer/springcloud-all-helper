package com.chippy.common.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * Cron 表达式 工具类.
 *
 * @author chippy
 */
public class CronUtils {
    private CronUtils() {
    }

    /***
     * 日期转换cron表达式.
     *
     * @param date 日期
     * @return String
     */
    private static String formatDateByPattern(Date date) {
        String formatTimeStr = null;
        if (Objects.nonNull(date)) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ss mm HH dd MM ? yyyy");
            formatTimeStr = simpleDateFormat.format(date);
        }
        return formatTimeStr;
    }

    /***
     * convert Date to cron, eg "0 07 10 15 1 ? 2016"
     *
     * @param date 日期
     * @return String
     */
    public static String getCron(Date date) {
        return formatDateByPattern(date);
    }

}
