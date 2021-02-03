package com.ejoy.rabbit.support;

import com.ejoy.core.common.utils.CollectionsUtils;
import com.ejoy.rabbit.configuration.RabbitHelperProperties;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

/**
 * 抽象实现通用的RabbitMq操作服务类
 * 这里需要主义的是
 * reject()方法实现并不会将丢弃的数据放回到原始队列中排队
 * ack()方法实现只会确认提交的当前ack信息
 * nack()方法实现只会不确认提交的当前nack信息并将消息重新放回原始队列进行排队
 *
 * @author: chippy
 * @datetime 2021-02-02 10:28
 */
@Slf4j
public abstract class GenericRabbitMQOperationService implements RabbitMQOperationService {

    @Resource
    private RabbitHelperProperties rabbitHelperProperties;

    @Value("spring.application.name")
    private String server;

    protected String getServer() {
        return server;
    }

    protected void setServer(String server) {
        this.server = server;
    }

    @Override
    public void reject(Channel channel, Message message) throws IOException {
        String content = new String(message.getBody());
        log.debug("拒绝渠道[" + channel.getChannelNumber() + "], 信息[" + content + "]");
        channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
    }

    @Override
    public void ack(Channel channel, Message message) throws IOException {
        String content = new String(message.getBody());
        log.debug("确认渠道[" + channel.getChannelNumber() + "], 信息[" + content + "]");
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    @Override
    public void nack(Channel channel, Message message) throws IOException {
        String content = new String(message.getBody());
        log.debug("不确认渠道[" + channel.getChannelNumber() + "],信息[" + content + "]");
        channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
    }

    protected boolean compareToSize(String message, String business, long existsSize) {
        int compareSize = rabbitHelperProperties.getCommonMaxSize();
        final Map<String, Integer> customerQueueSize = rabbitHelperProperties.getCustomerQueueSize();
        log.debug("customerQueueSize-" + customerQueueSize);
        if (CollectionsUtils.isNotEmpty(customerQueueSize)) {
            Integer businessSize = customerQueueSize.get(business);
            log.debug("businessSize-" + businessSize);
            if (Objects.nonNull(businessSize)) {
                compareSize = businessSize;
            }
        }
        log.debug("compareSize-" + compareSize);
        boolean compareResult;
        if (compareResult = existsSize > compareSize) {
            log.error("消息存储已达到最大值, 请及时处理, 改为日志策略记录错误信息");
            log.error("异常消息信息记录-" + message);
        }
        return !compareResult; // false则说明可以新增
    }

}
