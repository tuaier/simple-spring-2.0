package com.tuaier.framework.webmvc.servlet;

import com.tuaier.framework.annotation.TuaierRequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author tuaier
 */
public class TuaierHandlerAdapter {
    public boolean supports(Object handler) {
        return handler instanceof TuaierHandlerMapping;
    }

    TuaierModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        TuaierHandlerMapping handlerMapping = (TuaierHandlerMapping) handler;

        Map<String, Integer> paramIndexMapping = new HashMap<>();

        // 方法的注解，因为一个字段可以有多个注解以及一个方法有多个参数所以是个二维数组
        Annotation[][] parameterAnnotations = handlerMapping.getMethod().getParameterAnnotations();
        for (int i = 0; i < parameterAnnotations.length; i ++) {
            for (Annotation a : parameterAnnotations[i]) {
                if (a instanceof TuaierRequestParam) {
                    String paramName = ((TuaierRequestParam) a).value();
                    if (!"".equals(paramName.trim())) {
                        paramIndexMapping.put(paramName, i);
                    }
                }
            }
        }

        Class<?>[] parameterTypes = handlerMapping.getMethod().getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            if (parameterTypes[i] == HttpServletRequest.class || parameterTypes[i] == HttpServletResponse.class) {
                paramIndexMapping.put(parameterTypes[i].getName(), i);
            }
        }

        // 获取方法的形参列表
        Map<String, String[]> parameterMap = request.getParameterMap();
        // 实参列表
        Object[] paramValues = new Object[parameterTypes.length];

        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            String value = Arrays.toString(entry.getValue()).replaceAll("\\[|]", "").replaceAll("\\s", "");
            if (!paramIndexMapping.containsKey(entry.getKey())) {
                continue;
            }
            int index = paramIndexMapping.get(entry.getKey());
            paramValues[index] = caseStringValue(value, parameterTypes[index]);
        }
        if (paramIndexMapping.containsKey(HttpServletRequest.class.getName())) {
            int reqIndex = paramIndexMapping.get(HttpServletRequest.class.getName());
            paramValues[reqIndex] = request;
        }

        if (paramIndexMapping.containsKey(HttpServletResponse.class.getName())) {
            int respIndex = paramIndexMapping.get(HttpServletResponse.class.getName());
            paramValues[respIndex] = response;
        }

        Object returnValue = handlerMapping.getMethod().invoke(handlerMapping.getController(), paramValues);

        if (returnValue == null || returnValue instanceof Void) {
            return null;
        }
        boolean isModelAndView = handlerMapping.getMethod().getReturnType() == TuaierModelAndView.class;
        if (isModelAndView){
            return (TuaierModelAndView) returnValue;
        }
        return null;
    }

    private Object caseStringValue(String value, Class<?> parameterType) {
        if (Integer.class == parameterType) {
            return Integer.valueOf(value);
        } else if (Double.class == parameterType) {
            return Double.valueOf(value);
        } else if (String.class == parameterType) {
            return value;
        } else {
            return value;
        }
    }
}
