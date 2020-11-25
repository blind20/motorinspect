package com.shsy.motoinspect.common;

import java.util.List;

import com.shsy.motoinspect.utils.Logger;
import com.shsy.motorinspect.R;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public abstract class CommonAdapter<T> extends BaseAdapter {
	
	protected LayoutInflater mInflater;
	protected List<T> mDatas;
	protected Context mContext;
	protected int layoutId;
	

	public CommonAdapter(List<T> mDatas, Context context, int layoutId) {
		this.mDatas = mDatas;
		this.mContext = context;
		if(context == null){
			Logger.show("**CommonAdapter**", "contxt is null");
		}
		mInflater = LayoutInflater.from(context);
		this.layoutId = layoutId;
	}

	@Override
	public int getCount() {
		return mDatas.size();
	}

	@Override
	public T getItem(int position) {
		return mDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		ViewHolder holder = ViewHolder.get(mContext, parent, convertView, layoutId, position);
		convert(holder, getItem(position));
		return holder.getConvertView();
	}
	
	
	
	public abstract void convert(ViewHolder holder, T t);


	public void setDatas(List<T> mDatas) {
		this.mDatas = mDatas;
	}
	

}
