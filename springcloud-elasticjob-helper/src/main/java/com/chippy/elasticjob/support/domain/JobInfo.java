package com.chippy.elasticjob.support.domain;

import com.chippy.core.common.utils.CronUtils;
import com.chippy.elasticjob.support.enums.JobStatusEnum;
import lombok.Data;
import org.redisson.api.annotation.REntity;
import org.redisson.api.annotation.RId;
import org.redisson.api.annotation.RIndex;

import java.io.Serializable;

/**
 * ElasticJob任务信息
 *
 * @author: chippy
 * @datetime 2020-12-18 17:39
 */
@REntity
@Data
public class JobInfo implements Serializable {

    /**
     * 任务名称
     */
    @RId
    private String jobName;

    /**
     * 原任务名称
     */
    @RIndex
    private String originalJobName;

    /**
     * 指定的任务时间cron格式
     */
    private String cron;

    /**
     * 指定的任务时间date格式
     */
    private String invokeDateTime;

    /**
     * 执行容器服务名称
     */
    private String invokeServiceClass;

    /**
     * 任务唯一标识
     */
    private String taskId;

    /**
     * 定时任务参数
     */
    private String jobParameter;

    /**
     * 任务状态{@link com.chippy.elasticjob.support.enums.JobStatusEnum}
     */
    @RIndex
    private String status;

    /**
     * 错误原因
     */
    private String errorReason;

    // =========================== 以下字段针对动态任务可以忽略 ===========================

    /**
     * 分片数量
     */
    private int shardingTotalCount = 1;

    /**
     * 分片参数
     */
    private String shardingParameter;

    /**
     * 当前执行分片项
     */
    private int shardingItem = 0;

    public JobInfo() {
    }

    /**
     * 构建一个简单的任务信息
     * <p>
     * 以下字段信息默认值
     * cron = 计算获得
     * jobName = 计算获得
     * taskId = null,
     * shardingTotalCount = 1,
     * shardingItem = 0,
     * shardingParameter = null,
     * jobParameter = null,
     * status = READY,
     * errorReason = null,
     * deleted = false
     *
     * @param originalJobName 原始任务名称
     * @param invokeDateTime  执行时间
     * @return com.oak.common.compments.elasticjob.support.domain.JobInfo
     * @author chippy
     */
    public static JobInfo buildSimpleJobInfo(String originalJobName, String invokeDateTime) {
        return buildSimpleJobInfo(originalJobName, invokeDateTime, JobStatusEnum.READY);
    }

    /**
     * 构建一个简单的任务信息
     * <p>
     * 以下字段信息默认值
     * cron = 计算获得
     * jobName = 计算获得
     * taskId = null,
     * shardingTotalCount = 1,
     * shardingItem = 0,
     * shardingParameter = null,
     * status = READY,
     * errorReason = null,
     * deleted = false
     *
     * @param originalJobName 原始任务名称
     * @param jobParameter    任务参数
     * @param invokeDateTime  执行时间
     * @return com.oak.common.compments.elasticjob.support.domain.JobInfo
     * @author chippy
     */
    public static JobInfo buildSimpleJobInfo(String originalJobName, String jobParameter, String invokeDateTime) {
        return buildSimpleJobInfo(originalJobName, jobParameter, invokeDateTime, JobStatusEnum.READY);
    }

    /**
     * 构建一个简单的任务信息
     * <p>
     * 以下字段信息默认值
     * cron = 计算获得
     * jobName = 计算获得
     * taskId = null,
     * shardingTotalCount = 1,
     * shardingItem = 0,
     * shardingParameter = null,
     * jobParameter = null,
     * errorReason = null,
     * deleted = false
     *
     * @param originalJobName 任务名称
     * @param invokeDateTime  执行时间
     * @param jobStatusEnum   任务状态
     * @return com.oak.common.compments.elasticjob.support.domain.JobInfo
     * @author chippy
     */
    public static JobInfo buildSimpleJobInfo(String originalJobName, String invokeDateTime,
        JobStatusEnum jobStatusEnum) {
        return buildSimpleJobInfo(originalJobName, null, invokeDateTime, jobStatusEnum);
    }

    /**
     * 构建一个简单的任务信息
     * <p>
     * 以下字段信息默认值
     * cron = 计算获得
     * jobName = 计算获得
     * taskId = null,
     * shardingTotalCount = 1,
     * shardingItem = 0,
     * shardingParameter = null,
     * errorReason = null,
     * deleted = false
     *
     * @param originalJobName 原始任务名称
     * @param jobParameter    参数内容
     * @param invokeDateTime  执行时间
     * @param jobStatusEnum   任务状态
     * @return com.oak.common.compments.elasticjob.support.domain.JobInfo
     * @author chippy
     */
    public static JobInfo buildSimpleJobInfo(String originalJobName, String jobParameter, String invokeDateTime,
        JobStatusEnum jobStatusEnum) {
        return new JobInfo(originalJobName, jobParameter, invokeDateTime, jobStatusEnum);
    }

    private JobInfo(String originalJobName, String jobParameter, String invokeDateTime, JobStatusEnum jobStatusEnum) {
        this(originalJobName, originalJobName + ":" + System.currentTimeMillis(), CronUtils.getCron(invokeDateTime),
            invokeDateTime, null, 1, 0, null, jobParameter, jobStatusEnum.toString(), null, null);
    }

    private JobInfo(String originalJobName, String jobName, String cron, String invokeDateTime, String taskId,
        Integer shardingTotalCount, Integer shardingItem, String shardingParameter, String jobParameter, String status,
        String errorReason, String invokeServiceClass) {
        this.originalJobName = originalJobName;
        this.jobName = jobName;
        this.cron = cron;
        this.invokeDateTime = invokeDateTime;
        this.jobParameter = jobParameter;
        this.status = status;
        this.errorReason = errorReason;
        this.invokeServiceClass = invokeServiceClass;
        this.taskId = taskId;
        this.shardingTotalCount = shardingTotalCount;
        this.shardingItem = shardingItem;
        this.shardingParameter = shardingParameter;
    }

}
