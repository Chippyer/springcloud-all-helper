package com.ejoy.tkmapper.support.domain;

import com.ejoy.core.common.utils.UUIDUtil;
import lombok.Data;
import org.redisson.api.annotation.REntity;

import java.io.Serializable;
import java.util.Date;

/**
 * 监控操作日志信息
 *
 * @author: chippy
 * @datetime 2021-02-20 17:04
 */
@REntity
@Data
public class MonitorOperationLogInfo implements Serializable {

    /**
     * 监控数据唯一标识
     */
    private String id;

    /**
     * 监控字段名称
     */
    private String monitorField;

    /**
     * 监控字段值
     */
    private String monitorFieldValue;

    /**
     * 监控字段类主键名称
     */
    private String primaryKeyField;

    /**
     * 监控字段类主键值
     */
    private String primaryKeyFieldValue;

    /**
     * 操作信息描述
     */
    private String desc;

    /**
     * 扩展参数
     */
    private String extensionParam;

    /**
     * 记录创建时间
     */
    private Date createDateTime;

    public MonitorOperationLogInfo() {
        this.id = UUIDUtil.generateUuid();
    }
}
