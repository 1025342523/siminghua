package com.yscoco.blue.app;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.yscoco.blue.MyBlueDriver;
import com.yscoco.blue.imp.BlueDriver;
import com.yscoco.blue.service.BleScanService;

/**
 * 作者：karl.wei
 * 创建日期： 2017/12/9 0009 13:46
 * 邮箱：karl.wei@yscoco.com
 * QQ:2736764338
 * 类介绍：配置蓝牙主应用
 */
public class BaseBlueApplication extends Application {

    public static BaseBlueApplication getApplication() {
        return instance;
    }
    private static BaseBlueApplication instance;
    private static BlueDriver myDriver;

    /*获取操作的工具类*/
    public static BlueDriver getMyDriver() {
        if(myDriver==null){
            myDriver = MyBlueDriver.getInstance(getApplication());
        }
        return myDriver;
    }

    public void setMyDriver(BlueDriver myDriver) {
        this.myDriver = myDriver;
    }
    public BaseBlueApplication() {
        instance = this;
    }

    public static BaseBlueApplication getInstance() {
        if(instance==null){
            instance = new BaseBlueApplication();
        }
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
//        initBlueTooth();
    }
    // 蓝牙服务
    private static BleScanService mBluetoothLeService;
    public static BleScanService getmBluetoothLeService() {
        return mBluetoothLeService;
    }
    @SuppressWarnings("static-access")
    public static void setmBluetoothLeService(BleScanService mBluetoothLeService) {
        getApplication().mBluetoothLeService = mBluetoothLeService;
    }
    // 开启Bluetoothseivice
    public static void initBlueTooth() {
        Intent gattServiceIntent = new Intent(getApplication(), BleScanService.class);
        getApplication().bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    };

    // 管理生命周期的代码
    public final static ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };
}
