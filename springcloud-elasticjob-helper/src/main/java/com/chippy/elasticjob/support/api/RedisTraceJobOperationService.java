package com.chippy.elasticjob.support.api;

import com.chippy.core.common.constants.GlobalConstantEnum;
import com.chippy.elasticjob.support.domain.JobInfo;
import com.chippy.elasticjob.support.enums.JobStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLiveObjectService;
import org.redisson.api.condition.Condition;
import org.redisson.api.condition.Conditions;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 任务信息服务接口REDIS实现
 *
 * @author: chippy
 * @datetime 2020-12-11 0:34
 */
@Slf4j
public class RedisTraceJobOperationService implements TraceJobOperationService {

    private RLiveObjectService liveObjectService;
    private String server;

    public RedisTraceJobOperationService(RLiveObjectService liveObjectService, String server) {
        this.liveObjectService = liveObjectService;
        this.server = server;
    }

    @Override
    public List<JobInfo> byOriginalJobName(String originalJobName) {
        if (Objects.isNull(originalJobName)) {
            return Collections.emptyList();
        }

        final Condition eqOriginalJobNameCondition =
            Conditions.eq(GlobalConstantEnum.ELASTIC_JOB_INFO_FILED_ORIGINAL_NAME.getConstantValue(), originalJobName);
        return (List<JobInfo>)liveObjectService
            .find(JobInfo.class, Conditions.and(eqOriginalJobNameCondition, this.getServerCondition()));
    }

    @Override
    public List<JobInfo> byOriginalJobName(String originalJobName, JobStatusEnum jobStatusEnum) {
        if (Objects.isNull(jobStatusEnum)) {
            return this.byOriginalJobName(originalJobName);
        }
        final Condition eqOriginalCondition =
            Conditions.eq(GlobalConstantEnum.ELASTIC_JOB_INFO_FILED_ORIGINAL_NAME.getConstantValue(), originalJobName);
        final Condition eqStatusCondition = Conditions
            .eq(GlobalConstantEnum.ELASTIC_JOB_INFO_FILED_STATUS.getConstantValue(), jobStatusEnum.toString());
        return (List<JobInfo>)liveObjectService
            .find(JobInfo.class, Conditions.and(eqOriginalCondition, eqStatusCondition, this.getServerCondition()));
    }

    @Override
    public JobInfo byJobName(String jobName) {
        return Objects.isNull(jobName) ? null : liveObjectService.get(JobInfo.class, jobName);
    }

    @Override
    public JobInfo byJobName(String jobName, JobStatusEnum jobStatusEnum) {
        if (Objects.isNull(jobStatusEnum)) {
            return this.byJobName(jobName);
        }
        final JobInfo jobInfo = this.byJobName(jobName);
        if (Objects.isNull(jobInfo)) {
            return null;
        }
        return jobInfo.getStatus().equals(jobStatusEnum.toString()) ? jobInfo : null;
    }

    @Override
    public void insert(JobInfo jobInfo) {
        if (Objects.isNull(jobInfo)) {
            throw new IllegalArgumentException("传入的存储任务参数为空，无法进行任务信息存储");
        }
        liveObjectService.merge(jobInfo);
    }

    @Override
    public boolean update(JobInfo jobInfo) {
        return true;
    }

    @Override
    public boolean remove(JobInfo jobInfo) {
        if (Objects.isNull(jobInfo)) {
            throw new IllegalArgumentException("传入的存储任务参数为空，无法进行任务信息删除");
        }
        liveObjectService.delete(jobInfo);
        return true;
    }

    @Override
    public List<JobInfo> getUnperformedTask() {
        final Condition eqStatusCondition = Conditions
            .eq(GlobalConstantEnum.ELASTIC_JOB_INFO_FILED_STATUS.getConstantValue(), JobStatusEnum.READY.toString());
        return (List<JobInfo>)liveObjectService
            .find(JobInfo.class, Conditions.and(eqStatusCondition, this.getServerCondition()));
    }

    private Condition getServerCondition() {
        return Conditions.eq(GlobalConstantEnum.ELASTIC_JOB_INFO_FILED_SERVER.getConstantValue(), server);
    }

}
