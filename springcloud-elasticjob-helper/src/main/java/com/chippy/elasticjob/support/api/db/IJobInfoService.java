package com.chippy.elasticjob.support.api.db;

import com.chippy.elasticjob.support.domain.enums.JobStatusEnum;

import java.util.List;

/**
 * 任务信息服务接口
 *
 * @author: chippy
 * @datetime 2020-12-09 13:11
 */
public interface IJobInfoService {

    List<JobInfo> byOriginalJobName(String originalJobName);

    JobInfo byOriginalJobNameIgnoreOverStatus(String originalJobName, JobStatusEnum jobStatusEnum);

    List<JobInfo> byOriginalJobName(String originalJobName, List<String> status);

    JobInfo byJobName(String jobName);

    JobInfo byJobName(String jobName, JobStatusEnum jobStatusEnum);

    List<JobInfo> byStatus(JobStatusEnum jobStatusEnum);

    List<JobInfo> byStatus(String jobStatus);

    boolean updateByPrimaryKeySelective(JobInfo jobInfo);

    void insertSelective(JobInfo jobInfo);

}
