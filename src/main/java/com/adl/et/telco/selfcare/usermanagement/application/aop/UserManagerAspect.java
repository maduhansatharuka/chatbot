/**
 * Copyrights 2024 Axiata Digital Labs Pvt Ltd.
 * All Rights Reserved.
 * <p>
 * These material are unpublished, proprietary, confidential source
 * code of Axiata Digital Labs Pvt Ltd (ADL) and constitute a TRADE
 * SECRET of ADL.
 * <p>
 * ADL retains all title to and intellectual property rights in these
 * materials.
 */
package com.adl.et.telco.selfcare.usermanagement.application.aop;

import ch.qos.logback.classic.Logger;
import com.adl.et.telco.dte.plugin.logging.services.LoggingUtils;
import com.adl.et.telco.selfcare.usermanagement.domain.exception.DomainException;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.jboss.logging.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static com.adl.et.telco.selfcare.usermanagement.domain.util.logs.LogConstants.*;

/**
 * This AOP class is used to manage logs
 *
 * @author Hasini Hatharasinghe
 * @version 1.0
 * @since 2024/10/23
 */

@Aspect
@Component
@Order(1)
public class UserManagerAspect {
    private static final Logger logger = LoggingUtils.getLogger(UserManagerAspect.class.getName());

    /**
     * Logs information for methods within the domain service layer.
     *
     * @param proceedingJoinPoint Provides context for the intercepted method call.
     * @return The result of the intercepted method's execution.
     * @throws Throwable If the intercepted method throws an exception.
     */
    @Around("execution(" + "* com.adl.et.telco.selfcare.usermanagement.domain.service..*(..))")
    public Object logDomainServiceInfo(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

        return logServiceData(proceedingJoinPoint);
    }

    /**
     * Logs information for methods within the external service layer.
     *
     * @param proceedingJoinPoint Provides context for the intercepted method call.
     * @return The result of the intercepted method's execution.
     * @throws Throwable If the intercepted method throws an exception.
     */
    @Around("execution(" + "* com.adl.et.telco.selfcare.usermanagement.external.service..*(..))")
    public Object logExternalServiceInfo(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

        return logServiceData(proceedingJoinPoint);
    }

    /**
     * Logs information for methods within the domain boundry layer.
     *
     * @param proceedingJoinPoint Provides context for the intercepted method call.
     * @return The result of the intercepted method's execution.
     * @throws Throwable If the intercepted method throws an exception.
     */
    @Around("execution(" + "* com.adl.et.telco.selfcare.usermanagement.domain.boundary..*(..))")
    public Object logDomainRepositoryInfo(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

        return logDBData(proceedingJoinPoint);
    }

    /**
     * Logs information for methods within the application controller layer.
     *
     * @param proceedingJoinPoint Provides context for the intercepted method call.
     * @return The result of the intercepted method's execution.
     * @throws Throwable If the intercepted method throws an exception.
     */
    @Around("execution(" + "* com.adl.et.telco.selfcare.usermanagement.application.controller..*(..))")
    public Object logApplicationControllerInfo(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

        return logControllerData(proceedingJoinPoint);
    }

    /**
     * Logs exceptions thrown from methods in the domain service layer.
     *
     * @param exception
     * @throws Throwable
     */
    @AfterThrowing(pointcut = "execution(" + "* com.adl.et.telco.selfcare.usermanagement.domain.service..*(..))", throwing = "exception")
    public void logExceptionInfo(Throwable exception) {

        logServiceException(exception);
    }

    /**
     * Logs details about an exception, including the method where the exception occurred and the status code.
     *
     * @param exception
     */
    private void logServiceException(Throwable exception) {
        String methodName = null;
        String statusCode = null;
        StackTraceElement[] stackTrace = exception.getStackTrace();
        if (stackTrace != null && stackTrace.length > 0) {
            // The first element of the stack trace array usually corresponds to the method where the exception occurred
            methodName = stackTrace[0].getMethodName();
        }
        if (exception instanceof DomainException) {
            DomainException e = (DomainException) exception;
            statusCode = e.getCode();
        }
        logger.error(EXCEPTION_HANDLER_LOG, MDC.get(FLOW_TYPE), methodName, statusCode, exception.getMessage());
    }


    /**
     * The common method used to log incoming and outgoing data of service methods.
     *
     * @param proceedingJoinPoint the joint-point of the aspect.
     * @return the result of the method.
     * @throws Throwable when the method throws an exception.
     */
    private Object logServiceData(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

        Object result = null;
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        long start = System.currentTimeMillis();
        Object[] signatureArgs = proceedingJoinPoint.getArgs();
        MethodSignature methodSignature = (MethodSignature) (proceedingJoinPoint.getSignature());
        String[] paramNames = methodSignature.getParameterNames();
        String argString = getArgumentString(true, paramNames, signatureArgs);
        String requestURI = request.getRequestURI();
        logger.info(REQUEST_RECEIVED_SERVICE, MDC.get(FLOW_TYPE), requestURI, proceedingJoinPoint.getSignature().getName(), argString);

        result = proceedingJoinPoint.proceed();
        String resultString = getResultString(result);
        long elapsedTime = System.currentTimeMillis() - start;
        logger.info(REQUEST_TERMINATED_SERVICE, MDC.get(FLOW_TYPE), requestURI, proceedingJoinPoint.getSignature().getName(), elapsedTime, "code", resultString);
        return result;
    }

    /**
     * Logs incoming and outgoing data for controller methods.
     *
     * @param proceedingJoinPoint
     * @return
     * @throws Throwable
     */
    private Object logControllerData(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object result = null;
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        long start = System.currentTimeMillis();
        Signature signature = proceedingJoinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        String methodName = methodSignature.getMethod().getName();

        Object[] signatureArgs = proceedingJoinPoint.getArgs();
        String[] paramNames = methodSignature.getParameterNames();
        String argString = getArgumentString(true, paramNames, signatureArgs);

        logger.info(REQUEST_RECEIVED_CONTROLLER, MDC.get(FLOW_TYPE), request.getRequestURI(), methodName, argString);

        result = proceedingJoinPoint.proceed();
        ResponseEntity<?> responseEntity = (ResponseEntity<?>) result;

        long elapsedTime = System.currentTimeMillis() - start;
        logger.info(REQUEST_COMPLETE_CONTROLLER, MDC.get(FLOW_TYPE), request.getRequestURI(), methodName, responseEntity.getStatusCode().value(), elapsedTime, responseEntity.getBody());
        return result;
    }

    /**
     * Logs database operation start and completion details.
     *
     * @param proceedingJoinPoint
     * @return
     * @throws Throwable
     */
    private Object logDBData(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object result = null;
        long start = System.currentTimeMillis();
        Object[] signatureArgs = proceedingJoinPoint.getArgs();
        MethodSignature methodSignature = (MethodSignature) (proceedingJoinPoint.getSignature());
        String[] paramNames = methodSignature.getParameterNames();

        String argString = getArgumentString(true, paramNames, signatureArgs);
        logger.debug(DB_OPERATION_START, MDC.get(FLOW_TYPE), proceedingJoinPoint.getSignature(), proceedingJoinPoint.getSignature().getName(), argString);
        result = proceedingJoinPoint.proceed();
        String resultString = getResultString(result);

        long elapsedTime = System.currentTimeMillis() - start;
        logger.debug(DB_OPERATION_COMPLETE, MDC.get(FLOW_TYPE), proceedingJoinPoint.getSignature(), proceedingJoinPoint.getSignature().getName(), elapsedTime, resultString);
        return result;
    }

    /**
     * Constructs a string representation of method arguments.
     *
     * @param isShowArgs    whether to show arguments in the log.
     * @param paramNames    the names of the method parameters.
     * @param signatureArgs the values of the method arguments.
     * @return a string representation of the method arguments.
     */
    private String getArgumentString(boolean isShowArgs, String[] paramNames, Object[] signatureArgs) {

        StringBuilder argString;
        if (isShowArgs) {
            argString = new StringBuilder();
            for (int i = 0; i < signatureArgs.length; i++) {
                argString.append(paramNames[i]).append(" - ").append((signatureArgs[i] == null) ? "null" : signatureArgs[i].toString()).append(" ");
            }
        } else {
            argString = new StringBuilder("Args hidden");
        }
        return argString.toString();
    }

    /**
     * Converts the given result object to its string representation.
     *
     * @param result
     * @return
     */
    private String getResultString(@Nullable Object result) {
        return (result == null) ? null : result.toString();
    }

}
