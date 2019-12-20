package com.ngw.socket;

import java.util.Map;

public interface ISocketEmitFail {
    void onEmitFail(String eventName, Object object, Map<String, String> response);
}
