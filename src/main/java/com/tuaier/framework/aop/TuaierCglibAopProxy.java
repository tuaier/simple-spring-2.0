package com.tuaier.framework.aop;

import com.tuaier.framework.aop.support.TuaierAdvisedSupport; /**
 * @author tuaier
 */
public class TuaierCglibAopProxy implements TuaierAopProxy {
    
    public TuaierCglibAopProxy(TuaierAdvisedSupport config) {

    }

    @Override
    public Object getProxy() {
        return null;
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        return null;
    }
}
