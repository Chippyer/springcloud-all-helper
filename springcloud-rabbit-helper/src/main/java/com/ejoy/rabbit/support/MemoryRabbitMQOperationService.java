package com.ejoy.rabbit.support;

import com.ejoy.core.common.utils.CollectionsUtils;
import com.ejoy.core.common.utils.ObjectsUtil;
import com.ejoy.rabbit.support.domain.MessageInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RabbitMQ操作服务
 * 基于本地内存作为消息存储
 * 不介意生产使用此类策略
 *
 * @author: chippy
 * @datetime 2021-02-02 16:27
 */
@Slf4j
public class MemoryRabbitMQOperationService extends GenericRabbitMQOperationService {

    private Map<String, List<MessageInfo>> cacheRabbitMessage = new HashMap<>(2048);

    @Override
    public void save(String id, String message, String business,
        RabbitMQHandleTemplate.ExceptionStrategy exceptionStrategy, Boolean isAutoAck) {
        if (ObjectsUtil.isEmpty(business)) {
            if (log.isErrorEnabled()) {
                log.error("传入的业务参数信息不能为空");
            }
            return;
        }
        int existsSize = 0;
        final List<MessageInfo> messageInfos = cacheRabbitMessage.get(business);
        if (CollectionsUtils.isNotEmpty(messageInfos)) {
            existsSize = messageInfos.size();
        }
        if (super.compareToSize(message, business, existsSize)) {
            MessageInfo messageInfo = new MessageInfo();
            messageInfo.setId(id);
            messageInfo.setInvokeServer(super.getServer());
            messageInfo.setBusiness(business);
            messageInfo.setExceptionProcessStrategy(String.valueOf(exceptionStrategy));
            messageInfo.setIsAutoAck(isAutoAck);
            messageInfo.setMessage(message);
            messageInfos.add(messageInfo);
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
        return cacheRabbitMessage.get(business);
    }

}
