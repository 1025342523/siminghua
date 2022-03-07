package com.yscoco.siminghua.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.ys.module.utils.StringUtils;
import com.yscoco.siminghua.R;

/**
 * Created by ZhangZeZhi on 2018\10\27 0027.
 */

public class AddModeDialog extends Dialog {

    private Context mContext;
    private TextView mTvTitle;
    private TextView mTvVibrator;
    private TextView mTvVibrator2;
    private TextView mTvRotate;
    private TextView mTvShrink;

    private String title;
    private String vibrator;
    private String vibrator2;
    private String rotate;
    private String shrink;

    private View.OnClickListener vibratorListener;
    private View.OnClickListener vibrator2Listener;
    private View.OnClickListener rotateListener;
    private View.OnClickListener shrinkListener;

    public AddModeDialog(@NonNull Context context) {
        super(context, R.style.DialogStyle);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_choose_your_characteristics);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mTvVibrator = (TextView) findViewById(R.id.tv_vibrator);
        mTvVibrator2 = (TextView) findViewById(R.id.tv_vibrator2);
        mTvRotate = (TextView) findViewById(R.id.tv_rotate);
        mTvShrink = (TextView) findViewById(R.id.tv_shrink);

    }

    @Override
    public void show() {
        super.show();
        show(this);
    }

    private void show(AddModeDialog dialog) {

        if (!StringUtils.isEmpty(dialog.title)) {
            dialog.mTvTitle.setText(dialog.title);
        }

        if (!StringUtils.isEmpty(dialog.vibrator)) {
            mTvVibrator.setText(dialog.vibrator);
        }

        if (!StringUtils.isEmpty(dialog.vibrator2)) {
            mTvVibrator2.setText(dialog.vibrator2);
        }

        if (!StringUtils.isEmpty(dialog.rotate)) {
            mTvRotate.setText(dialog.rotate);
        }

        if (!StringUtils.isEmpty(dialog.shrink)) {
            mTvShrink.setText(dialog.shrink);
        }

        if (vibratorListener != null) {
            dialog.mTvVibrator.setOnClickListener(vibratorListener);
        }

        if (vibrator2Listener != null) {
            dialog.mTvVibrator2.setOnClickListener(vibrator2Listener);
        }

        if (rotateListener != null) {
            dialog.mTvRotate.setOnClickListener(rotateListener);
        }

        if (shrinkListener != null) {
            dialog.mTvShrink.setOnClickListener(shrinkListener);
        }

    }

    public static class Builder {

        private AddModeDialog mDialog;

        public Builder(Context context) {
            this.mDialog = new AddModeDialog(context);
        }

        public Builder setTitle(String title) {
            mDialog.title = title;
            return this;
        }

        public Builder setVibrator(String vibrator) {
            mDialog.vibrator = vibrator;
            return this;
        }

        public Builder setVibrator2(String vibrator2) {
            mDialog.vibrator2 = vibrator2;
            return this;
        }

        public Builder setRotate(String rotate) {
            mDialog.rotate = rotate;
            return this;
        }

        public Builder setShrink(String shrink) {
            mDialog.shrink = shrink;
            return this;
        }

        public Builder setVibratorListener(View.OnClickListener l) {
            mDialog.vibratorListener = l;
            return this;
        }

        public Builder setVibrator2Listener(View.OnClickListener l) {
            mDialog.vibrator2Listener = l;
            return this;
        }

        public Builder setRotateListener(View.OnClickListener l) {
            mDialog.rotateListener = l;
            return this;
        }

        public Builder setShrinkListener(View.OnClickListener l) {
            mDialog.shrinkListener = l;
            return this;
        }

        public AddModeDialog create() {
            return mDialog;
        }

    }

}
