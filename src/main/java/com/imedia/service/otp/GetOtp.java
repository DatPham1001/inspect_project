package com.imedia.service.otp;

import com.imedia.service.user.model.GetOTPRequest;
import com.imedia.service.user.model.VerifyOTPResponse;

public abstract class GetOtp {
    public abstract VerifyOTPResponse doGetOtp() throws Exception;
}
