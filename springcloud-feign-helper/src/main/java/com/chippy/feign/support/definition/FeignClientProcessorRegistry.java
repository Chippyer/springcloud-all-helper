package com.chippy.feign.support.definition;

import com.chippy.common.utils.ObjectsUtil;
import com.chippy.feign.support.api.processor.FeignClientProcessor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * FeignClientHelper处理工具{@link FeignClientProcessor}注册器
 *
 * @author: chippy
 * @datetime 2020/12/13 15:31
 */
@Slf4j
public class FeignClientProcessorRegistry {

    private static Map<String, List<FeignClientProcessor>> feignClientProcessorMap = new HashMap<>();

    public static List<FeignClientProcessor> get(String fullPath) {
        return ObjectsUtil.isEmpty(feignClientProcessorMap) ? null : feignClientProcessorMap.get(fullPath);
    }

    public static void register(String fullPath, List<FeignClientProcessor> feignClientProcessors) {
        if (null == feignClientProcessors || feignClientProcessors.isEmpty()) {
            log.debug("注册FeignClientProcessor不能为空"); // 此处不应进入
            return;
        }
        for (FeignClientProcessor feignClientProcessor : feignClientProcessors) {
            register(fullPath, feignClientProcessor);
        }
    }

    public static void register(String fullPath, FeignClientProcessor feignClientProcessor) {
        if (null == feignClientProcessor) {
            log.debug("注册FeignClientProcessor不能为空"); // 此处不应进入
            return;
        }
        final List<FeignClientProcessor> feignClientProcessors = feignClientProcessorMap.get(fullPath);
        if (ObjectsUtil.isEmpty(feignClientProcessors)) {
            final LinkedList<FeignClientProcessor> newFeignClientProcessor = new LinkedList<>();
            newFeignClientProcessor.add(feignClientProcessor);
            feignClientProcessorMap.put(fullPath, newFeignClientProcessor);
            return;
        }

        feignClientProcessors.add(feignClientProcessor);
        feignClientProcessorMap.put(fullPath, feignClientProcessors);
    }

}
