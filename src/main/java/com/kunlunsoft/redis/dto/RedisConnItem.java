package com.kunlunsoft.redis.dto;

import redis.clients.jedis.Jedis;

public class RedisConnItem extends RedisParam {
    private Jedis jedis;

    public Jedis getJedis() {
        return jedis;
    }

    public RedisParam setJedis(Jedis jedis) {
        this.jedis = jedis;
        return this;
    }
}
