package com.ngw.socket;

import com.ngw.util.FastJsonUtil;
import io.socket.emitter.Emitter;
import jodd.util.ReflectUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class ListenerNoBlock<T> implements Emitter.Listener {

    private static final Logger logger = Logger.getLogger(ListenerNoBlock.class.getName());

    private Class<T> tClass;

    public ListenerNoBlock() {
        tClass = ReflectUtil.getGenericSupertype(getClass(),0);
    }

    @Override
    public void call(Object... args) {
        for (Object object : args) {
            T stuff = null;
            try {
                stuff = FastJsonUtil.parseObject(object, tClass);
            } catch (Exception e) {
                logger.log(Level.SEVERE, String.format("%s can not parse to %s", object == null ? "" : object.toString(), tClass.toString()));
            }
            if (stuff == null) {
                continue;
            }
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            final T finalStuff = stuff;
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    onEventCall(finalStuff);
                }
            });
        }
    }

    abstract public void onEventCall(T object);
}
