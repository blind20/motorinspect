package com.shsy.motoinspect.adapter;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.shsy.motoinspect.CommonConstants;
import com.shsy.motoinspect.entity.CarPhotoEntity;
import com.shsy.motoinspect.utils.Logger;
import com.shsy.motoinspect.utils.SharedPreferenceUtils;
import com.shsy.motorinspect.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.TextView;

public class MultiListAdapter extends BaseAdapter {
	
	public static final int TYPE_1 = 1;
	public static final int TYPE_2 = 2;
	
	private Context mContext;
	private List<Map<String,Object>> mList;
	
	public MultiListAdapter(Context context,List<Map<String,Object>> list) {
		super();
		this.mContext = context;
		this.mList = list;
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	public void setData(List<Map<String,Object>> mCopyList){
		this.mList= mCopyList;
	}
	
	
	@Override
	public Map<String,Object> getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public int getItemViewType(int position) {
		return position%2==0?TYPE_2:TYPE_1;
	}
	
	@Override
	public int getViewTypeCount() {
		return 1;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder1 viewHolder1;
		ViewHolder2 viewHolder2;
		if(getItemViewType(position) == TYPE_1){
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(R.layout.item_setting, null);
				viewHolder1 = new ViewHolder1();
				viewHolder1.text1 = (TextView) convertView.findViewById(R.id.tv_name1);
				viewHolder1.text2 = (TextView) convertView.findViewById(R.id.tv_name2);
				convertView.setTag(viewHolder1);
			}else{
				viewHolder1 = (ViewHolder1) convertView.getTag();
			}
			Map<String,Object> map1 = mList.get(position);
			String itemText1="";
			for(String key : map1.keySet()){
				itemText1 = key;
				break;
			}
			viewHolder1.text1.setText(itemText1);
			viewHolder1.text2.setText((String) map1.get(itemText1));
		}else if(getItemViewType(position) == TYPE_2){
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(R.layout.item_setting2, null);
				viewHolder2 = new ViewHolder2();
				viewHolder2.text = (TextView) convertView.findViewById(R.id.tv_setting_name);
				viewHolder2.switchBtn = (Switch) convertView.findViewById(R.id.switchbtn);
				convertView.setTag(viewHolder2);
			}else{
				viewHolder2 = (ViewHolder2) convertView.getTag();
			}
			Map<String,Object> map2 = mList.get(position);
			String setting="";
			for(String key : map2.keySet()){
				setting = key;
				break;
			}
			viewHolder2.text.setText(setting);
			viewHolder2.switchBtn.setChecked((Boolean) map2.get(setting));
			viewHolder2.switchBtn.setOnCheckedChangeListener(new MyListener(position));
		}
		
		return convertView;
	}
	
	class ViewHolder1 {
        TextView text1;
        TextView text2;
    }

    class ViewHolder2 {
        TextView text;
        Switch switchBtn;
    }

    private class MyListener implements CompoundButton.OnCheckedChangeListener{
		int mPos;
		public MyListener(int position){  
			mPos = position;  
			Logger.show("MyListener", "MyListener="+mPos);
		}
		
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			SharedPreferenceUtils.put(mContext, CommonConstants.IS_SELECT_LINE, isChecked);
			mList.get(mPos).put(mContext.getString(R.string.system_setting_0), isChecked);
			setData(mList);
			notifyDataSetChanged();
		}
	}
}
