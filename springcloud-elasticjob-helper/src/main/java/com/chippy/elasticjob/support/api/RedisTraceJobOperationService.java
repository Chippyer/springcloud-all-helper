package com.chippy.elasticjob.support.api;

import com.chippy.core.common.constants.GlobalConstantEnum;
import com.chippy.elasticjob.support.domain.JobInfo;
import com.chippy.elasticjob.support.enums.JobStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLiveObjectService;
import org.redisson.api.condition.Condition;
import org.redisson.api.condition.Conditions;

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

    public RedisTraceJobOperationService(RLiveObjectService liveObjectService) {
        this.liveObjectService = liveObjectService;
    }

    @Override
    public List<JobInfo> byOriginalJobName(String originalJobName) {
        return (List<JobInfo>)liveObjectService.find(JobInfo.class,
            Conditions.eq(GlobalConstantEnum.ELASTIC_JOB_INFO_FILED_ORIGINAL_NAME.getConstantValue(), originalJobName));
        // final List<String> jobNameList = this.getJobNameList(originalJobName);
        // return jobNameList.stream().map(this::getCompleteJobInfo).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public List<JobInfo> byOriginalJobName(String originalJobName, JobStatusEnum jobStatusEnum) {
        final Condition eqOriginalCondition =
            Conditions.eq(GlobalConstantEnum.ELASTIC_JOB_INFO_FILED_ORIGINAL_NAME.getConstantValue(), originalJobName);
        final Condition eqStatusCondition = Conditions
            .eq(GlobalConstantEnum.ELASTIC_JOB_INFO_FILED_STATUS.getConstantValue(), jobStatusEnum.toString());
        return (List<JobInfo>)liveObjectService
            .find(JobInfo.class, Conditions.and(eqOriginalCondition, eqStatusCondition));

        // final List<String> jobNameList = this.getJobNameList(originalJobName);
        // String status = jobStatusEnum.toString();
        // return jobNameList.stream().map(jobName -> {
        //     final CompleteJobInfo completeJobInfo = this.getCompleteJobInfo(jobName);
        //     return completeJobInfo.getStatus().equals(status) ? completeJobInfo : null;
        // }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public JobInfo byJobName(String jobName) {
        return liveObjectService.get(JobInfo.class, jobName);
    }

    @Override
    public JobInfo byJobName(String jobName, JobStatusEnum jobStatusEnum) {
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
    public boolean update(JobInfo completeJobInfo) {
        // if (Objects.isNull(completeJobInfo)) {
        //     throw new IllegalArgumentException("传入的更新任务参数为空，无法进行任务信息更新");
        // }
        // final CompleteJobInfo existsCompleteJobInfo = this.byJobName(completeJobInfo.getJobName());
        // if (Objects.isNull(existsCompleteJobInfo)) {
        //     if (log.isDebugEnabled()) {
        //         log.debug("需要更新的任务状态信息不存在");
        //     }
        //     return false;
        // }
        // existsCompleteJobInfo.setCron(completeJobInfo.getCron());
        // existsCompleteJobInfo.setErrorReason(completeJobInfo.getErrorReason());
        // existsCompleteJobInfo.setInvokeDateTime(completeJobInfo.getInvokeDateTime());
        // existsCompleteJobInfo.setJobName(completeJobInfo.getJobName());
        // existsCompleteJobInfo.setJobParameter(completeJobInfo.getJobParameter());
        // existsCompleteJobInfo.setInvokeServiceClass(completeJobInfo.getInvokeServiceClass());
        // existsCompleteJobInfo.setStatus(completeJobInfo.getStatus());
        // existsCompleteJobInfo.setTaskId(completeJobInfo.getTaskId());
        // return true;
        return true;
    }

    @Override
    public List<JobInfo> getUnperformedTask() {
        return (List<JobInfo>)liveObjectService.find(JobInfo.class, Conditions
            .eq(GlobalConstantEnum.ELASTIC_JOB_INFO_FILED_STATUS.getConstantValue(), JobStatusEnum.READY.toString()));
    }

    // private CompleteJobInfo getCompleteJobInfo(String jobName) {
    //     return liveObjectService.get(CompleteJobInfo.class, jobName);
    // }

    // private OriginalJobRelation getOriginalJobRelation(String originalJobName) {
    //     return Objects.nonNull(originalJobName) ? liveObjectService.get(OriginalJobRelation.class, originalJobName) :
    //         null;
    // }

    // private List<String> getJobNameList(String originalJobName) {
    //     if (Objects.isNull(originalJobName)) {
    //         return Collections.emptyList();
    //     }
    //     final OriginalJobRelation originalJobRelation = this.getOriginalJobRelation(originalJobName);
    //     List<String> jobNameList =
    //         Objects.nonNull(originalJobRelation) ? originalJobRelation.getJobNameList() : Collections.emptyList();
    //     return CollectionsUtils.isNotEmpty(jobNameList) ? jobNameList : Collections.emptyList();
    // }
    //
}
