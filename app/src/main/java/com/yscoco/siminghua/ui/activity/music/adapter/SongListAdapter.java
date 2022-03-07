package com.yscoco.siminghua.ui.activity.music.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ys.module.utils.StringUtils;
import com.yscoco.siminghua.R;
import com.yscoco.siminghua.domain.SongBean;

import java.util.List;

/**
 * Created by ZhangZeZhi on 2018\11\22 0022.
 */

public class SongListAdapter extends BaseQuickAdapter<SongBean, BaseViewHolder> {

    public SongListAdapter(int layoutResId, @Nullable List<SongBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, SongBean item) {

        helper.setText(R.id.tv_position, String.valueOf(helper.getPosition() + 1));

        if (!StringUtils.isEmpty(item.getSongInfo().getSongName())) {
            helper.setText(R.id.tv_title, item.getSongInfo().getSongName());
        } else {
            helper.setText(R.id.tv_title, "未知");
        }

        if (item.isSelected()) {
            helper.setVisible(R.id.iv_select, true);
        } else {
            helper.setVisible(R.id.iv_select, false);
        }

    }
}
