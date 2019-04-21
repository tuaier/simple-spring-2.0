package com.tuaier.framework.webmvc.servlet;

import com.tuaier.framework.annotation.TuaierController;
import com.tuaier.framework.annotation.TuaierRequestMapping;
import com.tuaier.framework.context.TuaierApplicationContext;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author tuaier
 */
@Slf4j
public class TuaierDispatcherServlet extends HttpServlet {

    private final String CONTEXT_CONFIG_LOCATION = "contextConfigLocation";

    private TuaierApplicationContext context;

    private List<TuaierHandlerMapping> handlerMappings =new ArrayList<>();

    private Map<TuaierHandlerMapping, TuaierHandlerAdapter> handlerAdapters = new ConcurrentHashMap<>();

    private List<TuaierViewResolver> viewResolvers = new ArrayList<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            this.doDispatch(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().write("500 EXCEPTION, DETAIL: " + Arrays.toString(e.getStackTrace()));
        }
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        //1、通过从request中拿到url匹配一个handlerMapping
        TuaierHandlerMapping handler = getHandlerM(req);
        if (handler == null) {
            processDispatchResult(req, resp, new TuaierModelAndView("404"));
            return;
        }
        //2、准备调用前的参数
        TuaierHandlerAdapter ha = getHandlerAdapter(handler);
        //3、调用真正的方法，返回modelAndView存储了要传给页面的值和页面模板的名称
        TuaierModelAndView mv = ha.handle(req, resp,handler);
        // 真正的输出处理
        processDispatchResult(req, resp, mv);
    }

    private void processDispatchResult(HttpServletRequest req, HttpServletResponse resp, TuaierModelAndView mv) throws Exception {
        //将ModelAndView转为一个Context-Type对应的html outputStrean json freemark veolcity
        if (mv == null) {
            return;
        }

        if (this.viewResolvers.isEmpty()){
            return;
        }
        for (TuaierViewResolver viewResolver : this.viewResolvers) {
            TuaierView view = viewResolver.resolveViewName(mv.getViewName(), null);
            view.render(mv.getModel(), req, resp);
            return;
        }
    }

    private TuaierHandlerAdapter getHandlerAdapter(TuaierHandlerMapping handler) {
        if(this.handlerAdapters.isEmpty()) {
            return null;
        }
        TuaierHandlerAdapter ha = this.handlerAdapters.get(handler);
        if (ha.supports(handler)) {
            return ha;
        }
        return null;
    }

    private TuaierHandlerMapping getHandlerM(HttpServletRequest req) {
        if (this.handlerMappings.isEmpty()) {
            return null;
        }
        // 获得相对路径
        String uri = req.getRequestURI();
        String context = req.getContextPath();
        uri = uri.replaceAll(context, "").replaceAll("/+", "/");

        for (TuaierHandlerMapping handler : this.handlerMappings) {
            Matcher matcher = handler.getPattern().matcher(uri);
            if (!matcher.matches()) {
                continue;
            }
            return handler;
        }
        return null;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        //1、初始化ApplicationContext
        context = new TuaierApplicationContext(config.getInitParameter(CONTEXT_CONFIG_LOCATION));
        //2、初始化mvc九大组件
        initStrategies(context);
    }

    protected void initStrategies(TuaierApplicationContext context) {
        initMultipartResolver(context);
        initLocaleResolver(context);
        initThemeResolver(context);

        initHandlerMappings(context);
        initHandlerAdapters(context);

        initHandlerExceptionResolvers(context);
        initRequestToViewNameTranslator(context);

        initViewResolvers(context);

        initFlashMapManager(context);
    }

    /**
     * 参数缓存器
     */
    private void initFlashMapManager(TuaierApplicationContext context) {

    }

    /**
     * 视图转换器
     */
    private void initViewResolvers(TuaierApplicationContext context) {
        //得到模板的存放目录
        String templateRoot = context.getConfig().getProperty("templateRoot");
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();

        File templateRootDir = new File(templateRootPath);
        for (File template : templateRootDir.listFiles()) {
            this.viewResolvers.add(new TuaierViewResolver(templateRoot));
        }

    }

    /**
     * 视图预处理器
     */
    private void initRequestToViewNameTranslator(TuaierApplicationContext context) {

    }

    /**
     * 异常拦截器
     */
    private void initHandlerExceptionResolvers(TuaierApplicationContext context) {

    }

    /**
     * 参数适配器 将一个request请求变成一个handler 参数都是字符串的需要自动匹配到handler的形参
     * 需要匹配到handlerMapping中才能使用
     */
    private void initHandlerAdapters(TuaierApplicationContext context) {
        for (TuaierHandlerMapping handlerMapping : this.handlerMappings) {
            this.handlerAdapters.put(handlerMapping, new TuaierHandlerAdapter());
        }
    }

    private void initHandlerMappings(TuaierApplicationContext context) {
        String[] beanNames = context.getBeanDefinitionNames();
        try {
            for (String beanName : beanNames) {
                Object controller = context.getBean(beanName);
                Class<?> clazz = controller.getClass();
                if (!clazz.isAnnotationPresent(TuaierController.class)) {
                    continue;
                }
                // 类上的地址获取
                String baseURI = "";
                if (clazz.isAnnotationPresent(TuaierRequestMapping.class)) {
                    baseURI = clazz.getAnnotation(TuaierRequestMapping.class).value();
                }
                // 获取所有方法上的地址
                for (Method method : clazz.getMethods()) {
                    if (!method.isAnnotationPresent(TuaierRequestMapping.class)) {
                        continue;
                    }
                    String methodURI = method.getAnnotation(TuaierRequestMapping.class).value();
                    // URL处理/
                    String regexURI = ("/" + baseURI + "/" + methodURI.replaceAll("\\*", ".*")).replaceAll("/+", "/");

                    TuaierHandlerMapping handlerMapping = new TuaierHandlerMapping(Pattern.compile(regexURI), controller, method);
                    this.handlerMappings.add(handlerMapping);
                    log.info("Mapped :" + Pattern.compile(regexURI) + "," + method);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 模板处理器
     */
    private void initThemeResolver(TuaierApplicationContext context) {

    }

    /**
     * 本地语言环境
     */
    private void initLocaleResolver(TuaierApplicationContext context) {

    }

    /**
     * 多文件上传
     */
    private void initMultipartResolver(TuaierApplicationContext context) {

    }
}
