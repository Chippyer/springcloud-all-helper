package com.ejoy.tkmapper.support.definition;

import tk.mybatis.mapper.common.Mapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 监控类定义信息
 *
 * @author: chippy
 * @datetime 2021-02-20 17:18
 */
@SuppressWarnings("all")
public class MonitorClassDefinition {

    private static final int DEFAULT_SIZE = 128;

    private static Map<String, Element> cache = new HashMap<>(DEFAULT_SIZE);

    /**
     * 当前服务名称
     */
    private static String server;

    /**
     * 获取一个单例的缓存实例
     *
     * @author chippy
     */
    public static MonitorClassDefinition getInstance() {
        return CacheInstance.INSTANCE.getMonitorClassDefinition();
    }

    public static String server() {
        return server;
    }

    public static Element get(String classFullPath) {
        if (Objects.isNull(classFullPath)) {
            return null;
        }
        return cache.get(classFullPath);
    }

    void setServer(String server) {
        MonitorClassDefinition.server = server;
    }

    void register(Element element) {
        if (Objects.isNull(element) || Objects.isNull(element.getMonitorClassFullPath())) {
            return;
        }
        cache.put(element.getMonitorClassFullPath(), element);
    }

    public static class Element {
        /**
         * 监控对象全路径信息
         */
        private String monitorClassFullPath;

        /**
         * 监控对象Class信息
         */
        private Class monitorClass;

        /**
         * 监控实体对应的数据执行器
         */
        private Mapper mapper;

        /**
         * 监控对象主键信息
         */
        private String primaryKeyField;

        /**
         * 监控对象具体字段
         */
        private String monitorField;

        /**
         * 监控对象具体字段是否自动处理记录操作信息
         */
        private Boolean isCustomerProcess;

        /**
         * 监控记录操作信息失效时间
         */
        private long expireTime;

        public Element(Class monitorClass, Mapper mapper) {
            this.monitorClass = monitorClass;
            this.monitorClassFullPath = monitorClass.getName();
            this.mapper = mapper;
        }

        public String getMonitorClassFullPath() {
            return monitorClassFullPath;
        }

        public Class getMonitorClass() {
            return monitorClass;
        }

        public Mapper getMapper() {
            return mapper;
        }

        public String getPrimaryKeyField() {
            return primaryKeyField;
        }

        public String getMonitorField() {
            return monitorField;
        }

        public Boolean getCustomerProcess() {
            return isCustomerProcess;
        }

        public long getExpireTime() {
            return expireTime;
        }

        public void setPrimaryKeyField(String primaryKeyField) {
            this.primaryKeyField = primaryKeyField;
        }

        public void setMonitorField(String monitorField) {
            this.monitorField = monitorField;
        }

        public void setCustomerProcess(Boolean customerProcess) {
            isCustomerProcess = customerProcess;
        }

        public void setExpireTime(long expireTime) {
            this.expireTime = expireTime;
        }
    }

    private enum CacheInstance {
        INSTANCE(new MonitorClassDefinition());

        private MonitorClassDefinition monitorClassDefinition;

        CacheInstance(MonitorClassDefinition monitorClassDefinition) {
            this.monitorClassDefinition = monitorClassDefinition;
        }

        private MonitorClassDefinition getMonitorClassDefinition() {
            return monitorClassDefinition;
        }
    }

}
