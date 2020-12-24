package com.chippy.elasticjob.support.api;

import com.chippy.elasticjob.support.domain.JobInfo;
import com.chippy.elasticjob.support.enums.JobStatusEnum;

import java.util.List;

/**
 * 完整的任务信息操作服务
 *
 * @author: chippy
 * @datetime: 2020-12-24 12:53
 */
public interface TraceJobOperationService {

    List<JobInfo> byOriginalJobName(String originalJobName);

    List<JobInfo> byOriginalJobName(String originalJobName, JobStatusEnum jobStatusEnum);

    JobInfo byJobName(String jobName);

    JobInfo byJobName(String jobName, JobStatusEnum jobStatusEnum);

    void insert(JobInfo completeJobInfo);

    boolean update(JobInfo completeJobInfo);

    List<JobInfo> getUnperformedTask();

}
