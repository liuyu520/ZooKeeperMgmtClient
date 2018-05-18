package com.kunlunsoft.event.listener;

import com.google.common.eventbus.Subscribe;
import com.kunlunsoft.event.ZkModifyEvent;
import com.kunlunsoft.util.ZkConnect;
import com.swing.component.ComponentUtil;

import javax.swing.*;

public class ZkModifyListener {
    private JTextArea resultTextArea;

    public ZkModifyListener(JTextArea resultTextArea) {
        this.resultTextArea = resultTextArea;
        ZkConnect.getEventBus().register(this);
    }

    @Subscribe
    public void logEvent(final ZkModifyEvent event) {
        ComponentUtil.appendResult(resultTextArea, "old value:" + event.getOldValue() + " , path:" + event.getPath(), true);
    }
}
