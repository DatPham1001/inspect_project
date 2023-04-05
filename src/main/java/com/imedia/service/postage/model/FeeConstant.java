package com.imedia.service.postage.model;

public class FeeConstant {

    public final static int  INFINITY = -1;
    public final static int  YES = 1;
    public final static int  NO = 0;

    public enum TYPE_CAL{
        TYPE_FEE_VND(1),
        TYPE_FEE_PERCENT(2);
        public int value = 0;
        TYPE_CAL(int code){value = code;}
    }

    public enum ChangedInfoTypeFee {
        COD(1),
        ADDRESS(2),
        PHONE(3),
        DIMENSION(4),
        REQUIRED_NOTE(5);
        public int code;

        ChangedInfoTypeFee(int code) {
            this.code = code;
        }

        public static ChangedInfoTypeFee ofCode(int code){
            for (ChangedInfoTypeFee value : ChangedInfoTypeFee.values()) {
                if (value.code == code){
                    return value;
                }
            }
            return COD;
        }

    }
}
