package com.ngw.socket.ack;

import com.ngw.domain.ResponseCode;
import com.ngw.domain.ResponseModel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AckESTimeOut extends AckTimeOut {

    private static final Logger logger = Logger.getLogger(AckESTimeOut.class.getName());

    private static final ExecutorService executorService = Executors.newCachedThreadPool();
    private Future future;

    protected void start() {
        future = executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(getTimeOut());
                    logger.log(Level.SEVERE, "ACK timeout");
                    callback(ResponseModel.getResponseModel(ResponseCode.ACK_TIMEOUT));
                } catch (InterruptedException e) {
                    logger.info(e.getMessage());
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "ACK error\n" + e.getMessage());
                    future.cancel(true);
                }
            }
        });
    }

    protected void shutdown() {
        if (future != null) {
            future.cancel(true);
        }
    }
}
