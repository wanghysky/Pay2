package com.sum.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author why
 * @date 2023/11/25 21:27
 */
public class ResetPassword {

    @JsonProperty("accessToken")
    public String accessToken;
    @JsonProperty("loginName")
    public String loginName;
    @JsonProperty("merchantNo")
    private String merchantNo;
    @JsonProperty("password")
    public String password;
}
