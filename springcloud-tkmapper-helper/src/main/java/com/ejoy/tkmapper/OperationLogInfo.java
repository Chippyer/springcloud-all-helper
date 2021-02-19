package com.ejoy.tkmapper;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.redisson.api.annotation.REntity;
import org.redisson.api.annotation.RId;
import org.redisson.api.annotation.RIndex;
import tk.mybatis.mapper.common.Mapper;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 操作日志信息
 *
 * @author: chippy
 * @datetime 2021-02-19 14:30
 */
@Data
@NoArgsConstructor
@REntity
public class OperationLogInfo implements Serializable {

    @RId
    @NotNull
    private String id;

    /**
     * 实体类型全路径
     */
    @RIndex
    @NotNull
    private String monitorFullPath;

    /**
     * 监控对象Class信息
     */
    @NotNull
    private Class monitorClass;

    /**
     * 实体对应的Mybatis_Mapper
     */
    @NotNull
    private Mapper mapper;

    private String monitorPrimaryKeyField;

    @RIndex
    private String monitorPrimaryKeyFieldValue;

    private String monitorField;

    @RIndex
    private String monitorFieldValue;

    private String desc;

    private String updateDateTime;

    public OperationLogInfo(@NotNull Class monitorClass, @NotNull Mapper mapper) {
        this.monitorClass = monitorClass;
        this.monitorFullPath = monitorClass.getName();
        this.mapper = mapper;
    }
}
