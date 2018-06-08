package com.kunlunsoft.redis.dialog;

import com.kunlunsoft.redis.util.ConnMgmt;
import com.string.widget.util.ValueWidget;
import com.swing.dialog.DialogUtil;
import com.swing.dialog.GenericDialog;
import com.swing.dialog.toast.ToastMessage;
import redis.clients.jedis.Jedis;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class RedisConnectDialog extends GenericDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField ipTextField1;
    private JTextField portTextField1;
    private JTextField textField1;
    private ConnMgmt connMgmt;

    public static RedisConnectDialog show(ConnMgmt connMgmt) {
        RedisConnectDialog redisConnectDialog = new RedisConnectDialog();
        redisConnectDialog.setConnMgmt(connMgmt);
        redisConnectDialog.setLoc(500, 130);
        redisConnectDialog.setVisible(true);
        return redisConnectDialog;
    }

    private void initUI() {
        this.ipTextField1.setText("59.110.236.186");
        this.portTextField1.setText(String.valueOf(ConnMgmt.REDIS_PORT));
        setTitle("连接redis 服务");
    }
    public RedisConnectDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        initUI();
        DialogUtil.escape2CloseDialog(this);
    }

    private void onOK() {
        // 1. 校验
        if (!DialogUtil.verifyTFEmpty(ipTextField1, "redis 服务ip")) {
            return;
        }
        if (!DialogUtil.verifyTFEmpty(portTextField1, "redis 端口")) {
            return;
        }
        String ip = ipTextField1.getText();
        String portStr = portTextField1.getText();
        int port;
        if (ValueWidget.isNullOrEmpty(portStr)) {
            port = ConnMgmt.REDIS_PORT;
        } else {
            port = Integer.parseInt(portStr);
        }
        Jedis jedis = connMgmt.connect(ip, port);
        if (null == jedis) {
            ToastMessage.toast("连接失败", 2000, Color.RED);
        } else {
            ToastMessage.toast("连接成功", 2000);
            dispose();
        }
    }

    private void onCancel() {
        dispose();
    }

    public static void main(String[] args) {
        RedisConnectDialog dialog = new RedisConnectDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    public ConnMgmt getConnMgmt() {
        return connMgmt;
    }

    public void setConnMgmt(ConnMgmt connMgmt) {
        this.connMgmt = connMgmt;
    }
}
