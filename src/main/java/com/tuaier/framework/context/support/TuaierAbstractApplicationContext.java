package com.tuaier.framework.context.support;

/**
 * IOC容器实现的顶层设计
 *
 * @author tuaier
 */
public abstract class TuaierAbstractApplicationContext {

    /**
     * 设置为protected只提供给子类重写
     */
    protected void refresh(){}
}
