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
import com.shsy.motoinspect.network.ListCarInfoCallback;
import com.shsy.motoinspect.utils.Logger;
import com.shsy.motoinspect.utils.SharedPreferenceUtils;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ListView;
import okhttp3.Call;


public class PullCarToLineFrm extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

	private List<CarListInfoEntity> mCarList;
	private TitleBarView mTitleBarView;
	private ListView mListView;
	
	private boolean isRecycle =false;
	
	private CommonAdapter<CarListInfoEntity> adpter;
	
	public static final String MTYPE ="mType"; //put/get mType值
	private int mType;
	
	private SwipeRefreshLayout mSwipeLayout;
	public static final int REFRESH_COMPLETE = 0X110; 
	public static final int REQ_CONFIRM_DLG =0x120;
	private Integer mPosition;
	private String msgFormat;
	
	
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case REFRESH_COMPLETE:
				getCarListNetwork(mType);
				if (!mCarList.isEmpty()) {
					adpter.notifyDataSetChanged();
				}
				mSwipeLayout.setRefreshing(false);
				break;

			}
		};
	};
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getArguments()!= null){
			String title = getArguments().getString(NavigationFrm.TO_VEHLISTATY);
			Logger.show("*****titletitle", "titletitle"+title);
			mType = getTypeByParam(title);
		}
	}
	
	
	private int getTypeByParam(String extra) {
		if(extra.equals(getString(R.string.outer_nav2_menu1))){
			return CommonConstants.PULLCAR;
		}
		return CommonConstants.SENDBACK;
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(savedInstanceState != null ){
			isRecycle = savedInstanceState.getBoolean("isRecycle");
			if(isRecycle){
				Logger.show(getTag(), "savedInstanceState");
				mType = savedInstanceState.getInt(MTYPE);
				ArrayList list = savedInstanceState.getParcelableArrayList("mcarlist");
				if(list.size()>0){
					mCarList = (List<CarListInfoEntity>) list.get(0);
				}
			}
		}
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	
	@Override
	public void initParam() {
		initView(mType);
		initDatas(mType);
		
	}

	@Override
	public int getLayoutResID() {
		return R.layout.listview_vehinfo;
	}

	public void initView(int type) {
		mTitleBarView = new TitleBarView(mActivity);
		mTitleBarView = (TitleBarView) mRootView.findViewById(R.id.titlebar);
		mTitleBarView.setCommonTitle(View.VISIBLE, View.VISIBLE, View.GONE);
		mSwipeLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.id_swipe_ly);
		
		switch (type) {
			case CommonConstants.PULLCAR:
				mTitleBarView.setTitle(R.string.pullcar);
				break;
	
			case CommonConstants.SENDBACK:
				mTitleBarView.setTitle(R.string.sendback_cars);
				break;
		}
		
		mTitleBarView.setBtnLeftOnclickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mCallback.onPullBack();
			}
		});
		
		mListView = (ListView) mRootView.findViewById(R.id.lv_carinspect);
		mSwipeLayout.setOnRefreshListener(this);
	}

	private void initDatas(int type) {
		mCarList = new ArrayList<CarListInfoEntity>();
		
//		mCarList.add(new CarListInfoEntity("苏C2868J", "小型汽车", "2016050123985890", "2016-03-02",CommonConstants.NOTPULLCAR
//				,"",1,"","","","01"));
//		viewSetAdapter();
		
		getCarListNetwork(type);
	}


	private String getAvailUrl(int type) {
		String url = null;
		switch (type) {
			case CommonConstants.PULLCAR:
				url = ToolUtils.pullCarsUrl(mActivity);
				break;
	
			case CommonConstants.SENDBACK:
				url = ToolUtils.pullCarsUrl(mActivity);
				break;

		}
		return url;
	}


	private void getCarListNetwork(final int type) {
		String url = getAvailUrl(type);
		Map<String, String> headers = new HashMap<String, String>();
		String session = (String) SharedPreferenceUtils.get(mActivity, CommonConstants.JSESSIONID, "");
		if(TextUtils.isEmpty(session)){
			return;
		}
		headers.put("Cookie", "JSESSIONID="+session);
		
		OkHttpUtils.post()
		.url(url).headers(headers)
		.addParams("status", Integer.toString(CommonConstants.WAITPULLCAR))
		.build()
		.execute(new ListCarInfoCallback(){

			@Override
			public void onError(Call call, Exception e, int id) {
				Logger.show(getTag(), e.getMessage());
				e.printStackTrace();
			}

			@Override
			public void onResponse(List<CarListInfoEntity> list, int id) {
				mCarList.clear();
				mCarList.addAll(list);
				viewSetAdapter(type);
			}
			
		});
	}

	

	private void viewSetAdapter(final int type) {
		
		final String lineNameFormat = getResources().getString(R.string.check_line_num);
		
		adpter = new CommonAdapter<CarListInfoEntity>(mCarList,mActivity,R.layout.item_pullcar_list) {

			@Override
			public void convert(ViewHolder holder, final CarListInfoEntity t) {
				
				String sFinal = String.format(lineNameFormat, t.getJcxdh());
				
				holder.setText(R.id.tv_hphm, t.getHphm())
					  .setText(R.id.tv_hpzl, convertCode(t.getHpzl()))
					  .setText(R.id.tv_lsh, t.getLsh())
					  .setText(R.id.tv_date, t.getDate())
					  .setText(R.id.tv_checkline,sFinal);
				
				if(type == CommonConstants.SENDBACK){
					holder.setButtonText(R.id.btn_pullcar, getString(R.string.veh_sendback));
					msgFormat = getString(R.string.is_confirm_sendback);
				}else{
					msgFormat = getString(R.string.is_confirm_pullcar);
				}

				mPosition = holder.getPosition();
				
				holder.setButtonListen(R.id.btn_pullcar, new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						MyDialogFragment dialog = MyDialogFragment.newInstance(MyDialogFragment.DLG_CONFIRM, 
																	t.getHphm(),
																	String.format(msgFormat, t.getHphm()),
																	REQ_CONFIRM_DLG);
						dialog.setTargetFragment(PullCarToLineFrm.this, REQ_CONFIRM_DLG);
						dialog.show(getFragmentManager(), "");
					}
				});
				
			}
		};
		
		mListView.setAdapter(adpter);
	}
	
	
	private void pushCarOnLineNetwork(final int position, String pushCarOnlineUrl,int type) {
		Map<String, String> headers = new HashMap<String, String>();
		String session = (String) SharedPreferenceUtils.get(mActivity, CommonConstants.JSESSIONID, "");
		if(TextUtils.isEmpty(session)){
			return;
		}
		headers.put("Cookie", "JSESSIONID="+session);
		
		OkHttpUtils.post().url(pushCarOnlineUrl).headers(headers)
		.addParams("id", mCarList.get(position).getId()+"")
		.build().execute(new StringCallback() {
			
			@Override
			public void onResponse(String response, int id) {
				try {
					JSONObject jo = new JSONObject(response);
					Integer state = (Integer) jo.get("state");
					if(CommonConstants.STATAS_SUCCESS==state){
						mCarList.remove(position);
						adpter.notifyDataSetChanged();
						return;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onError(Call call, Exception e, int id) {
				Logger.show(getClass().getName(), "onItemClick onError " + e.getMessage());
				e.printStackTrace();
			}
		});
	}
	
	
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		super.onSaveInstanceState(outState);
		isRecycle = true;
		ArrayList list = new ArrayList();
		list.add(mCarList);
		outState.putBoolean("isRecycle", isRecycle);
		outState.putParcelableArrayList("mcarlist", list);
		outState.putInt(MTYPE, mType);
	}
	
	private String convertCode(String hpzlCode) {
		return ToolUtils.getLableByCode(mActivity, hpzlCode, R.array.hpzl, R.array.hpzl_code);
	}
	

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == PullCarToLineFrm.REQ_CONFIRM_DLG){
			//引车或者退办
			String url = ToolUtils.pushCarOnlineUrl(mActivity);
			pushCarOnLineNetwork(mPosition,url,mType);
		}
	}

	@Override
	public void onRefresh() {
		mHandler.sendEmptyMessageDelayed(REFRESH_COMPLETE, 0);  
	}
	
	@Override
	public void onResume() {
		super.onResume();
		onRefresh();
	}
	
	protected OnPullBackListener mCallback;
	public interface OnPullBackListener{
		public void onPullBack();
	}
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCallback = (OnPullBackListener) mActivity;
		} catch (ClassCastException e) {
			e.printStackTrace();
			throw new ClassCastException(mActivity.toString()
					+ " must implement OnCarItemSelListener");
		}
	}
}
