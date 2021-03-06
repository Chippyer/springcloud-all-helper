package com.ejoy.feign.support.api.clients;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ReflectUtil;
import com.ejoy.core.common.response.Result;
import com.ejoy.core.common.utils.CommonSpringContext;
import com.ejoy.core.common.utils.ObjectsUtil;
import com.ejoy.feign.exception.FastClientInvokeException;
import com.ejoy.feign.support.api.processor.FeignClientProcessor;
import com.ejoy.feign.support.definition.FeignClientDefinition;
import com.ejoy.feign.support.definition.FeignClientProcessorRegistry;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;

/**
 * FeignClientHelper操作器, 核心调用操作封装
 *
 * @author: chippy
 * @datetime 2020/12/13 16:27
 */
@Slf4j
public class FeignClientOperator {

    /**
     * 核心处理方法, 调用其他服务的核心处理API
     * 可在服务调用前后实现自定义操作
     *
     * @author chippy
     */
    @SuppressWarnings("unchecked")
    static Result<Object> process(FeignClientDefinition.Element element, Object[] params) {
        final List<FeignClientProcessor> feignClientProcessorList =
            FeignClientProcessorRegistry.get(element.getFullPath() + element.getMethod());
        if (null == feignClientProcessorList || feignClientProcessorList.isEmpty()) {
            return doInvoke(element, params);
        }
        final Object[] wrapParams =
            doInvokeProcessorBefore(element, params, feignClientProcessorList, feignClientProcessorList.size());
        return (Result<Object>)doInvokeProcessAfter(element, doInvoke(element, wrapParams), feignClientProcessorList,
            feignClientProcessorList.size());
    }

    @SuppressWarnings("unchecked")
    private static Result<Object> doInvoke(FeignClientDefinition.Element element, Object[] params) {
        return Objects.isNull(params) ? (Result<Object>)ReflectUtil
            .invoke(CommonSpringContext.getBean(element.getFeignClientClass()), element.getMethod()) :
            (Result<Object>)ReflectUtil
                .invoke(CommonSpringContext.getBean(element.getFeignClientClass()), element.getMethod(), params);
    }

    static FeignClientDefinition.Element doGetElement(String business) {
        final FeignClientDefinition.Element element = FeignClientDefinition.get(business);
        if (element == null) {
            throw new FastClientInvokeException("此服务[" + FeignClientDefinition.server() + "]下不包含此业务类型");
        }
        return element;
    }

    /**
     * 外部服务响应结果处理, 如果遇到异常或不符合预期数据返回null
     *
     * @author chippy
     */
    static <R> Object doProcessResponse(FeignClientDefinition.Element element, Result<Object> response,
        Class<R> dataClass, boolean isNormalProcess) {
        final String fullPath = element.getFullPath(), method = element.getMethod();
        if (ObjectsUtil.isEmpty(response)) {
            if (log.isErrorEnabled()) {
                log.error("调用服务[" + fullPath + "]的方法[" + method + "]执行后接收结果为空", fullPath, method);
            }
            return null;
        }

        if (!Objects.equals(response.getCode(), response.definitionSuccessCode())) {
            if (log.isErrorEnabled()) {
                log.error("调用服务[" + fullPath + "]的方法[" + method + "]执行后接收结果异常-[" + response.getCode() + ", " + response
                    .getErrorMsg() + "]");
            }
            return null;
        }

        return isNormalProcess ? Convert.convert(dataClass, response.getData()) :
            Convert.toList(dataClass, response.getData());
    }

    /**
     * 外部服务响应结果处理, 如果遇见异常, 抛出交给外部处理
     *
     * @author chippy
     */
    static <R> Object doProcessResponseIfExThrow(FeignClientDefinition.Element element, Result<Object> response,
        Class<R> dataClass, boolean isNormalProcess) {
        String fullPath = element.getFullPath(), method = element.getMethod();
        if (ObjectsUtil.isEmpty(response)) {
            if (log.isErrorEnabled()) {
                log.error("调用服务[" + fullPath + "]的方法[" + method + "]执行后接收结果为空", fullPath, method);
            }
            throw new FastClientInvokeException(String.format("调用方法:[%s]容断处理", method));
        }

        if (!Objects.equals(response.getCode(), response.definitionSuccessCode())) {
            if (log.isErrorEnabled()) {
                log.error("调用服务[" + fullPath + "]的方法[" + method + "]执行后接收结果异常-[" + response.getCode() + ", " + response
                    .getErrorMsg() + "]");
            }
            throw new FastClientInvokeException(response.getErrorMsg(), response.getCode());
        }

        return isNormalProcess ? Convert.convert(dataClass, response.getData()) :
            Convert.toList(dataClass, response.getData());
    }

    private static Object[] doInvokeProcessorBefore(FeignClientDefinition.Element element, Object[] params,
        List<FeignClientProcessor> feignClientProcessorList, int size) {
        if (size > 0) {
            int newSize = size - 1;
            final FeignClientProcessor feignClientProcessor = feignClientProcessorList.get(newSize);
            final Object[] wrapParam = feignClientProcessor.processBefore(element, params);
            return doInvokeProcessorBefore(element, wrapParam, feignClientProcessorList, newSize);
        }
        return params;
    }

    private static Object doInvokeProcessAfter(FeignClientDefinition.Element element, Object response,
        List<FeignClientProcessor> feignClientProcessorList, int size) {
        if (size > 0) {
            int newSize = size - 1;
            final FeignClientProcessor feignClientProcessor = feignClientProcessorList.get(newSize);
            final Object wrapResponse = feignClientProcessor.processAfter(element, response);
            return doInvokeProcessAfter(element, wrapResponse, feignClientProcessorList, newSize);
        }
        return response;
    }

    /**
     * 针对异常情况做出自定义处理, 可自定义实现{@link FeignClientProcessor}接口完成自定义的操作处理
     *
     * @author chippy
     */
    static void doProcessException(FeignClientDefinition.Element element, Exception e) {
        final List<FeignClientProcessor> feignClientProcessorList =
            FeignClientProcessorRegistry.get(element.getFullPath() + element.getMethod());
        if (null != feignClientProcessorList && !feignClientProcessorList.isEmpty()) {
            for (FeignClientProcessor feignClientProcessor : feignClientProcessorList) {
                feignClientProcessor.processException(element, e);
            }
        }
    }

}
