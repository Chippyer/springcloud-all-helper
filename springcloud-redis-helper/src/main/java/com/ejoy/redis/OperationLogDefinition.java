package com.ejoy.redis;

import lombok.Data;
import org.redisson.api.annotation.REntity;
import org.redisson.api.annotation.RIndex;

import java.io.Serializable;

/**
 * 操作日志信息
 *
 * @author: chippy
 * @datetime 2021-02-18 18:03
 */
@REntity
@Data
public class OperationLogDefinition implements Serializable {

    /**
     * 业务唯一标识(例如商品主键ID)
     */
    @RIndex
    private String businessId;

    /**
     * 日志分类
     */
    private String type;

    /**
     * 状态列名称
     */
    private String field;

    /**
     * 状态列值
     */
    private String lastFieldValue;

    /**
     * 记录创建时间
     */
    private String createTime;

}
