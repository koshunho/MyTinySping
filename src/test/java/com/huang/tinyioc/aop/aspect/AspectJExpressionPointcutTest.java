package com.huang.tinyioc.aop.aspect;

import com.huang.tinyioc.Konnichiha;
import com.huang.tinyioc.KonnichihaImpl;
import org.junit.Assert;
import org.junit.Test;

public class AspectJExpressionPointcutTest {
    @Test
    public void testClassFilter() throws Exception {
        String expression = "execution(* com.huang.tinyioc.*.*(..))";
        AspectJExpressionPointcut aspectJExpressionPointcut = new AspectJExpressionPointcut();
        aspectJExpressionPointcut.setExpression(expression);
        boolean matches = aspectJExpressionPointcut.getClassFilter().matches(Konnichiha.class);
        Assert.assertTrue(matches);
    }

    @Test
    public void testMethodInterceptor() throws Exception {
        String expression = "execution(* com.huang.tinyioc.*.*(..))";
        AspectJExpressionPointcut aspectJExpressionPointcut = new AspectJExpressionPointcut();
        aspectJExpressionPointcut.setExpression(expression);
        boolean matches = aspectJExpressionPointcut.getMethodMatcher().matches(KonnichihaImpl.class.getDeclaredMethod("say"),KonnichihaImpl.class);
        Assert.assertTrue(matches);
    }
}
