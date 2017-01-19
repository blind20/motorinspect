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
import com.shsy.motoinspect.common.ViewHolder;
import com.shsy.motoinspect.utils.Logger;
import com.shsy.motoinspect.utils.SharedPreferenceUtils;
import com.shsy.motoinspect.utils.ToolUtils;
import com.shsy.motorinspect.R;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import okhttp3.Call;


public class RoadTestFrm extends BaseFragment {

	private ListView mListView;
	private List<HashMap<String,String>> mRoadItems;
	private CommonAdapter<HashMap<String,String>> adapter;
	private String jylsh;
//	boolean isFirst = false;
	
	public RoadTestFrm() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		jylsh = getArguments().getString(PullCarToLineFrm.ROADTEST_JYLSH);
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	
	@Override
	public int getLayoutResID() {
		return R.layout.listview_common;
	}

	@Override
	public void initParam() {
		initView();
		
		initListener();
	}
	
	private void initView() {
		mListView = (ListView) mRootView.findViewById(R.id.listview);
		mRoadItems = initDatas();
		String url = ToolUtils.getRoadProcessURL(mActivity);
		getRoadTimesNetwork(url,jylsh);
		
	}
	
	
	
	private void getRoadTimesNetwork(String url,String jylsh) {
		Map<String, String> headers = new HashMap<String, String>();
		String session = (String) SharedPreferenceUtils.get(mActivity, CommonConstants.JSESSIONID, "");
		if(TextUtils.isEmpty(session)){
			return;
		}
		headers.put("Cookie", "JSESSIONID="+session);
		
		OkHttpUtils.post().url(url).headers(headers)
		.addParams("jylsh", jylsh)
		.build().execute(new StringCallback() {
			
			@Override
			public void onResponse(String response, int id) {
				try {
					
					JSONArray ja = new JSONArray(response);
					for(int i=0;i<ja.length();i++){
						String kssj = ja.getJSONObject(i).getString("kssj");
						String jssj = ja.getJSONObject(i).getString("jssj");
						mRoadItems.get(i).put("starttime", kssj);
						mRoadItems.get(i).put("endtime", jssj);
					}
					viewSetAdapter();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onError(Call call, Exception e, int id) {
				Logger.show(getClass().getName(), "getRoadTimesNetwork Error:" + e.getMessage());
				e.printStackTrace();
			}
		});
	}

	private void viewSetAdapter() {
		
		
		adapter = new CommonAdapter<HashMap<String,String>>(mRoadItems,mActivity,R.layout.item_roadtest) {

			@Override
			public void convert(ViewHolder holder, HashMap<String,String> map) {
				
				String starttime = getRoadTime(R.id.tv_roadtest_start,map);
				String endtime = getRoadTime(R.id.tv_roadtest_end,map);
				
				
				holder.setText(R.id.tv_roadtest_type, map.get("item"))
					  .setText(R.id.tv_roadtest_start, starttime)
					  .setText(R.id.tv_roadtest_end, endtime)
					  .setButtonText(R.id.btn_roadtest_start, getButtonTxt(R.id.btn_roadtest_start, map))
					  .setButtonText(R.id.btn_roadtest_end, getButtonTxt(R.id.btn_roadtest_end, map));
				
				//Button点击事件
				buttonListener(holder,R.id.btn_roadtest_start);
				buttonListener(holder,R.id.btn_roadtest_end);
			}

			
		};
		mListView.setAdapter(adapter);
	}

	
	
	
	private List<HashMap<String,String>> initDatas() {
		List<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
		for(int i=0;i<3;i++){
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("starttime", "");
			map.put("endtime", "");
			switch (i) {
				case 0:
					map.put("item", "路试制动");
					break;
				case 1:
					map.put("item", "路试驻车");
					break;
				case 2:
					map.put("item", "路试速度");
					break;
			}
			list.add(map);
		}
		return list;
	}

	
	
	
	private void buttonListener(final ViewHolder holder,final int resId) {
		
		holder.setButtonListen(resId, new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String jyxm="R1";
				Integer type=0; 
				int pos = holder.getPosition();
				switch (pos) {
					case 0:
						jyxm = "R1";
						if(v.getId()==R.id.btn_roadtest_start){
							type = 0;
						}else{
							type =1;
						}
						break;
	
					case 1:
						jyxm = "R2";
						if(v.getId()==R.id.btn_roadtest_start){
							type = 0;
						}else{
							type =1;
						}
						break;
						
					case 2:
						jyxm = "R3";
						if(v.getId()==R.id.btn_roadtest_start){
							type = 0;
						}else{
							type =1;
						}
						break;
				}
				
				String url = ToolUtils.roadProcessURL(mActivity);
				roadTestAction(url,jyxm,type);
			}
		});
	}
	
	
	
	
	private void roadTestAction(String url,String jyxm,Integer type){
		Logger.show("roadTestAction", "jyxm:"+jyxm+",type="+type);
		Map<String, String> headers = new HashMap<String, String>();
		String session = (String) SharedPreferenceUtils.get(mActivity, CommonConstants.JSESSIONID, "");
		if(TextUtils.isEmpty(session)){
			return;
		}
		headers.put("Cookie", "JSESSIONID="+session);
		
		OkHttpUtils.post().url(url).headers(headers)
		.addParams("jylsh", jylsh)
		.addParams("jyxm", jyxm)
		.addParams("type", type+"")
		.build().execute(new StringCallback() {
			
			@Override
			public void onResponse(String response, int id) {
				Logger.show("roadTestAction", "roadTestAction sucess" );
				String url = ToolUtils.getRoadProcessURL(mActivity);
				getRoadTimesNetwork(url,jylsh);
				adapter.notifyDataSetChanged();
			}

			@Override
			public void onError(Call call, Exception e, int id) {
				Logger.show("roadTestAction", "roadTestAction Error:" + e.getMessage());
				e.printStackTrace();
			}
		});
		
	}
	
	
	private String getRoadTime(int resId,HashMap<String,String> map){
		if(resId ==  R.id.tv_roadtest_start){
			return getString(R.string.starttime)+map.get("starttime");
		}
		return getString(R.string.endtime)+map.get("endtime");
	}
	
	
	private String getRoadTime(int resId,HashMap<String,String> map,String timestamp){
		if("null".equals(timestamp) || TextUtils.isEmpty(timestamp)){
			return getString(resId);
		}
		return getString(resId)+timestamp;
	}
	
	
	private String getButtonTxt(int resId,HashMap<String,String> map){
		if(resId == R.id.btn_roadtest_start){
			return map.get("item").substring(2)+"开始";
		}
		return map.get("item").substring(2)+"结束";
	}
	
	
	
	private void initListener() {
		
	}



}
