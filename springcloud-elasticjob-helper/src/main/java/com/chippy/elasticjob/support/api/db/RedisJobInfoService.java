package com.chippy.elasticjob.support.api.db;

import com.chippy.common.utils.CollectionsUtils;
import com.chippy.elasticjob.support.api.db.redis.JobInfo;
import com.chippy.elasticjob.support.api.db.redis.OriginalJobRelation;
import com.chippy.elasticjob.support.domain.enums.JobStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLiveObjectService;

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
public class RedisJobInfoService {

    private RLiveObjectService liveObjectService;

    public RedisJobInfoService(RLiveObjectService liveObjectService) {
        this.liveObjectService = liveObjectService;
    }

    public List<JobInfo> byOriginalJobName(String originalJobName) {
        final OriginalJobRelation originalJobInfo = liveObjectService.get(OriginalJobRelation.class, originalJobName);
        if (Objects.isNull(originalJobInfo)) {
            return Collections.emptyList();
        }

        final List<String> jobNameList = originalJobInfo.getJobNameList();
        if (CollectionsUtils.isEmpty(jobNameList)) {
            return Collections.emptyList();
        }

        return jobNameList.stream().map(jobName -> liveObjectService.get(JobInfo.class, jobName))
            .filter(Objects::nonNull).collect(Collectors.toList());
    }

    public JobInfo byJobName(String jobName) {
        liveObjectService.get(OriginalJobRelation.class) return null;
    }

    public JobInfo byJobName(String jobName, JobStatusEnum jobStatusEnum) {
        return null;
    }

    public List<JobInfo> byStatus(JobStatusEnum jobStatusEnum) {
        return null;
    }

    public List<JobInfo> byStatus(String jobStatus) {
        return null;
    }

    public boolean updateByPrimaryKeySelective(JobInfo jobInfo) {
        return false;
    }

    public void insertSelective(JobInfo jobInfo) {

    }
}
