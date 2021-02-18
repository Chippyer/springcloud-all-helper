package com.ejoy.tkmapper;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.redisson.api.annotation.REntity;
import org.redisson.api.annotation.RIndex;
import tk.mybatis.mapper.common.Mapper;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 监控实例信息定义
 *
 * @author: chippy
 * @datetime 2021-02-18 18:03
 */
@REntity
@Data
@NoArgsConstructor
public class MonitorFieldDefinition implements Serializable {

    /**
     * 实体类型全路径
     */
    @RIndex
    @NotNull
    private String monitorFullPath;

    /**
     * 监控对象Class信息
     */
    private Class monitorClass;

    /**
     * 实体对应的Mybatis_Mapper
     */
    @NotNull
    private Mapper mapper;

    /**
     * 监控列名称
     */
    @NotNull
    private String field;

    /**
     * 监控列值(初始为null)
     */
    private String fieldValue;

    /**
     * 监控对象主键列名称
     */
    @NotNull
    private String fieldPrimaryKey;

    /**
     * 监控对象主键列值(初始为null)
     */
    private String fieldPrimaryKeyValue;

    public MonitorFieldDefinition(Class monitorClass, Mapper mapper) {
        this.monitorClass = monitorClass;
        this.monitorFullPath = monitorClass.getName();
        this.mapper = mapper;
    }
}
