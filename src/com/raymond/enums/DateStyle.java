package com.raymond.enums;

/**
 * 时间格式枚举
 *
 * @author :  raymond
 * @version :  V1.0
 * @date :  2019-12-14 09:07
 */
public enum DateStyle {
    /**月日**/
    MM_DD("MM-dd"),
    /**年月**/
    YYYY_MM("yyyy-MM"),
    /**年月日**/
    YYYY_MM_DD("yyyy-MM-dd"),
    /**月日时分**/
    MM_DD_HH_MM("MM-dd HH:mm"),
    /**月日时分秒**/
    MM_DD_HH_MM_SS("MM-dd HH:mm:ss"),
    /**年月日时分**/
    YYYY_MM_DD_HH_MM("yyyy-MM-dd HH:mm"),
    /**年月日时分秒**/
    YYYY_MM_DD_HH_MM_SS("yyyy-MM-dd HH:mm:ss"),
    /**年月日时分秒毫秒**/
    YYYY_MM_DD_HH_MM_SS_SSS("yyyy-MM-dd HH:mm:ss.SSS"),

    /**月日**/
    MM_DD_EN("MM/dd"),
    /**年月**/
    YYYY_MM_EN("yyyy/MM"),
    /**年月日**/
    YYYY_MM_DD_EN("yyyy/MM/dd"),
    /**月日时分**/
    MM_DD_HH_MM_EN("MM/dd HH:mm"),
    /**月日时分秒**/
    MM_DD_HH_MM_SS_EN("MM/dd HH:mm:ss"),
    /**年月日时分**/
    YYYY_MM_DD_HH_MM_EN("yyyy/MM/dd HH:mm"),
    /**年月日时分秒**/
    YYYY_MM_DD_HH_MM_SS_EN("yyyy/MM/dd HH:mm:ss"),
//    /**月日**/
//    MM_DD_CN("MM月dd日"),
//    /**年月**/
//    YYYY_MM_CN("yyyy年MM月"),
//    /**年月日**/
//    YYYY_MM_DD_CN("yyyy年MM月dd日"),
//    /**月日时分**/
//    MM_DD_HH_MM_CN("MM月dd日 HH:mm"),
//    /**月日时分秒**/
//    MM_DD_HH_MM_SS_CN("MM月dd日 HH:mm:ss"),
//    /**年月日时分**/
//    YYYY_MM_DD_HH_MM_CN("yyyy年MM月dd日 HH:mm"),
//    /**年月日时分秒**/
//    YYYY_MM_DD_HH_MM_SS_CN("yyyy年MM月dd日 HH:mm:ss"),

    /**时分**/
    HH_MM("HH:mm"),
    /**时分秒**/
    HH_MM_SS("HH:mm:ss");

    private String value;

    DateStyle(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
