package com.yscoco.siminghua.view;

import android.annotation.SuppressLint;
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
import com.yscoco.siminghua.domain.CustomBean;
import com.yscoco.siminghua.ui.activity.custom.adapter.CustomModePopupAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZhangZeZhi on 2018-10-28.
 */

public class CustomModePopupWindow extends PopupWindow implements CustomModePopupAdapter.OnItemClickListener {

    private Context mContext;
    private RecyclerView mRv;

    private List<CustomBean> dataList = new ArrayList<>();
    private CustomModePopupAdapter mAdapter;

    @SuppressLint("NewApi")
    public CustomModePopupWindow(Context context) {
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
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.layout_custom_mode_popup, null);
        setContentView(rootView);
        mRv = (RecyclerView) rootView.findViewById(R.id.rv);
        mRv.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new CustomModePopupAdapter(R.layout.layout_custom_mode_popup_rv_item, dataList);
        mAdapter.setOnItemClickListener(this);
        mRv.setAdapter(mAdapter);
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void setDataList(List<CustomBean> list) {
        dataList.clear();
        dataList.addAll(list);
        mAdapter.setNewData(dataList);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (mListener != null) {
            mListener.onPopupItemClick(mAdapter.getItem(position), position);
        }
        dismiss();
    }

    private OnPopupItemClickListener mListener;

    public void setOnPopupItemClickListener(OnPopupItemClickListener l) {
        mListener = l;
    }

    public interface OnPopupItemClickListener {
        void onPopupItemClick(CustomBean bean, int position);
    }

}










