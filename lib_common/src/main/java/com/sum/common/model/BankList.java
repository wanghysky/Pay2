package com.sum.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author why
 * @date 2023/12/8 00:44
 */
public class BankList {


    @JsonProperty("banks")
    public List<BanksDTO> banks;

    public static class BanksDTO {
        @JsonProperty("bankCode")
        public String bankCode;
        @JsonProperty("bankName")
        public String bankName;
    }
}
