package com.yscoco.siminghua.ui.activity.music.adapter;

import android.support.annotation.Nullable;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ys.module.utils.StringUtils;
import com.yscoco.siminghua.R;
import com.yscoco.siminghua.domain.SongBean;

import java.util.List;

/**
 * Created by ZhangZeZhi on 2018\11\23 0023.
 */

public class PlayMusicListAdapter extends BaseQuickAdapter<SongBean, BaseViewHolder> {

    public PlayMusicListAdapter(int layoutResId, @Nullable List<SongBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(final BaseViewHolder helper, final SongBean item) {
        helper.setText(R.id.tv_position, helper.getPosition() + 1 + "");
        if (!StringUtils.isEmpty(item.getSongInfo().getSongName())) {
            helper.setText(R.id.tv_title, item.getSongInfo().getSongName());
        }
        helper.setOnClickListener(R.id.main, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onItemClicked(item, helper.getPosition(), helper.getView(R.id.main));
                }
            }
        });
        helper.setOnClickListener(R.id.tv_delete, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onViewClicked(helper.getPosition());
                }
            }
        });

    }

    private OnItemViewClickedListener mListener;

    public void setOnItemViewClickListener(OnItemViewClickedListener l) {
        mListener = l;
    }

    public interface OnItemViewClickedListener {

        void onViewClicked(int position);

        void onItemClicked(SongBean bean, int position, View view);

    }


}
