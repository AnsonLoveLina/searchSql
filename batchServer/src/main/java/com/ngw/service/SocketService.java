package com.ngw.service;

import com.alibaba.fastjson.JSON;
import com.ngw.domain.ResponseCode;
import com.ngw.domain.ResponseModel;
import com.ngw.domain.SqlParam;
import com.ngw.service.api.SqlService;
import com.ngw.socket.ISocketEmitCallBack;
import com.ngw.socket.ListenerNoBlock;
import com.ngw.socket.SocketIOClient;
import com.ngw.socket.SocketIOClientUtil;
import com.ngw.socket.ack.AckESTimeOut;
import com.ngw.util.Constant;
import com.ngw.util.CustomerType;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

import static com.ngw.util.Constant.BATCH_SEARCH_EVENT;
import static com.ngw.util.Constant.BATCH_SEARCH_RESULT_EVENT;
import static com.ngw.socket.SocketIOClient.EVENT_REGISTER;
import static com.ngw.util.Constant.SUCCESS_FLAG;

/**
 * Created by zy-xx on 2019/11/4.
 */
public class SocketService {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(SocketService.class);

    private SocketIOClient socketIOClient;

    @Resource
    private SqlService sqlService;

    public void socketConnect(String url) {
        String userId = Integer.toString((new Random()).nextInt(100000000));
        //ExecutorService executor = Executors.newSingleThreadExecutor();
        socketIOClient = SocketIOClientUtil.socketConnect(url);
        if (socketIOClient == null) {
            return;
        }
        socketIOClient.register(new SocketIOClient.Customer(CustomerType.GROUP, "110"), new ISocketEmitCallBack() {
            @Override
            public void call(Map<String, String> response) {
                if (!SUCCESS_FLAG.equals(response.get("flag"))) {
                    logger.error(String.format("%s error!\n%s", EVENT_REGISTER, response.toString()));
                }
            }
        });
        socketIOClient.register(new SocketIOClient.Customer(CustomerType.USER, userId), new ISocketEmitCallBack() {
            @Override
            public void call(Map<String, String> response) {
                if (!SUCCESS_FLAG.equals(response.get("flag"))) {
                    logger.error(String.format("%s error!\n%s", EVENT_REGISTER, response.toString()));
                }
            }
        });
//            }
//        }).start();

    }
}
