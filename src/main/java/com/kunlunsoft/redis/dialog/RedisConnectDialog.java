package com.kunlunsoft.redis.dialog;

import com.kunlunsoft.redis.dto.RedisParam;
import com.kunlunsoft.redis.util.ConnMgmt;
import com.string.widget.util.ValueWidget;
import com.swing.component.AssistPopupTextField;
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
    private AssistPopupTextField ipTextField1;
    private AssistPopupTextField portTextField1;
    private AssistPopupTextField passwordTextField1;
    /**
     * 强制重连
     */
    private JButton reconnectButton;
    private AssistPopupTextField nameTextField1;
    private JComboBox historyComboBox1;
    private ConnMgmt connMgmt;
    private RedisParam redisParam;

    public static RedisConnectDialog show(ConnMgmt connMgmt) {
        RedisConnectDialog redisConnectDialog = new RedisConnectDialog(connMgmt);
//        redisConnectDialog.setConnMgmt(connMgmt);
        redisConnectDialog.setLoc(500, 130);
        redisConnectDialog.setVisible(true);
        return redisConnectDialog;
    }

    private void initUI() {
        this.ipTextField1.setText("59.110.236.186");
        this.portTextField1.setText(String.valueOf(ConnMgmt.REDIS_PORT));
        this.portTextField1.placeHolder("redis 端口号");

        String placeHolder = "可为空";
        this.passwordTextField1.placeHolder(placeHolder).setToolTipText(placeHolder);
        this.passwordTextField1.setEditable(false);

        if (null != this.redisParam) {
            this.ipTextField1.setText(this.redisParam.getHost());
            this.passwordTextField1.setText(this.redisParam.getPassword());
            this.portTextField1.setText(String.valueOf(this.redisParam.getPort()));
            this.nameTextField1.setText(this.redisParam.getName());
        }

        setTitle("连接redis 服务");
    }

    public RedisConnectDialog(ConnMgmt connMgmt) {
        if (null != connMgmt.getCurrentRedisParam()) {
            this.redisParam = connMgmt.getCurrentRedisParam();
        }
        setConnMgmt(connMgmt);
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onOK(false);
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
        reconnectButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                onOK(true);
            }
        });
    }

    private void onOK(boolean force) {
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
        String password2 = passwordTextField1.getText2();
        if (nameTextField1 == null) {
            System.exit(0);
            return;
        }
        Jedis jedis = connMgmt.connect(nameTextField1.getText(), ip, password2, port, force);
        if (null == jedis) {
            ToastMessage.toast("连接失败", 2000, Color.RED);
        } else {
            ToastMessage.toast("连接成功", 2000);
           /* redisParam = new RedisParam();
            redisParam.setPassword(password2);
            redisParam.setHost(ip);
            redisParam.setPort(port);*/
            dispose();
        }
    }

    private void onCancel() {
        dispose();
    }

    public static void main(String[] args) {
        RedisConnectDialog dialog = new RedisConnectDialog(null);
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

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        contentPane = new JPanel();
        contentPane.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 1, new Insets(10, 10, 10, 10), -1, -1));
        contentPane.setEnabled(false);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel1, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
        panel1.add(spacer1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1, true, false));
        panel1.add(panel2, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        buttonOK = new JButton();
        buttonOK.setText("OK");
        panel2.add(buttonOK, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonCancel = new JButton();
        buttonCancel.setText("Cancel");
        panel2.add(buttonCancel, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 4, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel3, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("ip");
        panel3.add(label1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        ipTextField1 = new AssistPopupTextField();
        panel3.add(ipTextField1, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(223, 26), null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText(":");
        panel3.add(label2, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        portTextField1 = new AssistPopupTextField();
        panel3.add(portTextField1, new com.intellij.uiDesigner.core.GridConstraints(0, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("password");
        panel3.add(label3, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        passwordTextField1 = new AssistPopupTextField();
        panel3.add(passwordTextField1, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(223, 26), null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }
}
