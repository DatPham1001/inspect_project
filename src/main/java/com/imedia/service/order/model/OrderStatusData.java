package com.imedia.service.order.model;

import com.imedia.config.application.AppConfig;
import com.imedia.service.order.enums.OrderStatusNameEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class OrderStatusData {
    private int code;
    private String name;
    private String note;
    private int groupStatus;

    public OrderStatusData(int code) {
        this.code = code;
        this.name = OrderStatusNameEnum.valueOf(code).message;
        this.groupStatus = getGroupStatus(code);

    }

    private int getGroupStatus(int code) {
        try {
            HashMap<Integer, String> statuses = AppConfig.getInstance().targetStatuses;
            for (Map.Entry<Integer, String> groupStatus : statuses.entrySet()) {
                String value = groupStatus.getValue();
                if (Arrays.asList(value.split(",")).contains(String.valueOf(code)))
                    return groupStatus.getKey();
            }
        } catch (Exception e) {

        }
        return 100;
    }

    public static void main(String[] args) throws Exception {
        HashMap<Integer, String> statuses = AppConfig.getInstance().targetStatuses;
        int a = 2009;
        for (Map.Entry<Integer, String> groupStatus : statuses.entrySet()) {
            String value = groupStatus.getValue();
            if (value.contains(String.valueOf(a)))
                System.out.println(groupStatus.getKey());
        }
    }
}
