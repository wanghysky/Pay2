package com.sum.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author why
 * @date 2023/12/1 00:32
 */
public class AwardAmount {


    @JsonProperty("amount")
    public String amount;
    @JsonProperty("step")
    public String step;
}
