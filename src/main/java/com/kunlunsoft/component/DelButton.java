package com.kunlunsoft.component;

import com.kunlunsoft.util.ZkConnect;
import com.string.widget.util.ValueWidget;
import com.swing.dialog.toast.ToastMessage;

import javax.swing.*;
import java.util.List;

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
            if (!rootPath.endsWith("/")) {
                rootPath = rootPath + "/";
            }
            String fullPath = rootPath + nodeKey;
            List<String> zNodes = getCurrentZk().getChildren(fullPath, true);
            if (!ValueWidget.isNullOrEmpty(zNodes)) {//不是普通的节点,是目录
                ToastMessage.toast("目录不为空,不允许直接删除:" + nodeKey, 2000, java.awt.Color.RED);
                return;
            }
            ZkConnect.deleteNode(getCurrentZk(), fullPath);
            ZkConnect.clearCache(rootPath);
            callback();
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
