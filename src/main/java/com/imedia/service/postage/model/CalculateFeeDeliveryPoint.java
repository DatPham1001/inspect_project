package com.imedia.service.postage.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CalculateFeeDeliveryPoint implements Serializable {
    private static final long serialVersionUID = 1L;

    private String address;
    private String province;
    private String district;
    private String ward;
    private List<CalculateFeeReceivers> receivers;
}
