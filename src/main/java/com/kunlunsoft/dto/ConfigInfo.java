package com.kunlunsoft.dto;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/***
 * 配置<br />
 * 注意:在操作过程中,环境信息 统一从ConnItem 获取,不要从 ConfigInfo 中获取
 */
public class ConfigInfo {
    protected static final Logger logger = Logger.getLogger(ConfigInfo.class);
    /***
     * 不同的环境
     */
    private List<ZkEnvironment> environments;
    private boolean activate = true;
    /***
     * 备注
     */
    private String remark;
    /***
     * 环境
     */
    private int envIndex;
    /***
     * 当前环境名称
     */
    private String currEnvDisp;

    public List<ZkEnvironment> getEnvironments() {
        return environments;
    }

    public void setEnvironments(List<ZkEnvironment> environments) {
        this.environments = environments;
    }

    public boolean isActivate() {
        return activate;
    }

    public void setActivate(boolean activate) {
        this.activate = activate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getEnvIndex() {
        return envIndex;
    }

    public void setEnvIndex(int envIndex) {
        this.envIndex = envIndex;
    }

    public ZkEnvironment getZkEnvironment(Integer envIndex) {
        ZkEnvironment zkEnvironment = getZkEnvironmentByIndex(envIndex);
        if (null == zkEnvironment) {
            zkEnvironment = new ZkEnvironment();
            this.environments.set(envIndex, zkEnvironment);
        }
        return zkEnvironment;
    }

    public ZkEnvironment getCurrentEnvironment() {
        int selectedIndex = getEnvIndex();
        return getZkEnvironmentByIndex(selectedIndex);
    }

    private ZkEnvironment getZkEnvironmentByIndex(int selectedIndex) {
        if (null == this.environments) {
            this.environments = new ArrayList<>();
        }
        int length = this.environments.size();
        if (selectedIndex >= length) {
            int delta = selectedIndex - length + 1;
            for (int i = 0; i < delta; i++) {
                this.environments.add(new ZkEnvironment());
            }
        }
        return this.environments.get(selectedIndex);
    }

    public String getCurrentIp() {
        ZkEnvironment zkEnvironment = getCurrentEnvironment();
        return zkEnvironment.getIp();
    }

    public Integer getCurrentPort() {
        ZkEnvironment zkEnvironment = getCurrentEnvironment();
        return zkEnvironment.getPort();
    }

    public String getgetCurrentPortStr() {
        Integer port = getCurrentPort();
        if (null == port) {
            return "";
        }
        return String.valueOf(port);
    }

    public String getCurrEnvDisp() {
        return currEnvDisp;
    }

    public void setCurrEnvDisp(String currEnvDisp) {
        this.currEnvDisp = currEnvDisp;
    }
}
