package com.imedia.service.order;

import com.imedia.oracle.entity.AppUser;
import com.imedia.oracle.entity.SvcOrderDetail;
import com.imedia.oracle.repository.AppUserRepository;
import com.imedia.service.order.model.AffiliateCallbackData;
import com.imedia.util.CallServer;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

@Service
@Log4j2
public class CallBackService {
    private final AppUserRepository appUserRepository;

    @Autowired
    public CallBackService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    public void callbackAffiliate(SvcOrderDetail orderDetail, int status, String note) {
        //Callback
        try {
            if (!orderDetail.getChannel().equals("AFF") && !orderDetail.getChannel().equals("API")) {
                log.info("======NOT AFFILIATE ORDER TO CALLBACK======" + orderDetail.getSvcOrderDetailCode());
                return;
            }
            if (orderDetail.getShopReferId() == null)
                return;
            AppUser referUser = appUserRepository.findAppUserById(orderDetail.getShopReferId());

            //Build callback
            AffiliateCallbackData affiliateCallbackData = new AffiliateCallbackData();
            affiliateCallbackData.setShopId(orderDetail.getShopId());
            affiliateCallbackData.setOrderShortcode(String.valueOf(orderDetail.getSvcOrderDetailCode()));
            if (orderDetail.getCarrierOrderId() != null)
                affiliateCallbackData.setCarrierOrderCode(orderDetail.getCarrierOrderId());
            affiliateCallbackData.setShopOrderCode(orderDetail.getShopOrderId());
            affiliateCallbackData.setCarrierId(orderDetail.getCarrierId());
            affiliateCallbackData.setCarrierCode(orderDetail.getCarrierServiceCode());
            affiliateCallbackData.setStatus(status);
//            affiliateCallbackData.setStatusText(OrderStatusNameEnum.valueOf(status).message);
            affiliateCallbackData.setNote(note);
            affiliateCallbackData.setActionTime(new Timestamp(new Date().getTime()));
            affiliateCallbackData.setWeight(orderDetail.getWeight());
//            BigDecimal totalFee = sellingFeeRepository.sumTotalFee(orderDetail.getSvcOrderDetailCode());
//            affiliateCallbackData.setFee(totalFee);
//            affiliateCallbackData.setCod(orderDetail.getRealityCod());
//            affiliateCallbackData.setCallbackUrl(referUser.getCallbackUrl());
//            if (referUser.getCallbackUrl() != null)
//                CallServer.getInstance().post(referUser.getCallbackUrl(), gson.toJson(affiliateCallbackData));
        } catch (Exception e) {
            log.info("======CALBACK TO AFF EXCEPTION=====" + orderDetail.getSvcOrderDetailCode(), e);
        }
    }
}
