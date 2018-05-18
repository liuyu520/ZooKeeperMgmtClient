package com.kunlunsoft.listener;

import com.kunlunsoft.dto.ConfigInfo;
import com.kunlunsoft.util.ZkConnect;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuBarListener implements ActionListener {
    private ConfigInfo configInfo;

    public MenuBarListener(ConfigInfo configInfo) {
        this.configInfo = configInfo;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        if (command.equals("清除全部")) {
            ZkConnect.clearCache();
        } else if (command.equals("清除当前路径")) {
            /*String rootPath = ZkUtil.getCurrentZkRootPaths(configInfo);
            if (null == rootPath) {
                GUIUtil23.warningDialog("未设置当前路径");
                return;
            }
            ZkConnect.clearCache(rootPath.replace("/", ""));
            ToastMessage.toast("清除缓存成功:" + rootPath, 2000);*/
        }
    }
}
