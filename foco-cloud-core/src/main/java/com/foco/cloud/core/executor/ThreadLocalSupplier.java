package com.foco.cloud.core.executor;


import com.foco.context.core.ThreadLocalTransmitManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.skywalking.apm.toolkit.trace.TraceCrossThread;

import java.util.function.Supplier;

/**
 * description： 解决ThreadLocal 跨线程传递
 *
 * @Author lucoo
 * @Date 2021/6/2 9:57
 */
@Slf4j
@TraceCrossThread
public class ThreadLocalSupplier<T> implements Supplier<T> {
    private Supplier supplier;
    /**
     * ContextThreadLocal对象
     */
    private ThreadLocalTransmitManager threadLocalTransmitManager;

    public ThreadLocalSupplier(Supplier supplier) {
        this.supplier = supplier;
        this.threadLocalTransmitManager = new ThreadLocalTransmitManager();
    }


    @Override
    public T get() {
        threadLocalTransmitManager.set();
        T o = null;
        try {
            o = (T) supplier.get();
        }catch (Throwable e){
            log.error("线程执行异常",e);
        } finally {
            threadLocalTransmitManager.remove();
        }
        return o;
    }
}
