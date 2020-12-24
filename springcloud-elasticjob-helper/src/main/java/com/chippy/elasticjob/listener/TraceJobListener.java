package com.chippy.elasticjob.listener;

import com.chippy.elasticjob.support.api.TraceJobOperationService;
import com.chippy.elasticjob.support.domain.JobInfo;
import com.chippy.elasticjob.support.enums.JobStatusEnum;
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

    private TraceJobOperationService completeJobInfOperationService;
    private boolean traceMonitor;

    public TraceJobListener(TraceJobOperationService completeJobInfOperationService, boolean traceMonitor) {
        this.completeJobInfOperationService = completeJobInfOperationService;
        this.traceMonitor = traceMonitor;
    }

    @Override
    public void beforeJobExecuted(ShardingContexts shardingContexts) {
        super.beforeJobExecuted(shardingContexts);
        if (traceMonitor) {
            final JobInfo jobInfo = completeJobInfOperationService.byJobName(shardingContexts.getJobName());
            if (Objects.isNull(jobInfo)) {
                if (log.isErrorEnabled()) {
                    log.error("通过任务名称查询状态为空, 此处不对任务信息进行记录");
                }
                return;
            }
            jobInfo.setShardingItem(shardingContexts.getCurrentJobEventSamplingCount());
            jobInfo.setTaskId(shardingContexts.getTaskId());
            jobInfo.setStatus(JobStatusEnum.ING.toString());
            completeJobInfOperationService.update(jobInfo);
        }
    }

    @Override
    public void afterJobExecuted(ShardingContexts shardingContexts) {
        super.afterJobExecuted(shardingContexts);
        if (traceMonitor) {
            final JobInfo jobInfo = completeJobInfOperationService.byJobName(shardingContexts.getJobName());
            if (Objects.isNull(jobInfo)) {
                if (log.isErrorEnabled()) {
                    log.error("通过任务名称查询状态为空, 此处不对任务信息进行记录");
                }
                return;
            }
            jobInfo.setStatus(JobStatusEnum.OVER.toString());
            completeJobInfOperationService.update(jobInfo);
        }
    }

}
