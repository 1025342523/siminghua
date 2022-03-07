package com.yscoco.siminghua.ui.activity.connecting;

import android.bluetooth.BluetoothGatt;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.scan.BleScanRuleConfig;
import com.ys.module.title.TitleBar;
import com.ys.module.toast.ToastTool;
import com.yscoco.siminghua.Constans;
import com.yscoco.siminghua.R;
import com.yscoco.siminghua.base.activity.BaseActivity;
import com.yscoco.siminghua.domain.BleDeviceBean;
import com.yscoco.siminghua.domain.MessageEvent;
import com.yscoco.siminghua.ui.activity.connecting.adapter.ScanBluetoothAdapter;
import com.yscoco.siminghua.utils.SpUtils;
import com.yscoco.siminghua.utils.ThreadUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by ZhangZeZhi on 2018\10\23 0023.
 */

public class ScanBluetoothActivity extends BaseActivity implements ScanBluetoothAdapter.OnItemClickListener {

    @BindView(R.id.tb_title)
    TitleBar mTitleBar;

    @BindView(R.id.iv_scan)
    ImageView mIvScan;

    @BindView(R.id.rv)
    RecyclerView mRv;

    @BindView(R.id.iv_battery)
    ImageView mIvBattery;

    @BindView(R.id.tv_no_device)
    TextView mTvNoDevice;

    @BindView(R.id.iv_battery_two)
    ImageView mIvBatteryTwo;

    private Animation mAnimation;
    private ScanBluetoothAdapter mAdapter;
    private List<BleDeviceBean> mConnectedList = new ArrayList<>();
    private List<BleDeviceBean> mBleDeviceList = new ArrayList<>();
    private List<BleDeviceBean> mSaveBleDeviceList = new ArrayList<>(2);
    private boolean isConnect = false;
    private int electricityValue = 0;
    private int electricityValueTwo = 0;
    private boolean disConnect = false;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_scan_bluetooth;
    }

    @Override
    protected void init() {
        transparentStatusBar(R.color.transparent);
        mTitleBar.setTitle(R.string.connecting_device_text);
        mTitleBar.gone();
        mRv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mAnimation = AnimationUtils.loadAnimation(this, R.anim.image_rotate_anim);
        mAnimation.setInterpolator(new LinearInterpolator());
        if (mAnimation != null) {
            mIvScan.startAnimation(mAnimation);
        } else {
            mIvScan.setAnimation(mAnimation);
            mIvScan.startAnimation(mAnimation);
        }
        mSaveBleDeviceList.clear();
        mSaveBleDeviceList.addAll(SpUtils.getDataList(Constans.BLUETOOTH_DEVICE_KEYS, BleDeviceBean.class));
        Log.e("size", mSaveBleDeviceList.size() + "");
        initBluetooth();
    }

    private void initBluetooth() {
        if (BleManager.getInstance().isSupportBle()) {//是否支持Ble
            if (!BleManager.getInstance().isBlueEnable()) {//蓝牙是否开启
                ThreadUtils.getThreadPool().execute(new Runnable() {//没有开启异步开启蓝牙
                    @Override
                    public void run() {
                        while (true) {
                            if (!BleManager.getInstance().isBlueEnable()) {
                                BleManager.getInstance().enableBluetooth();
                                try {
                                    MessageEvent event = new MessageEvent();
                                    event.setCode(10);
                                    EventBus.getDefault().post(event);
                                    Thread.sleep(10000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                });
            } else {
                Log.e("initBluetooth", "initBluetooth");
                onScan();
            }
        } else {
            ToastTool.showNormalShort(this, "不支持蓝牙BLE功能");
        }
    }

    private void onScan() {
        BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder()
                .setAutoConnect(true)      // 连接时的autoConnect参数，可选，默认false
                .setScanTimeOut(3000)              // 扫描超时时间，可选，默认10秒
                .build();
        BleManager.getInstance().initScanRule(scanRuleConfig);
        BleManager.getInstance().scan(new BleScanCallback() {
            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {
                Log.e("onScanFinished", scanResultList.toString());
                mBleDeviceList.clear();
                Set<String> set = new HashSet<>();
                if (mSaveBleDeviceList.size() > 0) {
                    for (int i = 0; i < mSaveBleDeviceList.size(); i++) {
                        scanResultList.add(i, mSaveBleDeviceList.get(i).getBleDevice());
                    }
                }

                if (scanResultList.size() > 0) {
                    for (BleDevice bleDevice : scanResultList) {
                        BleDeviceBean deviceBean = new BleDeviceBean();
                        deviceBean.setBleDevice(bleDevice);
                        if (mSaveBleDeviceList.size() > 0) {
                            for (BleDeviceBean bleDeviceBean : mSaveBleDeviceList) {
                                if (bleDevice.getMac().equals(bleDeviceBean.getBleDevice().getMac())) {
                                    bleDeviceBean.setBleDevice(bleDevice);
                                    boolean connected = BleManager.getInstance().isConnected(bleDevice);
                                    bleDeviceBean.setConnectable(connected);
//                                    bleDeviceBean.setConnectable(true);
                                    if (set.add(bleDevice.getMac())) {
                                        mBleDeviceList.add(bleDeviceBean);
                                    }
                                } else {
                                    if (set.add(bleDevice.getMac())) {
                                        mBleDeviceList.add(deviceBean);
                                    }
                                }
                            }
                        } else {
                            if (set.add(bleDevice.getMac())) {
                                mBleDeviceList.add(deviceBean);
                            }
                        }
                    }
                    mAdapter = new ScanBluetoothAdapter(R.layout.layout_scan_bluetooth_item, mBleDeviceList);
                    mAdapter.setHasStableIds(true);
                    mRv.setAdapter(mAdapter);
                    mAdapter.setOnItemClickListener(ScanBluetoothActivity.this);
                    mAdapter.notifyDataSetChanged();
                } else {
                    mTvNoDevice.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onScanStarted(boolean success) {
            }

            @Override
            public void onScanning(BleDevice bleDevice) {
            }
        });
    }

    @OnClick({R.id.iv_left_arrow})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.iv_left_arrow:
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        mIvScan.clearAnimation();
        super.onDestroy();
    }

    @Override
    public void onMessageEvent(MessageEvent event) {
        super.onMessageEvent(event);
        if (event.getCode() == 10) {
            onScan();
        } else if (event.getCode() == Constans.BLUE_BATTERY_ELECTRICITY) {
            byte[] data = (byte[]) event.getObj();
            int electricity = data[0];
            Log.e("electricity", electricity + "");
            if (80 <= electricity && electricity <= 100) {
                mIvBattery.setImageResource(R.mipmap.icon_blue_battery_100);
            } else if (60 <= electricity && electricity < 80) {
                mIvBattery.setImageResource(R.mipmap.icon_blue_battery_80);
            } else if (40 <= electricity && electricity < 60) {
                mIvBattery.setImageResource(R.mipmap.icon_blue_battery_60);
            } else if (20 <= electricity && electricity < 40) {
                mIvBattery.setImageResource(R.mipmap.icon_blue_battery_40);
            } else if (0 <= electricity && electricity < 20) {
                mIvBattery.setImageResource(R.mipmap.icon_blue_battery_20);
            }
        } else if (event.getCode() == Constans.BLUE_BATTERY_ELECTRICITY_TWO) {
            byte[] data = (byte[]) event.getObj();
            int electricity = data[0];
            Log.e("electricity", electricity + "");
            if (80 <= electricity && electricity <= 100) {
                mIvBatteryTwo.setImageResource(R.mipmap.icon_blue_battery_100);
            } else if (60 <= electricity && electricity < 80) {
                mIvBatteryTwo.setImageResource(R.mipmap.icon_blue_battery_80);
            } else if (40 <= electricity && electricity < 60) {
                mIvBatteryTwo.setImageResource(R.mipmap.icon_blue_battery_60);
            } else if (20 <= electricity && electricity < 40) {
                mIvBatteryTwo.setImageResource(R.mipmap.icon_blue_battery_40);
            } else if (0 <= electricity && electricity < 20) {
                mIvBatteryTwo.setImageResource(R.mipmap.icon_blue_battery_20);
            }
        }

    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, final View view, final int position) {

        final MessageEvent event = new MessageEvent();
        final BleDeviceBean bleDeviceBean = mAdapter.getItem(position);
        boolean connected = BleManager.getInstance().isConnected(bleDeviceBean.getBleDevice());
        if (!connected) {
            BleManager.getInstance().connect(mAdapter.getItem(position).getBleDevice(), new BleGattCallback() {
                @Override
                public void onStartConnect() {

                }

                @Override
                public void onConnectFail(BleDevice bleDevice, final BleException exception) {
                    bleDeviceBean.setConnectable(false);
                    if (mSaveBleDeviceList != null && mSaveBleDeviceList.size() >= 1) {
                        for (int i = 0; i < mSaveBleDeviceList.size(); i++) {
                            if (mSaveBleDeviceList.get(i).getBleDevice().getMac().equals(bleDevice.getMac()) && i == 1) {
                                mIvBattery.setVisibility(View.GONE);
                            } else {
                                mIvBatteryTwo.setVisibility(View.GONE);
                            }
                        }
                    } else {
                        mIvBattery.setVisibility(View.GONE);
                    }
                    isConnect = false;
                    Log.e("onConnectFail", exception.toString());
                    event.setCode(Constans.BLUETOOTH_DEVICE_CODE);
                    event.setObj(bleDeviceBean);
                    EventBus.getDefault().post(event);
                    ToastTool.showNormalShort(ScanBluetoothActivity.this, "连接失败");
                }

                @Override
                public void onConnectSuccess(final BleDevice bleDevice, BluetoothGatt gatt, int status) {
                    disConnect = false;//蓝牙广播
                    SpUtils.putBoolean(ScanBluetoothActivity.this, Constans.DISCONNECTED_KEY, disConnect);

                    bleDeviceBean.setConnectable(true);

                    isConnect = true;
                    ToastTool.showNormalShort(ScanBluetoothActivity.this, "连接成功");
                    mBleDeviceList.set(position, bleDeviceBean);
                    mAdapter.setData(position, bleDeviceBean);

                    if (mSaveBleDeviceList.size() >= 1) {
                        int size = mSaveBleDeviceList.size();
                        for (int i = 0; i < size; i++) {
                            if (!mSaveBleDeviceList.get(i).getBleDevice().getMac().equals(bleDevice.getMac())) {
                                mSaveBleDeviceList.add(bleDeviceBean);
                                SpUtils.setDataList(Constans.BLUETOOTH_DEVICE_KEYS, mSaveBleDeviceList);
                            }
                        }
                    } else {
                        mSaveBleDeviceList.add(bleDeviceBean);
                        SpUtils.setDataList(Constans.BLUETOOTH_DEVICE_KEYS, mSaveBleDeviceList);
                    }
                    if (mSaveBleDeviceList != null && mSaveBleDeviceList.size() > 1) {
                        SpUtils.putBoolean(ScanBluetoothActivity.this, Constans.DISCONNECTED_TWO_KEY, disConnect);
                    }
                    if (mSaveBleDeviceList != null && mSaveBleDeviceList.size() > 1) {
                        mIvBatteryTwo.setVisibility(View.VISIBLE);
                    } else {
                        mIvBattery.setVisibility(View.VISIBLE);
                    }
                    mAdapter.notifyDataSetChanged();
                    event.setCode(Constans.BLUETOOTH_DEVICE_CODE);
                    event.setObj(bleDeviceBean);
                    EventBus.getDefault().post(event);

                    if (mSaveBleDeviceList != null && mSaveBleDeviceList.size() > 1) {
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
                                if (electricityValueTwo == 0) {
                                    electricityValueTwo = data[0];
                                    bleDeviceBean.setElectricity(electricityValueTwo);
                                    for (int i = 0; i < mSaveBleDeviceList.size(); i++) {
                                        if (mSaveBleDeviceList.get(i).getBleDevice().getMac().equals(bleDeviceBean.getBleDevice().getMac())) {
                                            mSaveBleDeviceList.set(i, bleDeviceBean);
                                            SpUtils.setDataList(Constans.BLUETOOTH_DEVICE_KEYS, mSaveBleDeviceList);
                                        }
                                    }
                                }
                                MessageEvent messageEvent = new MessageEvent();
                                messageEvent.setCode(Constans.BLUE_BATTERY_ELECTRICITY_TWO);
                                messageEvent.setObj(data);
                                EventBus.getDefault().post(messageEvent);
                            }
                        });
                    } else {
                        notifyOneDevice(bleDevice, bleDeviceBean);
                    }

                }

                @Override
                public void onDisConnected(boolean isActiveDisConnected, final BleDevice device, BluetoothGatt gatt, int status) {

                    bleDeviceBean.setConnectable(false);
                    if (mSaveBleDeviceList != null && mSaveBleDeviceList.size() >= 1) {
                        mIvBatteryTwo.setVisibility(View.GONE);
                    } else {
                        mIvBattery.setVisibility(View.GONE);
                    }
                    isConnect = false;
                    ToastTool.showNormalShort(ScanBluetoothActivity.this, "断开连接");
                    mBleDeviceList.set(position, bleDeviceBean);
                    mAdapter.setData(position, bleDeviceBean);
                    mAdapter.notifyDataSetChanged();

                    for (int i = 0; i < mSaveBleDeviceList.size(); i++) {
                        if (mSaveBleDeviceList.get(i).getBleDevice().getMac().equals(device.getMac())) {
                            mSaveBleDeviceList.remove(i);
                            i--;
                        }
                    }
                    SpUtils.setDataList(Constans.BLUETOOTH_DEVICE_KEYS, mSaveBleDeviceList);
                    event.setCode(Constans.BLUETOOTH_DEVICE_CODE);
                    event.setObj(bleDeviceBean);
                    EventBus.getDefault().post(event);
                }
            });
        } else {
            BleManager.getInstance().disconnect(bleDeviceBean.getBleDevice());
            Log.e("Mac", bleDeviceBean.getBleDevice().getMac());
            isConnect = false;
            disConnect = true;
            SpUtils.putBoolean(this, Constans.DISCONNECTED_KEY, disConnect);
            bleDeviceBean.setConnectable(false);

            if (mSaveBleDeviceList != null && mSaveBleDeviceList.size() > 1) {
                mIvBatteryTwo.setVisibility(View.GONE);
//                disTwoConnect = true;
            } else {
                mIvBattery.setVisibility(View.GONE);
            }

            mBleDeviceList.set(position, bleDeviceBean);
            mAdapter.setData(position, bleDeviceBean);
            mAdapter.notifyDataSetChanged();

            event.setCode(Constans.BLUETOOTH_DEVICE_CODE);
            event.setObj(bleDeviceBean);
            EventBus.getDefault().post(event);

            for (int i = 0; i < mSaveBleDeviceList.size(); i++) {
                if (mSaveBleDeviceList.get(i).getBleDevice().getMac().equals(bleDeviceBean.getBleDevice().getMac())) {
                    mSaveBleDeviceList.remove(i);
                    i--;
                }
            }

            SpUtils.setDataList(Constans.BLUETOOTH_DEVICE_KEYS, mSaveBleDeviceList);
        }
    }

    private void notifyOneDevice(BleDevice bleDevice, BleDeviceBean bleDeviceBean) {
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
                    bleDeviceBean.setElectricity(electricityValue);
                    for (int i = 0; i < mSaveBleDeviceList.size(); i++) {
                        if (mSaveBleDeviceList.get(i).getBleDevice().getMac().equals(bleDeviceBean.getBleDevice().getMac())) {
                            mSaveBleDeviceList.set(i, bleDeviceBean);
                            SpUtils.setDataList(Constans.BLUETOOTH_DEVICE_KEYS, mSaveBleDeviceList);
                        }
                    }
                }
                MessageEvent messageEvent = new MessageEvent();
                messageEvent.setCode(Constans.BLUE_BATTERY_ELECTRICITY);
                messageEvent.setObj(data);
                EventBus.getDefault().post(messageEvent);
            }
        });
    }

}
