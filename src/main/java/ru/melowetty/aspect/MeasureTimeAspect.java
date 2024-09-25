package ru.melowetty.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class MeasureTimeAspect {
    @Around("@annotation(ru.melowetty.annotation.Timed)")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object proceed = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - start;
        log.info("{}.{} executed in {}ms", joinPoint.getSignature().getDeclaringType().getName(),
                joinPoint.getSignature().getName(), executionTime);
        return proceed;
    }
}
