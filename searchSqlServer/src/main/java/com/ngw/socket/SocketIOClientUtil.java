package com.ngw.socket;

import com.google.common.collect.Sets;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import io.socket.emitter.Emitter;
import jodd.util.StringUtil;

public class SocketIOClientUtil {

    private static final Logger logger = Logger.getLogger(SocketIOClientUtil.class.getName());

    private static SocketIOClient socketIOClient;

    public static SocketIOClient getInstance() {
        return socketIOClient;
    }

    private static SocketIOClient getInstance(String socketUrl) {
        if (socketIOClient == null && socketUrl != null) {
            socketIOClient = new SocketIOClient(socketUrl);
        }
        return socketIOClient;
    }

    /**
     * userid,groupids在连接成功后第一时间回调注册
     *
     * @param url
     * @return
     */
    public static SocketIOClient socketConnect(String url) {
        socketIOClient = SocketIOClientUtil.getInstance(url);
        if (socketIOClient == null) {
            return null;
        }
        socketIOClient.connection();
        return socketIOClient;
    }

    public static Set<SocketIOClient.Customer> getGroups() {
        return socketIOClient.getCustomers();
    }

    public static SocketIOClient.Customer getUser() {
        return socketIOClient.getUserCustomer();
    }
}
