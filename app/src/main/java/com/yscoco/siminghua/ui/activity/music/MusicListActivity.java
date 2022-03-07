package com.yscoco.siminghua.ui.activity.music;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzx.musiclibrary.aidl.model.SongInfo;
import com.ys.module.title.TitleBar;
import com.yscoco.siminghua.Constans;
import com.yscoco.siminghua.R;
import com.yscoco.siminghua.base.activity.BaseActivity;
import com.yscoco.siminghua.domain.MessageEvent;
import com.yscoco.siminghua.domain.SongBean;
import com.yscoco.siminghua.ui.activity.music.adapter.SongListAdapter;
import com.yscoco.siminghua.utils.AudioUtils;
import com.yscoco.siminghua.utils.ResourcesUtils;
import com.yscoco.siminghua.utils.SpUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by ZhangZeZhi on 2018\10\26 0026.
 */

public class MusicListActivity extends BaseActivity implements SongListAdapter.OnItemClickListener {

    @BindView(R.id.tb_title)
    TitleBar mTitleBar;

    @BindView(R.id.rv)
    RecyclerView mRv;

    private List<SongBean> mSongBeanList = new ArrayList<>();
    private SongListAdapter mAdapter;
    private String mName;
    private List<SongBean> mBeanList = new ArrayList<>();

    @Override
    protected int setLayoutId() {
        return R.layout.activity_music_list;
    }

    @Override
    protected void init() {
        transparentStatusBar(R.color.transparent);
        mTitleBar.setTitle(R.string.music_list_text, ResourcesUtils.getColor(R.color.title_color));
        mName = (String) getValue();
        List<SongBean> songBeanList = SpUtils.getDataList(mName, SongBean.class);
        mBeanList.clear();
        mBeanList.addAll(songBeanList);
        Log.e("mName", mName);
        Log.e("mName", mBeanList.size() + "");
        mSongBeanList.clear();
        List<SongInfo> infoList = AudioUtils.getAllSongs(this);
        for (SongInfo songInfo : infoList) {
            SongBean songBean = new SongBean();
            songBean.setSongInfo(songInfo);
            songBean.setSelected(false);
            songBean.setPlay(false);
            mSongBeanList.add(songBean);
        }
        if (mBeanList != null && mBeanList.size() > 0) {
            for (int i = 0; i < mSongBeanList.size(); i++) {
                for (SongBean bean : mBeanList) {
                    if (mSongBeanList.get(i).getSongInfo().getSongUrl().equals(bean.getSongInfo().getSongUrl())) {
                        mSongBeanList.set(i, bean);
                    }
                }
            }
        }

        mAdapter = new SongListAdapter(R.layout.layout_song_rv_item, mSongBeanList);
        mRv.setLayoutManager(new LinearLayoutManager(this));
        mAdapter.setOnItemClickListener(this);
        mRv.setAdapter(mAdapter);

    }

    @OnClick({R.id.iv_left_arrow})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_left_arrow:
                finish();
                break;
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        SongBean item = mAdapter.getItem(position);
        if (item.isSelected()) {
            item.setSelected(false);
            if (mBeanList.contains(item)) {
                mBeanList.remove(item);
            }
        } else {
            item.setSelected(true);
            if (!mBeanList.contains(item)) {
                mBeanList.add(item);
            }
        }
        mAdapter.setData(position, item);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        SpUtils.setDataList(mName, mBeanList);
        MessageEvent event = new MessageEvent();
        event.setCode(Constans.SONG_BEAN_CODE);
        event.setObj(mBeanList);
        EventBus.getDefault().post(event);
        super.onDestroy();
    }

}
