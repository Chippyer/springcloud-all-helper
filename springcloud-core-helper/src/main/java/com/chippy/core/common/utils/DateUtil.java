package com.chippy.core.common.utils;

import cn.hutool.core.date.DateTime;

/**
 * 时间工具类
 * 继承自{@link DateTime}，在此基础上扩张相关功能
 *
 * @author: chippy
 * @datetime 2020-12-11 17:50
 */
public class DateUtil extends cn.hutool.core.date.DateUtil {

    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String HH_MM_SS = "HH:mm:ss";
    public static final String YYYY_MM_DD_HH_MM = YYYY_MM_DD + " HH:mm";
    public static final String YYYY_MM_DD_HH_MM_SS = YYYY_MM_DD + " " + HH_MM_SS;

    public static boolean aGreaterThanOrEqualToB(String aTime, String bTime) {
        return aGreaterThanOrEqualToB(aTime, bTime, YYYY_MM_DD_HH_MM_SS);
    }

    public static boolean aGreaterThanOrEqualToB(String aTime, String bTime, String format) {
        DateTime aDateTime = new DateTime(aTime, format);
        DateTime bDateTime = new DateTime(bTime, format);
        return aDateTime.getTime() >= bDateTime.getTime();
    }

}
