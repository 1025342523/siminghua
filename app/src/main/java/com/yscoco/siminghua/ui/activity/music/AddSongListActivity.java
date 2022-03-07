package com.yscoco.siminghua.ui.activity.music;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ys.module.dialog.EditDialogUtils;
import com.ys.module.title.TitleBar;
import com.yscoco.siminghua.Constans;
import com.yscoco.siminghua.R;
import com.yscoco.siminghua.base.activity.BaseActivity;
import com.yscoco.siminghua.ui.activity.music.adapter.AddSongListAdapter;
import com.yscoco.siminghua.utils.ResourcesUtils;
import com.yscoco.siminghua.utils.SpUtils;
import com.yscoco.siminghua.view.SwipeItemLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by ZhangZeZhi on 2018-10-25.
 */

public class AddSongListActivity extends BaseActivity implements AddSongListAdapter.ItemClickedListener {

    @BindView(R.id.tb_title)
    TitleBar mTitleBar;

    @BindView(R.id.rv)
    RecyclerView mRv;

    private EditDialogUtils mEditDialogUtils;
    private List<String> mNameList = new ArrayList<>();
    private AddSongListAdapter mAdapter;
    private SwipeItemLayout.OnSwipeItemTouchListener mItemTouchListener;
    private EditDialogUtils mEditDialog;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_add_song_list;
    }

    @Override
    protected void init() {

        transparentStatusBar(R.color.transparent);
        mTitleBar.setTitle(R.string.play_music_text, ResourcesUtils.getColor(R.color.title_color));
        mTitleBar.gone();
        mRv.setLayoutManager(new LinearLayoutManager(this));
        mNameList.clear();
        mNameList.addAll(SpUtils.getDataList(Constans.SONG_LIST_KEY, String.class));
        mAdapter = new AddSongListAdapter(R.layout.layout_add_song_list_item, mNameList);
        mAdapter.setItemClickedListener(this);
        mRv.setAdapter(mAdapter);
        mItemTouchListener = new SwipeItemLayout.OnSwipeItemTouchListener(this);
        mRv.addOnItemTouchListener(mItemTouchListener);

    }

    @Override
    public void onItemClicked(View view, String item, int position) {
        String name = mAdapter.getItem(position);
        showActivity(PlayMusicActivity.class, name);
    }

    @Override
    public void onItemViewClicked(View view, int position, SwipeItemLayout itemLayout) {
        switch (view.getId()) {
            case R.id.tv_delete:
                mNameList.remove(position);
                mAdapter.setNewData(mNameList);
                mAdapter.notifyDataSetChanged();
                SpUtils.setDataList(Constans.SONG_LIST_KEY, mNameList);
                itemLayout.close();
                break;
            case R.id.tv_edit:
                showEditDialog(position);
                itemLayout.close();
                break;
        }
    }

    @OnClick({R.id.iv_left_arrow, R.id.ll_add_song})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_left_arrow:
                finish();
                break;
            case R.id.ll_add_song:
                showAddDialog();
                break;
        }
    }

    /**
     * 弹出编辑框
     */
    private void showEditDialog(int position) {
        mEditDialog = new EditDialogUtils(this);
        dialogUpdate(mEditDialog.builder(), "编辑音乐", "")
                .setRightBack(new EditDialogUtils.RightCallBack() {
                    @Override
                    public void rightBtn(int what, String content) {
//                        mNameList.add(content);
                        mNameList.set(position, content);
                        mAdapter.notifyDataSetChanged();
                        SpUtils.setDataList(Constans.SONG_LIST_KEY, mNameList);
                    }
                });
        mEditDialog.show();
    }

    private void showAddDialog() {
        mEditDialogUtils = new EditDialogUtils(this);
        dialogUpdate(mEditDialogUtils.builder(), "创建音乐列表", "")
                .setRightBack(new EditDialogUtils.RightCallBack() {
                    @Override
                    public void rightBtn(int what, String content) {
                        mNameList.add(content);
                        mAdapter.notifyDataSetChanged();
                        SpUtils.setDataList(Constans.SONG_LIST_KEY, mNameList);
                    }
                });
        mEditDialogUtils.show();
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

}
