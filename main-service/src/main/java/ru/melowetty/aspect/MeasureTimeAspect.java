package ru.melowetty.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class MeasureTimeAspect {
    @Around("@annotation(ru.melowetty.annotation.Timed)")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object proceed = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - start;
        LoggerFactory.getLogger(joinPoint.getSignature().getDeclaringType()).info("Method {} executed in {}ms",
                joinPoint.getSignature().getName(), executionTime);
        return proceed;
    }
}
