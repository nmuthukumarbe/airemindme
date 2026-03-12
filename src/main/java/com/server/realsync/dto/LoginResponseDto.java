package com.server.realsync.dto;

import org.springdoc.api.ErrorMessage;

public class LoginResponseDto {
    public Integer account_id;
    public Long user_id;
    public String email;
    public String token;
    public String errorMessage;
}
