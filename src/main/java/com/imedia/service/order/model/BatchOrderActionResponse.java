package com.imedia.service.order.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BatchOrderActionResponse {
    int totalRequest = 0;
    int totalFailed = 0;
    int totalSuccess = 0;
}
