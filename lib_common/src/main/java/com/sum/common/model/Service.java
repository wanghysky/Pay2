package com.sum.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author why
 * @date 2023/12/10 17:39
 */
public class Service {

    @JsonProperty("email")
    public String email;
    @JsonProperty("phone")
    public String phone;
    @JsonProperty("whatsApp")
    public String whatsApp;
}
