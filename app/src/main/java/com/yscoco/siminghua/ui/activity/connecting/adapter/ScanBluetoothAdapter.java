package com.yscoco.siminghua.ui.activity.connecting.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ys.module.utils.StringUtils;
import com.yscoco.siminghua.R;
import com.yscoco.siminghua.domain.BleDeviceBean;
import com.yscoco.siminghua.utils.ResourcesUtils;

import java.util.List;

/**
 * Created by ZhangZeZhi on 2018-10-23.
 */

public class ScanBluetoothAdapter extends BaseQuickAdapter<BleDeviceBean, BaseViewHolder> {

    public ScanBluetoothAdapter(int layoutResId, @Nullable List<BleDeviceBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, BleDeviceBean item) {

        if (!StringUtils.isEmpty(item.getBleDevice().getName())) {
            helper.setText(R.id.tv_name, item.getBleDevice().getName());
        } else if (!StringUtils.isEmpty(item.getBleDeviceName())) {
            helper.setText(R.id.tv_name, item.getBleDeviceName());
        }

        if (item.isConnectable()) {
            helper.setText(R.id.tv_add, "已连接");
            helper.setTextColor(R.id.tv_name, ResourcesUtils.getColor(R.color.pink_color));
        } else {
            helper.setText(R.id.tv_add, "");
            helper.setTextColor(R.id.tv_name, ResourcesUtils.getColor(R.color.item_right_color));
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
