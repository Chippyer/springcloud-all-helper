package com.ejoy.tkmapper;

import com.ejoy.core.common.utils.UUIDUtil;
import lombok.Data;
import org.redisson.api.annotation.REntity;

import java.io.Serializable;

/**
 * 监控操作日志信息
 *
 * @author: chippy
 * @datetime 2021-02-20 17:04
 */
@REntity
@Data
public class MonitorOperationLogInfo implements Serializable {

    private String id;

    private String monitorField;

    private String monitorFieldValue;

    private String primaryKeyField;

    private String primaryKeyFieldValue;

    private String desc;

    private String createDateTime;

    public MonitorOperationLogInfo() {
        this.id = UUIDUtil.generateUuid();
    }
}
