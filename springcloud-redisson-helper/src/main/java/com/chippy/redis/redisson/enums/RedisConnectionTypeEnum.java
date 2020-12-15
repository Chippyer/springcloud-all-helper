package com.chippy.redis.redisson.enums;

/**
 * Redis连接方式枚举。
 *
 * @author chippy
 */
public enum RedisConnectionTypeEnum {

    STANDALONE("singleServerConfig", "单节点部署方式"),
    SENTINEL("sentinelServersConfig", "哨兵部署方式"),
    CLUSTER("clusterServersConfig", "集群方式"),
    MASTER_SLAVE("masterSlaveServersConfig", "主从部署方式");

    private final String connectionType;
    private final String connectionDesc;

    private RedisConnectionTypeEnum(String connectionType, String connectionDesc) {
        this.connectionType = connectionType;
        this.connectionDesc = connectionDesc;
    }

    public String getConnectionType() {
        return connectionType;
    }

    public String getConnectionDesc() {
        return connectionDesc;
    }
}
