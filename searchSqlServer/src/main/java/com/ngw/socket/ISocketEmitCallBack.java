package com.ngw.socket;

import com.ngw.domain.ResponseModel;

import java.util.Map;

public interface ISocketEmitCallBack {
    void call(Map<String, String> responseMap);
}
