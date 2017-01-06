package com.shsy.motoinspect.ui.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.shsy.motoinspect.BaseFragment;
import com.shsy.motoinspect.CommonConstants;
import com.shsy.motoinspect.common.CommonAdapter;
import com.shsy.motoinspect.common.TitleBarView;
import com.shsy.motoinspect.common.ViewHolder;
import com.shsy.motoinspect.entity.CarListInfoEntity;
import com.shsy.motoinspect.entity.CarPhotoEntity;
import com.shsy.motoinspect.network.ListCarInfoCallback;
import com.shsy.motoinspect.ui.activity.MainActivity;
import com.shsy.motoinspect.ui.activity.OuterInspectActivity;
import com.shsy.motoinspect.utils.Logger;
import com.shsy.motoinspect.utils.SharedPreferenceUtils;
import com.shsy.motoinspect.utils.ToastUtils;
import com.shsy.motoinspect.utils.ToolUtils;
import com.shsy.motorinspect.R;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Request.Builder; 

public class OuterCheckFrm extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

	private List<CarListInfoEntity> mCarList;
	private ListView mListView;
	private boolean isRecycle =false;
	
	private TitleBarView mTitleBarView;
	
	private static final int REFRESH_COMPLETE = 0X110;  
    private SwipeRefreshLayout mSwipeLayout;
    private CommonAdapter<CarListInfoEntity> adapter;
	
	private String TAG = getClass().getName();
	
	public static final String OUTCHECKTYPE ="OUTCHECKTYPE"; //put/get mOutCheckType值
	private int mOutCheckType;
	
	
	 private Handler mHandler = new Handler(){  
	        public void handleMessage(android.os.Message msg)  
	        {  
	            switch (msg.what)  
	            {  
		            case REFRESH_COMPLETE:  
		            	getCarListByCheckType(mOutCheckType);
		            	if(!mCarList.isEmpty()){
		            		adapter.notifyDataSetChanged();
		            	}
		                mSwipeLayout.setRefreshing(false);  
		                break;  
		                
	            }  
	        };  
	}; 
	

	@Override
	public void initParam() {
		initView();
		initDatas();
		
	}

	@Override
	public int getLayoutResID() {
		return R.layout.listview_vehinfo;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String extra = getArguments().getString(NavigationFrm.TO_VEHLISTATY);
		mOutCheckType = getTypeByParam(extra);
	}
	
	
	private int getTypeByParam(String extra) {
		if(extra.equals(getString(R.string.outer_nav1_menu2))){
			return CommonConstants.DYNAMIC;
		}
		if(extra.equals(getString(R.string.outer_nav1_menu3))){
			return CommonConstants.CHASSIS;
		}
		return CommonConstants.APPEARANCE;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Logger.show(getTag(), "onCreateView");
		
		if(savedInstanceState != null ){
			isRecycle = savedInstanceState.getBoolean("isRecycle");
			if(isRecycle){
				Logger.show(getTag(), "savedInstanceState");
				mOutCheckType = savedInstanceState.getInt(OUTCHECKTYPE);
				ArrayList list = savedInstanceState.getParcelableArrayList("mcarlist");
				if(list.size()>0){
					mCarList = (List<CarListInfoEntity>) list.get(0);
				}
				
			}
		}
		
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	
	@Override
	public void onResume() {
		super.onResume();
		onRefresh();
	}
	

	
	
	private void initView() {
		
		mSwipeLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.id_swipe_ly);
		mTitleBarView = new TitleBarView(mActivity);
		mTitleBarView = (TitleBarView) mRootView.findViewById(R.id.titlebar);
		mTitleBarView.setCommonTitle(View.VISIBLE, View.VISIBLE, View.GONE);
		
		
		switch (mOutCheckType) {
			case CommonConstants.APPEARANCE:
				mTitleBarView.setTitle(R.string.waitinspectcar);
				break;
	
			case CommonConstants.CHASSIS:
				mTitleBarView.setTitle(R.string.chassis_check_car);
				break;
				
			case CommonConstants.DYNAMIC:
				mTitleBarView.setTitle(R.string.dynamic_check_car);
				break;
		}
		
		mListView = (ListView) mRootView.findViewById(R.id.lv_carinspect);
		
		mSwipeLayout.setOnRefreshListener(this);
		
		mTitleBarView.setBtnLeftOnclickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mCallback.onOuterCheckBack();
			}
		});
	}

	
	
	private void initDatas() {
		mCarList = new ArrayList<CarListInfoEntity>();
		
		//测试代码
//		mCarList.add(new CarListInfoEntity("苏C2868J", "小型汽车", "2016050123985890", "2016-03-02",CommonConstants.NOTPULLCAR
//				,"",1,"","","",""));
//		mCarList.add(new CarListInfoEntity("苏J1111J", "小型汽车", "2016050123985892", "2016-03-03",CommonConstants.NOTPULLCAR
//				,"",1,"","","",""));
//		viewSetAdapter();
		
		//根据mOutCheckType 跳转请求http
		getCarListByCheckType(mOutCheckType);
		
	}
	
	
	private void getCarListByCheckType(int type) {
		String url = null;
		switch (type) {
			case CommonConstants.APPEARANCE:
				url = ToolUtils.outCarsUrl(mActivity);
				break;
	
			case CommonConstants.CHASSIS:
				url = ToolUtils.getExternalC1Url(mActivity);
				break;
			case CommonConstants.DYNAMIC:
				url = ToolUtils.getExternalDCUrl(mActivity);
				break;
		}
		if(TextUtils.isEmpty(url)){
			return;
		}
		getCarListNetwork(url);
	}

	
	
	private void getCarListNetwork(String url) {
		Map<String, String> headers = new HashMap<String, String>();
		String session = (String) SharedPreferenceUtils.get(mActivity, CommonConstants.JSESSIONID, "");
		if(TextUtils.isEmpty(session)){
			Logger.show("getCarListNetwork", "sessionid = null");
			return;
		}else{
			Logger.show("getCarListNetwork", "sessionid = "+session);
		}
		headers.put("Cookie", "JSESSIONID="+session);

		OkHttpUtils.post().url(url).headers(headers)
				.build().execute(new ListCarInfoCallback(){

			@Override
			public void onError(Call call, Exception e, int id) {
				ToastUtils.showToast(mActivity, mActivity.getString(R.string.not_connect_server), Toast.LENGTH_LONG);
				Logger.show(getTag(), e.getMessage());
				e.printStackTrace();
			}

			@Override
			public void onResponse(List<CarListInfoEntity> list, int id) {
				
				mCarList.clear();
				mCarList.addAll(list);
				viewSetAdapter();
			}
			
		});
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Logger.show(getTag(), "onSaveInstanceState");
		isRecycle = true;
		ArrayList list = new ArrayList();
		list.add(mCarList);
		outState.putBoolean("isRecycle", isRecycle);
		outState.putParcelableArrayList("mcarlist", list);
		outState.putInt(OUTCHECKTYPE, mOutCheckType);
	}
	
	

	private void viewSetAdapter() {
		
		adapter = new CommonAdapter<CarListInfoEntity>(mCarList,this.getActivity(),R.layout.item_carinspect_list) {

			@Override
			public void convert(ViewHolder holder, CarListInfoEntity t) {
				holder.setText(R.id.tv_hphm, t.getHphm())
					  .setText(R.id.tv_hpzl, convertCode(t.getHpzl()))
					  .setText(R.id.tv_lsh, t.getLsh())
					  .setText(R.id.tv_date, t.getDate());
			}
		};
		
		mListView.setAdapter(adapter);
		
		mListView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				
				Intent intent = new Intent(getActivity(), OuterInspectActivity.class);
				Bundle bundle = new Bundle();
				bundle.putParcelable(CommonConstants.BUNDLE_TO_OUTER, mCarList.get(position));
				bundle.putInt(OUTCHECKTYPE, mOutCheckType);
				bundle.putString("jylsh", mCarList.get(position).getLsh());
				bundle.putString("jycs", "1");
				intent.putExtras(bundle);
				startActivity(intent);
			}
			
		});
	}
	
	
	
	
	
	
	
	
	
	private String convertCode(String hpzlCode) {
		return ToolUtils.getLableByCode(mActivity, hpzlCode, R.array.hpzl, R.array.hpzl_code);
	}
	

	@Override
	public void onRefresh() {
		mHandler.sendEmptyMessageDelayed(REFRESH_COMPLETE, 0);  
	}
	
	
	protected OnOuterCheckBackListener mCallback;
	public interface OnOuterCheckBackListener{
		public void onOuterCheckBack();
	}
	@Override
	public void onAttach(Activity activity) {
		Logger.show(getTag(), "onAttach");
		super.onAttach(activity);
		try {
			mCallback = (OnOuterCheckBackListener) mActivity;
		} catch (ClassCastException e) {
			e.printStackTrace();
			throw new ClassCastException(mActivity.toString()
					+ " must implement OnCarItemSelListener");
		}
	}
	
}
