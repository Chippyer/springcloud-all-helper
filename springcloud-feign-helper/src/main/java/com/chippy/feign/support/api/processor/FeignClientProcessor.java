package com.chippy.feign.support.api.processor;

import com.chippy.feign.support.definition.FeignClientDefinition;

/**
 * FeignClientHelper调用时的处理器
 *
 * @author: chippy
 * @datetime 2020/12/13 15:35
 */
public interface FeignClientProcessor {

    /**
     * 调用前的自定义操作
     *
     * @author chippy
     */
    void processBefore(FeignClientDefinition.Element element, Object[] param);

    /**
     * 调用后的自定义操作
     *
     * @author chippy
     */
    void processAfter(FeignClientDefinition.Element element, Object object);

    /**
     * 调用异常的自定义操作
     *
     * @author chippy
     */
    void processException(FeignClientDefinition.Element element, Exception e);

}
