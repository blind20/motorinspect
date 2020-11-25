package com.shsy.motoinspect.ui.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.JsonObject;
import com.shsy.motoinspect.BaseFragment;
import com.shsy.motoinspect.CommonConstants;
import com.shsy.motoinspect.common.CommonAdapter;
import com.shsy.motoinspect.common.TitleBarView;
import com.shsy.motoinspect.common.ViewHolder;
import com.shsy.motoinspect.entity.CarListInfoEntity;
import com.shsy.motoinspect.network.ListCarInfoCallback;
import com.shsy.motoinspect.ui.fragment.ResetWorkFrm.WorkStation;
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
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import okhttp3.Call;


public class ResetWorkFrm extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

	private List<WorkStation> mWorkStationList;
	private TitleBarView mTitleBarView;
	private ListView mListView;
	
	private boolean isRecycle =false;
	
	private CommonAdapter<WorkStation> adpter;
	
	private SwipeRefreshLayout mSwipeLayout;
	private static final int REFRESH_COMPLETE = 0x110; 
	public static final int REQ_CONFIRM_DLG = 0x120;
	
	
	private Integer mPosition ;
	
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case REFRESH_COMPLETE:
				getWorkStationListNetwork(ToolUtils.pullCarsUrl(mActivity));
				if (!mWorkStationList.isEmpty()) {
					adpter.notifyDataSetChanged();
				}
				mSwipeLayout.setRefreshing(false);
				break;
				
			}
		};
	};
	
	
	
	public class WorkStation implements Parcelable{
		String id;
		String jcxdh;
		String wsxh;
		public WorkStation(){}
		public WorkStation(String id, String jcxdh, String wsxh) {
			this.id = id;
			this.jcxdh = jcxdh;
			this.wsxh = wsxh;
		}
		public Parcelable.Creator<WorkStation> CREATOR = new Parcelable.Creator<WorkStation>() {

			@Override
			public WorkStation createFromParcel(Parcel source) {
				WorkStation workStation = new WorkStation();
				workStation.id = source.readString();
				workStation.jcxdh = source.readString();
				workStation.wsxh = source.readString();
				return workStation;
			}

			@Override
			public WorkStation[] newArray(int size) {
				return new WorkStation[size];
			}
		};
		@Override
		public int describeContents() {
			return 0;
		}
		@Override
		public void writeToParcel(Parcel parcel, int flags) {
			parcel.writeString(id);
			parcel.writeString(jcxdh);
			parcel.writeString(wsxh);
		}
	}
	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getArguments()!= null){
			String title = getArguments().getString(NavigationFrm.TO_VEHLISTATY);
			Logger.show("titletitle", "titletitle"+title);
		}
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(savedInstanceState != null ){
			isRecycle = savedInstanceState.getBoolean("isRecycle");
			if(isRecycle){
				ArrayList list = savedInstanceState.getParcelableArrayList("mcarlist");
				if(list.size()>0){
					mWorkStationList = (List<WorkStation>) list.get(0);
				}
			}
		}
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	
	@Override
	public void initParam() {
		initView();
		initDatas();
		
	}

	@Override
	public int getLayoutResID() {
		return R.layout.listview_vehinfo;
	}

	public void initView() {
		mTitleBarView = new TitleBarView(mActivity);
		mTitleBarView = (TitleBarView) mRootView.findViewById(R.id.titlebar);
		mTitleBarView.setCommonTitle(View.VISIBLE, View.VISIBLE, View.GONE);
		mTitleBarView.setTitle(R.string.workreset);
		mSwipeLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.id_swipe_ly);
		
		mTitleBarView.setBtnLeftOnclickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mCallback.onResetBack();
			}
		});
		
		mListView = (ListView) mRootView.findViewById(R.id.lv_carinspect);
		mSwipeLayout.setOnRefreshListener(this);
	}

	private void initDatas() {
		mWorkStationList = new ArrayList<WorkStation>();
		
		/*--------------------------测试代码-----------------------------------------*/
//		mWorkStationList.add(new WorkStation("2号检测线","3号工位"));
//		viewSetAdapter();
		/*---------------------------正式代码----------------------------------------*/
		String url = ToolUtils.getWorkPointsUrl(mActivity);
		getWorkStationListNetwork(url);
	}


	private void getWorkStationListNetwork(String url) {
		Map<String, String> headers = new HashMap<String, String>();
		String session = (String) SharedPreferenceUtils.get(mActivity, CommonConstants.JSESSIONID, "");
		if(TextUtils.isEmpty(session)){
			return;
		}
		headers.put("Cookie", "JSESSIONID="+session);
		OkHttpUtils.post()
		.url(url).headers(headers)
		.build()
		.execute(new StringCallback() {
			
			@Override
			public void onResponse(String response, int id) {
				Logger.show("getStationListNetwork", response+"+===+");
				if(TextUtils.isEmpty(response)){
					return;
				}
				mWorkStationList.clear();
				mWorkStationList = parseWSJson(response);
				viewSetAdapter();
			}
			
			@Override
			public void onError(Call call, Exception e, int id) {
				
			}
		});
	}

	
	private List<WorkStation> parseWSJson(String json){
		List<WorkStation> list = new ArrayList<WorkStation>();
		try {
			JSONObject obj = new JSONObject(json);
			JSONArray jsons = obj.getJSONArray("rows");
			for(int i=0;i<jsons.length();i++){
				WorkStation ws = new WorkStation();
				ws.id = jsons.getJSONObject(i).getString("id");
//				String name = jsons.getJSONObject(i).getString("name");
//				ws.jcxdh = name.split("线")[0]+ getString(R.string.veh_jcx);
//				ws.wsxh = name.split("线")[1];
				
				ws.jcxdh = jsons.getJSONObject(i).getString("jcxdh")+getString(R.string.veh_jcx);
				ws.wsxh = jsons.getJSONObject(i).getString("sort")+"工位";
				list.add(ws);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	
	
	

	private void viewSetAdapter() {
		
		adpter = new CommonAdapter<WorkStation>(mWorkStationList,mActivity,R.layout.item_reset_station_list) {

			@Override
			public void convert(final ViewHolder holder, final WorkStation t) {
				holder.setText(R.id.tv_jcxdh, t.jcxdh)
					  .setText(R.id.tv_gwdh, t.wsxh);
				
				final String format = getString(R.string.is_confirm_reset);
				holder.setButtonListen(R.id.btn_reset, new OnClickListener() {
					@Override
					public void onClick(View v) {
						mPosition = holder.getPosition();
						MyDialogFragment dialog = MyDialogFragment.newInstance(MyDialogFragment.DLG_CONFIRM, 
																				getString(R.string.reset),
																				String.format(format, t.jcxdh+t.wsxh),
																				ResetWorkFrm.REQ_CONFIRM_DLG);
						
						dialog.setTargetFragment(ResetWorkFrm.this, ResetWorkFrm.REQ_CONFIRM_DLG);
						dialog.show(getFragmentManager(), "");
					}
				});
			}
		};
		
		mListView.setAdapter(adpter);
	}
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == ResetWorkFrm.REQ_CONFIRM_DLG){
			String url = ToolUtils.getReStartUrl(mActivity);
			Logger.show("mPosition", "mPosition="+mPosition);
			restartWorkStation(mPosition,url);
		}
	}
	
	
	
	private void restartWorkStation(final int position, String url) {
		Map<String, String> headers = new HashMap<String, String>();
		String session = (String) SharedPreferenceUtils.get(mActivity, CommonConstants.JSESSIONID, "");
		if(TextUtils.isEmpty(session)){
			return;
		}
		headers.put("Cookie", "JSESSIONID="+session);
		Logger.show("mWorkStationList", "id ="+mWorkStationList.get(position).id);
		
		OkHttpUtils.post().url(url).headers(headers)
		.addParams("id", mWorkStationList.get(position).id)
		.build().execute(new StringCallback() {
			
			@Override
			public void onResponse(String response, int id) {
				try {
					JSONObject jo = new JSONObject(response);
					Integer state = (Integer) jo.get("state");
					if(CommonConstants.STATAS_SUCCESS==state){
						ToastUtils.showToast(mActivity, getString(R.string.reset_success), Toast.LENGTH_SHORT);
						return;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onError(Call call, Exception e, int id) {
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
		list.add(mWorkStationList);
		outState.putBoolean("isRecycle", isRecycle);
		outState.putParcelableArrayList("mcarlist", list);
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
	
	protected OnResetBackListener mCallback;
	public interface OnResetBackListener{
		public void onResetBack();
	}
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCallback = (OnResetBackListener) mActivity;
		} catch (ClassCastException e) {
			e.printStackTrace();
			throw new ClassCastException(mActivity.toString()
					+ " must implement OnCarItemSelListener");
		}
	}
}
