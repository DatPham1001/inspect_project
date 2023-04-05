package com.imedia.service.userwallet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.imedia.config.application.AppConfig;
import com.imedia.model.BaseResponse;
import com.imedia.oracle.dao.UserWalletDAO;
import com.imedia.oracle.dao.WalletLogsDAO;
import com.imedia.oracle.entity.*;
import com.imedia.oracle.repository.*;
import com.imedia.service.user.model.BankAccountResponse;
import com.imedia.service.userwallet.model.*;
import com.imedia.service.wallet.WalletService;
import com.imedia.service.wallet.model.WalletBaseResponse;
import com.imedia.service.wallet.model.WalletRegisterVAResponse;
import com.imedia.service.wallet.model.WalletUserInfo;
import com.imedia.util.*;
import ma.glasnost.orika.MapperFacade;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class UserWalletService {
    static final Logger logger = LogManager.getLogger(UserWalletService.class);
    static final Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
    private final WalletService walletService;
    private final VaWalletRepository vaWalletRepository;
    private static final HashMap<Integer, ErrorCodesWebshop> errorCodes = PreLoadStaticUtil.errorCodeWeb;
    private final AppUserRepository appUserRepository;
    private final BankAccountRepository bankAccountRepository;
    private final UserWalletDAO userWalletDAO;
    private final MapperFacade mapperFacade;
    private final WalletLogsDAO walletLogsDAO;
    private final BankRepository bankRepository;
    private final WalletLogRepository walletLogRepository;

    @Autowired
    public UserWalletService(WalletService walletService, VaWalletRepository vaWalletRepository, AppUserRepository appUserRepository, BankAccountRepository bankAccountRepository, UserWalletDAO userWalletDAO, MapperFacade mapperFacade, WalletLogsDAO walletLogsDAO, BankRepository bankRepository, WalletLogRepository walletLogRepository) {
        this.walletService = walletService;
        this.vaWalletRepository = vaWalletRepository;
        this.appUserRepository = appUserRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.userWalletDAO = userWalletDAO;
        this.mapperFacade = mapperFacade;
        this.walletLogsDAO = walletLogsDAO;
        this.bankRepository = bankRepository;
        this.walletLogRepository = walletLogRepository;
    }


    static final List<Integer> withdrawFailCode = Arrays.asList(103, 104, 105, 108, 116, 115, 117, 138, 143, 135, 120);


    public BaseResponse createVA(UserRegisterVARequest userRegisterVARequest) throws Exception {
        AppConfig appConfig = AppConfig.getInstance();
        AppUser oldAppUser = appUserRepository.findByPhone(userRegisterVARequest.getUsername());
        VaWallet oldVaWallet = vaWalletRepository.findByUserId(oldAppUser.getId());
        if (oldVaWallet != null)
            return new BaseResponse(507, oldVaWallet);
        WalletBaseResponse walletBaseResponse = null;
        try {
            walletBaseResponse = walletService.createVA(userRegisterVARequest);
        } catch (Exception e) {
            logger.info("========REGISTER VA EXCEPTION======" + userRegisterVARequest.getUsername(), e);
        }
        if (walletBaseResponse != null) {
            if (walletBaseResponse.getStatus() == 200) {
                //Create VA account
                String data = TripleDES.decrypt(appConfig.epurse_key, walletBaseResponse.getData());
                WalletRegisterVAResponse vaResponse = gson.fromJson(data, WalletRegisterVAResponse.class);
                vaWalletRepository.insertVaWallet(oldAppUser.getId(), vaResponse.getAccount_no(), vaResponse.getAccount_name(),
                        vaResponse.getBank_code(), vaResponse.getBank_name());
                //Build response
                VaWallet vaWallet = new VaWallet();
                Bank bank = bankRepository.findByBankCode(vaResponse.getBank_code());
                if (bank != null) {
                    vaWallet.setBankName(bank.getName());
                    vaWallet.setBankCode(bank.getBankCode());
                }
                vaWallet.setUserName(vaResponse.getAccount_name());
                vaWallet.setShopCode(vaResponse.getAccount_no());
                HashMap<String, Object> resData = new HashMap<>();
                resData.put("username", userRegisterVARequest.getUsername());
                resData.put("customerName", "CONG TY CP CONG NGHE VA DICH VU IMEDIA");
                resData.put("va", vaWallet);
                return new BaseResponse(200, resData);
            } else
                return new BaseResponse(walletBaseResponse.getStatus());
        }
        return new BaseResponse(512);
    }

    public BaseResponse addBankAccount(AddBankWithdrawAccountRequest bankWithdrawAccountRequest) throws Exception {
        AppConfig appConfig = AppConfig.getInstance();
        AppUser oldAppUser = appUserRepository.findByPhone(bankWithdrawAccountRequest.getUsername());
        List<BankAccount> oldBankAccount =
                bankAccountRepository.findAllByBankAccountAndAppUserIdAndIsDeleted(bankWithdrawAccountRequest.getAccountNo(), oldAppUser.getId(), 1);
        if (oldBankAccount.size() > 0) {
            return new BaseResponse(509);
        }
        //Build Firm request
        FirmBankAddAccountRequest firmBankAddAccountRequest = new FirmBankAddAccountRequest();
        firmBankAddAccountRequest.setRequestId(GenerateTransactionId.GetInstance().GeneratePartnerTransactionId("CBC_"));
        firmBankAddAccountRequest.setBankNo(bankWithdrawAccountRequest.getBankCode());
        firmBankAddAccountRequest.setAccNo(bankWithdrawAccountRequest.getAccountNo());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        firmBankAddAccountRequest.setRequestTime(dateFormat.format(new Date()));
        firmBankAddAccountRequest.setAccType(String.valueOf(bankWithdrawAccountRequest.getAccountNoType()));
        firmBankAddAccountRequest.setOperation(9001);
        firmBankAddAccountRequest.setPartnerCode(appConfig.firmbank_partnercode);
        firmBankAddAccountRequest.setSignature("");
        try {
            String response = CallServer.getInstance().post(appConfig.firmbank_url, gson.toJson(firmBankAddAccountRequest));
            if (response == null) {
                return new BaseResponse(513);
            }
            FirmBankAddAccountResponse firmBankAddAccountResponse = gson.fromJson(response, FirmBankAddAccountResponse.class);
            //Success
            if (firmBankAddAccountResponse.getResponseCode() == 200) {
                //Check bank username
                String accUserNameRq = bankWithdrawAccountRequest.getAccUsername().trim().replaceAll(" ", "");
                String accUserNameRs = firmBankAddAccountResponse.getAccName().trim().replaceAll(" ", "");
                if (!accUserNameRq.equalsIgnoreCase(accUserNameRs))
                    return new BaseResponse(511);
                //Create bank
                BankAccount bankAccount = new BankAccount();
                bankAccount.setBankAccount(bankWithdrawAccountRequest.getAccountNo());
                bankAccount.setBankAccountName(firmBankAddAccountResponse.getAccName());
                bankAccount.setStatus(1);
                bankAccount.setPhone(oldAppUser.getPhone());
                bankAccount.setRoleType(BigDecimal.valueOf(0));
                bankAccount.setBankCode(bankWithdrawAccountRequest.getBankCode());
                bankAccount.setAppUserId(oldAppUser.getId());
                bankAccount.setType(bankWithdrawAccountRequest.getAccountNoType());
                bankAccount.setIsDeleted(1);
                bankAccount.setWithdrawType(0);
                bankAccountRepository.save(bankAccount);
                BankAccountResponse bankAccountResponse = mapperFacade.map(bankAccount, BankAccountResponse.class);
                Bank bank = bankRepository.findByBankCode(bankAccount.getBankCode());
                bankAccountResponse.setImageBank(
                        AppConfig.getInstance().imageUrl.replace("/document", "")
                                + bank.getImageBank());
                bankAccountResponse.setBankName(bank.getName());
                bankAccountResponse.setBankCode(bank.getBankCode());
                bankAccountResponse.setBankShortName(bank.getCode());
                return new BaseResponse(200, bankAccountResponse);
            }
            if (firmBankAddAccountResponse.getResponseCode() == 11) {
                return new BaseResponse(508);
            }
        } catch (Exception e) {
            logger.info("=======ADD BANK ACCOUNT EXCEPTION======" + gson.toJson(bankWithdrawAccountRequest), e);
        }
        return new BaseResponse(500);
    }

    public BaseResponse getListBankAccount(String username, Integer withdrawType) {
        try {
            AppUser appUser = appUserRepository.findByPhone(username);
            AppConfig appConfig = AppConfig.getInstance();
            HashMap<String, Object> response = new HashMap<>();
            response.put("transactionFee", appConfig.bankFee);
            if (appUser.getAllowWithdraw() != null && appUser.getAllowWithdraw() == 0) {
                response.put("allowWithdraw", 0);
            } else response.put("allowWithdraw", 1);
            UserBalanceResponse userBalanceResponse = getBalances(username);
            if (userBalanceResponse.getAvailableBalance().compareTo(appConfig.maxWithdraw) > 0) {
                response.put("maxWithdraw", appConfig.maxWithdraw);
            } else response.put("maxWithdraw", userBalanceResponse.getAvailableBalance());
            response.put("minxWithdraw", appConfig.minWithdraw);
            List<BankAccountDTO> bankAccounts = userWalletDAO.getBankAccounts(username, withdrawType);
            List<BankAccountResponse> bankAccountResponses = new ArrayList<>();
            for (BankAccountDTO bankAccount : bankAccounts) {
                BankAccountResponse bankAccountResponse = mapperFacade.map(bankAccount, BankAccountResponse.class);
                Bank bank = bankRepository.findByBankCode(bankAccount.getBankCode());
                bankAccountResponse.setImageBank(
                        AppConfig.getInstance().imageUrl.replace("/document", "")
                                + bank.getImageBank());
                bankAccountResponse.setBankName(bank.getName());
                bankAccountResponse.setBankCode(bank.getBankCode());
                bankAccountResponse.setBankShortName(bank.getCode());
                bankAccountResponses.add(bankAccountResponse);
            }
            response.put("banks", bankAccountResponses);
            return new BaseResponse(200, response);
        } catch (Exception e) {
            logger.info("=======GET LIST BANK ACCOUNT EXCEPTION======" + username, e);
            return new BaseResponse(500);
        }
    }

    public synchronized NotifyBalanceResponse depositVA(NotifyBalanceRequest notifyBalanceRequest) throws Exception {
        //Verify
//        boolean verify = verifySignature(notifyBalanceRequest);
//        if (verify) {
        //Call Wallet
        VaWallet vaWallet = vaWalletRepository.findByShopCode(notifyBalanceRequest.getAccountNumber());
        if (vaWallet != null) {
            AppUser appUser = appUserRepository.findAppUserById(vaWallet.getUserId());
            if (appUser != null) {
                //Check transId
                WalletLog oldLog = walletLogRepository.findByCode(notifyBalanceRequest.getRequestId());
                if (oldLog == null)
                    walletService.depositVA(notifyBalanceRequest, appUser);
                else logger.info("=========DUPLICATE REQUEST=========" + notifyBalanceRequest.getRequestId());
            }
        } else {
            logger.info("==========CANNOT FIND VA OR USER=========" + gson.toJson(notifyBalanceRequest));
        }
//        } else logger.info("========SIGNATURE VERIFY FALSE========" + gson.toJson(notifyBalanceRequest));
        return new NotifyBalanceResponse("200", "Thành công", null);
    }


    public BaseResponse filterWalletLogs(FilterWalletLogRequest filterWalletLogRequest) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        //Validate
        try {
            if (filterWalletLogRequest.getOrderId() == null || filterWalletLogRequest.getOrderId().isEmpty())
                filterWalletLogRequest.setOrderId("");
            if (filterWalletLogRequest.getStatus() == null)
                filterWalletLogRequest.setStatus(-1);
            try {
                if (filterWalletLogRequest.getFromDate() == null || filterWalletLogRequest.getFromDate().isEmpty())
                    filterWalletLogRequest.setFromDate("");
                else {
                    simpleDateFormat.parse(filterWalletLogRequest.getFromDate());
                }
                if (filterWalletLogRequest.getToDate() == null || filterWalletLogRequest.getToDate().isEmpty())
                    filterWalletLogRequest.setToDate("");
                else {
                    simpleDateFormat.parse(filterWalletLogRequest.getToDate());
                }
            } catch (ParseException e) {
                return new BaseResponse(400);
            }
            if (filterWalletLogRequest.getPage() == null || filterWalletLogRequest.getPage() == 0 || filterWalletLogRequest.getPage() < 0)
                filterWalletLogRequest.setPage(0);
            else filterWalletLogRequest.setPage(filterWalletLogRequest.getPage() - 1);
            if (filterWalletLogRequest.getSize() == null)
                filterWalletLogRequest.setSize(10);
        } catch (Exception e) {
            return new BaseResponse(400, e);
        }
        List<FilterWalletLogDataResponse> filterWalletLogDataResponses = walletLogsDAO.filterWalletLogs(filterWalletLogRequest);
        BigDecimal total = walletLogsDAO.countFilterWalletLogs(filterWalletLogRequest);
        //Build response
        return new BaseResponse(200, new FilterWalletLogResponse(total.intValue(),
                filterWalletLogRequest.getPage() + 1, filterWalletLogRequest.getSize(), filterWalletLogDataResponses));
    }

    public BaseResponse getVA(String username) {
        try {
            AppUser appUser = appUserRepository.findByPhone(username);
            VaWallet vaWallet = vaWalletRepository.findByUserId(appUser.getId());
            return new BaseResponse(200, vaWallet);
        } catch (Exception e) {
            logger.info("======GET VA EXCEPTION=-=======", e);
            return new BaseResponse(500);
        }
    }

    public BaseResponse deleteBankAccount(String username, BigDecimal id) {
        try {
            BankAccount bankAccount = bankAccountRepository.findByIdAndPhone(id, username);
            if (bankAccount != null) {
                bankAccount.setIsDeleted(0);
                bankAccountRepository.save(bankAccount);
                return new BaseResponse(200);
            }
        } catch (Exception e) {
            logger.info("=======DELETE BANK ACCOUNT EXCEPTION======" + username + "||" + id, e);
        }
        return new BaseResponse(500);
    }

    public UserBalanceResponse getBalances(String username) {
        try {
            AppConfig appConfig = AppConfig.getInstance();
            WalletUserInfo userInfo = walletService.getUserInfo(username);
            if (userInfo != null && userInfo.getBalance() != null && userInfo.getBalance().size() > 0) {
                UserBalanceResponse userBalanceResponse = new UserBalanceResponse();
                userBalanceResponse.setUsername(username);
                userInfo.getBalance().forEach(b -> {
                    if (b.getBal_code().equals(appConfig.mainStackCode)) {
                        userBalanceResponse.setRemainBalance(b.getRemain_balance());
                        userBalanceResponse.setHoldBalance(b.getHolding_balance());
                        userBalanceResponse.setCreditBalance(b.getAdvance_balance_before());
                        userBalanceResponse.setDebt(b.getDebt_balance_before());
                        BigDecimal availableHold = userBalanceResponse.getRemainBalance().add(userBalanceResponse.getCreditBalance())
                                .subtract(userBalanceResponse.getDebt().add(userBalanceResponse.getHoldBalance()));
                        userBalanceResponse.setAvailableHold(availableHold);
                    }
                    if (b.getBal_code().equals(appConfig.rewardPointStackCode)) {
                        userBalanceResponse.setPoint(b.getRemain_balance());
                    }
                });
                BigDecimal availableBalance = userBalanceResponse.getRemainBalance()
                        .subtract(userBalanceResponse.getHoldBalance())
                        .subtract(userBalanceResponse.getDebt());
                userBalanceResponse.setAvailableBalance(availableBalance);
                walletService.updateBalanceCache(username, userBalanceResponse.getRemainBalance());
                return userBalanceResponse;
            }
        } catch (Exception e) {
            logger.info("=======GET BALANCES EXCEPTION======" + username, e);
        }
        return null;
    }

    public BaseResponse withdrawBank(UserWithdrawRequest withdrawRequest) throws Exception {
        AppConfig appConfig = AppConfig.getInstance();
        AppUser appUser = appUserRepository.findByPhone(withdrawRequest.getUsername());
        if (appUser != null) {
            if (appUser.getAllowWithdraw() != null && appUser.getAllowWithdraw() == 0)
                return new BaseResponse(517);
            List<BankAccount> bankAccounts = bankAccountRepository.findAllByBankAccountAndAppUserIdAndBankCodeAndIsDeleted(withdrawRequest.getBankAccount(),
                    appUser.getId(), withdrawRequest.getBankCode(), 1);
            Bank bank = bankRepository.findByBankCode(withdrawRequest.getBankCode());
            if (bankAccounts.size() > 0 && bank != null) {
                if (bankAccounts.size() > 1) {
                    try {
                        for (int i = 1; i < bankAccounts.size(); i++)
                            bankAccountRepository.deleteBankAccount(BigDecimal.valueOf(bank.getId()));
                    } catch (Exception e) {
                        logger.info("=====DELETE BANK ACCOUNT====", e);
                    }
                }
                BankAccount bankAccount = bankAccounts.get(0);
                try {
                    //Check max min withdraw
                    if (withdrawRequest.getAmount().compareTo(appConfig.maxWithdraw) > 0)
                        return new BaseResponse(515);
                    if (withdrawRequest.getAmount().compareTo(appConfig.minWithdraw) < 0)
                        return new BaseResponse(516);
                    UserBalanceResponse userInfo = getBalances(withdrawRequest.getUsername());
                    if (userInfo != null) {
                        //Phi ngoai
                        if (appConfig.bankFeeType == 2) {
                            BigDecimal totalAmount = withdrawRequest.getAmount().add(appConfig.bankFee);
                            if (userInfo.getRemainBalance().compareTo(totalAmount) >= 0) {
                                String requestId = GenerateTransactionId.GetInstance().GeneratePartnerTransactionId("WDB");
                                return walletService.withdrawBank(withdrawRequest, bankAccount, requestId, bank, totalAmount, appUser);
                            } else return new BaseResponse(514);
                        }
                        //Phi trong
                        if (appConfig.bankFeeType == 1) {
                            if (userInfo.getRemainBalance().compareTo(withdrawRequest.getAmount()) >= 0) {
                                String requestId = GenerateTransactionId.GetInstance().GeneratePartnerTransactionId("WDB");
                                return walletService.withdrawBank(withdrawRequest, bankAccount, requestId, bank, withdrawRequest.getAmount().subtract(appConfig.bankFee), appUser);
                            } else return new BaseResponse(514);
                        }
                    } else return new BaseResponse(513);
                } catch (Exception e) {
                    logger.info("=======WITH DRAW BANK EXCEPTION======" + withdrawRequest.getUsername(), e);
                    return new BaseResponse(500);
                }
            } else return new BaseResponse(510);
        }
        return new BaseResponse(101);
    }

//    public static void main(String[] args) throws UnsupportedEncodingException, SignatureException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException {
//        String text = "963104800937|50000|CONG TY CP CONG NGHE VA DICH VU IMEDIA|TRANS20210930164057079zXU3";
//        String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDvjdYcbcczm162rSiraZVeNiREtFixdcfYEFsZcXx2b5Z7MBw7shiXExhLNRXCgZrDLwfOHsbjMQvd1XsqvNieAA2WtCCrEPKVtffiTM9Gfr1T1YMEdofiiQz6VwoysOR0mZG4JtqCerAQhCk8uPQYyNEZ9FeF4uditDJF1";
//        String sign = "HbRWFFZnxEiP1n0ubgaioSAR8MFq5hIcIGfbHhT03CHpFzPD7BRi4KAvPL6/0wSvpIBinOxoe1nHapEpVnBICLEDi3J7ICK/wx47GR4ZCAClR8eC7dyBZ+Td851c5eeAjWbG5aFPOASczMPTxt6Wzetg65IQFNI+0Qt4dh2m+EA\u003d";
//        System.out.println(RSAUtil.verify(publicKey, text, sign));
//
////        String sign2 = RSAUtil.sign(publicKey,)
//    }


    private boolean verifySignature(NotifyBalanceRequest request) {
        try {
            AppConfig appConfig = AppConfig.getInstance();
            logger.info(appConfig.firmbank_thuho_publicKey);
            logger.info(appConfig.firmbank_thuho_privateKey);
            String plaintext = request.getAccountNumber() + "|" +
                    request.getChargeAmount() +
                    "|" +
                    request.getAccountName() +
                    "|" +
                    request.getRequestId();
            return RSAUtil.verify(appConfig.firmbank_thuho_publicKey, plaintext, request.getSignature());
        } catch (Exception e) {
            logger.info("=========VERIFY EXCEPTION=========" + gson.toJson(request), e);
        }
        return false;
    }

    public static void main(String[] args) throws Exception {
        String a = "{\"username\":\"0392912024\",\"password\":\"*****\",\"newpassword\":\"*****\",\"remain_balance\":13999,\"holding_balance\":13999,\"bonus_bal\":0,\"require_change_pass\":0,\"login_from\":0,\"client_identity_str\":\"\",\"email\":\"phuongnamuiu1234@gmail.com\",\"phone\":\"0392912024\",\"session_key\":\"*****\",\"birthday\":\"19000101\",\"balance\":[{\"bal_code\":\"3696c56a1836f1c2bef27f2c35e4dbe7\",\"remain_balance\":0,\"holding_balance\":0,\"latest_update\":\"2021/09/30 11:02:41\",\"debt_balance_before\":0,\"debt_update\":\"2021/09/30 11:02:41\",\"advance_balance_before\":0,\"advance_update\":\"2021/09/30 11:02:41\"},{\"bal_code\":\"73017ed33b64273d17b85efaa30e4492\",\"remain_balance\":36315,\"holding_balance\":902000,\"latest_update\":\"2021/10/01 00:57:25\",\"debt_balance_before\":0,\"debt_update\":\"2021/09/30 11:02:40\",\"advance_balance_before\":2000000,\"advance_update\":\"2021/10/04 09:18:41\"}],\"account_epurse_id\":4647}";
        WalletUserInfo userInfo = gson.fromJson(a, WalletUserInfo.class);
        AppConfig appConfig = AppConfig.getInstance();
        UserBalanceResponse userBalanceResponse = new UserBalanceResponse();
        userBalanceResponse.setUsername(userInfo.getUsername());
        userInfo.getBalance().forEach(b -> {
            try {
                if (b.getBal_code().equals(appConfig.mainStackCode)) {
                    userBalanceResponse.setRemainBalance(b.getRemain_balance());
                    userBalanceResponse.setHoldBalance(b.getHolding_balance());
                    userBalanceResponse.setCreditBalance(b.getAdvance_balance_before());
                    userBalanceResponse.setDebt(b.getDebt_balance_before());
                    BigDecimal availableHold = userBalanceResponse.getRemainBalance().add(userBalanceResponse.getCreditBalance())
                            .subtract(userBalanceResponse.getDebt().add(userBalanceResponse.getHoldBalance()));
                    userBalanceResponse.setAvailableHold(availableHold);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (b.getBal_code().equals(appConfig.rewardPointStackCode)) {
                userBalanceResponse.setPoint(b.getRemain_balance());
            }
        });
        BigDecimal availableBalance = userBalanceResponse.getRemainBalance()
                .subtract(userBalanceResponse.getHoldBalance())
                .subtract(userBalanceResponse.getDebt());
        userBalanceResponse.setAvailableBalance(availableBalance);
        System.out.println(gson.toJson(userBalanceResponse));
//        walletService.updateBalanceCache(us, userBalanceResponse.getRemainBalance());
    }
}