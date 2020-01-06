package com.ngw.socket.ack;

import com.ngw.domain.ResponseCode;
import com.ngw.domain.ResponseModel;

import java.util.Timer;
import java.util.TimerTask;

@Deprecated
/**
 *too much memory,see com.hisign.broadcastx.socket.SocketIOClientUtilTest.testAckTimeOut()
 */
public abstract class AckTimerTimeOut extends AckTimeOut {

    private Timer timer;

    protected void start() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                callback(ResponseModel.getResponseModel(ResponseCode.ACK_TIMEOUT));
            }
        }, getTimeOut());
    }

    protected void shutdown() {
        if (timer != null) {
            timer.cancel();
        }
    }
}
