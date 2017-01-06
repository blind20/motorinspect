package com.shsy.motoinspect.ui.fragment;


import com.shsy.motoinspect.BaseFragment;
import com.shsy.motoinspect.CommonConstants;
import com.shsy.motoinspect.utils.Logger;
import com.shsy.motoinspect.utils.SharedPreferenceUtils;
import com.shsy.motoinspect.utils.ToastUtils;
import com.shsy.motorinspect.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SettingServerFrm extends BaseFragment {

	private EditText et_ip;
	private EditText et_port;
	private Button btn_save;
	private boolean isRecycle = false;
	
	public String ip;
	public String port;
	
	public SettingServerFrm(){
	}
	
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Logger.show(getClass().getName(), "onCreateView");
		if(savedInstanceState != null ){
			isRecycle = savedInstanceState.getBoolean("isRecycle");
			if(isRecycle){
//				ArrayList list = savedInstanceState.getParcelableArrayList("msubdatas");
//				mSubDatas = (List<CheckItemEntity>) list.get(0);
			}
		}
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	


	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		isRecycle = true;
	}
	

	@Override
	public void initParam() {
		
		findView();
		setView();
		onclick();
		
	}


	private void onclick() {
		btn_save.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ToastUtils.showToast(mActivity, "ÒÑ±£´æ", 0);
				ip = et_ip.getText().toString().trim();
				port = et_port.getText().toString().trim();
				SharedPreferenceUtils.put(mActivity, CommonConstants.IP, ip);
				SharedPreferenceUtils.put(mActivity, CommonConstants.PORT, port);
				getActivity().finish();
			}
		});
	}



	@Override
	public int getLayoutResID() {
		return R.layout.frm_setting_ip;
	}
	
	
	private void findView() {
		et_ip = (EditText) mRootView.findViewById(R.id.et_ip);
		et_port = (EditText) mRootView.findViewById(R.id.et_port);
		btn_save = (Button) mRootView.findViewById(R.id.btn_save);
	}
	
	

	private void setView() {
		
		ip = (String) SharedPreferenceUtils.get(mActivity, CommonConstants.IP, "");
		port = (String) SharedPreferenceUtils.get(mActivity, CommonConstants.PORT, "");
		
		if("".equals(ip)||"".equals(port)){
			return;
		}else{
			et_ip.setText(ip);
			et_port.setText(port);
		}
	}
	
	
}
