package com.yscoco.siminghua;

import android.view.View;

import com.yscoco.siminghua.base.activity.BaseActivity;
import com.yscoco.siminghua.ui.activity.home.HomeActivity;

import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @Override
    protected int setLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void init() {
        transparentStatusBar(R.color.transparent);
    }

    @OnClick({R.id.btn_start})
    public void onViewClicked(View view) {
        showActivity(HomeActivity.class);
        finish();
    }

}
