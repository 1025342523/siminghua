package com.yscoco.blue;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.yscoco.blue.base.BaseBlueDriver;
import com.yscoco.blue.base.BaseBtManager;
import com.yscoco.blue.utils.FileWriteUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

/**
 * 作者：karl.wei
 * 创建日期： 2017/12/9 0009 13:46
 * 邮箱：karl.wei@yscoco.com
 * QQ:2736764338
 * 类介绍：蓝牙设备传输类
 */
public class BtManager extends BaseBtManager {

    private BluetoothManager mBluetoothManager;// 蓝牙管理器

    private BluetoothAdapter mBluetoothAdapter;// 蓝牙适配器

    TimerTask task;
    Timer rssiTimer;
    /**
     * mac地址
     */
    private String mMac;
    public String getmMac() {
        return mMac;
    }
    /**
     * 连接状态：已连接；未连接
     */
    private boolean isConnect = false;
    /**
     * 是否重连
     */
    private boolean isReconnect = true;
    /**
     * 是否断开过,断开过不再发送断开连接提醒
     */
    private boolean isDisconnected = false;
    private BluetoothGatt mBluetoothGatt;

    public BluetoothGatt getmBluetoothGatt() {
        return mBluetoothGatt;
    }

    public void setmBluetoothGatt(BluetoothGatt mBluetoothGatt) {
        this.mBluetoothGatt = mBluetoothGatt;
    }

    protected Context mContext;
    private BluetoothDevice mDevice;
    private BaseBlueDriver mBlueDriver;
    public BtManager(Context c, String mac, BluetoothDevice mDevice,BaseBlueDriver blueDriver) {
        mContext = c;
        mMac = mac;
        mBlueDriver = blueDriver;
        this.mDevice = mDevice;
        mHandler = new Handler(mContext.getMainLooper());
//        // 增加读rssi 的定时器F
//        task = new TimerTask() {
//            @Override
//            public void run() {
//                if (isConnect()&&mBluetoothGatt!=null) {
//                    mBluetoothGatt.readRemoteRssi();
//                }
//            }
//        };
//        rssiTimer = new Timer();
//        if(task!=null){
//            rssiTimer.schedule(task, 10, 1000);
//        }
    }
    public BluetoothDevice getmDevice() {
        return mDevice;
    }

    public void setmDevice(BluetoothDevice mDevice) {
        this.mDevice = mDevice;
    }
    public boolean connected(){
        //初始化
        if (!initialize()) {
            return false;
        }

        if (!mBluetoothAdapter.isEnabled()) {
            return false;
        }

        // 有为空的情况就直接返回
        if (mBluetoothAdapter == null || mMac == null) {
            return false;
        }
//        if(!isConnected) {
            close();
//        }
        //设为断开可以重连
        isReconnect = true;
        // 断开连接
        if (!isConnect) {
            if (mBluetoothGatt != null) {
                mBluetoothGatt.disconnect();
                mBluetoothGatt = null;
            }
        }
//         以前连接设备。尝试重新连接。
//         Previously onConnected device. Try to reconnect.
//        if (mMac != null && mBluetoothGatt != null) {
//            Log.e("blue", "Trying to use an existing mBluetoothGatt for connection.");
//            mBluetoothGatt.connect();
//        }

        final BluetoothDevice device;
        try {
            device = mBluetoothAdapter.getRemoteDevice(mMac.toUpperCase().trim());
            if (device == null) {
                return false;
            }
            mBluetoothGatt = device.connectGatt(mContext, false, mGattCallback);
//            mBluetoothGatt.connect();
            Log.e("blue", mMac+"device.getBondState==" + device.getBondState());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
    public boolean isConnect() {
        return isConnect;
    }

    public boolean isDisconnected() {
        return isDisconnected;
    }

    public void disConnect(String mac, boolean isReconnect) {
        Log.e("blue","disconnect");
        this.isReconnect = isReconnect;
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            return;
        }
        if(mac.equals(mMac)){
            mBluetoothGatt.disconnect();
        }
    }

    public void setReconnect(boolean isReconnect) {
        this.isReconnect = isReconnect;
    }

    public boolean isReconnect() {
        return isReconnect;
    }

    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter
        // through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                // Log.e("error", "Unable to initialize BluetoothManager.");
                return false;
            }
        }
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            return false;
        }
        return true;
    }

    // Implements callback methods for GATT events that the app cares about. For
    // example,
    // connection change and services discovered.
    // 通过BLE API的不同类型的回调方法
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.e("blue","status" + status + "，newState" + newState + "," + gatt.getDevice().getAddress());
            FileWriteUtils.initWrite("status" + status + "，newState" + newState + "," + gatt.getDevice().getAddress());
            if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {
                Log.e("blue","回调的方法连接"+this.toString());
                FileWriteUtils.initWrite("回调的方法连接"+this.toString());
                //已连接
                mBluetoothGatt.discoverServices();
            } else {//已断开
                Log.e("blue","回调的方法断开"+this.toString());
                FileWriteUtils.initWrite("回调的方法断开"+this.toString());
                isConnect = false;
                mBluetoothGatt.close();
                mBluetoothGatt = null;
                if(!isDisconnected){
                    mBlueDriver.sendMessage(mMac,DISCONNECT);
                }
                isDisconnected = true;
                if (isReconnect()) {
                    /**
                     * 启动重连功能
                     * 逻辑在BaseBluDriver.DisconnNewConnTh 里实现
                     */
                    connect();
//                    isContinue = true;
                }
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.e("blue","发现服务成功");
                FileWriteUtils.initWrite("发现服务成功");
                isConnect = true;
                isDisconnected = false;
                mBlueDriver.sendMessage(mMac,CONNECTED);
//                mBlueDriver.sendMessage(mMac, MyBlueDriver.MSG, "设备连接成功");
                List<BluetoothGattService> services =  gatt.getServices();
                for(BluetoothGattService service:services){
                    Log.e("blue",service.getUuid().toString());
                }
                startNotification(SERVICE_BATTERY_UUID1,CHA_BATTERY_NOTIFY);
            }else{
                Log.e("blue","发现服务失败");
                FileWriteUtils.initWrite("发现服务失败");
                disConnect(mMac,true);
            }
            super.onServicesDiscovered(gatt, status);
        }

        /**
         * 返回数据。
         */
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
//            Log.e("blue","onCharacteristicChanged:" + "有数据");
            // 获取特征值的UUID
            String uuid = characteristic.getUuid().toString().trim();
            final byte[] data = characteristic.getValue();
            StringBuffer b = new StringBuffer();
            for (int i = 0; i < data.length; i++) {
                b.append(String.format("%02X ", data[i]).toString().trim());
            }
            Log.e("blue",mMac+"数据为" + b.toString());
            FileWriteUtils.initWrite(mMac+"数据为" + b.toString());
            mBlueDriver.dataHandler(uuid,mMac,data);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            if (status != BluetoothGatt.GATT_SUCCESS) {
//                mBlueDriver.sendMessage(mMac, MyBlueDriver.MSG, "读取电量失败");
                return;
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            Log.e("blue","onCharacteristicWrite" + status);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            if (status != BluetoothGatt.GATT_SUCCESS) {
                Log.e("blue","开启notify失败");
                disConnect(mMac,true);
                return;
            }
            if(descriptor.getCharacteristic().getUuid().toString().toUpperCase().equals(CHA_BATTERY_NOTIFY.toUpperCase())){
                Log.e("blue","开启电量通知成功");
                startNotification(SERVICE_UUID1, CHA_NOTIFY);
            }
            if(descriptor.getCharacteristic().getUuid().toString().toUpperCase().equals(CHA_NOTIFY.toUpperCase())){
                /*数据通道开启成功*/
                mBlueDriver.sendMessage(mMac,NOTIFY_ON);
                notifySuccess();
                Log.e("blue","开启notify成功");
//                mBlueDriver.sendMessage(mMac, MyBlueDriver.MSG, "电量监听开启");
            }
        }
        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
            Message msg = new Message();
            mBlueDriver.sendMessage(mMac,NOTIFY_RSSI,rssi);

        }
    };

    private void connect() {
        mBlueDriver.sendMessage(mMac,RE_CONNECT);
    }

    private ArrayList<Integer> mRssis = new ArrayList<>();

    /**
     * @param service_uuid 服务的UUID
     * @param cha_uuid     特征值的UUID
     * @param bb           发送的字节数组的值
     */
    @SuppressWarnings("unused")
    public synchronized boolean writeLlsAlertLevel(String service_uuid, String cha_uuid, byte[] bb) {
        StringBuffer b = new StringBuffer();
        for (int i = 0; i < bb.length; i++) {
            b.append(String.format("%02X ", bb[i]).toString().trim());
        }
        Log.e("blue","fa song:" + b.toString() +"设备mac:"+ mMac);
        FileWriteUtils.initWrite("发送:" + b.toString() +"设备mac:"+ mMac);
        BluetoothGattService linkLossService;
        BluetoothGattCharacteristic alertLevel = getCharacter(service_uuid, cha_uuid);
        if (alertLevel == null) {
            Log.e("error", "link loss Alert Level charateristic not found!");
            return false;
        }
        boolean status = false;
        int storedLevel = alertLevel.getWriteType();
        alertLevel.setValue(bb);
        alertLevel.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        status = mBluetoothGatt.writeCharacteristic(alertLevel);
        Log.e("blue","数据写状态" + status);
        FileWriteUtils.initWrite("数据写状态" + status);
        return status;
    }

    /**
     * 获取特征值
     */
    public BluetoothGattCharacteristic getCharacter(String serviceUUID, String characterUUID) {
        // Log.e("error","设备名称："+mBluetoothGatt.getDevice().getAddress());
        if (mBluetoothGatt == null) {
            return null;
        }
        BluetoothGattService service = mBluetoothGatt.getService(UUID.fromString(serviceUUID));
        if (service != null) {
            return service.getCharacteristic(UUID.fromString(characterUUID));
        }
        return null;
    }

    /**
     * 开启通知
     */
    private void startNotification(String serviceUUID, String charaterUUID) {
        BluetoothGattCharacteristic c1 = getCharacter(serviceUUID, charaterUUID);
        if (c1 != null) {
            final int cx1 = c1.getProperties();
            if ((cx1 | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                // 如果有一个活跃的通知上的特点,明确第一所以不更新用户界面上的数据字段。
//                if (c1 != null) {
//                    setCharacteristicNotification(c1, false);
//                }
//                readCharacteristic(serviceUUID, charaterUUID);
                if ((cx1 | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                    setCharacteristicNotification(c1, true);
                }
            }
        }
    }

    /**
     * 读取特征值的Values
     */
    public synchronized void readCharacteristic(String service_uuid, String cha_uuid) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            return;
        }
        BluetoothGattCharacteristic c1 = getCharacter(service_uuid, cha_uuid);
        if (c1 != null&&mBluetoothGatt!=null) {
            mBluetoothGatt.readCharacteristic(c1);
        }
    }

    /**
     * 设置通知开启
     */
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            // Log.e("error", "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, true);
        // Log.e("taa","开启了广播");
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(DES_UUID1));
        if (descriptor != null&&mBluetoothGatt!=null) {
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);
        }
    }

    /*通道开启成功后调用*/
    public void notifySuccess(){

    }
    public Handler mHandler;
    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    public boolean writeData(byte[] cmd){
        return writeLlsAlertLevel(SERVICE_UUID1,CHA_WRITE,cmd);
    }
    public boolean writeData( byte[] cmd,String serviceUUID,String charUUID){
        return writeLlsAlertLevel(serviceUUID,charUUID,cmd);
    }

    /**
     * 读取数据
     */
    public void readData(){
        readCharacteristic(SERVICE_UUID1,CHA_NOTIFY);
    };
    /**
     * 读取特定服务和属性的数据
     */
    public void readData(String serviceUUID,String charUUID){
        readCharacteristic(serviceUUID,charUUID);
    };
}
