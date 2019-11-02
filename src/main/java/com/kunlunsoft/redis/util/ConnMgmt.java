package com.kunlunsoft.redis.util;

import com.kunlunsoft.redis.dto.RedisConnItem;
import com.kunlunsoft.redis.dto.RedisParam;
import com.string.widget.util.ValueWidget;
import com.swing.messagebox.GUIUtil23;
import redis.clients.jedis.Jedis;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class ConnMgmt {
    /**
     * redis 默认端口号
     */
    public static final int REDIS_PORT = 6379;
    private Map<String, RedisConnItem> redisParamMap = new HashMap<>();
    /***
     * 当前的redis 连接
     */
    private Jedis currentConn;
    private RedisParam currentRedisParam;

    public Map<String, RedisConnItem> getRedisParamMap() {
        return redisParamMap;
    }

    public void setRedisParamMap(Map<String, RedisConnItem> redisParamMap) {
        this.redisParamMap = redisParamMap;
    }

    public Jedis connect(String host, String password, final int port, boolean force) {
        RedisParam redisParam = new RedisParam();
        redisParam.setHost(host)
                .setPort(port)
                .setPassword(password);
        String uniqueId = redisParam.getUniqueId();
        RedisConnItem redisParam1 = redisParamMap.get(uniqueId);
        if (force || null == redisParam1
                || null == redisParam1.getJedis()) {
            Jedis jedis = jedis = new Jedis(redisParam.getHost(), redisParam.getPort());
            currentRedisParam = redisParam;
            try {
                if (ValueWidget.isNullOrEmpty(password)) {
                    jedis.connect();
                    //                jedis = new Jedis(redisParam.getHost(), redisParam.getPort());
                } else {
                    //                jedis = new Jedis(redisParam.getHost(), redisParam.getPassword(), redisParam.getPort());
                    jedis.auth(redisParam.getPassword());
                }
            } catch (Exception e) {
                e.printStackTrace();
                GUIUtil23.errorDialog(e);
                return null;
            }
            String msg = "连接redis...ip:" + redisParam.getHost();
            System.out.println(msg);
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
        String msg = "不用重新连接";
        System.out.println(msg);
        setCurrentConn(redisParam1.getJedis());
        return redisParam1.getJedis();
    }

    public Jedis getCurrentConn() {
        return currentConn;
    }

    public void setCurrentConn(Jedis currentConn) {
        this.currentConn = currentConn;
    }

    public RedisParam getCurrentRedisParam() {
        return currentRedisParam;
    }

    public void setCurrentRedisParam(RedisParam currentRedisParam) {
        this.currentRedisParam = currentRedisParam;
    }
}
