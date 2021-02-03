package com.ejoy.rabbit.support;

import com.ejoy.rabbit.support.domain.MessageInfo;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;

import java.io.IOException;
import java.util.List;

/**
 * RabbitMq操作服务接口定义
 *
 * @author: chippy
 * @datetime 2021-02-02 10:09
 */
public interface RabbitMQOperationService {

    void save(String id, String message, String business, RabbitMQHandleTemplate.ExceptionStrategy exceptionStrategy,
        Boolean isAutoAck);

    List<MessageInfo> byBusiness(String business);

    void reject(Channel channel, Message message) throws IOException;

    void ack(Channel channel, Message message) throws IOException;

    void nack(Channel channel, Message message) throws IOException;

}
