package com.yscoco.siminghua.ui.activity.connecting;

import android.bluetooth.BluetoothGatt;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.kyleduo.switchbutton.SwitchButton;
import com.ys.module.title.TitleBar;
import com.ys.module.toast.ToastTool;
import com.yscoco.siminghua.Constans;
import com.yscoco.siminghua.R;
import com.yscoco.siminghua.base.activity.BaseActivity;
import com.yscoco.siminghua.domain.BleDeviceBean;
import com.yscoco.siminghua.ui.activity.connecting.adapter.ConnectingDeviceAdapter;
import com.yscoco.siminghua.utils.ResourcesUtils;
import com.yscoco.siminghua.utils.SpUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by ZhangZeZhi on 2018\10\23 0023.
 */

public class ConnectingDeviceActivity extends BaseActivity implements ConnectingDeviceAdapter.OnCheckChangeListener {

    @BindView(R.id.tb_title)
    TitleBar mTitleBar;

    @BindView(R.id.rv)
    RecyclerView mRv;

    private List<BleDeviceBean> mBleDeviceBeanList;
    private ConnectingDeviceAdapter mAdapter;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_connecting_device;
    }

    @Override
    protected void init() {
        transparentStatusBar(R.color.white_255);
        mTitleBar.setTitle(R.string.connecting_device_text, ResourcesUtils.getColor(R.color.title_color));
        mTitleBar.gone();
        mRv.setLayoutManager(new LinearLayoutManager(this));
        mBleDeviceBeanList = SpUtils.getDataList(Constans.BLUETOOTH_DEVICE_KEYS, BleDeviceBean.class);
        mAdapter = new ConnectingDeviceAdapter(R.layout.layout_connecting_rv_item, mBleDeviceBeanList);
        mRv.setAdapter(mAdapter);
        mAdapter.setOnCheckChangeListener(this);
        mAdapter.notifyDataSetChanged();
    }

    @OnClick({R.id.iv_left_arrow, R.id.iv_add})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.iv_add:
                showActivity(ScanBluetoothActivity.class);
                break;
            case R.id.iv_left_arrow:
                finish();
                break;
        }
    }

    @Override
    public void onCheckChange(final BleDeviceBean deviceBean, boolean isCheck, final int position) {
        final SwitchButton switchButton = (SwitchButton) mAdapter.getViewByPosition(position, R.id.sb_switch);
        if (isCheck) {
            BleManager.getInstance().connect(deviceBean.getBleDevice(), new BleGattCallback() {
                @Override
                public void onStartConnect() {

                }

                @Override
                public void onConnectFail(BleDevice bleDevice, BleException exception) {
                    ToastTool.showNormalShort(ConnectingDeviceActivity.this, "连接失败");
                    switchButton.setChecked(false);
                }

                @Override
                public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                    ToastTool.showNormalShort(ConnectingDeviceActivity.this, "连接成功");
                }

                @Override
                public void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status) {

                }
            });
        } else {
            BleManager.getInstance().disconnect(deviceBean.getBleDevice());
            ToastTool.showNormalShort(ConnectingDeviceActivity.this, "断开连接成功");
            switchButton.setChecked(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
