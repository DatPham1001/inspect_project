package com.imedia.schedule;

import com.imedia.config.application.AppConfig;
import com.imedia.oracle.entity.WalletLog;
import com.imedia.oracle.repository.WalletLogRepository;
import com.imedia.service.wallet.WalletService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CheckPendingTransaction {
    private final WalletLogRepository walletLogRepository;
    private final WalletService walletService;
    static final Logger logger = LogManager.getLogger(CheckPendingTransaction.class);

    @Autowired
    public CheckPendingTransaction(WalletLogRepository walletLogRepository, WalletService walletService) {
        this.walletLogRepository = walletLogRepository;
        this.walletService = walletService;
    }

    @Scheduled(fixedDelay = 60000)
    public void checkPending() throws Exception {
        List<WalletLog> walletLogList = walletLogRepository.getPendingLog();
        if (walletLogList.size() > 0 && AppConfig.getInstance().checkPending == 1) {
            for (WalletLog walletLog : walletLogList) {
                try {
                    logger.info("======CHECK PENDING======"+walletLog.getCode());
                    walletService.checkPendingTransaction(walletLog);
                } catch (Exception e) {
                    logger.info("======CHECK PEDNING EXCEPTION======" + walletLog.getCode(), e);
                }
            }
        }
    }
}
