package com.tuaier.framework.webmvc.servlet;

import java.io.File;
import java.util.Locale;

/**
 * @author tuaier
 */
public class TuaierViewResolver {
    private final String DEFAULT_TEMPLATE_SUFFIX = ".html";

    private File templateRootDir;

    private String viewName;

    public TuaierViewResolver(String templateRoot) {
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        this.templateRootDir = new File(templateRootPath);
    }

    public TuaierView resolveViewName(String viewName, Locale locale) throws Exception{
        if (viewName == null || "".equals(viewName)) {
            return null;
        }
        viewName = viewName.toLowerCase().endsWith(DEFAULT_TEMPLATE_SUFFIX) ? viewName : viewName + DEFAULT_TEMPLATE_SUFFIX;
        File templateFile = new File((templateRootDir.getPath() + "/" + viewName).replaceAll("/+", "/"));
        return new TuaierView(templateFile);
    }
}
