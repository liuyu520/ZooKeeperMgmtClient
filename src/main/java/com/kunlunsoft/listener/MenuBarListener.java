package com.kunlunsoft.listener;

import com.io.hw.json.HWJacksonUtils;
import com.kunlunsoft.ZkEditorApp;
import com.kunlunsoft.dto.ConfigInfo;
import com.kunlunsoft.util.ZkConnect;
import com.swing.messagebox.GUIUtil23;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
        } else if (command.equals("读取缓存")) {
            System.out.println("读取缓存 :");
            String oldContent;
            try {
                oldContent = ZkConnect.getConfigContent(new File(ZkEditorApp.cacheFilePath));
                Map<String, Map<String, String>> searchResultCacheMap = HWJacksonUtils.deSerializeMap(oldContent, HashMap.class);
                ZkConnect.mergeCache(searchResultCacheMap);
            } catch (IOException e1) {
                e1.printStackTrace();
                GUIUtil23.errorDialog(e1);
            }
        }
    }
}
