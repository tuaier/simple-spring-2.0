package com.tuaier.framework.beans.support;

import com.tuaier.framework.beans.config.TuaierBeanDefinition;
import com.tuaier.framework.context.support.TuaierAbstractApplicationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author tuaier
 */
public class TuaierDefaultListableBeanFactory extends TuaierAbstractApplicationContext{

    /**
     * 存储注册信息的BeanDefinition
     */
    private final Map<String, TuaierBeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(256);
}
