package com.sum.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author why
 * @date 2023/11/22 00:01
 */
public class SmsCount {

    @JsonProperty("merchantNo")
    public String merchantNo;
    @JsonProperty("phone")
    public String phone;
    @JsonProperty("type")
    public String type;
    @JsonProperty("voiceCode")
    public Boolean voiceCode;
}
