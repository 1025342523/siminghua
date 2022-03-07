package com.yscoco.siminghua.view.visualizers;

import android.support.annotation.ColorInt;

/**
 * 工厂模式
 * <p>
 */
public class RendererFactory {
    public WaveformRenderer createSimpleWaveformRender(@ColorInt int foreground, @ColorInt int background) {
        return SimpleWaveformRenderer.newInstance(background, foreground);
    }
}
