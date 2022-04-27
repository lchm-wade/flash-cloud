package com.foco.cloud.core.executor;

import com.foco.context.executor.ThreadLocalExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * @author lucoo
 * @version 1.0.0
 * @description TODO
 * @date 2021/11/19 10:11
 */
public class CloudThreadLocalExecutor  implements ThreadLocalExecutor {
    private ThreadPoolTaskExecutor delegate;

    public CloudThreadLocalExecutor(ThreadPoolTaskExecutor delegate) {
        this.delegate = delegate;
    }

    @Override
    public void execute(Runnable command) {
        delegate.execute(new CloudThreadLocalRunnable(command));
    }

    @Override
    public Future<?> submit(Runnable command) {
        return delegate.submit(new CloudThreadLocalRunnable(command));
    }

    @Override
    public <T> Future<T> submit(Callable<T> callable) {
        return delegate.submit(new CloudThreadLocalCallable<>(callable));
    }
}
