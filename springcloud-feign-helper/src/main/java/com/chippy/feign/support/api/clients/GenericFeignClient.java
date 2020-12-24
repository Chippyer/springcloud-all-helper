package com.chippy.feign.support.api.clients;

import com.chippy.core.common.response.Result;
import com.chippy.feign.exception.FastClientInvokeException;
import com.chippy.feign.support.definition.FeignClientDefinition;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotNull;

/**
 * 单对象相应客户端, 用户处理单个对象返回的请求
 *
 * @author: chippy
 */
@Slf4j
public class GenericFeignClient {

    /**
     * 无参异常消化调用
     *
     * @param dataClass 返回对象类型
     * @param method    具体调用方法
     * @author chippy
     */
    @SuppressWarnings("unchecked")
    public static <R> R invoke(Class<R> dataClass, @NotNull String method) {
        final FeignClientDefinition.Element element = FeignClientOperator.getElement(method);
        try {
            Result<Object> response = FeignClientOperator.doProcess(element, null);
            return (R)FeignClientOperator.doProcessResponse(element, response, dataClass, true);
        } catch (Exception e) {
            FeignClientOperator.processException(element, e);
            return null;
        }
    }

    /**
     * 带参异常消化调用
     *
     * @param dataClass 返回对象类型
     * @param method    具体调用方法
     * @param params    传入参数数组
     * @author chippy
     */
    @SuppressWarnings("unchecked")
    public static <R> R invoke(Class<R> dataClass, String method, Object... params) {
        if (null == params) {
            return invoke(dataClass, method);
        }
        final FeignClientDefinition.Element element = FeignClientOperator.getElement(method);
        try {
            Result<Object> response = FeignClientOperator.doProcess(element, params);
            return (R)FeignClientOperator.doProcessResponse(element, response, dataClass, true);
        } catch (Exception e) {
            FeignClientOperator.processException(element, e);
            return null;
        }
    }

    /**
     * 无参抛出异常操作
     *
     * @param dataClass 返回对象类型
     * @param method    具体调用方法
     * @author chippy
     */
    @SuppressWarnings("unchecked")
    public static <R> R invokeIfExThrow(Class<R> dataClass, String method) {
        final FeignClientDefinition.Element element = FeignClientOperator.getElement(method);
        try {
            Result<Object> response = FeignClientOperator.doProcess(element, null);
            return (R)FeignClientOperator.doProcessResponseIfExThrow(element, response, dataClass, true);
        } catch (FastClientInvokeException e) {
            FeignClientOperator.processException(element, e);
            throw e;
        } catch (Exception e) {
            FeignClientOperator.processException(element, e);
            throw new FastClientInvokeException(String.format("调用方法:[%s]未知异常", method));
        }
    }

    /**
     * 带参抛出异常操作
     *
     * @param dataClass 返回对象类型
     * @param method    具体调用方法
     * @param params    传入参数数组
     * @author chippy
     */
    @SuppressWarnings("unchecked")
    public static <R> R invokeIfExThrow(Class<R> dataClass, String method, Object... params) {
        if (null == params) {
            return invokeIfExThrow(dataClass, method);
        }
        final FeignClientDefinition.Element element = FeignClientOperator.getElement(method);
        try {
            Result<Object> response = FeignClientOperator.doProcess(element, params);
            return (R)FeignClientOperator.doProcessResponseIfExThrow(element, response, dataClass, true);
        } catch (FastClientInvokeException e) {
            FeignClientOperator.processException(element, e);
            throw e;
        } catch (Exception e) {
            FeignClientOperator.processException(element, e);
            throw new FastClientInvokeException(String.format("调用方法:[%s]未知异常", method));
        }
    }

}
