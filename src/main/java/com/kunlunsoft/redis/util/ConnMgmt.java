package com.kunlunsoft.redis.util;

import com.kunlunsoft.redis.dto.RedisConnItem;
import com.kunlunsoft.redis.dto.RedisParam;
import org.springframework.beans.BeanUtils;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;

public class ConnMgmt {
    private Map<String, RedisConnItem> redisParamMap = new HashMap<>();
    private Jedis currentConn;

    public Map<String, RedisConnItem> getRedisParamMap() {
        return redisParamMap;
    }

    public void setRedisParamMap(Map<String, RedisConnItem> redisParamMap) {
        this.redisParamMap = redisParamMap;
    }

    public Jedis connect(String host, final int port) {
        RedisParam redisParam = new RedisParam();
        redisParam.setHost(host)
                .setPort(port);
        String uniqueId = redisParam.getUniqueId();
        RedisConnItem redisParam1 = redisParamMap.get(uniqueId);
        if (null == redisParam1
                || null == redisParam1.getJedis()) {
            Jedis jedis = new Jedis(redisParam.getHost(), redisParam.getPort());
            setCurrentConn(jedis);
            RedisConnItem redisConnItem = new RedisConnItem();
            BeanUtils.copyProperties(redisParam, redisConnItem);
            redisConnItem.setJedis(jedis);
            redisParamMap.put(uniqueId, redisConnItem);
            return jedis;
        }
        return redisParam1.getJedis();
    }

    public Jedis getCurrentConn() {
        return currentConn;
    }

    public void setCurrentConn(Jedis currentConn) {
        this.currentConn = currentConn;
    }
}
