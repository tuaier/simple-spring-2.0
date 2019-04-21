package com.tuaier.demo.controller;

import com.tuaier.demo.service.IDemoService;
import com.tuaier.framework.annotation.TuaierAutowired;
import com.tuaier.framework.annotation.TuaierController;
import com.tuaier.framework.annotation.TuaierRequestMapping;
import com.tuaier.framework.annotation.TuaierRequestParam;
import com.tuaier.framework.webmvc.servlet.TuaierModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author tuaier
 * @since 2019-03-25
 */
@TuaierController
@TuaierRequestMapping("/tuaier")
public class DemoController {

    @TuaierAutowired
    private IDemoService demoService;

    @TuaierRequestMapping("/query")
    public TuaierModelAndView query(HttpServletRequest req, HttpServletResponse resp, @TuaierRequestParam("name") String name) {
        String result = demoService.getName(name);
        return out(resp, result);
    }

    @TuaierRequestMapping("/add")
    public TuaierModelAndView add(HttpServletRequest req, HttpServletResponse resp, @TuaierRequestParam("a") String a, @TuaierRequestParam("b") String b) {
        /*String result = a + "+" + b + "=" + (a + b);
        return out(resp, result);*/
        try {
            String result = demoService.add(a, b);
            return out(resp, result);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> model = new HashMap<>();
            model.put("detail", e.getMessage());
            model.put("stackTrace", Arrays.toString(e.getStackTrace()));
            return new TuaierModelAndView("500", model);
        }
    }

    @TuaierRequestMapping("/sub")
    public TuaierModelAndView sub (HttpServletRequest req, HttpServletResponse resp, @TuaierRequestParam("a") Double a, @TuaierRequestParam("b") Double b) {
        String result = a + "-" + b + "=" + (a - b);
        return out(resp, result);
    }

    @TuaierRequestMapping("/up.*")
    public String up (@TuaierRequestParam("up") Integer up) {
        return "" + up;
    }

    private TuaierModelAndView out(HttpServletResponse response, String result) {
        try {
            response.getWriter().write(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
