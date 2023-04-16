package com.imedia.util;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class GenerateTransactionId {

    static GenerateTransactionId instance = null;
    static AtomicInteger atomicVal = new AtomicInteger(0);

    public static GenerateTransactionId GetInstance() {
        if (instance == null)
            new GenerateTransactionId();
        return instance;
    }

    public GenerateTransactionId() {
        instance = this;
    }

    /**
     * Ham sinh ra transactionID
     *
     * @param input neu input != null chuoi sinh ra se co prefix la input
     * @return String
     * @author VietTung
     */
    public String GeneratePartnerTransactionId(String input) {
        return input + GenerateTransactionId.dateToString(new Date(),
                "YYMMdd") +
                generateRandomId(6);
    }

    public String generateRandomId(String prefix, Integer number) {
        StringBuilder stringBuilder = new StringBuilder(prefix);
        for (int i = 1; i <= number; i++) {
            stringBuilder.append(new Random().nextInt(10));
        }
        return stringBuilder.toString();
    }

    public static String generateRandomId2(Integer number) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 1; i <= number; i++) {
            stringBuilder.append(new Random().nextInt(9) + 1);
        }
        return stringBuilder.toString();
    }

    public String generateRandomId(Integer number) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 1; i <= number; i++) {
            stringBuilder.append(new Random().nextInt(10));
        }
        return stringBuilder.toString();
    }

    /**
     * Ham sinh ra transaction ID gui sang FTP
     */
    public String GeneratePartnerTransactionId() {
        String strDateFormat = GenerateTransactionId.dateToString(new Date(),
                "yyyyMMddHHmmss");
        return strDateFormat + String.format("%05d", GetIncreaseNumber());
    }

    private int GetIncreaseNumber() {
        synchronized (atomicVal) {
            int value = atomicVal.getAndIncrement();
            if (value == 999)
                atomicVal = new AtomicInteger(0);
            return value;
        }
    }

    public static String dateToString(Date date, String formatString) {
        DateFormat dateFormat = new SimpleDateFormat(formatString);// "yyyy/MM/dd HH:mm:ss");
        return dateFormat.format(date);
    }

    public static BigDecimal generateUniqueOrderCode() {
        DateFormat dateFormat = new SimpleDateFormat("yyMMdd");//
        String ymd = dateFormat.format(new Date());
//        String sequence = CallRedis.generateSequence();
//        String result = ymd + sequence;
        String result = CallRedis.generateSequence() + ymd;
        Long value = Long.parseLong(result);
        return BigDecimal.valueOf(value);
    }

    public static void main(String[] args) {
        for (int i = 0; i < 50; i++)
            System.out.println(generateUniqueOrderCode());
    }
}
