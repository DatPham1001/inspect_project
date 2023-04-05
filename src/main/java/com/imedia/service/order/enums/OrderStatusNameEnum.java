package com.imedia.service.order.enums;

public enum OrderStatusNameEnum {
    //Chờ duyệt
    WAIT_TO_CONFIRM(900, "Đơn hàng chờ duyệt"),
    UPDATE_ORDER(999, "Sửa thông tin tạo đơn"),
    //DELAY
    DELAY_PICK(1028, "Delay lấy hàng"),
    DELAY_DELIVERY(5111, "Delay giao hàng"),
    DELAY_RETURN(50216, "Delay hoàn hàng"),
    FINDING_SHIP(1001, "Tìm tài xế"),
    //Chờ lấy
    ACCEPTED(1002, "Đã tiếp nhận"),
    WAIT_TO_PICK(100, "Chờ lấy hàng"),
    WAIT_TO_BRING(103, "Chờ người gửi mang hàng ra bưu cục"),
    PICKING(104, "Đang lấy hàng"),
    PUSH_SUCCESS(906, "Vận đơn đã đẩy sang đối tác thành công"),
    CONFIRMED(909, "Vận đơn đã duyệt"),

    //Lấy thất bại
    PICK_FAILED(102, "Lấy hàng thất bại"),
    PICK_FAILED_RE_APPOINT(1021, "Lấy thất bại - người gửi hẹn lại ngày lấy"),
    PICK_FAILED_NOT_DONE(1022, "Lấy thất bại - người gửi chưa chuẩn bị xong hàng"),
    PICK_FAILED_CANCELED(1023, "Lấy thất bại - người gửi hủy đơn"),
    PICK_FAILED_OTHER(1025, "Lấy thất bại - nguyên nhân khác"),
    PICK_FAILED_ODD(1026, "Lấy thất bại - hàng hóa ngoài danh mục vận chuyển"),
    PICK_FAILED_CALL_FAIL(1029, "Lấy thất bại - không liên hệ được người gửi"),

    //Lưu kho
    STORED(200, "Lưu kho"),
    SORTING(300, "Đang phân loại"),
    PICKED(105, "Lấy thành công"),
    STORED_DESTRUCTION(2001, "Lưu kho - chờ tiêu hủy"),
    STORED_REDELIVERY(2002, "Lưu kho - chờ giao lại"),
    STORED_HDO(2005, "Lưu kho - chờ xác nhận bàn giao"),
    STORED_HDO2(20051, "Chờ xác nhận bàn giao - Đi giao"),
    STORED_HDO3(20052, "Chờ xác nhận bàn giao - Đi hoàn"),
    CLOSED_TRANSPORT(301, "Đóng kiện luân chuyển"),
    TRANSPORTING(303, "Đang luân chuyển"),
    PACK_ARRIVED(400, "Nhận kiện đến"),
    PACK_EQUAL(401, "Nhận kiện - đủ"),
    PACK_LESS(402, "Nhận kiện - thiếu"),
    PACK_MORE(403, "Nhận kiện - dư"),
    OVER_WEIGHT(921, "Hàng vượt cân"),
    TRANSPORTING_RETURN(3031, "Đang luân chuyển - hoàn"),
    STORED_DELAY_DEL(2011, "Lưu kho - Delay giao"),
    STORED_DELAY_RET(2012, "Lưu kho - Delay trả"),
    //Đã hoàn
    RETURNED(504, "Hoàn thành công"),
    //Đang giao
    DELIVERING(500, "Đang giao"),
    DELIVERING_LAST(904, "Đang giao lại lần cuối"),
    RE_DELIVERING(908, "Yêu cầu giao lại"),
    PUSH_CONFIRM(910, "Đẩy vào bảng order confirm"),
    WTC_OW_ORDER(922, "Người gửi xác nhận giao tiếp khi đơn sai cân nặng - kích thước"),
    //Giao thất bại
    DELIVERY_FAILED(511, "Giao thất bại"),
    DF_DENIED(5112, "Giao thất bại - người nhận từ chối nhận hàng"),
    DF_RE_APPOINT(5113, "Giao thất bại - người nhận hẹn lại ngày giao"),
    DF_NOT_CALL(5114, "Chưa liên hệ người nhận - nhập lại kho"),
    DF_CALL_FAILED(5115, "Giao thất bại - không liên hệ được người nhận"),
    DF_SHIP_DECLINE(5116, "Giao hàng thất bại - shipper hủy đơn"),
    //Chờ hoàn
    RETURN_WAIT(505, "Chờ hoàn"),
    RETURN_STORED(2003, "Lưu kho - chờ hoàn"),
    RETURN_REQ(907, "Chuyển hoàn tự động"),
    //Giao thành công
    DELIVERED(501, "Giao thành công"),
    //Đang hoàn
    RETURNING(502, "Đang hoàn"),
    RETURNING_STORED(2004, "Lưu kho - đang hoàn"),
    //Chờ hủy
    CANCEL_WAIT(901, "Chờ hủy"),

    //Có vấn đề
    PROBLEM(2006, "Đơn có vấn đề"),
    BANNED(20061, "Hàng cấm"),
    LOST(20062, "Mất hàng"),
    DAMAGED(20063, "Hàng hư hỏng"),
    PACK_BANNED(20064, "Kiện có hàng cấm"),
    PACK_LOST(20065, "Kiện mất"),
    DESTROYED(2007, "Đã tiêu hủy"),
    REFUNDED(2008, "Đã đền bù"),
    OUT_OF_PROCESS(2009, "Đơn hàng xử lý ngoài quy trình"),
    //Hoàn thất bại
    WAIT_DESTRUCTION(905, "Chờ tiêu hủy"),
    RETURN_FAILED(5021, "Hoàn thất bại"),
    RF_CALL_FAILED(50213, "Chưa liên hệ người gửi - nhập lại kho"),
    RF_SHIPPER_DECLINED(50214, "Hoàn hàng thất bại - shipper huỷ đơn"),
    RF_RE_APPOINT(50212, "Hoàn thất bại - Người gửi hẹn lại ngày nhận"),
    RF_RECEIVER_DECLINED(50211, "Hoàn thất bại - Người gửi từ chối nhận hàng"),
    RF_CALL_RE_FAILED(50215, "Hoàn thất bại - Không liên hệ được người gửi"),
    //Hủy
    CANCEL_CARRIER(107, "Đơn vị vận chuyển hủy đơn hàng"),
    CANCEL_AUTO(903, "Vận đơn bị hủy tự động"),
    CANCEL_ADMIN(902, "Vận đơn hủy bởi quản trị viên"),
    CANCEL_SHOP(924, "Đơn hủy bởi chủ shop"),
    //Sai can nang
    WEIGHT_WRONG(923, "Người gửi xác nhận chuyển hoàn khi đơn sai cân nặng - kích thước"),
//
//    NOTE_CREATED(909, "Tạo đơn hàng thành công"),
//    NOTE_PUSH_GW_FAILED(900, "Tạo đơn thất bại"),
//    NOTE_PUSH_GW_FAILED2(900, "Tạo đơn thất bại"),
//    NOTE_HOLD_FAILED(900, "Đơn hàng duyệt thất bại do không đủ số dư"),
//    NOTE_NOTFOUND_SHIPPER(900, "Không tìm thấy bưu tá"),
//    PRE_CONFIRM_FAIL(900, "Đơn hàng duyệt thất bại do gói cước không áp dụng với"),
//    PRE_FEE_FAIL(900, "Gói cước không áp dụng với"),
//    NOTE_ORDER_PAYMENT(900, " người nhận trả phí"),
//    NOTE_ORDER_PAYMENT2(900, "Gói cước không áp dụng với người nhận trả phí"),
//    NOTE_PAYMENT_TYPE(900, " thanh toán tiền mặt"),
//    NOTE_PAYMENT_TYPE2(900, "Gói cước không áp dụng với thanh toán tiền mặt"),
//    NOTE_PARTIAL_TYPE(900, " giao hàng một phần"),
//    NOTE_PARTIAL_TYPE2(900, "Gói cước không áp dụng với giao hàng một phần"),
//    NOTE_COD_CONFIRM(1, "Cập nhật COD thành công"),
//    NOTE_COD_REJECT(1, "Cập nhật COD thất bại do bưu tá từ chối"),
//    NOTE_REQ_REDELIVERY(1, "Yêu cầu giao lại từ shop"),

            ;
    public final Integer code;
    public final String message;

    OrderStatusNameEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
    public static OrderStatusNameEnum valueOf(int value){
        for (OrderStatusNameEnum val : values()) {
            if (val.code == value)
                return val;
        }
        return OrderStatusNameEnum.WAIT_TO_CONFIRM;
    }
}
