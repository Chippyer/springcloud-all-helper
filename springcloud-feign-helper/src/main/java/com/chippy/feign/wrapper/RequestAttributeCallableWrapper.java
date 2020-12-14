package com.chippy.feign.wrapper;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.concurrent.Callable;

/**
 * 使执行线程拥有附加的父线程中的Request请求属性
 *
 * @author: chippy
 * @datetime 2020-11-05 14:34
 */
public class RequestAttributeCallableWrapper implements HystrixCallableWrapper {

    @Override
    public <T> Callable<T> wrap(Callable<T> callable) {
        return new RequestAttributeAwareCallable(callable, RequestContextHolder.getRequestAttributes());
    }

    private static class RequestAttributeAwareCallable<T> implements Callable<T> {

        private final Callable<T> delegate;
        private final RequestAttributes requestAttributes;

        RequestAttributeAwareCallable(Callable<T> callable, RequestAttributes requestAttributes) {
            this.delegate = callable;
            this.requestAttributes = requestAttributes;
        }

        @Override
        public T call() throws Exception {
            try {
                RequestContextHolder.setRequestAttributes(requestAttributes);
                return delegate.call();
            } finally {
                RequestContextHolder.resetRequestAttributes();
            }
        }
    }

}
