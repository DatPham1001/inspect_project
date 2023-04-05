package com.imedia.schedule;

import com.google.gson.Gson;
import com.imedia.config.application.AppConfig;
import com.imedia.oracle.entity.OrderPartialRequest;
import com.imedia.oracle.entity.SvcOrderDetail;
import com.imedia.oracle.reportrepository.RouteAreaReportRepository;
import com.imedia.oracle.repository.DetailSellingFeeRepository;
import com.imedia.oracle.repository.OrderPartialRequestRepository;
import com.imedia.oracle.repository.SvcOrderDetailRepository;
import com.imedia.service.notify.NotifyService;
import com.imedia.service.order.FindShipService;
import com.imedia.service.order.OrderFileService;
import com.imedia.service.order.OrderLogService;
import com.imedia.service.order.OrderUpdateService;
import com.imedia.service.order.enums.OrderStatusEnum;
import com.imedia.service.order.enums.SellingFeeEnum;
import com.imedia.service.order.model.ChangeCodCallback;
import com.imedia.util.CallRedis;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ScheduleTask {
    static final Logger logger = LogManager.getLogger(ScheduleTask.class);
    static final Gson gson = new Gson();
    private final OrderFileService orderFileService;
    private final FindShipService findShipService;
    private final OrderUpdateService orderUpdateService;
    private final OrderPartialRequestRepository partialRequestRepository;
    private final NotifyService notifyService;
    private final OrderLogService orderLogService;
    private final SvcOrderDetailRepository orderDetailRepository;
    private final DetailSellingFeeRepository sellingFeeRepository;
    private final RouteAreaReportRepository routeAreaReportRepository;

    @Autowired
    public ScheduleTask(OrderFileService orderFileService, FindShipService findShipService, OrderUpdateService orderUpdateService, OrderPartialRequestRepository partialRequestRepository, NotifyService notifyService, OrderLogService orderLogService, SvcOrderDetailRepository orderDetailRepository, DetailSellingFeeRepository sellingFeeRepository, RouteAreaReportRepository routeAreaReportRepository) {
        this.orderFileService = orderFileService;
        this.findShipService = findShipService;
        this.orderUpdateService = orderUpdateService;
        this.partialRequestRepository = partialRequestRepository;
        this.notifyService = notifyService;
        this.orderLogService = orderLogService;
        this.orderDetailRepository = orderDetailRepository;
        this.sellingFeeRepository = sellingFeeRepository;
        this.routeAreaReportRepository = routeAreaReportRepository;
    }

    @Scheduled(fixedDelay = 30000)
    public void reloadConfig() {
        System.out.println("RELOAD CONFIG DONE");
        AppConfig.setInstance(null);
    }


    @Scheduled(fixedDelay = 2000)
    public void createFileOrder() {
        List<String> dataQueues = CallRedis.getBatchQueue("PUSH_FILE_ORDER", 4);
        for (String dataQueue : dataQueues) {
            logger.info("======CREATE FILE ORDER QUEUE DATA=======" + dataQueue);
            try {
                orderFileService.handleCreateFileOrder(dataQueue);
                Thread.sleep(10);
            } catch (Exception e) {
                logger.info("======CREATE FILE ORDER EXCEPTION=======" + dataQueue, e);
                continue;
            }
        }
    }

    @Scheduled(fixedDelay = 2500)
    public void findShipperCallback() {
        List<String> dataQueues = CallRedis.getBatchQueue("hls:queue:holashipOrderResponse", 0);
        for (String dataQueue : dataQueues) {
            logger.info("======FIND SHIP CALBACK QUEUE DATA=======" + dataQueue);
            try {
                findShipService.handleFindShipperOrderCallback(dataQueue);
                Thread.sleep(10);
            } catch (Exception e) {
                logger.info("======FIND SHIPPER CALLBACK EXCEPTION=======" + dataQueue, e);
                continue;
            }
        }
    }

    @Scheduled(fixedDelay = 1000)
    public void confirmChangeCod() {
        List<String> dataQueues = CallRedis.getBatchQueue("CONFIRM_INFO", 0);
        for (String dataQueue : dataQueues) {
            logger.info("======CONFIRM COD CALLBACK=======" + dataQueue);
            try {
                ChangeCodCallback callback = gson.fromJson(dataQueue, ChangeCodCallback.class);
                orderUpdateService.handleConfirmCodCallback(callback);
            } catch (Exception e) {
                logger.info("======CONFIRM COD CALLBACK EXCEPTION=======" + dataQueue, e);
                continue;
            }
        }


    }


    @Scheduled(fixedDelay = 300000)
    public void rejectPartialRequest() {
        try {
            AppConfig appConfig = AppConfig.getInstance();
            List<OrderPartialRequest> partialRequests = partialRequestRepository.getOverWaitRequest(appConfig.partialRequestWait);
            if (partialRequests.size() > 0) {
                for (OrderPartialRequest partialRequest : partialRequests) {
                    SvcOrderDetail orderDetail = orderDetailRepository.getValidBySvcOrderDetailCode(partialRequest.getOrderDetailCode());
                    if (orderDetail != null) {
                        try {
                            partialRequest.setConfirmBy(-1L);
                            partialRequest.setReason("Shop không phản hồi yêu cầu giao hàng 1 phần");
                            partialRequest.setIsConfirmed(2);
                            orderLogService.insertOrderLog(orderDetail, OrderStatusEnum.NOTE_PARTIAL_REJECT_AUTO.message, orderDetail.getStatus());
                            //Notify to app ship
                            notifyService.notifyPartialRequest(partialRequest);
                            sellingFeeRepository.deleteSellingFeeByDetailCode(orderDetail.getSvcOrderDetailCode(), SellingFeeEnum.PARTIAL_FEE.code);
                            logger.info("========PARTIAL REQUEST SYSTEM REJECT=========" + gson.toJson(partialRequest));
                            partialRequestRepository.save(partialRequest);
                        } catch (Exception e) {
                            logger.info("========SCHEDULED REJECT PARTIAL REQUEST EXCEPTION========" + gson.toJson(partialRequest), e);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.info("=======REJECT PARTIAL REQUEST=======", e);
        }

    }
}
