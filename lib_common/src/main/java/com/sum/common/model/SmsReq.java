package com.sum.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author why
 * @date 2023/11/22 00:25
 */
public class SmsReq {

    @JsonProperty("merchantNo")
    public String merchantNo;
    @JsonProperty("phone")
    public String phone;
    @JsonProperty("type")
    public String type;
    @JsonProperty("voiceCode")
    public Boolean voiceCode;
    @JsonProperty("imageCode")
    public String imageCode;
    @JsonProperty("uniquCode")
    public String uniquCode;
}
