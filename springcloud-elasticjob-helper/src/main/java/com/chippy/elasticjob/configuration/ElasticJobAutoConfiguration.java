package com.chippy.elasticjob.configuration;

import com.chippy.common.constants.GlobalConstantEnum;
import com.chippy.common.utils.AnnotationUtils;
import com.chippy.common.utils.ObjectsUtil;
import com.chippy.elasticjob.annotation.EnableElasticJob;
import com.chippy.elasticjob.exception.ZooKeeperCreationException;
import com.chippy.elasticjob.listener.UpdateJobInfoElasticJobListener;
import com.chippy.elasticjob.support.api.db.IJobInfoService;
import com.chippy.elasticjob.support.api.db.MysqlJobInfoService;
import com.chippy.elasticjob.support.api.db.RedisJobInfoService;
import com.chippy.elasticjob.support.domain.ElasticJobMetaInfo;
import com.chippy.elasticjob.support.domain.mapper.JobInfoMapper;
import com.ulisesbocchio.jasyptspringboot.annotation.ConditionalOnMissingBean;
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
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.spring.annotation.MapperScan;

import javax.sql.DataSource;

/**
 * ElasticJob自动配置类
 *
 * @author chippy
 */
@Slf4j
@Configuration
@MapperScan(basePackages = {"com.chippy.elasticjob.support.domain.mapper"})
@ComponentScan({"com.chippy.elasticjob.support"})
public class ElasticJobAutoConfiguration implements ApplicationContextAware, InitializingBean {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Bean
    @ConditionalOnMissingBean
    public ZookeeperConfiguration zkConfiguration() {
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
        return new ZookeeperRegistryCenter(zkConfiguration());
    }

    @Bean
    @SuppressWarnings("unchecked")
    public IJobInfoService jobInfoService() {
        final EnableElasticJob enableElasticJob =
            AnnotationUtils.getFirstAnnotation(applicationContext, EnableElasticJob.class);
        final DBTypeEnum dbType = enableElasticJob.traceDbType();
        switch (dbType) {
            case MYSQL:
                return new MysqlJobInfoService(applicationContext.getBean(JobInfoMapper.class));
            case REDIS:
                return new RedisJobInfoService(applicationContext.getBean(RedisTemplate.class));
            default:
                throw new BeanCreationException("注解[EnableElasticJob]中的DB类型不支持");
        }
    }

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
    public ElasticJobListener updateJobInfoElasticJobListener() {
        return new UpdateJobInfoElasticJobListener(applicationContext.getBean(IJobInfoService.class));
    }

    @Bean
    @ConditionalOnMissingBean
    public TracingConfiguration<DataSource> tracingConfiguration(DataSource dataSource) {
        RDBTracingListenerConfiguration rdbTracingListenerConfiguration = new RDBTracingListenerConfiguration();
        return new TracingConfiguration<>(rdbTracingListenerConfiguration.getType(), dataSource);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Environment environment = applicationContext.getEnvironment();
        String failToRetryServerIp =
            environment.getProperty(GlobalConstantEnum.ELASTIC_JOB_FAIL_RETRY_SERVER_IP.getConstantValue());
        final ElasticJobMetaInfo elasticJobMetaInfo = ElasticJobMetaInfo.getInstance();
        elasticJobMetaInfo.setFailToRetryServerIp(failToRetryServerIp);
    }

}