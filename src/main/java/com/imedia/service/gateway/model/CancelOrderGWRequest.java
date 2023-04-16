package com.imedia.service.gateway.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CancelOrderGWRequest {
    private Integer account_id;
    private Integer transporter_id;
    private Integer id_account_partner;
    private String order_id;
    private String partner_order_id;
    private String transporter_code;
    //Đổi COD thì type = 3
    private int type = 3;
    private int confirm_type = 3;
    private int channel = 1;
    //Defaults
    private int get_info_flg = 0;
    private int flag_fix_fee = 0;
    private int real_deposit_amt = 0;
    private int tien_hang = 0;
    private int loai_vandon = 0;
    private int trong_luong = 0;
    private int tong_cuoc_vnd = 0;
    private int phi_cod = 0;
    private int bao_hiem = 0;
    private int phi_vas = 0;
    private int phu_phi = 0;
    private int phu_phi_khac = 0;
    private int tong_vat = 0;
    private int tong_tien = 0;
    private int tien_thu_ho = 0;
    private int so_luong = 0;
    private int chg_deposit_amt = 0;
    private int is_check = 0;
    private int is_insurance = 0;
    private int chieu_cao = 0;
    private int chieu_rong = 0;
    private int chieu_dai = 0;
    private int store_id = 0;
    private int payment_type_id = 0;
    private int check_flag = 0;
    private int partner_service_id = 0;
    private int order_payment = 0;
    private int part_sign = 0;
    private int status = 0;
    private int old_status = 0;


//"{\"order_id\":\"210625000257\","
//        "\"get_info_flg\":0,"
//        "\"flag_fix_fee\":0,"
//        "\"real_deposit_amt\":0,"
//        "\"tien_hang\":0," +
//        "\"loai_vandon\":0," +
//        "\"trong_luong\":0," +
//        "\"tong_cuoc_vnd\":0," +
//        "\"phi_cod\":0," +
//        "\"phi_vas\":0," +
//        "\"bao_hiem\":0," +
//        "\"phu_phi\":0," +
//        "\"phu_phi_khac\":0," +
//        "\"tong_vat\":0," +
//        "\"tong_tien\":0," +
//        "\"tien_thu_ho\":0," +
//        "\"so_luong\":0," +
//        "\"chg_deposit_amt\":0,
//        \"account_id\":1070,
//        \"comment\":
//        \"H?y ??n hàng ?ã duy?t\"," +
//        "\"type\":3,
//        \"confirm_type\":3,
//        \"is_check\":0,
//        \"is_insurance\":0,
//        \"transporter_id\":101," +
//        "\"transporter_code\":\"HNI\","
//        "\"ca_giao_hang\":0," +
//        "\"chieu_cao\":0," +
//        "\"chieu_rong\":0," +
//        "\"chieu_dai\":0," +
//        "\"store_id\":0," +
//        "\"payment_type_id\":0," +
//        "\"check_flag\":0," +
//        "\"partner_service_id\":0," +
//        "\"order_payment\":0," +
//        "\"part_sign\":0," +
//        "\"partner_order_id\":\"HLS210625000257\"," +
//        "\"status\":0," +
//        "\"old_status\":0}
}
