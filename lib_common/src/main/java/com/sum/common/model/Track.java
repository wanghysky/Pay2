package com.sum.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author why
 * @date 2023/12/10 22:46
 */
public class Track {

    @JsonProperty("adid")
    private String adid;
    @JsonProperty("ctime")
    private String ctime;
    @JsonProperty("deviceNo")
    private String deviceNo;
    @JsonProperty("endTime")
    public String endTime;
    @JsonProperty("ip")
    private String ip;
    @JsonProperty("latitude")
    private String latitude;
    @JsonProperty("longitude")
    private String longitude;
    @JsonProperty("merchantNo")
    private String merchantNo;
    @JsonProperty("packageName")
    private String packageName;
    @JsonProperty("sceneType")
    public String sceneType;
    @JsonProperty("startTime")
    public String startTime;
    @JsonProperty("userId")
    private Integer userId;
    @JsonProperty("userRandomKey")
    private String userRandomKey;
}
