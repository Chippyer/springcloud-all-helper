package com.chippy.redis.redisson.task.support;

import com.chippy.common.utils.IpUtil;

import java.util.List;

/**
 * 任务相关工具类
 *
 * @author: chippy
 * @datetime 2020-12-17 21:31
 */
class TaskUtils {

    private static final List<String> currentServerIps;
    private static String assignServer = null;
    private static boolean isContain;

    static {
        currentServerIps = IpUtil.getCurrentServerIp();
    }

    static void setAssignServer(String assignServer) {
        isContain = currentServerIps.contains(assignServer);
        TaskUtils.assignServer = assignServer;
    }

    static String getAssignServer() {
        return assignServer;
    }

    static List<String> getCurrentServerIps() {
        return currentServerIps;
    }

    static boolean isContain() {
        return isContain;
    }

}
