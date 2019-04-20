package com.tuaier.framework.webmvc.servlet;

import lombok.Data;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * @author tuaier
 */
@Data
public class TuaierHandlerMapping {
    private Object controller;
    private Method method;
    /**
     *     URL的正则匹配
     */
    private Pattern pattern;

    public TuaierHandlerMapping(Pattern pattern, Object controller, Method method) {
        this.controller = controller;
        this.method = method;
        this.pattern = pattern;
    }
}
