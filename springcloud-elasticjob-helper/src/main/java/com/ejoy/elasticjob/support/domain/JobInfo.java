package com.ejoy.elasticjob.support.domain;

import com.ejoy.core.common.utils.CronUtils;
import com.ejoy.elasticjob.support.enums.JobStatusEnum;
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
     * 服务名称{spring.application.name}
     */
    @RIndex
    private String server;

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
     * 任务状态{@link JobStatusEnum}
     */
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
     * jobName = null
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
        this(originalJobName, CronUtils.getCron(invokeDateTime), invokeDateTime, jobParameter,
            jobStatusEnum.toString());
    }

    private JobInfo(String originalJobName, String cron, String invokeDateTime, String jobParameter, String status) {
        this.originalJobName = originalJobName;
        this.cron = cron;
        this.invokeDateTime = invokeDateTime;
        this.jobParameter = jobParameter;
        this.status = status;
    }

}
