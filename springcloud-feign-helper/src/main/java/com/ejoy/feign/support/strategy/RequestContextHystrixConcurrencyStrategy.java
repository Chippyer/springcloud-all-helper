package com.ejoy.feign.support.strategy;

import com.ejoy.feign.support.wrapper.HystrixCallableWrapper;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.Callable;

/**
 * 自定义Hystrix并发策略
 * 在调用外部服务执行前对应定义好的实现自{@link HystrixCallableWrapper}类进行遍历调用其扩展(包装)操作
 *
 * @author: chippy
 * @datetime 2020-11-05 10:47
 */
public class RequestContextHystrixConcurrencyStrategy extends HystrixConcurrencyStrategy {

    private final Collection<HystrixCallableWrapper> wrappers;

    public RequestContextHystrixConcurrencyStrategy(Collection<HystrixCallableWrapper> wrappers) {
        this.wrappers = wrappers;
    }

    @Override
    public <T> Callable<T> wrapCallable(Callable<T> callable) {
        return new CallableWrapperChain<>(callable, wrappers.iterator()).wrapCallable();
    }

    private static class CallableWrapperChain<T> {
        private final Callable<T> callable;
        private final Iterator<HystrixCallableWrapper> wrappers;

        CallableWrapperChain(Callable<T> callable, Iterator<HystrixCallableWrapper> wrappers) {
            this.callable = callable;
            this.wrappers = wrappers;
        }

        Callable<T> wrapCallable() {
            Callable<T> delegate = callable;
            while (wrappers.hasNext()) {
                delegate = wrappers.next().wrap(delegate);
            }
            return delegate;
        }
    }

}
