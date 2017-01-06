package com.shsy.motoinspect.common;

import com.shsy.motoinspect.ui.fragment.OuterItemFailReasonFrm;
import com.shsy.motoinspect.utils.Logger;
import com.shsy.motorinspect.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewHolder {
	private SparseArray<View> mViews;
	private View mConvertView;
	private int mPosition;

	public ViewHolder(Context context , ViewGroup parent, int layoutId, int position) {
		this.mPosition = position;
		this.mViews = new SparseArray<View>();
		mConvertView = LayoutInflater.from(context).inflate(layoutId, parent,false);
		mConvertView.setTag(this);
	}
	
	public static ViewHolder get(Context context , ViewGroup parent, 
			View convertView ,int layoutId, int position){
		
		if(convertView == null){
			return new ViewHolder(context,parent,layoutId,position);
		}else{
			ViewHolder holder = (ViewHolder) convertView.getTag();
			holder.mPosition = position;
			return holder;
		}
	}
	
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
	
	
	
	
	public ViewHolder setText(int viewId, String text){
		
		TextView tv = getView(viewId);
		tv.setText(text);
		return this;
	}
	
	public ViewHolder setTextColor(int viewId, int color){
		TextView tv = getView(viewId);
		tv.setTextColor(color);
		return this;
	}
	
	public ViewHolder setButtonListen(int viewId, View.OnClickListener listener){
		Button btn = getView(viewId);
		btn.setOnClickListener(listener);
		return this;
	}
	
	public ViewHolder setButtonText(int viewId, String text){
		
		Button btn = getView(viewId);
		btn.setText(text);
		return this;
	}
	
	
	public ViewHolder setViewVisibleOrGone(int viewId, int visible){
		getView(viewId).setVisibility(visible);
		return this;
	}
	
	
	public int getPosition(){
		return mPosition;
	}
	
	
	public ViewHolder setImageResource(int viewId,int resId){
		
		ImageView iv = getView(viewId);
		iv.setImageResource(resId);
		return this;
	}
	
	public ViewHolder setImageDrawable(int viewId,Drawable drawable){
		
		ImageView iv = getView(viewId);
		iv.setImageDrawable(drawable);
		return this;
	}
	
	public ImageView getImageView(int viewId){
		return (ImageView)getView(viewId);
	}

}
