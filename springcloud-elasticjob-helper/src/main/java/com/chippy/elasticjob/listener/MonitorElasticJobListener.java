package com.chippy.elasticjob.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.elasticjob.api.listener.ElasticJobListener;
import org.apache.shardingsphere.elasticjob.api.listener.ShardingContexts;

/**
 * 实现分布式任务监听器。
 * 如果任务有分片，分布式监听器会在任务开始前执行一次，结束时执行一次。
 *
 * @author chippy
 **/
@Slf4j
public class MonitorElasticJobListener implements ElasticJobListener {

    @Override
    public void beforeJobExecuted(ShardingContexts shardingContexts) {
        if (log.isDebugEnabled()) {
            log.debug("任务名：" + shardingContexts.getJobName() + ", 任务参数：" + shardingContexts.getJobParameter() + "，总片数："
                + shardingContexts.getShardingTotalCount() + "，当前分片：" + shardingContexts
                .getCurrentJobEventSamplingCount() + "，分片参数：" + shardingContexts.getShardingItemParameters()
                + " 开始执行！");
        }
    }

    @Override
    public void afterJobExecuted(ShardingContexts shardingContexts) {
        if (log.isDebugEnabled()) {
            log.debug("任务名：" + shardingContexts.getJobName() + ", 任务参数：" + shardingContexts.getJobParameter() + "，总片数："
                + shardingContexts.getShardingTotalCount() + "，当前分片：" + shardingContexts
                .getCurrentJobEventSamplingCount() + "，分片参数：" + shardingContexts.getShardingItemParameters()
                + " 执行结束！");
        }
    }

}