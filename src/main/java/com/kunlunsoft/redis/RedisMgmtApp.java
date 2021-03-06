package com.kunlunsoft.redis;

import com.cmd.dos.hw.util.CMDUtil;
import com.common.util.SystemHWUtil;
import com.io.hw.file.util.FileUtils;
import com.io.hw.json.HWJacksonUtils;
import com.kunlunsoft.dto.ConfigGlobalDTO;
import com.kunlunsoft.dto.RedisKeyValDto;
import com.kunlunsoft.redis.dialog.RedisConnectDialog;
import com.kunlunsoft.redis.dto.RedisConnItem;
import com.kunlunsoft.redis.util.ConnMgmt;
import com.kunlunsoft.util.ZkConnect;
import com.string.widget.util.ValueWidget;
import com.swing.component.AssistPopupTextArea;
import com.swing.component.AssistPopupTextField;
import com.swing.dialog.DialogUtil;
import com.swing.dialog.GenericFrame;
import com.swing.dialog.toast.ToastMessage;
import com.swing.messagebox.GUIUtil23;
import redis.clients.jedis.Jedis;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

/***
 * redis管理
 */
public class RedisMgmtApp {
    private AssistPopupTextField idTextField1;
    private AssistPopupTextField keyTextField1;
    private AssistPopupTextArea valTextArea1;
    private JButton saveButton;
    private AssistPopupTextField id2TextField1;
    private AssistPopupTextField key2TextField2;
    private AssistPopupTextArea resultTextArea1;
    private JPanel rootPane;
    private AssistPopupTextField secondTextField1;
    private ConnMgmt connMgmt;
    /***
     * 配置
     */
    private RedisKeyValDto redisKeyValDto;
    /***
     * 配置文件路径
     */
    public static final String configFilePath = System.getProperty("user.home") + File.separator + ".redis_key2.properties";
    /***
     * 配置文件
     */
    private File configFile;

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

        this.secondTextField1.setEditable(false);

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
                Jedis jedis = checkJedisIsConnected();
                if (jedis == null) {
                    return;
                }
                try {
                    if (ValueWidget.isNullOrEmpty(key)) {
                        result = jedis.get(id);
                    } else {
                        result = jedis.hget(id, key);
                        //                    result = map.get(key);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    GUIUtil23.errorDialog(ex);
                    return;
                }

                System.out.println("result :" + result);
                resultTextArea1.setText(result);

                //save
                RedisMgmtApp.this.redisKeyValDto = new RedisKeyValDto();
                redisKeyValDto.setQueryId(id);
                redisKeyValDto.setQueryKey(key);
                saveConfig();
            }
        };

        id2TextField1.addActionListener(actionListener);
        key2TextField2.addActionListener(actionListener);

        //redis save
        saveButton.addActionListener((ActionEvent e) -> {
            saveAction();
        });

        //4.

    }

    public void saveAction() {
        String id = idTextField1.getText2();
        if (!DialogUtil.verifyTFEmpty(idTextField1, "redis id")) {
            return;
        }
        String key = keyTextField1.getText2();
        String val = valTextArea1.getText2();
        String secondStr = secondTextField1.getText2();
        Jedis jedis = checkJedisIsConnected();
        if (jedis == null) {
            return;
        }
        Integer second = null;
        if (!ValueWidget.isNullOrEmpty(secondStr)) {
            if (ValueWidget.isNumeric(secondStr)) {
                second = Integer.parseInt(secondStr);
            } else {
                ToastMessage.toast("有效期必须是数字,单位:秒 ", 2000, Color.RED);
                return;
            }
        }
        try {
            if (ValueWidget.isNullOrEmpty(key)) {
                jedis.set(id, val);

            } else {
                jedis.hset(id, key, val);

            }
        } catch (Exception e) {
            e.printStackTrace();
            GUIUtil23.errorDialog(e);
            return;
        }
        //设置有效期
        if (null != second) {
            jedis.expire(id, second);
        }

        ToastMessage.toast("保存成功", 2000);
    }

    public Jedis checkJedisIsConnected() {
        Jedis jedis = connMgmt.getCurrentConn();
        if (null == jedis) {
            ToastMessage.toast("请先连接redis ", 2000, Color.RED);
            return null;
        }
        return jedis;
    }


    public RedisMgmtApp(/*JFrame frame*/) {
        initUI();
    }

    public static void main(String[] args) {
        DialogUtil.lookAndFeel2();
        final RedisMgmtApp redisMgmtApp = new RedisMgmtApp();
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
//                contentPane.add(redisMgmtApp.getRootPane());
                init33(this);
//                fullScreen();
//                setLoc(700, 600);
                setMenu();
                setGlobalShortCuts();//设置全局快捷键

                try {
                    redisMgmtApp.readConfig();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void beforeDispose() {
                super.beforeDispose();
                ConnMgmt connMgmt = redisMgmtApp.getConnMgmt();
                if (null == connMgmt) {
                    return;
                }
                int count = 0;
                for (RedisConnItem redisConnItem : connMgmt.getRedisParamMap().values()) {
                    Jedis jedis = redisConnItem.getJedis();
                    if (null != jedis) {
                        jedis.close();
                        count++;
                    }
                }
                System.out.println("关闭连接 :" + count);

                // 保存配置文件
                redisMgmtApp.saveConfig();
            }

            @Override
            protected void saveConfig() {
                super.saveConfig();
                System.out.println("保存 :");
                redisMgmtApp.saveAction();
            }
        };
        genericFrame.setTitle("redis 管理");
        genericFrame.launchFrame();
        genericFrame.setLoc(700, 600);
    }

    /***
     * 保存配置文件
     */
    public void saveConfig() {
        System.out.println("saveConfig :");
        configFile = new File(configFilePath);
        if (!configFile.exists()) {
            try {
                SystemHWUtil.createEmptyFile(configFile);
            } catch (IOException e) {
                e.printStackTrace();
                GUIUtil23.errorDialog(e);
            }
        }
        CMDUtil.show(configFilePath);//因为隐藏文件是只读的
        if (null != configFile) {
            //处理
            System.out.println("保存文件:" + configFilePath);
            if (this.redisKeyValDto == null) {
                this.redisKeyValDto = new RedisKeyValDto();
            }
            this.redisKeyValDto.setQueryId(id2TextField1.getText2());
            this.redisKeyValDto.setQueryKey(key2TextField2.getText2());
            this.redisKeyValDto.setSaveId(idTextField1.getText2());
            this.redisKeyValDto.setSaveKey(keyTextField1.getText2());
            this.redisKeyValDto.setSaveValue(valTextArea1.getText2());

            ConfigGlobalDTO configGlobalDTO = new ConfigGlobalDTO();
            configGlobalDTO.setRedisKeyValDto(this.redisKeyValDto);
            configGlobalDTO.setRedisParam(connMgmt.getCurrentRedisParam());
            String content = HWJacksonUtils.getJsonP(configGlobalDTO);
            if (ValueWidget.isNullOrEmpty(content)
                    || content.length() < 20) {
                String msg = "要保存的内容太小,不保存";
                System.out.println("msg :" + msg);
                return;
            }
            FileUtils.writeToFile(configFile, content, SystemHWUtil.CHARSET_UTF);
            CMDUtil.hide(configFilePath);
        }
    }

    /***
     * 读取配置文件
     * @throws IOException
     */
    private boolean readConfig() throws IOException {
        configFile = new File(configFilePath);

        if (!configFile.exists()) {
            return false;
        }
        String resumeInput = ZkConnect.getConfigContent(configFile);
        if (ValueWidget.isNullOrEmpty(resumeInput)) {
            GUIUtil23.warningDialog("请先去进行配置");
            return false;
        }
        ConfigGlobalDTO configGlobalDTO = HWJacksonUtils.deSerialize(resumeInput, ConfigGlobalDTO.class);
        this.redisKeyValDto = configGlobalDTO.getRedisKeyValDto();
        if (configGlobalDTO.getRedisParam() != null) {
            this.connMgmt.setCurrentRedisParam(configGlobalDTO.getRedisParam());
        }

        if (null == this.redisKeyValDto) {
            return false;
        }
        id2TextField1.setText(this.redisKeyValDto.getQueryId(), true);
        key2TextField2.setText(this.redisKeyValDto.getQueryKey(), true);

        idTextField1.setText(this.redisKeyValDto.getSaveId(), true);
        keyTextField1.setText(this.redisKeyValDto.getSaveKey(), true);
        valTextArea1.setText(this.redisKeyValDto.getSaveValue()/*, true*/);
        return true;
    }


    public void showConnectDialog() {
        RedisConnectDialog.show(connMgmt);
    }

    public JPanel getRootPane() {
        return rootPane;
    }

    public ConnMgmt getConnMgmt() {
        return connMgmt;
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
        rootPane = new JPanel();
        rootPane.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        final JScrollPane scrollPane1 = new JScrollPane();
        rootPane.add(scrollPane1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        scrollPane1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16716762)), "编辑"));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(3, 3, new Insets(0, 0, 0, 0), -1, -1));
        scrollPane1.setViewportView(panel1);
        final JLabel label1 = new JLabel();
        label1.setText("key");
        panel1.add(label1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        idTextField1 = new AssistPopupTextField();
        panel1.add(idTextField1, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(150, -1), null, 0, false));
        keyTextField1 = new AssistPopupTextField();
        panel1.add(keyTextField1, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(150, -1), null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("value");
        panel1.add(label2, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane2 = new JScrollPane();
        panel1.add(scrollPane2, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        valTextArea1 = new AssistPopupTextArea();
        valTextArea1.setText("");
        scrollPane2.setViewportView(valTextArea1);
        final JLabel label3 = new JLabel();
        label3.setText("有效期(秒)");
        panel1.add(label3, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        secondTextField1 = new AssistPopupTextField();
        panel1.add(secondTextField1, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JScrollPane scrollPane3 = new JScrollPane();
        rootPane.add(scrollPane3, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        scrollPane3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-65536)), "查询"));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 3, new Insets(0, 0, 0, 0), -1, -1));
        scrollPane3.setViewportView(panel2);
        final JLabel label4 = new JLabel();
        label4.setText("key");
        panel2.add(label4, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        id2TextField1 = new AssistPopupTextField();
        panel2.add(id2TextField1, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        key2TextField2 = new AssistPopupTextField();
        panel2.add(key2TextField2, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JScrollPane scrollPane4 = new JScrollPane();
        panel2.add(scrollPane4, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 3, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        resultTextArea1 = new AssistPopupTextArea();
        scrollPane4.setViewportView(resultTextArea1);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        rootPane.add(panel3, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        saveButton = new JButton();
        saveButton.setText("save");
        panel3.add(saveButton, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return rootPane;
    }
}
