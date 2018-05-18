package com.kunlunsoft.dialog;

import com.kunlunsoft.dto.ConfigInfo;
import com.kunlunsoft.dto.ZkEnvironment;
import com.kunlunsoft.event.SaveConfigEvent;
import com.kunlunsoft.util.ZkConnect;
import com.string.widget.util.ValueWidget;
import com.swing.component.AssistPopupTextArea;
import com.swing.component.AssistPopupTextField;
import com.swing.dialog.DialogUtil;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class ConfigDialog extends JDialog {

    private final JPanel contentPanel = new JPanel();
    private AssistPopupTextField porttextField;
    private ConfigInfo configInfo;
    private AssistPopupTextArea ipTextArea;
    private JCheckBox activateCheckBox;
    private AssistPopupTextField rootpathtextField;
    /***
     * 备忘
     */
    private AssistPopupTextArea remarkTextArea;
    private JComboBox envComboBox;

    /**
     * Launch the application.
     */
    /*public static void main(String[] args) {
		try {
			ConfigDialog dialog = new ConfigDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
*/

    /**
     * Create the dialog.
     */
    public ConfigDialog(final ConfigInfo configInfo) {
        this.configInfo = configInfo;
        setModal(true);
        setTitle("zookeeper 配置面板");
        setBounds(100, 100, 450, 300);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        GridBagLayout gbl_contentPanel = new GridBagLayout();
        gbl_contentPanel.columnWidths = new int[]{64, 0, 0};
        gbl_contentPanel.rowHeights = new int[]{0, 0, 0, 0, 0, 0};
        gbl_contentPanel.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
        gbl_contentPanel.rowWeights = new double[]{1.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        contentPanel.setLayout(gbl_contentPanel);
        {
            JScrollPane scrollPane = new JScrollPane();
            GridBagConstraints gbc_scrollPane = new GridBagConstraints();
            gbc_scrollPane.gridwidth = 2;
            gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
            gbc_scrollPane.fill = GridBagConstraints.BOTH;
            gbc_scrollPane.gridx = 0;
            gbc_scrollPane.gridy = 0;
            contentPanel.add(scrollPane, gbc_scrollPane);
            {
                ipTextArea = new AssistPopupTextArea();
                ipTextArea.setText(configInfo.getCurrentIp());
                scrollPane.setViewportView(ipTextArea);
                Border borderResultTADMh = BorderFactory.createEtchedBorder(Color.white,
                        new Color(148, 145, 140));
                TitledBorder resultTitleDMh = new TitledBorder(borderResultTADMh, "服务器ip");
                scrollPane.setBorder(resultTitleDMh);
            }
        }
        {
            remarkTextArea = new AssistPopupTextArea();
            remarkTextArea.placeHolder("备注");
            if (!ValueWidget.isNullOrEmpty(this.configInfo.getRemark())) {
                remarkTextArea.setText(this.configInfo.getRemark());
            }
            GridBagConstraints gbc_textArea = new GridBagConstraints();
            gbc_textArea.gridwidth = 2;
            gbc_textArea.insets = new Insets(0, 0, 5, 0);
            gbc_textArea.fill = GridBagConstraints.BOTH;
            gbc_textArea.gridx = 0;
            gbc_textArea.gridy = 1;
            contentPanel.add(remarkTextArea, gbc_textArea);
        }
        JLabel label = new JLabel("端口号");
        GridBagConstraints gbc_label = new GridBagConstraints();
        gbc_label.anchor = GridBagConstraints.EAST;
        gbc_label.insets = new Insets(0, 0, 5, 5);
        gbc_label.gridx = 0;
        gbc_label.gridy = 2;
        contentPanel.add(label, gbc_label);


        porttextField = new AssistPopupTextField();
        porttextField.setText(this.configInfo.getgetCurrentPortStr());
        porttextField.setToolTipText("端口号默认是2181");
        GridBagConstraints gbc_porttextField = new GridBagConstraints();
        gbc_porttextField.insets = new Insets(0, 0, 5, 0);
        gbc_porttextField.fill = GridBagConstraints.HORIZONTAL;
        gbc_porttextField.gridx = 1;
        gbc_porttextField.gridy = 2;
        contentPanel.add(porttextField, gbc_porttextField);
        porttextField.setColumns(10);

        JLabel label2 = new JLabel("环境");
        GridBagConstraints gbc_label2 = new GridBagConstraints();
        gbc_label2.anchor = GridBagConstraints.EAST;
        gbc_label2.insets = new Insets(0, 0, 5, 5);
        gbc_label2.gridx = 0;
        gbc_label2.gridy = 3;
        contentPanel.add(label2, gbc_label2);

        envComboBox = new JComboBox();
        envComboBox.addItem("测试");
        envComboBox.addItem("集测");
        envComboBox.addItem("模拟");
        envComboBox.addItem("线上");
        envComboBox.setSelectedIndex(configInfo.getEnvIndex());
        GridBagConstraints gbc_envComboBox = new GridBagConstraints();
        gbc_envComboBox.insets = new Insets(0, 0, 5, 0);
        gbc_envComboBox.fill = GridBagConstraints.HORIZONTAL;
        gbc_envComboBox.gridx = 1;
        gbc_envComboBox.gridy = 3;
        contentPanel.add(envComboBox, gbc_envComboBox);

        envComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    int selectedIndex = envComboBox.getSelectedIndex();

//                    List<String> ips = ZkUtil.fillConfigIps(selectedIndex, configInfo);
//                    List<String> zkRootPaths = ZkUtil.fillConfigZkRootPaths(selectedIndex, configInfo);
                    /*if (selectedIndex >= ips.size()) {
                        ipTextArea.setText(SystemHWUtil.EMPTY);
                        return;
                    }*/
                    ZkEnvironment zkEnvironment = configInfo.getZkEnvironment(selectedIndex);
                    ipTextArea.setText(zkEnvironment.getIp());

                    rootpathtextField.setText(zkEnvironment.getZkRootPath());
                }
            }
        });

        JLabel lblRootpath = new JLabel("rootPath");
        GridBagConstraints gbc_lblRootpath = new GridBagConstraints();
        gbc_lblRootpath.anchor = GridBagConstraints.EAST;
        gbc_lblRootpath.insets = new Insets(0, 0, 0, 5);
        gbc_lblRootpath.gridx = 0;
        gbc_lblRootpath.gridy = 4;
        contentPanel.add(lblRootpath, gbc_lblRootpath);


        rootpathtextField = new AssistPopupTextField();
        rootpathtextField.setText(this.configInfo.getCurrentEnvironment().getZkRootPath());
        GridBagConstraints gbc_rootpathtextField = new GridBagConstraints();
        gbc_rootpathtextField.fill = GridBagConstraints.HORIZONTAL;
        gbc_rootpathtextField.gridx = 1;
        gbc_rootpathtextField.gridy = 4;
        contentPanel.add(rootpathtextField, gbc_rootpathtextField);
        rootpathtextField.setColumns(10);


        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                JButton okButton = new JButton("OK");
                okButton.setActionCommand("OK");
                okButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        saveAction(configInfo);
                        ConfigDialog.this.dispose();
                    }
                });
                {
                    activateCheckBox = new JCheckBox("激活");
                    activateCheckBox.setSelected(ConfigDialog.this.configInfo.isActivate());
                    buttonPane.add(activateCheckBox);
                }
                buttonPane.add(okButton);
                getRootPane().setDefaultButton(okButton);
            }
            {
                JButton cancelButton = new JButton("Cancel");
                cancelButton.setActionCommand("Cancel");
                cancelButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        ConfigDialog.this.dispose();
                    }
                });
                buttonPane.add(cancelButton);
            }
            DialogUtil.escape2CloseDialog(this);
        }
    }

    public void saveAction(ConfigInfo configInfo) {
        int selectedIndex = envComboBox.getSelectedIndex();
        ZkEnvironment zkEnvironment = configInfo.getZkEnvironment(selectedIndex);
        ConfigDialog.this.configInfo.setEnvIndex(selectedIndex);
        zkEnvironment.setIp(ipTextArea.getText());
//        ips.set(selectedIndex, ipTextArea.getText());
        ConfigDialog.this.configInfo
//                .setPort(porttextField.getText())
                .setActivate(activateCheckBox.isSelected());
        if (!ValueWidget.isNullOrEmpty(rootpathtextField.getText())) {
            zkEnvironment.setZkRootPath(rootpathtextField.getText().trim());

        }
        if (ConfigDialog.this.configInfo.isActivate()) {//只有选中"激活"才会触发 TODO
            SaveConfigEvent saveConfigEvent = new SaveConfigEvent();
            ZkConnect.getEventBus().post(saveConfigEvent);
        }
    }

}
