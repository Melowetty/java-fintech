package ru.melowetty.config;

import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.melowetty.annotation.Timed;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TimedPostProcessorTest {

    private TimedPostProcessor timedPostProcessor;

    @BeforeEach
    void setUp() {
        timedPostProcessor = new TimedPostProcessor();
    }

    @Test
    public void postProcessBeforeInitialization_withTimedAnnotation_addsToTimedClasses() {
        Object bean = new TestObject();
        String beanName = "timedBean";

        timedPostProcessor.postProcessBeforeInitialization(bean, beanName);

        assertTrue(timedPostProcessor.timedClasses.containsKey(beanName));
    }

    @Test
    public void postProcessBeforeInitialization_withoutTimedAnnotation_doesNotAddToTimedClasses() {
        Object bean = new Object();
        String beanName = "nonTimedBean";

        timedPostProcessor.postProcessBeforeInitialization(bean, beanName);

        assertFalse(timedPostProcessor.timedClasses.containsKey(beanName));
    }

    @Test
    public void postProcessAfterInitialization_withoutTimedClass_returnsOriginalBean() {
        Object bean = new Object();
        String beanName = "nonTimedBean";

        Object result = timedPostProcessor.postProcessAfterInitialization(bean, beanName);

        assertSame(bean, result);
    }

    @Test
    public void timedMethodInterceptor_invokesMethodAndLogsExecutionTime() throws Throwable {
        TimedPostProcessor.TimedMethodInterceptor interceptor = new TimedPostProcessor.TimedMethodInterceptor();
        MethodInvocation invocation = mock(MethodInvocation.class);
        when(invocation.proceed()).thenReturn(null);
        when(invocation.getMethod()).thenReturn(Object.class.getMethod("toString"));

        interceptor.invoke(invocation);

        verify(invocation, times(1)).proceed();
    }

    @Timed
    static class TestObject {
    }
}