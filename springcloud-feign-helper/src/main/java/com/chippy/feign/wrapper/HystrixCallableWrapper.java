package com.chippy.feign.wrapper;

import java.util.concurrent.Callable;

/**
 * 调用外部服务的执行线程包装接口定义
 * <p>
 * 使执行线程拥有附加的自定义能力
 *
 * @author: chippy
 * @datetime 2020-11-05 14:28
 */
public interface HystrixCallableWrapper {

    /**
     * 包装Callable实例, 使其在原有的功能基础上支持附加功能
     *
     * @param callable 待包装实例
     * @param <T>      返回类型
     * @return 包装后的实例
     */
    <T> Callable<T> wrap(Callable<T> callable);

}
