package com.george.Logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class Log {

    private static final Logger LOGGER = LoggerFactory.getLogger(Log.class);
    
    // Pointcut to capture all methods in controller, service, and repository layers
    @Pointcut("within(com.george.controller..*) || within(com.george.Service..*) || within(com.george.repository..*)")
    public void applicationPointcut() {}

    // Log before method execution with method details
    @Before("applicationPointcut()")
    public void logBefore(org.aspectj.lang.JoinPoint joinPoint) {
        LOGGER.info("Starting method: {}.{} with arguments: {}", 
            joinPoint.getTarget().getClass().getSimpleName(),
            joinPoint.getSignature().getName(),
            joinPoint.getArgs());
    }

    // Log after successful method execution with return value
    @AfterReturning(pointcut = "applicationPointcut()", returning = "result")
    public void logAfterReturning(org.aspectj.lang.JoinPoint joinPoint, Object result) {
        LOGGER.info("Method {}.{} completed successfully with return: {}", 
            joinPoint.getTarget().getClass().getSimpleName(),
            joinPoint.getSignature().getName(),
            result);
    }

    // Log exceptions with detailed error information
    @AfterThrowing(pointcut = "applicationPointcut()", throwing = "ex")
    public void logAfterThrowing(org.aspectj.lang.JoinPoint joinPoint, Exception ex) {
        LOGGER.error("Exception in {}.{}: {} - {}", 
            joinPoint.getTarget().getClass().getSimpleName(),
            joinPoint.getSignature().getName(),
            ex.getClass().getSimpleName(),
            ex.getMessage(),
            ex);
    }

    // Log method execution time and details
    @Around("applicationPointcut()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;
            
            if (executionTime > 1000) { // Log warning for slow methods
                LOGGER.warn("Slow execution - {}.{} took {} ms", 
                    className, methodName, executionTime);
            } else {
                LOGGER.info("Normal execution - {}.{} took {} ms", 
                    className, methodName, executionTime);
            }
            
            return result;
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            LOGGER.error("Exception in {}.{} after {} ms: {}", 
                className, methodName, executionTime, e.getMessage(), e);
            throw e;
        }
    }
}