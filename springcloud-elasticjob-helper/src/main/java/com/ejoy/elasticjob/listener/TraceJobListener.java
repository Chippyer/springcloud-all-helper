package com.ejoy.elasticjob.listener;

import com.ejoy.elasticjob.support.api.TraceJobOperationService;
import com.ejoy.elasticjob.support.domain.JobInfo;
import com.ejoy.elasticjob.support.enums.JobStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.elasticjob.api.listener.ShardingContexts;

import java.util.Objects;

/**
 * 更新状态信息任务监听器
 *
 * @author: chippy
 * @datetime 2020-12-09 13:05
 */
@Slf4j
public class TraceJobListener extends MonitorJobListener {

    private TraceJobOperationService traceJobOperationService;
    private boolean traceMonitor;

    public TraceJobListener(TraceJobOperationService traceJobOperationService, boolean traceMonitor) {
        this.traceJobOperationService = traceJobOperationService;
        this.traceMonitor = traceMonitor;
    }

    @Override
    public void beforeJobExecuted(ShardingContexts shardingContexts) {
        super.beforeJobExecuted(shardingContexts);
        if (traceMonitor) {
            final JobInfo jobInfo =
                traceJobOperationService.byJobName(shardingContexts.getJobName(), JobStatusEnum.READY);
            if (Objects.isNull(jobInfo)) {
                if (log.isErrorEnabled()) {
                    log.error("通过任务名称查询状态为空, 此处不对任务信息进行记录");
                }
                return;
            }
            jobInfo.setShardingItem(shardingContexts.getCurrentJobEventSamplingCount());
            jobInfo.setTaskId(shardingContexts.getTaskId());
            jobInfo.setStatus(JobStatusEnum.ING.toString());
            traceJobOperationService.update(jobInfo);
        }
    }

    @Override
    public void afterJobExecuted(ShardingContexts shardingContexts) {
        super.afterJobExecuted(shardingContexts);
        if (traceMonitor) {
            final JobInfo jobInfo =
                traceJobOperationService.byJobName(shardingContexts.getJobName(), JobStatusEnum.ING);
            if (Objects.isNull(jobInfo)) {
                if (log.isErrorEnabled()) {
                    log.error("通过任务名称查询状态为空, 此处不对任务信息进行记录");
                }
                return;
            }
            // jobInfo.setStatus(JobStatusEnum.OVER.toString());
            traceJobOperationService.remove(jobInfo);
        }
    }

}
