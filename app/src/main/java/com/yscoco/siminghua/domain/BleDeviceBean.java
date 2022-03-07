package com.yscoco.siminghua.domain;

import com.clj.fastble.data.BleDevice;

import java.io.Serializable;

/**
 * Created by ZhangZeZhi on 2018\10\24 0024.
 */

public class BleDeviceBean implements Serializable {

    private BleDevice bleDevice;
    private boolean isAdd;   //是否添加
    private boolean connectable = false;
    private String bleDeviceName;
    private int electricity;

    public BleDevice getBleDevice() {
        return bleDevice;
    }

    public BleDeviceBean setBleDevice(BleDevice bleDevice) {
        this.bleDevice = bleDevice;
        return this;
    }

    public boolean isAdd() {
        return isAdd;
    }

    public BleDeviceBean setAdd(boolean add) {
        isAdd = add;
        return this;
    }

    public String getBleDeviceName() {
        return bleDeviceName;
    }

    public BleDeviceBean setBleDeviceName(String bleDeviceName) {
        this.bleDeviceName = bleDeviceName;
        return this;
    }

    public boolean isConnectable() {
        return connectable;
    }

    public BleDeviceBean setConnectable(boolean connectable) {
        this.connectable = connectable;
        return this;
    }

    public int getElectricity() {
        return electricity;
    }

    public BleDeviceBean setElectricity(int electricity) {
        this.electricity = electricity;
        return this;
    }

    @Override
    public String toString() {
        return "BleDeviceBean{" +
                "bleDevice=" + bleDevice +
                ", isAdd=" + isAdd +
                ", connectable=" + connectable +
                ", bleDeviceName='" + bleDeviceName + '\'' +
                '}';
    }

}
