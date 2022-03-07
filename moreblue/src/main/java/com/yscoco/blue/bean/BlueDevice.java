package com.yscoco.blue.bean;

import android.bluetooth.BluetoothDevice;

/**
 * 作者：karl.wei
 * 创建日期： 2017/12/09
 * 邮箱：karl.wei@yscoco.com
 * QQ：2736764338
 * 类介绍：蓝牙连接实体类
 */
public class BlueDevice {
    private BluetoothDevice device;
    private int rssi;
    private boolean connectState=false;

    public boolean isConnectState() {
        return connectState;
    }

    public void setConnectState(boolean connectState) {
        this.connectState = connectState;
    }

    public BlueDevice(BluetoothDevice device, int rssi) {
        this.device = device;
        this.rssi = rssi;
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

}
