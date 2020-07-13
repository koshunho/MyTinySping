package com.huang.tinyioc.aop.aspect;

// 类过滤器
public interface ClassFilter {
    boolean matches(Class target);
}
