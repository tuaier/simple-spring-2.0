package com.tuaier.framework.aop.aspect;

import com.tuaier.framework.aop.framework.TuaierMethodInterceptor;
import com.tuaier.framework.aop.framework.TuaierMethodInvocation;

import java.lang.reflect.Method;

/**
 * @author tuaier
 */
public class TuaierMethodAfterThrowingAdviceInterceptor extends TuaierAbstractAspectAdvice implements TuaierAdvice,TuaierMethodInterceptor {
    private String throwingName;

    /**
     * @param aspectMethod 代理的方法
     * @param aspectTarget 要代理的类
     */
    public TuaierMethodAfterThrowingAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    @Override
    public Object invoke(TuaierMethodInvocation mi) throws Throwable {
        try {
            return mi.proceed();
        }catch (Throwable e){
            invokeAdviceMethod(mi,null,e.getCause());
            throw e;
        }
    }

    public void setThrowName(String throwName) {
        this.throwingName = throwName;
    }
}
