package com.shsy.motoinspect.common;

import java.util.List;

import com.shsy.motoinspect.utils.Logger;
import com.shsy.motorinspect.R;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public abstract class ExpandCommonAdapter<T> extends BaseExpandableListAdapter  {
	
	protected LayoutInflater mInflater;
	protected List<T> mGroup;
	private List<List<T>> mItemList;
	protected Context mContext;
	protected int layoutId;
	

	public ExpandCommonAdapter(List<T> mGroup,List<List<T>> mItemList, Context context, int layoutId) {
		this.mGroup = mGroup;
		this.mItemList = mItemList;
		this.mContext = context;
		if(context == null){
			Logger.show("**CommonAdapter**", "contxt is null");
		}
		mInflater = LayoutInflater.from(context);
		this.layoutId = layoutId;
	}

	@Override
	public int getGroupCount() {
		return mGroup.size();
	}
	@Override
	public int getChildrenCount(int groupPosition) {
		return mItemList.get(groupPosition).size();
	}

	@Override
	public T getGroup(int groupPosition) {
		return mGroup.get(groupPosition);
	}
	
	@Override
	public T getChild(int groupPosition, int childPosition) {
		return mItemList.get(groupPosition).get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}
	
	 @Override
	public boolean hasStableIds() {
	    return false;
	}
	
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		ExpandViewHolder holder = ExpandViewHolder.get(mContext, parent, convertView, layoutId, groupPosition);
		convertGroup(holder, getGroup(groupPosition));
		return holder.getConvertView();
	}
	
	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
			ViewGroup parent) {
		return null;
	}
	
	
	
	public abstract void convertGroup(ExpandViewHolder holder, T t);
	
	public abstract void convertChild(ExpandViewHolder holder, T t);


	public void setGroupDatas(List<T> mGroup) {
		this.mGroup = mGroup;
	}
	public void setChildDatas(List<List<T>> mItemList) {
		this.mItemList = mItemList;
	}
	

}
