package com.ejoy.tkmapper.support.api;

import com.ejoy.tkmapper.support.domain.MonitorOperationLogInfo;

import java.util.List;

/**
 * 监控服务类
 *
 * @author: chippy
 * @datetime: 2021-02-19 17:09
 */
public interface IMonitorService {

    /**
     * 处理监控字段的信息记录
     *
     * @author chippy
     */
    void process(Object object);

    /**
     * 处理监控字段的信息记录
     *
     * @author chippy
     */
    void process(Object object, String customerDesc);

    /**
     * 查询最新一条的记录信息
     *
     * @param clazz 监控类信息
     * @param id    监控类型信息主键唯一标识
     * @author chippy
     */
    MonitorOperationLogInfo getOne(Class clazz, String id);

    /**
     * 查询最新十条的记录信息
     *
     * @param clazz 监控类信息
     * @param id    监控类型信息主键唯一标识
     * @author chippy
     */
    List<MonitorOperationLogInfo> get(Class clazz, String id);

    /**
     * 查询最新{endIndex}条的记录信息
     *
     * @param clazz    监控类信息
     * @param id       监控类型信息主键唯一标识
     * @param endIndex 结束下标值
     * @author chippy
     */
    List<MonitorOperationLogInfo> get(Class clazz, String id, int endIndex);

    /**
     * 从{startIndex}开始查询最新{endIndex - startIndex}条的记录信息
     *
     * @param clazz    监控类信息
     * @param id       监控类型信息主键唯一标识
     * @param endIndex 结束下标值
     * @author chippy
     */
    List<MonitorOperationLogInfo> get(Class clazz, String id, int startIndex, int endIndex);

}
