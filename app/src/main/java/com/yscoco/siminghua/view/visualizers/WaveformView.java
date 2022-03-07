package com.yscoco.siminghua.view.visualizers;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import java.util.Arrays;

/**
 * 音频波纹视图
 * <p>
 */
public class WaveformView extends View {

    private WaveformRenderer mRenderer; // 绘制类
    private byte[] mWaveform; // 波纹形状

    public WaveformView(Context context) {
        super(context);
    }

    public WaveformView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WaveformView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public WaveformView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setRenderer(WaveformRenderer renderer) {
        mRenderer = renderer;
    }

    public void setWaveform(byte[] waveform) {
        mWaveform = Arrays.copyOf(waveform, waveform.length); // 数组复制
        invalidate(); // 设置波纹之后, 需要重绘
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mRenderer != null) {
            mRenderer.render(canvas, mWaveform);
        }
    }

}
