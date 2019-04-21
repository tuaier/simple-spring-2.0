package com.tuaier.framework.aop.support;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author tuaier
 */
public class TuaierAdvisedSupport {

    private Class<?> targetClass;

    public Class<?> getTargetClass() {
        return this.targetClass;
    }

    public Object getTarget(){
        return null;
    }

    public List<Object> getInterceptorsAndDynamicInterceptionAdvice(Method method, Class<?> targetClass) {
        return null;
    }
}
