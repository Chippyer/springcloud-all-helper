package com.ejoy.elasticjob.support.api;

import com.ejoy.core.common.constants.GlobalConstantEnum;
import com.ejoy.core.common.utils.CollectionsUtils;
import com.ejoy.elasticjob.support.domain.JobInfo;
import com.ejoy.elasticjob.support.enums.JobStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLiveObjectService;
import org.redisson.api.condition.Condition;
import org.redisson.api.condition.Conditions;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
        final List<JobInfo> jobInfos = (List<JobInfo>)liveObjectService
            .find(JobInfo.class, Conditions.and(eqOriginalCondition, this.getServerCondition()));
        if (CollectionsUtils.isEmpty(jobInfos)) {
            return Collections.emptyList();
        }
        return jobInfos.stream().filter(e -> e.getStatus().equals(jobStatusEnum.toString()))
            .collect(Collectors.toList());

    }

    @Override
    public JobInfo byJobName(String jobName) {
        return Objects.isNull(jobName) ? null : liveObjectService.get(JobInfo.class, jobName);
    }

    @Override
    public JobInfo byJobName(String jobName, JobStatusEnum jobStatusEnum) {
        final JobInfo jobInfo = this.byJobName(jobName);
        if (Objects.isNull(jobInfo)) {
            return null;
        }
        if (Objects.isNull(jobStatusEnum)) {
            return jobInfo;
        }
        return jobInfo.getStatus().equals(jobStatusEnum.toString()) ? jobInfo : null;
    }

    @Override
    public void insert(JobInfo jobInfo) {
        if (Objects.isNull(jobInfo)) {
            throw new IllegalArgumentException("传入的存储任务参数为空，无法进行任务信息存储");
        }
        jobInfo.setServer(server);
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
        final List<JobInfo> jobInfos =
            (List<JobInfo>)liveObjectService.find(JobInfo.class, Conditions.and(this.getServerCondition()));
        if (CollectionsUtils.isEmpty(jobInfos)) {
            return Collections.emptyList();
        }
        return jobInfos.stream().filter(e -> e.getStatus().equals(JobStatusEnum.READY.toString()))
            .collect(Collectors.toList());
    }

    private Condition getServerCondition() {
        return Conditions.eq(GlobalConstantEnum.ELASTIC_JOB_INFO_FILED_SERVER.getConstantValue(), server);
    }

}
