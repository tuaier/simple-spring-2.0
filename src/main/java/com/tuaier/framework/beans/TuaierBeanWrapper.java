package com.tuaier.framework.beans;

/**
 * @author tuaier
 */
public class TuaierBeanWrapper {

    private Object wrappedInstance;

    private Class<?> wrappedClass;

    public TuaierBeanWrapper(Object wrappedInstance) {
        this.wrappedInstance = wrappedInstance;

    }
    public Object getWrappedInstance(){
        return this.wrappedInstance;
    }

    /**
     * 实例或者代理对象 $proxy0
     */
    public Class<?> getWrappedClass(){
        return this.wrappedInstance.getClass();
    }
}