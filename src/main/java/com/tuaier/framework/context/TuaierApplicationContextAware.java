package com.tuaier.framework.context;

/**
 * 通过解耦获得IOC容器的顶层设计，后面通过一个监听器去扫描所有的类只要实现了此接口将自动调用setApplicationContext，从而将IOC容器注入目标
 * 类中
 *
 * @author tuaier
 */
public interface TuaierApplicationContextAware {

    void setApplicationContext(TuaierApplicationContext tuaierApplicationContext);
}
