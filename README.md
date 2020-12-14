# springcloud-all-helper
基于SpringCloud的相关组件封装, 如Feign, Redisson, RabbitMQ, ElasticJob...
希望大家多多关注和使用, 一起讨论学习。

目前已实现Feign相关基础功能
如正常调用我们要写如下代码
```java
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
```

使用springcloud-feign-helper后
```java
@GetMapping("/2")
public MyResultImpl<List<String>> test02(String param) {
    final List<String> data = ListFeignClient.invoke(String.class, "test02", param);
    return MyResultImpl.success(data);
}
```



逐步更新中...
