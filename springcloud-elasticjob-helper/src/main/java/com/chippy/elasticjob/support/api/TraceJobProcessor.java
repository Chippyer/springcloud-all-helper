package com.chippy.elasticjob.support.api;

/**
 * 定时任务业务处理器
 * 这是业务类一定要实现的接口，否则任务将找不到对应的任务处理器进行处理
 * 一般来说我们底层任务操作动作还是会借助{@link TraceJobHandler}的实现类进行操作
 *
 * @author: chippy
 * @datetime: 2020-11-12 20:02
 */
public interface TraceJobProcessor<T> {

    /**
     * 处理实现定时应用的业务内容
     *
     * @param cronParam 处理定时任务参数
     */
    void processCronJob(T cronParam);

    /**
     * 创建一个任务信息并携带指定的参数信息
     *
     * @param cronParam 创建定时任务参数
     * @author chippy
     */
    void createCronJob(T cronParam);

    /**
     * 更新一个任务信息并携带指定的参数信息
     *
     * @param cronParam 创建定时任务参数
     * @author chippy
     */
    void updateCronJob(T cronParam);

    /**
     * 关闭并移除一个任务信息并携带指定的参数信息
     *
     * @param cronParam 创建定时任务参数
     * @author chippy
     */
    void removeCronJob(T cronParam);

}
