package com.ngw.util;

import com.google.common.collect.Maps;

import java.util.Map;

import static com.ngw.util.Constant.baseFailResponseMap;

public class SocketUtil {

    public static Map<String,String> getBaseFailResponseMap(String message){
        Map<String,String> map = Maps.newHashMap(baseFailResponseMap);
        map.put("message",message);
        return map;
    }
}
