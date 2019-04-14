package com.tuaier.framework.context;

import com.tuaier.framework.beans.TuaierBeanFactory;
import com.tuaier.framework.beans.TuaierBeanWrapper;
import com.tuaier.framework.beans.config.TuaierBeanDefinition;
import com.tuaier.framework.beans.support.TuaierBeanDefinitionReader;
import com.tuaier.framework.beans.support.TuaierDefaultListableBeanFactory;

import java.util.List;
import java.util.Map;

/**
 * @author tuaier
 */
public class TuaierApplicationContext extends TuaierDefaultListableBeanFactory implements TuaierBeanFactory {

    private String[] configLocations;

    private TuaierBeanDefinitionReader reader;

    public TuaierApplicationContext(String... configLocations) {
        this.configLocations = configLocations;
    }

    /**
     * 设置为protected只提供给子类重写
     */
    @Override
    protected void refresh() {
        //1、定位 配置文件
        reader =  new TuaierBeanDefinitionReader(this.configLocations);
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
                getBean(beanName);
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
    public Object getBean(String beanName) {
        //1、初始化
        instantiateBean(beanName, new TuaierBeanDefinition());
        //2、注入
        populateBean(beanName, new TuaierBeanDefinition(), new TuaierBeanWrapper());
        return null;
    }

    private void populateBean(String beanName, TuaierBeanDefinition tuaierBeanDefinition, TuaierBeanWrapper tuaierBeanWrapper) {

    }

    private void instantiateBean(String beanName, TuaierBeanDefinition tuaierBeanDefinition) {

    }


}
