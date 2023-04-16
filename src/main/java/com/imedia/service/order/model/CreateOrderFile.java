package com.imedia.service.order.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateOrderFile {
    private Integer index;
    private Integer shopID;
    private String shopOrderId;
    private String userPhone;
    private Integer pickupAddressID;
    private String packageCode;
    private String packName;
    //1 người gửi trả phí 2 là người nhận trả phí
    private Integer orderPayment;
    //1 là tiền mặt 2 ví hola
    private Integer paymentType;
    private Integer pickupType;
    private Integer weight;
    private Integer length;
    private Integer width;
    private Integer height;
    //ghi chu bat buoc - 1: cho thu hang, 2: cho xem hang, 3: ko cho xem hang
    private Integer requireNote;
    // 0 la khong 1 la co
    private Integer partialDelivery;
    private String note;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private String receiverProvinceCode;
    private String receiverDistrictCode;
    private String receiverWardCode;
    private String discountCoupon;
    //1: co chuyen hoan, 0: ko chuyen hoan
    private Integer isReturn;
    //1 co 0 khong
    private Integer isPorter;
    //1 co 0 khong
    private Integer isDoorDeliver;
    private List<CreateOrderFileProduct> products;
    private List<CreateOrderFileFee> fees;
    private String feeReason;


//    private String shopOrderId;
//    [
//    {
//        "index": "1",
//            "shopID": "",
//            "pickupAddressID": "",
//            "packageCode": "ALL",
//            "orderPayment": "1",
//            "paymentType": "1",
//            "pickupType": "1",
//            "weight": "3999",
//            "length": "30",
//            "width": "10",
//            "height": "20",
//            "requireNote": "1",
//            "partialDelivery": "0",
//            "note": "",
//            "receiverName": "Nguyễn Hải Tiến",
//            "receiverPhone": "0966894521",
//            "receiverAddress": "Số 011 Hoàng Đình Giong P Hợp giang Cao Bằng",
//            "receiverProvinceCode": "",
//            "receiverDistrictCode": "",
//            "receiverWardCode": "",
//            "shopOrderId": "LEXRT0001073780VNA",
//            "discountCoupon": "HLS20K1",
//            "isReturn": "",
//            "isPorter": 1,
//            "isDoorDeliver": 1,
//            "products": [
//        {
//            "productName": "Quần áo trẻ em",
//                "productQuantity": "",
//                "productCOD": 0,
//                "productValue": "2000000"
//        },
//        {
//            "productName": "Quần áo trẻ em",
//                "productQuantity": "",
//                "productCOD": 0,
//                "productValue": "2000000"
//        }
//    ]
//    }
//]
}
