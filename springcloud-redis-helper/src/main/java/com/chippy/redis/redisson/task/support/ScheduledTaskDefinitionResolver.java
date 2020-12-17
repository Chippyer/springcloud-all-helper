package com.chippy.redis.redisson.task.support;

import cn.hutool.core.util.ReflectUtil;
import com.chippy.common.constants.GlobalConstantEnum;
import com.chippy.common.utils.AopUtils;
import com.chippy.redis.redisson.task.definition.ScheduledTaskDefinition;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLiveObjectService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
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
        this.initAssignServerIfNecessary();
        this.initNormal();
    }

    private void initAssignServerIfNecessary() {
        final Environment environment = applicationContext.getEnvironment();
        final String assignServer =
            environment.getProperty(GlobalConstantEnum.SPRING_SCHEDULED_ASSIGN_SERVER.getConstantValue());
        if (null != assignServer) {
            TaskUtils.setAssignServer(assignServer);
            if (log.isDebugEnabled()) {
                log.debug("当前执行模式为指定服务IP[" + assignServer + "]执行");
            }
        }
    }

    private void initNormal() {
        final RLiveObjectService liveObjectService = applicationContext.getBean(RLiveObjectService.class);
        final Map<String, DistributedScheduled> beans = applicationContext.getBeansOfType(DistributedScheduled.class);
        beans.forEach((k, clazz) -> {
            final Class<?> targetClazz = AopUtils.getTargetClass(clazz);
            final Method[] methods = ReflectUtil.getMethodsDirectly(targetClazz, false);
            for (Method method : methods) {
                final Scheduled scheduled = method.getAnnotation(Scheduled.class);
                if (null != scheduled) {
                    String taskId = targetClazz.getName() + "_" + method.getName();
                    ScheduledTaskDefinition scheduledTaskMetaInfo = new ScheduledTaskDefinition(taskId, null, false);
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
