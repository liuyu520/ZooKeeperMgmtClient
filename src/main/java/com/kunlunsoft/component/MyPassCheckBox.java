package com.kunlunsoft.component;

import javax.swing.JCheckBox;


public class MyPassCheckBox extends JCheckBox {
    private static final long serialVersionUID = -5655090604649008682L;
    private String zNodePath;

    public MyPassCheckBox(String string) {
        super(string);
    }

    public String getzNodePath() {
        return zNodePath;
    }

    public void setzNodePath(String zNodePath) {
        this.zNodePath = zNodePath;
    }
}
