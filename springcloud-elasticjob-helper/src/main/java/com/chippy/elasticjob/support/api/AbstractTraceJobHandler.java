package com.chippy.elasticjob.support.api;

import cn.hutool.core.lang.Assert;
import com.chippy.core.common.utils.CollectionsUtils;
import com.chippy.elasticjob.exception.DuplicateCreationException;
import com.chippy.elasticjob.exception.JobInfoModifyException;
import com.chippy.elasticjob.support.domain.JobInfo;
import com.chippy.elasticjob.support.enums.JobStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.elasticjob.api.ElasticJob;
import org.apache.shardingsphere.elasticjob.api.JobConfiguration;
import org.apache.shardingsphere.elasticjob.api.listener.ElasticJobListener;
import org.apache.shardingsphere.elasticjob.infra.pojo.JobConfigurationPOJO;
import org.apache.shardingsphere.elasticjob.lite.api.bootstrap.impl.ScheduleJobBootstrap;
import org.apache.shardingsphere.elasticjob.lite.lifecycle.api.JobConfigurationAPI;
import org.apache.shardingsphere.elasticjob.lite.lifecycle.api.JobOperateAPI;
import org.apache.shardingsphere.elasticjob.lite.lifecycle.api.JobStatisticsAPI;
import org.apache.shardingsphere.elasticjob.lite.lifecycle.domain.JobBriefInfo;
import org.apache.shardingsphere.elasticjob.reg.zookeeper.ZookeeperRegistryCenter;
import org.apache.shardingsphere.elasticjob.tracing.api.TracingConfiguration;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 任务状态跟踪记录抽象实现基础功能
 * 一般来说和{@link AbstractTraceJob}是想配合着用的
 *
 * @author: chippy
 * @datetime 2020-11-11 18:14
 */
@Slf4j
public abstract class AbstractTraceJobHandler implements TraceJobHandler {

    @Resource
    protected ZookeeperRegistryCenter registryCenter;

    @Resource
    protected ElasticJobListener elasticJobListener;

    @Resource
    protected JobConfigurationAPI jobConfigurationAPI;

    @Resource
    protected JobStatisticsAPI jobStatisticsAPI;

    @Resource
    protected JobOperateAPI jobOperateAPI;

    @Resource
    private TracingConfiguration tracingConfiguration;

    @Resource
    private TraceJobOperationService traceJobOperationService;

    public abstract ElasticJob getJob();

    public abstract String getErrorMessageFormat();

    @Override
    public void createJob(String originalJobName, String jobParameter, String invokeDateTime) {
        Assert.notNull(originalJobName, "需要更新的定时原任务名称不能为空");
        Assert.notNull(invokeDateTime, "预定执行时间不能为空");
        if (log.isDebugEnabled()) {
            log.debug(
                "创建定时任务[originalJobName:" + originalJobName + ", jobParameter:" + jobParameter + ", invokeDateTime:"
                    + invokeDateTime + "]");
        }

        try {
            final List<JobInfo> jobInfos =
                traceJobOperationService.byOriginalJobName(originalJobName, JobStatusEnum.READY);
            if (CollectionsUtils.isNotEmpty(jobInfos)) {
                throw new DuplicateCreationException("任务信息[" + originalJobName + "]已存在");
            }

            final JobInfo jobInfo = this.buildReadyStatusJobInfo(originalJobName, jobParameter, invokeDateTime);
            this.doCreateJob(jobInfo);
            traceJobOperationService.insert(jobInfo);
        } catch (Exception e) {
            log.error(String.format(getErrorMessageFormat() + "-%s", e.getMessage()));
            throw e;
        }
    }

    private void doCreateJob(JobInfo jobInfo) {
        JobConfiguration jobConfig =
            JobConfiguration.newBuilder(jobInfo.getJobName(), jobInfo.getShardingTotalCount()).cron(jobInfo.getCron())
                .jobParameter(jobInfo.getJobParameter()).failover(Boolean.TRUE)
                .shardingItemParameters(jobInfo.getShardingParameter()).build();
        new ScheduleJobBootstrap(registryCenter, this.getJob(), jobConfig, tracingConfiguration, elasticJobListener)
            .schedule();
    }

    @Override
    public void removeJob(String originalJobName) {
        Assert.notNull(originalJobName, "需要移除的定时任务名称不能为空");
        if (log.isDebugEnabled()) {
            log.debug("移除定时任务:[" + originalJobName + "]");
        }

        final List<JobInfo> jobInfos = traceJobOperationService.byOriginalJobName(originalJobName, JobStatusEnum.READY);
        if (CollectionsUtils.isNotEmpty(jobInfos)) {
            this.doRemove(jobInfos.get(0));
        }
    }

    private void doRemove(JobInfo jobInfo) {
        String jobName = jobInfo.getJobName();
        jobOperateAPI.disable(jobName, null);
        jobOperateAPI.remove(jobName, null);
        this.buildOverStatusJobInfo(jobInfo);
        traceJobOperationService.update(jobInfo);
    }

    @Override
    public void updateJob(String originalJobName, String jobParameter, String invokeDateTime) {
        Assert.notNull(originalJobName, "需要更新的定时原任务名称不能为空");
        Assert.notNull(invokeDateTime, "预定执行时间不能为空");
        if (log.isDebugEnabled()) {
            log.debug(
                "更新定时任务[originalJobName:" + originalJobName + ", jobParameter:" + jobParameter + ", invokeDateTime:"
                    + invokeDateTime + "]");
        }

        final List<JobInfo> jobInfos = traceJobOperationService.byOriginalJobName(originalJobName, JobStatusEnum.READY);
        if (CollectionsUtils.isEmpty(jobInfos)) {
            throw new JobInfoModifyException("更新任务[" + originalJobName + "]不存在");
        }
        this.doUpdateJob(originalJobName, jobParameter, invokeDateTime, jobInfos.get(0));
    }

    @Override
    public void updateJob(String jobName, String originalJobName, String jobParameter, String invokeDateTime) {
        Assert.notNull(originalJobName, "需要更新的定时原任务名称不能为空");
        Assert.notNull(invokeDateTime, "预定执行时间不能为空");
        if (log.isDebugEnabled()) {
            log.debug(
                "更新定时任务[jobName:" + jobName + ", originalJobName:" + originalJobName + ", jobParameter:" + jobParameter
                    + ", invokeDateTime:" + invokeDateTime + "]");
        }

        if (Objects.isNull(jobName)) {
            this.updateJob(originalJobName, jobParameter, invokeDateTime);
            return;
        }

        try {
            /*
               为了避免任务名称变更，出现两条同样的逻辑但是不同时间点执行的任务
               列入：A任务需要进修修改， 查询任务状态后发现任务名称变更了查询不存在则新增任务任务状态B
               此时出现A, B两条任务均存在任务状态数据中，此时服务器宕机重启则会导致A, B都将被启用
               ---
               故此此处不进行任何修改操作，用删除插入两个动作进行弥补
             */
            this.doUpdateJob(originalJobName, jobParameter, invokeDateTime,
                traceJobOperationService.byJobName(jobName));
        } catch (Exception e) {
            String exceptionMessage = "更新的定时任务:[" + originalJobName + "]信息已不存在-[" + e.getMessage() + "]";
            log.error(exceptionMessage);
            throw e;
        }
    }

    private void doUpdateJob(String originalJobName, String jobParameter, String invokeDateTime, JobInfo jobInfo) {
        if (Objects.nonNull(jobInfo)) {
            this.doRemove(jobInfo);
        }
        this.createJob(originalJobName, jobParameter, invokeDateTime);
    }

    @Override
    public List<JobConfigurationPOJO> getJob(String originalJobName) {
        if (Objects.isNull(originalJobName)) {
            return Collections.emptyList();
        }
        if (log.isDebugEnabled()) {
            log.debug("获取定时任务:[" + originalJobName + "]");
        }
        final List<JobInfo> jobInfos = traceJobOperationService.byOriginalJobName(originalJobName, null);
        if (CollectionUtils.isEmpty(jobInfos)) {
            return Collections.emptyList();
        }
        return CollectionsUtils.isEmpty(jobInfos) ? Collections.emptyList() : jobInfos.stream()
            .map(completeJobInfo -> jobConfigurationAPI.getJobConfiguration(completeJobInfo.getJobName()))
            .filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public List<JobBriefInfo> getJobBriefInfo(String originalJobName) {
        if (Objects.isNull(originalJobName)) {
            return null;
        }
        if (log.isDebugEnabled()) {
            log.debug("获取简明定时任务集合信息:[" + originalJobName + "]");
        }
        final List<JobInfo> jobInfos = traceJobOperationService.byOriginalJobName(originalJobName, null);
        if (CollectionUtils.isEmpty(jobInfos)) {
            return Collections.emptyList();
        }
        return CollectionsUtils.isEmpty(jobInfos) ? Collections.emptyList() :
            jobInfos.stream().map(jobInfo -> jobStatisticsAPI.getJobBriefInfo(jobInfo.getJobName()))
                .filter(Objects::nonNull).collect(Collectors.toList());
    }

    private JobInfo buildReadyStatusJobInfo(String originalJobName, String jobParameter, String invokeDateTime) {
        final JobInfo jobInfo =
            JobInfo.buildSimpleJobInfo(originalJobName, jobParameter, invokeDateTime, JobStatusEnum.READY);
        jobInfo.setInvokeServiceClass(this.getClass().getName());
        jobInfo.setJobName(this.generateJobName(originalJobName));
        return jobInfo;
    }

    private void buildOverStatusJobInfo(JobInfo jobInfo) {
        jobInfo.setStatus(JobStatusEnum.OVER.toString());
    }

    private String generateJobName(String originalJobName) {
        return originalJobName + ":" + System.currentTimeMillis();
    }

}
