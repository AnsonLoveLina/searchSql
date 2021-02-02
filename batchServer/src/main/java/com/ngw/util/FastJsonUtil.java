package com.ngw.util;

import com.alibaba.fastjson.JSON;

public class FastJsonUtil {

    public static <T> T parseObject(Object object, Class<T> resultClass) {
        T stuff = null;
        if (object != null && object.getClass() == resultClass) {
            stuff = (T) object;
        } else if (object != null && object.getClass() == String.class) {
            try {
                stuff = JSON.parseObject(object.toString(), resultClass);
            } catch (Exception e) {
                throw e;
            }
        } else {
            stuff = null;
        }
        return stuff;
    }

    public static <T> String toJSONString(T object) {
        String result = null;
        if (object != null) {
            if (object.getClass() == String.class) {
                result = (String) object;
            } else {
                try {
                    result = JSON.toJSONString(object.toString());
                } catch (Exception e) {
                    throw e;
                }
            }
        } else {
            result = null;
        }
        return result;
    }
}
