package com.yscoco.siminghua.ui.activity.realtimeoperation;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.utils.HexUtil;
import com.jaygoo.widget.OnRangeChangedListener;
import com.jaygoo.widget.RangeSeekBar;
import com.jaygoo.widget.VerticalRangeSeekBar;
import com.ys.module.title.TitleBar;
import com.yscoco.siminghua.Constans;
import com.yscoco.siminghua.R;
import com.yscoco.siminghua.base.activity.BaseActivity;
import com.yscoco.siminghua.domain.BleDeviceBean;
import com.yscoco.siminghua.domain.MessageEvent;
import com.yscoco.siminghua.utils.DensityUtil;
import com.yscoco.siminghua.utils.ResourcesUtils;
import com.yscoco.siminghua.utils.SpUtils;
import com.yscoco.siminghua.utils.ThreadUtils;
import com.yscoco.siminghua.view.NewLineView;
import com.yscoco.siminghua.view.TouchImageView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by ZhangZeZhi on 2018\10\24 0024.
 */

public class RealTimeOperationActivity extends BaseActivity {

    private static final String TAG = RealTimeOperationActivity.class.getSimpleName();

    @BindView(R.id.tb_title)
    TitleBar mTitleBar;

    @BindView(R.id.view_real_time)
    View mViewRealTime;

    @BindView(R.id.view_cycle)
    View mViewCycle;

    @BindView(R.id.view_traditional)
    View mViewTraditional;

    @BindView(R.id.tv_real_time)
    TextView mTvRealTime;

    @BindView(R.id.tv_cycle)
    TextView mTvCycle;

    @BindView(R.id.tv_traditional)
    TextView mTvTraditional;

    @BindView(R.id.tv_one)
    TextView mTvOne;

    @BindView(R.id.tv_two)
    TextView mTvTwo;

    @BindView(R.id.tv_three)
    TextView mTvThree;

    @BindView(R.id.tv_four)
    TextView mTvFour;

    @BindView(R.id.tv_five)
    TextView mTvFive;

    @BindView(R.id.tv_six)
    TextView mTvSix;

    @BindView(R.id.tv_seven)
    TextView mTvSeven;

    @BindView(R.id.ll_tab)
    LinearLayout mLlTab;

    @BindView(R.id.tv_vibration_mode)
    TextView mTvVibrationMode;

    @BindView(R.id.new_line_view)
    NewLineView mNewLineView;

    @BindView(R.id.iv_touch_image)
    TouchImageView mIvTouchView;

    @BindView(R.id.new_line_view_two)
    NewLineView mNewLineViewTwo;

    @BindView(R.id.iv_touch_image_two)
    TouchImageView mIvTouchViewTwo;

    @BindView(R.id.iv_battery)
    ImageView mIvBattery;

    @BindView(R.id.iv_battery_two)
    ImageView mIvBatteryTwo;


    @BindView(R.id.vr_sb)
    VerticalRangeSeekBar mSeekBar;

    @BindView(R.id.vr_sb_two)
    VerticalRangeSeekBar mSeekBarTwo;

    private boolean isRealRun = false;

    private boolean isRun = true;

    private boolean isCycle = false;

    private boolean isCycleRun = false;

    private boolean isTwoCycle = false;

    private boolean isTwoCycleRun = false;

    private boolean isPostData = true;

    private int viewValue = 0;

    private int twoViewValue = 0;

    private long sleepTime = 20;

    private boolean isTraditional = false;

    private List<Integer> mCycleList = new ArrayList<>();

    private List<Integer> mCycleTwoList = new ArrayList<>();

    private boolean isSlideRun = false;

    private boolean isFirstWrite = true;

    private boolean isFirstTwoWrite = true;

    private boolean isWriteSuccess = false;

    private Runnable slideRunnable = new Runnable() {
        @Override
        public void run() {
            MessageEvent event = new MessageEvent();
            while (isRun) {
                event.setCode(Constans.REAL_TIME_VIEW_REFRESH_CODE);
                event.setObj(((Double) (viewValue / 4.4)).intValue());
                EventBus.getDefault().post(event);
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private Runnable slideTwoRunnable = new Runnable() {
        @Override
        public void run() {
            MessageEvent event = new MessageEvent();
            while (isRun) {
                event.setCode(Constans.REAL_TIME_VIEW_REFRESH_TWO_CODE);
                event.setObj(twoViewValue);
                EventBus.getDefault().post(event);
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private Runnable mCycleRunnable = new Runnable() {
        @Override
        public void run() {
            MessageEvent event = new MessageEvent();
            while (isCycleRun) {
                for (int i = 0; i < mCycleList.size(); i++) {
                    event.setCode(Constans.REAL_TIME_VIEW_CYCLE_REFRESH_CODE);
                    event.setObj(mCycleList.get(i));
                    if (isPostData) {
                        EventBus.getDefault().post(event);
                    }
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private Runnable mCycleTwoRunnable = new Runnable() {
        @Override
        public void run() {
            MessageEvent event = new MessageEvent();
            while (isTwoCycleRun) {
                for (int i = 0; i < mCycleTwoList.size(); i++) {
                    event.setCode(Constans.REAL_TIME_VIEW_CYCLE_REFRESH_TWO_CODE);
                    event.setObj(mCycleTwoList.get(i));
                    if (isPostData) {
                        EventBus.getDefault().post(event);
                    }
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private BleDeviceBean mBleDeviceBean;
    private boolean mConnected;
    private byte[] mBytes;
    private List<BleDeviceBean> mDeviceBeanList;
    private boolean mTwoConnected;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_real_time_operation;
    }

    @Override
    protected void init() {

        transparentStatusBar(R.color.transparent);
        mTitleBar.setTitle("实时操控");
        mTitleBar.gone();
        mDeviceBeanList = SpUtils.getDataList(Constans.BLUETOOTH_DEVICE_KEYS, BleDeviceBean.class);
        if (mDeviceBeanList != null && mDeviceBeanList.size() > 0) {
            mConnected = BleManager.getInstance().isConnected(mDeviceBeanList.get(0).getBleDevice());
            if (mConnected) {
                changImg((byte) mDeviceBeanList.get(0).getElectricity(), mIvBattery);
            }
            if (mDeviceBeanList.size() > 1 && BleManager.getInstance().isConnected(mDeviceBeanList.get(1).getBleDevice())) {
                mIvTouchViewTwo.setVisibility(View.VISIBLE);
                mNewLineViewTwo.setVisibility(View.VISIBLE);
                changImg((byte) mDeviceBeanList.get(0).getElectricity(), mIvBatteryTwo);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(this, 95));
                mNewLineView.setLayoutParams(params);
                mTwoConnected = BleManager.getInstance().isConnected(mDeviceBeanList.get(1).getBleDevice());
                ThreadUtils.getThreadPool().execute(slideTwoRunnable);
            } else {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(this, 225));
                mNewLineView.setLayoutParams(params);
            }
        }

        ThreadUtils.getThreadPool().execute(slideRunnable);
        mIvTouchView.setOnMoveYListener(new TouchImageView.OnMoveYListener() {
            @Override
            public void onMoveY(int y) {
                if (isTraditional) {
                    return;
                }

                viewValue = (int) (y * 4.5);
                mNewLineView.setValue(y);
                mNewLineView.refrshView();
                long strength = Long.parseLong(String.valueOf(viewValue), 16);
                byte[] viewBytes = null;
                if (viewValue == 0) {
                    viewBytes = HexUtil.hexStringToBytes("A20100");
                } else {
                    viewBytes = HexUtil.hexStringToBytes("A201" + String.valueOf(strength));
                }

                if (mConnected) {
                    BleManager.getInstance().write(mDeviceBeanList.get(0).getBleDevice(), Constans.CUSTOM_SERVICE_UUID, Constans.CUSTOM_WRITE_CHARACTERISTIC_UUID, viewBytes, new BleWriteCallback() {
                        @Override
                        public void onWriteSuccess(int current, int total, byte[] justWrite) {
                            Log.e("onWriteSuccess", current + "   " + total);
                            isFirstWrite = false;
                        }

                        @Override
                        public void onWriteFailure(BleException exception) {
                            Log.e("onWriteFailure", exception.toString());
                        }
                    });

                }

                if (isCycle) {
                    isCycleRun = false;
                    mCycleList.add(y);
                }
            }

            @Override
            public void onUp() {
                if (isCycle) {
                    isCycleRun = true;
                    ThreadUtils.getThreadPool().execute(mCycleRunnable);
                }
            }

            @Override
            public void onDown() {
                if (isCycle) {
                    mNewLineView.clear();
                    isCycleRun = false;
                    mCycleList.clear();
                }
            }

        });

        mIvTouchViewTwo.setOnMoveYListener(new TouchImageView.OnMoveYListener() {
            @Override
            public void onMoveY(int y) {
                if (isTraditional) {
                    return;
                }
                twoViewValue = (int) (y * 4.5);
                mNewLineViewTwo.setValue(y);
                mNewLineViewTwo.refrshView();

                long strength = Long.parseLong(String.valueOf(viewValue), 16);

                byte[] viewBytes = null;
                if (viewValue == 0) {
                    viewBytes = HexUtil.hexStringToBytes("A20100");
                } else {
                    viewBytes = HexUtil.hexStringToBytes("A201" + String.valueOf(strength));
                }

                if (BleManager.getInstance().isConnected(mDeviceBeanList.get(1).getBleDevice())) {

                    BleManager.getInstance().write(mDeviceBeanList.get(1).getBleDevice(), Constans.CUSTOM_SERVICE_UUID, Constans.CUSTOM_WRITE_CHARACTERISTIC_UUID, viewBytes, new BleWriteCallback() {
                        @Override
                        public void onWriteSuccess(int current, int total, byte[] justWrite) {
                            Log.e("onWriteSuccess", current + "   " + total);
                            isFirstTwoWrite = false;
                        }

                        @Override
                        public void onWriteFailure(BleException exception) {
                            Log.e("onWriteFailure", exception.toString());
                        }
                    });

                }

                if (isTwoCycle) {
                    isTwoCycleRun = false;
                    mCycleTwoList.add(y);
                }
            }

            @Override
            public void onUp() {
                if (isTwoCycle) {
                    isTwoCycleRun = true;
                    ThreadUtils.getThreadPool().execute(mCycleTwoRunnable);
                }
            }

            @Override
            public void onDown() {
                if (isTwoCycle && mDeviceBeanList.size() > 1) {
                    mNewLineViewTwo.clear();
                    isTwoCycleRun = false;
                    mCycleTwoList.clear();
                }
            }
        });

        mSeekBar.setOnRangeChangedListener(new OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
                if (mDeviceBeanList != null && mDeviceBeanList.size() > 0) {
                    if (leftValue == 0) {
                        writeDevice(HexUtil.hexStringToBytes("A20100"));
                    } else {
                        writeDevice(HexUtil.hexStringToBytes("A201" + Long.parseLong(String.valueOf((int) leftValue), 16)));
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }
        });

        mSeekBarTwo.setOnRangeChangedListener(new OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
                if (mDeviceBeanList != null && mDeviceBeanList.size() > 1) {
                    if (leftValue == 0) {
                        writeDeviceTwo(HexUtil.hexStringToBytes("A20100"));
                    } else {
                        writeDeviceTwo(HexUtil.hexStringToBytes("A201" + Long.parseLong(String.valueOf((int) leftValue), 16)));
                    }

                }
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }
        });

    }

    private void writeDeviceTwo(byte[] hexValue) {
        BleManager.getInstance().write(mDeviceBeanList.get(1).getBleDevice(), Constans.CUSTOM_SERVICE_UUID, Constans.CUSTOM_WRITE_CHARACTERISTIC_UUID, hexValue, new BleWriteCallback() {
            @Override
            public void onWriteSuccess(int current, int total, byte[] justWrite) {
                Log.e("onWriteSuccess Two", justWrite[0] + "");
            }

            @Override
            public void onWriteFailure(BleException exception) {

            }
        });
    }

    private void writeDevice(byte[] hexValue) {
        BleManager.getInstance().write(mDeviceBeanList.get(0).getBleDevice(), Constans.CUSTOM_SERVICE_UUID, Constans.CUSTOM_WRITE_CHARACTERISTIC_UUID, hexValue, new BleWriteCallback() {
            @Override
            public void onWriteSuccess(int current, int total, byte[] justWrite) {
                Log.e("onWriteSuccess", justWrite[0] + "");
            }

            @Override
            public void onWriteFailure(BleException exception) {

            }
        });
    }

    @OnClick({R.id.ll_real_time, R.id.ll_cycle
            , R.id.ll_traditional, R.id.iv_left_arrow
            , R.id.tv_one, R.id.tv_two, R.id.tv_three
            , R.id.tv_four, R.id.tv_five, R.id.tv_six
            , R.id.tv_seven})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.ll_real_time:
                linearRealTime();
                break;
            case R.id.ll_cycle:
                linearCycle();
                break;
            case R.id.ll_traditional:
                linearTraditional();
                break;
            case R.id.iv_left_arrow:
                finish();
                break;

            case R.id.tv_one:
                mTvOne.setTextColor(ResourcesUtils.getColor(R.color.white_255));
                mTvOne.setBackground(ResourcesUtils.getDrawable(R.drawable.shape_real_time_operation_tab_select_bg));
                mTvTwo.setTextColor(ResourcesUtils.getColor(R.color.item_left_color));
                mTvTwo.setBackground(ResourcesUtils.getDrawable(R.drawable.shape_real_time_operation_tab_unselect_bg));
                mTvThree.setTextColor(ResourcesUtils.getColor(R.color.item_left_color));
                mTvThree.setBackground(ResourcesUtils.getDrawable(R.drawable.shape_real_time_operation_tab_unselect_bg));
                mTvFour.setTextColor(ResourcesUtils.getColor(R.color.item_left_color));
                mTvFour.setBackground(ResourcesUtils.getDrawable(R.drawable.shape_real_time_operation_tab_unselect_bg));
                mTvFive.setTextColor(ResourcesUtils.getColor(R.color.item_left_color));
                mTvFive.setBackground(ResourcesUtils.getDrawable(R.drawable.shape_real_time_operation_tab_unselect_bg));
                mTvSix.setTextColor(ResourcesUtils.getColor(R.color.item_left_color));
                mTvSix.setBackground(ResourcesUtils.getDrawable(R.drawable.shape_real_time_operation_tab_unselect_bg));
                mTvSeven.setTextColor(ResourcesUtils.getColor(R.color.item_left_color));
                mTvSeven.setBackground(ResourcesUtils.getDrawable(R.drawable.shape_real_time_operation_tab_unselect_bg));

                switchMode("A101");

                break;
            case R.id.tv_two:
                mTvOne.setTextColor(ResourcesUtils.getColor(R.color.item_left_color));
                mTvOne.setBackground(ResourcesUtils.getDrawable(R.drawable.shape_real_time_operation_tab_unselect_bg));
                mTvTwo.setTextColor(ResourcesUtils.getColor(R.color.white_255));
                mTvTwo.setBackground(ResourcesUtils.getDrawable(R.drawable.shape_real_time_operation_tab_select_bg));
                mTvThree.setTextColor(ResourcesUtils.getColor(R.color.item_left_color));
                mTvThree.setBackground(ResourcesUtils.getDrawable(R.drawable.shape_real_time_operation_tab_unselect_bg));
                mTvFour.setTextColor(ResourcesUtils.getColor(R.color.item_left_color));
                mTvFour.setBackground(ResourcesUtils.getDrawable(R.drawable.shape_real_time_operation_tab_unselect_bg));
                mTvFive.setTextColor(ResourcesUtils.getColor(R.color.item_left_color));
                mTvFive.setBackground(ResourcesUtils.getDrawable(R.drawable.shape_real_time_operation_tab_unselect_bg));
                mTvSix.setTextColor(ResourcesUtils.getColor(R.color.item_left_color));
                mTvSix.setBackground(ResourcesUtils.getDrawable(R.drawable.shape_real_time_operation_tab_unselect_bg));
                mTvSeven.setTextColor(ResourcesUtils.getColor(R.color.item_left_color));
                mTvSeven.setBackground(ResourcesUtils.getDrawable(R.drawable.shape_real_time_operation_tab_unselect_bg));

                switchMode("A102");

                break;
            case R.id.tv_three:
                mTvOne.setTextColor(ResourcesUtils.getColor(R.color.item_left_color));
                mTvOne.setBackground(ResourcesUtils.getDrawable(R.drawable.shape_real_time_operation_tab_unselect_bg));
                mTvTwo.setTextColor(ResourcesUtils.getColor(R.color.item_left_color));
                mTvTwo.setBackground(ResourcesUtils.getDrawable(R.drawable.shape_real_time_operation_tab_unselect_bg));
                mTvThree.setTextColor(ResourcesUtils.getColor(R.color.white_255));
                mTvThree.setBackground(ResourcesUtils.getDrawable(R.drawable.shape_real_time_operation_tab_select_bg));
                mTvFour.setTextColor(ResourcesUtils.getColor(R.color.item_left_color));
                mTvFour.setBackground(ResourcesUtils.getDrawable(R.drawable.shape_real_time_operation_tab_unselect_bg));
                mTvFive.setTextColor(ResourcesUtils.getColor(R.color.item_left_color));
                mTvFive.setBackground(ResourcesUtils.getDrawable(R.drawable.shape_real_time_operation_tab_unselect_bg));
                mTvSix.setTextColor(ResourcesUtils.getColor(R.color.item_left_color));
                mTvSix.setBackground(ResourcesUtils.getDrawable(R.drawable.shape_real_time_operation_tab_unselect_bg));
                mTvSeven.setTextColor(ResourcesUtils.getColor(R.color.item_left_color));
                mTvSeven.setBackground(ResourcesUtils.getDrawable(R.drawable.shape_real_time_operation_tab_unselect_bg));

                switchMode("A103");

                break;

            case R.id.tv_four:
                mTvOne.setTextColor(ResourcesUtils.getColor(R.color.item_left_color));
                mTvOne.setBackground(ResourcesUtils.getDrawable(R.drawable.shape_real_time_operation_tab_unselect_bg));
                mTvTwo.setTextColor(ResourcesUtils.getColor(R.color.item_left_color));
                mTvTwo.setBackground(ResourcesUtils.getDrawable(R.drawable.shape_real_time_operation_tab_unselect_bg));
                mTvThree.setTextColor(ResourcesUtils.getColor(R.color.item_left_color));
                mTvThree.setBackground(ResourcesUtils.getDrawable(R.drawable.shape_real_time_operation_tab_unselect_bg));
                mTvFour.setTextColor(ResourcesUtils.getColor(R.color.white_255));
                mTvFour.setBackground(ResourcesUtils.getDrawable(R.drawable.shape_real_time_operation_tab_select_bg));
                mTvFive.setTextColor(ResourcesUtils.getColor(R.color.item_left_color));
                mTvFive.setBackground(ResourcesUtils.getDrawable(R.drawable.shape_real_time_operation_tab_unselect_bg));
                mTvSix.setTextColor(ResourcesUtils.getColor(R.color.item_left_color));
                mTvSix.setBackground(ResourcesUtils.getDrawable(R.drawable.shape_real_time_operation_tab_unselect_bg));
                mTvSeven.setTextColor(ResourcesUtils.getColor(R.color.item_left_color));
                mTvSeven.setBackground(ResourcesUtils.getDrawable(R.drawable.shape_real_time_operation_tab_unselect_bg));

                switchMode("A104");

                break;

            case R.id.tv_five:
                mTvOne.setTextColor(ResourcesUtils.getColor(R.color.item_left_color));
                mTvOne.setBackground(ResourcesUtils.getDrawable(R.drawable.shape_real_time_operation_tab_unselect_bg));
                mTvTwo.setTextColor(ResourcesUtils.getColor(R.color.item_left_color));
                mTvTwo.setBackground(ResourcesUtils.getDrawable(R.drawable.shape_real_time_operation_tab_unselect_bg));
                mTvThree.setTextColor(ResourcesUtils.getColor(R.color.item_left_color));
                mTvThree.setBackground(ResourcesUtils.getDrawable(R.drawable.shape_real_time_operation_tab_unselect_bg));
                mTvFour.setTextColor(ResourcesUtils.getColor(R.color.item_left_color));
                mTvFour.setBackground(ResourcesUtils.getDrawable(R.drawable.shape_real_time_operation_tab_unselect_bg));
                mTvFive.setTextColor(ResourcesUtils.getColor(R.color.white_255));
                mTvFive.setBackground(ResourcesUtils.getDrawable(R.drawable.shape_real_time_operation_tab_select_bg));
                mTvSix.setTextColor(ResourcesUtils.getColor(R.color.item_left_color));
                mTvSix.setBackground(ResourcesUtils.getDrawable(R.drawable.shape_real_time_operation_tab_unselect_bg));
                mTvSeven.setTextColor(ResourcesUtils.getColor(R.color.item_left_color));
                mTvSeven.setBackground(ResourcesUtils.getDrawable(R.drawable.shape_real_time_operation_tab_unselect_bg));

                switchMode("A105");

                break;

            case R.id.tv_six:
                mTvOne.setTextColor(ResourcesUtils.getColor(R.color.item_left_color));
                mTvOne.setBackground(ResourcesUtils.getDrawable(R.drawable.shape_real_time_operation_tab_unselect_bg));
                mTvTwo.setTextColor(ResourcesUtils.getColor(R.color.item_left_color));
                mTvTwo.setBackground(ResourcesUtils.getDrawable(R.drawable.shape_real_time_operation_tab_unselect_bg));
                mTvThree.setTextColor(ResourcesUtils.getColor(R.color.item_left_color));
                mTvThree.setBackground(ResourcesUtils.getDrawable(R.drawable.shape_real_time_operation_tab_unselect_bg));
                mTvFour.setTextColor(ResourcesUtils.getColor(R.color.item_left_color));
                mTvFour.setBackground(ResourcesUtils.getDrawable(R.drawable.shape_real_time_operation_tab_unselect_bg));
                mTvFive.setTextColor(ResourcesUtils.getColor(R.color.item_left_color));
                mTvFive.setBackground(ResourcesUtils.getDrawable(R.drawable.shape_real_time_operation_tab_unselect_bg));
                mTvSix.setTextColor(ResourcesUtils.getColor(R.color.white_255));
                mTvSix.setBackground(ResourcesUtils.getDrawable(R.drawable.shape_real_time_operation_tab_select_bg));
                mTvSeven.setTextColor(ResourcesUtils.getColor(R.color.item_left_color));
                mTvSeven.setBackground(ResourcesUtils.getDrawable(R.drawable.shape_real_time_operation_tab_unselect_bg));

                switchMode("A106");

                break;

            case R.id.tv_seven:
                mTvOne.setTextColor(ResourcesUtils.getColor(R.color.item_left_color));
                mTvOne.setBackground(ResourcesUtils.getDrawable(R.drawable.shape_real_time_operation_tab_unselect_bg));
                mTvTwo.setTextColor(ResourcesUtils.getColor(R.color.item_left_color));
                mTvTwo.setBackground(ResourcesUtils.getDrawable(R.drawable.shape_real_time_operation_tab_unselect_bg));
                mTvThree.setTextColor(ResourcesUtils.getColor(R.color.item_left_color));
                mTvThree.setBackground(ResourcesUtils.getDrawable(R.drawable.shape_real_time_operation_tab_unselect_bg));
                mTvFour.setTextColor(ResourcesUtils.getColor(R.color.item_left_color));
                mTvFour.setBackground(ResourcesUtils.getDrawable(R.drawable.shape_real_time_operation_tab_unselect_bg));
                mTvFive.setTextColor(ResourcesUtils.getColor(R.color.item_left_color));
                mTvFive.setBackground(ResourcesUtils.getDrawable(R.drawable.shape_real_time_operation_tab_unselect_bg));
                mTvSix.setTextColor(ResourcesUtils.getColor(R.color.item_left_color));
                mTvSix.setBackground(ResourcesUtils.getDrawable(R.drawable.shape_real_time_operation_tab_unselect_bg));
                mTvSeven.setTextColor(ResourcesUtils.getColor(R.color.white_255));
                mTvSeven.setBackground(ResourcesUtils.getDrawable(R.drawable.shape_real_time_operation_tab_select_bg));

                switchMode("A107");

                break;
        }
    }

    private void switchMode(String value) {

        if (mDeviceBeanList != null && mDeviceBeanList.size() > 0) {

            BleManager.getInstance().write(mDeviceBeanList.get(0).getBleDevice(), Constans.CUSTOM_SERVICE_UUID, Constans.CUSTOM_WRITE_CHARACTERISTIC_UUID, HexUtil.hexStringToBytes(value), new BleWriteCallback() {
                @Override
                public void onWriteSuccess(int current, int total, byte[] justWrite) {
                    Log.e("onWriteSuccess", justWrite[0] + "");
                }

                @Override
                public void onWriteFailure(BleException exception) {

                }
            });

        }

        if (mDeviceBeanList != null && mDeviceBeanList.size() > 1) {

            BleManager.getInstance().write(mDeviceBeanList.get(1).getBleDevice(), Constans.CUSTOM_SERVICE_UUID, Constans.CUSTOM_WRITE_CHARACTERISTIC_UUID, HexUtil.hexStringToBytes(value), new BleWriteCallback() {
                @Override
                public void onWriteSuccess(int current, int total, byte[] justWrite) {
                    Log.e("onWriteSucccess", justWrite[0] + "");
                }

                @Override
                public void onWriteFailure(BleException exception) {

                }

            });
        }
    }

    private void linearTraditional() {

        mViewTraditional.setVisibility(View.VISIBLE);
        mViewCycle.setVisibility(View.GONE);
        mViewRealTime.setVisibility(View.GONE);
        mTvCycle.setTextColor(ResourcesUtils.getColor(R.color.item_left_color));
        mTvRealTime.setTextColor(ResourcesUtils.getColor(R.color.item_left_color));
        mTvTraditional.setTextColor(ResourcesUtils.getColor(R.color.main_color));

        mTvVibrationMode.setVisibility(View.VISIBLE);
        mLlTab.setVisibility(View.VISIBLE);
        isRun = false;
        isCycle = false;
        isCycleRun = false;
        isTwoCycle = false;
        isTwoCycleRun = false;
        isPostData = false;
        isTraditional = true;
        isSlideRun = false;

        mNewLineView.clear();
        mNewLineView.setVisibility(View.GONE);
        mIvTouchView.setVisibility(View.GONE);
        mSeekBar.setVisibility(View.VISIBLE);
        mIvTouchViewTwo.setVisibility(View.GONE);
        mCycleList.clear();
        mCycleTwoList.clear();

        if (mDeviceBeanList != null && mDeviceBeanList.size() > 0) {

            Log.e("linearTraditional", "linearTraditional1");

            BleManager.getInstance().write(mDeviceBeanList.get(0).getBleDevice(), Constans.CUSTOM_SERVICE_UUID, Constans.CUSTOM_WRITE_CHARACTERISTIC_UUID, HexUtil.hexStringToBytes("A20100"), new BleWriteCallback() {
                @Override
                public void onWriteSuccess(int current, int total, byte[] justWrite) {
                    Log.e("linearTraditional", justWrite[0] + "     linearTraditional1");
                }

                @Override
                public void onWriteFailure(BleException exception) {
                    Log.e("onWriteFailure", exception.toString());
                }
            });

            if (!isPostData) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                BleManager.getInstance().write(mDeviceBeanList.get(0).getBleDevice(), Constans.CUSTOM_SERVICE_UUID, Constans.CUSTOM_WRITE_CHARACTERISTIC_UUID, HexUtil.hexStringToBytes("A20100"), new BleWriteCallback() {
                    @Override
                    public void onWriteSuccess(int current, int total, byte[] justWrite) {
                        Log.e("onWriteSuccess", justWrite[0] + "");
                    }

                    @Override
                    public void onWriteFailure(BleException exception) {

                    }
                });
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                BleManager.getInstance().write(mDeviceBeanList.get(0).getBleDevice(), Constans.CUSTOM_SERVICE_UUID, Constans.CUSTOM_WRITE_CHARACTERISTIC_UUID, HexUtil.hexStringToBytes("A20100"), new BleWriteCallback() {
                    @Override
                    public void onWriteSuccess(int current, int total, byte[] justWrite) {
                        Log.e("onWriteSuccess", justWrite[0] + "");
                    }

                    @Override
                    public void onWriteFailure(BleException exception) {

                    }
                });
            }
        }

        if (mDeviceBeanList != null && mDeviceBeanList.size() > 1) {

            Log.e("linearTraditional", "linearTraditional2");

            mSeekBarTwo.setVisibility(View.VISIBLE);
            mNewLineViewTwo.setVisibility(View.GONE);
            BleManager.getInstance().write(mDeviceBeanList.get(1).getBleDevice(), Constans.CUSTOM_SERVICE_UUID, Constans.CUSTOM_WRITE_CHARACTERISTIC_UUID, HexUtil.hexStringToBytes("A20100"), new BleWriteCallback() {
                @Override
                public void onWriteSuccess(int current, int total, byte[] justWrite) {
                    Log.e("onWriteSuccess", justWrite[0] + "");
                }

                @Override
                public void onWriteFailure(BleException exception) {

                }

            });

            if (!isPostData) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                BleManager.getInstance().write(mDeviceBeanList.get(0).getBleDevice(), Constans.CUSTOM_SERVICE_UUID, Constans.CUSTOM_WRITE_CHARACTERISTIC_UUID, HexUtil.hexStringToBytes("A20100"), new BleWriteCallback() {
                    @Override
                    public void onWriteSuccess(int current, int total, byte[] justWrite) {
                        Log.e("onWriteSuccess", justWrite[0] + "");
                    }

                    @Override
                    public void onWriteFailure(BleException exception) {

                    }
                });
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                BleManager.getInstance().write(mDeviceBeanList.get(0).getBleDevice(), Constans.CUSTOM_SERVICE_UUID, Constans.CUSTOM_WRITE_CHARACTERISTIC_UUID, HexUtil.hexStringToBytes("A20100"), new BleWriteCallback() {
                    @Override
                    public void onWriteSuccess(int current, int total, byte[] justWrite) {
                        Log.e("onWriteSuccess", justWrite[0] + "");
                    }

                    @Override
                    public void onWriteFailure(BleException exception) {

                    }
                });
            }
        }
    }

    private void linearCycle() {

        mViewRealTime.setVisibility(View.GONE);
        mViewCycle.setVisibility(View.VISIBLE);
        mViewTraditional.setVisibility(View.GONE);
        mTvRealTime.setTextColor(ResourcesUtils.getColor(R.color.item_left_color));
        mTvTraditional.setTextColor(ResourcesUtils.getColor(R.color.item_left_color));
        mTvCycle.setTextColor(ResourcesUtils.getColor(R.color.main_color));

        mLlTab.setVisibility(View.GONE);
        mTvVibrationMode.setVisibility(View.GONE);
        mNewLineView.clear();
        isTraditional = false;
        isRun = false;
        isCycle = true;
        isCycleRun = true;
        isTwoCycle = true;
        isTwoCycleRun = true;
        isPostData = true;
        isSlideRun = false;
        mNewLineView.setVisibility(View.VISIBLE);
        mIvTouchView.setVisibility(View.VISIBLE);
        mSeekBar.setVisibility(View.GONE);

        if (mDeviceBeanList != null && mDeviceBeanList.size() > 0) {

            BleManager.getInstance().write(mDeviceBeanList.get(0).getBleDevice(), Constans.CUSTOM_SERVICE_UUID, Constans.CUSTOM_WRITE_CHARACTERISTIC_UUID, HexUtil.hexStringToBytes("A20100"), new BleWriteCallback() {
                @Override
                public void onWriteSuccess(int current, int total, byte[] justWrite) {
                    Log.e("onWriteSuccess", justWrite[0] + "");
                }

                @Override
                public void onWriteFailure(BleException exception) {

                }
            });

        }

        if (mDeviceBeanList != null && mDeviceBeanList.size() > 1) {

            mIvTouchViewTwo.setVisibility(View.VISIBLE);
            mSeekBarTwo.setVisibility(View.GONE);
            mNewLineViewTwo.clear();
            mNewLineViewTwo.setVisibility(View.VISIBLE);

            BleManager.getInstance().write(mDeviceBeanList.get(1).getBleDevice(), Constans.CUSTOM_SERVICE_UUID, Constans.CUSTOM_WRITE_CHARACTERISTIC_UUID, HexUtil.hexStringToBytes("A20100"), new BleWriteCallback() {
                @Override
                public void onWriteSuccess(int current, int total, byte[] justWrite) {
                    Log.e("onWriteSuccess", justWrite[0] + "");
                }

                @Override
                public void onWriteFailure(BleException exception) {

                }
            });
        }

    }

    private void linearRealTime() {
        mViewRealTime.setVisibility(View.VISIBLE);
        mViewCycle.setVisibility(View.GONE);
        mViewTraditional.setVisibility(View.GONE);
        mTvRealTime.setTextColor(ResourcesUtils.getColor(R.color.main_color));
        mTvCycle.setTextColor(ResourcesUtils.getColor(R.color.item_left_color));
        mTvTraditional.setTextColor(ResourcesUtils.getColor(R.color.item_left_color));

        mLlTab.setVisibility(View.GONE);
        mTvVibrationMode.setVisibility(View.GONE);

        viewValue = 1;
        twoViewValue = 1;
        isRun = true;
        isCycle = false;
        isCycleRun = false;
        isTwoCycle = false;
        isTwoCycleRun = false;
        isPostData = false;
        isTraditional = false;
        mNewLineView.clear();
        mNewLineView.setVisibility(View.VISIBLE);
        mIvTouchView.setVisibility(View.VISIBLE);
        mSeekBar.setVisibility(View.GONE);
        mCycleTwoList.clear();
        mCycleList.clear();

        if (mDeviceBeanList != null && mDeviceBeanList.size() > 0) {

            BleManager.getInstance().write(mDeviceBeanList.get(0).getBleDevice(), Constans.CUSTOM_SERVICE_UUID, Constans.CUSTOM_WRITE_CHARACTERISTIC_UUID, HexUtil.hexStringToBytes("A20100"), new BleWriteCallback() {
                @Override
                public void onWriteSuccess(int current, int total, byte[] justWrite) {
                    Log.e("onWriteSuccess", justWrite[0] + "");
                }

                @Override
                public void onWriteFailure(BleException exception) {

                }
            });

            if (!isPostData) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                BleManager.getInstance().write(mDeviceBeanList.get(0).getBleDevice(), Constans.CUSTOM_SERVICE_UUID, Constans.CUSTOM_WRITE_CHARACTERISTIC_UUID, HexUtil.hexStringToBytes("A20100"), new BleWriteCallback() {
                    @Override
                    public void onWriteSuccess(int current, int total, byte[] justWrite) {
                        Log.e("onWriteSuccess", justWrite[0] + "");
                    }

                    @Override
                    public void onWriteFailure(BleException exception) {

                    }
                });

                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                BleManager.getInstance().write(mDeviceBeanList.get(0).getBleDevice(), Constans.CUSTOM_SERVICE_UUID, Constans.CUSTOM_WRITE_CHARACTERISTIC_UUID, HexUtil.hexStringToBytes("A20100"), new BleWriteCallback() {
                    @Override
                    public void onWriteSuccess(int current, int total, byte[] justWrite) {
                        Log.e("onWriteSuccess", justWrite[0] + "");
                    }

                    @Override
                    public void onWriteFailure(BleException exception) {

                    }
                });
            }

        }

        if (mDeviceBeanList != null && mDeviceBeanList.size() > 1) {

            mIvTouchViewTwo.setVisibility(View.VISIBLE);
            mSeekBarTwo.setVisibility(View.GONE);
            mNewLineViewTwo.clear();
            mNewLineViewTwo.setVisibility(View.VISIBLE);
            BleManager.getInstance().write(mDeviceBeanList.get(1).getBleDevice(), Constans.CUSTOM_SERVICE_UUID, Constans.CUSTOM_WRITE_CHARACTERISTIC_UUID, HexUtil.hexStringToBytes("A20100"), new BleWriteCallback() {
                @Override
                public void onWriteSuccess(int current, int total, byte[] justWrite) {
                    Log.e("onWriteSuccess", justWrite[0] + "");
                }

                @Override
                public void onWriteFailure(BleException exception) {

                }
            });

            if (!isPostData) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                BleManager.getInstance().write(mDeviceBeanList.get(1).getBleDevice(), Constans.CUSTOM_SERVICE_UUID, Constans.CUSTOM_WRITE_CHARACTERISTIC_UUID, HexUtil.hexStringToBytes("A20100"), new BleWriteCallback() {
                    @Override
                    public void onWriteSuccess(int current, int total, byte[] justWrite) {
                        Log.e("onWriteSuccess", justWrite[0] + "");
                    }

                    @Override
                    public void onWriteFailure(BleException exception) {

                    }
                });
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                BleManager.getInstance().write(mDeviceBeanList.get(1).getBleDevice(), Constans.CUSTOM_SERVICE_UUID, Constans.CUSTOM_WRITE_CHARACTERISTIC_UUID, HexUtil.hexStringToBytes("A20100"), new BleWriteCallback() {
                    @Override
                    public void onWriteSuccess(int current, int total, byte[] justWrite) {
                        Log.e("onWriteSuccess", justWrite[0] + "");
                    }

                    @Override
                    public void onWriteFailure(BleException exception) {

                    }
                });
            }

        }

        if (!isSlideRun) {
            ThreadUtils.getThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    isSlideRun = true;
                    slideRunnable.run();
                }
            });

            ThreadUtils.getThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    isSlideRun = true;
                    slideTwoRunnable.run();
                }
            });
        }


    }

    @Override
    public void onMessageEvent(MessageEvent event) {
        super.onMessageEvent(event);

        if (event.getCode() == Constans.REAL_TIME_VIEW_REFRESH_CODE) {

            mNewLineView.setValue((int) event.getObj());
            mNewLineView.refrshView();

        } else if (event.getCode() == Constans.REAL_TIME_VIEW_REFRESH_TWO_CODE) {

            mNewLineViewTwo.setValue((int) event.getObj());
            mNewLineView.refrshView();

        } else if (event.getCode() == Constans.REAL_TIME_VIEW_CYCLE_REFRESH_CODE) {

            Log.e("onMessageEvent", (int) event.getObj() + "");

            if (isPostData) {
                mNewLineView.setValue((int) event.getObj());
                mNewLineView.refrshView();
            }

            long strength = Long.parseLong(String.valueOf(((Double) ((int) event.getObj() * 4.5)).intValue()), 16);

            Log.e("strength", String.valueOf((((int) event.getObj() % 3 * 10))));

            byte[] viewBytes = null;
            if ((int) event.getObj() == 0) {
                viewBytes = HexUtil.hexStringToBytes("A20100");
            }/* else if ((int) event.getObj() == 12) {
                viewBytes = HexUtil.hexStringToBytes("A20128");
            }*/ else {
                viewBytes = HexUtil.hexStringToBytes("A201" + String.valueOf(strength));
            }

            if (mDeviceBeanList != null && mDeviceBeanList.get(0) != null) {

                BleManager.getInstance().write(mDeviceBeanList.get(0).getBleDevice(), Constans.CUSTOM_SERVICE_UUID, Constans.CUSTOM_WRITE_CHARACTERISTIC_UUID, viewBytes, new BleWriteCallback() {
                    @Override
                    public void onWriteSuccess(int current, int total, byte[] justWrite) {
                        Log.e("A20101", justWrite[0] + "");
                    }

                    @Override
                    public void onWriteFailure(BleException exception) {

                    }
                });

                if (!isPostData) {
                    BleManager.getInstance().write(mDeviceBeanList.get(0).getBleDevice(), Constans.CUSTOM_SERVICE_UUID, Constans.CUSTOM_WRITE_CHARACTERISTIC_UUID, HexUtil.hexStringToBytes("A20100"), new BleWriteCallback() {
                        @Override
                        public void onWriteSuccess(int current, int total, byte[] justWrite) {
                            Log.e("!isPostData", "justWrite=" + justWrite[0]);
                        }

                        @Override
                        public void onWriteFailure(BleException exception) {

                        }
                    });
                }

                if (!isPostData) {
                    BleManager.getInstance().write(mDeviceBeanList.get(0).getBleDevice(), Constans.CUSTOM_SERVICE_UUID, Constans.CUSTOM_WRITE_CHARACTERISTIC_UUID, HexUtil.hexStringToBytes("A20100"), new BleWriteCallback() {
                        @Override
                        public void onWriteSuccess(int current, int total, byte[] justWrite) {
                            Log.e("!isPostData", "justWrite=" + justWrite[0]);
                        }

                        @Override
                        public void onWriteFailure(BleException exception) {

                        }
                    });
                }
            }

        } else if (event.getCode() == Constans.REAL_TIME_VIEW_CYCLE_REFRESH_TWO_CODE) {

            Log.e("onMessageEvent two", (int) event.getObj() + "");

            if (isPostData) {
                mNewLineViewTwo.setValue((int) event.getObj());
                mNewLineViewTwo.refrshView();
            }
            long strength = Long.parseLong(String.valueOf(((Double) ((int) event.getObj() * 4.5)).intValue()), 16);
            byte[] viewBytes = null;
            if ((int) event.getObj() == 0) {
                viewBytes = HexUtil.hexStringToBytes("A20100");
            }/* else if ((int) event.getObj() == 12) {
                viewBytes = HexUtil.hexStringToBytes("A20128");
            } */ else {
                viewBytes = HexUtil.hexStringToBytes("A201" + String.valueOf(strength));
            }

            if (mDeviceBeanList != null && mDeviceBeanList.get(1) != null) {

                BleManager.getInstance().write(mDeviceBeanList.get(1).getBleDevice(), Constans.CUSTOM_SERVICE_UUID, Constans.CUSTOM_WRITE_CHARACTERISTIC_UUID, viewBytes, new BleWriteCallback() {
                    @Override
                    public void onWriteSuccess(int current, int total, byte[] justWrite) {
                        Log.e("A20101", justWrite[0] + "");
                    }

                    @Override
                    public void onWriteFailure(BleException exception) {

                    }
                });

                if (!isPostData) {
                    BleManager.getInstance().write(mDeviceBeanList.get(1).getBleDevice(), Constans.CUSTOM_SERVICE_UUID, Constans.CUSTOM_WRITE_CHARACTERISTIC_UUID, HexUtil.hexStringToBytes("A20100"), new BleWriteCallback() {
                        @Override
                        public void onWriteSuccess(int current, int total, byte[] justWrite) {
                            Log.e("!isPostData", "justWrite=" + justWrite[0]);
                        }

                        @Override
                        public void onWriteFailure(BleException exception) {

                        }
                    });
                }
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

    @Override
    protected void onDestroy() {
        isRun = false;
        isCycle = false;
        isCycleRun = false;
        isTraditional = false;
        if (mDeviceBeanList != null && mDeviceBeanList.size() > 0) {

            BleManager.getInstance().write(mDeviceBeanList.get(0).getBleDevice(), Constans.CUSTOM_SERVICE_UUID, Constans.CUSTOM_WRITE_CHARACTERISTIC_UUID, HexUtil.hexStringToBytes("A20100"), new BleWriteCallback() {

                @Override
                public void onWriteSuccess(int current, int total, byte[] justWrite) {
                    Log.e("onWriteSuccess", justWrite[0] + "");
                }

                @Override
                public void onWriteFailure(BleException exception) {

                }
            });

        }

        if (mDeviceBeanList != null && mDeviceBeanList.size() > 1) {

            BleManager.getInstance().write(mDeviceBeanList.get(1).getBleDevice(), Constans.CUSTOM_SERVICE_UUID, Constans.CUSTOM_WRITE_CHARACTERISTIC_UUID, HexUtil.hexStringToBytes("A20100"), new BleWriteCallback() {
                @Override
                public void onWriteSuccess(int current, int total, byte[] justWrite) {
                    Log.e("onWriteSuccessonDestroy", justWrite[0] + "");
                }

                @Override
                public void onWriteFailure(BleException exception) {

                }
            });

        }

        super.onDestroy();
    }

}
