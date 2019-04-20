package com.tuaier.demo;

import com.tuaier.demo.controller.DemoController;
import com.tuaier.framework.context.TuaierApplicationContext;

/**
 * 测试类
 *
 * @author tuaier
 */
public class TestSpring {
    public static void main(String[] args) {
        TuaierApplicationContext context = new TuaierApplicationContext("classpath:application.properties");
        try {
            Object bean = context.getBean(DemoController.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(context);
    }
}
