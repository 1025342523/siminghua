package com.yscoco.siminghua.base.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.umeng.analytics.MobclickAgent;
import com.ys.module.log.LogUtils;
import com.ys.module.toast.ToastTool;
import com.ys.module.utils.StatusBarUtil;
import com.yscoco.siminghua.R;
import com.yscoco.siminghua.domain.MessageEvent;
import com.yscoco.siminghua.utils.AppManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;

import butterknife.ButterKnife;
import me.yokeyword.fragmentation.SupportActivity;

/**
 * 作者：karl.wei
 * 创建日期： 2017/8/9 0009 14:11
 * 邮箱：karl.wei@yscoco.com
 * QQ:2736764338
 * 类介绍：界面基类
 */
public abstract class BaseActivity extends SupportActivity {
    
    private long mExitTime;
    public LocalBroadcastManager lbm;

    public void setDisableStatusBar(boolean disableStatusBar) {
        this.disableStatusBar = disableStatusBar;
    }

    //控制是否消除顶部状态栏区域
    private boolean disableStatusBar = false;

    @Override
    protected void onCreate(Bundle arg0) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
        super.onCreate(arg0);

        AppManager.getInstance().addActivity(this);
        setContentView(setLayoutId());
        ButterKnife.bind(this);
        lbm = LocalBroadcastManager.getInstance(this);
        EventBus.getDefault().register(this);
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        init();
        LogUtils.e("Activity:onCreate" + toString());
    }

    public int initAlph() {
        return 0;
    }

    public int initColor() {
        return R.color.title_bg_color;
    }

    /**
     * 设置布局的ID
     */
    protected abstract int setLayoutId();

    protected abstract void init();

    public void showActivity(Class<?> cls) {
        showActivity(cls, null);
    }

    public void showActivity(Class<?> cls, int in) {
        Intent i = new Intent(this, cls);
        i.putExtra("value", in);
        startActivity(i);
    }

    public void showActivity(Class<?> cls, String str) {
        Intent i = new Intent(this, cls);
        if (str != null) {
            i.putExtra("value", str);
        }
        startActivity(i);
    }

    public void showActivityForWebView(String title, String url) {
        Intent intent = new Intent(BaseActivity.this, WebViewActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("url", url);
        startActivity(intent);
    }

    /**
     * 是否退出
     *
     * @param keyCode
     * @return
     */
    protected boolean isExit(int keyCode) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - mExitTime > 2000) {
                ToastTool.showNormalShort(this, "再按一次退出程序");
                mExitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return false;
    }
/*
    public OkHttp getHttp() {
        if (mHttp == null) {
            mHttp = new OkHttp(this);
        }
        return mHttp;
    }*/

    public void showActivity(Class<?> cls, Serializable obj) {
        Intent i = new Intent(this, cls);
        if (obj != null) {
            i.putExtra("value", obj);
        }
        startActivity(i);
    }

    public void showActivityForResult(Class<?> cls, int requestCode, String str) {
        Intent intent = new Intent(this, cls);
        intent.putExtra("value", str);
        startActivityForResult(intent, requestCode);
    }

    public void showActivityForResult(Class<?> cls, int requestCode, Serializable str) {
        Intent intent = new Intent(this, cls);
        intent.putExtra("value", str);
        startActivityForResult(intent, requestCode);
    }

    public void showActivityForResult(Class<?> cls, int requestCode, Parcelable str) {
        Intent intent = new Intent(this, cls);
        intent.putExtra("value", str);
        startActivityForResult(intent, requestCode);
    }

    public Serializable getValue() {
        return getIntent().getSerializableExtra("value");
    }

    public void showActivityForResult(Class<?> cls, int requestCode) {
        Intent intent = new Intent(this, cls);
        startActivityForResult(intent, requestCode);
    }

    public void showActivitySetResult(int resultCode, String str) {
        Intent intent = new Intent();
        intent.putExtra(str + "", str);
        setResult(resultCode, intent);
        this.finish();
    }

    public void showActivitySetResult(Class<?> cls, int requstCode, Serializable obj) {
        Intent i = new Intent(this, cls);
        if (obj != null) {
            i.putExtra("value", obj);
        }
        startActivityForResult(i, requstCode);
    }

    public BaseActivity() {
        super();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getInstance().finishActivity(this);
        EventBus.getDefault().unregister(this);
        //防止输入法内存泄漏
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        try {
            InputMethodManager.class.getDeclaredMethod("windowDismissed", IBinder.class).invoke(imm,
                    getWindow().getDecorView().getWindowToken());
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtils.e("Activity:onDestroy" + toString());
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageStart(getClass().getSimpleName());
        MobclickAgent.onPause(this);
        LogUtils.e("Activity:onPause" + toString());
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageEnd(getClass().getSimpleName());
        MobclickAgent.onResume(this);
        LogUtils.e("Activity:onResume" + toString());
    }


   /* *//*调用token初始化用户信息*//*
    public String getToken() {
        info = SharePreferenceUser.readShareMember(this);
        if (info != null) {
            return StringUtils.nullTanst(info.getToken());
        } else {
            info = new UserInfoBean();
            return "";
        }
    }*/

    /*初始化用户信息*//*
    public void initUserInfo() {
        info = SharePreferenceUser.readShareMember(this);
        if (info == null) {
            info = new UserInfoBean();
        }
    }*/

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
    }

    /**
     * 全透状态栏
     */
    protected void setStatusBarFullTransparent() {
        if (Build.VERSION.SDK_INT >= 21) {
            //21表示5.0
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= 19) {//19表示4.4
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    /**
     * 半透明状态栏
     */
    protected void setHalfTransparent() {
        if (Build.VERSION.SDK_INT >= 21) {//21表示5.0
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else if (Build.VERSION.SDK_INT >= 19) {//19表示4.4
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    /**
     * 如果需要内容紧贴着StatusBar * 应该在对应的xml布局文件中，设置根布局fitsSystemWindows=true。
     */
    private View contentViewGroup;

    protected void setFitSystemWindow(boolean fitSystemWindow) {
        if (contentViewGroup == null) {
            contentViewGroup = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        }
        contentViewGroup.setFitsSystemWindows(fitSystemWindow);
    }

    /**
     * 为了兼容4.4的抽屉布局->透明状态栏
     */
    protected void setDrawerLayoutFitSystemWindow() {
        if (Build.VERSION.SDK_INT == 19) {//19表示4.4
            int statusBarHeight = StatusBarUtil.getStatusBarHeight(this);
            if (contentViewGroup == null) {
                contentViewGroup = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
            }
            if (contentViewGroup instanceof DrawerLayout) {
                DrawerLayout drawerLayout = (DrawerLayout) contentViewGroup;
                drawerLayout.setClipToPadding(true);
                drawerLayout.setFitsSystemWindows(false);
                for (int i = 0; i < drawerLayout.getChildCount(); i++) {
                    View child = drawerLayout.getChildAt(i);
                    child.setFitsSystemWindows(false);
                    child.setPadding(0, statusBarHeight, 0, 0);
                }
            }
        }
    }

    public void setWindwos() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
    }

    protected void transparentStatusBar(int resId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(resId);//通知栏所需颜色
        }
    }

}

