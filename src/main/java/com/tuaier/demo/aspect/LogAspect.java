package com.tuaier.demo.aspect;

import com.tuaier.framework.aop.aspect.TuaierJoinPoint;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * 测试切面
 *
 * @author tuaier
 */
@Slf4j
public class LogAspect {

    /**
     * 在调用一个方法之前，执行before方法
     */
    public void before(TuaierJoinPoint joinPoint) {
        joinPoint.setUserAttribute("startTime_" + joinPoint.getMethod().getName(), System.currentTimeMillis());
        //这个方法中的逻辑，是由我们自己写的
        log.info("Invoker Before Method!!!" + "\nTargetObject:" + joinPoint.getThis() + "\nArgs:" + Arrays.toString(joinPoint.getArguments()));
    }

    /**
     * 在调用一个方法之后，执行after方法
     */
    public void after(TuaierJoinPoint joinPoint) {
        log.info("Invoker After Method!!!" + "\nTargetObject:" + joinPoint.getThis() + "\nArgs:" + Arrays.toString(joinPoint.getArguments()));
        long startTime = (Long) joinPoint.getUserAttribute("startTime_" + joinPoint.getMethod().getName());
        long endTime = System.currentTimeMillis();
        System.out.println("use time :" + (endTime - startTime));
    }

    /**
     * 在调用方法跑出异常后之后，执行afterThrowing方法
     */
    public void afterThrowing(TuaierJoinPoint joinPoint, Throwable ex) {
        log.info("出现异常" + "\nTargetObject:" + joinPoint.getThis() + "\nArgs:" + Arrays.toString(joinPoint.getArguments()) + "\nThrows:" + ex.getMessage());
    }

}
