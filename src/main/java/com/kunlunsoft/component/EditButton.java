package com.kunlunsoft.component;

/**
 * Created by whuanghkl on 17/5/31.
 */
public abstract class EditButton extends MyButton {
    private String nodeValue;

    public EditButton(String nodeKey) {
        super("编辑", nodeKey);
        super.bindEvent();
    }

    public String getNodeValue() {
        return nodeValue;
    }

    public EditButton setNodeValue(String nodeValue) {
        this.nodeValue = nodeValue;
        return this;
    }
}
