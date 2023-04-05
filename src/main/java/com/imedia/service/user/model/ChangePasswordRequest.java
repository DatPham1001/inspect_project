package com.imedia.service.user.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordRequest {
    private String username;
    private String sessionKey;
    private String oldPassword;
    private String newPassword;
}
