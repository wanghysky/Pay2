package com.sum.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * @author why
 * @date 2023/12/2 00:45
 */

public class SaveUserInfo {

    @JsonProperty("data")
    public Map<String, String> data;
    @JsonProperty("step")
    public String step;
}
