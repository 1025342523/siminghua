package com.yscoco.siminghua.ui.activity.music.adapter;

import android.support.annotation.Nullable;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ys.module.utils.StringUtils;
import com.yscoco.siminghua.R;
import com.yscoco.siminghua.view.SwipeItemLayout;

import java.util.List;

/**
 * Created by ZhangZeZhi on 2018\10\26 0026.
 */

public class AddSongListAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public AddSongListAdapter(int layoutResId, @Nullable List<String> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(final BaseViewHolder helper, final String item) {

        if (!StringUtils.isEmpty(item)) {
            helper.setText(R.id.tv_name, item);
        }

        helper.setOnClickListener(R.id.main, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onItemClicked(view, item, helper.getPosition());
                }
            }
        });

        helper.setOnClickListener(R.id.tv_delete, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onItemViewClicked(view, helper.getPosition(), (SwipeItemLayout) helper.getConvertView());
                }
            }
        });

        helper.setOnClickListener(R.id.tv_edit, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onItemViewClicked(view, helper.getPosition(), (SwipeItemLayout) helper.getConvertView());
                }
            }
        });
        
    }

    private ItemClickedListener mListener;

    public void setItemClickedListener(ItemClickedListener l) {
        mListener = l;
    }

    public interface ItemClickedListener {

        void onItemClicked(View view, String item, int position);

        void onItemViewClicked(View view, int position, SwipeItemLayout itemLayout);

    }


}
