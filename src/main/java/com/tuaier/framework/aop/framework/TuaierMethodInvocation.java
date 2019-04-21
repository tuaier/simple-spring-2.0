package com.tuaier.framework.aop.framework;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author tuaier
 */
public class TuaierMethodInvocation {

    public TuaierMethodInvocation(Object proxy, Object target, Method method, Object[] arguments,
                                  Class<?> targetClass, List<Object> interceptorsAndDynamicMethodMatchers) {

        /*this.proxy = proxy;
        this.target = target;
        this.targetClass = targetClass;
        this.method = BridgeMethodResolver.findBridgedMethod(method);
        this.arguments = AopProxyUtils.adaptArgumentsIfNecessary(method, arguments);
        this.interceptorsAndDynamicMethodMatchers = interceptorsAndDynamicMethodMatchers;*/
    }

    public Object proceed() throws Throwable {
        return null;
    }
}
