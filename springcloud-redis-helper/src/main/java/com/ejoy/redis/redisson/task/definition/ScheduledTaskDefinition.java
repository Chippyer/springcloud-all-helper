package com.ejoy.redis.redisson.task.definition;

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
public class ScheduledTaskDefinition implements Serializable {

    @RId
    private String id;
    private List<String> lastProcessServerIps;
    private boolean status;

    public ScheduledTaskDefinition(String id, List<String> lastProcessServerIps, boolean status) {
        this.id = id;
        this.lastProcessServerIps = lastProcessServerIps;
        this.status = status;
    }

    public ScheduledTaskDefinition() {
    }

}
