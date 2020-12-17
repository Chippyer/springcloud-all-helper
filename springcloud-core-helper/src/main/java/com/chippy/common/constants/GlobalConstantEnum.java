package com.chippy.common.constants;

/**
 * 全局常量枚举
 *
 * @author chippy
 */
public enum GlobalConstantEnum {

    SPRING_SCHEDULED_ASSIGN_SERVER("spring.scheduled.assign-server", "SpringScheduled指定的执行服务");

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
