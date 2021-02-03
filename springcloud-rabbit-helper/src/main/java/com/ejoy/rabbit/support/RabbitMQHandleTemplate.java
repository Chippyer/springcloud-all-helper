package com.ejoy.rabbit.support;

import cn.hutool.json.JSONUtil;
import com.ejoy.core.common.utils.ObjectsUtil;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.InitializingBean;

import javax.annotation.Resource;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * RabbitMQ消息处理模板类
 *
 * @author chippy
 */
@Slf4j
public abstract class RabbitMQHandleTemplate<T> implements InitializingBean {

    @Resource
    protected RabbitMQOperationService rabbitMQOperationService;

    /**
     * 业务标识, 建议通过实现{@link InitializingBean#afterPropertiesSet}方法进行手动设置
     * 如果未设置, 将使用{this.getClass().getName()作为业务标识}
     */
    private String business = this.getClass().getName();

    /**
     * 是否自动进行{@link RabbitMQOperationService#ack}操作
     * 默认为true -> 流程成执行正常时自动进行ack确认, 异常则抛弃
     * 如果为false则自行控制
     */
    private boolean isAutoAck = true;

    /**
     * 是否业务处理触发异常时将异常抛出
     * 如果此值为true时, 抛出异常不进行任何额外操作处理
     * 如果此值为false时, 进行错误数据记录并丢弃此消息
     * 默认为false
     */
    private boolean isThrowException = false;

    private ExceptionStrategy exceptionStrategy = ExceptionStrategy.REJECT;

    /**
     * 设置业务标识数据
     */
    protected void setBusiness(String business) {
        this.business = business;
    }

    /**
     * 设置是否主动抛出异常, 默认false
     */
    protected void setThrowException(boolean isThrowException) {
        this.isThrowException = isThrowException;
    }

    /**
     * 是否自动在流程成功时进行{@link RabbitMQOperationService#ack}操作, 默认false
     */
    protected void setAutoAck(boolean isAutoAck) {
        this.isAutoAck = isAutoAck;
    }

    /**
     * 日志打印格式定义获取
     * 遵循格式: 日志打印格式%s
     *
     * @author chippy
     */
    protected String getLogTemplate() {
        return this.getClass().getName() + "-%s";
    }

    /**
     * 消息处理方法, 此方法实现具体核心得消息处理逻辑
     * 如果手动确认消息情况, 推荐调用{@link RabbitMQOperationService#ack}进行手动确认
     *
     * @author chippy
     */
    protected abstract void handleMethod(T t);

    protected void doProcess(String content, Channel channel, Message message) throws IOException {
        log.debug(String.format(this.getLogTemplate(), "消息内容-[" + content + "]"));

        // 内容为空
        if (ObjectsUtil.isEmpty(content)) {
            log.error(String.format(this.getLogTemplate(), "接收信息为空"));
            rabbitMQOperationService.reject(channel, message);
            return;
        }

        // 解析内容
        T record = this.parseJson(content);
        if (ObjectsUtil.isEmpty(record)) {
            log.error(String.format(this.getLogTemplate(), "未能成功获取消息, 丢弃消息"));
            rabbitMQOperationService.reject(channel, message);
            return;
        }

        // 处理业务
        try {
            this.handleMethod(record);
            if (isAutoAck) {
                rabbitMQOperationService.ack(channel, message);
            }
        } catch (Exception e) {
            log.error(String.format(this.getLogTemplate(), "消息处理异常-" + e.getMessage()));
            if (isThrowException) {
                throw e;
            }
            this.doProcessExceptionStrategy(content, channel, message);
        }
    }

    private void doProcessExceptionStrategy(String content, Channel channel, Message message) throws IOException {
        log.debug("rabbitMQOperationService-" + rabbitMQOperationService.getClass());
        rabbitMQOperationService
            .save(message.getMessageProperties().getCorrelationId(), content, business, exceptionStrategy, isAutoAck);
        switch (exceptionStrategy) {
            case NACK:
                if (log.isTraceEnabled()) {
                    log.trace("异常策略[NACK], 是否自动ACK-[" + isAutoAck + "]");
                }
                if (isAutoAck) {
                    rabbitMQOperationService.nack(channel, message);
                }
                break;
            case REJECT:
                if (log.isTraceEnabled()) {
                    log.trace("异常策略[REJECT]");
                }
                rabbitMQOperationService.reject(channel, message);
                break;
            case IGNORE:
                if (log.isTraceEnabled()) {
                    log.trace("异常策略[IGNORE]");
                }
                break;
            default:
                if (log.isErrorEnabled()) {
                    log.error("策略信息传入异常类型");
                }
                break;
        }
    }

    /**
     * 解析JSON格式的消息为JAVA对象
     *
     * @param json 消息内容
     * @return T
     */
    private T parseJson(String json) {
        try {
            return JSONUtil.toBean(json, this.getType(), true);
        } catch (Exception e) {
            log.error(String
                .format(this.getLogTemplate(), "JSON解析异常-" + e.getMessage() + ", 消息原文-" + json + ", 解析异常详情-" + e));
            return null;
        }
    }

    /**
     * Form {@link com.fasterxml.jackson.core.type} construction
     *
     * @return java.lang.reflect.Type
     * @author chippy
     */
    private Type getType() {
        Type superClass = getClass().getGenericSuperclass();
        if (superClass instanceof Class<?>) { // sanity check, should never happen
            throw new IllegalArgumentException(
                "Internal error: TypeReference constructed without actual type information");
        }
        return ((ParameterizedType)superClass).getActualTypeArguments()[0];
    }

    public enum ExceptionStrategy {
        /**
         * 不确认策略，配合isAutoAck字段进行使用
         * 如果配置此策略则原消息将会根据isAutoAck字段判断是否不确认并将消息重返队列
         */
        NACK,
        /**
         * 拒绝策略
         * 如果配置此策略则将消息拒绝并不返回队列
         */
        REJECT,
        /**
         * 忽略策略
         * 不执行任何操作
         */
        IGNORE
    }

}