package com.imedia.service.authenticate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.imedia.oracle.entity.ErrorCodesWebshop;
import com.imedia.util.PreLoadStaticUtil;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Getter
@Setter
public class SignInResponse implements Serializable {

    private static final long serialVersionUID = -8091879091924046844L;
    private String token = "";
    private String sessionKey = "";
    private String message;
    private Integer status;
    private Integer code;
    private String username;
    private Object userInfo;
    private Object data;
    @JsonIgnore
    private static final HashMap<Integer, ErrorCodesWebshop> errorCodes = PreLoadStaticUtil.errorCodeWeb;
    @JsonIgnore
    static final List<Integer> successCodes = Arrays.asList(200, 600, 601, 609, 700, 707, 720, 750, 760, 770, 780, 790, 2000);


    public SignInResponse(String message, Integer status) {
        this.message = message;
        this.status = status;
        if (successCodes.contains(status))
            this.code = 200;
        else
            this.code = status;
    }

}