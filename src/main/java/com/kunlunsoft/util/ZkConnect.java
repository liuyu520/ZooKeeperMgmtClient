package com.kunlunsoft.util;

import com.common.util.SystemHWUtil;
import com.google.common.eventbus.EventBus;
import com.kunlunsoft.conn.ConnItem;
import com.kunlunsoft.dto.ZkEnvironment;
import com.kunlunsoft.event.ZkConnSuccessEvent;
import com.string.widget.util.ValueWidget;
import com.swing.messagebox.GUIUtil23;
import org.apache.log4j.Logger;
import org.apache.zookeeper.*;

import java.io.UnsupportedEncodingException;
import java.util.*;

public class ZkConnect {
    protected static final Logger logger = Logger.getLogger(ZkConnect.class);
    /**
     * 为了在各个类之间 传递数据和事件
     */
    protected static EventBus eventBus = new EventBus();
    /***
     * 搜索缓存<br />
     * key 是搜索路径,即 方法zkConnect.search 的第一个参数
     */
    private static Map<String, Map<String, String>> searchResultCacheMap = new HashMap<>();

    public static byte[] getData(ZooKeeper zk, String zNodePath) throws KeeperException, InterruptedException {
        byte[] data = zk.getData(zNodePath, true, zk.exists(zNodePath, true));
        return data;
    }

    /***
     * 清空缓存
     */
    public static void clearCache() {
        searchResultCacheMap.clear();
        if (0 != searchResultCacheMap.size()) {
            searchResultCacheMap = new HashMap<>();
        }
    }

    /***
     * 只清除一条记录
     * @param path
     */
    public static void clearCache(String path) {
        if (searchResultCacheMap.size() > 0) {
            searchResultCacheMap.remove(path.replace("/", ""));
        }
    }

    public static Map<String, String> search(String path, ZooKeeper zk) throws KeeperException, InterruptedException {
        String key = path.replace("/", "");
        if (searchResultCacheMap.containsKey(key)) {
            System.out.println("使用缓存 :" + path);
            return searchResultCacheMap.get(key);
        }
        if (null == zk) {
            GUIUtil23.warningDialog("zk 未连接上");
            return null;
        }
        if (!path.equals("/")) {
            path = path.replace("//", "/").replaceAll("/$", SystemHWUtil.EMPTY);
        }
        //Path must not end with / character
        List<String> zNodes = zk.getChildren(path, true);//注意如果是根目录,则必须是"/",不能是""
        Map<String, String> map = new TreeMap<>();
        for (String zNode : zNodes) {
//            System.out.println("ChildrenNode " + zNode);
            String zNodePath = (path.endsWith("/") ? "" : path) + "/" + zNode;
            String val = getNodeValue(zk, zNodePath);
            map.put(zNode, val);
        }
        if (map.size() > 0) {
            searchResultCacheMap.put(key, map);
        }
        return map;
    }

    public static String getNodeValue(ZooKeeper zk, String zNodePath) throws KeeperException, InterruptedException {
        byte[] data = zk.getData(zNodePath, true, zk.exists(zNodePath, true));
        if (ValueWidget.isNullOrEmpty(data)) {
//            GUIUtil23.warningDialog("返回数据为空");
            return null;
        }
        String val = null;
        try {
            val = new String(data, SystemHWUtil.CHARSET_UTF);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return val;
    }

    public static ConnItem connect(/*String host, Integer port*/ZkEnvironment zkEnvironment) throws Exception {
        String host = zkEnvironment.getIp();
        Integer port = zkEnvironment.getPort();
        if (ValueWidget.isNullOrEmpty(host)) {
            GUIUtil23.warningDialog("未进行配置,请先去配置");
            return null;
        }

        if (null == port) {
            port = 2181;
        }
        host = host.replaceAll("[\\s]*:[\\s]*[0-9]+", "");
        String hostLog = "zk server:" + host;
        String portLog = "zk port:" + port;
        logger.warn(hostLog);
        logger.warn(portLog);
        System.out.println(hostLog);
        System.out.println(portLog);
        final ConnItem connItem = new ConnItem();
        ZooKeeper zk = new ZooKeeper(host, port, new Watcher() {
            public void process(WatchedEvent event) {
                if (event.getState() == Event.KeeperState.SyncConnected) {
                    connedSuccess();
                } else if (event.getState() == Event.KeeperState.ConnectedReadOnly) {
                    connedSuccess();
                } else if (event.getState() == Event.KeeperState.Expired) {
                    GUIUtil23.warningDialog("连接超时,需要重新连接");
                }
            }

            private void connedSuccess() {
                // 增加 event bus 事件,因为这是真正连接上
                connItem.getConnSignal().countDown();
                String msg = "连接成功..ok";
                System.out.println(msg);
                ZkConnSuccessEvent connSuccessEvent = new ZkConnSuccessEvent();
                getEventBus().post(connSuccessEvent);
            }
        });
        connItem.getConnSignal().await();
        connItem.setZk(zk);
        connItem.setZkEnvironment(zkEnvironment);
        return connItem;
    }

    public void close(ZooKeeper zk) throws InterruptedException {
        zk.close();
    }

    public static void createNode(ZooKeeper zk, String path, byte[] data) throws Exception {
        zk.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }

    public static void createNode(ZooKeeper zooKeeper, String path, String data) throws Exception {
        createNode(zooKeeper, path, data.getBytes(SystemHWUtil.CHARSET_UTF));
    }

    public static void createNode(ZooKeeper zooKeeper, String path) throws Exception {
        createNode(zooKeeper, path, new Date().toString().getBytes());
    }

    public static void updateNode(ZooKeeper zk, String path, byte[] data) throws Exception {
        zk.setData(path, data, zk.exists(path, true).getVersion());
    }

    public static String updateNode(ZooKeeper zk, String path, String data) throws Exception {
        byte[] val = zk.getData(path, true, zk.exists(path, true));

        String oldVal = null;
        if (!ValueWidget.isNullOrEmpty(val)) {
            oldVal = new String(val, SystemHWUtil.CHARSET_UTF);
//            logger(path, oldVal);
            System.out.println("原值:" + oldVal);
        }
        updateNode(zk, path, data.getBytes(SystemHWUtil.CHARSET_UTF));
        return oldVal;
    }

    public static void deleteNode(ZooKeeper zk, String path) throws Exception {
        String val = getNodeValue(zk, path);
        zk.delete(path, zk.exists(path, true).getVersion());
        logger(path, val);
    }

    public static void logger(String path, String val) {
        System.out.println("更新 ,val:" + val + ", path:" + path);
//        ZkModifyEvent zkModifyEvent = new ZkModifyEvent(path, val); TODO
//        eventBus.post(zkModifyEvent);
    }

    public static EventBus getEventBus() {
        return eventBus;
    }
}
