package com.sum.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author why
 * @date 2023/11/25 17:15
 */
public class LoginRequest {

    @JsonProperty("loginName")
    public String loginName;
    @JsonProperty("loginType")
    public String loginType;
    @JsonProperty("merchantNo")
    private String merchantNo;
    @JsonProperty("pwd")
    public String pwd;
    @JsonProperty("vcode")
    private String vcode;
}
