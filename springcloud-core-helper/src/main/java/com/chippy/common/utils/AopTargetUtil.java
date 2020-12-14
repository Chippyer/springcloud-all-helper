package com.chippy.common.utils;

import cn.hutool.core.util.ReflectUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.support.AopUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * AOP代码相关便捷方法封装
 *
 * @author chippy
 */
@Slf4j
public class AopTargetUtil {

    public static Object getTarget(Object proxy) {
        if (!AopUtils.isAopProxy(proxy)) {
            return proxy;//不是代理对象
        }

        try {
            if (AopUtils.isJdkDynamicProxy(proxy)) {
                return getJdkDynamicProxyTargetObject(proxy);
            } else {
                return getCglibProxyTargetObject(proxy);
            }
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("获取代理对象异常", e);
            }
            return null;
        }
    }

    /**
     * CGLIBProxy 返回目标类
     **/
    private static Object getCglibProxyTargetObject(Object proxy) throws Exception {
        Object dynamicAdvisedInterceptor = ReflectUtil.getFieldValue(proxy, "CGLIB$CALLBACK_0");
        return ((AdvisedSupport)ReflectUtil.getFieldValue(dynamicAdvisedInterceptor, "advised")).getTargetSource()
            .getTarget();
    }

    /**
     * JDKProxy 返回目标类
     **/
    private static Object getJdkDynamicProxyTargetObject(Object proxy) throws Exception {
        InvocationHandler invocationHandler = Proxy.getInvocationHandler(proxy);
        return ((AdvisedSupport)ReflectUtil.getFieldValue(invocationHandler, "advised")).getTargetSource().getTarget();
    }

}  