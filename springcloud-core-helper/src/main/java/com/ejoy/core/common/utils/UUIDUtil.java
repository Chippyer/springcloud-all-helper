package com.ejoy.core.common.utils;

import java.util.UUID;

/**
 * UUID工具类
 *
 * @author chippy
 */
public class UUIDUtil {

    private UUIDUtil() {
    }

    public static String generateUuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
