package com.kunlunsoft.dto;

import lombok.Data;

@Data
public class RedisKeyValDto {
    private String queryKey;
    private String queryId;

    private String saveKey;
    private String saveId;
    private String saveValue;
}
