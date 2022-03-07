package com.yscoco.siminghua.base.fragment;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.umeng.analytics.MobclickAgent;
import com.ys.module.dialog.LoadingDialog;
import com.yscoco.siminghua.base.activity.BaseActivity;
import com.yscoco.siminghua.domain.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;

import butterknife.ButterKnife;
import me.yokeyword.fragmentation.SupportFragment;

/**
 * 作者：karl.wei
 * 创建日期： 2018/5/12 0012
 * 邮箱：karl.wei@yscoco.com
 * QQ：2736764338
 * 类介绍：fragment基类
 */
public abstract class BaseFragment extends SupportFragment {

    protected View mView;

    protected BaseActivity mActivity;

    protected FragmentManager mFragmentManager;

    private int fragmentId;

    protected LoadingDialog mLoadingDialog;

    protected OnOpenDrawerLayoutListener mListener;

//    protected UserInfoBean userInfoDTO;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (BaseActivity) activity;
        EventBus.getDefault().register(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnOpenDrawerLayoutListener) {
            mListener = (OnOpenDrawerLayoutListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        EventBus.getDefault().unregister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

//        userInfoDTO = SharePreferenceUser.readShareMember(getActivity());

        if (mView != null) {
            ViewGroup parent = (ViewGroup) mView.getParent();
            if (parent != null) {
                parent.removeView(mView);
            }
        } else {
            mView = inflater.inflate(setLayoutId(), container, false);
            ButterKnife.bind(this, mView);
            setStatusBar();
        }
        return mView;
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        init();
    }

    protected void setStatusBar() {
    }

   /* public OkHttp getHttp() {
        return mActivity.getHttp();
//    }*/

    protected abstract int setLayoutId();

    protected abstract void init();

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(getClass().getSimpleName());
    }

    public void showActivity(Class<?> cls) {
        mActivity.showActivity(cls);
    }

    public void showActivity(Class<?> cls, Serializable arg) {
        mActivity.showActivity(cls, arg);
    }

    public void showActivityForResult(Class<?> cls, int requestCode) {
        Intent intent = new Intent(mActivity, cls);
        startActivityForResult(intent, requestCode);
    }

    public void showActivitySetResult(Class<?> cls, int requstCode, Serializable obj) {
        Intent i = new Intent(mActivity, cls);
        if (obj != null) {
            i.putExtra("value", obj);
        }
        startActivityForResult(i, requstCode);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {

    }

    public interface OnOpenDrawerLayoutListener {
        void onOpen();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
