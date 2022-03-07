package com.yscoco.blue;
/**
 * Created by Administrator on 2017/12/9 0009.
 */

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.yscoco.blue.base.BaseBlueDriver;
import com.yscoco.blue.imp.BlueListener;
import com.yscoco.blue.imp.NotifyListener;
import com.yscoco.blue.utils.NotifyUtils;
import com.yscoco.blue.utils.VoltUtils;

/**
 * 作者：karl.wei
 * 创建日期： 2017/12/9 0009 13:46
 * 邮箱：karl.wei@yscoco.com
 * QQ:2736764338
 * 类介绍：BaseBlueDriver实现类
 */
public class MyBlueDriver extends BaseBlueDriver {
    private static MyBlueDriver myDriver ;
    public static  MyBlueDriver getInstance(Context context){
        if(myDriver==null){
            synchronized (MyBlueDriver.class) {
                if(myDriver==null) {
                    myDriver = new MyBlueDriver(context);
                }
            }
        }
        return myDriver;
    }
    private MyBlueDriver(Context context){
        super(context);
    }
    @Override
    public void dataHandler(String uuid,String address, byte[] data) {
        StringBuffer b = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            b.append(String.format("%02X ", data[i]).toString().trim());
        }
        Message msg = new Message();
        Bundle bundle = new Bundle();
        bundle.putString("uuid",uuid);
        bundle.putByteArray("value",data);
        bundle.putString("data",b.toString());
        msg.setData(bundle);
        msg.what = BtManager.NOTIFY_VALUE;
        handlerMsg(address,msg);
    }

    @Override
    public void handlerMsg(String address, Message msg) {
        int what = msg.what;
        switch (what){
            /*设备连接成功*/
            case BtManager.CONNECTED:
                VoltUtils.clear();
                removeReconnect(address);
                if(mListeners!=null) {
                    for (BlueListener listner : mListeners) {
                        listner.onConnected(address);
                    }
                }
                break;
            /*设备断开连接*/
            case BtManager.DISCONNECT:
                Log.e("blue","移除设备"+address);
                mBtManagerMaps.remove(address);
                Log.e("blue","移除设备"+mBtManagerMaps.size());
                if(mListeners!=null) {
                    for (BlueListener listner : mListeners) {
                        listner.onDisConnected(address);
                    }
                }
                break;
            /*在通道开启成功后显示成功*/
            case BtManager.NOTIFY_ON:
                if(mListeners!=null) {
                    for (BlueListener listner : mListeners) {
                        listner.onNotifySuccess(address);
                    }
                }
                break;
            case BtManager.RE_CONNECT:
                addReconnect(address);
                if(mListeners!=null) {
                    for (BlueListener listner : mListeners) {
                        listner.reConnected(address);
                    }
                }
                break;
            case BtManager.NOTIFY_VALUE:
                Bundle bundle = msg.getData();
                String uuid = bundle.getString("uuid");
                byte[] data = bundle.getByteArray("value");
                if(mNotifyListeners!=null&&mNotifyListeners.size()>0) {
                    NotifyUtils.notify(uuid,address,data, mNotifyListeners);
                }
                break;
            case BtManager.NOTIFY_RSSI:
                int rssi =((int) msg.obj);
                if(mNotifyListeners!=null&&mNotifyListeners.size()>0) {
                    for (NotifyListener listner : mNotifyListeners) {
                    }
                }
                break;
                default:
        }
    }
}
