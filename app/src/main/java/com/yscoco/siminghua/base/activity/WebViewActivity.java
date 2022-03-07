package com.yscoco.siminghua.base.activity;

import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ys.module.title.TitleBar;
import com.yscoco.siminghua.R;

import butterknife.BindView;

/**
 * 作者：karl.wei
 * 创建日期： 2017/8/9 0009 14:11
 * 邮箱：karl.wei@yscoco.com
 * QQ:2736764338
 * 类介绍：WebviewActivity
 */
public class WebViewActivity extends BaseActivity {

    @BindView(R.id.tb_title)
    TitleBar tv_title;
    @BindView(R.id.wb_content)
    WebView wb_content;
    String mTitle;
    String url;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_web_view;
    }

    @Override
    protected void init() {
        if (getIntent().hasExtra("title")) {
            mTitle = getIntent().getStringExtra("title");

            tv_title.setTitle(mTitle);
        }
        url = getIntent().getStringExtra("url");
        wb_content.getSettings().setJavaScriptEnabled(true);
        wb_content.loadUrl(url);
        wb_content.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        wb_content.setWebChromeClient(new WebChromeClient());
    }
}
