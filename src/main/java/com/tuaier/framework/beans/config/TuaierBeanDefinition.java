package com.tuaier.framework.beans.config;

import lombok.Data;

/**
 * @author tuaier
 */
@Data
public class TuaierBeanDefinition {

    private String beanClassName;
    private boolean lazyInit = false;
    private String factoryBeanName;

}
