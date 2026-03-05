package com.chunfeng.enums;

/**
 * @ClassName DateTimePatternEnum
 * @Author chunfeng
 * @Description
 * @date 2026/3/5 16:34
 * @Version 1.0
 */
public enum DateTimePatternEnum {
    YYYY_MM_DD_HH_MM_SS("yyyy-MM-dd HH:mm:ss"),
    YYYY_MM_DD("yyyy-MM-dd"),
    YYYY_MM("yyyy-MM");

    private final String pattern;

    DateTimePatternEnum(String pattern) {
        this.pattern = pattern ;
    }

    public String getPattern() {
        return pattern;
    }
}
