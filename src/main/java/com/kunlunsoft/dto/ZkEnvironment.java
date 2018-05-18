package com.kunlunsoft.dto;

/***
 * 环境
 */
public class ZkEnvironment {
    private String ip;
    private Integer port;
    private String zkRootPath;

    public String getIp() {
        return ip;
    }

    public ZkEnvironment setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public Integer getPort() {
        return port;
    }

    public ZkEnvironment setPort(Integer port) {
        this.port = port;
        return this;
    }

    public String getZkRootPath() {
        return zkRootPath;
    }

    public ZkEnvironment setZkRootPath(String zkRootPath) {
        this.zkRootPath = zkRootPath;
        return this;
    }
}
