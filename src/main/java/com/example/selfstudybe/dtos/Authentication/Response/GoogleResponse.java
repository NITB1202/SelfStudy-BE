package com.example.selfstudybe.dtos.Authentication.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class GoogleResponse {
    private String access_token;
    private int expires_in;
    private String scope;
    private String token_type;
    private String id_token;
}
