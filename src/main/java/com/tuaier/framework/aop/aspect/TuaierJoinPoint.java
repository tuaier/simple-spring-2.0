package com.tuaier.framework.aop.aspect;

import java.lang.reflect.Method;

/**
 * @author tuaier
 */
public interface TuaierJoinPoint {

    Object getThis();

    Object[] getArguments();

    Method getMethod();

    void setUserAttribute(String key, Object value);

    Object getUserAttribute(String key);

}
