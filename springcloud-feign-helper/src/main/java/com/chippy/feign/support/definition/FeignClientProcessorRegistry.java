package com.chippy.feign.support.definition;

import com.chippy.common.utils.ObjectsUtil;
import com.chippy.feign.support.api.processor.FeignClientProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;

import java.util.*;

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
            final List<FeignClientProcessor> newFeignClientProcessor = new LinkedList<>();
            newFeignClientProcessor.add(feignClientProcessor);
            feignClientProcessorMap.put(fullPath, newFeignClientProcessor);
            return;
        }

        feignClientProcessors.add(feignClientProcessor);
        feignClientProcessorMap.put(fullPath, feignClientProcessors);
        feignClientProcessorMap.forEach((k, fcps) -> fcps.sort(new FeignClientProcessorComparator()));
    }

    static class FeignClientProcessorComparator implements Comparator<FeignClientProcessor> {
        @Override
        public int compare(FeignClientProcessor fcp1, FeignClientProcessor fcp2) {
            final Order o1 = fcp1.getClass().getAnnotation(Order.class);
            final Order o2 = fcp2.getClass().getAnnotation(Order.class);
            int o1Value = Integer.MAX_VALUE, o2Value = Integer.MAX_VALUE;
            if (null != o1) {
                o1Value = o1.value();
            }
            if (null != o2) {
                o2Value = o2.value();
            }
            return o1Value - o2Value;
        }
    }

}
