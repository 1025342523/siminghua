package com.yscoco.blue.base;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.yscoco.blue.BtManager;
import com.yscoco.blue.imp.BlueDriver;
import com.yscoco.blue.imp.BlueListener;
import com.yscoco.blue.imp.NotifyListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
/**
 * 作者：karl.wei
 * 创建日期： 2017/12/09
 * 邮箱：karl.wei@yscoco.com
 * QQ：2736764338
 * 类介绍：设备管理基类
 */

public abstract class BaseBlueDriver implements BlueDriver {
    protected Context mContext;
    /*存储设备mac和设备管理类的关联*/
    protected Map<String, BtManager> mBtManagerMaps = new HashMap<>();
    protected Set<String> mReconnectSet = new HashSet<String>();
    /*通知监听*/
    protected HashSet<BlueListener> mListeners;
    protected HashSet<NotifyListener> mNotifyListeners;
    public BaseBlueDriver() {
    }
    public BaseBlueDriver(Context c) {
        mContext = c;
    }
    @Override
    public boolean connect(String mac, BluetoothDevice mDevice, boolean isReconnect) {
        Log.e("blue","连接中：" + mac+mBtManagerMaps.size());
        //管理单个连接
        BtManager btManager = mBtManagerMaps.get(mac);
        if (btManager == null) {
            btManager = new BtManager(mContext, mac,mDevice, this);
            mBtManagerMaps.put(mac, btManager);
        }else{
            Log.e("blue","有数据");
        }
        btManager.setReconnect(isReconnect);
        return btManager.connected();
    }

    @Override
    public boolean isConnect(String mac) {
        if (mBtManagerMaps.get(mac) != null) {
            return mBtManagerMaps.get(mac).isConnect();
        }
        return false;
    }
    public boolean isDisConnect(String mac) {
        if (mBtManagerMaps.get(mac) != null) {
            return mBtManagerMaps.get(mac).isDisconnected();
        }
        return false;
    }

    @Override
    public void disConnect(String mac, boolean isReconnect) {
        if (mBtManagerMaps.get(mac) != null) {
            Log.e("blue","bluedisConnect"+mBtManagerMaps.size());
            mBtManagerMaps.get(mac).disConnect(mac,isReconnect);
        }
    }

    @Override
    public void disConnectAll(final String mac) {
        Log.e("blue","断开全部的方法是"+mBtManagerMaps.size());
        for (String key : mBtManagerMaps.keySet()) {
            Log.e("blue","bluedisConnectAll");
            if(mac!= null&&key.equals(mac)){
                Log.e("blue","diconnect"+mac);
                continue;
            }else{
                mBtManagerMaps.get(key).disConnect(mBtManagerMaps.get(key).getmMac(),false);
            }
        }
        mReconnectSet.clear();
    }

    @Override
    public void setReconnect(String mac, boolean isReconnect) {
        if (mBtManagerMaps.get(mac) != null) {
            mBtManagerMaps.get(mac).setReconnect(isReconnect);
        }
    }

    @Override
    public boolean isReconnect(String mac) {
        if (mBtManagerMaps.get(mac) != null) {
            return mBtManagerMaps.get(mac).isReconnect();
        }
        return false;
    }
    @Override
    public ArrayList<BluetoothDevice> getConnectDevice() {
        ArrayList<BluetoothDevice> deviceList = new ArrayList<>();
        if(mBtManagerMaps!=null){
            for (String key : mBtManagerMaps.keySet()) {
                if(isConnect(key)){
                    deviceList.add(mBtManagerMaps.get(key).getmDevice());
                }
            }
        }
        return deviceList;
    }
    @Override
    public void startReadRssi(String mac) {
        if (mBtManagerMaps.get(mac) != null) {
//            mBtManagerMaps.get(mac).startReadRssi();
        }
    }

    @Override
    public void stopReadRssi(String mac) {
        if (mBtManagerMaps.get(mac) != null) {
//            mBtManagerMaps.get(mac).stopReadRssi();
        }
    }


    @Override
    public boolean writeData(String mac, byte[] cmd) {
        if(mac==null){
            for(BtManager btManager:mBtManagerMaps.values()){
               return btManager.writeData(cmd);
            }
        }else {
            if (mBtManagerMaps.get(mac) != null) {
                return mBtManagerMaps.get(mac).writeData(cmd);
            }
        }
        return false;
    }

    @Override
    public boolean writeData(String mac, byte[] cmd, String serviceUUID, String charUUID) {
        if(mac==null){
            for(BtManager btManager:mBtManagerMaps.values()){
                btManager.writeData(cmd);
            }
        }else {
            if (mBtManagerMaps.get(mac) != null) {
                return mBtManagerMaps.get(mac).writeData(cmd, serviceUUID, charUUID);
            }
        }
        return true;
    }

    @Override
    public void readData(String mac) {
        if (mBtManagerMaps.get(mac) != null) {
             mBtManagerMaps.get(mac).readData();
        }
    }

    @Override
    public void readData(String mac, String serviceUUID, String charUUID) {
        if (mBtManagerMaps.get(mac) != null) {
            mBtManagerMaps.get(mac).readData(serviceUUID,charUUID);
        }
    }
    @Override
    public void addListener(BlueListener listener) {
        if (mListeners == null) {
            mListeners = new HashSet<>();
        }
        mListeners.add(listener);
    }
    @Override
    public void removeListener(BlueListener listener) {
        if(mListeners!=null) {
            mListeners.remove(listener);
        }
    }

    @Override
    public void addNotifyListener(NotifyListener listener) {
        if (mNotifyListeners == null) {
            mNotifyListeners = new HashSet<>();
        }
        mNotifyListeners.add(listener);
    }
    @Override
    public void removeNotifyListener(NotifyListener listener) {
        if(mNotifyListeners!=null) {
            mNotifyListeners.remove(listener);
        }
    }
    public abstract void dataHandler(String uuid,String mac, byte[] data);
    public abstract void handlerMsg(String mac, Message msg);
    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String mac = msg.getData().getString("mac");
            handlerMsg(mac, msg);
        }
    };


    public void sendMessage(String mac, int what, Object data) {
        Message msg = new Message();
        msg.what = what;
        Bundle bundle = new Bundle();
        bundle.putString("mac", mac);
        msg.setData(bundle);
        if (data != null) {
            msg.obj = data;
        }
        mHandler.sendMessage(msg);
    }

    public void sendMessage(String mac, int what) {
        sendMessage(mac, what, null);
    }
    public int getConnectSize(){
        return mBtManagerMaps==null? 0: mBtManagerMaps.size();
    }

    public Set<String> getmReconnectSet() {
        return mReconnectSet;
    }

    public void setmReconnectSet(Set<String> mReconnectSet) {
        this.mReconnectSet = mReconnectSet;
    }

    @Override
    public Set<String> getReconnect() {
        return mReconnectSet;
    }

    @Override
    public void addReconnect(String mac) {
        mReconnectSet.add(mac);
    }

    @Override
    public void removeReconnect(String mac) {
        mReconnectSet.remove(mac);
    }

    @Override
    public boolean isReconnectList(String mac) {
        return mReconnectSet.contains(mac);
    }
}

