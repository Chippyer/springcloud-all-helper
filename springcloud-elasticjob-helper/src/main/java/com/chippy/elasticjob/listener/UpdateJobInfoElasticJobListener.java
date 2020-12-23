package com.chippy.elasticjob.listener;

import com.chippy.elasticjob.support.api.db.IJobInfoService;
import com.chippy.elasticjob.support.domain.enums.JobStatusEnum;
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
public class UpdateJobInfoElasticJobListener extends MonitorElasticJobListener {

    private IJobInfoService jobInfoService;

    public UpdateJobInfoElasticJobListener(IJobInfoService jobInfoService) {
        this.jobInfoService = jobInfoService;
    }

    @Override
    public void beforeJobExecuted(ShardingContexts shardingContexts) {
        super.beforeJobExecuted(shardingContexts);
        JobInfo existsJobInfo = jobInfoService.byJobName(shardingContexts.getJobName());
        if (Objects.isNull(existsJobInfo)) {
            if (log.isErrorEnabled()) {
                log.error("通过任务名称查询状态为空, 此处不对任务信息进行记录");
            }
            return;
        }
        existsJobInfo.setShardingItem(shardingContexts.getCurrentJobEventSamplingCount());
        existsJobInfo.setTaskId(shardingContexts.getTaskId());
        existsJobInfo.setStatus(JobStatusEnum.ING.toString());
        jobInfoService.updateByPrimaryKeySelective(existsJobInfo);
    }

    @Override
    public void afterJobExecuted(ShardingContexts shardingContexts) {
        super.afterJobExecuted(shardingContexts);
        final JobInfo existsJobInfo = jobInfoService.byJobName(shardingContexts.getJobName());
        if (Objects.isNull(existsJobInfo)) {
            if (log.isErrorEnabled()) {
                log.error("通过任务名称查询状态为空, 此处不对任务信息进行记录");
            }
            return;
        }
        existsJobInfo.setStatus(JobStatusEnum.OVER.toString());
        jobInfoService.updateByPrimaryKeySelective(existsJobInfo);
    }

}
