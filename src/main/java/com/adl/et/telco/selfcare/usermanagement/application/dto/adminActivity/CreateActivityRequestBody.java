package com.adl.et.telco.selfcare.usermanagement.application.dto.adminActivity;

import com.adl.et.telco.selfcare.usermanagement.application.util.ActivityStatus;
import com.adl.et.telco.selfcare.usermanagement.domain.util.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateActivityRequestBody {

    private String username;

    private String activity;

    private String details;

    private Status status;

    private LocalDateTime activityDate;
}
