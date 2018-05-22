package com.kunlunsoft.dto;

import javax.swing.*;

public class CheckboxParam {
    private int id;
    private ZkEnvironment zkEnvironment;
    private String displayLabel;
    private JCheckBox checkBox;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ZkEnvironment getZkEnvironment() {
        return zkEnvironment;
    }

    public void setZkEnvironment(ZkEnvironment zkEnvironment) {
        this.zkEnvironment = zkEnvironment;
    }

    public String getDisplayLabel() {
        return displayLabel;
    }

    public void setDisplayLabel(String displayLabel) {
        this.displayLabel = displayLabel;
    }

    public JCheckBox getCheckBox() {
        return checkBox;
    }

    public void setCheckBox(JCheckBox checkBox) {
        this.checkBox = checkBox;
    }
}
