package com.tuaier.framework.aop.aspect;

import com.tuaier.framework.aop.framework.TuaierMethodInterceptor;
import com.tuaier.framework.aop.framework.TuaierMethodInvocation;

import java.lang.reflect.Method;

/**
 * @author tuaier
 */
public class TuaierMethodAfterReturningAdviceInterceptor extends TuaierAbstractAspectAdvice implements TuaierAdvice,TuaierMethodInterceptor {
    private TuaierJoinPoint joinPoint;


    /**
     * @param aspectMethod 代理的方法
     * @param aspectTarget 要代理的类
     */
    public TuaierMethodAfterReturningAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    @Override
    public Object invoke(TuaierMethodInvocation mi) throws Throwable {
        Object retVal = mi.proceed();
        this.joinPoint = mi;
        this.afterReturning(retVal, mi.getMethod(), mi.getArguments(), mi.getThis());
        return retVal;
    }

    private void afterReturning(Object retVal, Method method, Object[] arguments, Object aThis) throws Throwable {
        super.invokeAdviceMethod(this.joinPoint, retVal, null);
    }
}
