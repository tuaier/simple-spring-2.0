package com.tuaier.framework.aop.config;

import lombok.Data;

/**
 * application.properties中配置的切面实体
 *
 * @author tuaier
 */
@Data
public class TuaierAopConfig {
    private String pointCut;
    private String aspectBefore;
    private String aspectAfter;
    private String aspectClass;
    private String aspectAfterThrow;
    private String aspectAfterThrowingName;
}
