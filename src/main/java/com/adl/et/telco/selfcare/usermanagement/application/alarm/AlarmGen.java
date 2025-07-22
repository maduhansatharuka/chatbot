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
package com.adl.et.telco.selfcare.usermanagement.application.alarm;

import com.adl.et.telco.dte.plugin.alarming.dto.AlarmDef;
import com.adl.et.telco.dte.plugin.alarming.services.AlarmingUtils;
import org.springframework.stereotype.Service;

/**
 * This is the class which is used to generate alarms.
 *
 * @author Hasini Hatharasinghe
 * @version 1.0
 * @since 2024/10/18
 */
@Service
public class AlarmGen {

    private final AlarmingUtils alarm;

    /**
     * Constructor to initialize AlarmGen with AlarmingUtils.
     *
     * @param alarm The alarm utility used to generate and trigger alarms.
     */
    public AlarmGen(AlarmingUtils alarm) {
        this.alarm = alarm;
    }

    /**
     * Triggers an alarm with the given message type and message.
     *
     * @param msgType The type of message for the alarm.
     * @param msg     The alarm message.
     */
    public void alert(AlarmDef.MessageType msgType, String msg) {
        alarm.alert(alarm.createAlarm(msgType, msg));
    }

}
