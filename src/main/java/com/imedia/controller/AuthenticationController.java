package com.imedia.controller;

import com.google.gson.Gson;
import com.imedia.service.authenticate.AuthenticateService;
import com.imedia.service.authenticate.model.SignInRequest;
import com.imedia.service.authenticate.model.SignInResponse;
import com.imedia.util.PreLoadStaticUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Objects;

@RestController
@CrossOrigin
@RequestMapping("/api/public")
public class AuthenticationController {
    static final Logger logger = LogManager.getLogger(AuthenticationController.class);
    static final Gson gson = new Gson();
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AuthenticateService authenticateService;

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody SignInRequest authenticationRequest)
            throws Exception {
        logger.info("====LOGIN REQUEST==== " + gson.toJson(authenticationRequest));
        SignInResponse validated = validateInput(authenticationRequest);
        if (validated == null) {
            try {
                SignInResponse signInResponse = null;
                if (authenticationRequest.getLoginFrom() == 0)
                    signInResponse = authenticateService.login(authenticationRequest);

                if (authenticationRequest.getLoginFrom() == 1 || authenticationRequest.getLoginFrom() == 2) {
                    signInResponse = authenticateService.loginSocial(authenticationRequest);
                }
//                signInResponse.setUserInfo(null);
                //Model for mobile
                if (signInResponse != null) {
                    if (signInResponse.getStatus() == 200) {
                        HashMap<String, String> data = new HashMap<>();
                        data.put("token", signInResponse.getToken());
                        data.put("sessionKey", signInResponse.getSessionKey());
                        data.put("username", signInResponse.getUsername());
                        signInResponse.setData(data);
                    } else
                        signInResponse.setCode(signInResponse.getStatus());
                    logger.info("====LOGIN RESPONSE==== " + gson.toJson(signInResponse));
                    return ResponseEntity.ok(signInResponse);
                } else return ResponseEntity.ok(new SignInResponse(PreLoadStaticUtil.errorCodeWeb.get(500).getMessage(), 500));

            } catch (Exception e) {
                logger.info("====LOGIN EXCEPTION==== " + gson.toJson(authenticationRequest), e);
                return ResponseEntity.ok(new SignInResponse(PreLoadStaticUtil.errorCodeWeb.get(500).getMessage(), 500));
            }
        } else {
            logger.info("====LOGIN RESPONSE==== " + gson.toJson(validated));
            return ResponseEntity.ok(validated);
        }
//        final String token = jwtTokenUtil.generateToken(userDetails);
    }

    private void authenticate(String username, String password) throws Exception {
        Objects.requireNonNull(username);
        Objects.requireNonNull(password);

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }

    private SignInResponse validateInput(SignInRequest signInRequest) {
        SignInResponse signInResponse = null;
        if (signInRequest.getDeviceId() == null || signInRequest.getDeviceId().isEmpty()) {
            signInResponse = new SignInResponse(PreLoadStaticUtil.errorCodeWeb.get(666).getMessage(), 666);
        }
        if (signInRequest.getLoginFrom() == 1 || signInRequest.getLoginFrom() == 2) {
            if (signInRequest.getAccessToken() == null
                    || signInRequest.getAccessToken().isEmpty()
                    || signInRequest.getSocialId() == null || signInRequest.getSocialId().isEmpty()) {
                signInResponse = new SignInResponse(PreLoadStaticUtil.errorCodeWeb.get(666).getMessage(), 666);
            }
        }
        if (signInRequest.getLoginFrom() == 0) {
            if (signInRequest.getUsername() == null
                    || signInRequest.getUsername().isEmpty()
                    || signInRequest.getPassword() == null || signInRequest.getPassword().isEmpty()) {
                signInResponse = new SignInResponse(PreLoadStaticUtil.errorCodeWeb.get(666).getMessage(), 666);
            }
        }
        return signInResponse;
    }
}
