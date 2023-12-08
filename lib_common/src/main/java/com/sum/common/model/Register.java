package com.sum.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author why
 * @date 2023/11/23 14:05
 */

public class Register {

    @JsonProperty("accessToken")
    private String accessToken;
    @JsonProperty("appStoreCode")
    private String appStoreCode;
    @JsonProperty("channelCode")
    private String channelCode;
    @JsonProperty("invitationCode")
    private String invitationCode;
    @JsonProperty("loginName")
    public String loginName;
    @JsonProperty("merchantNo")
    private String merchantNo;
    @JsonProperty("password")
    public String password;
    @JsonProperty("registerAddr")
    private String registerAddr;
    @JsonProperty("registerClient")
    public String registerClient;
    @JsonProperty("registerCoordinate")
    private String registerCoordinate;
    @JsonProperty("vcode")
    public String vcode;
}
