package com.ngw.common;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zy-xx on 2019/8/26.
 */
public class Constants {
    public static String CHARSET_UTF8 = "UTF-8";
    public static String CHARSET_GB2312 = "GB2312";
    public static String CHARSET_GBK = "GBK";
    public static String CHARSET_GB18030 = "GB18030";
    public static String CHARSET_ISO88591 = "ISO-8859-1";
    public static String CHARSET_ASCLL = "ASCLL";
    public static String CHARSET_UTF16 = "UTF-16";

    public static Map<String, String> JSON_HEADER = new HashMap<>();

    static {
        JSON_HEADER.put("Content-Type","application/json");
    }
}
