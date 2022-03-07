package com.yscoco.siminghua.base.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yscoco.siminghua.R;
import com.yscoco.siminghua.utils.ResourcesUtils;

import java.util.List;


public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragmentsList;
    private String[] mTexts;
    private int[] icons;

    public MyFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public MyFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragments, String[] strs, int[] icons) {
        super(fm);
        this.fragmentsList = fragments;
        this.mTexts = strs;
        this.icons = icons;
    }

    @Override
    public int getCount() {
        return fragmentsList.size();
    }

    @Override
    public Fragment getItem(int arg0) {
        return fragmentsList.get(arg0);
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

    public View getTabView(int position) {
        View view = ResourcesUtils.inflate(R.layout.tab_item_view);
        ImageView ivTab = (ImageView) view.findViewById(R.id.imageview);
        TextView tvTab = (TextView) view.findViewById(R.id.textview);
        ivTab.setImageResource(icons[position]);
        tvTab.setText(mTexts[position]);
        return view;
    }

}
