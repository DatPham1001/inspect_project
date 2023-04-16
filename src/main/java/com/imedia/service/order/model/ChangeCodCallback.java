package com.imedia.service.order.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeCodCallback {
    private String message;
    private Integer code;
    private ChangeCodCallbackData data;
}
