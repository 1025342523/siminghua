package com.yscoco.siminghua.ui.activity.music;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.ys.module.title.TitleBar;
import com.ys.module.toast.ToastTool;
import com.ys.module.utils.StringUtils;
import com.yscoco.siminghua.Constans;
import com.yscoco.siminghua.R;
import com.yscoco.siminghua.base.activity.BaseActivity;
import com.yscoco.siminghua.domain.MessageEvent;
import com.yscoco.siminghua.domain.SongBean;
import com.yscoco.siminghua.ui.activity.music.adapter.PlayMusicListAdapter;
import com.yscoco.siminghua.utils.ResourcesUtils;
import com.yscoco.siminghua.utils.SpUtils;
import com.yscoco.siminghua.view.SwipeItemLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by ZhangZeZhi on 2018\10\26 0026.
 */

public class PlayMusicActivity extends BaseActivity implements PlayMusicListAdapter.OnItemViewClickedListener {

    @BindView(R.id.tb_title)
    TitleBar mTitleBar;

    @BindView(R.id.rv)
    RecyclerView mRv;

    @BindView(R.id.tv_count)
    TextView mTvCount;

    private String mName;
    private List<SongBean> mSongBeanList = new ArrayList<>();
    private PlayMusicListAdapter mAdapter;
    private SwipeItemLayout.OnSwipeItemTouchListener mItemTouchListener;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_play_music;
    }

    @Override
    protected void init() {
        transparentStatusBar(R.color.transparent);
        mTitleBar.setTitle(R.string.play_music_text, ResourcesUtils.getColor(R.color.title_color));
        mTitleBar.gone();
        mName = (String) getValue();
        mRv.setLayoutManager(new LinearLayoutManager(this));
        if (!StringUtils.isEmpty(mName)) {
            mSongBeanList.clear();
            mSongBeanList.addAll(SpUtils.getDataList(mName, SongBean.class));
            if (mSongBeanList != null && mSongBeanList.size() > 0) {
                mTvCount.setText("(共" + mSongBeanList.size() + "首歌曲)");
            }
            mAdapter = new PlayMusicListAdapter(R.layout.layout_play_music_rv_item, mSongBeanList);
            mAdapter.setOnItemViewClickListener(this);
            mRv.setAdapter(mAdapter);
            mItemTouchListener = new SwipeItemLayout.OnSwipeItemTouchListener(this);
            mRv.addOnItemTouchListener(mItemTouchListener);
        }
    }

    @OnClick({R.id.iv_back, R.id.iv_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_add:
                if (Environment.MEDIA_MOUNTED
                        .equals(Environment.getExternalStorageState())) {
                    showActivity(MusicListActivity.class, mName);
                } else {
                    ToastTool.showNormalShort(this, "SD卡不可用");
                }
                break;
        }
    }

    @Override
    public void onMessageEvent(MessageEvent event) {
        super.onMessageEvent(event);
        if (event.getCode() == Constans.SONG_BEAN_CODE) {
            Log.e("onMessageEvent", event.getCode() + "");
            List<SongBean> songBeanList = (List<SongBean>) event.getObj();
            mSongBeanList.clear();
            mSongBeanList.addAll(songBeanList);
            mAdapter.setNewData(mSongBeanList);
            mAdapter.notifyDataSetChanged();
            mTvCount.setText("(共" + mSongBeanList.size() + "首歌曲)");
        }
    }

    @Override
    public void onViewClicked(int position) {
        mSongBeanList.remove(position);
        mAdapter.setNewData(mSongBeanList);
        mAdapter.notifyDataSetChanged();
        SpUtils.setDataList(mName, mSongBeanList);
        Log.e("mName", mName);
        mTvCount.setText("(共" + mSongBeanList.size() + "首歌曲)");
    }

    @Override
    public void onItemClicked(SongBean bean, int position, View view) {
//        showActivity(MusicControlActivity.class, bean);
        Intent intent = new Intent(this, MusicControlActivity.class);
        intent.putExtra(Constans.POSITION_KEY, position);
        intent.putExtra(Constans.LIST_NAME_KEY, mName);
        startActivity(intent);
    }

}
