package com.chippy.elasticjob.support.api;

import cn.hutool.json.JSONUtil;
import com.chippy.elasticjob.support.domain.JobInfo;
import com.chippy.elasticjob.support.enums.JobStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 抽象实现状态跟踪记录类型任务
 * 实现该类一定要确保任务是实现{@link AbstractTraceJobHandler}，以保证任务状态跟踪记录
 * 一定要确保实现{@link TraceJobProcessor}，以保证任务进行相关业务操作
 *
 * @author: chippy
 * @datetime 2020-11-16 15:00
 */
@Slf4j
public abstract class AbstractTraceJob<T> implements SimpleJob {

    private static final String LOG_TEMPLATE = "通用定时任务功能实现-%s";

    @Resource
    private TraceJobOperationService completeJobInfOperationService;

    @Autowired
    private TraceJobProcessor<T> elasticJobBusinessProcessor;

    protected abstract Class<T> getGenericClass();

    protected void doExecute(String jobName, String jobParameter) {
        try {
            final JobInfo jobInfo = completeJobInfOperationService.byJobName(jobName, JobStatusEnum.ING);
            if (Objects.isNull(jobInfo)) {
                if (log.isErrorEnabled()) {
                    log.error("任务[" + jobName + "]状态已被修改，本次任务不做任何处理");
                }
                // TODO 记录一下ERROR MSG
                return;
            }
            T data = this.getJobParameter(jobParameter);
            elasticJobBusinessProcessor.processCronJob(data);
        } catch (Exception e) {
            String exceptionMessage = "异常信息-ex:[" + e.getMessage() + "]";
            log.error(String.format(LOG_TEMPLATE, exceptionMessage));
            throw e;
        }
    }

    @SuppressWarnings("unchecked")
    private T getJobParameter(String jobParameter) {
        T data;
        final Class<T> genericClass = this.getGenericClass();
        if (Objects.equals(genericClass, String.class)) {
            data = (T)jobParameter;
        } else {
            data = JSONUtil.toBean(jobParameter, this.getGenericClass());
        }
        return data;
    }

}
