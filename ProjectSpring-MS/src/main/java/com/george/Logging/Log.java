package com.george.Logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Aspect for logging application flow across Controller, Service, and Repository layers.
 */
@Aspect
@Component
public class Log {

    private static final Logger LOGGER = LoggerFactory.getLogger(Log.class);

    /**
     * Pointcut that matches all classes in controller, service, and repository packages.
     */
    @Pointcut("within(com.george.controller..*) || within(com.george.Service..*) || within(com.george.repository..*)")
    public void applicationPointcut() {}

    /**
     * Logs method entry with class name, method name, and arguments.
     *
     * @param joinPoint provides reflective access to method being intercepted.
     */
    @Before("applicationPointcut()")
    public void logBefore(org.aspectj.lang.JoinPoint joinPoint) {
        LOGGER.info("Starting method: {}.{} with arguments: {}", 
            joinPoint.getTarget().getClass().getSimpleName(),
            joinPoint.getSignature().getName(),
            joinPoint.getArgs());
    }

    /**
     * Logs method exit after successful execution with return value.
     *
     * @param joinPoint provides method details.
     * @param result the returned object from the method.
     */
    @AfterReturning(pointcut = "applicationPointcut()", returning = "result")
    public void logAfterReturning(org.aspectj.lang.JoinPoint joinPoint, Object result) {
        LOGGER.info("Method {}.{} completed successfully with return: {}", 
            joinPoint.getTarget().getClass().getSimpleName(),
            joinPoint.getSignature().getName(),
            result);
    }

    /**
     * Logs any exception thrown by the method.
     *
     * @param joinPoint provides method details.
     * @param ex the exception that was thrown.
     */
    @AfterThrowing(pointcut = "applicationPointcut()", throwing = "ex")
    public void logAfterThrowing(org.aspectj.lang.JoinPoint joinPoint, Exception ex) {
        LOGGER.error("Exception in {}.{}: {} - {}", 
            joinPoint.getTarget().getClass().getSimpleName(),
            joinPoint.getSignature().getName(),
            ex.getClass().getSimpleName(),
            ex.getMessage(),
            ex);
    }

    /**
     * Logs execution time for a method, and raises warning if method takes too long.
     *
     * @param joinPoint provides reflective access and allows proceeding with method execution.
     * @return the result from the method execution.
     * @throws Throwable allows propagation of original exception.
     */
    @Around("applicationPointcut()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;

            // Warn if execution takes longer than 1 second
            if (executionTime > 1000) {
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
