package com.imedia.service.gateway.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RedeliveryGWRequest {
    private Integer account_id;
    private Integer transporter_id;
    private Integer id_account_partner;
    private String order_id;
    private String partner_order_id;
    private String transporter_code;

    private int type = 1;
    //Đổi COD thì confirm type = 3
    //redelivery thì confirm type = 1
    //retrurn thì confirm type = 2
    private int confirm_type = 1;
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
}
