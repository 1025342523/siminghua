package com.yscoco.siminghua.ui.activity.music.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ys.module.utils.StringUtils;
import com.yscoco.siminghua.R;
import com.yscoco.siminghua.domain.SongBean;
import com.yscoco.siminghua.utils.ResourcesUtils;

import java.util.List;

/**
 * Created by ZhangZeZhi on 2018\11\26 0026.
 */

public class MusicControlPopupAdapter extends BaseQuickAdapter<SongBean, BaseViewHolder> {

    public MusicControlPopupAdapter(int layoutResId, @Nullable List<SongBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, SongBean item) {
        helper.setVisible(R.id.iv_right_view, true);
        helper.setText(R.id.tv_head_number, (helper.getPosition() + 1) + "");
        if (!StringUtils.isEmpty(item.getSongInfo().getSongName())) {
            helper.setText(R.id.tv_name, item.getSongInfo().getSongName());
        } else {
            helper.setText(R.id.tv_name, "未知歌曲");
        }

        if (item.isPlay()) {
            helper.setTextColor(R.id.tv_name, ResourcesUtils.getColor(R.color.pink_color));
            helper.setVisible(R.id.iv_horn, true);
            helper.setGone(R.id.tv_head_number, false);
        } else {
            helper.setTextColor(R.id.tv_name, ResourcesUtils.getColor(R.color.black_255));
            helper.setVisible(R.id.tv_head_number, true);
            helper.setGone(R.id.iv_horn, false);
        }
    }
}


