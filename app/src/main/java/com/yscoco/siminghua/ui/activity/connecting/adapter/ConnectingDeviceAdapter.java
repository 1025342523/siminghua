package com.yscoco.siminghua.ui.activity.connecting.adapter;

import android.support.annotation.Nullable;
import android.widget.CompoundButton;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kyleduo.switchbutton.SwitchButton;
import com.ys.module.utils.StringUtils;
import com.yscoco.siminghua.R;
import com.yscoco.siminghua.domain.BleDeviceBean;

import java.util.List;

/**
 * Created by ZhangZeZhi on 2018\10\24 0024.
 */

public class ConnectingDeviceAdapter extends BaseQuickAdapter<BleDeviceBean, BaseViewHolder> {
    public ConnectingDeviceAdapter(int layoutResId, @Nullable List<BleDeviceBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(final BaseViewHolder helper, final BleDeviceBean item) {
        if (!StringUtils.isEmpty(item.getBleDeviceName())) {
            helper.setText(R.id.tv_name, item.getBleDeviceName());
        } else if (!StringUtils.isEmpty(item.getBleDevice().getName())) {
            helper.setText(R.id.tv_name, item.getBleDevice().getName());
        }
        helper.setChecked(R.id.sb_switch, item.isConnectable());
        final SwitchButton switchButton = helper.getView(R.id.sb_switch);
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switchButton.setEnabled(isChecked);
                if (mListener != null) {
                    mListener.onCheckChange(item, isChecked, helper.getPosition());
                }
            }
        });
    }

    private OnCheckChangeListener mListener;

    public void setOnCheckChangeListener(OnCheckChangeListener l) {
        mListener = l;
    }

    public interface OnCheckChangeListener {
        void onCheckChange(BleDeviceBean bleDeviceBean, boolean isCheck, int position);
    }

}
