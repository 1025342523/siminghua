package com.yscoco.siminghua.ui.activity.custom;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ys.module.title.TitleBar;
import com.yscoco.siminghua.Constans;
import com.yscoco.siminghua.R;
import com.yscoco.siminghua.base.activity.BaseActivity;
import com.yscoco.siminghua.domain.CustomBean;
import com.yscoco.siminghua.domain.MessageEvent;
import com.yscoco.siminghua.ui.activity.custom.adapter.CustomAdapter;
import com.yscoco.siminghua.utils.ResourcesUtils;
import com.yscoco.siminghua.utils.SpUtils;
import com.yscoco.siminghua.view.AddModeDialog;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by ZhangZeZhi on 2018\10\27 0027.
 */

public class CustomActivity extends BaseActivity implements CustomAdapter.OnItemClickListener {

    @BindView(R.id.tb_title)
    TitleBar mTitleBar;

    @BindView(R.id.rv)
    RecyclerView mRv;

    private AddModeDialog mDialog;
    private List<CustomBean> mNameList;
    private CustomAdapter mAdapter;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_custom;
    }

    @Override
    protected void init() {
        transparentStatusBar(R.color.transparent);
        mTitleBar.setTitle(R.string.custom_text, ResourcesUtils.getColor(R.color.custom_title_text_color));
        mNameList = SpUtils.getDataList(Constans.MODE_NAME_KEY, CustomBean.class);
        mRv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mAdapter = new CustomAdapter(R.layout.layout_custom_rv_item, mNameList);
        mAdapter.setOnItemClickListener(this);
        mRv.setAdapter(mAdapter);
    }

    @OnClick({R.id.iv_back, R.id.ll_add_mode
            , R.id.ll_pulse, R.id.ll_wave
            , R.id.ll_fireworks, R.id.ll_earthquake})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.ll_add_mode:
                showAddModeDialog();
                break;
            case R.id.ll_pulse:
                showActivity(CustomModeActivity.class, Constans.ORIGINAL_MODE_PULSE);
                break;
            case R.id.ll_wave:
                showActivity(CustomModeActivity.class, Constans.ORIGINAL_MODE_WAVE);
                break;
            case R.id.ll_fireworks:
                showActivity(CustomModeActivity.class, Constans.ORIGINAL_MODE_FIREWORKS);
                break;
            case R.id.ll_earthquake:
                showActivity(CustomModeActivity.class, Constans.ORIGINAL_MODE_EARTHQUAKE);
                break;
        }
    }

    private void showAddModeDialog() {
        mDialog = new AddModeDialog.Builder(this).setVibratorListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showActivity(AddCustomModeActivity.class, Constans.MODE_TYPE_ONE_CODE);
                mDialog.dismiss();
            }
        }).setVibrator2Listener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showActivity(AddCustomModeActivity.class, Constans.MODE_TYPE_TWO_CODE);
                mDialog.dismiss();
            }
        }).setRotateListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showActivity(AddCustomModeActivity.class, Constans.MODE_TYPE_THREE_CODE);
                mDialog.dismiss();
            }
        }).setShrinkListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showActivity(AddCustomModeActivity.class, Constans.MODE_TYPE_FOUR_CODE);
                mDialog.dismiss();
            }
        }).create();
        mDialog.show();
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        CustomBean item = mAdapter.getItem(position);
        item.setPlayer(true);
        Intent intent = new Intent(this, CustomModeActivity.class);
        intent.putExtra(Constans.CUSTOM_BEAN_KEY, item);
        intent.putExtra(Constans.CURRENT_POSITION_KEY, position);
        startActivity(intent);
    }

    @Override
    public void onMessageEvent(MessageEvent event) {
        super.onMessageEvent(event);
        if (event.getCode() == Constans.SAVE_MODE_CODE) {
            mNameList.add((CustomBean) event.getObj());
            SpUtils.setDataList(Constans.MODE_NAME_KEY, mNameList);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
        super.onDestroy();
    }
}
