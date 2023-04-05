package com.imedia.service.order.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FilterOrderShipperData {
    private String name;
    private String phone;

    public FilterOrderShipperData() {
    }

    public FilterOrderShipperData(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }
}
