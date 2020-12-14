package com.chippy.feign.support.definition;

import cn.hutool.core.lang.ClassScanner;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.json.JSONUtil;
import com.chippy.feign.support.api.processor.FeignClientProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

/**
 * FeignClientHelper元素信息解析器
 *
 * @author chippy
 */
@Slf4j
public class FeignClientDefinitionResolver implements ApplicationContextAware, InitializingBean {

    private static final String SPRING_APPLICATION_NAME = "spring.application.name";
    private StringBuilder scannerPackages = new StringBuilder();
    private ApplicationContext applicationContext;
    private String server;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        this.server = applicationContext.getEnvironment().getProperty(SPRING_APPLICATION_NAME);
    }

    public void init() {
        this.initScannerPackages();
        Map<String, FeignClientDefinition.Element> elements = new HashMap<>();
        for (String scannerPackage : this.scannerPackages.toString().split(",")) {
            for (Class<?> clazz : ClassScanner.scanPackage(scannerPackage)) {
                if (this.isFeignClient(clazz)) {
                    elements.putAll(this.resolveClazz(clazz));
                }
            }
        }
        // 处理自定义追加类
        elements.putAll(this.classForName(this.getAppendClassName()));
        this.initFeignClientDefinition(elements);
        if (log.isDebugEnabled()) {
            log.debug("feign element list -> [{}]", JSONUtil.toJsonStr(FeignClientDefinition.elements()));
        }
    }

    private void initScannerPackages() {
        Map<String, Object> enableFeignClientAnnotations =
            applicationContext.getBeansWithAnnotation(EnableFeignClients.class);
        enableFeignClientAnnotations.forEach((k, clazz) -> {
            EnableFeignClients enableFeignClients =
                AnnotationUtils.findAnnotation(clazz.getClass(), EnableFeignClients.class);
            if (null != enableFeignClients) {
                String[] enableFeignClientsPackages =
                    this.getScannerPackages(clazz.getClass().getPackage().getName(), enableFeignClients.value());
                for (String enableFeignClientsPackage : enableFeignClientsPackages) {
                    scannerPackages.append(enableFeignClientsPackage).append(",");
                }
            }
        });
    }

    private String[] getScannerPackages(String defaultScannerPackage, String[] feignClientsDefinitionPackages) {
        // 如果注解设置为空, 则将设置注解处做为切入点进行扫描
        return feignClientsDefinitionPackages.length < 1 ? new String[] {defaultScannerPackage} :
            feignClientsDefinitionPackages;
    }

    /**
     * 追加非基础包路径得FeignClient类信息
     *
     * @return java.util.List<java.lang.String>
     * @author chippy
     */
    public List<String> append() {
        return null;
    }

    private List<String> getAppendClassName() {
        List<String> appendFeignClients = this.append();
        if (null == appendFeignClients || appendFeignClients.isEmpty()) {
            if (log.isTraceEnabled()) {
                log.trace("未指定附加类得加载路径");
            }
            return Collections.emptyList();
        }
        return appendFeignClients;
    }

    private Map<String, FeignClientDefinition.Element> classForName(List<String> scannerClassFullPaths) {
        Map<String, FeignClientDefinition.Element> elements = new HashMap<>(scannerClassFullPaths.size());
        for (String classFullPath : scannerClassFullPaths) {
            Class<?> clazz;
            try {
                clazz = Class.forName(classFullPath);
            } catch (ClassNotFoundException e) {
                if (log.isErrorEnabled()) {
                    log.error("路径[" + classFullPath + "]未反射出类");
                }
                continue;
            }

            if (!this.isFeignClient(clazz)) {
                if (log.isTraceEnabled()) {
                    log.trace("当前类不是FeignClient -> " + classFullPath);
                }
                continue; // is not feign client -> continue;
            }
            elements.putAll(this.resolveClazz(clazz));
        }
        return elements;
    }

    private Map<String, FeignClientDefinition.Element> resolveClazz(Class<?> clazz) {
        Map<String, FeignClientDefinition.Element> feignClientDefinitionInfo = new HashMap<>();
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            PostMapping postMapping = method.getAnnotation(PostMapping.class);
            GetMapping getMapping = method.getAnnotation(GetMapping.class);
            RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
            if (null != postMapping || null != getMapping || null != requestMapping) {
                FeignClientDefinition.Element element =
                    this.doInitializedFeignDefinition(clazz, feignClientDefinitionInfo, method);
                this.doRegisterFeignClientProcessor(element);
            }
        }
        return feignClientDefinitionInfo;
    }

    private FeignClientDefinition.Element doInitializedFeignDefinition(Class<?> clazz,
        Map<String, FeignClientDefinition.Element> feignClientDefinitionInfo, Method method) {
        FeignClientDefinition.Element element =
            new FeignClientDefinition.Element(method.getName(), clazz.getName(), clazz);
        feignClientDefinitionInfo.put(method.getName(), element);
        return element;
    }

    private void doRegisterFeignClientProcessor(FeignClientDefinition.Element element) {
        final ServiceLoader<FeignClientProcessor> feignClientProcessors =
            ServiceLoader.load(FeignClientProcessor.class);
        for (FeignClientProcessor feignClientProcessor : feignClientProcessors) {
            FeignClientProcessorRegistry.register(element.getFullPath() + element.getMethod(),
                ReflectUtil.newInstance(feignClientProcessor.getClass()));
        }
    }

    private void initFeignClientDefinition(Map<String, FeignClientDefinition.Element> elements) {
        FeignClientDefinition instance = FeignClientDefinition.getInstance();
        instance.setCache(elements);
        instance.setServer(server);
    }

    private boolean isFeignClient(Class<?> clazz) {
        Annotation[] annotations = clazz.getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().getName().contains("FeignClient")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.init();
    }
}
