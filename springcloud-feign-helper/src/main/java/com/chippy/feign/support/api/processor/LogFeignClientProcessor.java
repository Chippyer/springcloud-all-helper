package com.chippy.feign.support.api.processor;

import cn.hutool.json.JSONUtil;
import com.chippy.feign.support.definition.FeignClientDefinition;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 日志功能FeignClientHelper调用时的处理器
 *
 * @author: chippy
 * @datetime 2020/12/13 15:43
 */
@Slf4j
public class LogFeignClientProcessor implements FeignClientProcessor {

    /*** 核心处理前的日志信息预先缓存 */
    private static final String processBeforeLogStr = "调用服务[%s]的方法[%s]参数[%s]";
    /*** 核心处理后的日志信息预先缓存 */
    private static final String processAfterLogStr = "调用服务[%s]的方法[%s]结果[%s]";
    /*** 核心处理异常的日志信息预先缓存 */
    private static final String processExceptionLogStr = "调用服务[%s]的方法[%s]异常[%s]";

    @Override
    public List<String> getIncludePathPattern() {
        return new ArrayList<String>() {{
            add("/**");
        }};
    }

    @Override
    public Object[] processBefore(FeignClientDefinition.Element element, Object[] param) {
        if (log.isDebugEnabled()) {
            log.debug(String
                .format(processBeforeLogStr, element.getFullPath(), element.getMethod(), JSONUtil.toJsonStr(param)));
        }
        return param;
    }

    @Override
    public Object processAfter(FeignClientDefinition.Element element, Object response) {
        if (log.isDebugEnabled()) {
            log.debug(String
                .format(processAfterLogStr, element.getFullPath(), element.getMethod(), JSONUtil.toJsonStr(response)));
        }
        return response;
    }

    @Override
    public void processException(FeignClientDefinition.Element element, Exception e) {
        if (log.isErrorEnabled()) {
            log.error(
                String.format(processExceptionLogStr, element.getFullPath(), element.getMethod(), e.getMessage()));
            log.error("具体异常信息-" + e);
        }
    }

}
