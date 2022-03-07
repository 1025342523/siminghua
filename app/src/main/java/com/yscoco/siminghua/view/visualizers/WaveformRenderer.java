package com.yscoco.siminghua.view.visualizers;

import android.graphics.Canvas;

/**
 * 音频波纹渲染接口
 * <p>
 */
public interface WaveformRenderer {
    void render(Canvas canvas, byte[] waveform);
}
