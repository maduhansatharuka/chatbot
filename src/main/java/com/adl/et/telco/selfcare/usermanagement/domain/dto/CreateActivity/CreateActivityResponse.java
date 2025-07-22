package com.adl.et.telco.selfcare.usermanagement.domain.dto.CreateActivity;

import com.adl.et.telco.selfcare.usermanagement.domain.dto.ResponseHeader;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateActivityResponse {

    private ResponseHeader responseHeader;

    private CreateActivityResponseBody responseBody;
}
