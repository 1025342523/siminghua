package com.yscoco.siminghua.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yscoco.siminghua.R;
import com.yscoco.siminghua.domain.SongBean;
import com.yscoco.siminghua.ui.activity.music.adapter.MusicControlPopupAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZhangZeZhi on 2018-11-25.
 */

public class MusicControlPopupWindow extends PopupWindow implements MusicControlPopupAdapter.OnItemClickListener {

    private Context mContext;
    private View mRootView;
    private RecyclerView mRv;
    private List<SongBean> mSongBeanList = new ArrayList<>();
    private MusicControlPopupAdapter mAdapter;

    public MusicControlPopupWindow(Context context) {
        super(null, ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.MATCH_PARENT, true);
        this.mContext = context;

        setTouchable(true);
        setOutsideTouchable(true);
        setClippingEnabled(false);

        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);

        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(Color.parseColor("#88000000"));
        setBackgroundDrawable(new BitmapDrawable(context.getResources(), bitmap));

        initView();
    }

    private void initView() {

        mRootView = LayoutInflater.from(mContext).inflate(R.layout.layout_custom_mode_popup, null);
        setContentView(mRootView);
        mRv = mRootView.findViewById(R.id.rv);
        mRv.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new MusicControlPopupAdapter(R.layout.layout_custom_mode_popup_rv_item, mSongBeanList);
        mAdapter.setOnItemClickListener(this);
        mRv.setAdapter(mAdapter);

        mRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    public void setData(List<SongBean> songInfoList) {
        mSongBeanList.clear();
        mSongBeanList.addAll(songInfoList);
        mAdapter.setNewData(mSongBeanList);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (mListener != null) {
            mListener.onMusicControlItemClick(position, mAdapter.getData());
        }
        dismiss();
    }

    private OnMusicControlItemClickListener mListener;

    public void setOnMusicControlItemClickListener(OnMusicControlItemClickListener l) {
        mListener = l;
    }

    public interface OnMusicControlItemClickListener {

        void onMusicControlItemClick(int position, List<SongBean> songBeanList);

    }


}


