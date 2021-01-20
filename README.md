# springcloud-all-helper
基于SpringCloud的相关组件封装, 如Feign, Redis, RabbitMQ, ElasticJob...
希望大家多多关注和使用, 一起讨论学习。	
目前已实现Feign, Redis相关基础功能，详细示例查看 [springcloud-helper-example](https://github.com/Chippyer/springcloud-helper-example "springcloud-helper-example")
___
<h2>springcloud-feign-helper</h2>
对于Feign调用任务的高度封装，让我们的请求变的更加简单。并且在扩展性上提供了支持。
具体详细描述详见 [springcloud-helper-example](https://github.com/Chippyer/springcloud-helper-example)
<h3>部分功能使用描述</h3>
<h5>调度使用</h5>
实现了更加简洁便利的实现请求的调度，让你只关心业务即可。此时你会问，那日志输入的代
码体现在哪呢？你完全不必担心我们可以找到[AbstractLogFeignClientProcessor]指
定要你需要输出日志的路径即可表达式完全兼容spring的拦截器表达式，如"/*"

```java
package com.chippy.example.feign;

import com.chippy.example.common.respnse.ResponseResult;
import GenericFeignClient;
import ListFeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

/**
 * 测试FeignClient
 *
 * @author: chippy
 * @datetime 2020-12-15 10:59
 */
@RestController
@RequestMapping("/test/feignClient")
public class FeignClientController {

    @Resource
    private MarketingFeignClient marketingFeignClient;
    
    @GetMapping("/1")
    public MyResultImpl<String> test01(String param) {
        log.debug("xxx-param[" + param + "]");
        final MyResultImpl<String> response = marketingFeignClient.test01(param);
        log.debug("xxx-result[" + JSONUtil.toJsonStr(response) + "]");
        if (null == response) {
            // error response-to do something...
        }
        if (response.getCode() != 0) {
            // 0 is success code
            // error code-to do something...
        }
        final String data = response.getData();
        // get data-to do something...
        return MyResultImpl.success(data);
    }

    /**
     * 获取单个对象返回测试
     *
     * @return com.chippy.example.common.respnse.ResponseResult<com.chippy.example.feign.OrderInfoResult>
     * @author chippy
     */
    @GetMapping("/2")
    public ResponseResult<OrderInfoResult> getOrderInfo(@RequestParam("orderNo") String orderNo) {
        return ResponseResult.success(GenericFeignClient.invoke(OrderInfoResult.class, "getOrderInfo", orderNo));
    }

}
```

<h5>请求处理器</h5>
在Feign的执行请求前后实现自定义规则，如上述的[AbstractLogFeignClientProcessor]
日子功能的实现就是以来此接口完成的

```java
package com.chippy.example.feign.processor;

import Result;
import ObjectsUtil;
import com.chippy.example.feign.OrderInfoResult;
import com.chippy.example.feign.service.ServiceA;
import FeignClientProcessor;
import FeignClientDefinition;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 自定义FeignClient处理器
 *
 * @author: chippy
 * @datetime 2020-12-15 11:25
 */
@Slf4j
@Component
public class CustomerProcessor implements FeignClientProcessor {

    @Resource
    private ServiceA serviceA;

    @Override
    public List<String> getIncludePathPattern() {
        return new ArrayList<String>() {{
            add("/order/getOrderInfo");
        }};
    }

    @Override
    public Object[] processBefore(FeignClientDefinition.Element element, Object[] param) {
        log.debug("我是自定义处理器-before");
        final String hello = serviceA.hello();
        log.debug("hello -" + hello);
        return param;
    }

    @Override
    public Object processAfter(FeignClientDefinition.Element element, Object response) {
        log.debug("我是自定义处理器-after");
        final Result<OrderInfoResult> result = (Result<OrderInfoResult>)response;
        final OrderInfoResult data = result.getData();
        if (ObjectsUtil.isNotEmpty(data)) {
            data.setName("补充用户名");
            result.setData(data);
        }
        return result;
    }

    @SneakyThrows
    @Override
    public void processException(FeignClientDefinition.Element element, Exception e) {
        throw e;
    }

}
```


<h2>springcloud-redis-helper</h2>
底层依赖redisson框架，故此配置时参考redisson的相关配置文件进行配置。
具体使用详见具体详细描述详见 [springcloud-helper-example](https://github.com/Chippyer/springcloud-helper-example "springcloud-helper-example")
<h3>部分功能使用描述</h3>
<h5>解决集群服务定时任务重复执行问题</h5>
通常我们在一个服务集群部署时，定时任务会在多个实例上同时执行，但是我们
想要预期是某一台或者说是指定某一台进行执行时。你只需实现一个接口即可。

```java
package com.chippy.example.redisson.task;

import DistributedScheduled;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author: chippy
 * @datetime 2020-12-17 18:01
 */
@Component
@Slf4j
public class TestTask implements DistributedScheduled {

    @Scheduled(cron = "0 0/1 * * * ?")
    public void taskA() {
        log.debug("你的业务逻辑");
    }

}
```

没错就是这么简单。如果想要指定某台机器执行，在你yml配置文件中加入如下配置即可。

```yaml
spring:
  scheduled:
    assign-server: 你的服务器ip地址
```

那么你会问，集群服务运行在同一台机器上怎么办?
很好，我们不能规避这个问题，所以底层做了处理，这类情况依然会保证多实例只会执行一次
但是无法保证是哪个实例完成了任务，因为存在着竞争。

---
<h2>springcloud-elasticjob-helper</h2>
底层依赖elasticjob-lite-lifecycle(3.0.0-alpha)框架。注意这里只针对动态任务的封装。
如果非动态任务类型请正常依赖原生elastic-job框架进行实现。
本项目实现在elastic-job框架原有的功能基础上支持了更加灵活的动态任务使用(任务创建、更新、移除、查询、调用都将变的更加简单)，
任务的相关API封装以及服务骤停后的任务补偿机制(保证任务不会丢失或未执行)。
由于项目以来springcloud-redis-helper故此需要配置redisson配置文件。
具体使用详见具体详细描述详见 [springcloud-helper-example](https://github.com/Chippyer/springcloud-helper-example "springcloud-helper-example")
<h3>部分功能使用描述</h3>

1. 通过注解EnableElasticJob打开功能支持
```java
package com.chippy.example;

import EnableElasticJob;
import EnableFeignClientHelper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 用户服务启动类
 *
 * @author: chippy
 * @datetime 2020-12-15 10:57
 */
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
@EnableFeignClientHelper
@EnableScheduling
@EnableElasticJob
public class UserApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }

}
```
2. yml配置文件配置如下
```yaml
elastic-job:
  reg-center:
    server-list: localhost:2181
    namespace: test-job
    max-retries: 3
    max-sleep-time-ms: 3000
    session-timeout-ms: 5000
  fail-retry-server-ip: 192.168.0.112 # 指定执行任务补偿机制机器
```
3. 实现自己任务处理类
```java
package com.chippy.example.elasticjob;

import AbstractTraceJobHandler;
import org.apache.shardingsphere.elasticjob.api.ElasticJob;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author: chippy
 * @datetime 2020-12-24 16:46
 */
@Component
public class MyHandler extends AbstractTraceJobHandler {

    @Resource
    private MyJob myJob;

    @Override
    public ElasticJob getJob() {
        return myJob;
    }

    @Override
    public String getErrorMessageFormat() {
        return "我的自定义任务处理器";
    }
}
```
4. 实现自己的业务任务类
```java
package com.chippy.example.elasticjob;

import AbstractTraceJob;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.springframework.stereotype.Component;

/**
 * @author: chippy
 * @datetime 2020-12-24 16:46
 */
@Component
public class MyJob extends AbstractTraceJob<String> {
    @Override
    protected Class<String> getGenericClass() {
        return String.class;
    }

    @Override
    public void execute(ShardingContext shardingContext) {
        super.doExecute(shardingContext.getJobName(), shardingContext.getJobParameter());
    }
}
```
5. 实现处理自己的业务任务类
```java
package com.chippy.example.elasticjob;

import TraceJobProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author: chippy
 * @datetime 2020-12-24 16:44
 */
@Component
@Slf4j
public class MyProcessor implements TraceJobProcessor<String> {

    @Override
    public void processCronJob(String cronParam) {
        log.debug("处理定时任务: " + cronParam);
    }

    @Override
    public void createCronJob(String cronParam) {
        log.debug("创建定时任务: " + cronParam);
    }

    @Override
    public void updateCronJob(String cronParam) {
        log.debug("修改定时任务: " + cronParam);
    }

    @Override
    public void removeCronJob(String cronParam) {
        log.debug("移除定时任务: " + cronParam);
    }

}
```
走到这里就大功告成啦，你可以通过自己实现的[任务处理类(继承自AbstractTraceJobHandler的类)]进行的创建操作。
关于业务内容的处理将会有[业务任务类(继承自TraceJobProcessor的类)]进行。

___
本项目立志于像Hutool的核心理念发展发展，让Java语言也可以“甜甜的”。

逐步更新中...
