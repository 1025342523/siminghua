package com.yscoco.blue.utils;/**
 * Created by Administrator on 2018/1/6 0006.
 */

import android.util.Log;

import com.yscoco.blue.BtManager;
import com.yscoco.blue.imp.NotifyListener;

import java.util.Set;

/**
 * 作者：karl.wei
 * 创建日期： 2018/1/6 0006 16:54
 * 邮箱：karl.wei@yscoco.com
 * QQ:2736764338
 * 类介绍：处理notify返回的数据结果
 */
public class NotifyUtils {
    public static void notify(String uuid, String mac,byte[] data, Set<NotifyListener> mNotifyListener){
        StringBuffer b = new StringBuffer();
        for (byte bytes:data) {
            b.append(String.format("%02X ", bytes).toString().trim());
        }
        Log.e("blue",b.toString()+"mNotifyListener.size"+mNotifyListener.size());
        FileWriteUtils.initWrite(b.toString()+"mNotifyListener.size"+mNotifyListener.size());
        if(uuid.toUpperCase().equals(BtManager.CHA_BATTERY_NOTIFY.toUpperCase())){
            for (NotifyListener listner : mNotifyListener) {
                listner.battery(data[0]&0xFF);
            }
        }else {
            for (NotifyListener listner : mNotifyListener) {
                listner.change(data);
            }
        }
    }
}
