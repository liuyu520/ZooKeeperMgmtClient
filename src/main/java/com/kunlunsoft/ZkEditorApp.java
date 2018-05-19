package com.kunlunsoft;

import com.cmd.dos.hw.util.CMDUtil;
import com.common.util.SystemHWUtil;
import com.common.util.WindowUtil;
import com.google.common.eventbus.Subscribe;
import com.io.hw.file.util.FileUtils;
import com.io.hw.json.HWJacksonUtils;
import com.kunlunsoft.component.DelButton;
import com.kunlunsoft.component.EditButton;
import com.kunlunsoft.component.EnterDirectoryBtn;
import com.kunlunsoft.conn.ConnItem;
import com.kunlunsoft.conn.ZkConnectMgmt;
import com.kunlunsoft.dialog.ConfigDialog;
import com.kunlunsoft.dto.ConfigInfo;
import com.kunlunsoft.event.SaveConfigEvent;
import com.kunlunsoft.event.ZkConnSuccessEvent;
import com.kunlunsoft.event.listener.ZkModifyListener;
import com.kunlunsoft.listener.MenuBarListener;
import com.kunlunsoft.util.ZkConnect;
import com.string.widget.util.RegexUtil;
import com.string.widget.util.ValueWidget;
import com.swing.component.AssistPopupTextArea;
import com.swing.component.AssistPopupTextField;
import com.swing.dialog.DialogUtil;
import com.swing.dialog.GenericFrame;
import com.swing.dialog.toast.ToastMessage;
import com.swing.event.EventHWUtil;
import com.swing.messagebox.GUIUtil23;
import com.swing.table.MyButtonEditor;
import com.swing.table.MyButtonRender;
import com.swing.table.TableUtil3;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class ZkEditorApp extends GenericFrame {

    public static final String ZOOKEEPER_title = "zookeeper 编辑器";
    private JPanel contentPane;
    private AssistPopupTextField keyTextField;
    private AssistPopupTextField valTextField;
    private ConnItem zkConnItem;
    //    private ZkConnect zkConnect;
    private JCheckBox inteCheckBox;
    private JCheckBox testCheckBox;
    private JCheckBox moniCheckBox;
    private JCheckBox onlineCheckBox;
    private AssistPopupTextField searchTextField;
    private JTable zkNodeTable;
    static final String[] columnNames = {"参数名", "参数值(字符串)", "删除", "编辑", "进入"};
    private Map<String, String> resultMap;
    private JLabel statusLabel;
    private ConfigInfo configInfo = new ConfigInfo();
    /***
     * 模糊查询
     */
    private JCheckBox fuzzyCheckBox;
    /***
     * 配置文件路径
     */
    public static final String configFilePath = System.getProperty("user.home") + File.separator + ".zookeeper_env_editer.properties";
    /***
     * 配置文件
     */
    private File configFile;
    private JTextField currentPathtextField;
    private JCheckBox needPrefixCheckbox;
    private AssistPopupTextArea resultTextArea;
    /***
     * g管理各环境的连接
     */
    private ZkConnectMgmt zkConnectMgmt;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    ZkEditorApp frame = new ZkEditorApp();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public ZkEditorApp() {
        setTitle(ZOOKEEPER_title);
        try {
            setIcon("/img/logo.jpg", this.getClass());
        } catch (IOException e) {
            e.printStackTrace();
        }

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        setBounds(100, 100, 650, 300);
        fullScreen2(this);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);

        JSplitPane splitPane = new JSplitPane();
        splitPane.setDividerLocation(300);
        contentPane.add(splitPane, BorderLayout.CENTER);

        JPanel panel = new JPanel();
        splitPane.setLeftComponent(panel);
        GridBagLayout gbl_panel = new GridBagLayout();
        gbl_panel.columnWidths = new int[]{0, 0, 0, 0};
        gbl_panel.rowHeights = new int[]{0, 0, 0, 0, 0, 0};
        gbl_panel.columnWeights = new double[]{0.0, 1.0, 1.0, Double.MIN_VALUE};
        gbl_panel.rowWeights = new double[]{0.0, 0.0, 0.0, 1.0, 1.0, Double.MIN_VALUE};
        panel.setLayout(gbl_panel);

        JLabel lblKey = new JLabel("key");
        GridBagConstraints gbc_lblKey = new GridBagConstraints();
        gbc_lblKey.insets = new Insets(0, 0, 5, 5);
        gbc_lblKey.anchor = GridBagConstraints.EAST;
        gbc_lblKey.gridx = 1;
        gbc_lblKey.gridy = 0;
        panel.add(lblKey, gbc_lblKey);

        keyTextField = new AssistPopupTextField();
        keyTextField.setText("");
        keyTextField.placeHolder("HOST_EE");
        GridBagConstraints gbc_keyTextField = new GridBagConstraints();
        gbc_keyTextField.insets = new Insets(0, 0, 5, 0);
        gbc_keyTextField.fill = GridBagConstraints.HORIZONTAL;
        gbc_keyTextField.gridx = 2;
        gbc_keyTextField.gridy = 0;
        panel.add(keyTextField, gbc_keyTextField);
        keyTextField.setColumns(10);

        JLabel lblValue = new JLabel("value");
        GridBagConstraints gbc_lblValue = new GridBagConstraints();
        gbc_lblValue.anchor = GridBagConstraints.EAST;
        gbc_lblValue.insets = new Insets(0, 0, 5, 5);
        gbc_lblValue.gridx = 1;
        gbc_lblValue.gridy = 1;
        panel.add(lblValue, gbc_lblValue);

        valTextField = new AssistPopupTextField();
        valTextField.placeHolder("不必用双引号括起来");
        GridBagConstraints gbc_valTextField = new GridBagConstraints();
        gbc_valTextField.insets = new Insets(0, 0, 5, 0);
        gbc_valTextField.fill = GridBagConstraints.HORIZONTAL;
        gbc_valTextField.gridx = 2;
        gbc_valTextField.gridy = 1;
        panel.add(valTextField, gbc_valTextField);
        valTextField.setColumns(10);

        JPanel panel_2 = new JPanel();
        GridBagConstraints gbc_panel_2 = new GridBagConstraints();
        gbc_panel_2.insets = new Insets(0, 0, 5, 0);
        gbc_panel_2.gridwidth = 2;
        gbc_panel_2.fill = GridBagConstraints.BOTH;
        gbc_panel_2.gridx = 1;
        gbc_panel_2.gridy = 2;
        panel.add(panel_2, gbc_panel_2);

//		JCheckBox onlinehCeckBox = new JCheckBox("线上");
//		panel_2.add(onlinehCeckBox);
        testCheckBox = new JCheckBox("测试");
        testCheckBox.setSelected(true);
        panel_2.add(testCheckBox);

        inteCheckBox = new JCheckBox("集测");
        inteCheckBox.setSelected(true);
        panel_2.add(inteCheckBox);

        moniCheckBox = new JCheckBox(" 模拟");
        moniCheckBox.setSelected(true);
        panel_2.add(moniCheckBox);

        onlineCheckBox = new JCheckBox("线上");
        onlineCheckBox.setSelected(true);
        panel_2.add(onlineCheckBox);

        JPanel panel_3 = new JPanel();
        GridBagConstraints gbc_panel_3 = new GridBagConstraints();
        gbc_panel_3.insets = new Insets(0, 0, 5, 0);
        gbc_panel_3.gridwidth = 2;
        gbc_panel_3.fill = GridBagConstraints.BOTH;
        gbc_panel_3.gridx = 1;
        gbc_panel_3.gridy = 3;
        panel.add(panel_3, gbc_panel_3);

        JButton btnUpdate = new JButton("update");
        btnUpdate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (check()) return;

                boolean hasSelected = false;
                if (inteCheckBox.isSelected()) {
                    ConnItem connItem = zkConnectMgmt.getZK(1);
                    updateZkNode(connItem.getZk(), connItem.getZkEnvironment().getZkRootPath());//TODO
                    hasSelected = true;
                }
                if (testCheckBox.isSelected()) {
                    ConnItem connItem = zkConnectMgmt.getZK(0);
                    updateZkNode(connItem.getZk(), connItem.getZkEnvironment().getZkRootPath());
                    hasSelected = true;
                }
                if (!hasSelected) {
                    if (configInfo.isActivate()) {
                        String path = getRootPath();//configInfo.getZkRootPath();
                        updateZkNode(zkConnItem.getZk(), path);
                        return;
                    }
                }
            }
        });
        panel_3.add(btnUpdate);

        JButton btnAdd = new JButton("add");
        btnAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (check()) return;

                boolean hasSelected = false;
                if (inteCheckBox.isSelected()) {
                    ConnItem connItem = zkConnectMgmt.getZK(1);
                    createZkNode(connItem.getZk(), connItem.getZkEnvironment().getZkRootPath());
                    hasSelected = true;
                }
                if (testCheckBox.isSelected()) {
                    ConnItem connItem = zkConnectMgmt.getZK(0);
                    createZkNode(connItem.getZk(), connItem.getZkEnvironment().getZkRootPath());
                    hasSelected = true;
                }
                if (!hasSelected) {
                    if (configInfo.isActivate()) {
                        String path = getRootPath();
                        createZkNode(zkConnItem.getZk(), path);
                    }
                }
            }
        });
        panel_3.add(btnAdd);

        JScrollPane scrollPane_1 = new JScrollPane();
        GridBagConstraints gbc_scrollPane_1 = new GridBagConstraints();
        gbc_scrollPane_1.gridwidth = 2;
        gbc_scrollPane_1.insets = new Insets(0, 0, 0, 5);
        gbc_scrollPane_1.fill = GridBagConstraints.BOTH;
        gbc_scrollPane_1.gridx = 1;
        gbc_scrollPane_1.gridy = 4;
        panel.add(scrollPane_1, gbc_scrollPane_1);

        resultTextArea = new AssistPopupTextArea();
        String helpTips = "帮助:双击不会触发编辑,想编辑,请点击[编辑]按钮";
        resultTextArea.setText(helpTips);
        resultTextArea.setLineWrap(true);
        resultTextArea.setWrapStyleWord(true);
        scrollPane_1.setViewportView(resultTextArea);

        JPanel panel_1 = new JPanel();
        splitPane.setRightComponent(panel_1);
        GridBagLayout gbl_panel_1 = new GridBagLayout();
        gbl_panel_1.columnWidths = new int[]{0, 0, 0, 0};
        gbl_panel_1.rowHeights = new int[]{0, 0, 0, 0, 0};
        gbl_panel_1.columnWeights = new double[]{1.0, 0.0, 0.0, Double.MIN_VALUE};
        gbl_panel_1.rowWeights = new double[]{0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
        panel_1.setLayout(gbl_panel_1);

        searchTextField = new AssistPopupTextField();
        searchTextField.placeHolder("输入关键字搜索");
        searchTextField.setToolTipText("输入关键字搜索");
        GridBagConstraints gbc_searchTextField = new GridBagConstraints();
        gbc_searchTextField.insets = new Insets(0, 0, 5, 5);
        gbc_searchTextField.fill = GridBagConstraints.HORIZONTAL;
        gbc_searchTextField.gridx = 0;
        gbc_searchTextField.gridy = 0;
        panel_1.add(searchTextField, gbc_searchTextField);
        searchTextField.setColumns(10);
        searchTextField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        searchTextField.setEnabled(false);
                        searchAction();
                        searchTextField.setEnabled(true);
                    }
                }).start();

                ToastMessage.toast("查询完成", 1000);
            }
        });

        fuzzyCheckBox = new JCheckBox("模糊查询");
        fuzzyCheckBox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                searchTextField.requestFocus();
            }
        });
        GridBagConstraints gbc_checkBox = new GridBagConstraints();
        gbc_checkBox.insets = new Insets(0, 0, 5, 5);
        gbc_checkBox.gridx = 1;
        gbc_checkBox.gridy = 0;
        panel_1.add(fuzzyCheckBox, gbc_checkBox);

        final JButton btnSearch = new JButton("search");
        GridBagConstraints gbc_btnSearch = new GridBagConstraints();
        gbc_btnSearch.insets = new Insets(0, 0, 5, 0);
        gbc_btnSearch.gridx = 2;
        gbc_btnSearch.gridy = 0;
        panel_1.add(btnSearch, gbc_btnSearch);
        btnSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        btnSearch.setEnabled(false);
                        searchAction();
                        btnSearch.setEnabled(true);
                    }
                }).start();
            }
        });

        JScrollPane scrollPane = new JScrollPane();
        GridBagConstraints gbc_scrollPane = new GridBagConstraints();
        gbc_scrollPane.insets = new Insets(0, 0, 0, 5);
        gbc_scrollPane.gridwidth = 3;
        gbc_scrollPane.fill = GridBagConstraints.BOTH;
        gbc_scrollPane.gridx = 0;
        gbc_scrollPane.gridy = 1;
        panel_1.add(scrollPane, gbc_scrollPane);

        zkNodeTable = new JTable();
        scrollPane.setViewportView(zkNodeTable);

        JPanel panel_5 = new JPanel();
        GridBagConstraints gbc_panel_5 = new GridBagConstraints();
        gbc_panel_5.anchor = GridBagConstraints.WEST;
        gbc_panel_5.gridwidth = 3;
        gbc_panel_5.insets = new Insets(0, 0, 5, 0);
        gbc_panel_5.fill = GridBagConstraints.VERTICAL;
        gbc_panel_5.gridx = 0;
        gbc_panel_5.gridy = 2;
        panel_1.add(panel_5, gbc_panel_5);

        statusLabel = new JLabel("");
        panel_5.add(statusLabel);

        JPanel panel_4 = new JPanel();
        GridBagConstraints gbc_panel_4 = new GridBagConstraints();
        gbc_panel_4.gridwidth = 3;
        gbc_panel_4.fill = GridBagConstraints.BOTH;
        gbc_panel_4.gridx = 0;
        gbc_panel_4.gridy = 3;
        panel_1.add(panel_4, gbc_panel_4);

        needPrefixCheckbox = new JCheckBox("需要前缀名");
        panel_4.add(needPrefixCheckbox);

        JButton exportBtton = new JButton("复制到剪切板");
        exportBtton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String exportResult = exportZKNode(needPrefixCheckbox.isSelected());
                WindowUtil.setSysClipboardText(exportResult);
                ToastMessage.toast("已复制到剪切板", 2000);
            }
        });
        panel_4.add(exportBtton);

        JPanel panel_6 = new JPanel();
        FlowLayout flowLayout = (FlowLayout) panel_6.getLayout();
        flowLayout.setAlignment(FlowLayout.LEFT);
        contentPane.add(panel_6, BorderLayout.NORTH);

        JLabel label = new JLabel("当前路径:");
        panel_6.add(label);

        currentPathtextField = new JTextField();
        currentPathtextField.setEditable(false);
        panel_6.add(currentPathtextField);
        currentPathtextField.setColumns(40);

        JButton backBtn = new JButton("回到上一级");
        backBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String orginRoot = getRootPath();// configInfo.getZkRootPath();
//                configInfo.setZkRootPath(SystemHWUtil.getParentDir(orginRoot));
//                configInfo.saveZkRootPath(SystemHWUtil.getParentDir(orginRoot));
                zkConnItem.getZkEnvironment().setZkRootPath(SystemHWUtil.getParentDir(orginRoot));
                refreshCurrentPath();
                searchAction(true);
            }
        });
        panel_6.add(backBtn);

        boolean readConfigSuccess = false;
        try {
            readConfigSuccess = readConfig();//必须在connectServer()方法之前
        } catch (IOException e) {
            e.printStackTrace();
            ToastMessage.toast("读取配置文件失败", 2000, Color.red);
        }

        setMenuBar2();
        if (readConfigSuccess) {
            //连接zk服务器
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        connectServer(false);
                        refreshCurrentPath();//设置主面板的当前路径
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        try {
            init33(this, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        addGlobalKey();
//        refreshCurrentPath();

        ZkModifyListener zkModifyListener = new ZkModifyListener(resultTextArea);


        //
        ZkConnect.getEventBus().register(this);
    }

    @Subscribe
    public void handleConnSucces(ZkConnSuccessEvent zkConnSuccessEvent) {
        String msg = "连接zk 成功.";
        System.out.println("msg :" + msg);
    }
    @Subscribe
    public void handleSaveConfig(SaveConfigEvent saveConfigEvent) {
        System.out.println("保存配置文件2222 :");
        if (null == zkConnectMgmt) {
            zkConnectMgmt = new ZkConnectMgmt(this.configInfo);
        }
        String currEnvDisp = this.configInfo.getCurrEnvDisp();
        if (!ValueWidget.isNullOrEmpty(currEnvDisp)) {
            setTitle(ZOOKEEPER_title + "-" + currEnvDisp);
        }

        try {
            connectServer(false);
            refreshCurrentPath();
        } catch (Exception e) {
            e.printStackTrace();
            GUIUtil23.errorDialog(e.getMessage());
        }
    }

    public void refreshCurrentPath() {
        currentPathtextField.setText(getRootPath());
    }

    public String exportZKNode(boolean needPrefix) {
        StringBuffer stringBuffer = new StringBuffer();
        if (null == resultMap) {
            searchAction();
        }
        Set<String> keys = resultMap.keySet();
        String path = getRootPath();
        for (String key : keys) {
            Object[] objs = new Object[2];
            objs[0] = key;
            if (needPrefix) {
                stringBuffer.append(path);
            }
            stringBuffer.append(key).append("=");
            stringBuffer.append(resultMap.get(key)).append(SystemHWUtil.CRLF);
        }
//        System.out.println(" :" +stringBuffer);
        return stringBuffer.toString();
    }

    public void searchAction() {
        searchAction(false);
    }

    public void searchAction(boolean ignoreKeyword) {
        System.out.println(" search");
        String searchKeyWord = searchTextField.getText2().trim();
        String path = getRootPath();
        if (ignoreKeyword) {
            searchKeyWord = null;
        }
        setTableData2(searchKeyWord, path);
//        refreshCurrentPath();
    }

    public String getRootPath() {
        /*String path = ZkConnect.rootPath;
        if (testCheckBox.isSelected()) {
            path = ZkConnect.rootPathTest;
        }
        String currentZkPath = ZkUtil.getCurrentZkRootPaths(configInfo);
        if (this.configInfo.isActivate() && (!ValueWidget.isNullOrEmpty(currentZkPath))) {
            path = currentZkPath;
        }*/
        if (null == zkConnItem) {
            return "";
        }
        return zkConnItem.getZkEnvironment().getZkRootPath();
    }

    public void setTableData2(String searchKeyWord, String path) {
        TableUtil3.setTableData2(zkNodeTable, new Object[0][], columnNames);
        try {
            resultMap = ZkConnect.search(path, zkConnItem.getZk());

//            System.out.println("map :" + resultMap);
            Object[][] datas = null;
            if (ValueWidget.isNullOrEmpty(searchKeyWord)) {
                datas = parseTableDate(resultMap);
            } else {
                if (fuzzyCheckBox.isSelected()) {
                    Map<String, String> filteMap = new TreeMap<>();
                    Set<String> keys = resultMap.keySet();
                    for (String key : keys) {
                        if (key.contains(searchKeyWord) || RegexUtil.contain2(key, searchKeyWord)) {
                            filteMap.put(key, resultMap.get(key));
                        }
                    }
                    datas = parseTableDate(filteMap);
                } else {
                    datas = accurateQuery(searchKeyWord);
                }
            }
            statusLabel.setText("结果: 共" + datas.length + " 条数据");
            TableUtil3.setTableData2(zkNodeTable, datas, columnNames);
            rendTable();
        } catch (KeeperException e1) {
            //org.apache.zookeeper.KeeperException$ConnectionLossException: KeeperErrorCode = ConnectionLoss for /aliinte/C0129_store/1.0.0
            //	at org.apache.zookeeper.KeeperException.create(KeeperException.java:99)
            e1.printStackTrace();
            GUIUtil23.errorDialog(e1);
            //增加重连机制
            if (e1.getMessage().contains("KeeperErrorCode = ConnectionLoss for")) {
                ToastMessage.toast("将重新连接", 2000);
                reconnectAction();
            }
        } catch (InterruptedException e1) {
            e1.printStackTrace();
            GUIUtil23.errorDialog(e1);
        }
    }

    public Object[][] accurateQuery(String searchKeyWord) {
        Object[][] datas;
        String searchVal = resultMap.get(searchKeyWord);
        if (searchVal == null) {
            searchVal = "没有找到";
            ToastMessage.toast("没有找到:" + searchKeyWord, 2000, Color.red);
        } else {
            searchVal = "\"" + searchVal + "\"";
        }
        datas = new Object[1][];
        Object[] objs = new Object[5];
        objs[0] = searchKeyWord;
        objs[1] = searchVal;
        addOperateBtn(searchKeyWord, objs, searchVal);
        datas[0] = objs;
        return datas;
    }

    /***
     * 刷新表格
     */
    public void repaintTable() {
        zkNodeTable.repaint();
    }

    /***
     * setCellEditor and setCellRenderer
     */
    public void rendTable() {
        TableColumnModel columnModel = this.zkNodeTable.getColumnModel();
//        int width=120;
//        TableColumn tableColumn0=columnModel.getColumn(0);
//        tableColumn0.setWidth(width);
//        tableColumn0.setMinWidth(width);
//        TableColumn tableColumn1=columnModel.getColumn(1);
//        tableColumn1.setWidth(width);
//        tableColumn1.setMinWidth(width);
        TableColumn tableColumn2 = columnModel.getColumn(2);
        int btnWidth = 60;
        tableColumn2.setMinWidth(btnWidth);
        tableColumn2.setMaxWidth(btnWidth);
        tableColumn2.setCellEditor(new MyButtonEditor());

        tableColumn2
                .setCellRenderer(new MyButtonRender());


        TableColumn tableColumn3 = columnModel.getColumn(3);
        tableColumn3.setMinWidth(btnWidth);
        tableColumn3.setMaxWidth(btnWidth);
        tableColumn3.setCellEditor(new MyButtonEditor());
        tableColumn3
                .setCellRenderer(new MyButtonRender());


        TableColumn tableColumn4 = columnModel.getColumn(4);
        tableColumn4.setMinWidth(80);
        tableColumn4.setMaxWidth(100);
        tableColumn4.setCellEditor(new MyButtonEditor());
        tableColumn4
                .setCellRenderer(new MyButtonRender());

        repaintTable();//单元格更新access_token 后,需要更新单元格
    }

    public Object[][] parseTableDate(Map<String, String> map) {
        Set<String> keys = map.keySet();
        Object[][] datas = new Object[keys.size()][];
        int index = 0;
        for (String key : keys) {
            Object[] objs = new Object[5];
            objs[0] = key;
            String nodeVal = map.get(key);
            objs[1] = "\"" + nodeVal + "\"";
            addOperateBtn(key, objs, nodeVal);

            datas[index] = objs;
            index++;
        }
        return datas;
    }

    public void addOperateBtn(String key, Object[] objs, String nodeVal) {
        objs[2] = new DelButton(key) {//删除,有确认提示
            @Override
            public void callback() {
                super.callback();
                searchAction();
            }
        }.setZkConnect(zkConnectMgmt).setRootPath(getRootPath());

        objs[3] = new EditButton(key) {//编辑
            @Override
            public void action(String nodeKey) {
                keyTextField.setText(getNodeKey(), true);
                valTextField.setText(getNodeValue());
            }
        }.setNodeValue(nodeVal);

        objs[4] = new EnterDirectoryBtn(key) {
            @Override
            public void action(String nodeKey) {
//                    super.callback();
//                configInfo.setZkRootPath(getRootPath() + nodeKey);
//                configInfo.saveZkRootPath(getRootPath() + nodeKey);
                zkConnItem.getZkEnvironment().setZkRootPath(SystemHWUtil.mergeTwoPath(getRootPath(), nodeKey));
                refreshCurrentPath();
                searchAction(true);
            }
        }.setRootPath(getRootPath());
    }

    public boolean check() {
        if (!DialogUtil.verifyTFEmpty(keyTextField, "键")) {
            return true;
        }
        if (!DialogUtil.verifyTFEmpty(valTextField, "键值")) {
            return true;
        }
        return false;
    }

    public void updateZkNode(ZooKeeper zk, String rootPath) {
        try {
            String oldVal = ZkConnect.updateNode(zk, SystemHWUtil.mergeTwoPath(rootPath, keyTextField.getText2()), valTextField.getText());
            ZkConnect.clearCache(rootPath);
            ToastMessage.toast("更新成功,原值:" + oldVal, 2000);
        } catch (Exception e1) {
            e1.printStackTrace();
            ToastMessage.toast("请确认该节点是否已经存在", 3000, Color.red);
        }
    }

    public void createZkNode(ZooKeeper zooKeeper, String rootPath) {
        try {
            ZkConnect.createNode(zooKeeper, SystemHWUtil.mergeTwoPath(rootPath, keyTextField.getText2()), valTextField.getText());
            ZkConnect.clearCache(rootPath);
            ToastMessage.toast("添加成功", 2000);
        } catch (Exception e1) {
            e1.printStackTrace();
            ToastMessage.toast("可能已经存在:" + e1.getMessage(), 3000, Color.RED);
        }
    }

    public static int getOtherEnvIndex() {
        return Integer.MAX_VALUE - 1;
    }

    /***
     * 连接zk
     * @throws Exception
     */
    public void connectServer(boolean force) throws Exception {
        String msg = "尝试连接zk.....";
        System.out.println("msg :" + msg);
        /*ZooKeeper zkConnItem = */
        int index = 0;
        if (this.configInfo.isActivate()) {
            index = configInfo.getEnvIndex();
            System.out.println("index :" + index);
            if (zkConnectMgmt.hasIndex(index) && (!force)) {
                zkConnItem = zkConnectMgmt.getZK(index);
                msg = "不用连接zk,从缓存获取.";
                System.out.println("msg :" + msg);
                return;
            }
//            ZkUtil.connect(index, configInfo, zkConnect);
            zkConnItem = ZkConnect.connect(configInfo.getCurrentEnvironment());
        } else {
//            ZkConnect.connect(null, null);
//            index = getOtherEnvIndex();
        }
//        zkConnect = ZkConnect.connect();
//        zkConnItem = zkConnect.getZk();
        if (null == zkConnItem) {
            return;
        }
        zkConnectMgmt.put(index, zkConnItem);
        zkConnectMgmt.setCurrZk(zkConnItem);

    }

    /***
     * 只有子类调用了GenericFrame.init33() 方法,beforeDispose() 才会被调用
     */
    @Override
    public void beforeDispose() {
        super.beforeDispose();
        closeZk();
        saveConfig();
    }

    public void closeZk() {
        if (null != zkConnItem) try {
            zkConnItem.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /***
     * 读取配置文件
     * @throws IOException
     */
    private boolean readConfig() throws IOException {
        configFile = new File(configFilePath);
        if (!configFile.exists()) {
            configFile = new File(".zookeeper_editer.properties");
        }
        if (!configFile.exists()) {
            return false;
        }
        InputStream inStream = new FileInputStream(configFile);
        String resumeInput = FileUtils.getFullContent4(inStream, SystemHWUtil.CHARSET_UTF);
        inStream.close();//及时关闭资源
        if (ValueWidget.isNullOrEmpty(resumeInput)) {
            GUIUtil23.warningDialog("请先去进行配置");
            return false;
        }
        this.configInfo = (ConfigInfo) HWJacksonUtils.deSerialize(resumeInput, ConfigInfo.class);
        //初始化 ZkConnectMgmt
        zkConnectMgmt = new ZkConnectMgmt(this.configInfo);
        return true;
    }

    private void setMenuBar2() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileM = new JMenu("File");
        JMenuItem configItem = new JMenuItem("配置");
        JMenuItem connectItem = new JMenuItem("重新连接");
        configItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showConfig();

            }
        });

        connectItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int result = JOptionPane.showConfirmDialog(null, "确认要重新连接吗 ?", "确认",
                        JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    reconnectAction();
                }
            }
        });
        fileM.add(configItem);
        menuBar.add(fileM);

        MenuBarListener menuBarListener = new MenuBarListener(configInfo);
        JMenu cleanCacheM = new JMenu("清除缓存");
        JMenuItem cleanAllItem = new JMenuItem("清除全部");
        cleanAllItem.addActionListener(menuBarListener);
        JMenuItem cleanOneItemItem = new JMenuItem("清除当前路径");
        cleanOneItemItem.addActionListener(menuBarListener);

        cleanCacheM.add(cleanAllItem);
        cleanCacheM.add(cleanOneItemItem);

        menuBar.add(cleanCacheM);
//        menuBar.add(configItem);
        menuBar.add(connectItem);
        setJMenuBar(menuBar);
    }

    /***
     * 重新连接<br />
     * 调用时机:<br />
     * 1,程序启动时;<br />
     * 2,抛异常:org.apache.zookeeper.KeeperException$ConnectionLossException: KeeperErrorCode = ConnectionLoss for /aliinte/C0129_store/1.0.0
     at org.apache.zookeeper.KeeperException.create(KeeperException.java:99)
     */
    public void reconnectAction() {
        closeZk();//先关闭连接,再创建新的连接
        try {
            connectServer(true);
            ToastMessage.toast("连接成功", 2000);
        } catch (Exception e1) {
            e1.printStackTrace();
            ToastMessage.toast("连接失败", 2000, Color.red);
        }
    }

    public void showConfig() {
        new ConfigDialog(ZkEditorApp.this.configInfo).setVisible(true);

    }

    /***
     * 增加全局快捷键Shift+Tab<br>
     * Ctrl+Shift+X
     */
    private void addGlobalKey() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        // 注册应用程序全局键盘事件, 所有的键盘事件都会被此事件监听器处理.
        toolkit.addAWTEventListener(
                new java.awt.event.AWTEventListener() {
                    public void eventDispatched(AWTEvent event) {
                        if (event.getClass() != KeyEvent.class) {
                            return;
                        }
                        KeyEvent kE = ((KeyEvent) event);
                        if (kE.getKeyCode() == KeyEvent.VK_K/*Ctrl +K*/
                                && (kE.isControlDown() || kE.isMetaDown())
                                && kE.getID() == KeyEvent.KEY_PRESSED) {
                            searchTextField.requestFocus();
                            searchTextField.selectAll();
                        } else if ((kE.getKeyCode() == KeyEvent.VK_COMMA) && (!((InputEvent) event)//显示配置面板,mac :command;window:ctrl
                                .isShiftDown()) && (EventHWUtil.isControlDown((InputEvent) event) || ((InputEvent) event).isControlDown()) && kE.getID() == KeyEvent.KEY_PRESSED) {//Ctrl+H
                            showConfig();
                        }
                    }
                }, java.awt.AWTEvent.KEY_EVENT_MASK);
    }

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
            FileUtils.writeToFile(configFile, HWJacksonUtils.getJsonP(this.configInfo), SystemHWUtil.CHARSET_UTF);
            CMDUtil.hide(configFilePath);
        }
    }
}

