package com.yscoco.siminghua.service;

import android.app.Service;
import android.bluetooth.BluetoothGatt;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.ys.module.toast.ToastTool;
import com.yscoco.siminghua.Constans;
import com.yscoco.siminghua.MyApplication;
import com.yscoco.siminghua.domain.BleDeviceBean;
import com.yscoco.siminghua.domain.MessageEvent;
import com.yscoco.siminghua.utils.SpUtils;
import com.yscoco.siminghua.utils.ThreadUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * Created by ZhangZeZhi on 2018\11\21 0021.
 */

public class BluetoothConnectService extends Service {

    public static boolean isRun = true;

    public static long sleepTime = 1000L;

    public static Runnable connRunnable = new Runnable() {
        @Override
        public void run() {

            while (isRun) {

                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                boolean disconnect = SpUtils.getBoolean(MyApplication.getApplication(), Constans.DISCONNECTED_KEY, false);
                mBleDeviceBeanList = SpUtils.getDataList(Constans.BLUETOOTH_DEVICE_KEYS, BleDeviceBean.class);
                if (mBleDeviceBeanList != null && mBleDeviceBeanList.size() > 0) {

                    boolean connected = BleManager.getInstance().isConnected(mBleDeviceBeanList.get(0).getBleDevice());
                    if (!connected) {
                        ToastTool.showNormalShort(MyApplication.getApplication(), "设备1掉线");
                    }

                    if (mBleDeviceBeanList.size() > 1) {
                        boolean connectedTwo = BleManager.getInstance().isConnected(mBleDeviceBeanList.get(1).getBleDevice());
                        if (!connectedTwo) {
                            ToastTool.showNormalShort(MyApplication.getApplication(), "设备2掉线");
                        }
                    }

                    for (int i = 0; i < mBleDeviceBeanList.size(); i++) {

                        if (!disconnect && !BleManager.getInstance().isConnected(mBleDeviceBeanList.get(i).getBleDevice())) {
                            BleManager.getInstance().connect(mBleDeviceBeanList.get(i).getBleDevice(), new BleGattCallback() {
                                @Override
                                public void onStartConnect() {

                                }

                                @Override
                                public void onConnectFail(BleDevice bleDevice, BleException exception) {

                                }

                                @Override
                                public void onConnectSuccess(final BleDevice bleDevice, BluetoothGatt gatt, int status) {
                                    //连接成功
                                    BleManager.getInstance().notify(bleDevice, Constans.UUID_BATTERY_SERVICE, Constans.UUID_BATTERY_LEVEL_CHARACTERISTIC, new BleNotifyCallback() {
                                        @Override
                                        public void onNotifySuccess() {
                                        }

                                        @Override
                                        public void onNotifyFailure(BleException exception) {
                                            Log.e("onNotifyFailure", exception.toString());
                                        }

                                        @Override
                                        public void onCharacteristicChanged(byte[] data) {

                                           /* if (electricityValue == 0) {
                                                electricityValue = data[0];
                                                bleDeviceBean.setElectricity(electricityValue);
//                                SpUtils.setData(Constans.CONNECT_DEVICE_BEAN_KEY, bleDeviceBean);
                                                for (int i = 0; i < mSaveBleDeviceList.size(); i++) {
                                                    if (mSaveBleDeviceList.get(i).getBleDevice().getMac().equals(bleDeviceBean.getBleDevice().getMac())) {
                                                        mSaveBleDeviceList.set(i, bleDeviceBean);
                                                        SpUtils.setDataList(Constans.BLUETOOTH_DEVICE_KEYS, mSaveBleDeviceList);
                                                    }
                                                }
                                            }*/
                                            MessageEvent messageEvent = new MessageEvent();
                                            messageEvent.setCode(Constans.BLUE_BATTERY_ELECTRICITY);
                                            messageEvent.setObj(data);
                                            EventBus.getDefault().post(messageEvent);

                                            MessageEvent event = new MessageEvent();
                                            event.setCode(Constans.SERVICE_CONNECTED_CODE);
                                            messageEvent.setObj(bleDevice);
                                            EventBus.getDefault().post(event);

                                        }
                                    });

                                }

                                @Override
                                public void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status) {

                                }
                            });
                        }
                    }

                }

            }
        }
    };

    private static List<BleDeviceBean> mBleDeviceBeanList;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        ThreadUtils.getThreadPool().execute(connRunnable);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {

        if (event.getCode() == Constans.SERVICE_CONNECTED_CODE) {
            BleDeviceBean deviceBean = (BleDeviceBean) event.getObj();

            for (int i = 0; i < mBleDeviceBeanList.size(); i++) {
                if (deviceBean.getBleDevice().getMac().equals(mBleDeviceBeanList.get(i).getBleDevice().getMac())) {
                    deviceBean.setConnectable(true);
                    mBleDeviceBeanList.set(i, deviceBean);
                }
            }

            SpUtils.setDataList(Constans.BLUETOOTH_DEVICE_KEYS, mBleDeviceBeanList);
        }


    }

}

