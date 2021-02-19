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

    public static void register(String classFullPath, String primaryKeyField) {
        if (Objects.isNull(classFullPath) || Objects.isNull(primaryKeyField)) {
            return;
        }
        cache.put(classFullPath, primaryKeyField);
    }

    public static String get(String invokeMethodId) {
        if (Objects.isNull(invokeMethodId)) {
            return null;
        }
        return cache.get(invokeMethodId);
    }

}
