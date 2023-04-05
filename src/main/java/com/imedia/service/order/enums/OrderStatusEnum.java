package com.imedia.service.order.enums;

public enum OrderStatusEnum {
    DEFAULT(1, ""),
    //Chờ duyệt
    PENDING(900, "Đơn hàng chờ xử lý"),
    WAIT_TO_CONFIRM(900, "Đơn hàng chờ duyệt"),
    UPDATE_ORDER(999, "Sửa thông tin tạo đơn"),

    FINDING_SHIP(1001, "Đơn hàng đang tìm tài xế"),
    //Chờ lấy
    ACCEPTED(1002, ""),
    //    ACCEPTED(1002, "Đã tiếp nhận"),
    WAIT_TO_PICK(100, "Gán đơn cho bưu tá đi lấy"),
    WAIT_TO_BRING(103, ""),
    //    WAIT_TO_BRING(103, "Chờ người gửi mang hàng ra bưu cục"),
    PICKING(104, "Bưu tá đang lấy hàng"),
    PUSH_SUCCESS(906, ""),
    //    PUSH_SUCCESS(906, "Vận đơn đã đẩy sang đối tác thành công"),
    CONFIRMED(909, "Đơn hàng đã duyệt"),

    //Lấy thất bại
    PICK_FAILED(102, "Bưu tá lấy hàng thất bại"),
    PICK_FAILED_RE_APPOINT(1021, "Bưu tá lấy hàng thất bại"),
    PICK_FAILED_NOT_DONE(1022, "Bưu tá lấy hàng thất bại"),
    PICK_FAILED_CANCELED(1023, "Bưu tá lấy hàng thất bại"),
    PICK_FAILED_OTHER(1025, "Bưu tá lấy hàng thất bại"),
    PICK_FAILED_ODD(1026, "Bưu tá lấy hàng thất bại"),
    PICK_FAILED_CALL_FAIL(1029, "Bưu tá lấy hàng thất bại"),
    //DELAY
    DELAY_PICK(1028, "Delay lấy hàng"),
    DELAY_DELIVERY(5111, "Delay giao hàng"),
    DELAY_RETURN(50216, "Delay hoàn hàng"),
    //Lưu kho
    STORED(200, "Lưu kho"),
    SORTING(300, "Đơn hàng đang được phân loại"),
    PICKED(105, "Lấy thành công"),
    STORED_DESTRUCTION(2001, "Lưu kho - chờ tiêu hủy"),
    STORED_REDELIVERY(2002, "Lưu kho - chờ giao lại"),
    STORED_HDO(2005, "Lưu kho - chờ xác nhận bàn giao"),
    STORED_HDO2(20051, "Lưu kho - chờ xác nhận bàn giao"),
    STORED_HDO3(20052, "Lưu kho - chờ xác nhận bàn giao"),
    CLOSED_TRANSPORT(301, "Đơn hàng đang được đóng kiện luân chuyển"),
    TRANSPORTING(303, "Đơn hàng đang được luân chuyển"),
    PACK_ARRIVED(400, ""),
    PACK_EQUAL(401, ""),
    PACK_LESS(402, ""),
    PACK_MORE(403, ""),
    OVER_WEIGHT(921, ""),
    TRANSPORTING_RETURN(3031, "Đơn hàng đang được luân chuyển - hoàn"),
    STORED_DELAY_DEL(2011, ""),
    STORED_DELAY_RET(2012, ""),
    //Đã hoàn
    RETURNED(504, "Bưu tá hoàn hàng thành công"),
    //Đang giao
    DELIVERING(500, "Bưu tá nhận hàng đi giao"),
    DELIVERING_LAST(904, "Bưu tá nhận hàng đi giao lại lần cuối"),
    RE_DELIVERING(908, "Shop yêu cầu giao lại"),
    PUSH_CONFIRM(910, ""),
    WTC_OW_ORDER(922, ""),
    //Giao thất bại
    DELIVERY_FAILED(511, "Bưu tá giao hàng thất bại"),
    DF_DENIED(5112, "Bưu tá giao hàng thất bại"),
    DF_RE_APPOINT(5113, "Bưu tá giao hàng thất bại"),
    DF_NOT_CALL(5114, "Bưu tá giao hàng thất bại"),
    DF_CALL_FAILED(5115, "Bưu tá giao hàng thất bại"),
    DF_SHIP_DECLINE(5116, "Bưu tá giao hàng thất bại"),
    //Chờ hoàn
    RETURN_WAIT(505, "Chờ hoàn"),
    RETURN_STORED(2003, "Lưu kho - chờ hoàn"),
    RETURN_REQ(907, "Chuyển hoàn tự động"),
    //Giao thành công
    DELIVERED(501, ""),
    //Đang hoàn
    RETURNING(502, "Bưu tá nhận hàng đi hoàn"),
    RETURNING_STORED(2004, ""),
    //Chờ hủy
    CANCEL_WAIT(901, ""),

    //Có vấn đề
    PROBLEM(2006, ""),
    BANNED(20061, ""),
    LOST(20062, ""),
    DAMAGED(20063, ""),
    PACK_BANNED(20064, ""),
    PACK_LOST(20065, ""),
    DESTROYED(2007, ""),
    REFUNDED(2008, ""),
    OUT_OF_PROCESS(2009, ""),
    //Hoàn thất bại
    WAIT_DESTRUCTION(905, "Chờ tiêu hủy"),
    RETURN_FAILED(5021, "Bưu tá hoàn hàng thất bại"),
    RF_CALL_FAILED(50213, ""),
    RF_SHIPPER_DECLINED(50214, ""),
    RF_RE_APPOINT(50212, ""),
    RF_RECEIVER_DECLINED(50211, ""),
    RF_CALL_RE_FAILED(50215, ""),
    //Hủy
    CANCEL_CARRIER(107, "Đơn vị vận chuyển hủy đơn hàng"),
    CANCEL_AUTO(903, "Vận đơn bị hủy tự động"),
    CANCEL_ADMIN(902, "Vận đơn hủy bởi quản trị viên"),
    CANCEL_SHOP(924, "Shop yêu cầu hủy đơn hàng"),
    //Sai can nang
    WEIGHT_WRONG(923, "Người gửi xác nhận chuyển hoàn khi đơn sai cân nặng - kích thước"),
    PUSH_FAILED(1003, "Tạo đơn thất bại"),


    NOTE_CREATED(909, "Tạo đơn hàng thành công"),
    NOTE_PUSH_GW_FAILED(900, "Tạo đơn thất bại"),
    NOTE_PUSH_GW_FAILED2(900, "Tạo đơn thất bại"),
    NOTE_HOLD_FAILED(900, "Đơn hàng duyệt thất bại do không đủ số dư"),
    NOTE_NOTFOUND_SHIPPER(900, "Không tìm thấy bưu tá"),
    PRE_CONFIRM_FAIL(900, "Đơn hàng duyệt thất bại do gói cước không áp dụng với"),
    PRE_UPDATED_CF_FAIL(900, "Cập nhật đơn hàng thành công,duyệt thất bại do gói cước không áp dụng với"),
    PRE_FEE_FAIL(900, "Gói cước không áp dụng với"),
    NOTE_ORDER_PAYMENT(900, " người nhận trả phí"),
    NOTE_ORDER_PAYMENT2(900, "Gói cước không áp dụng với người nhận trả phí"),
    NOTE_PAYMENT_TYPE(900, " thanh toán tiền mặt"),
    NOTE_PAYMENT_TYPE2(900, "Gói cước không áp dụng với thanh toán tiền mặt"),
    NOTE_PARTIAL_TYPE(900, " giao hàng một phần"),
    NOTE_PARTIAL_TYPE2(900, "Gói cước không áp dụng với giao hàng một phần"),
    NOTE_COD_CONFIRM(1, "Cập nhật COD thành công"),
    NOTE_COD_REJECT(1, "Cập nhật COD thất bại do bưu tá từ chối"),
    NOTE_REQ_REDELIVERY(1, "Shop yêu cầu giao lại đơn hàng"),
    NOTE_PARTIAL_DELIVERED(501, "Xác nhận giao hàng 1 phần thành công"),
    NOTE_PARTIAL_RETURNING(907, "Chuyển hoàn 1 phần đơn hàng"),
    NOTE_PARTIAL_REJECT(1, "Từ chối giao hàng 1 phần"),
    NOTE_PARTIAL_REJECT_AUTO(1, "Shop không phản hồi yêu cầu giao hàng 1 phần"),
    NOTE_PARTIAL_DESTROYED(1, " Do đơn giao 1 phần không chọn chuyển hoàn"),
    NOTE_AUTO_STORED(1, "Nhập kho chờ giao lại tự động"),

    NOTE_UPDATE_ORDER_INFO(1, "Shop cập nhật thông tin đơn hàng"),
    NOTE_UPDATE_ORDER_COD(1, "Shop cập nhật COD "),
    NOTE_UPDATE_ORDER_WEIGHT(1, "Shop cập nhật cân nặng "),
    NOTE_UPDATE_ORDER_WEIGHT2(1, "Shop cập nhật kích thước "),
    NOTE_UPDATE_ORDER_RECEIVER(1, "Shop cập nhật thông tin người nhận"),
    NOTE_UPDATE_ORDER_RECEIVER_ADD(1, "Shop cập nhật thông tin địa chỉ người nhận"),
    NOTE_RETURN_REQ(1,"Shop yêu cầu chuyển hoàn đơn hàng"),
    NOTE_STORED_RETURN_REQ(1,"Nhập kho hoàn hàng tự động"),
    ;
    public final Integer code;
    public final String message;

    OrderStatusEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static OrderStatusEnum valueOf(int value) {
        for (OrderStatusEnum val : values()) {
            if (val.code == value)
                return val;
        }
        return OrderStatusEnum.DEFAULT;
    }

}
