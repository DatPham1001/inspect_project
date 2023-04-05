package com.imedia.model;

public enum ErrorCode {
    E106(106, "Hệ thống đang tạm thời gián đoạn, vui lòng thử lại sau"),
    E514(514, "Số dư không đủ để thực hiện giao dịch"),
    E603(603, "Tạo đơn hàng thất bại do khởi tạo điểm giao thất bại, vui lòng thử lại sau"),
    E604(604, "Địa chỉ lấy hàng không hợp lệ với tài khoản"),
    E607(607, "Tạo đơn hàng thất bại do không tìm thấy kho hàng"),
    E608(608, "Địa chỉ lấy hàng không thuộc chủ tài khoản này"),
    E610(610, "Gói cước chỉ áp dụng với người gửi trả phí"),
    E615(615, "Gói cước không áp dụng giao hàng một phần"),
    E617(617, "Gói cước không áp dụng thanh toán tiền mặt"),
    E713(713, "Vượt quá số lần thay đổi thông tin tối đa [x] lần"),
    E714(714, "Vượt quá số lần thay đổi COD tối đa [x] lần"),
    E715(715, "Vượt quá số lần thay đổi địa chỉ tối đa [x] lần"),
    E716(716, "Vượt quá số lần thay đổi cân nặng tối đa [x] lần"),
    E618(618, "Duyệt đơn hàng không thành công"),
    E750(750, "Yêu cầu giao lại thành công"),
    E751(751, "Trạng thái không cho phép gửi yêu cầu giao lại"),
    E762(762, "Hủy đơn hàng thất bại, vui lòng liên hệ quản trị"),
    E760(760, "Hủy đơn hàng thành công"),
    E761(761, "Trạng thái đơn hàng không được phép hủy đơn"),
    E770(770, "Yêu cầu chuyển hoàn thành công"),
    E771(771, "Trạng thái đơn không được yêu cầu chuyển hoàn"),
    E772(772, "Yêu cầu chuyển hoàn thất bại, vui lòng liên hệ quản trị"),
    E717(717, "COD tối thiểu được phép thay đổi là [x]"),
    E718(718, "COD tối đa được phép thay đổi là [x]"),
    E719(719, "Đơn hàng có yêu cầu giao hàng một phần"),
    E132(132, "Số điện thoại hoặc tài khoản đã tồn tại"),
    E780(780, "Xác nhận yêu cầu thành công"),
    E781(781, "Không tồn tại yêu cầu giao hàng 1 phần"),
    E8001(8001, "Thất bại"),
    E8002(8002, "Trạng thái không phù hợp"),
    E8003(8003, "Thời gian không phù hợp"),
    E2112(2112, "Khoảng cách không hợp lệ theo gói cước"),
    E99(99, "Giao dịch đang được xử lý"),
    E408(408, "Đăng ký tài khoản không thành công, liên hệ admin để được hỗ trợ"),
    E409(409, "Đăng nhập không thành công,liên hệ admin để được hỗ trợ"),
    E510(510, "Không tồn tại số tài khoản liên kết "),
    E120(120, "Giao dịch không thành công,vui lòng thử lại sau"),
    E515(515, "Quá hạn mức giao dịch, số tiền tối đa được rút 1 lần là "),
    E516(516, "Số tiền tối thiểu phải rút là"),
    E2104(2104, "Mã đơn hàng đã tồn tại"),
    E666(666, "Sai dữ liệu đầu vào"),
    E8005(8005, "Trạng thái đơn hàng không phù hợp"),
    E507(507, "Tài khoản đã tạo VA "),
    E511(511, "Tên chủ tài khoản không đúng"),
    E211(211, "Quá số lượng tối đa nhận OTP trong 1 ngày, vui lòng thử lại sau"),
    E2001(2001, "Lỗi hệ thống tính giá,vui lòng liên hệ quản trị viên"),
    E2105(2105, "Điểm giao hàng không thuộc tuyến giao của gói cước"),
    E2000(2000, "Tạo đơn hàng thành công"),
    E605(605, "Gói cước đồng giá chỉ áp dụng 1 điểm giao 1 người nhận"),
    E606(606, "Đơn hàng không có điểm giao"),
    E700(700, "Cập nhật đơn hàng thành công"),
    E701(701, "Trạng thái đơn không được cập nhật"),
    E702(702, "Trạng thái đơn không được cập nhật COD"),
    E703(703, "Trạng thái đơn không được cập nhật địa chỉ giao hàng"),
    E704(704, "Trạng thái đơn không được cập nhật cân nặng"),
    E705(705, "Tiếp nhận yêu cầu thành công, chờ shipper xác nhận COD mới"),
    E706(706, "Gói cước không tồn tại"),
    E707(707, "Cập nhật đơn hàng thành công"),
    E708(708, "Cập nhật thông tin đơn hàng thất bại"),
    E709(709, "Cập nhật thông tin sản phẩm thất bại"),
    E710(710, "Gói cước không hỗ trợ đổi COD"),
    E782(782, "Từ chối yêu cầu thành công"),
    E2124(2124, "Giá trị khai giá không hợp lệ"),
    E790(790, "Xóa đơn hàng thành công"),
    E2101(2101, "Dữ liệu không hợp lệ,vui lòng kiểm tra lại thông tin đơn hàng"),
    E505(505, "Không tìm thấy mã địa chỉ truyền vào"),
    E512(512, "Đã có lỗi trong quá trình kích hoạt tài khoản VA. Vui lòng thử lại sau"),
    E513(513, "Dịch vụ đang tạm thời gián đoạn do phía ngân hàng đang nâng cấp bảo trì hệ thống. Vui lòng thử lại sau, xin cảm ơn!"),
    E611(611, "Không tìm được shipper, có thể tìm lại ship trong trang đơn chờ duyệt"),
    E612(612, "Đang tìm shipper cho đơn hàng"),
    E609(609, "Duyệt đơn hàng thành công"),
    E613(613, "Đơn hàng chưa được tiếp nhận thành công, vui lòng kiểm tra trang đơn chờ duyệt"),
    E614(614, "Đơn hàng không tồn tại"),
    E616(616, "Không đủ số dư để duyệt đơn hàng"),
    E2102(2102, "Gói cước không hợp lệ, vui lòng đổi gói cước"),
    E720(720, "Hủy đơn hàng thành công"),
    E721(721, "Trạng thái đơn hàng không được hủy"),
    E722(722, "Hủy đơn hàng thất bại, vui lòng thử lại sau"),
    E8004(8004, "Đơn hàng không phù hợp"),
    E200(200, "Thành công"),
    E140(140, "Số điện thoại hoặc tài khoản đã tồn tại"),
    E100(100, "Tài khoản hoặc mật khẩu không đúng"),
    E101(101, "Tài khoản không tồn tại"),
    E103(103, "Giải mã dữ liệu thất bại"),
    E105(105, "Không tìm thấy Session"),
    E115(115, "Sai số điện thoại"),
    E117(117, "Không đủ số dư"),
    E126(126, "Cộng tiền cho tài khoản thất bại"),
    E128(128, "Không tìm thấy tài khoản nhận tiền"),
    E133(133, "Độ dài mật khẩu quá dài"),
    E134(134, "Định dạng email không đúng"),
    E135(135, "Mã OTP không đúng hoặc không tồn tại"),
    E136(136, "Tài khoản không có số điện thoại,vui lòng cập nhật số điện thoại"),
    E138(138, "Sai thông tin user hoặc mã ngăn ví dịch vụ"),
    E139(139, "Số tiền muốn thanh toán phải lớn hơn 0"),
    E141(141, "Email đã tồn tại"),
    E501(501, "Phát hiện đăng nhập từ thiết bị mới,đã gửi OTP về số điện thoại đăng ký"),
    E500(500, "Dịch vụ đang tạm thời gián đoạn.Vui lòng thử lại sau"),
    E401(401, "Unauthorzied"),
    E402(402, "Truy vấn sai username ứng với token"),
    E400(400, "Sai format truyền vào"),
    E502(502, "Cập nhật thông tin thành công,đã gửi OTP vào số điện thoại"),
    E504(504, "Không tìm thấy mã thiết bị"),
    E406(406, "Tài khoản và số điện thoại đã tồn tại "),
    E506(506, "Không tìm thấy thông tin kho hàng"),
    E508(508, "Số tài khoản hoặc số thẻ ngân hàng không đúng"),
    E509(509, "Số tài khoản ngân hàng đã được thêm với tài khoản này"),
    E600(600, "Tạo đơn hàng thành công,đơn hàng đang xử lý"),
    E601(601, "Đơn tạo thành công,để duyệt đơn cần nạp thêm [x]"),
    E602(602, "Tạo đơn hàng thất bại do hệ thống bận, vui lòng thử lại sau"),
    E711(711, "Gói cước không áp dụng thay đổi địa chỉ"),
    E712(712, "Gói cước không áp dụng thay đổi cân nặng"),
    E410(410, "Thay đổi mật khẩu thành công"),
    E411(411, "Đổi mật khẩu thất bại"),
    E2111(2111, "Kích thước hàng hóa vượt quá cấu hình gói cước"),
    E2103(2103, "Không tìm thấy mã vùng "),
    E2113(2113, "Số điểm giao vượt quá cấu hình gói cước"),
    E2114(2114, "Số đơn trong một điểm giao vượt quá cấu hình gói cước"),
    E2115(2115, "Số tiền thu hộ vượt quá cấu hình gói cước"),
    E2125(2125, "Mã tỉnh/thành phố người gửi không hợp lệ"),
    E2126(2126, "Mã tỉnh/thành phố người nhận không hợp lệ"),
    E2127(2127, "Mã quận/huyện người gửi không hợp lệ"),
    E2128(2128, "Mã quận/huyện người nhận không hợp lệ"),
    E2129(2129, "Mã phường/xã người gửi không hợp lệ"),
    E2130(2130, "Mã phường/xã người nhận không hợp lệ"),
    E517(517, "Tài khoản không có quyền truy cập tính năng này"),
    ;
    public final Integer code;
    public final String message;

    ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static ErrorCode valueOf(int value) {
        for (ErrorCode val : values()) {
            if (val.code == value)
                return val;
        }
        return ErrorCode.E500;
    }

    public static void main(String[] args) {

    }
}
