package com.chippy.feign.configuration;

import com.chippy.feign.exception.HystrixPluginsException;
import com.chippy.feign.support.strategy.RequestContextHystrixConcurrencyStrategy;
import com.chippy.feign.support.wrapper.HystrixCallableWrapper;
import com.chippy.feign.support.wrapper.RequestAttributeCallableWrapper;
import com.netflix.hystrix.strategy.HystrixPlugins;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import com.netflix.hystrix.strategy.eventnotifier.HystrixEventNotifier;
import com.netflix.hystrix.strategy.executionhook.HystrixCommandExecutionHook;
import com.netflix.hystrix.strategy.metrics.HystrixMetricsPublisher;
import com.netflix.hystrix.strategy.properties.HystrixPropertiesStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Feign透传请求数据配置
 *
 * @author: chippy
 * @datetime 2020-11-05 10:49
 */
@Slf4j
public class FeignRequestConfiguration {

    @Bean
    public HystrixCallableWrapper requestAttributeAwareCallableWrapper() {
        return new RequestAttributeCallableWrapper();
    }

    @Autowired(required = false)
    private List<HystrixCallableWrapper> wrappers;

    @PostConstruct
    public void init() {
        try {
            HystrixConcurrencyStrategy strategy = HystrixPlugins.getInstance().getConcurrencyStrategy();
            if (strategy instanceof RequestContextHystrixConcurrencyStrategy) {
                return;
            }
            HystrixConcurrencyStrategy hystrixConcurrencyStrategy =
                new RequestContextHystrixConcurrencyStrategy(wrappers);

            // 获取原来的相关数据配置
            HystrixCommandExecutionHook commandExecutionHook = HystrixPlugins.getInstance().getCommandExecutionHook();
            HystrixEventNotifier eventNotifier = HystrixPlugins.getInstance().getEventNotifier();
            HystrixMetricsPublisher metricsPublisher = HystrixPlugins.getInstance().getMetricsPublisher();
            HystrixPropertiesStrategy propertiesStrategy = HystrixPlugins.getInstance().getPropertiesStrategy();

            if (log.isDebugEnabled()) {
                log.debug(
                    "当前Hystrix插件配置-" + "concurrencyStrategy [" + hystrixConcurrencyStrategy + "]," + "eventNotifier ["
                        + eventNotifier + "]," + "metricPublisher [" + metricsPublisher + "]," + "propertiesStrategy ["
                        + propertiesStrategy + "]");
            }

            // 重置再重新填充
            // 直接设置会触发异常 Caused by: java.lang.IllegalStateException: Another strategy was already registered.
            HystrixPlugins.reset();
            HystrixPlugins.getInstance().registerConcurrencyStrategy(hystrixConcurrencyStrategy);
            HystrixPlugins.getInstance().registerCommandExecutionHook(commandExecutionHook);
            HystrixPlugins.getInstance().registerEventNotifier(eventNotifier);
            HystrixPlugins.getInstance().registerMetricsPublisher(metricsPublisher);
            HystrixPlugins.getInstance().registerPropertiesStrategy(propertiesStrategy);
        } catch (Exception e) {
            throw new HystrixPluginsException("注册Muses Hystrix并发策略失败");
        }
    }

}
