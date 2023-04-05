package com.imedia.service.bank;

import com.imedia.config.application.AppConfig;
import com.imedia.model.BaseResponse;
import com.imedia.oracle.reportentity.BankReport;
import com.imedia.oracle.reportrepository.BankReportRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BankService {
    static final Logger logger = LogManager.getLogger(BankService.class);

    private final BankReportRepository bankReportRepository;

    @Autowired
    public BankService(BankReportRepository bankReportRepository) {
        this.bankReportRepository = bankReportRepository;
    }

    public BaseResponse getBankList (String searchString) throws Exception {
        AppConfig appConfig =AppConfig.getInstance();
        List<BankReport> bankReportList = bankReportRepository.getBankList(searchString.replaceAll(" ", ""));
        bankReportList.forEach(b -> b.setImageBank(appConfig.imageUrl.replace("/document","")+ b.getImageBank()));
        return new BaseResponse(200, bankReportList);
    }
}
