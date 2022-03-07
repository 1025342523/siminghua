package com.yscoco.siminghua.ui.activity.setting;

import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.utils.HexUtil;
import com.kyleduo.switchbutton.SwitchButton;
import com.ys.module.title.TitleBar;
import com.yscoco.siminghua.Constans;
import com.yscoco.siminghua.R;
import com.yscoco.siminghua.base.activity.BaseActivity;
import com.yscoco.siminghua.domain.BleDeviceBean;
import com.yscoco.siminghua.utils.SpUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by ZhangZeZhi on 2018\10\23 0023.
 */

public class SetPrivacyActivity extends BaseActivity {

    @BindView(R.id.tb_title)
    TitleBar mTitleBar;

    @BindView(R.id.sb_switch)
    SwitchButton mSbSwitch;

    private BleDeviceBean mDeviceBean;

    private List<BleDeviceBean> mBleDeviceBeanList;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_set_privacy;
    }

    @Override
    protected void init() {
        transparentStatusBar(R.color.transparent);
        mTitleBar.setTitle(R.string.set_privacy_text, R.color.black_255);
        mTitleBar.gone();

//        mDeviceBean = SpUtils.getData(Constans.CONNECT_DEVICE_BEAN_KEY, BleDeviceBean.class);

        mBleDeviceBeanList = SpUtils.getDataList(Constans.BLUETOOTH_DEVICE_KEYS, BleDeviceBean.class);

        boolean isSetPrivacy = SpUtils.getBoolean(this, Constans.SET_PRIVACY_KEY, false);
        mSbSwitch.setChecked(isSetPrivacy);

        mSbSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                SpUtils.putBoolean(SetPrivacyActivity.this, Constans.SET_PRIVACY_KEY, isChecked);

                if (mBleDeviceBeanList != null && mBleDeviceBeanList.size() > 0) {

                    for (int i = 0; i < mBleDeviceBeanList.size(); i++) {

                        if (BleManager.getInstance().isConnected(mBleDeviceBeanList.get(i).getBleDevice())) {

                            if (isChecked) {

                                BleManager.getInstance().write(mBleDeviceBeanList.get(i).getBleDevice(), Constans.CUSTOM_SERVICE_UUID, Constans.CUSTOM_WRITE_CHARACTERISTIC_UUID, HexUtil.hexStringToBytes("A300"), new BleWriteCallback() {
                                    @Override
                                    public void onWriteSuccess(int current, int total, byte[] justWrite) {
                                        Log.e("onWriteSuccess", "蓝牙指示灯打开");
                                    }

                                    @Override
                                    public void onWriteFailure(BleException exception) {

                                    }
                                });

                            } else {

                                BleManager.getInstance().write(mBleDeviceBeanList.get(i).getBleDevice(), Constans.CUSTOM_SERVICE_UUID, Constans.CUSTOM_WRITE_CHARACTERISTIC_UUID, HexUtil.hexStringToBytes("A301"), new BleWriteCallback() {
                                    @Override
                                    public void onWriteSuccess(int current, int total, byte[] justWrite) {
                                        Log.e("onWriteSuccess", "蓝牙指示灯关闭");
                                    }

                                    @Override
                                    public void onWriteFailure(BleException exception) {

                                    }
                                });

                            }
                        }
                    }
                }

                /*if (mBleDeviceBeanList != null && BleManager.getInstance().isConnected(mDeviceBean.getBleDevice())) {
                    if (isChecked) {
                        BleManager.getInstance().write(mDeviceBean.getBleDevice(), Constans.CUSTOM_SERVICE_UUID, Constans.CUSTOM_WRITE_CHARACTERISTIC_UUID, HexUtil.hexStringToBytes("A300"), new BleWriteCallback() {
                            @Override
                            public void onWriteSuccess(int current, int total, byte[] justWrite) {
                                Log.e("onWriteSuccess", "蓝牙指示灯关闭");
                            }

                            @Override
                            public void onWriteFailure(BleException exception) {

                            }
                        });
                    } else {
                        BleManager.getInstance().write(mDeviceBean.getBleDevice(), Constans.CUSTOM_SERVICE_UUID, Constans.CUSTOM_WRITE_CHARACTERISTIC_UUID, HexUtil.hexStringToBytes("A300"), new BleWriteCallback() {
                            @Override
                            public void onWriteSuccess(int current, int total, byte[] justWrite) {
                                Log.e("onWriteSuccess", "蓝牙指示灯关闭");
                            }

                            @Override
                            public void onWriteFailure(BleException exception) {

                            }
                        });
                    }
                }*/

            }
        });
    }

    @OnClick({R.id.iv_back})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
