package com.imedia.service.order.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SumOrderStatusDTO {
    private Integer status;
    private Integer total;
}
