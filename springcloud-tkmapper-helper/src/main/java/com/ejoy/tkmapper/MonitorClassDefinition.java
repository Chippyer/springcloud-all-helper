package com.ejoy.tkmapper;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 监控对象信息定义
 *
 * @author: chippy
 * @datetime 2021/2/19 0:51
 */
@Data
public class MonitorClassDefinition implements Serializable {

    /**
     * 缓存元素信息集合
     */
    private static Map<String, String> cache = new HashMap<>(256);

    public static void register(String invokeMethodId, String monitorClassFullPath) {
        if (Objects.isNull(invokeMethodId) || Objects.isNull(monitorClassFullPath)) {
            return;
        }
        cache.put(invokeMethodId, monitorClassFullPath);
    }

    public static String get(String invokeMethodId) {
        if (Objects.isNull(invokeMethodId)) {
            return null;
        }
        return cache.get(invokeMethodId);
    }

}
