package com.yscoco.blue.utils;

import java.io.Serializable;

/**
 * 作者：karl.wei
 * 创建日期： 2018/5/22 0022
 * 邮箱：karl.wei@yscoco.com
 * QQ：2736764338
 * 类介绍：电池电量
 */

public class VoltBean implements Serializable{
    public Integer position;
    public float curretVolt = -1;
    public float lastVolt = -1;

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public float getCurretVolt() {
        return curretVolt;
    }

    public void setCurretVolt(float curretVolt) {
        this.curretVolt = curretVolt;
    }

    public float getLastVolt() {
        return lastVolt;
    }

    public void setLastVolt(float lastVolt) {
        this.lastVolt = lastVolt;
    }
}
