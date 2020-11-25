package com.shsy.motoinspect.ui.fragment;

import java.util.ArrayList;
import java.util.Collections;
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
import com.shsy.motoinspect.entity.VehCheckProcess;
import com.shsy.motoinspect.network.ListCarInfoCallback;
import com.shsy.motoinspect.network.MyHttpUtils;
import com.shsy.motoinspect.network.QDZListCarInfoCallback;
import com.shsy.motoinspect.ui.fragment.PullCarToLineFrm.SingleClick;
import com.shsy.motoinspect.ui.fragment.ZbzlCarListFrm.OnZbzlCarListClick;
import com.shsy.motoinspect.utils.Logger;
import com.shsy.motoinspect.utils.SharedPreferenceUtils;
import com.shsy.motoinspect.utils.ToastUtils;
import com.shsy.motoinspect.utils.ToolUtils;
import com.shsy.motorinspect.R;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import okhttp3.Call;
import android.widget.AdapterView;
import android.widget.ListView;

public class DownLineWithReUpLineFrm extends BaseFragment implements View.OnClickListener {
	
	private List<ReUpDownCarInfo> mCarList;
	private ListView mListView;
	private boolean isRecycle =false;
	
	private TitleBarView mTitleBarView;
	
	private static final int REFRESH_COMPLETE = 0X110;  
//    private SwipeRefreshLayout mSwipeLayout;
    private CommonAdapter<ReUpDownCarInfo> adapter;
	
	private String TAG = getClass().getName();
	
	private ProgressDialog mProgressDlg;
	
	private String mTitle;
	private Button btn_downcarlist;
	private Button btn_reupcarlist;
	private boolean isOnCreate =false;
	private Integer mPosition;
	
	private Handler mHandler = new Handler(){  
        public void handleMessage(android.os.Message msg)  
        {  
            switch (msg.what)  
            {  
	            case REFRESH_COMPLETE: 
//	        		getCarListNetwork();
	            	if(!mCarList.isEmpty()){
	            		adapter.notifyDataSetChanged();
	            	}
//	                mSwipeLayout.setRefreshing(false);  
	                break;  
	                
            }  
        };  
	}; 
	
	@Override
	public int getLayoutResID() {
		return R.layout.frm_downwithreup;
	}

	@Override
	public void initParam() {
		initView();
		mCarList = new ArrayList<ReUpDownCarInfo>();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getArguments()!= null){
			mTitle = getArguments().getString(NavigationFrm.TO_VEHLISTATY);
		}
		isOnCreate = true;
	}
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(savedInstanceState != null ){
			isRecycle = savedInstanceState.getBoolean("isRecycle");
			if(isRecycle){
				ArrayList list = savedInstanceState.getParcelableArrayList("mcarlist");
				if(list.size()>0){
					mCarList = (List<ReUpDownCarInfo>) list.get(0);
				}
				
			}
		}
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	
	@Override
	public void onResume() {
		super.onResume();
		if(!isOnCreate){
			mProgressDlg.dismiss();
//			onRefresh();
			isOnCreate = false;
		}
		
	}
	

	
	
	private void initView() {
//		mSwipeLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.id_swipe_ly);
		mTitleBarView = new TitleBarView(mActivity);
		mTitleBarView = (TitleBarView) mRootView.findViewById(R.id.titlebar);
		mTitleBarView.setCommonTitle(View.VISIBLE, View.VISIBLE, View.GONE);
		mTitleBarView.setTitle(mTitle);
		
		mListView = (ListView) mRootView.findViewById(R.id.lv_carinspect);
//		mSwipeLayout.setOnRefreshListener(this);
		
		mTitleBarView.setBtnLeftOnclickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentManager manager = getActivity().getSupportFragmentManager();
				if(manager.getBackStackEntryCount() != 0){
					manager.popBackStack();
				}
			}
		});
		
		btn_downcarlist = (Button) mRootView.findViewById(R.id.btn_downcarlist);
		btn_reupcarlist = (Button) mRootView.findViewById(R.id.btn_reupcarlist);
		btn_downcarlist.setOnClickListener(this);
		btn_reupcarlist.setOnClickListener(this);
		
		mProgressDlg = new ProgressDialog(mActivity);
	}
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_downcarlist:
			getCarListNetwork(0);
			break;

		case R.id.btn_reupcarlist:
			getCarListNetwork(1);
			break;
		}
	}
	
	
	private void getCarListNetwork(final Integer status) {
		String url = ToolUtils.getCheckQueueVeh(mActivity);
		Map<String, String> map = new HashMap<String, String>();
		map.put("status", Integer.toString(status));
		MyHttpUtils.getInstance(mActivity).postHttpByParam(url, map, new StringCallback() {
			@Override
			public void onResponse(String response, int id) {
				mCarList = parseResJson2Car(response);
				if(mCarList!=null && mCarList.size()>0){
					viewSetAdapter(mCarList,status);
				}else{
					//列表清空
					adapter.setDatas(new ArrayList<ReUpDownCarInfo>());
					adapter.notifyDataSetChanged();
				}
			}
			
			@Override
			public void onError(Call call, Exception e, int id) {
				ToastUtils.showToast(mActivity, "网络响应失败"+e.getMessage(), Toast.LENGTH_LONG);
				e.printStackTrace();
			}
		});
	}
	
	

	protected List<ReUpDownCarInfo> parseResJson2Car(String response) {
		List<ReUpDownCarInfo> list= new ArrayList<ReUpDownCarInfo>();
		if(TextUtils.isEmpty(response)){
			return null;
		}
		try {
			JSONArray ja = new JSONArray(response);
			if(ja.length()<1){
				return null;
			}
			for(int i=0;i<ja.length();i++){
				ReUpDownCarInfo car = new ReUpDownCarInfo();
				JSONObject jo = ja.getJSONObject(i);
				car.setJylsh(jo.getString("jylsh"));
				car.setHphm(jo.getString("hphm"));
				car.setHpzl(jo.getString("hpzl"));
				car.setJcxdh(jo.getInt("jcxdh"));
				list.add(car);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Logger.show(getTag(), "onSaveInstanceState");
		isRecycle = true;
	}
	
	

	private void viewSetAdapter(final List<ReUpDownCarInfo> list,final Integer status) {
		adapter = new CommonAdapter<ReUpDownCarInfo>(list,mActivity,R.layout.item_reupdownline) {

			@Override
			public void convert(final ViewHolder holder, final ReUpDownCarInfo t) {
				holder.setText(R.id.tv_hphm, t.getHphm())
					  .setText(R.id.tv_hpzl, convertCode(t.getHpzl()))
					  .setText(R.id.tv_lsh, t.getJylsh())
					  .setText(R.id.tv_checkline, t.getJcxdh()+"号线");
				if(0==status){
					holder.setButtonText(R.id.btn_reupdown, "退线下线");
				}else{
					holder.setButtonText(R.id.btn_reupdown, "重新上线");
				}
				holder.setButtonListen(R.id.btn_reupdown, new OnClickListener() {
					@Override
					public void onClick(View v) {
						mPosition = holder.getPosition();

						if (SingleClick.isSingle()) {
							Logger.show("PullCarTOLineFrm", "间隔短于3秒算同一次点击");
							ToastUtils.showToast(mActivity, "间隔短于3秒算同一次点击", Toast.LENGTH_SHORT);
						} else {
							String msg="";
							if(0==status){
								msg = "是否确定下线";
							}else{
								msg = "是否确定重新上线";
							}
							new AlertDialog.Builder(mActivity).setMessage(t.getHphm()+msg)
							.setPositiveButton("确定", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									reUpDownLineNetwork(status, mPosition);
								}
							}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
								}
							}).show();
							
						}
					}
				});
			}
		};
		mListView.setAdapter(adapter);
	}
	
	
	
	private void reUpDownLineNetwork(final Integer status,final Integer position) {
		String url=null;
		if(status==0){
			url = ToolUtils.downLine(mActivity);
		}else if(status==1){
			url = ToolUtils.reUpLine(mActivity);
		}else{
			ToastUtils.showToast(mActivity, "下线还是重新上线未知", Toast.LENGTH_SHORT);
			return;
		}
		final ReUpDownCarInfo car= mCarList.get(position);
		Map<String, String> map = new HashMap<String, String>();
		map.put("jylsh", car.getJylsh());
		MyHttpUtils.getInstance(mActivity).postHttpByParam(url, map, new StringCallback() {
			@Override
			public void onResponse(String response, int id) {
				try {
					Logger.show(TAG, "res="+response);
					JSONObject jo = new JSONObject(response);
					String state = jo.getString("state");
					if(state.equals("OK")){
						if(status==0){
							showDialog("下线成功。如果显示屏还显示该车辆,请至PDA工位复位功能进行工位复位操作");
						}else{
							showDialog("重新上线成功");
						}
						mCarList.remove(car);
						adapter.setDatas(mCarList);
						adapter.notifyDataSetChanged();
					}else{
						showDialog("响应不成功,"+response);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public void onError(Call call, Exception e, int id) {
				ToastUtils.showToast(mActivity, "网络响应失败"+e.getMessage(), Toast.LENGTH_LONG);
				e.printStackTrace();
			}
		});
	}
	
	
	
	
	private String convertCode(String hpzlCode) {
		return ToolUtils.getLableByCode(mActivity, hpzlCode, R.array.hpzl, R.array.hpzl_code);
	}

	
	
	class ReUpDownCarInfo{
		String jylsh;
		String hphm;
		String hpzl;
		Integer jcxdh;
		public String getJylsh() {
			return jylsh;
		}
		public String getHphm() {
			return hphm;
		}
		public String getHpzl() {
			return hpzl;
		}
		public Integer getJcxdh() {
			return jcxdh;
		}
		public void setJylsh(String jylsh) {
			this.jylsh = jylsh;
		}
		public void setHphm(String hphm) {
			this.hphm = hphm;
		}
		public void setHpzl(String hpzl) {
			this.hpzl = hpzl;
		}
		public void setJcxdh(Integer jcxdh) {
			this.jcxdh = jcxdh;
		}
	}
	
	private void showDialog(String msg) {
		new AlertDialog.Builder(mActivity).setMessage(msg)
		.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		}).show();
		
    }
	
	
}
