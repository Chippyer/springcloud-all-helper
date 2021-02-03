package com.ejoy.rabbit.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;
import java.util.Map;

/**
 * RabbitHelper配置类
 *
 * @author: chippy
 * @datetime 2021-02-02 15:37
 */
@Data
@ConfigurationProperties(prefix = "spring.rabbit")
public class RabbitHelperProperties implements Serializable {

    /**
     * 默认公共最大缓存问题消息数量
     */
    private Integer commonMaxSize = Integer.MAX_VALUE;

    /**
     * 指定队列最大缓存问题消息数量
     */
    private Map<String, Integer> customerQueueSize;

}
