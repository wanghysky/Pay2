package com.sum.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author why
 * @date 2023/12/1 22:34
 */
public class DistList {

    @JsonProperty("dictList")
    public List<DictListDTO> dictList;


    public static class DictListDTO {
        @JsonProperty("productId")
        public Integer productId;
        @JsonProperty("remark")
        public String remark;
        @JsonProperty("typeCode")
        public String typeCode;
        @JsonProperty("typeName")
        public String typeName;
    }
}
