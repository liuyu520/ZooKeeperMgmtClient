package com.kunlunsoft.dto;

import com.kunlunsoft.redis.dto.RedisParam;
import lombok.Data;

@Data
public class ConfigGlobalDTO {
    private RedisKeyValDto redisKeyValDto;
    private RedisParam redisParam;
}
