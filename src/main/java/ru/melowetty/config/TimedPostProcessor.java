package ru.melowetty.config;

import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import ru.melowetty.annotation.Timed;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class TimedPostProcessor implements BeanPostProcessor {
    private final Map<String, Class<?>> timedClasses = new HashMap<>();

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if(timedClasses.containsKey(beanName)) {
            var proxy = new ProxyFactory(bean);
            proxy.addAdvice(new TimedMethodInterceptor());
            return proxy.getProxy();
        }
        return bean;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if(bean.getClass().getAnnotation(Timed.class) != null) {
            timedClasses.put(beanName, bean.getClass());
        }
        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }

    static class TimedMethodInterceptor implements MethodInterceptor {
        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            long start = System.currentTimeMillis();
            Object proceed = invocation.proceed();
            long executionTime = System.currentTimeMillis() - start;
            LoggerFactory.getLogger(invocation.getMethod().getDeclaringClass()).info("Method {} executed in {}ms", invocation.getMethod().getName(), executionTime);
            return proceed;
        }
    }
}
