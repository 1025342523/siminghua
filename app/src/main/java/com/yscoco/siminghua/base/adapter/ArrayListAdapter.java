package com.yscoco.siminghua.base.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：karl.wei
 * 创建日期： 2017/8/9 0009 14:11
 * 邮箱：karl.wei@yscoco.com
 * QQ:2736764338
 * 类介绍：arrayList的基类
 */
public abstract class ArrayListAdapter<T> extends BaseAdapter {

	protected Activity mContext;
	protected ArrayList<T> mList = new ArrayList<T>();

	public ArrayListAdapter(Activity c) {
		this.mContext = c;
	}

	public void setList(ArrayList<T> list) {
		if(mList==null){
			mList = new ArrayList<>();
		}
		mList = list;
		notifyDataSetChanged();
	}

	public void addItem(T t) {
		if(mList==null){
			mList = new ArrayList<>();
		}
		mList.add(t);
		notifyDataSetChanged();
	}

	public void addItems(ArrayList<T> list) {
		if(mList==null){
			mList = new ArrayList<>();
		}
		for (T t : list) {
			mList.add(t);
		}
		notifyDataSetChanged();
	}
	
	public void deleteItem(T t) {
		if(mList==null){
			mList = new ArrayList<>();
		}
		mList.remove(t);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return (mList != null) ? mList.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return (mList != null) ? mList.get(position) : null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	abstract public View getView(int position, View convertView, ViewGroup parent);

	public List<T> getList() {
		return mList;
	}
	
	public void clearItem() {
		mList.clear();
	}
}
