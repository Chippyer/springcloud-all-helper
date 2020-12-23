package com.chippy.elasticjob.support.domain.enums;

/**
 * 任务状态枚举定义
 *
 * @author: chippy
 * @datetime 2020-12-09 12:45
 */
public enum JobStatusEnum {

    /**
     * 未执行
     */
    READY,
    /**
     * 进行中
     */
    ING,
    /**
     * 执行结束
     */
    OVER,
    /**
     * 关闭
     */
    REMOVE

}
