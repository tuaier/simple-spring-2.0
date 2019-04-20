package com.tuaier.framework.webmvc.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author tuaier
 */
public class TuaierHandlerAdapter {
    public boolean supports(Object handler) {
        return handler instanceof TuaierHandlerMapping;
    }

    public TuaierModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        return null;
    }
}
