package com.imedia.service.gateway.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Hls1UpdateResponse {
    private Integer changeStatus;
    private String message;
    private Hls1FeeData updateFee;
}
