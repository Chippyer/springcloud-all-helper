package com.chippy.elasticjob.support.domain;

import lombok.Data;
import org.redisson.api.annotation.REntity;
import org.redisson.api.annotation.RId;
import org.redisson.api.annotation.RIndex;

import java.io.Serializable;
import java.util.Date;

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
    private Date invokeDateTime;

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
    private int shardingTotalCount;

    /**
     * 分片参数
     */
    private String shardingParameter;

    /**
     * 当前执行分片项
     */
    private int shardingItem;

}
