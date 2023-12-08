package com.sum.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author why
 * @date 2023/12/3 21:29
 */

public class Positive {

    @JsonProperty("livenessId")
    public String livenessId;
    @JsonProperty("photoName")
    public String photoName;
    @JsonProperty("tongDunOCRResponseEntity")
    public TongDunOCRResponseEntityDTO tongDunOCRResponseEntity;
    @JsonProperty("url")
    public String url;


    public static class TongDunOCRResponseEntityDTO {
        @JsonProperty("idCardData")
        public IdCardDataDTO idCardData;
        @JsonProperty("success")
        public Boolean success;
        @JsonProperty("taskId")
        public String taskId;
        @JsonProperty("userId")
        public Integer userId;
        @JsonProperty("userMobile")
        public String userMobile;


        public static class IdCardDataDTO {
            @JsonProperty("address")
            public String address;
            @JsonProperty("birthday")
            public String birthday;
            @JsonProperty("birthplace")
            public String birthplace;
            @JsonProperty("bloodType")
            public String bloodType;
            @JsonProperty("expiryDate")
            public String expiryDate;
            @JsonProperty("gender")
            public String gender;
            @JsonProperty("maritalStatus")
            private String maritalStatus;
            @JsonProperty("name")
            public String name;
            @JsonProperty("nationality")
            private String nationality;
            @JsonProperty("nik")
            public String nik;
            @JsonProperty("occupation")
            private String occupation;
            @JsonProperty("religion")
            private String religion;
            @JsonProperty("rtRw")
            private String rtRw;
            @JsonProperty("street")
            private String street;
            @JsonProperty("village")
            private String village;
        }
    }
}
