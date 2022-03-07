package com.yscoco.siminghua.ui.activity.custom.adapter;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ys.module.utils.StringUtils;
import com.yscoco.siminghua.Constans;
import com.yscoco.siminghua.R;
import com.yscoco.siminghua.domain.CustomBean;

import java.util.List;

/**
 * Created by ZhangZeZhi on 2018-10-28.
 */

public class CustomModePopupAdapter extends BaseQuickAdapter<CustomBean, BaseViewHolder> {

    public CustomModePopupAdapter(int layoutResId, @Nullable List<CustomBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, CustomBean item) {
        if (item == null) {
            return;
        }
        if (!StringUtils.isEmpty(item.getName())) {
            helper.setText(R.id.tv_name, item.getName());
        }
        helper.setText(R.id.tv_head_number, String.valueOf(helper.getPosition() + 1));
        if (item.getTypeCode() == Constans.MODE_TYPE_ONE_CODE) {
            helper.setText(R.id.tv_mode, "(1震动)");
        } else if (item.getTypeCode() == Constans.MODE_TYPE_TWO_CODE) {
            helper.setText(R.id.tv_mode, "(2震动)");
        } else if (item.getTypeCode() == Constans.MODE_TYPE_THREE_CODE) {
            helper.setText(R.id.tv_mode, "(震动+旋转)");
        } else if (item.getTypeCode() == Constans.MODE_TYPE_FOUR_CODE) {
            helper.setText(R.id.tv_mode, "(震动+缩放)");
        }
        TextView tvHeadNumber = helper.getView(R.id.tv_head_number);
        ImageView ivHorn = helper.getView(R.id.iv_horn);
        if (item.isPlayer()) {
            tvHeadNumber.setVisibility(View.GONE);
            ivHorn.setVisibility(View.VISIBLE);
        } else {
            tvHeadNumber.setVisibility(View.VISIBLE);
            ivHorn.setVisibility(View.GONE);
        }
    }
}
