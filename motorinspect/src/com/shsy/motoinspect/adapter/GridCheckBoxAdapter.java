package com.shsy.motoinspect.adapter;

import java.util.List;

import com.shsy.motoinspect.entity.JyxmInfo;
import com.shsy.motoinspect.utils.Logger;
import com.shsy.motorinspect.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class GridCheckBoxAdapter extends BaseAdapter {

	private List<JyxmInfo> mList;
	private LayoutInflater inflater = null;  
	
	public GridCheckBoxAdapter(List<JyxmInfo> list, Context context) {
		super();
		this.mList = list;
		inflater = LayoutInflater.from(context);  
	}
	
	public void setData(List<JyxmInfo> mCopyList){
		this.mList= mCopyList;
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public JyxmInfo getItem(int position) {
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
			convertView = inflater.inflate(R.layout.griditem_checkbox_recheck, null);
			viewHolder = new ViewHolder();
			
			viewHolder.cb = (CheckBox) convertView.findViewById(R.id.item_cb);
			viewHolder.jyxm = (TextView) convertView.findViewById(R.id.item_jyxm);
			viewHolder.jyxmzl = (TextView) convertView.findViewById(R.id.item_jyxmzl);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.cb.setChecked(mList.get(position).getSelected()); 
		viewHolder.cb.setText(mList.get(position).getJyxm());
		viewHolder.jyxm.setText(mList.get(position).getJyxm());
		viewHolder.jyxmzl.setText(mList.get(position).getJyxmzl());
		
		return convertView;
	}

	
	public class ViewHolder{
		public CheckBox cb;    
        public TextView jyxm;    
        public TextView jyxmzl; 
	}
	
	
}
