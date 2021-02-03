package com.ejoy.rabbit.support;

import cn.hutool.json.JSONUtil;
import com.ejoy.core.common.utils.CollectionsUtils;
import com.ejoy.core.common.utils.ObjectsUtil;
import com.ejoy.rabbit.support.domain.MessageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * RabbitMQ操作服务
 * 基于Redis作为消息存储
 *
 * @author: chippy
 * @datetime 2021-02-02 14:33
 */
@Slf4j
public class RedisRabbitMQOperationService extends GenericRabbitMQOperationService {

    private final String REDIS_PRE = "rabbit_mq:";

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public void save(String id, String message, String business,
        RabbitMQHandleTemplate.ExceptionStrategy exceptionStrategy, Boolean isAutoAck) {
        if (ObjectsUtil.isEmpty(business)) {
            if (log.isErrorEnabled()) {
                log.error("传入的业务参数信息不能为空");
            }
            return;
        }
        String redisKey = REDIS_PRE + business;
        Long existsSize = redisTemplate.opsForList().size(redisKey);
        if (Objects.isNull(existsSize)) {
            existsSize = 0L;
        }
        if (super.compareToSize(message, business, existsSize)) {
            MessageInfo messageInfo = new MessageInfo();
            messageInfo.setId(id);
            messageInfo.setInvokeServer(super.getServer());
            messageInfo.setBusiness(business);
            messageInfo.setExceptionProcessStrategy(String.valueOf(exceptionStrategy));
            messageInfo.setIsAutoAck(isAutoAck);
            messageInfo.setMessage(message);
            redisTemplate.opsForList().rightPush(redisKey, JSONUtil.toJsonStr(messageInfo));
        }
    }

    @Override
    public List<MessageInfo> byBusiness(String business) {
        if (ObjectsUtil.isEmpty(business)) {
            if (log.isErrorEnabled()) {
                log.error("传入的业务参数信息不能为空");
            }
            return Collections.emptyList();
        }
        String redisKey = REDIS_PRE + business;
        Long size = redisTemplate.opsForList().size(redisKey);
        if (Objects.isNull(size)) {
            size = 0L;
        }
        final List<String> messageList = redisTemplate.opsForList().range(redisKey, 0, size);
        if (CollectionsUtils.isEmpty(messageList)) {
            return Collections.emptyList();
        }
        return messageList.stream().map(messageJson -> JSONUtil.toBean(messageJson, MessageInfo.class))
            .collect(Collectors.toList());
    }

}
