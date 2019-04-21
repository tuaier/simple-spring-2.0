package com.tuaier.framework.aop.framework;

/**
 * @author tuaier
 */
public interface TuaierMethodInterceptor {
    Object invoke(TuaierMethodInvocation invocation) throws Throwable;
}
