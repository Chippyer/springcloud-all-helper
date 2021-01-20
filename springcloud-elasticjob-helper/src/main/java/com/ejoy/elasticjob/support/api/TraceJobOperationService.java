package com.ejoy.elasticjob.support.api;

import com.ejoy.elasticjob.support.domain.JobInfo;
import com.ejoy.elasticjob.support.enums.JobStatusEnum;

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

    void insert(JobInfo jobInfo);

    boolean update(JobInfo jobInfo);

    boolean remove(JobInfo jobInfo);

    List<JobInfo> getUnperformedTask();

}
