package com.yscoco.blue.imp;

/**
 * 作者：karl.wei
 * 创建日期： 2017/12/09
 * 邮箱：karl.wei@yscoco.com
 * QQ：2736764338
 * 类介绍：蓝牙连接状态回调
 */

public interface BlueListener {
    /**
     * 设备连接成功
     * @param mac
     */
    void onConnected(String mac);
    /**
     * 设备断开连接
     */
    void onDisConnected(String mac);
    /**
     * 发现服务
     */
    void onNotifySuccess(String mac);

    /**
     * 重连机制
     */
    void reConnected(String mac);
}
