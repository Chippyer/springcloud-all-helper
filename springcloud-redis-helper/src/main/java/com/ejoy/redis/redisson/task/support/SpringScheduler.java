package com.ejoy.redis.redisson.task.support;

import cn.hutool.json.JSONUtil;
import com.ejoy.core.common.utils.IpUtil;
import com.ejoy.redis.redisson.task.definition.ScheduledTaskDefinition;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.redisson.api.RLiveObjectService;

/**
 * SpringScheduled任务执行器
 *
 * @author: chippy
 * @datetime 2020-12-17 16:39
 */
@Aspect
@Slf4j
public class SpringScheduler {

    private RLiveObjectService liveObjectService;

    public SpringScheduler(RLiveObjectService liveObjectService) {
        this.liveObjectService = liveObjectService;
    }

    @Pointcut("@annotation(org.springframework.scheduling.annotation.Scheduled)")
    private void taskPointCut() {
    }

    @Around("taskPointCut()")
    public void around(ProceedingJoinPoint joinPoint) throws Throwable {
        final Signature signature = joinPoint.getSignature();
        final String taskId = signature.getDeclaringTypeName() + "_" + signature.getName();

        final String assignServer = TaskUtils.getAssignServer();
        if (!TaskUtils.isContain()) {
            if (log.isTraceEnabled()) {
                log.debug("当前服务IP[" + assignServer + "]无权限执行任务[" + taskId + "]");
            }
            return;
        }

        this.doProceed(joinPoint, taskId);
    }

    private void doProceed(ProceedingJoinPoint joinPoint, String taskId) throws Throwable {
        final ScheduledTaskDefinition scheduledTaskDefinition =
            liveObjectService.get(ScheduledTaskDefinition.class, taskId);
        if (scheduledTaskDefinition.isStatus()) {
            if (log.isDebugEnabled()) {
                log.debug("任务已执行-" + JSONUtil.toJsonStr(scheduledTaskDefinition));
            }
            return;
        }
        scheduledTaskDefinition.setStatus(true);
        joinPoint.proceed();
        scheduledTaskDefinition.setLastProcessServerIps(IpUtil.getCurrentServerIp());
        scheduledTaskDefinition.setStatus(false);
    }

}
