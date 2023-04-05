package com.imedia.service.postage.model;

import org.springframework.stereotype.Component;

@Component
public interface FeeSettingInfoOM {
    Integer getFeeSettingId();

    String getName();

    Integer getType();

    Integer getMin();

    Integer getMax();
}
