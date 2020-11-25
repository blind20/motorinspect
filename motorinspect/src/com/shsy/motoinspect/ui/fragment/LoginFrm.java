package com.shsy.motoinspect.ui.fragment;


import org.json.JSONException;
import org.json.JSONObject;

import com.shsy.motoinspect.BaseFragment;
import com.shsy.motoinspect.CommonConstants;
import com.shsy.motoinspect.utils.Logger;
import com.shsy.motoinspect.utils.SharedPreferenceUtils;
import com.shsy.motoinspect.utils.ToastUtils;
import com.shsy.motoinspect.utils.ToolUtils;
import com.shsy.motorinspect.R;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import okhttp3.Call;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class LoginFrm extends BaseFragment {

	private EditText et_username;
	private EditText et_password;
	private Button btn_login;
	private boolean isRecycle = false;
	
	private String usrname;
	private String pwd;
	
	OnLoginListener mCallback;
	public interface OnLoginListener{
		public void onLogin();
	}
	
	public LoginFrm(){
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
	
	
//	@Override
//	public void onAttach(Activity activity) {
//		super.onAttach(activity);
//		try {
//			mCallback = (OnLoginListener) mActivity;
//		} catch (ClassCastException e) {
//			e.printStackTrace();
//			throw new ClassCastException(mActivity.toString()
//					+ " must implement OnLoginListener");
//		}
//	}
	
	

	@Override
	public void initParam() {
		findView();
		setView();
		onclick();
	}


	private void onclick() {
		btn_login.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				usrname = et_username.getText().toString().trim();
				pwd = et_password.getText().toString().trim();
				
				Login();
			}
		});
	}
	



	private void Login() {
		String loginUrl = ToolUtils.loginUrl(mActivity);
		if(TextUtils.isEmpty(loginUrl)){
			return ;
		}
		
		OkHttpUtils.post()
					.url(loginUrl)
					.addParams("userName", usrname)
					.addParams("password", pwd)
					.build()
					.execute(new StringCallback() {
						
						@Override
						public void onResponse(String response, int id) {
							try {
								if(TextUtils.isEmpty(response)){
									ToastUtils.showToast(mActivity, "返回空", Toast.LENGTH_LONG);
									return;
								}
								Logger.show("onResponse", "resp="+response);
								JSONObject jo = new JSONObject(response);
								Integer state = (Integer) jo.get("state");
								String sessionId = jo.getString("session");
								String realName="";
								if(jo.getJSONObject("data")!=null){
									realName = jo.getJSONObject("data").getString("realName");
								}
								
								if(CommonConstants.STATAS_SUCCESS==state){
									SharedPreferenceUtils.put(mActivity, CommonConstants.USERNAME, usrname);
									SharedPreferenceUtils.put(mActivity, CommonConstants.PWD, pwd);
									SharedPreferenceUtils.put(mActivity, CommonConstants.REALNAME, realName);
									SharedPreferenceUtils.put(mActivity, CommonConstants.JSESSIONID, sessionId);
									Intent intent = new Intent();
									intent.putExtra(CommonConstants.USERNAME, usrname);
									getActivity().setResult(CommonConstants.REQUEST_LOGIN, intent);
									getActivity().finish();
									Logger.show("session id", "session id = "+sessionId);
									return;
								}
								if(CommonConstants.STATAS_FAIL==state){
									ToastUtils.showToast(mActivity, "用户名或密码错误,登陆失败", Toast.LENGTH_SHORT);
								}
								if(CommonConstants.STATAS_INVALID==state){
									ToastUtils.showToast(mActivity, "会话失效,重新登陆", Toast.LENGTH_SHORT);
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
							
						}
						
						@Override
						public void onError(Call call, Exception e, int id) {
							ToastUtils.showToast(mActivity, "网络问题", 0);
							e.printStackTrace();
						}
					});
		
	}
	
	

	@Override
	public int getLayoutResID() {
		return R.layout.frm_setting_login;
	}
	
	
	private void findView() {
		et_username = (EditText) mRootView.findViewById(R.id.et_username);
		et_password = (EditText) mRootView.findViewById(R.id.et_password);
		btn_login = (Button) mRootView.findViewById(R.id.btn_login);
	}

	private void setView() {
		usrname = (String) SharedPreferenceUtils.get(mActivity, CommonConstants.USERNAME, "");
		pwd = (String) SharedPreferenceUtils.get(mActivity, CommonConstants.PWD, "");
		
		if("".equals(usrname)||"".equals(pwd)){
			return;
		}else{
			et_username.setText(usrname);
			et_password.setText(pwd);
		}
	}
	
}
