package com.ngw.socket.ack;


import com.ngw.util.Constant;
import com.ngw.util.FastJsonUtil;
import com.ngw.util.SocketUtil;
import io.socket.client.Ack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static com.ngw.util.Constant.TIMEOUT_FLAG;


public abstract class AckTimeOut implements Ack {

    private static Logger logger = LoggerFactory.getLogger(AckTimeOut.class);

    private long timeOut = 0;
    private boolean called = false;

    public AckTimeOut() {
        this(Constant.EMIT_TIMEOUT);
    }

    public AckTimeOut(long timeOut) {
        if (timeOut <= 0)
            return;
        setTimeOut(timeOut);
        start();
    }

    protected abstract void start();

    protected abstract void shutdown();

    public void reset() {
        shutdown();
        start();
    }

    protected void callback(Object... args) {
        if (called) return;
        called = true;
        shutdown();
        call(args);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void call(Object... args) {
        Map<String, String> responseMap = new HashMap<>();
        String response = args[0] == null ? "" : args[0].toString();
        try {
            if (args[0] != null && Map.class.isAssignableFrom(args[0].getClass())) {
                responseMap = (Map<String, String>) args[0];
            } else {
                responseMap = FastJsonUtil.parseObject(response, Map.class);
            }
        } catch (Exception e) {
            responseMap = SocketUtil.getBaseFailResponseMap(String.format("%s can not parse to %s", response, Map.class.toString()));
            logger.error(String.format("%s can not parse", response));
        } finally {
            if (!TIMEOUT_FLAG.equals(responseMap.get("flag"))) {
                shutdown();
            }
        }
        responseCall(responseMap);
    }

    public abstract void responseCall(Map<String, String> responseMap);

    protected long getTimeOut() {
        return timeOut;
    }

    protected void setTimeOut(long timeOut) {
        this.timeOut = timeOut;
    }
}