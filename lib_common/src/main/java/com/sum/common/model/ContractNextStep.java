package com.sum.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author why
 * @date 2023/11/30 23:58
 */

public class ContractNextStep {
    @JsonProperty("data")
    private DataDTO data;
    @JsonProperty("livenConfig")
    public LivenConfigDTO livenConfig;
    @JsonProperty("ocrComplete")
    private Boolean ocrComplete;
    @JsonProperty("step")
    public String step;

    public static class DataDTO {
    }


    public static class LivenConfigDTO {
        @JsonProperty("livenChannel")
        public String livenChannel;
        @JsonProperty("skipLiven")
        public Boolean skipLiven;
    }
}
