package com.chippy.elasticjob.support.runner;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import com.chippy.common.utils.IpUtil;
import com.chippy.common.utils.ObjectsUtil;
import com.chippy.elasticjob.support.api.common.ElasticJobProcessor;
import com.chippy.elasticjob.support.api.db.IJobInfoService;
import com.chippy.elasticjob.support.domain.ElasticJobMetaInfo;
import com.chippy.elasticjob.support.domain.enums.JobStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 对任务中未执行的任务重新激活执行
 *
 * @author: chippy
 * @datetime 2020-12-09 10:23
 */
@Component
@Slf4j
public class FailToRetryRunner implements CommandLineRunner, ApplicationContextAware {

    @Resource
    private IJobInfoService jobInfoService;

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void run(String... args) throws Exception {
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

        final List<JobInfo> jobInfos = jobInfoService.byStatus(JobStatusEnum.READY);
        if (ObjectsUtil.isNotEmpty(jobInfos)) {
            final int size = jobInfos.size();
            AtomicInteger successSize = new AtomicInteger(0);
            AtomicInteger failSize = new AtomicInteger(0);
            for (JobInfo jobInfo : jobInfos) {
                Class clazz = this.classForName(jobInfo);
                if (null == clazz) {
                    String errorReason = "此[" + jobInfo.getInvokeServiceClass() + "]服务名称不在容其中, 请手动处理";
                    this.storeErrorReason(jobInfo, errorReason);
                    continue;
                }
                final ElasticJobProcessor elasticJobProcessor = (ElasticJobProcessor)applicationContext.getBean(clazz);
                if (ObjectsUtil.isEmpty(elasticJobProcessor)) {
                    String errorReason = "此[" + jobInfo.getInvokeServiceClass() + "]服务名称不在容其中, 请手动处理";
                    this.storeErrorReason(jobInfo, errorReason);
                    return;
                }
                try {
                    this.doCreateJob(jobInfo, elasticJobProcessor);
                    successSize.incrementAndGet();
                } catch (Exception e) {
                    log.error("失效重试任务-[" + jobInfo.getJobName() + "]执行异常-" + e.getMessage(), e);
                    failSize.incrementAndGet();
                }
            }
            log.debug("总任务数量[" + size + "], 成功数量[" + successSize.get() + "], 失败数量[" + failSize.get());
        }
        log.debug("---------------------- 准备对ElasticJob中未执行的任务重新激活执行-完成 ----------------------");
    }

    private void doCreateJob(JobInfo jobInfo, ElasticJobProcessor elasticJobProcessor) {
        final DateTime currentDateTime = new DateTime();
        if (jobInfo.getInvokeDateTime().getTime() < currentDateTime.getTime()) {
            // 对于预定执行并执行时间已过期的任务进行延时处理 -> 当前时间+1min
            final DateTime newInvokeDateTime = currentDateTime.offsetNew(DateField.MINUTE, 1);
            jobInfo.setInvokeDateTime(newInvokeDateTime);
            elasticJobProcessor.createJob(jobInfo);
        } else {
            elasticJobProcessor.createJob(jobInfo);
        }
    }

    private void storeErrorReason(JobInfo jobInfo, String errorReason) {
        jobInfo.setErrorReason(errorReason);
        jobInfo.setDeleted(Boolean.TRUE);
        jobInfoService.updateByPrimaryKeySelective(jobInfo);
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
