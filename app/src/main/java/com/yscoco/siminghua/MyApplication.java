package com.yscoco.siminghua;

import android.app.Activity;
import android.util.Log;

import com.clj.fastble.BleManager;
import com.lzx.musiclibrary.cache.CacheConfig;
import com.lzx.musiclibrary.cache.CacheUtils;
import com.lzx.musiclibrary.manager.MusicLibrary;
import com.lzx.musiclibrary.utils.BaseUtil;
import com.squareup.leakcanary.LeakCanary;
import com.umeng.commonsdk.UMConfigure;
import com.ys.module.log.LogUtils;
import com.yscoco.blue.app.BaseBlueApplication;
import com.yscoco.siminghua.base.activity.BaseActivity;

import me.jessyan.autosize.AutoSize;
import me.jessyan.autosize.AutoSizeConfig;
import me.jessyan.autosize.external.ExternalAdaptInfo;
import me.jessyan.autosize.onAdaptListener;

/**
 * Created by ZhangZeZhi on 2018-10-22.
 */

public class MyApplication extends BaseBlueApplication {

    private static MyApplication instance;
    private static MusicLibrary mMusicLibrary;

    public MyApplication() {
        instance = this;
    }

    public static MyApplication getApplication() {
        if (instance == null) {
            instance = new MyApplication();
        }
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        initThreeService();
    }

    private void initThreeService() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
        UMConfigure.init(this, Constans.UMENG_APP_KEY, Constans.CHANNEL_KEY, UMConfigure.DEVICE_TYPE_PHONE, null);
        /*关闭日志*/
        LogUtils.setLog(false);
        BleManager.getInstance().init(getApplication());
        BleManager.getInstance()
                .setReConnectCount(3, 3000)
                .setConnectOverTime(10000)
                .setOperateTimeout(3000);

        AutoSizeConfig.getInstance()
                .setCustomFragment(false)
                .setOnAdaptListener(new onAdaptListener() {
                    @Override
                    public void onAdaptBefore(Object target, Activity activity) {

                    }

                    @Override
                    public void onAdaptAfter(Object target, Activity activity) {

                    }
                });
        AutoSize.initCompatMultiProcess(this);
        customAdaptForExternal();
        if (BaseUtil.getCurProcessName(this).equals("com.yscoco.siminghua")) {
            CacheConfig cacheConfig = new CacheConfig.Builder()
                    .setOpenCacheWhenPlaying(false)
                    .setCachePath(CacheUtils.getStorageDirectoryPath() + "/MusicControl/Cache/")
                    .build();
            mMusicLibrary = new MusicLibrary.Builder(this)
                    .setCacheConfig(cacheConfig)
                    .setUseMediaPlayer(true)
                    .setAutoPlayNext(true)
                    .build();
            mMusicLibrary.startMusicService();
            Log.e("packageName", getPackageName());

        }
    }

    private void customAdaptForExternal() {
        AutoSizeConfig.getInstance().getExternalAdaptManager()
                .addExternalAdaptInfoOfActivity(BaseActivity.class, new ExternalAdaptInfo(true, 400));
    }

    public static MusicLibrary getMusicLibrary() {
        return mMusicLibrary;
    }

}
