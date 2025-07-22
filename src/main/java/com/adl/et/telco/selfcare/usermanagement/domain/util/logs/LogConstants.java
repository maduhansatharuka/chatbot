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
package com.adl.et.telco.selfcare.usermanagement.domain.util.logs;

/**
 * This is the class for the log constants
 *
 * @author Hasini Hatharasinghe
 * @version 1.0
 * @since 2024/10/23
 */
public class LogConstants {
    public static final String REQUEST_RECEIVED_CONTROLLER = "{}: REQUEST_RECEIVED: {}: {}: {}";
    //<Microservice>: REQUEST_RECEIVED:<API>:<Method Name>:<Request Body>

    public static final String REQUEST_COMPLETE_CONTROLLER = "{}: REQUEST_TERMINATED: {}: {}: {}: {}ms: {}";
    //<Microservice>: REQUEST_TERMINATED:<API>:<Method Name>:<Response Code>:< Response Time>:<Response Body>

    public static final String DATA_IN_SVC = "{}: METHOD_START: {}: {}: METHOD_ARGUMENTS: {}";

    public static final String REQUEST_RECEIVED_SERVICE = "{}: METHOD_START: {}: {}: METHOD_ARGUMENTS: {}";
    //<Microservice>: METHOD_START:<API>:<Method Name>: METHOD_ARGUMENTS:<Method Arguments>

    public static final String DATA_OUT_SVC = "{}: METHOD_OUT: {}: {}: {}ms: {}: {}";

    public static final String REQUEST_TERMINATED_SERVICE = "{}: METHOD_OUT: {}: {}: {} {}: {}";
    //<Microservice>: METHOD_OUT:<API>:<Method Name>:<Response Time>:<Response Code>:<Response Body>

    public static final String ADDITIONAL_ERROR_SVC = "METHOD_OUT: {}";

    public static final String ADDITIONAL_INFO_SVC = "REQUEST_RECEIVED: {}: {}";

    public static final String DB_OPERATION_START = "{}: DB_REQUEST_INITIATED: {}: DB Function: {}: METHOD_ARGUMENTS: {}";
    //<Microservice>: DB_REQUEST_INITIATED:<Method Name>: DB Function:<JPA Method>: METHOD_ARGUMENTS:<Method Arguments>

    public static final String DB_OPERATION_COMPLETE = "{}: DB_REQUEST_TERMINATED: {}: DB Function: {}: {}ms: {}";
    //<Microservice>: DB_REQUEST_TERMINATED:<Method Name>: DB Function:<JPA Method>:<Response Time>:<DB Response>

    public static final String EXCEPTION_HANDLER_LOG = "{}: METHOD_IN:{}:ERROR_HANDLER:{}:{}";
    //<Microservice>:METHOD_IN:<Method Name>:ERROR_HANDLER:<Error Code>:<Error Message>

    public static final String METHOD_OUT_LOG  = "{}: METHOD_OUT: {}";
    public static final String ERROR_CONTINUE = " METHOD_IN:{}:{}:{}ms :ERROR OCCURRED:{}:{}";
    public static final String SUCCESS = "SUCCESS";
    public static final String SCHEDULER = "SCHEDULER";
    public static final String API = "API";

    /* MDC constants. */
    public static final String FLOW_TYPE = "OP_FLOW";
    public static final String MS_NAME = "MS_NAME";

}
