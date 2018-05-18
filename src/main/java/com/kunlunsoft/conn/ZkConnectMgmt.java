package com.kunlunsoft.conn;

import com.kunlunsoft.dto.ConfigInfo;
import com.kunlunsoft.util.ZkConnect;
import org.apache.log4j.Logger;
import org.apache.zookeeper.ZooKeeper;

import java.util.HashMap;
import java.util.Map;

/***
 * zk l连接管理
 *
 */
public class ZkConnectMgmt {
    protected static final Logger logger = Logger.getLogger(ZkConnectMgmt.class);
    /***
     * current ZooKeeper
     */
    private ConnItem currZk;
    private Map<Integer, ConnItem> connItemMap = new HashMap<>();
    private ConfigInfo configInfo;

    public ZkConnectMgmt() {
        super();
    }

    public ZkConnectMgmt(ConfigInfo configInfo) {
        this.configInfo = configInfo;
    }

    public ConnItem getCurrZk() {
        return currZk;
    }

    public void setCurrZk(ConnItem currZk) {
        this.currZk = currZk;
    }

    public ZooKeeper getCurrentZookeep() {
        return getCurrZk().getZk();
    }

    public Map<Integer, ConnItem> getConnItemMap() {
        return connItemMap;
    }

    public void setConnItemMap(Map<Integer, ConnItem> connItemMap) {
        this.connItemMap = connItemMap;
    }

    public ConfigInfo getConfigInfo() {
        return configInfo;
    }

    public void setConfigInfo(ConfigInfo configInfo) {
        this.configInfo = configInfo;
    }

    public void put(Integer index, ConnItem connItem) {
        this.connItemMap.put(index, connItem);
    }

    public boolean hasIndex(Integer index) {
        return this.connItemMap.keySet().contains(index);
    }

    public ConnItem getZK(Integer index) {
        ConnItem connItem = this.connItemMap.get(index);
        if (null == connItem) {
            ZkConnect connect = new ZkConnect();
            try {
                connItem = ZkConnect.connect(configInfo.getZkEnvironment(index));
                put(index, connItem);
                return connItem;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return connItem;
    }
}
