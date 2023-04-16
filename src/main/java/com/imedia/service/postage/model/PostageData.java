package com.imedia.service.postage.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostageData {
    private String code;
    private String name;
    private Integer status;
    private Integer type;
    private String time;
    private Integer settingId;
    private Integer max;
    private Integer baseType;

    public PostageData(String code, String name, Integer status, Integer type, String time) {
        this.code = code;
        this.name = name;
        this.status = status;
        this.type = type;
        this.time = time;
    }

    public PostageData(String code, String name, Integer status, Integer type, String time, Integer settingId, Integer max, Integer baseType) {
        this.code = code;
        this.name = name;
        this.status = status;
        this.type = type;
        this.time = time;
        this.settingId = settingId;
        this.max = max;
        this.baseType = baseType;
    }
}
