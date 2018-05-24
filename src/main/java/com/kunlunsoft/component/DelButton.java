package com.kunlunsoft.component;

import com.kunlunsoft.util.ZkConnect;
import com.swing.dialog.toast.ToastMessage;
import org.apache.zookeeper.ZooKeeper;

import javax.swing.*;

/**
 * Created by whuanghkl on 17/5/31.
 */
public class DelButton extends MyButton {
    @Override
    public void action(final String nodeKey) {
        System.out.println("nodeKey :" + nodeKey);
        int result = JOptionPane.showConfirmDialog(null, "确认要删除\""+nodeKey+"\"吗 ?", "确认",
                JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                setEnabled(false);
                deleteAction(nodeKey);
                setEnabled(true);
            }
        }).start();
    }

    private void deleteAction(String nodeKey) {
        try {
            String rootPath = getRootPath();
            ZooKeeper zooKeeper = getCurrentZk();
            if (ZkConnect.deleteNodeAction(zooKeeper, nodeKey, rootPath)) return;
            ZkConnect.clearCache(rootPath);
            callback();// ZkEditApp中有实现
        } catch (Exception e) {
            e.printStackTrace();
            ToastMessage.toast("删除失败:" + nodeKey, 2000, java.awt.Color.RED);
        }
    }


    public DelButton(String nodeKey) {
        super("删除", nodeKey);
        super.bindEvent();
    }

    public void callback() {
    }
}
