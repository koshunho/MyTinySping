package com.huang.tinyioc.aop.aspect;

// 切点
public interface Pointcut {
    ClassFilter getClassFilter();

    MethodMatcher getMethodMatcher();
}
