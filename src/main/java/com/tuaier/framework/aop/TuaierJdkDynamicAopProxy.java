package com.tuaier.framework.aop;

import com.tuaier.framework.aop.framework.TuaierMethodInvocation;
import com.tuaier.framework.aop.support.TuaierAdvisedSupport;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * @author tuaier
 */
public class TuaierJdkDynamicAopProxy implements TuaierAopProxy, InvocationHandler {
    private TuaierAdvisedSupport advised;

    public TuaierJdkDynamicAopProxy(TuaierAdvisedSupport config) {
        this.advised = config;
    }

    @Override
    public Object getProxy() {
        return getProxy(this.advised.getTargetClass().getClassLoader());
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        return Proxy.newProxyInstance(classLoader, this.advised.getTargetClass().getInterfaces(), this);
    }
    
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        List<Object> chain = this.advised.getInterceptorsAndDynamicInterceptionAdvice(method, this.advised.getTargetClass());
        TuaierMethodInvocation invocation = new TuaierMethodInvocation(proxy, this.advised.getTarget(), method, args, this.advised.getTargetClass(), chain);
        return invocation.proceed();
    }
}
