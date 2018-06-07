package com.kunlunsoft.redis;

import com.swing.component.AssistPopupTextArea;
import com.swing.component.AssistPopupTextField;
import com.swing.dialog.DialogUtil;
import com.swing.dialog.GenericFrame;

import javax.swing.*;
import java.awt.*;

public class RedisMgmtApp {
    private AssistPopupTextField idTextField1;
    private AssistPopupTextField keyTextField1;
    private AssistPopupTextArea valTextArea1;
    private JButton saveButton;
    private AssistPopupTextField id2TextField1;
    private AssistPopupTextField val2TextField2;
    private AssistPopupTextArea resultTextArea1;
    private JPanel rootPane;

    /***
     * 初始化界面
     */
    private void initUI() {

    }

    public RedisMgmtApp() {

    }

    public static void main(String[] args) {
        DialogUtil.lookAndFeel2();
        RedisMgmtApp redisMgmtApp = new RedisMgmtApp();
        GenericFrame genericFrame = new GenericFrame() {
            @Override
            public void layout3(Container contentPane) {
                setContentPane(redisMgmtApp.getRootPane());
                init33(this);
//                fullScreen();
                setLoc(500, 600);
            }
        };
        genericFrame.launchFrame();
    }

    public JPanel getRootPane() {
        return rootPane;
    }
}
