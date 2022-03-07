package com.yscoco.siminghua.receiver;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.ys.module.toast.ToastTool;
import com.ys.module.utils.StringUtils;
import com.yscoco.siminghua.Constans;
import com.yscoco.siminghua.domain.BleDeviceBean;
import com.yscoco.siminghua.domain.MessageEvent;
import com.yscoco.siminghua.utils.SpUtils;
import com.yscoco.siminghua.utils.ThreadUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by ZhangZeZhi on 2018\11\29 0029.
 */

public class BluetoothDisconnectReceiver extends BroadcastReceiver {

    public static boolean isRun = true;

    public static long sleepTime = 3000L;

    private boolean isFirstRun = false;

    private String[] address = new String[2];

    private String macStr;
    private int electricityValue;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        List<BleDeviceBean> deviceBeanList = SpUtils.getDataList(Constans.BLUETOOTH_DEVICE_KEYS, BleDeviceBean.class);
        if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
            final boolean[] disconnected = {SpUtils.getBoolean(context, Constans.DISCONNECTED_KEY, false)};
            Log.e("onReceive", device.getAddress());
            if (!disconnected[0]) {
                if (StringUtils.isEmpty(address[0])) {
                    for (int i = 0; i < deviceBeanList.size(); i++) {
                        if (deviceBeanList.get(i).getBleDevice().getMac().equals(device.getAddress())) {
                            deviceBeanList.remove(i);
                            i--;
                        }
                    }
                    address[0] = device.getAddress();
                } else if (StringUtils.isEmpty(address[1])) {
                    for (int i = 0; i < deviceBeanList.size(); i++) {
                        if (deviceBeanList.get(i).getBleDevice().getMac().equals(device.getAddress())) {
                            deviceBeanList.remove(i);
                            i--;
                        }
                    }
                    address[1] = device.getAddress();
                }
            } else {
                for (int i = 0; i < deviceBeanList.size(); i++) {
                    if (deviceBeanList.get(i).getBleDevice().getMac().equals(device)) {
                        deviceBeanList.remove(i);
                        i--;
                    }
                }
            }

            if (!isFirstRun) {
                ThreadUtils.getThreadPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("run", "run");
                        while (isRun && !disconnected[0]) {
                            if (!StringUtils.isEmpty(address[0]) && !BleManager.getInstance().isConnected(address[0])) {
                                macStr = address[0];
                            } else {
                                address[0] = null;
                            }
                            if (!StringUtils.isEmpty(address[1]) && !BleManager.getInstance().isConnected(address[1]) && StringUtils.isEmpty(address[0])) {
                                macStr = address[1];
                            } else {
                                address[1] = null;
                            }
                            if (!disconnected[0] && !BleManager.getInstance().isConnected(macStr)) {
                                BleManager.getInstance().connect(macStr, new BleGattCallback() {

                                    @Override
                                    public void onStartConnect() {

                                    }

                                    @Override
                                    public void onConnectFail(BleDevice bleDevice, BleException exception) {
                                        Log.e("onConnectFail", exception.toString());
                                    }

                                    @Override
                                    public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {

                                        disconnected[0] = true;
                                        ToastTool.showNormalShort(context, "连接成功");
                                        Log.e("onConnectSuccess", "连接成功");
                                        BleDeviceBean deviceBean = new BleDeviceBean();
                                        deviceBean.setBleDevice(bleDevice);
                                        deviceBean.setConnectable(true);
                                        deviceBean.setAdd(true);

                                        for (int i = 0; i < deviceBeanList.size(); i++) {
                                            if (bleDevice.getMac().equals(deviceBeanList.get(i).getBleDevice().getMac())) {
                                                deviceBeanList.remove(i);
                                            }
                                        }

                                        if (deviceBeanList != null && deviceBeanList.size() >= 1) {
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
                                                    if (electricityValue == 0) {
                                                        electricityValue = data[0];
                                                        deviceBean.setElectricity(electricityValue);
                                                    }
                                                    MessageEvent messageEvent = new MessageEvent();
                                                    messageEvent.setCode(Constans.BLUE_BATTERY_ELECTRICITY_TWO);
                                                    messageEvent.setObj(data);
                                                    EventBus.getDefault().post(messageEvent);
                                                }
                                            });
                                        } else {
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
                                                    if (electricityValue == 0) {
                                                        electricityValue = data[0];
                                                        deviceBean.setElectricity(electricityValue);
                                                    }
                                                    MessageEvent messageEvent = new MessageEvent();
                                                    messageEvent.setCode(Constans.BLUE_BATTERY_ELECTRICITY);
                                                    messageEvent.setObj(data);
                                                    EventBus.getDefault().post(messageEvent);
                                                }
                                            });
                                        }

                                        deviceBeanList.add(deviceBean);
                                        SpUtils.setDataList(Constans.BLUETOOTH_DEVICE_KEYS, deviceBeanList);
                                        MessageEvent event = new MessageEvent();
                                        event.setCode(Constans.BLUETOOTH_DEVICE_CODE);
                                        event.setObj(deviceBean);
                                        EventBus.getDefault().post(event);
                                    }

                                    @Override
                                    public void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status) {
                                        for (int i = 0; i < deviceBeanList.size(); i++) {
                                            if (deviceBeanList.get(i).getBleDevice().getMac().equals(device.getMac())) {
                                                ToastTool.showNormalShort(context, "断开连接");
                                                deviceBeanList.remove(i);
                                                i--;
                                                SpUtils.setDataList(Constans.BLUETOOTH_DEVICE_KEYS, deviceBeanList);
                                            }
                                        }
                                        Log.e("onDisConnected", "断开连接");
                                    }
                                });
                            }
                            try {
                                Thread.sleep(sleepTime);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                isFirstRun = true;
            }
        }
    }

}