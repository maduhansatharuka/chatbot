package com.adl.et.telco.selfcare.usermanagement.application.util;

import ch.qos.logback.classic.Logger;
import com.adl.et.telco.dte.plugin.logging.services.LoggingUtils;

import static com.adl.et.telco.selfcare.usermanagement.domain.util.logs.LogConstants.ADDITIONAL_ERROR_SVC;
import static com.adl.et.telco.selfcare.usermanagement.domain.util.logs.LogConstants.ADDITIONAL_INFO_SVC;

/**
 * This class is used to log errors
 *
 * @author Hasini Hatharasinghe
 * @version 1.0
 * @since 2024/12/20
 */
public class HandleErrorLogs {
    private static final Logger logger = LoggingUtils.getLogger(HandleErrorLogs.class.getName());

    public static void logError(String methodName, Throwable stackTrace) {
        logger.error(ADDITIONAL_ERROR_SVC, methodName, stackTrace);
    }

    public static void logInfo(String message) {
        logger.debug(ADDITIONAL_INFO_SVC, Thread.currentThread().getStackTrace()[1].getMethodName(), message);
    }
}