package com.imedia.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.imedia.config.application.AppConfig;
import com.imedia.oracle.entity.ErrorCodesWebshop;
import com.imedia.util.PreLoadStaticUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Getter
@Setter

public class BaseResponse {
    private Integer status;
    private Integer code;
    private String message;
    private String requestId;
    private Object data;
    @JsonIgnore
    private static final HashMap<Integer, ErrorCodesWebshop> errorCodes = PreLoadStaticUtil.errorCodeWeb;
    @JsonIgnore
    static final List<Integer> successCodes = Arrays.asList(99,200, 600, 601, 612, 609, 700, 707, 720, 750, 760, 770, 780, 790, 2000);

    public BaseResponse(Integer status, String message, Object data) {
        ErrorCodesWebshop errorCodesWebshop = errorCodes.get(status);
        this.status = status;
        if (successCodes.contains(status))
            this.code = 200;
        else
            this.code = errorCodesWebshop.getErrorCode();
        this.message = message;
        this.data = data;
    }

    public BaseResponse(Integer status, Object data) {
        ErrorCodesWebshop errorCodesWebshop = errorCodes.get(status);
        this.status = errorCodesWebshop.getErrorCode();
        if (successCodes.contains(status))
            this.code = 200;
        else
            this.code = errorCodesWebshop.getErrorCode();
        this.message = errorCodesWebshop.getMessage();
        this.data = data;
    }

    public BaseResponse(Integer status, String message, Integer type) {
        if (type == 1) {
            ErrorCodesWebshop errorCodesWebshop = errorCodes.get(status);
            this.status = errorCodesWebshop.getErrorCode();
            this.message = message;
            if (successCodes.contains(status))
                this.code = 200;
            else this.code = errorCodesWebshop.getErrorCode();
        }
        if (type == 2) {
            ErrorCodesWebshop errorCodesWebshop = errorCodes.get(status);
            this.status = errorCodesWebshop.getErrorCode();
            this.message = errorCodesWebshop.getMessage().replace("[x]", message);
            if (successCodes.contains(status))
                this.code = 200;
            else this.code = errorCodesWebshop.getErrorCode();
        }
        if (type == 3) {
            ErrorCodesWebshop errorCodesWebshop = errorCodes.get(status);
            this.status = status;
            this.message = message;
            if (successCodes.contains(status))
                this.code = 200;
            else this.code = status;
        }
    }

    public BaseResponse(Integer status) {
        ErrorCodesWebshop errorCodesWebshop = errorCodes.get(status);
        this.status = errorCodesWebshop.getErrorCode();
        if (successCodes.contains(status))
            this.code = 200;
        else this.code = errorCodesWebshop.getErrorCode();
        if (status == 516) {
            try {
                this.message = errorCodesWebshop.getMessage() + ":" + AppConfig.getInstance().minWithdraw;
            } catch (Exception e) {
                this.message = errorCodesWebshop.getMessage();
            }
        } else if (status == 515) {
            try {
                this.message = errorCodesWebshop.getMessage() + ":" + AppConfig.getInstance().maxWithdraw;
            } catch (Exception e) {
                this.message = errorCodesWebshop.getMessage();
            }
        } else this.message = errorCodesWebshop.getMessage();
    }
}
