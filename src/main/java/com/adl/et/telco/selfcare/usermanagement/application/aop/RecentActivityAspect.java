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
import com.adl.et.telco.selfcare.usermanagement.application.dto.RequestHeader;
import com.adl.et.telco.selfcare.usermanagement.domain.exception.DomainException;
import com.adl.et.telco.selfcare.usermanagement.domain.service.EnterpriseActivityService;
import com.adl.et.telco.selfcare.usermanagement.domain.util.Status;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.jboss.logging.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.adl.et.telco.selfcare.usermanagement.application.util.Constant.CAPTURE_ADMIN_ACTIVITY_ERROR_IN_SERVER_EXCEPTION_OCCURRED;
import static com.adl.et.telco.selfcare.usermanagement.domain.util.logs.LogConstants.FLOW_TYPE;
import static com.adl.et.telco.selfcare.usermanagement.domain.util.logs.LogConstants.METHOD_OUT_LOG;

/**
 * This is the RecentActivityAspect class.
 *
 * @author Maduhansa Tharuka
 * @version 1.0
 * @since 2025.03.10
 */
@Aspect
@Component
public class RecentActivityAspect {

    private static final Logger logger = LoggingUtils.getLogger(RecentActivityAspect.class.getName());
    @Autowired
    private EnterpriseActivityService enterpriseActivityService;
    @Autowired
    private HttpServletRequest request;

    @Around("execution(* com.adl.et.telco.selfcare.configurationmanager.application.controller..*(..))")
    public Object logAroundApiCall(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = null;
        Status status = Status.SUCCESS;
        String details = null;
        try {
            result = joinPoint.proceed();
        } catch (Exception e) {
            status = Status.FAILED;
            logActivity(joinPoint, status, result);
            throw e;
        }
        try {
            logActivity(joinPoint, status, result);
        }
        catch(Exception e){
            logger.error(METHOD_OUT_LOG, MDC.get(FLOW_TYPE), CAPTURE_ADMIN_ACTIVITY_ERROR_IN_SERVER_EXCEPTION_OCCURRED, e);
        }
        return result;
    }

    private void logActivity(ProceedingJoinPoint joinPoint, Status status, Object result) throws DomainException {
        try {
            RequestHeader requestHeader;
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            TrackActivity trackActivity = method.getAnnotation(TrackActivity.class);

            if (trackActivity != null) {
                String activityName = trackActivity.value();
                String detailTemplate = trackActivity.detail();
                String[] argNames = trackActivity.args().split(",");
                String currentUserName = getUserIdFromArgs(joinPoint.getArgs());
                String transactionId = request.getHeader("transactionId");

                // Replace placeholders in detail template with actual values
                Object[] args = joinPoint.getArgs();
                String details = detailTemplate;

                for (Object arg : args) {
                    if (arg != null) {
                        details = replacePlaceholders(details, arg, argNames);
                    }
                }

                // Replace placeholders with status values
                details = details.replace("{status}", status.toString().toLowerCase());

                // Replace placeholders with transactionId
                if (transactionId != null) {
                    details = details.replace("{transactionId}", transactionId);
                }

                // Replace placeholders with response body values
                if (result != null && result instanceof ResponseEntity) {
                    ResponseEntity<?> responseEntity = (ResponseEntity<?>) result;
                    Map<String, Object> map = (Map<String, Object>) responseEntity.getBody();
                    if (responseEntity.getBody() != null) {
                        details = replacePlaceholders(details, map.get("responseBody"), argNames);

                    }
                }

                Pattern pattern = Pattern.compile("\\{[^{}]+\\}");
                Matcher matcher = pattern.matcher(details);
                while (matcher.find()) {
                    String placeholder = matcher.group();
                    String argName = placeholder.substring(1, placeholder.length() - 1).trim();
                    details = details.replaceAll("([^,]+\\s*:\\s*\\{" + argName + "\\},?\\s*)", "");
                }

                enterpriseActivityService.saveActivity(activityName, currentUserName, status, details, LocalDateTime.now());
            }
        }
        catch (Exception e){
            logger.error(METHOD_OUT_LOG, MDC.get(FLOW_TYPE), CAPTURE_ADMIN_ACTIVITY_ERROR_IN_SERVER_EXCEPTION_OCCURRED, e);
        }
    }

    private String getUserIdFromArgs(Object[] args) throws NoSuchFieldException, IllegalAccessException {
        for (Object arg : args) {
            Field field = arg.getClass().getDeclaredField("requestHeader");
            field.setAccessible(true);
            Object nestedArg = field.get(arg);
            if (nestedArg != null) {
                Field nestedField = nestedArg.getClass().getDeclaredField("requestHeader");
                nestedField.setAccessible(true);
                Object value = nestedField.get(nestedArg);
                if (value != null) {
                    return value.toString();
                } else {
                    return null;
                }
            }
        }
        return null;
    }

    private String replacePlaceholders(String template, Object arg, String[] argNames) {
        Map<String, Object> argMap = new HashMap<>();
        for (String argName : argNames) {
            argName = argName.trim();
            try {
                if (argName.contains(".")) {
                    String[] parts = argName.split("\\.");
                    Field field = arg.getClass().getDeclaredField(parts[0]);
                    field.setAccessible(true);
                    Object nestedArg = field.get(arg);
                    if (nestedArg != null) {
                        Field nestedField = nestedArg.getClass().getDeclaredField(parts[1]);
                        nestedField.setAccessible(true);
                        Object value = nestedField.get(nestedArg);
                        if (value != null) {
                            argMap.put(argName, value.toString());
                        } else {
                            argMap.put(argName, null);
                        }
                    }
                } else {
                    Field field = arg.getClass().getDeclaredField(argName);
                    field.setAccessible(true);
                    Object value = field.get(arg);
                    if (value != null) {
                        argMap.put(argName, value.toString());
                    } else {
                        argMap.put(argName, null);
                    }
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                // Handle the exception as needed
            }
        }

        // Replace the remaining placeholders with actual values
        for (String argName : argNames) {
            argName = argName.trim();
            if (argMap.containsKey(argName) && argMap.get(argName) != null) {
                template = template.replace("{" + argName + "}", argMap.get(argName).toString());
            }
        }

        return template;
    }
}
