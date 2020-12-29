package com.chippy.elasticjob.support.runner;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import com.chippy.core.common.utils.DateUtil;
import com.chippy.core.common.utils.IpUtil;
import com.chippy.core.common.utils.ObjectsUtil;
import com.chippy.elasticjob.support.api.TraceJobHandler;
import com.chippy.elasticjob.support.api.TraceJobOperationService;
import com.chippy.elasticjob.support.domain.ElasticJobMetaInfo;
import com.chippy.elasticjob.support.domain.JobInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 对任务中未执行的任务重新激活执行
 *
 * @author: chippy
 * @datetime 2020-12-09 10:23
 */
@Slf4j
public class FailToRetryRunner implements CommandLineRunner, ApplicationContextAware {

    private TraceJobOperationService traceJobOperationService;
    private boolean traceMonitor;

    public FailToRetryRunner(TraceJobOperationService traceJobOperationService, boolean traceMonitor) {
        this.traceJobOperationService = traceJobOperationService;
        this.traceMonitor = traceMonitor;
    }

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void run(String... args) throws Exception {
        if (traceMonitor) {
            log.debug("---------------------- 准备对ElasticJob中未执行的任务重新激活执行-开始 ----------------------");
            final ElasticJobMetaInfo metaInfo = ElasticJobMetaInfo.getInstance();
            final String failToRetryServerIp = metaInfo.getFailToRetryServerIp();
            if (ObjectsUtil.isEmpty(failToRetryServerIp)) {
                log.debug("未配置失效重试服务地址-失败重试操作将不会执行");
            }
            final List<String> ips = IpUtil.getCurrentServerIp();
            if (ObjectsUtil.isEmpty(ips)) {
                log.error("当前机器未获取到IP地址-失败重试操作将不会执行");
                return;
            }
            if (!ips.contains(metaInfo.getFailToRetryServerIp())) {
                log.debug("当前机器IP地址不匹配配置地址-失败重试操作将不会执行");
                return;
            }
            this.failToRetry();
            log.debug("---------------------- 准备对ElasticJob中未执行的任务重新激活执行-完成 ----------------------");
        }
    }

    private void failToRetry() {
        final List<JobInfo> unperformedJobInfoTask = traceJobOperationService.getUnperformedTask();
        if (ObjectsUtil.isNotEmpty(unperformedJobInfoTask)) {
            final int size = unperformedJobInfoTask.size();
            AtomicInteger successSize = new AtomicInteger(0);
            AtomicInteger failSize = new AtomicInteger(0);
            for (JobInfo jobInfo : unperformedJobInfoTask) {
                Class clazz = this.classForName(jobInfo);
                if (null == clazz) {
                    String errorReason =
                        "此[" + jobInfo.getInvokeServiceClass() + "]服务名称不在容其中, 请手动处理(将服务执行服务注入Spring容器中)";
                    this.storeErrorReason(jobInfo, errorReason);
                    continue;
                }
                final TraceJobHandler traceJobHandler = (TraceJobHandler)applicationContext.getBean(clazz);
                if (ObjectsUtil.isEmpty(traceJobHandler)) {
                    String errorReason =
                        "此[" + jobInfo.getInvokeServiceClass() + "]服务名称不在容其中, 请手动处理(将服务执行服务注入Spring容器中)";
                    this.storeErrorReason(jobInfo, errorReason);
                    continue;
                }
                this.updateJob(successSize, failSize, jobInfo, traceJobHandler);
            }
            log.debug("总任务数量[" + size + "], 成功数量[" + successSize.get() + "], 失败数量[" + failSize.get());
        }
    }

    private void updateJob(AtomicInteger successSize, AtomicInteger failSize, JobInfo jobInfo,
        TraceJobHandler traceJobHandler) {
        try {
            this.doUpdateJob(jobInfo, traceJobHandler);
            successSize.incrementAndGet();
        } catch (Exception e) {
            log.error("失效重试任务-[" + jobInfo.getJobName() + "]执行异常-" + e.getMessage(), e);
            failSize.incrementAndGet();
        }
    }

    private void doUpdateJob(JobInfo jobInfo, TraceJobHandler traceJobHandler) {
        String jobName = jobInfo.getJobName(), originalJobName = jobInfo.getOriginalJobName();
        String jobParameter = jobInfo.getJobParameter(), invokeDateTimeStr = jobInfo.getInvokeDateTime();

        final DateTime currentDateTime = new DateTime();
        final DateTime invokeDateTime = this.getInvokeDateTime(invokeDateTimeStr);
        if (invokeDateTime.getTime() < currentDateTime.getTime()) {
            // 对于预定执行并执行时间已过期的任务进行延时处理 -> 当前时间+1min
            final DateTime newInvokeDateTime = currentDateTime.offsetNew(DateField.MINUTE, 1);
            jobInfo.setInvokeDateTime(newInvokeDateTime.toStringDefaultTimeZone());
        }
        traceJobHandler.updateJob(jobName, originalJobName, jobParameter, invokeDateTimeStr);
    }

    private DateTime getInvokeDateTime(String invokeDateTime) {
        return new DateTime(invokeDateTime, DateUtil.YYYY_MM_DD_HH_MM_SS);
    }

    private void storeErrorReason(JobInfo jobInfo, String errorReason) {
        jobInfo.setErrorReason(errorReason);
        traceJobOperationService.update(jobInfo);
    }

    private Class<?> classForName(JobInfo jobInfo) {
        try {
            return Class.forName(jobInfo.getInvokeServiceClass());
        } catch (ClassNotFoundException e) {
            log.error("通过执行类路径获取执行类信息异常-" + e.getMessage());
            return null;
        }
    }

}
