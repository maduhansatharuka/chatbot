package com.adl.et.telco.selfcare.usermanagement.application.dto.adminActivity;

import com.adl.et.telco.selfcare.usermanagement.application.dto.RequestHeader;
import com.adl.et.telco.selfcare.usermanagement.application.validator.RequestEntityInterface;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateActivityRequest implements RequestEntityInterface {

    private RequestHeader requestHeader;

    private CreateActivityRequestBody requestBody;
}
