package me.aias.common.utils.common;

import java.text.SimpleDateFormat;

/**
 * 日期类
 * @author Calvin
 * @date 2021-12-12
 **/
public class DateUtil {
    public static final ThreadLocal<SimpleDateFormat> YYYY_MM_dd_HH_mm_ss =
            new ThreadLocal<SimpleDateFormat>() {
                @Override
                protected SimpleDateFormat initialValue() {
                    return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                }
            };
    public static final ThreadLocal<SimpleDateFormat> YYYY_MM_dd =
            new ThreadLocal<SimpleDateFormat>() {
                @Override
                protected SimpleDateFormat initialValue() {
                    return new SimpleDateFormat("yyyy/MM/dd/");
                }
            };
    public static final ThreadLocal<SimpleDateFormat> YYYYMMdd =
            new ThreadLocal<SimpleDateFormat>() {
                @Override
                protected SimpleDateFormat initialValue() {
                    return new SimpleDateFormat("yyyyMMdd");
                }
            };
    public static final ThreadLocal<SimpleDateFormat> YYYYMMdd_HH_mm_ss =
            new ThreadLocal<SimpleDateFormat>() {
                @Override
                protected SimpleDateFormat initialValue() {
                    return new SimpleDateFormat("yyyyMMdd_HH:mm:ss");
                }
            };
}
