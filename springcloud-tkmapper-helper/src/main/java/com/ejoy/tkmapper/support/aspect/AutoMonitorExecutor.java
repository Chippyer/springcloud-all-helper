package com.ejoy.tkmapper.support.aspect;

import com.ejoy.tkmapper.support.api.IMonitorService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * 自动执行监控操作类
 *
 * @author: chippy
 * @datetime 2021/2/21 12:49
 */
@Aspect
@Slf4j
public class AutoMonitorExecutor {

    private IMonitorService monitorService;

    public AutoMonitorExecutor(IMonitorService monitorService) {
        this.monitorService = monitorService;
    }

    @Pointcut("@annotation(com.ejoy.tkmapper.annotation.AutoMonitor)")
    private void monitorPointCut() {
    }

    @AfterReturning(value = "monitorPointCut()", returning = "result")
    public void afterReturning(JoinPoint point, Object result) throws Throwable {
        monitorService.process(result);
    }

}
