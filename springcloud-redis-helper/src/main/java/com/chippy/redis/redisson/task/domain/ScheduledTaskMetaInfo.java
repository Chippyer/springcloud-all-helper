package com.chippy.redis.redisson.task.domain;

import lombok.Data;
import org.redisson.api.annotation.REntity;
import org.redisson.api.annotation.RId;

import java.io.Serializable;
import java.util.List;

/**
 * Spring Scheduled定时任务执行信息
 *
 * @author: chippy
 * @datetime 2020-12-17 16:22
 */
@REntity
@Data
public class ScheduledTaskMetaInfo implements Serializable {

    @RId
    private String id;
    private List<String> lastProcessServerIps;
    private boolean status; // 1. 执行中 2. 可执行 3. 执行结束

    public ScheduledTaskMetaInfo(String id, List<String> lastProcessServerIps, boolean status) {
        this.id = id;
        this.lastProcessServerIps = lastProcessServerIps;
        this.status = status;
    }

}
