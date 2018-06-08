package com.kunlunsoft.redis.util;

import com.kunlunsoft.redis.dto.RedisConnItem;
import com.kunlunsoft.redis.dto.RedisParam;
import redis.clients.jedis.Jedis;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class ConnMgmt {
    public static final int REDIS_PORT = 6379;
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
//            BeanUtils.copyProperties(redisParam, redisConnItem);
            try {
                org.apache.commons.beanutils.BeanUtils.copyProperties(redisConnItem, redisParam);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
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
