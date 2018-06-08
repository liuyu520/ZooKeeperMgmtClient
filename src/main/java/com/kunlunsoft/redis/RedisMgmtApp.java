package com.kunlunsoft.redis;

import com.common.util.SystemHWUtil;
import com.kunlunsoft.redis.dialog.RedisConnectDialog;
import com.kunlunsoft.redis.util.ConnMgmt;
import com.string.widget.util.ValueWidget;
import com.swing.component.AssistPopupTextArea;
import com.swing.component.AssistPopupTextField;
import com.swing.dialog.DialogUtil;
import com.swing.dialog.GenericFrame;
import com.swing.dialog.toast.ToastMessage;
import redis.clients.jedis.Jedis;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RedisMgmtApp {
    private AssistPopupTextField idTextField1;
    private AssistPopupTextField keyTextField1;
    private AssistPopupTextArea valTextArea1;
    private JButton saveButton;
    private AssistPopupTextField id2TextField1;
    private AssistPopupTextField key2TextField2;
    private AssistPopupTextArea resultTextArea1;
    private JPanel rootPane;
    private ConnMgmt connMgmt;

    /***
     * 初始化界面
     */
    private void initUI() {
        //1. 更新 UI,比如设置只读,placeholder等;
        this.idTextField1.placeHolder("redis 的id");
        this.keyTextField1.placeHolder("redis 的 key");

        String placeHolder = "回车触发";
        this.id2TextField1.placeHolder(placeHolder).setToolTipText(placeHolder);
        this.key2TextField2.placeHolder(placeHolder).setToolTipText(placeHolder);

        //2. redis 连接
        connMgmt = new ConnMgmt();

        //3. 绑定事件
        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resultTextArea1.setText(SystemHWUtil.EMPTY);
                String id = id2TextField1.getText2();
                String key = key2TextField2.getText2();
                String result = null;
                Jedis jedis = connMgmt.getCurrentConn();
                if (ValueWidget.isNullOrEmpty(key)) {
                    result = jedis.get(id);
                } else {
                    result = jedis.hget(id, key);
//                    result = map.get(key);
                }

                System.out.println("result :" + result);
                resultTextArea1.setText(result);
            }
        };

        id2TextField1.addActionListener(actionListener);
        key2TextField2.addActionListener(actionListener);

        //redis save
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = idTextField1.getText2();
                String key = keyTextField1.getText2();
                String val = valTextArea1.getText2();
                if (ValueWidget.isNullOrEmpty(key)) {
                    connMgmt.getCurrentConn().set(id, val);
                } else {
                    connMgmt.getCurrentConn().hset(id, key, val);
                }
                ToastMessage.toast("保存成功", 2000);
            }
        });
    }


    public RedisMgmtApp(/*JFrame frame*/) {
        initUI();
    }

    public static void main(String[] args) {
        DialogUtil.lookAndFeel2();
        RedisMgmtApp redisMgmtApp = new RedisMgmtApp();
        GenericFrame genericFrame = new GenericFrame() {
            public void setMenu() {
                JMenuBar menuBar = new JMenuBar();
                JMenu fileM = new JMenu("File");
                JMenuItem connectItem = new JMenuItem("连接");
                connectItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        redisMgmtApp.showConnectDialog();
                    }
                });
                fileM.add(connectItem);
                menuBar.add(fileM);
                setJMenuBar(menuBar);
            }
            @Override
            public void layout3(Container contentPane) {
                setContentPane(redisMgmtApp.getRootPane());
                init33(this);
//                fullScreen();
                setLoc(700, 600);
                setMenu();
            }
        };
        genericFrame.launchFrame();
    }

    public void showConnectDialog() {
        RedisConnectDialog.show(connMgmt);
    }
    public JPanel getRootPane() {
        return rootPane;
    }
}
