package com.chippy.common.constants;

/**
 * 全局常量枚举
 *
 * @author chippy
 */
public enum GlobalConstantEnum {

    REDIS_CONNECTION_PREFIX("redis://", "Redis地址配置前缀");

    private final String constantValue;
    private final String constantDesc;

    private GlobalConstantEnum(String constantValue, String constantDesc) {
        this.constantValue = constantValue;
        this.constantDesc = constantDesc;
    }

    public String getConstantValue() {
        return constantValue;
    }

    public String getConstantDesc() {
        return constantDesc;
    }
}
