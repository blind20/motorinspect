package com.shsy.motoinspect.common;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ExpandViewHolder {
	private SparseArray<View> mViews;
	private View mConvertView;
	private int mPosition;

	public ExpandViewHolder(Context context , ViewGroup parent, int layoutId, int position) {
		this.mPosition = position;
		this.mViews = new SparseArray<View>();
		mConvertView = LayoutInflater.from(context).inflate(layoutId, parent,false);
		mConvertView.setTag(this);
	}
	
	public static ExpandViewHolder get(Context context , ViewGroup parent, 
			View convertView ,int layoutId, int position){
		
		if(convertView == null){
			return new ExpandViewHolder(context,parent,layoutId,position);
		}else{
			ExpandViewHolder holder = (ExpandViewHolder) convertView.getTag();
			holder.mPosition = position;
			return holder;
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T extends View>T getView(int viewId){
		View view = mViews.get(viewId);
		if(view == null){
			view = mConvertView.findViewById(viewId);
			mViews.put(viewId, view);
		}
		return (T) view;
	}

	public View getConvertView() {
		return mConvertView;
	}
	
	

}
