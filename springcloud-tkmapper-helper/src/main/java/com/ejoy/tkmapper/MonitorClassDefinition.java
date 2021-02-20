package com.ejoy.tkmapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 监控类信息定义
 *
 * @author: chippy
 * @datetime 2021-02-20 17:18
 */
public class MonitorClassDefinition {

    /**
     * 缓存元素信息集合
     */
    private static Map<String, MonitorDefinition> cache = new HashMap<>(256);

    public static void register(MonitorDefinition monitorDefinition) {
        if (Objects.isNull(monitorDefinition)) {
            return;
        }
        cache.put(monitorDefinition.getMonitorClassFullPath(), monitorDefinition);
    }

    public static MonitorDefinition get(String classFullPath) {
        if (Objects.isNull(classFullPath)) {
            return null;
        }
        return cache.get(classFullPath);
    }

}
