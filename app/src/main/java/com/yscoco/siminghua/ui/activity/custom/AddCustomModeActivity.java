package com.yscoco.siminghua.ui.activity.custom;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ys.module.dialog.EditDialogUtils;
import com.ys.module.title.TitleBar;
import com.yscoco.siminghua.Constans;
import com.yscoco.siminghua.R;
import com.yscoco.siminghua.base.activity.BaseActivity;
import com.yscoco.siminghua.domain.CustomBean;
import com.yscoco.siminghua.domain.MessageEvent;
import com.yscoco.siminghua.utils.ResourcesUtils;
import com.yscoco.siminghua.utils.ThreadUtils;
import com.yscoco.siminghua.view.NewLineView;
import com.yscoco.siminghua.view.TouchImageView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by ZhangZeZhi on 2018\10\27 0027.
 * 自定义模式能够保存。
 */

public class AddCustomModeActivity extends BaseActivity {

    @BindView(R.id.tb_title)
    TitleBar mTitleBar;

    @BindView(R.id.rl_move_parent)
    RelativeLayout mRlMoveParent;

    @BindView(R.id.iv_touch_image)
    TouchImageView mIvTouchImage;

    @BindView(R.id.iv_touch_image_two)
    TouchImageView mIvTouchImageTwo;

    @BindView(R.id.new_line_view)
    NewLineView mNewLineView;

    @BindView(R.id.new_line_view_two)
    NewLineView mNewLineViewTwo;

    private EditDialogUtils mDialogUtils;

    private List<CustomBean> mCustomBean = new ArrayList<>();
    private List<Integer> lineList = new ArrayList<>();
    private List<Integer> towLineList = new ArrayList<>();
    private int mMode;
    private long time = 0;
    private boolean isRun = true;
    private int sleepTime = 20;
    private int viewValue = 0;
    private int viewTwoValue = 1;

    private Runnable addOneRunnable = new Runnable() {
        @Override
        public void run() {
            MessageEvent event = new MessageEvent();
            while (isRun) {
                event.setCode(Constans.ADD_CUSTOM_MODE_VIEW_REFRESH_CODE);
                event.setObj(viewValue);
                EventBus.getDefault().post(event);
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private Runnable addTwoRunnable = new Runnable() {
        @Override
        public void run() {
            MessageEvent event = new MessageEvent();
            while (isRun) {
                event.setCode(Constans.ADD_CUSTOM_MODE_VIEW_REFRESH_TWO_CODE);
                event.setObj(viewTwoValue);
                EventBus.getDefault().post(event);
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private Timer mTimer;
    private TimerTask mTimerTask;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_add_custom_mode;
    }

    @Override
    protected void init() {
        timer();
        lineList.clear();
        towLineList.clear();
        transparentStatusBar(R.color.transparent);
        mTitleBar.setTitle("用户自定义");
        mTitleBar.gone();
        mMode = getIntent().getIntExtra("value", 0);

        if (mMode == Constans.MODE_TYPE_TWO_CODE) {
            mIvTouchImageTwo.setVisibility(View.VISIBLE);
            mNewLineViewTwo.setColor(ResourcesUtils.getColor(R.color.yellow_color));
            ThreadUtils.getThreadPool().execute(addTwoRunnable);

            mIvTouchImageTwo.setOnMoveYListener(new TouchImageView.OnMoveYListener() {
                @Override
                public void onMoveY(int y) {
                    viewTwoValue = y % 12;
                    mNewLineViewTwo.setValue(viewTwoValue);
                    mNewLineViewTwo.refrshView();
                }

                @Override
                public void onUp() {

                }

                @Override
                public void onDown() {

                }
            });

        } else if (mMode == Constans.MODE_TYPE_THREE_CODE) {
            mIvTouchImageTwo.setVisibility(View.VISIBLE);
            mIvTouchImageTwo.setImageResource(R.mipmap.icon_rotate);
            mNewLineViewTwo.setColor(ResourcesUtils.getColor(R.color.yellow_color));
            ThreadUtils.getThreadPool().execute(addTwoRunnable);

            mIvTouchImageTwo.setOnMoveYListener(new TouchImageView.OnMoveYListener() {
                @Override
                public void onMoveY(int y) {
                    viewTwoValue = y % 12;
                    mNewLineViewTwo.setValue(viewTwoValue);
                    mNewLineViewTwo.refrshView();
                }

                @Override
                public void onUp() {

                }

                @Override
                public void onDown() {

                }
            });

        } else if (mMode == Constans.MODE_TYPE_FOUR_CODE) {
            mIvTouchImageTwo.setVisibility(View.VISIBLE);
            mIvTouchImageTwo.setImageResource(R.mipmap.icon_shrink);
            mNewLineViewTwo.setColor(ResourcesUtils.getColor(R.color.yellow_color));
            ThreadUtils.getThreadPool().execute(addTwoRunnable);

            mIvTouchImageTwo.setOnMoveYListener(new TouchImageView.OnMoveYListener() {
                @Override
                public void onMoveY(int y) {
                    viewTwoValue = y % 12;
                    mNewLineViewTwo.setValue(viewTwoValue);
                    mNewLineViewTwo.refrshView();
                }

                @Override
                public void onUp() {

                }

                @Override
                public void onDown() {

                }
            });
        }

        ThreadUtils.getThreadPool().execute(addOneRunnable);

        mIvTouchImage.setOnMoveYListener(new TouchImageView.OnMoveYListener() {
            @Override
            public void onMoveY(int y) {
                viewValue = y % 12;
                mNewLineView.setValue(viewValue);
                mNewLineView.refrshView();
            }

            @Override
            public void onUp() {

            }

            @Override
            public void onDown() {

            }
        });
    }

    @OnClick({R.id.iv_left_arrow, R.id.tv_save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_left_arrow:
                finish();
                break;
            case R.id.tv_save:
                isRun = false;
                mTimerTask.cancel();
                showSaveDialog();
                break;
        }
    }

    private void showSaveDialog() {
        mDialogUtils = new EditDialogUtils(this);
        dialogUpdate(mDialogUtils.builder(), "保存模式", "")
                .setRightBack(new EditDialogUtils.RightCallBack() {
                    @Override
                    public void rightBtn(int what, String content) {
                        CustomBean bean = new CustomBean();
                        MessageEvent event = new MessageEvent();
                        if (mMode == Constans.MODE_TYPE_TWO_CODE) {
                            bean.setSecondLineList(towLineList);
                        } else if (mMode == Constans.MODE_TYPE_THREE_CODE) {
                            bean.setSecondLineList(towLineList);
                        } else if (mMode == Constans.MODE_TYPE_FOUR_CODE) {
                            bean.setSecondLineList(towLineList);
                        }
                        bean.setName(content);
                        bean.setTypeCode(mMode);
                        bean.setLineList(lineList);
                        bean.setTimes(time);
                        event.setCode(Constans.SAVE_MODE_CODE);
                        event.setObj(bean);
                        Log.e("time", time + "");
                        EventBus.getDefault().post(event);
                        finish();
                    }
                })
                .setLeftBack(new EditDialogUtils.LeftCallBack() {
                    @Override
                    public void leftBtn(int what) {
                        mTimerTask.run();
                    }
                });
        mDialogUtils.show();
    }

    private EditDialogUtils dialogUpdate(EditDialogUtils dialog, String title, String content) {
        TextView titleView = dialog.getTitleView();
        titleView.setText(title);

        titleView.setTextColor(getResources().getColor(R.color.black_255));
        titleView.setBackground(getResources().getDrawable(R.drawable.shape_edit_dialog_title_bg));
        Button btnLeft = dialog.getBtnLeft();
        btnLeft.setTextColor(getResources().getColor(R.color.black_255));
        btnLeft.setText(R.string.no_text);
        Button btnRight = dialog.getBtnRight();

        btnRight.setTextColor(getResources().getColor(R.color.white_255));
        btnRight.setBackground(ResourcesUtils.getDrawable(R.drawable.shape_edit_right_btn_bg));
        btnRight.setText(R.string.yes_text);
        EditText etContent = dialog.getEtContent();
        etContent.setBackground(ResourcesUtils.getDrawable(R.drawable.shape_scan_bluetooth_edit_bg));
        etContent.setText(content);
        etContent.setTextColor(getResources().getColor(R.color.black_255));
        etContent.setGravity(Gravity.CENTER);

        return dialog;
    }

    @Override
    public void onMessageEvent(MessageEvent event) {
        super.onMessageEvent(event);
        if (event.getCode() == Constans.ADD_CUSTOM_MODE_VIEW_REFRESH_CODE) {
            mNewLineView.setValue((Integer) event.getObj());
            mNewLineView.refrshView();
            lineList.add((Integer) event.getObj());
        } else if (event.getCode() == Constans.ADD_CUSTOM_MODE_VIEW_REFRESH_TWO_CODE) {
            mNewLineViewTwo.setValue((Integer) event.getObj());
            mNewLineViewTwo.refrshView();
            towLineList.add((Integer) event.getObj());
        }
    }

    private void timer() {
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        time += 1000;
                        Log.e("mTimerTask", time + "");
                    }
                });
            }
        };
        mTimer.schedule(mTimerTask, 0, 1000);
    }

    @Override
    protected void onDestroy() {
        if (mTimer != null) {
            mTimer.cancel();
        }
        if (mTimerTask != null) {
            mTimerTask.cancel();
        }
        isRun = false;
        super.onDestroy();
    }

}
