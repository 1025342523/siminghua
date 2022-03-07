package com.yscoco.siminghua.ui.activity.music;

import android.content.Intent;
import android.graphics.Color;
import android.media.audiofx.Visualizer;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.utils.HexUtil;
import com.lzx.musiclibrary.aidl.listener.OnPlayerEventListener;
import com.lzx.musiclibrary.aidl.model.SongInfo;
import com.lzx.musiclibrary.constans.PlayMode;
import com.lzx.musiclibrary.manager.MusicManager;
import com.lzx.musiclibrary.manager.TimerTaskManager;
import com.ys.module.title.TitleBar;
import com.yscoco.siminghua.Constans;
import com.yscoco.siminghua.R;
import com.yscoco.siminghua.base.activity.BaseActivity;
import com.yscoco.siminghua.domain.BleDeviceBean;
import com.yscoco.siminghua.domain.SongBean;
import com.yscoco.siminghua.utils.BottomNavigationBarUtils;
import com.yscoco.siminghua.utils.DateUtil;
import com.yscoco.siminghua.utils.ResourcesUtils;
import com.yscoco.siminghua.utils.SpUtils;
import com.yscoco.siminghua.utils.ThreadUtils;
import com.yscoco.siminghua.view.MusicControlPopupWindow;
import com.yscoco.siminghua.view.visualizers.RendererFactory;
import com.yscoco.siminghua.view.visualizers.WaveformView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by ZhangZeZhi on 2018\11\24 0024.
 */

public class MusicControlActivity extends BaseActivity implements OnPlayerEventListener {

    private static final int CAPTURE_SIZE = 256;

    @BindView(R.id.tb_title)
    TitleBar mTitleBar;

    @BindView(R.id.iv_dot_pink_x_25)
    ImageView mIvX25;

    @BindView(R.id.iv_dot_x_25)
    ImageView mIvDotX25;

    @BindView(R.id.iv_dot_pink_x_5)
    ImageView mIvX5;

    @BindView(R.id.iv_dot_x_5)
    ImageView mIvDotX5;

    @BindView(R.id.iv_dot_pink_x1)
    ImageView mIvX1;

    @BindView(R.id.iv_dot_x1)
    ImageView mIvDotX1;

    @BindView(R.id.iv_dot_pink_x2)
    ImageView mIvX2;

    @BindView(R.id.iv_dot_x2)
    ImageView mIvDotX2;

    @BindView(R.id.iv_dot_pink_x4)
    ImageView mIvX4;

    @BindView(R.id.iv_dot_x4)
    ImageView mIvDotX4;

    @BindView(R.id.ll_x_25)
    LinearLayout mLlX25;

    @BindView(R.id.ll_x_5)
    LinearLayout mLlX5;

    @BindView(R.id.ll_x1)
    LinearLayout mLlX1;

    @BindView(R.id.ll_x2)
    LinearLayout mLlX2;

    @BindView(R.id.ll_x4)
    LinearLayout mLlX4;

    @BindView(R.id.iv_pause_or_player)
    ImageView mIvPauseOrPlayer;

    @BindView(R.id.iv_play_mode)
    ImageView mIvPlayMode;

    @BindView(R.id.wfv)
    WaveformView mWfv;

    @BindView(R.id.tv_time)
    TextView mTvTime;

    private int playCurrentState = 1;
    private int pauseState = 2;
    private int playState = 1;
    private int currentMode = 1;
    private int listLoopMode = 1;
    private int randomMode = 3;
    private Visualizer mVisualizer;
    private int singleSycleMode = 2;
    private int mPosition;
    private String mListName;
    private List<SongBean> mSongBeanList = new ArrayList<>();
    private List<SongInfo> mSongInfoList = new ArrayList<>();
    private MusicControlPopupWindow mPopupWindow;
    private List<BleDeviceBean> mDeviceBeanList;
    private TimerTaskManager mTimerTaskManager;
    private long duration;
    private boolean isFirstRun = false;
    private boolean isFirstWrite = false;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_music_control;
    }

    @Override
    protected void init() {
        transparentStatusBar(R.color.transparent);
        mTitleBar.setTitle(R.string.music_control_text, ResourcesUtils.getColor(R.color.white_255));
        mTitleBar.gone();
        Intent intent = getIntent();
        mPosition = intent.getIntExtra(Constans.POSITION_KEY, -1);
        mListName = intent.getStringExtra(Constans.LIST_NAME_KEY);
        mSongBeanList = SpUtils.getDataList(mListName, SongBean.class);

        mDeviceBeanList = SpUtils.getDataList(Constans.BLUETOOTH_DEVICE_KEYS, BleDeviceBean.class);

        MusicManager.get().addPlayerEventListener(this);
        MusicManager.get().setPlayMode(PlayMode.PLAY_IN_ORDER);//顺序播放
        if (mSongBeanList.size() > 0) {
            Log.e("SongBean", mSongBeanList.size() + "     mPosition = " + mPosition);
            SongBean bean = mSongBeanList.get(mPosition);
            bean.setPlay(true);
            mSongBeanList.set(mPosition, bean);
            for (SongBean songBean : mSongBeanList) {
                mSongInfoList.add(songBean.getSongInfo());
            }
            duration = mSongBeanList.get(mPosition).getSongInfo().getDuration();
            MusicManager.get().playMusic(mSongInfoList, mPosition);
        }

        initPopupWindow();
        RendererFactory rendererFactory = new RendererFactory();
        mWfv.setRenderer(rendererFactory.createSimpleWaveformRender(ContextCompat.getColor(this, R.color.yellow_color), Color.TRANSPARENT));
        mTimerTaskManager = new TimerTaskManager();
        mTimerTaskManager.setUpdateProgressTask(this::updateTime);

    }

    private void updateTime() {
        long progress = MusicManager.get().getProgress();
        mTvTime.setText(DateUtil.getDate((duration - progress), "mm:ss"));
    }

    private void initPopupWindow() {
        mPopupWindow = new MusicControlPopupWindow(this);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setData(mSongBeanList);

        mPopupWindow.setOnMusicControlItemClickListener(new MusicControlPopupWindow.OnMusicControlItemClickListener() {
            @Override
            public void onMusicControlItemClick(int position, List<SongBean> songBeanList) {
                SongBean songBean = mSongBeanList.get(mPosition);
                mSongBeanList.set(mPosition, songBean);
                songBean.setPlay(false);
                SongBean songBeanPos = mSongBeanList.get(position);
                songBeanPos.setPlay(true);
                mSongBeanList.set(position, songBeanPos);
                mPosition = position;
                MusicManager.get().playMusic(mSongInfoList, position);
                mPopupWindow.setData(mSongBeanList);
                mIvPauseOrPlayer.setImageResource(R.mipmap.icon_player);
                playCurrentState = playState;
            }
        });

    }

    @OnClick({R.id.iv_left_arrow, R.id.ll_x_25
            , R.id.ll_x_5, R.id.ll_x1, R.id.ll_x2
            , R.id.ll_x4, R.id.iv_play_mode
            , R.id.iv_previous, R.id.iv_next
            , R.id.iv_navigation, R.id.iv_pause_or_player})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_left_arrow:
                finish();
                break;
            case R.id.ll_x_25:
                selectX25();
                break;
            case R.id.ll_x_5:
                selectX5();
                break;
            case R.id.ll_x1:
                selectX1();
                break;
            case R.id.ll_x2:
                selectX2();
                break;
            case R.id.ll_x4:
                selectX4();
                break;
            case R.id.iv_play_mode:
                if (currentMode == listLoopMode) {
                    currentMode = singleSycleMode;
                    mIvPlayMode.setImageResource(R.mipmap.icon_single_cycle);
                    MusicManager.get().setPlayMode(PlayMode.PLAY_IN_SINGLE_LOOP);
                } else if (currentMode == singleSycleMode) {
                    currentMode = randomMode;
                    mIvPlayMode.setImageResource(R.mipmap.icon_random);
                    MusicManager.get().setPlayMode(PlayMode.PLAY_IN_RANDOM);
                } else if (currentMode == randomMode) {
                    currentMode = listLoopMode;
                    mIvPlayMode.setImageResource(R.mipmap.icon_list_cycle_play_type);
                    MusicManager.get().setPlayMode(PlayMode.PLAY_IN_LIST_LOOP);
                }
                break;
            case R.id.iv_previous: //上一首
                if (currentMode == listLoopMode || currentMode == singleSycleMode) {
                    if (mPosition == 0) {
                        SongBean songBean = mSongBeanList.get(mPosition);
                        songBean.setPlay(false);
                        mSongBeanList.set(mPosition, songBean);
                        mPosition = mSongBeanList.size() - 1;
                        SongBean songBeanPre = mSongBeanList.get(mPosition);
                        songBeanPre.setPlay(true);
                        mSongBeanList.set(mPosition, songBeanPre);
                        MusicManager.get().playMusic(mSongInfoList, mSongBeanList.size() - 1);
                    } else {
                        SongBean songBean = mSongBeanList.get(mPosition);
                        songBean.setPlay(false);
                        mSongBeanList.set(mPosition, songBean);
                        mPosition -= 1;
                        SongBean songBeanPre = mSongBeanList.get(mPosition);
                        songBeanPre.setPlay(true);
                        mSongBeanList.set(mPosition, songBeanPre);
                        MusicManager.get().playPre();
                    }
                } else if (currentMode == randomMode) {
                    SongBean songBean = mSongBeanList.get(mPosition);
                    songBean.setPlay(false);
                    mSongBeanList.set(mPosition, songBean);
                    MusicManager.get().playPre();
                    mPosition = MusicManager.get().getCurrPlayingIndex();
                    SongBean songBeanPre = mSongBeanList.get(mPosition);
                    songBeanPre.setPlay(true);
                    mSongBeanList.set(mPosition, songBeanPre);
                }
                mIvPauseOrPlayer.setImageResource(R.mipmap.icon_player);
                playCurrentState = playState;
                mPopupWindow.setData(mSongBeanList);
                break;
            case R.id.iv_next://下一首
                if (currentMode == listLoopMode || currentMode == singleSycleMode) {
                    if (mPosition == mSongBeanList.size() - 1) {
                        SongBean songBean = mSongBeanList.get(mPosition);
                        songBean.setPlay(false);
                        mSongBeanList.set(mPosition, songBean);
                        mPosition = 0;
                        SongBean songBeanNext = mSongBeanList.get(mPosition);
                        songBeanNext.setPlay(true);
                        mSongBeanList.set(mPosition, songBeanNext);
                        MusicManager.get().playMusic(mSongInfoList, 0);
                    } else {
                        SongBean songBean = mSongBeanList.get(mPosition);
                        songBean.setPlay(false);
                        mSongBeanList.set(mPosition, songBean);
                        mPosition += 1;
                        SongBean songBeanNext = mSongBeanList.get(mPosition);
                        songBeanNext.setPlay(true);
                        mSongBeanList.set(mPosition, songBeanNext);
                        MusicManager.get().playNext();
                    }
                } else if (currentMode == randomMode) {
                    SongBean songBean = mSongBeanList.get(mPosition);
                    songBean.setPlay(false);
                    mSongBeanList.set(mPosition, songBean);
                    MusicManager.get().playNext();
                    mPosition = MusicManager.get().getCurrPlayingIndex();
                    SongBean songBeanPre = mSongBeanList.get(mPosition);
                    songBeanPre.setPlay(true);
                    mSongBeanList.set(mPosition, songBeanPre);
                }
                mIvPauseOrPlayer.setImageResource(R.mipmap.icon_player);
                playCurrentState = playState;
                mPopupWindow.setData(mSongBeanList);
                break;
            case R.id.iv_navigation:
                showPopupWindow();
                break;
            case R.id.iv_pause_or_player:
                if (playCurrentState == playState) {
                    mIvPauseOrPlayer.setImageResource(R.mipmap.icon_pause);
                    playCurrentState = pauseState;
                    MusicManager.get().pauseMusic();
                    devicePause();
                } else {
                    mIvPauseOrPlayer.setImageResource(R.mipmap.icon_player);
                    playCurrentState = playState;
                    MusicManager.get().resumeMusic();
                }
                break;
        }
    }

    private void devicePause() {

        if (mDeviceBeanList != null && mDeviceBeanList.size() > 0) {
            stopDeviceOne();
        }

        if (mDeviceBeanList != null && mDeviceBeanList.size() > 1) {
            BleManager.getInstance().write(mDeviceBeanList.get(1).getBleDevice(), Constans.CUSTOM_SERVICE_UUID, Constans.CUSTOM_WRITE_CHARACTERISTIC_UUID, HexUtil.hexStringToBytes("A20100"), new BleWriteCallback() {
                @Override
                public void onWriteSuccess(int current, int total, byte[] justWrite) {
                    Log.e("onDestory", justWrite + "");
                }

                @Override
                public void onWriteFailure(BleException exception) {

                }
            });
        }

    }

    private void selectX4() {
        LinearLayout.LayoutParams paramsX4 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                , ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams paramsLlX4 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                , ViewGroup.LayoutParams.WRAP_CONTENT);
        paramsLlX4.weight = 1;
        paramsLlX4.setMargins(0, 0, 0, 0);
        mLlX4.setLayoutParams(paramsLlX4);
        paramsX4.weight = 1;
        paramsX4.setMargins(0, 24, 0, 0);
        mLlX25.setLayoutParams(paramsX4);
        mLlX5.setLayoutParams(paramsX4);
        mLlX2.setLayoutParams(paramsX4);
        mLlX1.setLayoutParams(paramsX4);
        mIvX4.setVisibility(View.VISIBLE);
        mIvDotX4.setImageResource(R.mipmap.icon_dot_120);
        mIvX25.setVisibility(View.GONE);
        mIvDotX25.setImageResource(R.mipmap.icon_dot);
        mIvX5.setVisibility(View.GONE);
        mIvDotX5.setImageResource(R.mipmap.icon_dot);
        mIvX2.setVisibility(View.GONE);
        mIvDotX2.setImageResource(R.mipmap.icon_dot);
        mIvX1.setVisibility(View.GONE);
        mIvDotX1.setImageResource(R.mipmap.icon_dot);
//                AudioManager audioManager4 = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//                int currentVolume4 = audioManager4.getStreamVolume(AudioManager.STREAM_MUSIC);
//        MusicManager.get().setPlaybackParameters(4f, MusicManager.get().getPlaybackPitch());
    }

    private void selectX2() {
        LinearLayout.LayoutParams paramsX2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                , ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams paramsLlX2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                , ViewGroup.LayoutParams.WRAP_CONTENT);
        paramsX2.weight = 1;
        paramsLlX2.weight = 1;
        paramsLlX2.setMargins(0, 0, 0, 0);
        mLlX2.setLayoutParams(paramsLlX2);
        paramsX2.setMargins(0, 24, 0, 0);
        mLlX25.setLayoutParams(paramsX2);
        mLlX5.setLayoutParams(paramsX2);
        mLlX1.setLayoutParams(paramsX2);
        mLlX4.setLayoutParams(paramsX2);
        mIvX2.setVisibility(View.VISIBLE);
        mIvDotX2.setImageResource(R.mipmap.icon_dot_120);
        mIvX25.setVisibility(View.GONE);
        mIvDotX25.setImageResource(R.mipmap.icon_dot);
        mIvX5.setVisibility(View.GONE);
        mIvDotX5.setImageResource(R.mipmap.icon_dot);
        mIvX1.setVisibility(View.GONE);
        mIvDotX1.setImageResource(R.mipmap.icon_dot);
        mIvX4.setVisibility(View.GONE);
        mIvDotX4.setImageResource(R.mipmap.icon_dot);
                /*AudioManager audioManager2 = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                int currentVolume2 = audioManager2.getStreamVolume(AudioManager.STREAM_MUSIC);*/
        /*MusicManager.get().pauseMusic();
        MusicManager.get().setPlaybackParameters(2f, MusicManager.get().getPlaybackPitch());
        MusicManager.get().resumeMusic();*/
    }

    private void selectX1() {
        LinearLayout.LayoutParams paramsX1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                , ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams paramsLlX1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                , ViewGroup.LayoutParams.WRAP_CONTENT);
        paramsX1.weight = 1;
        paramsLlX1.weight = 1;
        paramsLlX1.setMargins(0, 0, 0, 0);
        mLlX1.setLayoutParams(paramsLlX1);
        paramsX1.setMargins(0, 24, 0, 0);
        mLlX25.setLayoutParams(paramsX1);
        mLlX5.setLayoutParams(paramsX1);
        mLlX2.setLayoutParams(paramsX1);
        mLlX4.setLayoutParams(paramsX1);
        mIvX1.setVisibility(View.VISIBLE);
        mIvDotX1.setImageResource(R.mipmap.icon_dot_120);
        mIvX25.setVisibility(View.GONE);
        mIvDotX25.setImageResource(R.mipmap.icon_dot);
        mIvX5.setVisibility(View.GONE);
        mIvDotX5.setImageResource(R.mipmap.icon_dot);
        mIvX2.setVisibility(View.GONE);
        mIvDotX2.setImageResource(R.mipmap.icon_dot);
        mIvX4.setVisibility(View.GONE);
        mIvDotX4.setImageResource(R.mipmap.icon_dot);
                /*AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);*/
//        MusicManager.get().setPlaybackParameters(1f, MusicManager.get().getPlaybackPitch());
    }

    private void selectX5() {
        LinearLayout.LayoutParams paramsX5 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                , ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams paramsLlX5 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                , ViewGroup.LayoutParams.WRAP_CONTENT);
        paramsX5.weight = 1;
        paramsLlX5.weight = 1;
        paramsLlX5.setMargins(0, 0, 0, 0);
        mLlX5.setLayoutParams(paramsLlX5);
        paramsX5.setMargins(0, 24, 0, 0);
        mLlX25.setLayoutParams(paramsX5);
        mLlX1.setLayoutParams(paramsX5);
        mLlX2.setLayoutParams(paramsX5);
        mLlX4.setLayoutParams(paramsX5);
        mIvX5.setVisibility(View.VISIBLE);
        mIvDotX5.setImageResource(R.mipmap.icon_dot_120);
        mIvX25.setVisibility(View.GONE);
        mIvDotX25.setImageResource(R.mipmap.icon_dot);
        mIvX1.setVisibility(View.GONE);
        mIvDotX1.setImageResource(R.mipmap.icon_dot);
        mIvX2.setVisibility(View.GONE);
        mIvDotX2.setImageResource(R.mipmap.icon_dot);
        mIvX4.setVisibility(View.GONE);
        mIvDotX4.setImageResource(R.mipmap.icon_dot);
                /*AudioManager audioManager_5 = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                int currentVolume_5 = audioManager_5.getStreamVolume(AudioManager.STREAM_MUSIC);*/
//        MusicManager.get().setPlaybackParameters(0.5f, MusicManager.get().getPlaybackPitch());
    }

    private void selectX25() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                , ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams paramsLlX25 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                , ViewGroup.LayoutParams.WRAP_CONTENT);
        params.weight = 1;
        paramsLlX25.weight = 1;
        paramsLlX25.setMargins(0, 0, 0, 0);
        mLlX25.setLayoutParams(paramsLlX25);
        params.setMargins(0, 24, 0, 0);
        mLlX5.setLayoutParams(params);
        mLlX1.setLayoutParams(params);
        mLlX2.setLayoutParams(params);
        mLlX4.setLayoutParams(params);
        mIvX25.setVisibility(View.VISIBLE);
        mIvDotX25.setImageResource(R.mipmap.icon_dot_120);
        mIvX5.setVisibility(View.GONE);
        mIvDotX5.setImageResource(R.mipmap.icon_dot);
        mIvX1.setVisibility(View.GONE);
        mIvDotX1.setImageResource(R.mipmap.icon_dot);
        mIvX2.setVisibility(View.GONE);
        mIvDotX2.setImageResource(R.mipmap.icon_dot);
        mIvX4.setVisibility(View.GONE);
        mIvDotX4.setImageResource(R.mipmap.icon_dot);
                /*AudioManager audioManager_25 = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                int currentVolume_25 = audioManager_25.getStreamVolume(AudioManager.STREAM_MUSIC);*/
//        MusicManager.get().setPlaybackParameters(0.25f, MusicManager.get().getPlaybackPitch());
    }

    private void showPopupWindow() {
        View parent = LayoutInflater.from(this).inflate(R.layout.activity_music_control, null);
        mPopupWindow.showAtLocation(parent, Gravity.BOTTOM | Gravity.LEFT, 0, BottomNavigationBarUtils.getNavigationBarHeight(this));
    }

    // 设置音频线
    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    private void startVisualiser() {
        mVisualizer = new Visualizer(0); // 初始化
        mVisualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
            @Override
            public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {
                Log.e("onWaveFormDataCapture", String.valueOf(samplingRate));
                if (mWfv != null) {
                    mWfv.setWaveform(waveform);
                }

                if (!isFirstRun) {
                    ThreadUtils.getThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {

                            if (mDeviceBeanList != null && mDeviceBeanList.size() > 0) {
                                for (byte wave : waveform) {
                                    if (playCurrentState == playState) {
                                        BleManager.getInstance().write(mDeviceBeanList.get(0).getBleDevice(), Constans.CUSTOM_SERVICE_UUID, Constans.CUSTOM_WRITE_CHARACTERISTIC_UUID, HexUtil.hexStringToBytes("A201" + wave), new BleWriteCallback() {
                                            @Override
                                            public void onWriteSuccess(int current, int total, byte[] justWrite) {
                                                Log.e("onWriteSuccess1", justWrite + "");
                                                isFirstWrite = false;
                                            }

                                            @Override
                                            public void onWriteFailure(BleException exception) {

                                            }
                                        });
                                    } else {
                                        if (!isFirstWrite) {
                                            BleManager.getInstance().write(mDeviceBeanList.get(0).getBleDevice(), Constans.CUSTOM_SERVICE_UUID, Constans.CUSTOM_WRITE_CHARACTERISTIC_UUID, HexUtil.hexStringToBytes("A20100"), new BleWriteCallback() {
                                                @Override
                                                public void onWriteSuccess(int current, int total, byte[] justWrite) {
                                                    Log.e("onWriteSuccess1", justWrite + "");
                                                    isFirstWrite = true;
                                                }

                                                @Override
                                                public void onWriteFailure(BleException exception) {

                                                }
                                            });
                                        }
                                    }
                                }

                            }

                            if (mDeviceBeanList != null && mDeviceBeanList.size() > 1) {
                                for (byte wave : waveform) {
                                    if (playCurrentState == playState) {
                                        BleManager.getInstance().write(mDeviceBeanList.get(1).getBleDevice(), Constans.CUSTOM_SERVICE_UUID, Constans.CUSTOM_WRITE_CHARACTERISTIC_UUID, HexUtil.hexStringToBytes("A201" + wave), new BleWriteCallback() {
                                            @Override
                                            public void onWriteSuccess(int current, int total, byte[] justWrite) {
                                                Log.e("onWriteSuccess2", justWrite + "");
                                                isFirstWrite = false;
                                            }

                                            @Override
                                            public void onWriteFailure(BleException exception) {

                                            }
                                        });
                                    } else {
                                        if (!isFirstWrite) {
                                            BleManager.getInstance().write(mDeviceBeanList.get(1).getBleDevice(), Constans.CUSTOM_SERVICE_UUID, Constans.CUSTOM_WRITE_CHARACTERISTIC_UUID, HexUtil.hexStringToBytes("A20100"), new BleWriteCallback() {
                                                @Override
                                                public void onWriteSuccess(int current, int total, byte[] justWrite) {
                                                    Log.e("onWriteSuccess1", justWrite + "");
                                                    isFirstWrite = true;
                                                }

                                                @Override
                                                public void onWriteFailure(BleException exception) {

                                                }
                                            });
                                        }
                                    }
                                }

                            }

                            isFirstRun = true;
                        }
                    });
                }


            }

            @Override
            public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {
                Log.e("onFftDataCapture", String.valueOf(fft));
                if (fft != null) {
                    for (int j = 0; j < fft.length; j++) {
                        Log.e("fft", fft[j] + "");
                    }
                }
            }
        }, Visualizer.getMaxCaptureRate(), true, false);
        mVisualizer.setCaptureSize(CAPTURE_SIZE);
        mVisualizer.setEnabled(true);
    }

    @Override
    protected void onResume() {
        startVisualiser();
        super.onResume();
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    @Override
    protected void onPause() {
        if (mVisualizer != null) {
            mVisualizer.setEnabled(false);
            mVisualizer.release();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        MusicManager.get().stopMusic();
        mTimerTaskManager.onRemoveUpdateProgressTask();
        MusicManager.get().removePlayerEventListener(this);
        playCurrentState = pauseState;
        isFirstRun = false;
        isFirstWrite = false;

        playCurrentState = pauseState;

        if (mDeviceBeanList != null && mDeviceBeanList.size() > 0) {
            stopDeviceOne();
        }

        if (mDeviceBeanList != null && mDeviceBeanList.size() > 1) {
            stopDeviceTwo();
        }

        super.onDestroy();
    }

    @Override
    public void onMusicSwitch(SongInfo music) {
        Log.e("onMusicSwitch", "id=" + music.getSongId() + "   url=" + music.getSongUrl());
        if (music != null) {
            duration = music.getDuration();
            mTvTime.setText(DateUtil.getDate(music.getDuration(), "mm:ss"));
        }
    }

    @Override
    public void onPlayerStart() {
        Log.e("onPlayerStart", "onPlayerStart");
        mTimerTaskManager.scheduleSeekBarUpdate();
    }

    @Override
    public void onPlayerPause() {
        stopDeviceOne();
        stopDeviceTwo();
        Log.e("onPlayerPause", "onPlayerPause");
        mTimerTaskManager.stopSeekBarUpdate();
    }

    private void stopDeviceOne() {
        if (mDeviceBeanList != null && mDeviceBeanList.size() > 0) {
            BleManager.getInstance().write(mDeviceBeanList.get(0).getBleDevice(), Constans.CUSTOM_SERVICE_UUID, Constans.CUSTOM_WRITE_CHARACTERISTIC_UUID, HexUtil.hexStringToBytes("A20100"), new BleWriteCallback() {
                @Override
                public void onWriteSuccess(int current, int total, byte[] justWrite) {
                    Log.e("onDestory", justWrite + "");
                }

                @Override
                public void onWriteFailure(BleException exception) {

                }
            });
        }
    }

    private void stopDeviceTwo() {

        if (mDeviceBeanList != null && mDeviceBeanList.size() > 1) {

            BleManager.getInstance().write(mDeviceBeanList.get(1).getBleDevice(), Constans.CUSTOM_SERVICE_UUID, Constans.CUSTOM_WRITE_CHARACTERISTIC_UUID, HexUtil.hexStringToBytes("A20100"), new BleWriteCallback() {
                @Override
                public void onWriteSuccess(int current, int total, byte[] justWrite) {
                    Log.e("onDestory", justWrite + "");
                }

                @Override
                public void onWriteFailure(BleException exception) {

                }
            });

        }

        Log.e("onPlayerPause", "onPlayerPause");
        mTimerTaskManager.stopSeekBarUpdate();

    }

    @Override
    public void onPlayCompletion(SongInfo songInfo) {
        Log.e("onPlayCompletion", "SongInfo=" + songInfo.toString());
        if (currentMode == listLoopMode) {
            if (mPosition == mSongBeanList.size() - 1) {
                SongBean songBean = mSongBeanList.get(mPosition);
                songBean.setPlay(false);
                mSongBeanList.set(mPosition, songBean);
                mPosition = 0;
                SongBean songBeanNext = mSongBeanList.get(mPosition);
                songBeanNext.setPlay(true);
                mSongBeanList.set(mPosition, songBeanNext);
                MusicManager.get().playMusic(mSongInfoList, 0);
            } else {
                SongBean songBean = mSongBeanList.get(mPosition);
                songBean.setPlay(false);
                mSongBeanList.set(mPosition, songBean);
                mPosition += 1;
                SongBean songBeanNext = mSongBeanList.get(mPosition);
                songBeanNext.setPlay(true);
                mSongBeanList.set(mPosition, songBeanNext);
            }

        } else if (currentMode == singleSycleMode) {

        } else if (currentMode == randomMode) {

            SongBean songBean = mSongBeanList.get(mPosition);
            songBean.setPlay(false);
            mSongBeanList.set(mPosition, songBean);
            mPosition = MusicManager.get().getCurrPlayingIndex();
            SongBean songBeanRandom = mSongBeanList.get(mPosition);
            songBeanRandom.setPlay(true);
            mSongBeanList.set(mPosition, songBeanRandom);

        }

        mIvPauseOrPlayer.setImageResource(R.mipmap.icon_player);
        playCurrentState = playState;
        mPopupWindow.setData(mSongBeanList);

    }

    @Override
    public void onPlayerStop() {
        Log.e("onPlayerStop", "onPlayerStop");
    }

    @Override
    public void onError(String errorMsg) {
        Log.e("onError", "errorMsg" + errorMsg);
    }

    @Override
    public void onAsyncLoading(boolean isFinishLoading) {
        Log.e("onAsyncLoading", isFinishLoading + "");
    }

}

