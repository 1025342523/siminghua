package com.yscoco.siminghua.ui.activity.custom;

import android.os.CountDownTimer;
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
import com.ys.module.title.TitleBar;
import com.yscoco.siminghua.Constans;
import com.yscoco.siminghua.R;
import com.yscoco.siminghua.base.activity.BaseActivity;
import com.yscoco.siminghua.domain.BleDeviceBean;
import com.yscoco.siminghua.domain.CustomBean;
import com.yscoco.siminghua.domain.MessageEvent;
import com.yscoco.siminghua.utils.BottomNavigationBarUtils;
import com.yscoco.siminghua.utils.DataUtils;
import com.yscoco.siminghua.utils.DateUtil;
import com.yscoco.siminghua.utils.ResourcesUtils;
import com.yscoco.siminghua.utils.SpUtils;
import com.yscoco.siminghua.utils.ThreadUtils;
import com.yscoco.siminghua.view.CustomModePopupWindow;
import com.yscoco.siminghua.view.NewLineView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by ZhangZeZhi on 2018\10\27 0027.
 */

public class CustomModeActivity extends BaseActivity {

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

    @BindView(R.id.iv_play_mode)
    ImageView mIvPlayMode;

    @BindView(R.id.iv_pause_or_player)
    ImageView mIvPauseOrPlayer;

    @BindView(R.id.lineView)
    NewLineView mNewLineView;

    @BindView(R.id.towLineView)
    NewLineView mTowLineView;

    @BindView(R.id.tv_time)
    TextView mTvTime;

    @BindView(R.id.iv_battery)
    ImageView mIvBattery;

    @BindView(R.id.iv_battery_two)
    ImageView mIvBatteryTwo;

    private int currentPlayerMode = 1;
    private int currentState = 1; //当前播放状态

    private int listCycleMode = 1;
    private int randomMode = 2;
    private int singleCycleMode = 3;

    private int pauseState = 2;//暂停状态
    private int playerState = 1;//播放状态
    private CustomBean mCustomBean;
    private int type = 0;
    private List<CustomBean> mCustomBeanList = new ArrayList<>();
    private List<Runnable> mRunnableList = new ArrayList<>();
    private List<Boolean> postDataList = new ArrayList<>();
    private List<Boolean> runList = new ArrayList<>();
    private List<CustomBean> initList = new ArrayList<>();
    private List<Integer> mLineList = new ArrayList<>();
    private List<Integer> mTowLineList = new ArrayList<>();
    private List<Integer> randomList = new ArrayList<>();
    private CustomModePopupWindow mPopupWindow;
    private int currentPosition = 0;
    private CustomBean initCustomBean;
    private Runnable mCycleRunnable;
    private boolean isCycleRun = true;
    private boolean isPostData = true;
    private CustomBean mWaveCustomBean;
    private CustomBean mFireworksCustomBean;
    private CustomBean mEarthquakeCustomBean;
    private int speedTime_025 = 80;
    private int speedTime_05 = 40;
    private int speedTime = 20;
    private int speedTime_2 = 10;
    private int speedTime_4 = 5;
    private float speed = 1;
    private CountDownTimer mDownTimer;
    private boolean isFirst = true;
    private boolean isSecnodFirst = true;
    private boolean isSecondLineRun = true;
    private int randomPosition;
    private boolean isFirstWriteSuccess;
    private boolean fourFirstRunMode = true;
    private int pauseOrRun = 0;

    private int mOnePosition = 0;
    private int mTwoPosition = 0;

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            MessageEvent event = new MessageEvent();
            while (isCycleRun) {
                if (mLineList.size() > 0) {
                    for (int i = mOnePosition; i < mLineList.size(); i++) {
                        event.setCode(Constans.VIEW_CYCLE_REFRESH_CODE);
                        if (isPostData) {
                            event.setObj(mLineList.get(i));
                            if (i == mLineList.size() - 1) {
                                mOnePosition = 0;
                            } else {
                                mOnePosition = i;
                            }
                            EventBus.getDefault().post(event);
                            try {
                                Thread.sleep(speedTime);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } else {
                            continue;
                        }
                    }
                }

                try {
                    Thread.sleep(speedTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    };

    private Runnable mSecondRunnable = new Runnable() {
        @Override
        public void run() {
            MessageEvent event = new MessageEvent();
            while (isCycleRun) {
                if (mTowLineList.size() > 0) {
                    for (int i = mTwoPosition; i < mTowLineList.size(); i++) {
                        event.setCode(Constans.SECOND_VIEW_CYCLE_REFRESH_CODE);
                        event.setObj(mTowLineList.get(i));
                        Log.e("mTowLineList", mTowLineList.get(i) + "");
                        if (isPostData) {
                            mTwoPosition = i;
                            EventBus.getDefault().post(event);
                        } else {
                            continue;
                        }
                        try {
                            Thread.sleep(speedTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        Thread.sleep(speedTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };

    private List<BleDeviceBean> mBleDeviceBeanList;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_custom_mode;
    }

    @Override
    protected void init() {
        transparentStatusBar(R.color.transparent);
        mTitleBar.setTitle("自定义模式");
        mTitleBar.gone();
        mTowLineView.setColor(ResourcesUtils.getColor(R.color.yellow_color));
        mCustomBean = (CustomBean) getIntent().getSerializableExtra(Constans.CUSTOM_BEAN_KEY);

        mBleDeviceBeanList = SpUtils.getDataList(Constans.BLUETOOTH_DEVICE_KEYS, BleDeviceBean.class);

        if (getIntent().getIntExtra(Constans.CURRENT_POSITION_KEY, -1) != -1) {
            currentPosition = getIntent().getIntExtra(Constans.CURRENT_POSITION_KEY, -1);
        }

        if (getValue() != null) {
            type = (int) getValue();
        }
        mCustomBeanList.clear();

        if (mCustomBean != null && mCustomBean.isPlayer()) {
            currentState = 1;
            mCustomBeanList.addAll(SpUtils.getDataList(Constans.MODE_NAME_KEY, CustomBean.class));
            for (int j = 0; j < mCustomBeanList.size(); j++) {
                if (mCustomBean.getName().equals(mCustomBeanList.get(j).getName()) && mCustomBean.getTypeCode() == mCustomBeanList.get(j).getTypeCode()
                        && mCustomBean.getLineList().size() == mCustomBeanList.get(j).getLineList().size()) {
                    mCustomBeanList.get(j).setPlayer(true);
                    mCustomBeanList.set(j, mCustomBeanList.get(j));
                    currentPosition = j;
                }
            }
            if (type == 0) {
                SpUtils.setDataList(Constans.MODE_NAME_KEY, mCustomBeanList);
            }
            player(mCustomBean.getTimes(), currentPosition, mCustomBean.getLineList(), mCustomBean.getSecondLineList());
        } else {
            currentState = 1;
            initData();
        }

        if (mCustomBeanList.size() > 0) {
            HashSet<Integer> set = new HashSet<>();
            randomSet(0, mCustomBeanList.size() - 1, mCustomBeanList.size(), set);
            randomList.addAll(set);
            for (int rand : randomList)
                Log.e("random", rand + "");
        }

        Log.e("randomList.size()", randomList.size() + "");

        initPopupWindow();
    }

    private void initData() {

        initCustomBean = new CustomBean();
        initCustomBean.setPlayer(false);
        initCustomBean.setTypeCode(Constans.MODE_TYPE_ONE_CODE);
        initCustomBean.setName("Pulse");
        initCustomBean.setLineList(DataUtils.getPulseList());
        initCustomBean.setTimes(3 * 60 * 1000);

        mWaveCustomBean = new CustomBean();
        mWaveCustomBean.setPlayer(false);
        mWaveCustomBean.setTypeCode(Constans.MODE_TYPE_ONE_CODE);
        mWaveCustomBean.setName("Wave");
        mWaveCustomBean.setLineList(DataUtils.getWaveList());
        mWaveCustomBean.setTimes(22 * 60 * 1000 + 30 * 1000);

        mFireworksCustomBean = new CustomBean();
        mFireworksCustomBean.setPlayer(false);
        mFireworksCustomBean.setTypeCode(Constans.MODE_TYPE_ONE_CODE);
        mFireworksCustomBean.setName("Fireworks");
        mFireworksCustomBean.setLineList(DataUtils.getFireworksList());
        mFireworksCustomBean.setTimes(33 * 60 * 1000 + 20 * 1000);

        mEarthquakeCustomBean = new CustomBean();
        mEarthquakeCustomBean.setPlayer(false);
        mEarthquakeCustomBean.setTypeCode(Constans.MODE_TYPE_ONE_CODE);
        mEarthquakeCustomBean.setName("Earthquake");
        mEarthquakeCustomBean.setLineList(DataUtils.getEarthquake());
        mEarthquakeCustomBean.setTimes(2 * 60 * 1000 + 45 * 1000);

        if (type == Constans.ORIGINAL_MODE_PULSE) {
            initCustomBean.setPlayer(true);
            //播放 pulse
            mCustomBean = initCustomBean;

            player(initCustomBean.getTimes(), 0, initCustomBean.getLineList(), mCustomBean.getSecondLineList());
        } else if (type == Constans.ORIGINAL_MODE_WAVE) {
            mWaveCustomBean.setPlayer(true);
            //播放 wave
            mCustomBean = mWaveCustomBean;
            player(mWaveCustomBean.getTimes(), 1, mWaveCustomBean.getLineList(), mCustomBean.getSecondLineList());
        } else if (type == Constans.ORIGINAL_MODE_FIREWORKS) {
            mFireworksCustomBean.setPlayer(true);
            //播放 fireworks
            mCustomBean = mFireworksCustomBean;
            player(mFireworksCustomBean.getTimes(), 2, mFireworksCustomBean.getLineList(), mCustomBean.getSecondLineList());
        } else if (type == Constans.ORIGINAL_MODE_EARTHQUAKE) {
            mEarthquakeCustomBean.setPlayer(true);
            mCustomBean = mEarthquakeCustomBean;
            player(mEarthquakeCustomBean.getTimes(), 3, mEarthquakeCustomBean.getLineList(), mCustomBean.getSecondLineList());
        }

        mCustomBeanList.add(initCustomBean);
        mCustomBeanList.add(mWaveCustomBean);
        mCustomBeanList.add(mFireworksCustomBean);
        mCustomBeanList.add(mEarthquakeCustomBean);
    }

    private void player(final long totalTime, int position, final List<Integer> lineList, List<Integer> towLineList) {
        isCycleRun = true;
        isPostData = true;
        final long[] countTime = {totalTime};
        currentPosition = position;
        mLineList.clear();
        mTowLineList.clear();
        Log.e("player clear", mLineList.size() + "");
        mLineList.addAll(lineList);

        if (towLineList != null) {
            mTowLineList.addAll(towLineList);
        }

        mTvTime.setText(DateUtil.date2String(totalTime, "mm:ss"));

        if (isFirst) {
            ThreadUtils.getThreadPool().execute(mRunnable);
            isFirst = false;
        } else if (speed == 0.25f || speed == 0.5f || speed == 2f || speed == 4f) {
            ThreadUtils.getThreadPool().execute(mRunnable);
        }

        if (mTowLineList.size() > 0) {
            if (isSecnodFirst) {
                ThreadUtils.getThreadPool().execute(mSecondRunnable);
                isSecnodFirst = false;
            }
            if (!isSecondLineRun) {
                ThreadUtils.getThreadPool().execute(mSecondRunnable);
            }
            Log.e("mTowLineList.size()", mTowLineList.size() + "");
        } else {
            isSecnodFirst = true;
        }

        mDownTimer = new CountDownTimer(totalTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                Log.e("onTick", countTime[0] + "");

                if (countTime[0] < 0 && (speed == 0.25f || speed == 0.5f || speed == 2f || speed == 4f)) {
                    downTimerFinish();
                }

                countTime[0] -= 1000 * speed;
                mTvTime.setText(DateUtil.date2String(countTime[0], "mm:ss"));

            }

            @Override
            public void onFinish() {
                downTimerFinish();
            }
        };

        mDownTimer.start();
    }

    private void downTimerFinish() {
        mNewLineView.clear();
        mTowLineView.clear();
        isCycleRun = false;
        isPostData = false;
        mTowLineList.clear();
        mLineList.clear();
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (currentPlayerMode == listCycleMode) {
            if (currentPosition == mCustomBeanList.size() - 1) {
                mCustomBean.setPlayer(false);
                mCustomBeanList.set(currentPosition, mCustomBean);
                mCustomBean = mCustomBeanList.get(0);
                mCustomBean.setPlayer(true);
                mDownTimer.cancel();
                currentPosition = 0;
                mCustomBeanList.set(currentPosition, mCustomBean);
                player(mCustomBean.getTimes(), currentPosition, mCustomBean.getLineList(), mCustomBean.getSecondLineList());
            } else {
                mCustomBean.setPlayer(false);
                mCustomBeanList.set(currentPosition, mCustomBean);
                mCustomBean = mCustomBeanList.get(currentPosition + 1);
                mCustomBean.setPlayer(true);
                mDownTimer.cancel();
                currentPosition += 1;
                mCustomBeanList.set(currentPosition, mCustomBean);
                player(mCustomBean.getTimes(), currentPosition, mCustomBean.getLineList(), mCustomBean.getSecondLineList());
            }
            mPopupWindow.setDataList(mCustomBeanList);
        } else if (currentPlayerMode == singleCycleMode) {
            player(mCustomBean.getTimes(), currentPosition, mCustomBean.getLineList(), mCustomBean.getSecondLineList());
        } else if (currentPlayerMode == randomMode) {
            Log.e("currentPosition before", currentPosition + "");
            mCustomBean.setPlayer(false);
            mCustomBeanList.set(currentPosition, mCustomBean);
            for (int i = 0; i < randomList.size(); i++) {
                if (currentPosition == randomList.get(i)) {
                    if (i == randomList.size() - 1) {
                        randomPosition = 0;
                        currentPosition = randomList.get(0);
                    } else {
                        randomPosition = i + 1;
                        currentPosition = randomList.get(i + 1);
                    }
                } else {
                    currentPosition = randomList.get(i + 1);
                }
            }
            Log.e("currentPosition after", currentPosition + "");
            mCustomBean = mCustomBeanList.get(currentPosition);
            mCustomBean.setPlayer(true);
            mCustomBeanList.set(currentPosition, mCustomBean);
            player(mCustomBean.getTimes(), currentPosition, mCustomBean.getLineList(), mCustomBean.getSecondLineList());
            mPopupWindow.setDataList(mCustomBeanList);
        }
    }

    private void initPopupWindow() {
        mPopupWindow = new CustomModePopupWindow(this);
        Log.e("initPopupWindow", mCustomBeanList.size() + "");
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setDataList(mCustomBeanList);

        mPopupWindow.setOnPopupItemClickListener(new CustomModePopupWindow.OnPopupItemClickListener() {
            @Override
            public void onPopupItemClick(CustomBean bean, int position) {
                if (currentPosition == position) {
                    return;
                }
                bean.setPlayer(true);
                mCustomBean.setPlayer(false);
                mCustomBeanList.set(currentPosition, mCustomBean);
                mCustomBeanList.set(position, bean);
                mCustomBean = bean;
                mDownTimer.cancel();
                currentPosition = position;
                mNewLineView.clear();
                mTowLineView.clear();
                isCycleRun = false;
                isPostData = false;
                mLineList.clear();
                mTowLineList.clear();
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                player(mCustomBean.getTimes(), mCustomBeanList.size() - 1, mCustomBean.getLineList(), mCustomBean.getSecondLineList());
                if (type == 0) {
                    SpUtils.setDataList(Constans.MODE_NAME_KEY, mCustomBeanList);
                }
                mPopupWindow.setDataList(mCustomBeanList);
            }
        });
    }

    @OnClick({R.id.iv_left_arrow, R.id.ll_x_25
            , R.id.ll_x_5, R.id.ll_x1
            , R.id.ll_x2, R.id.ll_x4
            , R.id.iv_play_mode, R.id.iv_previous
            , R.id.iv_pause_or_player, R.id.iv_next
            , R.id.iv_navigation})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_left_arrow:
                finish();
                break;
            case R.id.ll_x_25:
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
                speedTime = speedTime_025;
                speed = 0.25f;
                break;
            case R.id.ll_x_5:
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
                speedTime = speedTime_05;
                speed = 0.5f;
                break;

            case R.id.ll_x1:
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
                speedTime = 20;
                speed = 1f;
                break;
            case R.id.ll_x2:
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
                speedTime = speedTime_2;
                speed = 2f;
                break;
            case R.id.ll_x4:
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
                speedTime = speedTime_4;
                speed = 4f;
                break;
            case R.id.iv_play_mode: //播放模式切换
                if (currentPlayerMode == listCycleMode) {
                    currentPlayerMode = randomMode;
                    mIvPlayMode.setImageResource(R.mipmap.icon_random);
                } else if (currentPlayerMode == randomMode) {
                    currentPlayerMode = singleCycleMode;
                    mIvPlayMode.setImageResource(R.mipmap.icon_single_cycle);
                } else if (currentPlayerMode == singleCycleMode) {
                    currentPlayerMode = listCycleMode;
                    mIvPlayMode.setImageResource(R.mipmap.icon_list_cycle_play_type);
                }
                break;
            case R.id.iv_previous://上一个
                //TODO
                mNewLineView.clear();
                mTowLineView.clear();
                isCycleRun = false;
                isPostData = false;
                mLineList.clear();
                mTowLineList.clear();
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (type != 0) {
                    if (currentPlayerMode == listCycleMode || currentPlayerMode == singleCycleMode) {
                        if (currentPosition == 0) {
                            Log.e("mCustomBeanList", mCustomBeanList.size() + "   " + currentPosition);
                            mCustomBean.setPlayer(false);
                            mCustomBeanList.set(currentPosition, mCustomBean);
                            mCustomBean = mCustomBeanList.get(mCustomBeanList.size() - 1);
                            mCustomBean.setPlayer(true);
                            mDownTimer.cancel();
                            currentPosition = mCustomBeanList.size() - 1;
                            mCustomBeanList.set(currentPosition, mCustomBean);
                            player(mCustomBean.getTimes(), mCustomBeanList.size() - 1, mCustomBean.getLineList(), mCustomBean.getSecondLineList());
                        } else {
                            Log.e("mCustomBeanList", mCustomBeanList.size() + "   " + currentPosition + "   " + (currentPosition - 1));
                            mCustomBean.setPlayer(false);
                            mCustomBeanList.set(currentPosition, mCustomBean);
                            mCustomBean = mCustomBeanList.get(currentPosition - 1);
                            mDownTimer.cancel();
                            currentPosition -= 1;
                            mCustomBean.setPlayer(true);
                            mCustomBeanList.set(currentPosition, mCustomBean);
                            player(mCustomBean.getTimes(), currentPosition, mCustomBean.getLineList(), mCustomBean.getSecondLineList());
                        }
                    } else if (currentPlayerMode == randomMode) {
                        mCustomBean.setPlayer(false);
                        mCustomBeanList.set(currentPosition, mCustomBean);
                        mDownTimer.cancel();

                        for (int i = 0; i < randomList.size(); i++) {
                            if (currentPosition == randomList.get(i)) {
                                if (i == 0) {
                                    currentPosition = randomList.get(randomList.size() - 1);
                                } else {
                                    currentPosition = randomList.get(i - 1);
                                }
                            }
                        }

                        mCustomBean = mCustomBeanList.get(currentPosition);
                        mCustomBean.setPlayer(true);
                        mCustomBeanList.set(currentPosition, mCustomBean);
                        player(mCustomBean.getTimes(), currentPosition, mCustomBean.getLineList(), mCustomBean.getSecondLineList());
                    }
                }
                mPopupWindow.setDataList(mCustomBeanList);
                break;
            case R.id.iv_pause_or_player:
                if (currentState == playerState) {
                    currentState = pauseState;
                    mIvPauseOrPlayer.setImageResource(R.mipmap.icon_pause);
                    //暂停
                    isPostData = false;
                    isCycleRun = false;
                    mCustomBean.setPlayer(false);
                    mCustomBeanList.set(currentPosition, mCustomBean);
                    if (mDownTimer != null) {
                        mDownTimer.cancel();
                    }
                    stopDevice();
                } else if (currentState == pauseState) {
                    currentState = playerState;
                    mIvPauseOrPlayer.setImageResource(R.mipmap.icon_player);
                    //播放
                    isPostData = true;
                    isCycleRun = true;
                    mCustomBean.setPlayer(true);
                    mCustomBeanList.set(currentPosition, mCustomBean);

                    ThreadUtils.getThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            mRunnable.run();
                        }
                    });

                    if (mCustomBean.getSecondLineList() != null && mCustomBean.getSecondLineList().size() > 0) {
                        ThreadUtils.getThreadPool().execute(new Runnable() {
                            @Override
                            public void run() {
                                mSecondRunnable.run();
                            }
                        });
                    }
                    if (mDownTimer != null) {
                        mDownTimer.start();
                    }
                }
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mPopupWindow.setDataList(mCustomBeanList);
                break;
            case R.id.iv_next://下一个
                mNewLineView.clear();
                mTowLineView.clear();
                isCycleRun = false;
                isPostData = false;
                mLineList.clear();
                mTowLineList.clear();
                try {
                    if (speed == 2f || speed == 4f) {
                        Thread.sleep(40);
                    } else {
                        Thread.sleep(20);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (currentPlayerMode == listCycleMode || currentPlayerMode == singleCycleMode) {

                    if (currentPosition == mCustomBeanList.size() - 1) {

                        mCustomBean.setPlayer(false);
                        mCustomBeanList.set(currentPosition, mCustomBean);
                        mCustomBean = mCustomBeanList.get(0);
                        mCustomBean.setPlayer(true);
                        mDownTimer.cancel();
                        currentPosition = 0;
                        mCustomBeanList.set(currentPosition, mCustomBean);
                        player(mCustomBean.getTimes(), currentPosition, mCustomBean.getLineList(), mCustomBean.getSecondLineList());

                    } else {

                        mCustomBean.setPlayer(false);
                        mCustomBeanList.set(currentPosition, mCustomBean);
                        Log.e("mCustomBean  before", mCustomBean.isPlayer() + "");
                        mCustomBean = mCustomBeanList.get(currentPosition + 1);
                        mCustomBean.setPlayer(true);
                        Log.e("mCustomBean  after", mCustomBean.isPlayer() + "");
                        mDownTimer.cancel();
                        currentPosition += 1;
                        mCustomBeanList.set(currentPosition, mCustomBean);
                        player(mCustomBean.getTimes(), currentPosition, mCustomBean.getLineList(), mCustomBean.getSecondLineList());
                    }
                } else if (currentPlayerMode == randomMode) {
                    mCustomBean.setPlayer(false);
                    Log.e("currentPosition before", currentPosition + "  ");
                    mCustomBeanList.set(currentPosition, mCustomBean);
                    mDownTimer.cancel();
                    for (int i = 0; i < randomList.size(); i++) {
                        if (currentPosition == randomList.get(i)) {
                            if (i == randomList.size() - 1) {
                                currentPosition = randomList.get(0);
                            } else {
                                currentPosition = randomList.get(i + 1);
                            }
                        } else {
                            currentPosition = randomList.get(i + 1);
                        }
                    }
                    Log.e("currentPosition after", currentPosition + "");
                    mCustomBean = mCustomBeanList.get(currentPosition);
                    mCustomBean.setPlayer(true);
                    mCustomBeanList.set(currentPosition, mCustomBean);
                    player(mCustomBean.getTimes(), currentPosition, mCustomBean.getLineList(), mCustomBean.getSecondLineList());
                }
                mPopupWindow.setDataList(mCustomBeanList);
                break;
            case R.id.iv_navigation:
                showPopupWindow();
                break;
        }
    }

    private void showPopupWindow() {
        View parent = LayoutInflater.from(this).inflate(R.layout.activity_custom_mode, null);
        mPopupWindow.showAtLocation(parent, Gravity.BOTTOM | Gravity.LEFT, 0, BottomNavigationBarUtils.getNavigationBarHeight(this));
    }

    @Override
    protected void onDestroy() {

        if (mCustomBean != null && type == 0) {
            mCustomBean.setPlayer(false);
            mCustomBeanList.set(currentPosition, mCustomBean);
            SpUtils.setDataList(Constans.MODE_NAME_KEY, mCustomBeanList);
        }

        if (mDownTimer != null) {
            mDownTimer.cancel();
        }

        isCycleRun = false;
        isPostData = false;
        stopDevice();
        isFirst = true;
        isSecnodFirst = true;
        mTowLineView.clear();
        mTowLineList.clear();
        mLineList.clear();
        mNewLineView.clear();
        mCustomBean = null;

        super.onDestroy();
    }

    private void stopDevice() {

        if (mBleDeviceBeanList != null && mBleDeviceBeanList.size() > 0) {
            try {
                Thread.sleep(70);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            BleManager.getInstance().write(mBleDeviceBeanList.get(0).getBleDevice(), Constans.CUSTOM_SERVICE_UUID, Constans.CUSTOM_WRITE_CHARACTERISTIC_UUID, HexUtil.hexStringToBytes("A20100"), new BleWriteCallback() {
                @Override
                public void onWriteSuccess(int current, int total, byte[] justWrite) {
                    Log.e("onWriteSuccess", justWrite[0] + "");
                }

                @Override
                public void onWriteFailure(BleException exception) {
                }
            });
            if (!isPostData) {
                BleManager.getInstance().write(mBleDeviceBeanList.get(0).getBleDevice(), Constans.CUSTOM_SERVICE_UUID, Constans.CUSTOM_WRITE_CHARACTERISTIC_UUID, HexUtil.hexStringToBytes("A20100"), new BleWriteCallback() {
                    @Override
                    public void onWriteSuccess(int current, int total, byte[] justWrite) {
                        Log.e("!isPostData", justWrite[0] + "");
                    }

                    @Override
                    public void onWriteFailure(BleException exception) {
                    }
                });
            }
        }
        if (mBleDeviceBeanList != null && mBleDeviceBeanList.size() > 1) {
            try {
                Thread.sleep(70);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            BleManager.getInstance().write(mBleDeviceBeanList.get(1).getBleDevice(), Constans.CUSTOM_SERVICE_UUID, Constans.CUSTOM_WRITE_CHARACTERISTIC_UUID, HexUtil.hexStringToBytes("A20100"), new BleWriteCallback() {

                @Override
                public void onWriteSuccess(int current, int total, byte[] justWrite) {
                    Log.e("onWriteSuccess", justWrite[0] + "");
                }

                @Override
                public void onWriteFailure(BleException exception) {

                }
            });

            if (!isPostData) {
                BleManager.getInstance().write(mBleDeviceBeanList.get(1).getBleDevice(), Constans.CUSTOM_SERVICE_UUID, Constans.CUSTOM_WRITE_CHARACTERISTIC_UUID, HexUtil.hexStringToBytes("A20100"), new BleWriteCallback() {

                    @Override
                    public void onWriteSuccess(int current, int total, byte[] justWrite) {
                        Log.e("!isPostData", justWrite[0] + "");
                    }

                    @Override
                    public void onWriteFailure(BleException exception) {

                    }
                });
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopDevice();
    }

    @Override
    public void onMessageEvent(MessageEvent event) {
        super.onMessageEvent(event);
        if (event.getCode() == Constans.VIEW_CYCLE_REFRESH_CODE) {
            mNewLineView.setValue((int) event.getObj());
            mNewLineView.refrshView();
            long i = ((Double) ((int) event.getObj() * 3.6)).longValue();
            if (mBleDeviceBeanList != null && mBleDeviceBeanList.size() > 0) {
                if ("Pulse".equals(mCustomBean.getName()) && fourFirstRunMode) {
                    BleManager.getInstance().write(mBleDeviceBeanList.get(0).getBleDevice(), Constans.CUSTOM_SERVICE_UUID, Constans.CUSTOM_WRITE_CHARACTERISTIC_UUID, HexUtil.hexStringToBytes("A104"), new BleWriteCallback() {
                        @Override
                        public void onWriteSuccess(int current, int total, byte[] justWrite) {
                            fourFirstRunMode = false;
                        }

                        @Override
                        public void onWriteFailure(BleException exception) {
                        }
                    });
                } else if ("Wave".equals(mCustomBean.getName()) && fourFirstRunMode) {
                    BleManager.getInstance().write(mBleDeviceBeanList.get(0).getBleDevice(), Constans.CUSTOM_SERVICE_UUID, Constans.CUSTOM_WRITE_CHARACTERISTIC_UUID, HexUtil.hexStringToBytes("A105"), new BleWriteCallback() {
                        @Override
                        public void onWriteSuccess(int current, int total, byte[] justWrite) {
                            Log.e("Wave", justWrite[0] + "");
                            fourFirstRunMode = false;
                        }

                        @Override
                        public void onWriteFailure(BleException exception) {
                        }
                    });
                } else if ("Fireworks".equals(mCustomBean.getName()) && fourFirstRunMode) {
                    BleManager.getInstance().write(mBleDeviceBeanList.get(0).getBleDevice(), Constans.CUSTOM_SERVICE_UUID, Constans.CUSTOM_WRITE_CHARACTERISTIC_UUID, HexUtil.hexStringToBytes("A106"), new BleWriteCallback() {
                        @Override
                        public void onWriteSuccess(int current, int total, byte[] justWrite) {
                            Log.e("Fireworks", justWrite[0] + "");
                            fourFirstRunMode = false;
                        }

                        @Override
                        public void onWriteFailure(BleException exception) {

                        }
                    });
                } else if ("Earthquake".equals(mCustomBean.getName()) && fourFirstRunMode) {
                    BleManager.getInstance().write(mBleDeviceBeanList.get(0).getBleDevice(), Constans.CUSTOM_SERVICE_UUID, Constans.CUSTOM_WRITE_CHARACTERISTIC_UUID, HexUtil.hexStringToBytes("A106"), new BleWriteCallback() {
                        @Override
                        public void onWriteSuccess(int current, int total, byte[] justWrite) {
                            Log.e("Pluse", justWrite[0] + "");
                            fourFirstRunMode = false;
                        }

                        @Override
                        public void onWriteFailure(BleException exception) {

                        }
                    });
                } else {
                    BleManager.getInstance().write(mBleDeviceBeanList.get(0).getBleDevice(), Constans.CUSTOM_SERVICE_UUID, Constans.CUSTOM_WRITE_CHARACTERISTIC_UUID, HexUtil.hexStringToBytes("A201" + (i == 0 ? "00" : Long.parseLong(String.valueOf(i), 16))), new BleWriteCallback() {
                        @Override
                        public void onWriteSuccess(int current, int total, byte[] justWrite) {

                        }

                        @Override
                        public void onWriteFailure(BleException exception) {

                        }
                    });
                }
            }

        } else if (event.getCode() == Constans.SECOND_VIEW_CYCLE_REFRESH_CODE) {
            mTowLineView.setValue((Integer) event.getObj());
            mTowLineView.refrshView();
            long hexValue = ((Double) ((int) event.getObj() * 3.6)).longValue();
            Log.e("hexValue", hexValue + "");
            if (mBleDeviceBeanList != null && mBleDeviceBeanList.size() > 1) {

                BleManager.getInstance().write(mBleDeviceBeanList.get(1).getBleDevice(), Constans.CUSTOM_SERVICE_UUID, Constans.CUSTOM_WRITE_CHARACTERISTIC_UUID, HexUtil.hexStringToBytes("A201" + (hexValue == 0 ? "00" : Long.parseLong(String.valueOf(hexValue), 16))), new BleWriteCallback() {
                    @Override
                    public void onWriteSuccess(int current, int total, byte[] justWrite) {
                        Log.e("onWriteSuccess", justWrite[0] + "");
                    }

                    @Override
                    public void onWriteFailure(BleException exception) {

                    }
                });
            }

        } else if (event.getCode() == Constans.BLUE_BATTERY_ELECTRICITY) {
            byte[] data = (byte[]) event.getObj();
            changImg(data[0], mIvBattery);
        } else if (event.getCode() == Constans.BLUE_BATTERY_ELECTRICITY_TWO) {
            byte[] data = (byte[]) event.getObj();
            changImg(data[0], mIvBatteryTwo);
        }
    }

    private void changImg(byte data, ImageView imageView) {
        int electricity = data;
        if (80 <= electricity && electricity <= 100) {
            imageView.setImageResource(R.mipmap.icon_blue_battery_100);
        } else if (60 <= electricity && electricity < 80) {
            imageView.setImageResource(R.mipmap.icon_blue_battery_80);
        } else if (40 <= electricity && electricity < 60) {
            imageView.setImageResource(R.mipmap.icon_blue_battery_60);
        } else if (20 <= electricity && electricity < 40) {
            imageView.setImageResource(R.mipmap.icon_blue_battery_40);
        } else if (0 <= electricity && electricity < 20) {
            imageView.setImageResource(R.mipmap.icon_blue_battery_20);
        }
    }

    /**
     * 生成不重复的随机数
     *
     * @param min
     * @param max
     * @param n
     * @param set
     */
    public static void randomSet(int min, int max, int n, HashSet<Integer> set) {
        if (n > (max - min + 1) || max < min) {
            return;
        }
        for (int i = 0; i < n; i++) {
            int num = (int) (Math.random() * (max - min)) + min;
            set.add(num);
        }
        int setSize = set.size();
        if (setSize < n) {
            randomSet(min, max, n - setSize, set);
        }
    }

}
