package com.tuaier.framework.aop;

/**
 * @author tuaier
 */
public interface TuaierAopProxy {
    Object getProxy();

    Object getProxy(ClassLoader classLoader);
}
