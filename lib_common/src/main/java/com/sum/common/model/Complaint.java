package com.sum.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author why
 * @date 2023/12/10 17:51
 */
public class Complaint {

    @JsonProperty("content")
    public String content;
    @JsonProperty("title")
    public String title;
}
