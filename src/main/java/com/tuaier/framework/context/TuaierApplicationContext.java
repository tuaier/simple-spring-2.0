package com.tuaier.framework.context;

import com.tuaier.framework.beans.TuaierBeanFactory;
import com.tuaier.framework.beans.support.TuaierDefaultListableBeanFactory;

/**

 */
public class TuaierApplicationContext extends TuaierDefaultListableBeanFactory implements TuaierBeanFactory {

    /**
     * 设置为protected只提供给子类重写
     */
    @Override
    protected void refresh() {
        super.refresh();
    }

    /**
     * 根据beanName从IOC容器获取实例bean
     *
     * @param beanName 实例名
     * @return
     */
    @Override
    public Object getBean(String beanName) {
        return null;
    }


}
