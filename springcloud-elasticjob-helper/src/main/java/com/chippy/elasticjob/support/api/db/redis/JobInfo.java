package com.chippy.elasticjob.support.api.db.redis;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * ElasticJob任务信息
 *
 * @author: chippy
 * @datetime 2020-12-18 17:39
 */
@Data
public class JobInfo implements Serializable {

    /**
     * 任务名称
     */
    private String jobName;

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
     * 任务状态{@link com.chippy.elasticjob.support.domain.enums.JobStatusEnum}
     */
    private String status;

    /**
     * 错误原因
     */
    private String errorReason;

}
