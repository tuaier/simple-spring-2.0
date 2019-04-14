package com.tuaier.framework.beans;

/**
 * 单例工厂的顶层设计
 *
 * @author tuaier
 */
public interface TuaierBeanFactory {

    /**
     * 根据beanName从IOC容器获取实例bean
     *
     * @param beanName 实例名
     * @return
     */
    Object getBean(String beanName);
}
