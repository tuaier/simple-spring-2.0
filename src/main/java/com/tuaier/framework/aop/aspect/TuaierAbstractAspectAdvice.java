package com.tuaier.framework.aop.aspect;

import java.lang.reflect.Method;

/**
 * @author tuaier
 */
public abstract class TuaierAbstractAspectAdvice {
    private Method aspectMethod;
    private Object aspectTarget;

    /**
     * @param aspectMethod 代理的方法
     * @param aspectTarget 要代理的类
     */
    public TuaierAbstractAspectAdvice(Method aspectMethod, Object aspectTarget) {
        this.aspectMethod = aspectMethod;
        this.aspectTarget = aspectTarget;
    }

    public Object invokeAdviceMethod(TuaierJoinPoint joinPoint, Object returnValue, Throwable tx) throws Throwable {
        Class<?>[] paramTypes = this.aspectMethod.getParameterTypes();
        if (null == paramTypes || paramTypes.length == 0) {
            return this.aspectMethod.invoke(aspectTarget);
        } else {
            Object[] args = new Object[paramTypes.length];
            for (int i = 0; i < paramTypes.length; i++) {
                if (paramTypes[i] == TuaierJoinPoint.class) {
                    args[i] = joinPoint;
                } else if (paramTypes[i] == Throwable.class) {
                    args[i] = tx;
                } else if (paramTypes[i] == Object.class) {
                    args[i] = returnValue;
                }
            }
            return this.aspectMethod.invoke(aspectTarget, args);
        }
    }
}
