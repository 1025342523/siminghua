package com.yscoco.siminghua.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import com.ys.module.toast.ToastTool;
import com.yscoco.siminghua.R;
import com.yscoco.siminghua.base.activity.BaseActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 作者：karl.wei
 * 创建日期： 2017/7/31 0031 19:23
 * 邮箱：karl.wei@yscoco.com
 * QQ:2736764338
 * 类介绍：版本升级
 */
public class AutoUpdate {
    /* 下载包安装路径 */
    private static final int DOWN_UPDATE = 1;
    private static final int DOWN_OVER = 2;
    private static final int DOWN_CANCEL = 4;
    // private final String mServerDir;
    private static String mAppFile = "WewBar.apk";
    private BaseActivity mContext = null;
    private Boolean state = false;
    private Dialog mNoticeDialog;
    private Dialog mDownloadDialog;
    private ProgressBar mProgressBar;

    private boolean mInterceptFlag;

    private int mProgress;

    private String mDownloadUrl;

    /**
     * 版本更新
     *
     * @param state 是否需要提示
     */
    public AutoUpdate(Activity context, Boolean state) {
        mContext = (BaseActivity) context;
        this.state = state;
//        checkVersionUpdate();
    }

    public AutoUpdate(Activity context) {
        mContext = (BaseActivity) context;
//        checkVersionUpdate();
    }

    /**
     * 外部接口调用
     */
    public void checkUpdateInfo(int serverCode) {
        int localVersion;
        try {
            localVersion = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionCode;
            if (localVersion < serverCode) {
                showNoticeDialog();
            } else {
                if (state) {
                    ToastTool.showNormalShort(mContext, R.string.curret_vesion_new_text);
                }
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 是否更新对话框
     */
    private void showNoticeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setInverseBackgroundForced(true);
        builder.setCancelable(false);
        builder.setTitle(R.string.vesion_update_text);
        builder.setPositiveButton(R.string.now_update_text, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                showDownloadDialog();
            }
        });

        builder.setNegativeButton(R.string.after_update_text, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        mNoticeDialog = builder.create();
        mNoticeDialog.show();
        mNoticeDialog.setCancelable(false);
    }

    private void showDownloadDialog() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.activity_update, null);
        mProgressBar = (ProgressBar) v.findViewById(R.id.progress_bar);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setCancelable(false);
        builder.setTitle(R.string.vesion_update_text);
        builder.setView(v);
        builder.setNegativeButton(R.string.cancel_text, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mInterceptFlag = false;
            }
        });
        mDownloadDialog = builder.create();
        mDownloadDialog.show();
        mInterceptFlag = true;
        new DownloadThread().start();
    }

    /**
     * 安装apk
     *
     * @param
     */
    private void installApk() {
        File tempFile = new File("/mnt/internal_sd/install.sys");// 安装权限
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        File file = mContext.getFileStreamPath(mAppFile);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        mContext.startActivity(intent);

        mContext.getFileStreamPath(mAppFile).deleteOnExit();
        tempFile.deleteOnExit();
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWN_UPDATE:
                    mProgressBar.setProgress(mProgress);
                    // mProgressTextView.setText(mProgress + "%");
                    break;
                case DOWN_OVER:
                    if (mDownloadDialog != null) {

                        mDownloadDialog.dismiss();
                    }
                    installApk();
                    break;
                case DOWN_CANCEL:
                    if (mDownloadDialog != null) {

                        mDownloadDialog.dismiss();
                    }
                    break;
                default:
                    break;
            }
        }

        ;
    };

    /**
     * 下载apk
     */
    private class DownloadThread extends Thread {

        @SuppressWarnings("deprecation")
        @Override
        public void run() {
            InputStream is = null;
            FileOutputStream fos = null;
            try {
                URL url = new URL(mDownloadUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();
                int length = conn.getContentLength();
                is = conn.getInputStream();
                // 必须为MODE_WORLD_READABLE模式，否则不能成功解析包
                fos = mContext.openFileOutput(mAppFile, Context.MODE_WORLD_READABLE);

                int count = 0;
                byte[] buf = new byte[1024];
                while (mInterceptFlag) {
                    int numread = is.read(buf);
                    count += numread;
                    if (numread <= 0) {
                        fos.flush();
                        mHandler.sendEmptyMessage(DOWN_OVER);
                        break;
                    }
                    mProgress = (int) (((float) count / length) * 100);
                    mHandler.sendEmptyMessage(DOWN_UPDATE);
                    fos.write(buf, 0, numread);
                }
            } catch (MalformedURLException e) {

                mHandler.sendEmptyMessage(DOWN_UPDATE);
                e.printStackTrace();
            } catch (IOException e) {
                mHandler.sendEmptyMessage(DOWN_UPDATE);

            } finally {
                try {
                    if (fos != null) {
                        fos.close();
                    }

                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private int count = 0;

 /*   private void checkVersionUpdate() {
        VesionParam param = new VesionParam();
        param.isIos = YN.N;
        param.appId = "kj";
        mContext.getHttp().curApp(param, new RequestListener<DataMessageBean<UpdatedVersionBean>>() {
            @Override
            public void onSuccess(DataMessageBean<UpdatedVersionBean> data) {
                if (data.getData() instanceof UpdatedVersionBean) {
                    UpdatedVersionBean dto = (UpdatedVersionBean) data.getData();
                    count = 0;
                    if (dto.getUrl() != null) {
                        mDownloadUrl = dto.getUrl();
                        checkUpdateInfo((int) dto.getVersion());
                    }
                }
            }
        });
    }*/
}
