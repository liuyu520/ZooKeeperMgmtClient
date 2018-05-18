package com.kunlunsoft.component;

/**
 * Created by whuanghkl on 17/5/31.
 */
public abstract class EnterDirectoryBtn extends MyButton {
    private String nodeValue;

    public EnterDirectoryBtn(String nodeKey) {
        super("进入目录", nodeKey);
        super.bindEvent();
    }

    public String getNodeValue() {
        return nodeValue;
    }

    public EnterDirectoryBtn setNodeValue(String nodeValue) {
        this.nodeValue = nodeValue;
        return this;
    }

    @Override
    public void action(String nodeKey) {

    }
}
