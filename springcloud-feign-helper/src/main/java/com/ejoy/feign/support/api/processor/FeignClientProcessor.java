package com.ejoy.feign.support.api.processor;

import com.ejoy.feign.support.definition.FeignClientDefinition;

import java.util.List;

/**
 * FeignClientHelper调用时的处理器
 *
 * @author: chippy
 * @datetime 2020/12/13 15:35
 */
public interface FeignClientProcessor {

    /**
     * 获取指定拦截路径规则
     *
     * @author chippy
     */
    List<String> getIncludePathPattern();

    /**
     * 获取指定排除的拦截路径规则
     *
     * @author chippy
     */
    default List<String> getExcludePathPattern() {
        return null;
    }

    /**
     * 调用前的自定义操作
     *
     * @author chippy
     */
    Object[] processBefore(FeignClientDefinition.Element element, Object[] param);

    /**
     * 调用后的自定义操作
     * 注意：这里如果想要包装data中的数据，一定要进行判空
     *
     * @author chippy
     */
    Object processAfter(FeignClientDefinition.Element element, Object response);

    /**
     * 调用异常的自定义操作
     *
     * @author chippy
     */
    void processException(FeignClientDefinition.Element element, Exception e);

}
