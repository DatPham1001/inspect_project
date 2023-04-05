package com.imedia.service.postage;

import com.google.gson.Gson;
import com.imedia.oracle.entity.*;
import com.imedia.oracle.repository.DetailSellingFeeRepository;
import com.imedia.oracle.repository.HistoryChangeReceiverRepository;
import com.imedia.oracle.repository.HistorySellingFeeRepository;
import com.imedia.oracle.repository.V2PackageRepository;
import com.imedia.service.order.enums.SellingFeeEnum;
import com.imedia.service.order.model.CreateOrderReceiver;
import com.imedia.service.postage.model.CalculateFeeReceivers;
import com.imedia.service.postage.model.CalculateSpecificFeeData;
import com.imedia.service.postage.model.FeeConstant;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FeeService {
    static final Logger logger = LogManager.getLogger(FeeService.class);
    static final Gson gson = new Gson();
    private final DetailSellingFeeRepository sellingFeeRepository;
    private final HistorySellingFeeRepository historySellingFeeRepository;
    private final PostageService postageService;
    private final HistoryChangeReceiverRepository changeReceiverRepository;

    private final V2PackageRepository packageRepository;

    @Autowired
    public FeeService(DetailSellingFeeRepository sellingFeeRepository, HistorySellingFeeRepository historySellingFeeRepository, PostageService postageService, HistoryChangeReceiverRepository changeReceiverRepository, V2PackageRepository packageRepository) {
        this.sellingFeeRepository = sellingFeeRepository;
        this.historySellingFeeRepository = historySellingFeeRepository;
        this.postageService = postageService;
        this.changeReceiverRepository = changeReceiverRepository;
        this.packageRepository = packageRepository;
    }

    public DetailSellingFee insertUpdateFee(BigDecimal orderDetailCode, String packCode, String changeType, int counter) {
        try {
            DetailSellingFee transportFee = sellingFeeRepository.findByCodeAndOrderDetailCode(SellingFeeEnum.TRANSPORT_FEE.code, orderDetailCode);
            if (transportFee != null) {
                CalculateSpecificFeeData feeData = postageService.calculateSpecificFee(orderDetailCode, packCode,
                        SellingFeeEnum.UPDATE_FEE.code, counter, transportFee.getValue(), changeType);
                if (feeData != null && feeData.getFee().compareTo(BigDecimal.ZERO) > 0) {
                    DetailSellingFee updateFee = sellingFeeRepository.findByCodeAndOrderDetailCode(SellingFeeEnum.UPDATE_FEE.code, orderDetailCode);
                    if (updateFee != null) {
                        updateFee.setValueOld(updateFee.getValue());
                        updateFee.setValue(updateFee.getValue().add(feeData.getFee()));
                        sellingFeeRepository.save(updateFee);
                        logger.info("=======UPDATE FEE SAVE TO DB=======" + orderDetailCode + "||" + gson.toJson(updateFee));
                        return updateFee;
                    } else {
                        DetailSellingFee sellingFee = new DetailSellingFee();
                        sellingFee.setValue(feeData.getFee());
                        sellingFee.setValueOld(BigDecimal.ZERO);
                        sellingFee.setName(SellingFeeEnum.UPDATE_FEE.message);
                        sellingFee.setOrderDetailCode(orderDetailCode);
                        sellingFee.setCode(SellingFeeEnum.UPDATE_FEE.code);
                        DetailSellingFee result = sellingFeeRepository.save(sellingFee);
                        logger.info("=======UPDATE FEE SAVE TO DB=======" + orderDetailCode + "||" + gson.toJson(result));
                        return result;
                    }
                }
            }
        } catch (Exception e) {
            logger.info("========INSERT UPDATE FEE EXCEPTION=======" + orderDetailCode + "||" + changeType, e);
        }
        return null;
    }

    public void revertUpdateFee(BigDecimal orderDetailCode, String changeType) {

    }

    public void insertChangeCodHistory(SvcOrderDetail oldOrderDetail, BigDecimal oldCod, BigDecimal newCod) {
        try {
            HistoryChangeInfoReceiver changeInfoReceiver = new HistoryChangeInfoReceiver();
            changeInfoReceiver.setChangeType(FeeConstant.ChangedInfoTypeFee.COD.name());
            changeInfoReceiver.setOrderDetailCode(oldOrderDetail.getSvcOrderDetailCode());
            changeInfoReceiver.setAddressDiliveryIdNew(oldOrderDetail.getAddressDeliveryId());
            changeInfoReceiver.setAddressDiliveryIdNew(oldOrderDetail.getAddressDeliveryId());
            changeInfoReceiver.setWeightNew(oldOrderDetail.getWeight());
            changeInfoReceiver.setWeightOld(oldOrderDetail.getWeight());
            changeInfoReceiver.setConsigneeNew(oldOrderDetail.getConsignee());
            changeInfoReceiver.setPhoneNew(oldOrderDetail.getPhone());
            changeInfoReceiver.setConsigneeOld(oldOrderDetail.getConsignee());
            changeInfoReceiver.setPhoneOld(oldOrderDetail.getPhone());
            //Changed
            changeInfoReceiver.setCodOld(oldCod);
            changeInfoReceiver.setCodNew(newCod);
            changeReceiverRepository.save(changeInfoReceiver);
        } catch (Exception e) {
            logger.info("========INSERT CHANGE COD EXCEPTION=======" + oldOrderDetail.getSvcOrderDetailCode()
                    + "|| OLD " + oldCod + "|| NEW " + newCod, e);
        }
    }

    public void insertChangeReceiverInfoHistory(SvcOrderDetail oldOrderDetail, CreateOrderReceiver updateCreateOrderReceiver,
                                                long newAddressDelivery, String changeType) {
        try {
            //Đổi sđt
            if (changeType.equals(FeeConstant.ChangedInfoTypeFee.PHONE.toString())) {
                HistoryChangeInfoReceiver changeInfoReceiver = new HistoryChangeInfoReceiver();
                changeInfoReceiver.setChangeType(changeType);
                changeInfoReceiver.setOrderDetailCode(oldOrderDetail.getSvcOrderDetailCode());
                changeInfoReceiver.setAddressDiliveryIdNew(oldOrderDetail.getAddressDeliveryId());
                changeInfoReceiver.setAddressDiliveryIdOld(oldOrderDetail.getAddressDeliveryId());
                changeInfoReceiver.setWeightNew(oldOrderDetail.getWeight());
                changeInfoReceiver.setWeightOld(oldOrderDetail.getWeight());
                changeInfoReceiver.setCodOld(BigDecimal.ZERO);
                changeInfoReceiver.setCodNew(BigDecimal.ZERO);
                //changed
                changeInfoReceiver.setConsigneeNew(updateCreateOrderReceiver.getName());
                changeInfoReceiver.setPhoneNew(updateCreateOrderReceiver.getPhone());
                changeInfoReceiver.setConsigneeOld(oldOrderDetail.getConsignee());
                changeInfoReceiver.setPhoneOld(oldOrderDetail.getPhone());
                changeReceiverRepository.save(changeInfoReceiver);
            }
            //Đổi địa chỉ
            if (changeType.equals(FeeConstant.ChangedInfoTypeFee.ADDRESS.toString())) {
                HistoryChangeInfoReceiver changeInfoReceiver = new HistoryChangeInfoReceiver();
                changeInfoReceiver.setChangeType(changeType);
                changeInfoReceiver.setOrderDetailCode(oldOrderDetail.getSvcOrderDetailCode());
                changeInfoReceiver.setConsigneeNew(oldOrderDetail.getConsignee());
                changeInfoReceiver.setPhoneNew(oldOrderDetail.getPhone());
                changeInfoReceiver.setConsigneeOld(oldOrderDetail.getConsignee());
                changeInfoReceiver.setPhoneOld(oldOrderDetail.getPhone());
                changeInfoReceiver.setWeightNew(oldOrderDetail.getWeight());
                changeInfoReceiver.setWeightOld(oldOrderDetail.getWeight());
                changeInfoReceiver.setCodOld(BigDecimal.ZERO);
                changeInfoReceiver.setCodNew(BigDecimal.ZERO);
                //Changed
                changeInfoReceiver.setAddressDiliveryIdNew(newAddressDelivery);
                changeInfoReceiver.setAddressDiliveryIdOld(oldOrderDetail.getAddressDeliveryId());
                changeReceiverRepository.save(changeInfoReceiver);
            }
            //Đổi cân nặng
            if (changeType.equals(FeeConstant.ChangedInfoTypeFee.DIMENSION.toString())) {
                HistoryChangeInfoReceiver changeInfoReceiver = new HistoryChangeInfoReceiver();
                changeInfoReceiver.setChangeType(changeType);
                changeInfoReceiver.setOrderDetailCode(oldOrderDetail.getSvcOrderDetailCode());
                changeInfoReceiver.setAddressDiliveryIdNew(oldOrderDetail.getAddressDeliveryId());
                changeInfoReceiver.setAddressDiliveryIdOld(oldOrderDetail.getAddressDeliveryId());
                changeInfoReceiver.setConsigneeNew(oldOrderDetail.getConsignee());
                changeInfoReceiver.setPhoneNew(oldOrderDetail.getPhone());
                changeInfoReceiver.setConsigneeOld(oldOrderDetail.getConsignee());
                changeInfoReceiver.setPhoneOld(oldOrderDetail.getPhone());
                changeInfoReceiver.setCodOld(BigDecimal.ZERO);
                changeInfoReceiver.setCodNew(BigDecimal.ZERO);
                //Changed
                changeInfoReceiver.setWeightNew(updateCreateOrderReceiver.getWeight());
                changeInfoReceiver.setWeightOld(oldOrderDetail.getWeight());
                changeReceiverRepository.save(changeInfoReceiver);
            }
        } catch (Exception e) {
            logger.info("========INSERT CHANGE COD EXCEPTION=======" + oldOrderDetail.getSvcOrderDetailCode()
                    + "|| TYPE " + changeType, e);
        }
    }

    public List<DetailSellingFee> createSellingFee(BigDecimal orderDetailCode, CalculateFeeReceivers calculateFeeReceivers) throws Exception {
        sellingFeeRepository.deleteSellingFeeByDetailCode(orderDetailCode);
        List<DetailSellingFee> detailSellingFees = new ArrayList<>();
        //Transport
        if (calculateFeeReceivers.getTransportFee().compareTo(BigDecimal.ZERO) > 0) {
            DetailSellingFee transportFee = new DetailSellingFee(
                    SellingFeeEnum.TRANSPORT_FEE.code,
                    SellingFeeEnum.TRANSPORT_FEE.message,
                    orderDetailCode,
                    calculateFeeReceivers.getTransportFee());
            detailSellingFees.add(transportFee);
            sellingFeeRepository.save(transportFee);
        }

        //Pickup
        if (calculateFeeReceivers.getPickupFee().compareTo(BigDecimal.ZERO) > 0) {
            DetailSellingFee pickupFee = new DetailSellingFee(SellingFeeEnum.PICKUP_FEE.code,
                    SellingFeeEnum.PICKUP_FEE.message,
                    orderDetailCode,
                    calculateFeeReceivers.getPickupFee());
            detailSellingFees.add(pickupFee);
            sellingFeeRepository.save(pickupFee);
        }
        //Porter
        if (calculateFeeReceivers.getPorterFee().compareTo(BigDecimal.ZERO) > 0) {
            DetailSellingFee porterFee = new DetailSellingFee(SellingFeeEnum.PORTER_FEE.code,
                    SellingFeeEnum.PORTER_FEE.message,
                    orderDetailCode,
                    calculateFeeReceivers.getPorterFee());
            detailSellingFees.add(porterFee);
            sellingFeeRepository.save(porterFee);
        }
        ///Partial
        if (calculateFeeReceivers.getPartialFee().compareTo(BigDecimal.ZERO) > 0) {
            DetailSellingFee partialFee = new DetailSellingFee(SellingFeeEnum.PARTIAL_FEE.code,
                    SellingFeeEnum.PARTIAL_FEE.message,
                    orderDetailCode,
                    calculateFeeReceivers.getPartialFee());
            detailSellingFees.add(partialFee);
            sellingFeeRepository.save(partialFee);
        }
        //Handover
        if (calculateFeeReceivers.getHandoverFee().compareTo(BigDecimal.ZERO) > 0) {
            DetailSellingFee handoverFee = new DetailSellingFee(SellingFeeEnum.HANDOVER_FEE.code,
                    SellingFeeEnum.HANDOVER_FEE.message,
                    orderDetailCode,
                    calculateFeeReceivers.getHandoverFee());
            detailSellingFees.add(handoverFee);
            sellingFeeRepository.save(handoverFee);
        }
        //Insurance
        if (calculateFeeReceivers.getInsuranceFee().compareTo(BigDecimal.ZERO) > 0) {
            DetailSellingFee insuranceFee = new DetailSellingFee(SellingFeeEnum.INSURANCE_FEE.code,
                    SellingFeeEnum.INSURANCE_FEE.message,
                    orderDetailCode,
                    calculateFeeReceivers.getInsuranceFee());
            detailSellingFees.add(insuranceFee);
            sellingFeeRepository.save(insuranceFee);
        }
        //COD fee
        if (calculateFeeReceivers.getCodFee().compareTo(BigDecimal.ZERO) > 0) {
            DetailSellingFee codFee = new DetailSellingFee(SellingFeeEnum.COD_FEE.code,
                    SellingFeeEnum.COD_FEE.message,
                    orderDetailCode,
                    calculateFeeReceivers.getCodFee());
            detailSellingFees.add(codFee);
            sellingFeeRepository.save(codFee);
        }
        //OtherFee
        if (calculateFeeReceivers.getOtherFee().compareTo(BigDecimal.ZERO) > 0) {
            DetailSellingFee otherFee = new DetailSellingFee(SellingFeeEnum.OTHER_FEE.code,
                    SellingFeeEnum.OTHER_FEE.message,
                    orderDetailCode,
                    calculateFeeReceivers.getOtherFee());
            detailSellingFees.add(otherFee);
            sellingFeeRepository.save(otherFee);
        }
        logger.info("======DETAIL FEE SAVE TO DB=======" + gson.toJson(detailSellingFees));
//        sellingFeeRepository.saveAll(detailSellingFees);
        return detailSellingFees;
    }

    public List<DetailSellingFee> updateSellingFee(BigDecimal orderDetailCode,
                                                   CalculateFeeReceivers calculateFeeReceivers) throws Exception {
        List<DetailSellingFee> oldSellingFees = sellingFeeRepository.findAllByOrderDetailCode(orderDetailCode);
        try {
            List<String> existCodes = oldSellingFees.stream()
                    .map(DetailSellingFee::getCode)
                    .collect(Collectors.toList());
            //Update 8 default fee
            for (DetailSellingFee sellingFee : oldSellingFees) {
                //Transport
                if (sellingFee.getCode().equals(SellingFeeEnum.TRANSPORT_FEE.code)
                        && calculateFeeReceivers.getTransportFee().compareTo(BigDecimal.ZERO) > 0
                        && !sellingFee.getValue().equals(calculateFeeReceivers.getTransportFee())) {
                    insertHistorySellingFee(orderDetailCode, SellingFeeEnum.TRANSPORT_FEE, calculateFeeReceivers.getTransportFee(), sellingFee.getValue());
                    sellingFee.setValueOld(sellingFee.getValue());
                    sellingFee.setValue(calculateFeeReceivers.getTransportFee());
                }
                //pickup
                if (calculateFeeReceivers.getPickupFee().compareTo(BigDecimal.ZERO) > 0
                        && sellingFee.getCode().equals(SellingFeeEnum.PICKUP_FEE.code)
                        && !sellingFee.getValue().equals(calculateFeeReceivers.getPickupFee())) {
                    insertHistorySellingFee(orderDetailCode, SellingFeeEnum.PICKUP_FEE, calculateFeeReceivers.getPickupFee(), sellingFee.getValue());
                    sellingFee.setValueOld(sellingFee.getValue());
                    sellingFee.setValue(calculateFeeReceivers.getPickupFee());
                }
                //Porter
                if (sellingFee.getCode().equals(SellingFeeEnum.PORTER_FEE.code)
                        && calculateFeeReceivers.getPorterFee().compareTo(BigDecimal.ZERO) > 0
                        && !sellingFee.getValue().equals(calculateFeeReceivers.getPorterFee())) {
                    insertHistorySellingFee(orderDetailCode, SellingFeeEnum.PORTER_FEE, calculateFeeReceivers.getPorterFee(), sellingFee.getValue());
                    sellingFee.setValueOld(sellingFee.getValue());
                    sellingFee.setValue(calculateFeeReceivers.getPorterFee());
                }
                //Partial
                if (sellingFee.getCode().equals(SellingFeeEnum.PARTIAL_FEE.code)
                        && calculateFeeReceivers.getPartialFee().compareTo(BigDecimal.ZERO) > 0
                        && !sellingFee.getValue().equals(calculateFeeReceivers.getPartialFee())) {
                    insertHistorySellingFee(orderDetailCode, SellingFeeEnum.PARTIAL_FEE, calculateFeeReceivers.getPartialFee(), sellingFee.getValue());
                    sellingFee.setValueOld(sellingFee.getValue());
                    sellingFee.setValue(calculateFeeReceivers.getPartialFee());
                }
                //Handover
                if (sellingFee.getCode().equals(SellingFeeEnum.HANDOVER_FEE.code)
                        && calculateFeeReceivers.getHandoverFee().compareTo(BigDecimal.ZERO) > 0
                        && !sellingFee.getValue().equals(calculateFeeReceivers.getHandoverFee())) {
                    insertHistorySellingFee(orderDetailCode, SellingFeeEnum.HANDOVER_FEE, calculateFeeReceivers.getHandoverFee(), sellingFee.getValue());
                    sellingFee.setValueOld(sellingFee.getValue());
                    sellingFee.setValue(calculateFeeReceivers.getHandoverFee());
                }
                //Insurance
                if (sellingFee.getCode().equals(SellingFeeEnum.INSURANCE_FEE.code)
                        && calculateFeeReceivers.getInsuranceFee().compareTo(BigDecimal.ZERO) > 0
                        && !sellingFee.getValue().equals(calculateFeeReceivers.getInsuranceFee())) {
                    insertHistorySellingFee(orderDetailCode, SellingFeeEnum.INSURANCE_FEE, calculateFeeReceivers.getInsuranceFee(), sellingFee.getValue());
                    sellingFee.setValueOld(sellingFee.getValue());
                    sellingFee.setValue(calculateFeeReceivers.getInsuranceFee());
                }
                //Cod
                if (sellingFee.getCode().equals(SellingFeeEnum.COD_FEE.code)
                        && calculateFeeReceivers.getCodFee().compareTo(BigDecimal.ZERO) > 0
                        && !sellingFee.getValue().equals(calculateFeeReceivers.getCodFee())) {
                    insertHistorySellingFee(orderDetailCode, SellingFeeEnum.COD_FEE, calculateFeeReceivers.getCodFee(), sellingFee.getValue());
                    sellingFee.setValueOld(sellingFee.getValue());
                    sellingFee.setValue(calculateFeeReceivers.getCodFee());
                }
                //Other
                if (sellingFee.getCode().equals(SellingFeeEnum.OTHER_FEE.code)
                        && calculateFeeReceivers.getOtherFee().compareTo(BigDecimal.ZERO) > 0
                        && !sellingFee.getValue().equals(calculateFeeReceivers.getOtherFee())) {
                    insertHistorySellingFee(orderDetailCode, SellingFeeEnum.OTHER_FEE, calculateFeeReceivers.getOtherFee(), sellingFee.getValue());
                    sellingFee.setValueOld(sellingFee.getValue());
                    sellingFee.setValue(calculateFeeReceivers.getOtherFee());
                }
            }
            //Nếu không tồn tại và khi tính giá lại có thì khởi tạo mới
            //transport
            if (calculateFeeReceivers.getTransportFee().compareTo(BigDecimal.ZERO) > 0
                    && !existCodes.contains(SellingFeeEnum.TRANSPORT_FEE.code)) {
                DetailSellingFee fee = new DetailSellingFee(
                        SellingFeeEnum.TRANSPORT_FEE.code,
                        SellingFeeEnum.TRANSPORT_FEE.message,
                        orderDetailCode,
                        calculateFeeReceivers.getTransportFee());
                oldSellingFees.add(fee);
            }
            //pickup
            if (calculateFeeReceivers.getPickupFee().compareTo(BigDecimal.ZERO) > 0
                    && !existCodes.contains(SellingFeeEnum.PICKUP_FEE.code)) {
                DetailSellingFee fee = new DetailSellingFee(
                        SellingFeeEnum.PICKUP_FEE.code,
                        SellingFeeEnum.PICKUP_FEE.message,
                        orderDetailCode,
                        calculateFeeReceivers.getPickupFee());
                oldSellingFees.add(fee);
            }
            //Porter
            if (calculateFeeReceivers.getPorterFee().compareTo(BigDecimal.ZERO) > 0
                    && !existCodes.contains(SellingFeeEnum.PORTER_FEE.code)) {
                DetailSellingFee fee = new DetailSellingFee(
                        SellingFeeEnum.PORTER_FEE.code,
                        SellingFeeEnum.PORTER_FEE.message,
                        orderDetailCode,
                        calculateFeeReceivers.getPorterFee());
                oldSellingFees.add(fee);
            }
            //Partial
            if (calculateFeeReceivers.getPartialFee().compareTo(BigDecimal.ZERO) > 0
                    && !existCodes.contains(SellingFeeEnum.PARTIAL_FEE.code)) {
                DetailSellingFee fee = new DetailSellingFee(
                        SellingFeeEnum.PARTIAL_FEE.code,
                        SellingFeeEnum.PARTIAL_FEE.message,
                        orderDetailCode,
                        calculateFeeReceivers.getPartialFee());
                oldSellingFees.add(fee);
            }
            //Handover
            if (calculateFeeReceivers.getHandoverFee().compareTo(BigDecimal.ZERO) > 0
                    && !existCodes.contains(SellingFeeEnum.HANDOVER_FEE.code)) {
                DetailSellingFee fee = new DetailSellingFee(
                        SellingFeeEnum.HANDOVER_FEE.code,
                        SellingFeeEnum.HANDOVER_FEE.message,
                        orderDetailCode,
                        calculateFeeReceivers.getHandoverFee());
                oldSellingFees.add(fee);
            }
            //Insurance
            if (calculateFeeReceivers.getInsuranceFee().compareTo(BigDecimal.ZERO) > 0
                    && !existCodes.contains(SellingFeeEnum.INSURANCE_FEE.code)) {
                DetailSellingFee fee = new DetailSellingFee(
                        SellingFeeEnum.INSURANCE_FEE.code,
                        SellingFeeEnum.INSURANCE_FEE.message,
                        orderDetailCode,
                        calculateFeeReceivers.getInsuranceFee());
                oldSellingFees.add(fee);
            }
            //Cod
            if (calculateFeeReceivers.getCodFee().compareTo(BigDecimal.ZERO) > 0
                    && !existCodes.contains(SellingFeeEnum.COD_FEE.code)) {
                DetailSellingFee fee = new DetailSellingFee(
                        SellingFeeEnum.COD_FEE.code,
                        SellingFeeEnum.COD_FEE.message,
                        orderDetailCode,
                        calculateFeeReceivers.getCodFee());
                oldSellingFees.add(fee);
            }
            //Other
            if (calculateFeeReceivers.getOtherFee().compareTo(BigDecimal.ZERO) > 0
                    && !existCodes.contains(SellingFeeEnum.OTHER_FEE.code)) {
                DetailSellingFee otherFee = new DetailSellingFee(
                        SellingFeeEnum.OTHER_FEE.code,
                        SellingFeeEnum.OTHER_FEE.message,
                        orderDetailCode,
                        calculateFeeReceivers.getOtherFee());
                oldSellingFees.add(otherFee);
            }
            sellingFeeRepository.saveAll(oldSellingFees);
        } catch (Exception e) {
            logger.info("=====UPDATE SELLING FEE EXCEPTION=====" + orderDetailCode, e);
        }
        return oldSellingFees;
    }

    private void insertHistorySellingFee(BigDecimal orderDetailCode, SellingFeeEnum sellingFeeEnum, BigDecimal newValue, BigDecimal oldValue) {
        HistoryDetailSellingFee historyDetailSellingFee = new HistoryDetailSellingFee();
        historyDetailSellingFee.setOrderDetailCode(orderDetailCode);
        historyDetailSellingFee.setName(sellingFeeEnum.message);
        historyDetailSellingFee.setCode(sellingFeeEnum.code);
        historyDetailSellingFee.setValueNew(newValue);
        historyDetailSellingFee.setValueOld(oldValue);
        historySellingFeeRepository.save(historyDetailSellingFee);
    }

    public void insertReDeliverySpecificFee(SvcOrderDetail orderDetail) {
        BigDecimal orderDetailCode = orderDetail.getSvcOrderDetailCode();
        try {
            //Phí Giao lại
            V2Package v2Package = packageRepository.findV2PackageById(BigDecimal.valueOf(orderDetail.getServicePackId()));
            List<DetailSellingFee> transportFee = sellingFeeRepository
                    .findAllByCodeAndOrderDetailCode(SellingFeeEnum.TRANSPORT_FEE.code, orderDetailCode);
            List<HistoryDetailSellingFee> refundFees = historySellingFeeRepository
                    .findAllByOrderDetailCodeAndCode(orderDetailCode, SellingFeeEnum.REDELIVERY_FEE.code);
            if (transportFee != null && transportFee.size() > 0) {
                CalculateSpecificFeeData feeData = postageService.calculateSpecificFee(orderDetailCode, v2Package.getCode(),
                        SellingFeeEnum.REDELIVERY_FEE.code, refundFees.size() + 1, transportFee.get(0).getValue(), "");
                if (feeData != null) {
                    DetailSellingFee refundFee = sellingFeeRepository.findByCodeAndOrderDetailCode(SellingFeeEnum.REDELIVERY_FEE.code, orderDetailCode);
                    HistoryDetailSellingFee historyDetailSellingFee = new HistoryDetailSellingFee();
                    historyDetailSellingFee.setOrderDetailCode(orderDetailCode);
                    historyDetailSellingFee.setName(SellingFeeEnum.REDELIVERY_FEE.message + " lần " + (refundFees.size() + 1));
                    historyDetailSellingFee.setCode(SellingFeeEnum.REDELIVERY_FEE.code);
                    historyDetailSellingFee.setValueNew(feeData.getFee());
                    if (refundFee != null) {
                        historyDetailSellingFee.setValueOld(refundFee.getValue());
                        historyDetailSellingFee.setValueNew(refundFee.getValue().add(feeData.getFee()));
                    } else historyDetailSellingFee.setValueOld(BigDecimal.ZERO);
                    historySellingFeeRepository.save(historyDetailSellingFee);
                    if (feeData.getFee().compareTo(BigDecimal.ZERO) > 0) {
                        if (refundFee != null) {
                            refundFee.setValueOld(refundFee.getValue());
                            refundFee.setValue(refundFee.getValue().add(feeData.getFee()));
                            sellingFeeRepository.save(refundFee);
                            logger.info("=======REDELIVERY_FEE SAVE TO DB=======" + orderDetailCode + "||" + gson.toJson(refundFee));
                        } else {
                            DetailSellingFee sellingFee = new DetailSellingFee();
                            sellingFee.setValue(feeData.getFee());
                            sellingFee.setValueOld(BigDecimal.ZERO);
                            sellingFee.setName(SellingFeeEnum.REDELIVERY_FEE.message);
                            sellingFee.setOrderDetailCode(orderDetailCode);
                            sellingFee.setCode(SellingFeeEnum.REDELIVERY_FEE.code);
                            DetailSellingFee result = sellingFeeRepository.save(sellingFee);
                            logger.info("=======REDELIVERY_FEE SAVE TO DB=======" + orderDetailCode + "||" + gson.toJson(result));
                        }
                    }

                }
            }
        } catch (Exception e) {
            logger.info("========INSERT REFUND FEE EXCEPTION=======" + orderDetailCode + "|| REFUND_FEE", e);
        }

    }
}
