package com.yscoco.siminghua.ui.activity.home;

import android.Manifest;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.clj.fastble.BleManager;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.ys.module.title.TitleBar;
import com.ys.module.toast.ToastTool;
import com.yscoco.siminghua.Constans;
import com.yscoco.siminghua.R;
import com.yscoco.siminghua.base.activity.BaseActivity;
import com.yscoco.siminghua.domain.BleDeviceBean;
import com.yscoco.siminghua.domain.MessageEvent;
import com.yscoco.siminghua.service.BluetoothConnectService;
import com.yscoco.siminghua.ui.activity.connecting.ScanBluetoothActivity;
import com.yscoco.siminghua.ui.activity.custom.CustomActivity;
import com.yscoco.siminghua.ui.activity.music.AddSongListActivity;
import com.yscoco.siminghua.ui.activity.realtimeoperation.RealTimeOperationActivity;
import com.yscoco.siminghua.ui.activity.setting.SetPrivacyActivity;
import com.yscoco.siminghua.ui.activity.voicecontrol.VoiceControlActivity;
import com.yscoco.siminghua.utils.CompatibleUtils;
import com.yscoco.siminghua.utils.SpUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;

/**
 * Created by ZhangZeZhi on 2018\10\23 0023.
 */

public class HomeActivity extends BaseActivity {

    @BindView(R.id.tb_title)
    TitleBar mTitleBar;

    @BindView(R.id.iv_battery)
    ImageView mIvBattery;

    @BindView(R.id.tv_bluetooth_connect)
    TextView mTvBluetoothConnect;

    @BindView(R.id.iv_battery_two)
    ImageView mIvBatteryTwo;

    private static final long WAIT_TIME = 2000L;
    private long TOUCH_TIME = 0;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    protected void init() {
        requestPermissions();
        transparentStatusBar(R.color.transparent);
        mTitleBar.gone();
        List<BleDeviceBean> deviceBeanList = SpUtils.getDataList(Constans.BLUETOOTH_DEVICE_KEYS, BleDeviceBean.class);
        if (deviceBeanList != null && deviceBeanList.size() > 0) {
            boolean connected = BleManager.getInstance().isConnected(deviceBeanList.get(0).getBleDevice());
            if (connected) {
                mIvBattery.setVisibility(View.VISIBLE);
                mTvBluetoothConnect.setText("设备已连接");
            }

            if (deviceBeanList.size() > 1) {
                boolean connectedTwo = BleManager.getInstance().isConnected(deviceBeanList.get(1).getBleDevice());
                if (connectedTwo) {
                    mIvBatteryTwo.setVisibility(View.VISIBLE);
                    mTvBluetoothConnect.setText("设备已连接");
                }
            }
        }

        //刷新媒体库
        CompatibleUtils.updateMedia(this, Environment.getExternalStorageDirectory().toString());
    }

    private void requestPermissions() {
        RxPermissions rxPermission = new RxPermissions(HomeActivity.this);
        //请求权限全部结果
        rxPermission.request(
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.RECORD_AUDIO)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean granted) throws Exception {
                        if (!granted) {
                            ToastTool.showNormalShort(HomeActivity.this, "App未能获取全部需要的相关权限，部分功能可能不能正常使用.");
                        }
                    }
                });
    }

    @OnClick({R.id.ll_bluetooth_connection, R.id.iv_setting
            , R.id.ll_real_time_manipulation, R.id.ll_play_music
            , R.id.ll_voice_control, R.id.ll_custom})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.ll_bluetooth_connection:
                showActivity(ScanBluetoothActivity.class);
                break;
            case R.id.iv_setting:
                showActivity(SetPrivacyActivity.class);
                break;
            case R.id.ll_real_time_manipulation:
                showActivity(RealTimeOperationActivity.class);
                break;
            case R.id.ll_play_music:
                showActivity(AddSongListActivity.class);
                break;
            case R.id.ll_voice_control:
                showActivity(VoiceControlActivity.class);
                break;
            case R.id.ll_custom:
                showActivity(CustomActivity.class);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        BleManager.getInstance().disconnectAllDevice();
        BleManager.getInstance().destroy();
        List<BleDeviceBean> bleDeviceBeanList = SpUtils.getDataList(Constans.BLUETOOTH_DEVICE_KEYS, BleDeviceBean.class);
        bleDeviceBeanList.clear();
        SpUtils.setData(Constans.BLUETOOTH_DEVICE_KEYS, bleDeviceBeanList);
        Intent intent = new Intent(this, BluetoothConnectService.class);
        stopService(intent);
        SpUtils.putBoolean(this, Constans.DISCONNECTED_KEY, true);
        super.onDestroy();
    }

    @Override
    public void onMessageEvent(MessageEvent event) {
        super.onMessageEvent(event);
        if (event.getCode() == Constans.BLUETOOTH_DEVICE_CODE) {
            BleDeviceBean bleDeviceBean = (BleDeviceBean) event.getObj();
            List<BleDeviceBean> bleDeviceBeanList = SpUtils.getDataList(Constans.BLUETOOTH_DEVICE_KEYS, BleDeviceBean.class);
            if (bleDeviceBeanList != null && bleDeviceBeanList.size() > 1) {
                mIvBattery.setVisibility(View.VISIBLE);
                mIvBatteryTwo.setVisibility(View.VISIBLE);
                mTvBluetoothConnect.setText("设备已连接");
            } else if (bleDeviceBeanList.size() == 1) {
                mTvBluetoothConnect.setText("设备已连接");
//                mIvBatteryTwo.setVisibility(View.GONE);
            } else {
                mIvBattery.setVisibility(View.GONE);
                mIvBatteryTwo.setVisibility(View.GONE);
                mTvBluetoothConnect.setText("设备未连接");
            }

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
    public void onBackPressedSupport() {
        if (System.currentTimeMillis() - TOUCH_TIME < WAIT_TIME) {
            finish();
        } else {
            TOUCH_TIME = System.currentTimeMillis();
            ToastTool.showNormalShort(this, "再按一次退出");
        }
    }
}
