package com.ys.module.title;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ys.module.R;

public class TitleBar extends RelativeLayout {
    private Activity activity;
    private LinearLayout ll_title_left;
    private TextView mBackBtn;
    private TextView mRightBtn;
    private ImageView iv_title_left;
    private ImageView mRightImage;
    private RelativeLayout rl_title_right;
    private TextView mTitle;
    private RelativeLayout rl_titile;
    private ImageView mTitleImg;

    private LeftCallback mCallback;
    private RightCallback mRightCallback;
    private int type;

    public interface LeftCallback {
        void leftClick(View v);
    }

    public interface RightCallback {
        void rightClick(View v);
    }

    public TitleBar(Context context) {
        this(context, null);
    }

    public TitleBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//		type = ShardPreUtils.readDeviceAdress(context);
        activity = (Activity) context;
        init();
    }

    private void init() {
        View view = View.inflate(getContext(), R.layout.title_bar, this);
        rl_titile = (RelativeLayout) view.findViewById(R.id.rl_titile);
        ll_title_left = (LinearLayout) view.findViewById(R.id.ll_title_left);
        mBackBtn = (TextView) view.findViewById(R.id.back_button);
        rl_title_right = (RelativeLayout) view.findViewById(R.id.rl_title_right);
        mRightBtn = (TextView) view.findViewById(R.id.right_btn);
        iv_title_left = (ImageView) view.findViewById(R.id.iv_title_left);
        mRightImage = (ImageView) view.findViewById(R.id.right_image);
        mTitle = (TextView) view.findViewById(R.id.title_name);
        mTitleImg = (ImageView) view.findViewById(R.id.title_img);
        ll_title_left.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClick(v);
            }
        });

        rl_title_right.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                rightImgClick(v);
            }
        });
    }

    /**
     * ????????????
     *
     * @param rsid
     */
    public void setTitle(int rsid) {
        mTitle.setVisibility(View.VISIBLE);
        mTitle.setText(rsid);
    }

    public void setTitleBg(int color) {
        rl_titile.setBackgroundResource(color);
    }

    /**
     * ????????????
     *
     * @param rsid
     */
    public void setTitle(int rsid, int color) {
        mTitle.setVisibility(View.VISIBLE);
        mTitle.setText(rsid);
        mTitle.setTextColor(color);
    }

    public void setTitle(String text) {
        mTitle.setVisibility(View.VISIBLE);
        mTitle.setText(text);
    }

    /**
     * ??????????????????(???????????????)
     * ???????????????????????????
     *
     * @param isShow
     */
    public void setVis(boolean isShow) {
        if (isShow) {
            ll_title_left.setVisibility(View.VISIBLE);
        } else {
            ll_title_left.setVisibility(View.GONE);
        }
    }

    /*???????????????*/
    public void gone() {
        ll_title_left.setVisibility(View.GONE);
    }

    /*??????????????????*/
    public void setLeftBtnText(int rsid) {
        setLeftBtnText(rsid, false);
    }

    /**
     * @param rsid
     * @param isView ??????????????????
     */
    public void setLeftBtnText(int rsid, boolean isView) {
        mBackBtn.setVisibility(View.VISIBLE);
        iv_title_left.setVisibility(View.GONE);
        if (!isView) {
            mBackBtn.setText("");
        } else {
            mBackBtn.setText(rsid);
        }
    }

    /**
     * ??????????????????(???????????????)
     *
     * @param rsid
     */
    public void setRightBtnText(int rsid) {
        rl_title_right.setVisibility(View.VISIBLE);
        mRightBtn.setVisibility(View.VISIBLE);
        mRightBtn.setText(rsid);
    }

    public void setRightBtnText(String str) {
        rl_title_right.setVisibility(View.VISIBLE);
        mRightBtn.setVisibility(View.VISIBLE);
        mRightBtn.setText(str);
    }

    /**
     * ????????????(???????????????)
     *
     * @param rsid
     */
    public void setLeftImage(int rsid) {
        iv_title_left.setImageResource(rsid);
    }

    /**
     * ??????????????????(???????????????)
     *
     * @param rsid
     */
    public void setRightImage(int rsid) {
        rl_title_right.setVisibility(View.VISIBLE);
        mRightImage.setVisibility(View.VISIBLE);
        mRightImage.setImageResource(rsid);
    }

    public void buttonClick(View v) {
        if (mCallback != null) {
            mCallback.leftClick(v);
        } else {
            activity.finish();
        }
    }

    /**
     * ????????????(????????????)
     *
     * @param callback
     */
    public void setCallback(LeftCallback callback) {
        this.mCallback = callback;
    }

    public void setTitleBackground(int rsid) {
        mTitleImg.setVisibility(View.VISIBLE);
        mTitleImg.setImageResource(rsid);
    }

    public void rightImgClick(View v) {
        if (mRightCallback != null) {
            mRightCallback.rightClick(v);
        }
    }

    /**
     * ????????????(???-??????)
     *
     * @param callback
     */
    public void setRightCallback(RightCallback callback) {
        this.mRightCallback = callback;
    }
}