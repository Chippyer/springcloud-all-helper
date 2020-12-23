package com.chippy.elasticjob.support.api.db.redis;

import lombok.Data;
import org.redisson.api.annotation.REntity;
import org.redisson.api.annotation.RId;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * ElasticJob原始任务信息
 *
 * @author: chippy
 * @datetime 2020-12-18 17:19
 */
@REntity
@Data
public class OriginalJobRelation implements Serializable {

    /**
     * 原始任务名称
     */
    @RId
    private String originalJobName;

    /**
     * 原始任务所对应的任务集合信息
     */
    List<String> jobNameList;

}
