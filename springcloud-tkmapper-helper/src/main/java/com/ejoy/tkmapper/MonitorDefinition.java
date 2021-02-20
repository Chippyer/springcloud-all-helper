package com.ejoy.tkmapper;

import lombok.Data;
import lombok.NoArgsConstructor;
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
public class MonitorDefinition implements Serializable {

    /**
     * 实体类型全路径
     */
    @NotNull
    private String monitorClassFullPath;

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

    /**
     * 监控类主键字段名称
     */
    private String primaryKeyField;

    /**
     * 监控字段名称
     */
    private String monitorField;

    public MonitorDefinition(@NotNull Class monitorClass, @NotNull Mapper mapper) {
        this.monitorClass = monitorClass;
        this.monitorClassFullPath = monitorClass.getName();
        this.mapper = mapper;
    }
}
