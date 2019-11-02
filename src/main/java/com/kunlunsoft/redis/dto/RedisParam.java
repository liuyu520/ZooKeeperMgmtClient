package com.kunlunsoft.redis.dto;

public class RedisParam {
    private String host;
    private Integer port;
    private String password;
    private String name;


    public String getHost() {
        return host;
    }

    public RedisParam setHost(String host) {
        this.host = host;
        return this;
    }

    public Integer getPort() {
        return port;
    }

    public RedisParam setPort(Integer port) {
        this.port = port;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public RedisParam setPassword(String password) {
        this.password = password;
        return this;
    }


    public String getUniqueId() {
        return this.host + this.port + (null == this.password ? "" : this.password);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
