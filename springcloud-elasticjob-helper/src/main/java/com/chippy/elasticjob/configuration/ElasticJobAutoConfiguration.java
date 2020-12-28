package com.chippy.elasticjob.configuration;

import com.chippy.core.common.constants.GlobalConstantEnum;
import com.chippy.core.common.utils.AnnotationUtils;
import com.chippy.core.common.utils.ObjectsUtil;
import com.chippy.elasticjob.annotation.EnableElasticJob;
import com.chippy.elasticjob.exception.ZooKeeperCreationException;
import com.chippy.elasticjob.listener.TraceJobListener;
import com.chippy.elasticjob.support.api.RedisTraceJobOperationService;
import com.chippy.elasticjob.support.api.TraceJobOperationService;
import com.chippy.elasticjob.support.domain.ElasticJobMetaInfo;
import com.chippy.elasticjob.support.runner.FailToRetryRunner;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.elasticjob.api.listener.ElasticJobListener;
import org.apache.shardingsphere.elasticjob.lite.lifecycle.api.JobConfigurationAPI;
import org.apache.shardingsphere.elasticjob.lite.lifecycle.api.JobOperateAPI;
import org.apache.shardingsphere.elasticjob.lite.lifecycle.api.JobStatisticsAPI;
import org.apache.shardingsphere.elasticjob.lite.lifecycle.internal.operate.JobOperateAPIImpl;
import org.apache.shardingsphere.elasticjob.lite.lifecycle.internal.settings.JobConfigurationAPIImpl;
import org.apache.shardingsphere.elasticjob.lite.lifecycle.internal.statistics.JobStatisticsAPIImpl;
import org.apache.shardingsphere.elasticjob.reg.zookeeper.ZookeeperConfiguration;
import org.apache.shardingsphere.elasticjob.reg.zookeeper.ZookeeperRegistryCenter;
import org.apache.shardingsphere.elasticjob.tracing.api.TracingConfiguration;
import org.apache.shardingsphere.elasticjob.tracing.rdb.listener.RDBTracingListenerConfiguration;
import org.redisson.api.RLiveObjectService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.util.Objects;

/**
 * ElasticJob自动配置类
 *
 * @author chippy
 */
@Slf4j
@Configuration
public class ElasticJobAutoConfiguration implements ApplicationContextAware, InitializingBean {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    // ======================== zk ========================

    @Bean
    @ConditionalOnMissingBean
    public ZookeeperConfiguration zookepperConfiguration() {
        Environment environment = applicationContext.getEnvironment();
        String serverListProperty =
            environment.getProperty(GlobalConstantEnum.ELASTIC_JOB_REGCENTER_SERVER_LIST.getConstantValue());
        if (ObjectsUtil.isEmpty(serverListProperty)) {
            throw new ZooKeeperCreationException("创建ZooKeeper配置信息异常 -> [服务列表不能为空]");
        }
        String namespaceProperty =
            environment.getProperty(GlobalConstantEnum.ELASTIC_JOB_REGCENTER_NAMESPACE.getConstantValue());
        if (ObjectsUtil.isEmpty(namespaceProperty)) {
            throw new ZooKeeperCreationException("创建ZooKeeper配置信息异常 -> [名命空间不能为空]");
        }
        ZookeeperConfiguration zookeeperConfiguration =
            new ZookeeperConfiguration(serverListProperty, namespaceProperty);

        String maxRetriesProperty = String
            .valueOf(environment.getProperty(GlobalConstantEnum.ELASTIC_JOB_REGCENTER_MAX_TETRIES.getConstantValue()));
        if (ObjectsUtil.isNotEmpty(maxRetriesProperty)) {
            zookeeperConfiguration.setMaxRetries(Integer.parseInt(maxRetriesProperty));
        }

        String maxSleepTimeMillisecondsProperty = String.valueOf(
            environment.getProperty(GlobalConstantEnum.ELASTIC_JOB_REGCENTER_MAX_SLEEP_TIME_MS.getConstantValue()));
        if (ObjectsUtil.isNotEmpty(maxSleepTimeMillisecondsProperty)) {
            zookeeperConfiguration.setMaxSleepTimeMilliseconds(Integer.parseInt(maxSleepTimeMillisecondsProperty));
        }

        String sessionTimeoutMillisecondsProperty =
            environment.getProperty(GlobalConstantEnum.ELASTIC_JOB_REGCENTER_SESSION_TIMEOUT_MS.getConstantValue());
        if (null != sessionTimeoutMillisecondsProperty) {
            zookeeperConfiguration.setSessionTimeoutMilliseconds(Integer.parseInt(sessionTimeoutMillisecondsProperty));
        }
        return zookeeperConfiguration;
    }

    @Bean(initMethod = "init")
    @ConditionalOnMissingBean
    public ZookeeperRegistryCenter zookeeperRegistryCenter() {
        return new ZookeeperRegistryCenter(zookepperConfiguration());
    }

    // ======================== zk ========================

    // ======================== elastic-job 原生支持API ========================
    @Bean
    @ConditionalOnMissingBean
    public JobConfigurationAPI jobConfigurationAPI() {
        return new JobConfigurationAPIImpl(zookeeperRegistryCenter());
    }

    @Bean
    @ConditionalOnMissingBean
    public JobStatisticsAPI jobStatisticsAPI() {
        return new JobStatisticsAPIImpl(zookeeperRegistryCenter());
    }

    @Bean
    @ConditionalOnMissingBean
    public JobOperateAPI jobOperateAPI() {
        return new JobOperateAPIImpl(zookeeperRegistryCenter());
    }

    @Bean
    @ConditionalOnMissingBean
    public TracingConfiguration<DataSource> tracingConfiguration(DataSource dataSource) {
        RDBTracingListenerConfiguration rdbTracingListenerConfiguration = new RDBTracingListenerConfiguration();
        return new TracingConfiguration<>(rdbTracingListenerConfiguration.getType(), dataSource);
    }

    // ======================== elastic-job 原生支持API ========================

    // ======================== 自定义支持 ========================

    @Bean
    public TraceJobOperationService traceJobOperationService(RLiveObjectService liveObjectService) {
        final String server =
            String.valueOf(applicationContext.getEnvironment().getProperty("spring.application.name"));
        if (Objects.isNull(server)) {
            throw new BeanCreationException("创建Bean[traceJobOperationService]异常-spring.application.name配置信息不能为空");
        }
        return new RedisTraceJobOperationService(liveObjectService, server);
    }

    @Bean
    public FailToRetryRunner failToRetryRunner(TraceJobOperationService traceJobOperationService) {
        return new FailToRetryRunner(traceJobOperationService, this.getTraceMonitor());
    }

    @Bean
    public ElasticJobListener traceJobListener() {
        return new TraceJobListener(applicationContext.getBean(TraceJobOperationService.class), this.getTraceMonitor());
    }

    // ======================== 自定义支持 ========================

    @Override
    public void afterPropertiesSet() throws Exception {
        if (this.getTraceMonitor()) {
            final Environment environment = applicationContext.getEnvironment();
            String failToRetryServerIp =
                environment.getProperty(GlobalConstantEnum.ELASTIC_JOB_FAIL_RETRY_SERVER_IP.getConstantValue());
            final ElasticJobMetaInfo elasticJobMetaInfo = ElasticJobMetaInfo.getInstance();
            elasticJobMetaInfo.setFailToRetryServerIp(failToRetryServerIp);
        }
    }

    private EnableElasticJob getEnableElasticJob() {
        return AnnotationUtils.getFirstAnnotation(applicationContext, EnableElasticJob.class);
    }

    private boolean getTraceMonitor() {
        return getEnableElasticJob().traceMonitor();
    }

}