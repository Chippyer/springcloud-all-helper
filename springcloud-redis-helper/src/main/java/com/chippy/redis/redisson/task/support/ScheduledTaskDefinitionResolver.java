package com.chippy.redis.redisson.task.support;

import cn.hutool.core.util.ReflectUtil;
import com.chippy.redis.redisson.task.domain.ScheduledTaskMetaInfo;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLiveObjectService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.annotation.Scheduled;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * FeignClientHelper元素信息解析器
 *
 * @author chippy
 */
@Slf4j
public class ScheduledTaskDefinitionResolver implements ApplicationContextAware, InitializingBean {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void init() {
        final Map<String, Object> scheduledAnnotations = applicationContext.getBeansWithAnnotation(Scheduled.class);
        final RLiveObjectService liveObjectService = applicationContext.getBean(RLiveObjectService.class);
        scheduledAnnotations.forEach((k, clazz) -> {
            final Class<?> targetClazz = clazz.getClass();
            final Method[] methods = ReflectUtil.getMethods(targetClazz);
            for (Method method : methods) {
                final Scheduled scheduled = method.getAnnotation(Scheduled.class);
                if (null != scheduled) {
                    String taskId = targetClazz.getName() + "_" + method.getName();
                    ScheduledTaskMetaInfo scheduledTaskMetaInfo = new ScheduledTaskMetaInfo(taskId, null, false);
                    liveObjectService.merge(scheduledTaskMetaInfo);
                }
            }
        });
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.init();
    }

}
