package com.ngw.util;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Constant {
    public static String SUCCESS_FLAG = "1";
    public static String ERROR_FLAG = "0";
    public static String TIMEOUT_FLAG = "2";
    public static Map<String, String> defaultFailResponseMap = Maps.newHashMap();

    static {
        defaultFailResponseMap.put("flag", ERROR_FLAG);
        defaultFailResponseMap.put("messageLevel", "err");
        defaultFailResponseMap.put("message", "unknown error!");
    }

    public static Map<String, String> timeoutFailResponseMap = Maps.newHashMap();

    static {
        timeoutFailResponseMap.put("flag", TIMEOUT_FLAG);
        timeoutFailResponseMap.put("messageLevel", "err");
        timeoutFailResponseMap.put("message", "emit timeout error!");
    }

    public static Map<String, String> baseFailResponseMap = Maps.newHashMap();

    static {
        baseFailResponseMap.put("flag", ERROR_FLAG);
        baseFailResponseMap.put("messageLevel", "err");
    }
}
