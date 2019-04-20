package com.tuaier.framework.context;

import com.tuaier.framework.annotation.TuaierAutowired;
import com.tuaier.framework.annotation.TuaierController;
import com.tuaier.framework.annotation.TuaierService;
import com.tuaier.framework.beans.TuaierBeanFactory;
import com.tuaier.framework.beans.TuaierBeanWrapper;
import com.tuaier.framework.beans.config.TuaierBeanDefinition;
import com.tuaier.framework.beans.config.TuaierBeanPostProcessor;
import com.tuaier.framework.beans.support.TuaierBeanDefinitionReader;
import com.tuaier.framework.beans.support.TuaierDefaultListableBeanFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author tuaier
 */
public class TuaierApplicationContext extends TuaierDefaultListableBeanFactory implements TuaierBeanFactory {

    private String[] configLocations;

    private TuaierBeanDefinitionReader reader;

    private Map<String, Object> singletonObjects = new ConcurrentHashMap<>();

    private Map<String, TuaierBeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<>();

    public TuaierApplicationContext(String... configLocations) {
        this.configLocations = configLocations;

        refresh();
    }

    /**
     * 设置为protected只提供给子类重写
     */
    @Override
    protected void refresh() {
        //1、定位 配置文件
        reader = new TuaierBeanDefinitionReader(this.configLocations);
        //2、加载配置文件 扫描相关的类并封装成BeanDefinition
        List<TuaierBeanDefinition> beanDefinitions = reader.loadBeanDefinitions();
        //3、注册 把配置信息放到容器里面（伪IOC容器）
        doRegisterBeanDefinition(beanDefinitions);
        //4、不是延迟加载的类提前初始化
        doAutowrited();

    }

    /**
     * 非延迟加载的初始化
     */
    private void doAutowrited() {
        for (Map.Entry<String, TuaierBeanDefinition> beanDefinitionEntry : super.beanDefinitionMap.entrySet()) {
            String beanName = beanDefinitionEntry.getKey();
            if (!beanDefinitionEntry.getValue().isLazyInit()) {
                try {
                    getBean(beanName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void doRegisterBeanDefinition(List<TuaierBeanDefinition> beanDefinitions) {
        for (TuaierBeanDefinition beanDefinition : beanDefinitions) {
            super.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
        }
    }

    /**
     * 根据beanName从IOC容器获取实例bean
     *
     * @param beanName 实例名
     * @return
     */
    @Override
    public Object getBean(String beanName) throws Exception {
        Object instance = null;
        // 前通知 此段暂留 参考spring实现
        TuaierBeanPostProcessor postProcessor = new TuaierBeanPostProcessor();
        postProcessor.postProcessBeforeInitialization(instance, beanName);

        //1、初始化
        instance = instantiateBean(beanName, this.beanDefinitionMap.get(beanName));

        //2、将对象封装到beanWrapper并存入真的IOC容器中，单例对象缓存singletonObjects IOC容器factoryBeanInstanceCache
        TuaierBeanWrapper tuaierBeanWrapper = new TuaierBeanWrapper(instance);

        //3、得到beanWrapper后，将beanWrapper放入IOC容器中
        this.factoryBeanInstanceCache.put(beanName, tuaierBeanWrapper);

        // 后通知
        postProcessor.postProcessAfterInitialization(instance, beanName);

        //4、注入
        populateBean(beanName, new TuaierBeanDefinition(), tuaierBeanWrapper);
        return this.factoryBeanInstanceCache.get(beanName).getWrappedInstance();
    }

    /**
     * 根据class从IOC容器获取实例bean
     *
     * @param clazz 实例名
     * @return
     */
    @Override
    public Object getBean(Class<?> clazz) throws Exception {
        return getBean(clazz.getName());
    }

    private void populateBean(String beanName, TuaierBeanDefinition tuaierBeanDefinition, TuaierBeanWrapper tuaierBeanWrapper) {
        Object instance = tuaierBeanWrapper.getWrappedInstance();

        Class<?> clazz = tuaierBeanWrapper.getWrappedClass();
        // 没有加注解的不用处理
        if (!(clazz.isAnnotationPresent(TuaierController.class) || clazz.isAnnotationPresent(TuaierService.class))) {
            return;
        }

        // 获取所有的fields注入
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(TuaierAutowired.class)) {
                continue;
            }

            TuaierAutowired autowired = field.getAnnotation(TuaierAutowired.class);
            String autowiredBeanName = autowired.value().trim();
            if ("".equals(autowiredBeanName)) {
                autowiredBeanName = field.getType().getName();
            }

            field.setAccessible(true);
            try {
                // 为甚么为null 先后顺序
                if (this.factoryBeanInstanceCache.get(autowiredBeanName) == null) {
                    continue;
                }
                field.set(instance, this.factoryBeanInstanceCache.get(autowiredBeanName).getWrappedInstance());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private Object instantiateBean(String beanName, TuaierBeanDefinition tuaierBeanDefinition) {
        //1、根据类名实例化得到对象
        String className = tuaierBeanDefinition.getBeanClassName();
        Object instance = null;
        try {
            if (this.singletonObjects.containsKey(className)) {
                instance = this.singletonObjects.get(className);
            } else {
                Class<?> clazz = Class.forName(className);
                instance = clazz.newInstance();
                this.singletonObjects.put(className, instance);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return instance;
    }

    public String[] getBeanDefinitionNames(){
        return this.beanDefinitionMap.keySet().toArray(new String[this.beanDefinitionMap.size()]);
    }

    public int getBeanDefinitionCount(){
        return this.beanDefinitionMap.size();
    }

    public Properties getConfig(){
        return this.reader.getConfig();
    }
}
