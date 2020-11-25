package com.shsy.motoinspect.adapter;

import java.io.File;
import java.util.List;

import com.shsy.motoinspect.entity.VideoInfo;
import com.shsy.motoinspect.utils.DensityUtil;
import com.shsy.motorinspect.R;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class GridVideoAdapter extends BaseAdapter {

	private List<VideoInfo> mList;
	private Context context;
	private int screenW;
	
	public GridVideoAdapter(List<VideoInfo> list, Context context) {
		super();
		this.mList = list;
		this.context = context;
		this.screenW = (context.getResources().getDisplayMetrics().widthPixels 
				- DensityUtil.dip2px(context,20))/4;
	}
	
	public void setData(List<VideoInfo> mCopyList){
		this.mList= mCopyList;
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public VideoInfo getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.griditem_photo, null);
			convertView.setLayoutParams(new GridView.LayoutParams(screenW, screenW));
			
			viewHolder = new ViewHolder();
			
			viewHolder.imageView = (ImageView) convertView.findViewById(R.id.iv_photo);
			viewHolder.imageDelete = (ImageView) convertView.findViewById(R.id.iv_del);
			viewHolder.textview = (TextView) convertView.findViewById(R.id.tv_photoname);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		VideoInfo videoInfo = getItem(position);
		if(videoInfo.getThumbnailBmp()!=null){
			viewHolder.imageView.setImageBitmap(videoInfo.getThumbnailBmp());
		}
		viewHolder.textview.setText(videoInfo.getVideoName());
		
		
		if(TextUtils.isEmpty(mList.get(position).getVideoFilePath())){
			viewHolder.imageDelete.setVisibility(View.INVISIBLE);
		}else{
			viewHolder.imageDelete.setVisibility(View.VISIBLE);
		}
		viewHolder.imageDelete.setTag(position);
		viewHolder.imageDelete.setOnClickListener(new MyListener(position));
	
		return convertView;
	}

	
	class ViewHolder{
		ImageView imageView;
		ImageView imageDelete;
		TextView textview;
	}
	
	private class MyListener implements OnClickListener{
		int mPos;
		public MyListener(int position){  
			mPos = position;  
		}
		
		@Override
		public void onClick(View v) {
			if(mPos == getCount()-1){
				return;
			}
			File file = new File(mList.get(mPos).getVideoFilePath());
			if(file.exists()){
				file.delete();
			}
			mList.remove(mPos);
			setData(mList);
			notifyDataSetChanged();
		}
	}
}
