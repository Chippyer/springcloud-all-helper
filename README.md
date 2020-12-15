# springcloud-all-helper
基于SpringCloud的相关组件封装, 如Feign, Redisson, RabbitMQ, ElasticJob...
希望大家多多关注和使用, 一起讨论学习。

目前已实现Feign相关基础功能，详细示例查看[springcloud-helper-example](https://github.com/Chippyer/springcloud-helper-example "springcloud-helper-example")
___
<h2>简单的对比</h2>
<p>正常调用我们要写如下代码</p>
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
更加便捷的进行调用以及对某些业务做通用功能封装的支持(详见FeignClientProcessor接口)，目前是为了让大家在项目中使用更加统一，规范，且便捷。



逐步更新中...
