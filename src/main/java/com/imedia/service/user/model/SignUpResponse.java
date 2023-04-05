package com.imedia.service.user.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.imedia.model.BaseRequest;
import com.imedia.oracle.entity.ErrorCodesWebshop;
import com.imedia.util.PreLoadStaticUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Getter
@Setter
public class SignUpResponse extends BaseRequest {
    private Integer status;
    private String message;
    private Integer code;
    @JsonIgnore
    private static final HashMap<Integer, ErrorCodesWebshop> errorCodes = PreLoadStaticUtil.errorCodeWeb;
    @JsonIgnore
    static final List<Integer> successCodes = Arrays.asList(200, 600, 601, 609, 700, 707, 720, 750, 760, 770, 780, 790, 2000);

    public SignUpResponse(Integer status, String message) {
        this.status = status;
        this.message = message;
        if (successCodes.contains(status))
            this.code = 200;
        else
            this.code = status;
    }

}
