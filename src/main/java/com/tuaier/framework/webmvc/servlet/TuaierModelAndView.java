package com.tuaier.framework.webmvc.servlet;

import java.util.Map;

/**
 * @author tuaier
 */
public class TuaierModelAndView {

    private String viewName;
    private Map<String, ?> model;

    public TuaierModelAndView(String viewName) {
        this.viewName = viewName;
    }

    public TuaierModelAndView(String viewName, Map<String, ?> model) {
        this.viewName = viewName;
        this.model = model;
    }

    public String getViewName() {
        return viewName;
    }

    public Map<String, ?> getModel() {
        return model;
    }
}
