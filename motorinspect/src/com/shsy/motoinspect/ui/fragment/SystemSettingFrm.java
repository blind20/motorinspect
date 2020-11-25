package com.shsy.motoinspect.ui.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.shsy.motoinspect.BaseFragment;
import com.shsy.motoinspect.CommonConstants;
import com.shsy.motoinspect.adapter.MultiListAdapter;
import com.shsy.motoinspect.common.TitleBarView;
import com.shsy.motoinspect.utils.Logger;
import com.shsy.motoinspect.utils.SharedPreferenceUtils;
import com.shsy.motoinspect.utils.ToastUtils;
import com.shsy.motorinspect.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class SystemSettingFrm extends BaseFragment {
	
	private ListView mListView;
	
	private TitleBarView mTitleBarView;
	
    private MultiListAdapter adapter;
	
	private String TAG = getClass().getName();
	
	
	private List<Map<String,Object>> mList;
	
	private boolean isSelectLine;
	private String mStation;
	
	@Override
	public int getLayoutResID() {
		return R.layout.listview_vehinfo;
	}

	@Override
	public void initParam() {
		initView();
		initDatas();
		viewSetAdapter();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getArguments()!= null){
			String title = getArguments().getString(NavigationFrm.TO_VEHLISTATY);
		}
	}
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	

	
	
	private void initView() {
		mTitleBarView = new TitleBarView(mActivity);
		mTitleBarView = (TitleBarView) mRootView.findViewById(R.id.titlebar);
		mTitleBarView.setCommonTitle(View.VISIBLE, View.VISIBLE, View.GONE);
		mTitleBarView.setTitle("系统设置");
		mTitleBarView.setBtnLeftOnclickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentManager manager = getActivity().getSupportFragmentManager();
				if(manager.getBackStackEntryCount() != 0){
					manager.popBackStack();
				}
			}
		});
		mListView = (ListView) mRootView.findViewById(R.id.lv_carinspect);
	}
	
	private void initDatas() {
		mList = new ArrayList<Map<String,Object>>();
		isSelectLine = (Boolean) SharedPreferenceUtils.get(mActivity, CommonConstants.IS_SELECT_LINE, false);
		mStation = (String) SharedPreferenceUtils.get(mActivity, CommonConstants.STATION, "检测站");
		for(int i=0;i<2;i++){
			Map<String,Object> map = new HashMap<String, Object>();
			switch (i) {
				case 0:
					map.put(getString(R.string.system_setting_0), isSelectLine);
					break;
				case 1:
					map.put(getString(R.string.system_setting_1), mStation);
					break;
				default:
					break;
			}
			mList.add(map);
		}
	}
	
	
	

	private void viewSetAdapter() {
		adapter = new MultiListAdapter(mActivity, mList);
		mListView.setAdapter(adapter);
		mListView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					final int position, long id) {
				Map<String,Object> map = mList.get(position);
				/*if(position%2==0){
					String setting="";
					for(String key : map.keySet()){
						setting = key;
						break;
					}
					boolean value = (Boolean) map.get(setting);
					mList.get(position).put(setting, !value);
					Logger.show("viewSetAdapter", "viewSetAdapter="+!value);
					adapter.setData(mList);
					adapter.notifyDataSetChanged();
				}*/
				if(position==1){
				    LayoutInflater inflater = LayoutInflater.from(mActivity);
				    View layout = inflater.inflate(R.layout.dialog_edit, null);
				    final EditText et = (EditText) layout.findViewById(R.id.editText);
				    new AlertDialog.Builder(mActivity)  
				    .setView(layout)  
				    .setPositiveButton("确定", new DialogInterface.OnClickListener() {  
				    @Override  
				    public void onClick(DialogInterface dialog, int which) {  
				    	String station = et.getText().toString().trim();
				    	if(!TextUtils.isEmpty(station)){
				    		SharedPreferenceUtils.put(mActivity, CommonConstants.STATION, station); 
				    		mList.get(position).put(getString(R.string.system_setting_1),station);
				    	}
				    }  
				    }).setNegativeButton("取消", null).create().show();  
				}
				
	    		adapter.setData(mList);
				adapter.notifyDataSetChanged();
			}

			
		});
	}
	

	
}
