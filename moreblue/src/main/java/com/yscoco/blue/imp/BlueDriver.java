package com.yscoco.blue.imp;

import android.bluetooth.BluetoothDevice;

import java.util.ArrayList;
import java.util.Set;

/**
 * 作者：karl.wei
 * 创建日期： 2017/12/09
 * 邮箱：karl.wei@yscoco.com
 * QQ：2736764338
 * 类介绍：设备管理基类
 */

public interface BlueDriver {
    /**
     * 连接设备
     * @param mac
     * @return
     */
    boolean connect(String mac, BluetoothDevice mDevice, boolean isReconnect);
    /**
     * 设备是否连接
     * @param mac
     * @return
     */
    boolean isConnect(String mac);
    /**
     * 设备是否断开连接过
     * @param mac
     * @return
     */
    boolean isDisConnect(String mac);
    /**
     * 断开连接的设备
     * @param mac
     * @param isReconnect  是否重连
     */
    void disConnect(String mac, boolean isReconnect);
    ArrayList<BluetoothDevice> getConnectDevice();
    /**
     * 断开所有连接的设备
     */
    void disConnectAll(String mac);

    /**
     * 设置设备是否重连
     * @param mac
     * @param isReconnect 是否重连
     */
    void setReconnect(String mac, boolean isReconnect);

    boolean isReconnect(String mac);

    void addListener(BlueListener listener);
    void removeListener(BlueListener listener);
     void addNotifyListener(NotifyListener listener);
    void removeNotifyListener(NotifyListener listener);
    /**
     * 读取RSSI值
     */
    void startReadRssi(String mac);
    /**
     * 停止读取RSSI值
     */
    void stopReadRssi(String mac);
    /**
     * 发送数据
     */
    boolean writeData(String mac, byte[] cmd);
    /**
     * 发送特定服务和属性的数据
    */
    boolean writeData(String mac, byte[] cmd, String serviceUUID, String charUUID);

    /**
     * 读取数据
     */
    void readData(String mac);
    /**
     * 读取特定服务和属性的数据
     */
    void readData(String mac, String serviceUUID, String charUUID);
    /**
     * 获取当前连接的设备列表
     */
    int getConnectSize();
    /**
     * 获取需要回连的设备列表
     */
    Set<String> getReconnect();
    /**
     * 添加需要回连的设备列表
     */
    void addReconnect(String mac);
    /**
     * 移除需要回连的设备
     */
    void removeReconnect(String mac);
    /**
     * 判断当前设备是否在回连列表
     */
    boolean isReconnectList(String mac);
}
