package com.shsy.motoinspect.ui.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.shsy.motoinspect.BaseFragment;
import com.shsy.motoinspect.CommonConstants;
import com.shsy.motoinspect.common.CommonAdapter;
import com.shsy.motoinspect.common.TitleBarView;
import com.shsy.motoinspect.common.ViewHolder;
import com.shsy.motoinspect.entity.CarListInfoEntity;
import com.shsy.motoinspect.network.ListCarInfoCallback;
import com.shsy.motoinspect.network.MyHttpUtils;
import com.shsy.motoinspect.ui.activity.MainActivity;
import com.shsy.motoinspect.ui.fragment.OuterCheckFrm.OnOuterCheckBackListener;
import com.shsy.motoinspect.utils.Logger;
import com.shsy.motoinspect.utils.SharedPreferenceUtils;
import com.shsy.motoinspect.utils.ToastUtils;
import com.shsy.motoinspect.utils.ToolUtils;
import com.shsy.motorinspect.R;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import android.Manifest.permission;
import android.R.bool;
import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import okhttp3.Call;

public class QDZCheckFrm extends BaseFragment implements View.OnClickListener{
	
	private boolean isRecycle =false;
	
	private TitleBarView mTitleBarView;
	
	private static final int REFRESH_COMPLETE = 0X110;  
    private SwipeRefreshLayout mSwipeLayout;
	
	private String TAG = getClass().getName();
	
	private ProgressDialog mProgressDlg;
	
	private FragmentTransaction ft;
	private Spinner sp_devices;
	public Button btn_qdz_dw;
	public Button btn_qdz_toline;
	public Button btn_finish;
	private CarListInfoEntity carInfo;
	
	private ArrayAdapter<String> deviceAdapter;
	
	
	@Override
	public int getLayoutResID() {
		return R.layout.frm_qdz_check;
	}

	@Override
	public void initParam() {
		initView();
		initDatas();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		if(bundle!=null){
			carInfo = bundle.getParcelable("carinfo");
		}
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(savedInstanceState != null ){
			isRecycle = savedInstanceState.getBoolean("isRecycle");
			if(isRecycle){
				Logger.show(getTag(), "savedInstanceState");
			}
		}
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	
	@Override
	public void onResume() {
		super.onResume();
		mProgressDlg.dismiss();
//		onRefresh();
	}
	

	
	
	private void initView() {
		mSwipeLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.id_swipe_ly);
		mTitleBarView = new TitleBarView(mActivity);
		mTitleBarView = (TitleBarView) mRootView.findViewById(R.id.titlebar);
		mTitleBarView.setCommonTitle(View.VISIBLE, View.VISIBLE, View.GONE);
		mTitleBarView.setTitle(carInfo.getHphm());
		mTitleBarView.setBtnLeftOnclickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				backKeyDown();
			}
		});
		sp_devices = (Spinner)mRootView.findViewById(R.id.sp_devices);
		btn_qdz_dw = (Button)mRootView.findViewById(R.id.btn_qdz_dw);
		btn_qdz_dw.setOnClickListener(this);
		btn_qdz_toline = (Button)mRootView.findViewById(R.id.btn_qdz_toline);
		btn_qdz_toline.setOnClickListener(this);
		btn_finish = (Button)mRootView.findViewById(R.id.btn_finish);
		btn_finish.setOnClickListener(this);
		mProgressDlg = new ProgressDialog(mActivity);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_qdz_dw:
				dwBtnClick();
				break;
			case R.id.btn_qdz_toline:
				String url = ToolUtils.upQDZ(mActivity);
				int deviceId = zIdList.get(sp_devices.getSelectedItemPosition());
				upQDZ(url,deviceId,carInfo.getId());
				break;
			case R.id.btn_finish:
				backKeyDown();
				break;
		}
	}
	
	
	private void initDatas() {
		String url = ToolUtils.getZ1Device(mActivity);
		getDevicesNetwork(url);
	}
	
	private List<Integer> zIdList = new ArrayList<Integer>();
	private List<String>  zNameList = new ArrayList<String>();
	
	private void getDevicesNetwork(String url) {
		MyHttpUtils.getInstance(getContext()).postHttpByParam(url, new HashMap<String, String>(), new StringCallback() {
			@Override
			public void onResponse(String response, int id) {
				zIdList.clear();
				zNameList.clear();
				try {
					if(!TextUtils.isEmpty(response)){
						JSONArray ja = new JSONArray(response);
						if(ja!=null && ja.length()>0){
							for(int i=0;i<ja.length();i++){
								JSONObject jo = ja.getJSONObject(i);
								Integer zid = jo.getInt("id");
								String name = jo.getString("name");
								if(TextUtils.isEmpty(name)){
									name = "设备ID"+zid;
								}
								zIdList.add(zid);
								zNameList.add(name);
							}
							spinnerBindData(zNameList);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			@Override
			public void onError(Call call, Exception e, int id) {
				ToastUtils.showToast(mActivity, "网络通信失败="+e.getMessage(), Toast.LENGTH_LONG);
				e.printStackTrace();
			}
		});
	}
	
	private void spinnerBindData(List<String> devices){
		deviceAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item,
				devices.toArray(new String[devices.size()]));
		deviceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp_devices.setAdapter(deviceAdapter);
	}
	
	
	private void qdzdwNetwork(String url,Integer zId,final Integer zw) {
		Logger.show(getTag(), "z1dwNetwork="+zId+";zw="+zw);
		Map<String, String> map = new HashMap<String, String>();
		map.put("deviceId", String.valueOf(zId));
		map.put("zw", String.valueOf(zw));
		MyHttpUtils.getInstance(getContext()).postHttpByParam(url, map, new StringCallback() {
			@Override
			public void onResponse(String response, int id) {
				try {
					if(!TextUtils.isEmpty(response)){
						JSONObject jo = new JSONObject(response);
						if(jo!=null){
							Integer state = jo.getInt("state");;
							if(CommonConstants.STATAS_SUCCESS==state){
								btn_qdz_dw.setBackgroundResource(R.drawable.login_btn_disenable);
								btn_qdz_dw.setEnabled(false);
							}
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
			}
			@Override
			public void onError(Call call, Exception e, int id) {
				ToastUtils.showToast(mActivity, "网络通信失败="+e.getMessage(), Toast.LENGTH_LONG);
				e.printStackTrace();
			}
		});
	}
	
	//轴位zw：前轴到位0后轴到位1
	protected void dwBtnClick(){
		String url = ToolUtils.z1dw(mActivity);
		int zId = zIdList.get(sp_devices.getSelectedItemPosition());
		qdzdwNetwork(url, zId, 1);
	}

	
	
	
	private void upQDZ(String url,Integer deviceId,String vehId) {
		if(deviceId ==null || TextUtils.isEmpty(vehId)){
			ToastUtils.showToast(getContext(), "设备ID=空;或车辆vehID=空", Toast.LENGTH_LONG);
		}
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("deviceId", String.valueOf(deviceId));
		map.put("testVehId", vehId);
		Logger.show(getTag(), "deviceId="+deviceId+";testVehId="+vehId);
		
		MyHttpUtils.getInstance(getContext()).postHttpByParam(url, map, new StringCallback() {
			@Override
			public void onResponse(String response, int id) {
				Logger.show(getTag(), "response="+response);
				try {
					if(!TextUtils.isEmpty(response)){
						JSONObject jo = new JSONObject(response);
						if(jo!=null){
							Integer state = jo.getInt("state");
							if(CommonConstants.STATAS_SUCCESS==state){
								new AlertDialog.Builder(mActivity).setMessage("引车上线成功")
								.setPositiveButton("确定", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
									}
								}).show();
							}
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
			}
			@Override
			public void onError(Call call, Exception e, int id) {
				ToastUtils.showToast(mActivity, "网络通信失败="+e.getMessage(), Toast.LENGTH_LONG);
				e.printStackTrace();
			}
		});
	}
	
	private void backKeyDown(){
		FragmentManager manager = getActivity().getSupportFragmentManager();
		if(manager.getBackStackEntryCount() != 0){
			manager.popBackStack();
		}
	}
	
}
