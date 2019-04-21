package com.tuaier.framework.aop.aspect;

import com.tuaier.framework.aop.framework.TuaierMethodInterceptor;
import com.tuaier.framework.aop.framework.TuaierMethodInvocation;

import java.lang.reflect.Method;

/**
 * @author tuaier
 */
public class TuaierMethodBeforeAdviceInterceptor extends TuaierAbstractAspectAdvice implements TuaierAdvice,TuaierMethodInterceptor {
    private TuaierJoinPoint joinPoint;

    /**
     * @param aspectMethod 代理的方法
     * @param aspectTarget 要代理的类
     */
    public TuaierMethodBeforeAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    private void before(Method method, Object[] args, Object target) throws Throwable {
        super.invokeAdviceMethod(this.joinPoint, null, null);
    }

    @Override
    public Object invoke(TuaierMethodInvocation mi) throws Throwable {
        //从被织入的代码中才能拿到，JoinPoint
        this.joinPoint = mi;
        before(mi.getMethod(), mi.getArguments(), mi.getThis());
        return mi.proceed();
    }
}
