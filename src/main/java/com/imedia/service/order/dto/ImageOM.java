package com.imedia.service.order.dto;

import org.springframework.stereotype.Component;

@Component
public interface ImageOM {
    Integer getId();

    String getPath();

    Long getImgId();

    String getFileName();

}
