package com.chippy.redis.redisson.task.aop;

import cn.hutool.json.JSONUtil;
import com.chippy.common.utils.IpUtil;
import com.chippy.redis.redisson.task.domain.ScheduledTaskMetaInfo;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.redisson.api.RLiveObjectService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 切面进入Spring定时任务控制任务执行
 *
 * @author: chippy
 * @datetime 2020-12-17 16:39
 */
@Aspect
@Component
@Slf4j
public class SpringScheduledMaster {

    @Resource
    private RLiveObjectService liveObjectService;

    /**
     * 需要切入的注解方法
     *
     * @author chippy
     */
    @Pointcut("@annotation(org.springframework.scheduling.annotation.Scheduled)")
    private void taskPointCut() {
    }

    @Around("taskPointCut()")
    public void around(ProceedingJoinPoint joinPoint) throws Throwable {
        final Signature signature = joinPoint.getSignature();
        final String id = signature.getClass().getName() + ":" + signature.getName();
        final ScheduledTaskMetaInfo scheduledTaskMetaInfo = liveObjectService.get(ScheduledTaskMetaInfo.class, id);
        if (scheduledTaskMetaInfo.isStatus()) {
            if (log.isDebugEnabled()) {
                log.debug("任务已执行-" + JSONUtil.toJsonStr(scheduledTaskMetaInfo));
            }
            return;
        }
        joinPoint.proceed();
        scheduledTaskMetaInfo.setLastProcessServerIps(IpUtil.getCurrentServerIp());
        scheduledTaskMetaInfo.setStatus(false);
    }

}
