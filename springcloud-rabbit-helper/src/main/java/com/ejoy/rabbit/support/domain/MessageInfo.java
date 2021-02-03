package com.ejoy.rabbit.support.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * RabbitMQ异常后存储的记录信息
 *
 * @author: chippy
 * @datetime 2021-02-02 14:23
 */
@Data
public class MessageInfo implements Serializable {

    /**
     * message信息的唯一标识
     */
    private String id;

    /**
     * 执行服务器
     */
    private String invokeServer;

    /**
     * 信息业务标识
     */
    private String business;

    /**
     * 消息内容
     */
    private String message;

    /**
     * 异常信息处理策略
     */
    private String exceptionProcessStrategy;

    /**
     * 是否开启自动ACK
     */
    private Boolean isAutoAck;

}
