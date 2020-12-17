package com.chippy.feign.support.definition;

import cn.hutool.core.lang.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * FeignClientHelper相关的元素缓存集合信息, 以字典表的方式进行k-v存储
 * <p>
 * 项目启动后, 直接将数据缓存到JVM中
 * 为避免数据错乱, 该类只公开提供查询操作
 *
 * @author: chippy
 */
public class FeignClientDefinition {

    /**
     * 默认缓存大小
     */
    private static final int DEFAULT_CAPACITY = 128;

    /**
     * 缓存存放集合
     */
    private static Map<String, Element> cache = new HashMap<>(DEFAULT_CAPACITY);

    /**
     * 当前持有权限服务名称
     */
    private static String server;

    /**
     * 获取一个单例的缓存实例
     *
     * @return com.oak.com.chippy.common.feign.FeignCacheElement
     * @author chippy
     */
    public static FeignClientDefinition getInstance() {
        return CacheInstance.INSTANCE.getFeignClientDefinition();
    }

    /**
     * 获取服务信息
     *
     * @author chippy
     */
    public static String server() {
        return FeignClientDefinition.server;
    }

    /**
     * 获取缓存实例
     *
     * @author chippy
     */
    public static Map<String, Element> elements() {
        return cache;
    }

    /**
     * 通过指定{business}获取缓存元素
     *
     * @param business 业务方法名
     * @author chippy
     */
    public static Element get(String business) {
        Assert.notNull(business, "业务标识值不能为空");
        return cache.get(business);
    }

    /**
     * 将元素信息集合添加到总的缓存元素集合中
     *
     * @param elements 需要缓存的元素信息集合
     * @author chippy
     */
    void setCache(Map<String, Element> elements) {
        elements.forEach((k, v) -> FeignClientDefinition.cache.put(k, v));
    }

    /**
     * 设置服务信息
     *
     * @param server 服务信息
     * @author chippy
     */
    void setServer(String server) {
        FeignClientDefinition.server = server;
    }

    public static class Element {
        final String method;
        final String fullPath;
        final Class<?> feignClientClass;

        public Element(String method, String fullPath, Class<?> feignClientClass) {
            this.method = method;
            this.fullPath = fullPath;
            this.feignClientClass = feignClientClass;
        }

        public String getMethod() {
            return this.method;
        }

        public String getFullPath() {
            return fullPath;
        }

        public Class<?> getFeignClientClass() {
            return feignClientClass;
        }
    }

    private enum CacheInstance {
        INSTANCE(new FeignClientDefinition());

        private FeignClientDefinition feignClientDefinition;

        CacheInstance(FeignClientDefinition feignClientDefinition) {
            this.feignClientDefinition = feignClientDefinition;
        }

        private FeignClientDefinition getFeignClientDefinition() {
            return feignClientDefinition;
        }
    }

}
