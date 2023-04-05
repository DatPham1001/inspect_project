package com.imedia.service.wallet.enums;

public enum WalletCodeEnum {
    PR_LOGIN(1100, ""),

    PR_LOGIN_SOCIAL(1200, "Đăng nhập từ FB"),

    PR_LOGIN_GMAIL(1300, "Đăng nhập từ Google"),

    PR_REGISTER(1400, " Đăng ký"),

    PR_ACTIVEPHONE(1500, "Active số điện thoại"),

    PR_ACTIVE_EMAIL(1600, " Active Email"),

    PR_UPDATE_USER_INFO(1700, "Cập nhật thông tin user(tên hiển thị, ngày sinh nhật, gới tính)"),

    PR_GETUSER_INF(1800, "Lấy thông tin user"),

    PR_INNIT_PAYMENT(1900, ""),

    PR_GET_OTP(2000, " Lấy OTP cho một nghiệp vụ xác định(reset pass, cập nhật thông tin, thanh toán,...)"),

    PR_SUBMIT_PAYMENT(2100, ""),

    PR_CHANGEPASS(2200, "Đổi mật khẩu sau khi đã login success"),

    PR_GET_BILL_INFO(2300, ""),

    PR_GET_HISTORY(2400, ""),

    PR_GET_HISTORY_DETAIL(2500, ""),

    PR_LOGOUT(2600, "Đăng xuất"),

    PR_ADD_HOLDING_BALANCE(2700, "Tăng số dư tạm giữ"),

    PR_SUBSTRACT_HOLDING_BALANCE(2800, "Trừ số dư tạm giữ"),

    PR_CREDIT_BALANCE(2900, " Cộng tiền xuất phát từ client"),

    PR_DEBIT_BALANCE(3000, "Trừ tiên xuất phát từ client"),

    PR_RESET_PASS(3100, "Reset password(yêu cầu OTP)"),

    PR_REGISTER_VA_ACCOUNT(3200, " Active tài khoản VA"),

    PR_DEBIT_SERVER_SERVER(3300, "Trừ tiền tài khoản"),

    PR_CREDIT_SERVER_SERVER(3400, "Cộng tiền vào tài khoản"),
    PR_WITHDRAW_BANK(4100, "Rút tiền vào tkhoan bank"),
    PR_CHECKTRANS(3800, "Kiểm tra trạng thái giao dịch"),

    PR_VERIFY_OTP(3500, "Verify otp"),
    PR_ADD_DEBT(4300, "Cộng nợ cho đơn hàng"),
    PR_SUBTRACT_DEBT(4400, "Cộng nợ cho đơn hàng cũ"),

    SUB_TYPE_ADD_HOLDING_ORDER(8024, "Cộng tạm giữ vận đơn"),
    SUB_TYPE_ADD_HOLDING_WITHDRAW(8025, "Cộng tạm giữ rút tiền"),
    SUB_TYPE_SUBTRACT_HOLDING_ORDER(8026, "Trừ tạm giữ vận đơn "),
    SUB_TYPE_SUBTRACT_HOLDING_WITHDRAW(8027, "Trừ tạm giữ rút tiền"),
    SUB_TYPE_ADD_EPURSE_COD(8028, "Cộng tiền COD"),
    SUB_TYPE_ADD_EPURSE_VA(8029, " Cộng nạp tiền - VA"),
    SUB_TYPE_SUBTRACT_EPURSE_WITHDRAW(8031, " Trừ số dư rút tiền ví "),
    SUB_TYPE_SUBTRACT_EPURSE_FEE(8034, "Trừ phí đơn hàng"),
    SUB_TYPE_ADD_EPURSE_DEBT(8035, "Cộng nợ do trừ phí đơn hàng thất bại"),
    SUB_TYPE_SUBTRACT_EPURSE_DEBT(8036, "Giảm trừ nợ theo đơn hàng"),


    ;

    public final Integer code;
    public final String message;

    WalletCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
