package com.imedia.service.user.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.imedia.model.BaseRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class VerifyOTPResponse extends BaseRequest {
    private Integer status;
    private Integer code;
    private String message;
    private String phoneReceivedOTP;
    @JsonIgnore
    static final List<Integer> successCodes = Arrays.asList(200, 600, 601, 609, 700, 707, 720, 750, 760, 770, 780, 790, 2000);

    public VerifyOTPResponse(Integer status, String message, String phoneReceivedOTP) {
        this.status = status;
        this.message = message;
        this.phoneReceivedOTP = phoneReceivedOTP;
        if (successCodes.contains(status))
            this.code = 200;
        else
            this.code = status;
    }
}
