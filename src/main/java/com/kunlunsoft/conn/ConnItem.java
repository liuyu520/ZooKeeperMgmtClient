package com.kunlunsoft.conn;

import com.kunlunsoft.dto.ZkEnvironment;
import com.kunlunsoft.util.ZkConnect;
import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;

/***
 * 连接,包括两部分:<br />
 * 1,连接本身;<br />
 * 2,环境:ZkEnvironment<br />
 * 注意:在操作过程中,环境信息 统一从ConnItem 获取,不要从 ConfigInfo 中获取
 */
public class ConnItem {
    /***
     * 真正的连接
     */
    private ZooKeeper zk;
    private CountDownLatch connSignal = new CountDownLatch(0);
    /***
     * 环境
     */
    private ZkEnvironment zkEnvironment;

    public ZooKeeper getZk() {
        return zk;
    }

    public void setZk(ZooKeeper zk) {
        this.zk = zk;
    }

    public CountDownLatch getConnSignal() {
        return connSignal;
    }

    public void setConnSignal(CountDownLatch connSignal) {
        this.connSignal = connSignal;
    }

    public ZkEnvironment getZkEnvironment() {
        return zkEnvironment;
    }

    public void setZkEnvironment(ZkEnvironment zkEnvironment) {
        this.zkEnvironment = zkEnvironment;
    }

    /***
     * 重连接
     */
    public void reconnect() {
        try {
            ConnItem connItem = ZkConnect.connect(this.zkEnvironment);
            this.setZk(connItem.getZk());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() throws InterruptedException {
        if (null != zk) {
            zk.close();
            setZk(null);
        }
    }
}
