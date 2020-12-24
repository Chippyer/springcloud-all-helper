package com.chippy.elasticjob.support.domain;

import lombok.Data;

/**
 * EnableElasticJob元数据信息实体
 *
 * @author: chippy
 * @datetime 2020-12-09 15:42
 */
@Data
public class ElasticJobMetaInfo {

    /**
     * 执行失败重试任务监听的服务器IP地址
     */
    private String failToRetryServerIp;

    public static volatile ElasticJobMetaInfo elasticJobMetaInfo;

    private ElasticJobMetaInfo() {
    }

    public static ElasticJobMetaInfo getInstance() {
        if (elasticJobMetaInfo != null) {
            return elasticJobMetaInfo;
        }
        synchronized (ElasticJobMetaInfo.class) {
            if (elasticJobMetaInfo == null) {
                elasticJobMetaInfo = new ElasticJobMetaInfo();
            }
            return elasticJobMetaInfo;
        }
    }

}
