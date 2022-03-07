package com.yscoco.blue.base;/**
 * Created by Administrator on 2017/12/9 0009.
 */

/**
 * 作者：karl.wei
 * 创建日期： 2017/12/9 0009 11:15
 * 邮箱：karl.wei@yscoco.com
 * QQ:2736764338
 * 类介绍：蓝牙的常量工具
 */
public class BaseBtManager {
    // service
    public static final String SERVICE_UUID1 = "0000FFB0-0000-1000-8000-00805F9B34FB";
    //通知
    public static final String CHA_NOTIFY = "0000FFB2-0000-1000-8000-00805F9B34FB";
    //写数据
    public static final String CHA_WRITE = "0000FFB1-0000-1000-8000-00805F9B34FB";

    // service
    public static final String SERVICE_BATTERY_UUID1 = "0000180F-0000-1000-8000-00805F9B34FB";

    //通知
    public static final String CHA_BATTERY_NOTIFY = "00002A19-0000-1000-8000-00805F9B34FB";

    // 开启通知UUID
    public static final String DES_UUID1 = "00002902-0000-1000-8000-00805F9B34FB";
    //设备连接成功
    public static final int CONNECTED = 1001;
    /**
     * 设备断开(设备报警之后的状态，此时调整UI)
     */
    public static final int DISCONNECT = 1002;
    /**
     * 非正常断开
     */
    public static final int IMPROPER_DIS_CONNECT = 1007;
    /**
     * 通道开启成功
     */
    public static final int NOTIFY_ON = 1009;
    /**
     * 重连
     */
    public static final int RE_CONNECT = 1010;

    /**
     * 通知数据
     */
    public static final int NOTIFY_VALUE = 1011;
    /**
     * 通知RSSI变化
     */
    public static final int NOTIFY_RSSI = 1012;


    /*开启设备报警*/
    public static byte[] onAlarms = new byte[]{(byte) 0xFF, (byte) 0x01, (byte) 0x01, (byte) 0x01};
    /*关闭设备报警*/
    public static byte[] offAlarms = new byte[]{(byte) 0xFF, (byte) 0x01, (byte) 0x00, (byte) 0x01};
    /*主动断开*/
    public static byte[] accordDisconnectAlarms = new byte[]{(byte) 0xFF, (byte) 0x02, (byte) 0x01, (byte) 0x00};
    /*寻找功能*/
    public static byte[] findAlarms = new byte[]{(byte) 0xFF, (byte) 0x03, (byte) 0x01, (byte) 0x00};
}
