package com.kunlunsoft.component;

import com.kunlunsoft.conn.ZkConnectMgmt;
import org.apache.zookeeper.ZooKeeper;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by whuanghkl on 17/5/31.
 */
public abstract class MyButton extends JButton {
    private String nodeKey;
    private ZkConnectMgmt zkConnect;
    private String rootPath;

    public abstract void action(String nodeKey);

    public MyButton(String text) {
        super(text);
    }

    public MyButton(String text, String nodeKey) {
        super(text);
        this.nodeKey = nodeKey;
    }

    public MyButton() {
        super();
    }

    public void bindEvent() {
        this.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                action(nodeKey);
            }
        });
    }

    public String getNodeKey() {
        return nodeKey;
    }

    public MyButton setNodeKey(String nodeKey) {
        this.nodeKey = nodeKey;
        return this;
    }

    public ZkConnectMgmt getZkConnect() {
        return zkConnect;
    }

    public ZooKeeper getCurrentZk() {
        return zkConnect.getCurrentZookeep();
    }

    public MyButton setZkConnect(ZkConnectMgmt zkConnect) {
        this.zkConnect = zkConnect;
        return this;
    }

    public String getRootPath() {
        return rootPath;
    }

    public MyButton setRootPath(String rootPath) {
        this.rootPath = rootPath;
        return this;
    }
}
