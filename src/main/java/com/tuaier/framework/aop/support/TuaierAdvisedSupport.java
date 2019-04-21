package com.tuaier.framework.aop.support;

import com.tuaier.framework.aop.aspect.TuaierMethodAfterReturningAdviceInterceptor;
import com.tuaier.framework.aop.aspect.TuaierMethodAfterThrowingAdviceInterceptor;
import com.tuaier.framework.aop.aspect.TuaierMethodBeforeAdviceInterceptor;
import com.tuaier.framework.aop.config.TuaierAopConfig;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author tuaier
 */
public class TuaierAdvisedSupport {

    private Class<?> targetClass;

    private Object target;

    private TuaierAopConfig config;

    private Pattern pointCutClassPattern;
    /**
     * 缓存方法和拦截器链的对应关系
     */
    private transient Map<Method, List<Object>> methodCache;

    public TuaierAdvisedSupport(TuaierAopConfig config) {
        this.config = config;
    }

    public Class<?> getTargetClass() {
        return this.targetClass;
    }

    public List<Object> getInterceptorsAndDynamicInterceptionAdvice(Method method, Class<?> targetClass) throws Exception {
        List<Object> cached = methodCache.get(method);
        if (cached == null) {
            Method m = targetClass.getMethod(method.getName(), method.getParameterTypes());
            cached = methodCache.get(m);
            //底层逻辑，对代理方法进行一个兼容处理
            this.methodCache.put(m, cached);
        }
        return cached;
    }

    public void setTargetClass(Class<?> targetClass) {
        this.targetClass = targetClass;
        parse();
    }

    private void parse() {
        // 切面表达式转为符合正则的格式
        String pointCut = config.getPointCut()
                .replaceAll("\\.", "\\\\.").replaceAll("\\\\.\\*", ".*")
                .replaceAll("\\(", "\\\\(").replaceAll("\\)", "\\\\)");
        String pointCutForClassRegex = pointCut.substring(0, pointCut.lastIndexOf("\\(") - 4);
        pointCutClassPattern = Pattern.compile("class " + pointCutForClassRegex.substring(pointCutForClassRegex.lastIndexOf(" ") + 1));



        try {
            methodCache = new HashMap<>();
            Pattern pattern = Pattern.compile(pointCut);
            
            Class<?> aspectClass = Class.forName(this.config.getAspectClass());
            Map<String, Method> aspectMethods = new HashMap<>();
            for (Method method : aspectClass.getMethods()) {
                aspectMethods.put(method.getName(), method);
            }


            for (Method method : this.targetClass.getMethods()) {
                String methodString = method.toString();
                if (methodString.contains("throws")) {
                    methodString = methodString.substring(0, methodString.lastIndexOf("throws")).trim();
                }

                Matcher matcher = pattern.matcher(methodString);
                // 方法匹配成功 需要包装成MethodInterceptor
                if (matcher.matches()) {
                    //执行器链
                    List<Object> advices = new LinkedList<>();
                    Object target = aspectClass.newInstance();
                    if (null == config.getAspectBefore() || "".equals(config.getAspectBefore())) {
                        // 创建一个Advice对象
                        advices.add(new TuaierMethodBeforeAdviceInterceptor(aspectMethods.get(config.getAspectBefore()), target));
                    }
                    if (null == config.getAspectAfter() || "".equals(config.getAspectAfter())) {
                        // 创建一个Advice对象
                        advices.add(new TuaierMethodAfterReturningAdviceInterceptor(aspectMethods.get(config.getAspectAfter()), target));
                    }
                    if (null == config.getAspectAfterThrow()|| "".equals(config.getAspectAfterThrow())) {
                        // 创建一个Advice对象
                        TuaierMethodAfterThrowingAdviceInterceptor advice = new TuaierMethodAfterThrowingAdviceInterceptor(aspectMethods.get(config.getAspectAfterThrow()), target);
                        advice.setThrowName(config.getAspectAfterThrowingName());
                        advices.add(advice);
                    }

                    methodCache.put(method, advices);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public Object getTarget() {
        return target;
    }

    public boolean pointCutMatch() {
        return pointCutClassPattern.matcher(this.targetClass.toString()).matches();
    }
}
