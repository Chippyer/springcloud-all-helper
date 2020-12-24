package com.chippy.core.common.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;

/**
 * 通用Spring上下文容器
 *
 * @author: chippy
 */
public class CommonSpringContext implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        if (ObjectsUtil.isNotEmpty(applicationContext)) {
            this.applicationContext = applicationContext;
        }
    }

    /**
     * 获取applicationContext
     *
     * @return org.springframework.context.ApplicationContext
     * @author chippy
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 通过name获取 Bean.
     *
     * @param name bean的名称
     * @return java.lang.Object
     * @author chippy
     */
    public static Object getBean(String name) {
        return getApplicationContext().getBean(name);
    }

    /**
     * 通过class获取Bean.
     *
     * @param clazz bean的类型
     * @return T clazz类型的bean
     * @author chippy
     */
    public static <T> T getBean(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }

    /**
     * 通过name,以及Clazz返回指定的Bean
     *
     * @param name  bean的名称
     * @param clazz bean的类型
     * @return T clazz类型的bean
     * @author chippy
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        return getApplicationContext().getBean(name, clazz);
    }

    /**
     * 获取Spring的环境变量
     *
     * @return org.springframework.core.env.Environment
     * @author chippy
     */
    public static Environment getEnvironment() {
        return getApplicationContext().getEnvironment();
    }
}
