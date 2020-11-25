package com.shsy.motoinspect.ui.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.shsy.motoinspect.BaseFragment;
import com.shsy.motoinspect.CommonConstants;
import com.shsy.motoinspect.common.CommonAdapter;
import com.shsy.motoinspect.common.TitleBarView;
import com.shsy.motoinspect.common.ViewHolder;
import com.shsy.motoinspect.entity.CarListInfoEntity;
import com.shsy.motoinspect.network.ListCarInfoCallback;
import com.shsy.motoinspect.network.QDZListCarInfoCallback;
import com.shsy.motoinspect.utils.Logger;
import com.shsy.motoinspect.utils.SharedPreferenceUtils;
import com.shsy.motoinspect.utils.ToastUtils;
import com.shsy.motoinspect.utils.ToolUtils;
import com.shsy.motorinspect.R;
import com.zhy.http.okhttp.OkHttpUtils;

import android.Manifest.permission;
import android.R.integer;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import okhttp3.Call;

public class ZbzlCarListFrm extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener{
	
	private List<CarListInfoEntity> mCarList;
	private ListView mListView;
	private boolean isRecycle =false;
	
	private TitleBarView mTitleBarView;
	
	private static final int REFRESH_COMPLETE = 0X110;  
    private SwipeRefreshLayout mSwipeLayout;
    private CommonAdapter<CarListInfoEntity> adapter;
	
	private String TAG = getClass().getName();
	
	private ProgressDialog mProgressDlg;
	private List<CarListInfoEntity> mTotalList;
	
	private boolean isOnCreate =false;
	
	
	/**
	 * 称重类型：0为整备质量称重，1为驱动轴称重
	 */
	private int mCZType;
	
	
	private Handler mHandler = new Handler(){  
        public void handleMessage(android.os.Message msg)  
        {  
            switch (msg.what)  
            {  
	            case REFRESH_COMPLETE: 
	            	String url = getUrl(mCZType);
	        		getCarListNetwork(url);
	            	if(!mCarList.isEmpty()){
	            		adapter.notifyDataSetChanged();
	            	}
	                mSwipeLayout.setRefreshing(false);  
	                break;  
	                
            }  
        };  
	}; 
	
	@Override
	public int getLayoutResID() {
		return R.layout.listview_vehinfo;
	}

	@Override
	public void initParam() {
		initView(mCZType);
		initDatas(mCZType);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getArguments()!= null){
			String title = getArguments().getString(NavigationFrm.TO_VEHLISTATY);
			mCZType = getTypeByParam(title);
		}
		isOnCreate = true;
	}
	
	/**
	 * 返回称重类型：0为整备质量称重，1为驱动轴称重
	 */
	private int getTypeByParam(String title) {
		if(title.equals(getString(R.string.outer_nav2_menu4))){
			return 0;
		}
		return 1;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(savedInstanceState != null ){
			isRecycle = savedInstanceState.getBoolean("isRecycle");
			if(isRecycle){
				Logger.show(getTag(), "savedInstanceState");
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
		Logger.show(getTag(), "onResume=1");
		super.onResume();
		if(!isOnCreate){
			mProgressDlg.dismiss();
			onRefresh();
			isOnCreate = false;
		}
		
	}
	

	
	
	private void initView(int type) {
		mSwipeLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.id_swipe_ly);
		mTitleBarView = new TitleBarView(mActivity);
		mTitleBarView = (TitleBarView) mRootView.findViewById(R.id.titlebar);
		mTitleBarView.setCommonTitle(View.VISIBLE, View.VISIBLE, View.GONE);
		
		
		mListView = (ListView) mRootView.findViewById(R.id.lv_carinspect);
		mSwipeLayout.setOnRefreshListener(this);
		
		if(type==0){
			mTitleBarView.setTitle(R.string.zbzlcarlist);
		}else{
			mTitleBarView.setTitle(R.string.qdzczcarlist);
		}
		
		mTitleBarView.setBtnLeftOnclickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentManager manager = getActivity().getSupportFragmentManager();
				if(manager.getBackStackEntryCount() != 0){
					manager.popBackStack();
				}
			}
		});
		
		mProgressDlg = new ProgressDialog(mActivity);
	}
	
	/**
	 * type称重类型：0为整备质量称重，1为驱动轴称重
	 */
	private void initDatas(int type) {
		mCarList = new ArrayList<CarListInfoEntity>();
		mTotalList = new ArrayList<CarListInfoEntity>();
		
		String url = getUrl(type);
		
		if(type==0){
			getCarListNetwork(url);
		}else{
			getQDZCarListNetwork(url);
		}
		
	}
	
	private String getUrl(int type){
		if(type==0){
			return ToolUtils.getZbzlCarList(mActivity);
		}else{
			return ToolUtils.getQDZCarList(mActivity);
		}
	}
	
	
	
	private void getCarListNetwork(String url) {
		Logger.show(getTag(), "url="+url);
		Map<String, String> headers = new HashMap<String, String>();
		String session = (String) SharedPreferenceUtils.get(mActivity, CommonConstants.JSESSIONID, "");
		if(TextUtils.isEmpty(session)){
			return;
		}
		headers.put("Cookie", "JSESSIONID="+session);
		OkHttpUtils.post().url(url).headers(headers)
				.build().execute(new ListCarInfoCallback(){

			@Override
			public void onError(Call call, Exception e, int id) {
				ToastUtils.showToast(mActivity, "网络响应失败"+e.getMessage(), Toast.LENGTH_LONG);
				Logger.show(getTag(), e.getMessage());
				e.printStackTrace();
			}

			@Override
			public void onResponse(List<CarListInfoEntity> list, int id) {
				Logger.show(getTag(), "zbzl+onResponse");
				mTotalList = list;
				mCarList.clear();
				mCarList.addAll(mTotalList);
				viewSetAdapter();
			}
		});
	}
	
	
	private void getQDZCarListNetwork(String url) {
		Logger.show(getTag(), "url="+url);
		Map<String, String> headers = new HashMap<String, String>();
		String session = (String) SharedPreferenceUtils.get(mActivity, CommonConstants.JSESSIONID, "");
		if(TextUtils.isEmpty(session)){
			return;
		}
		headers.put("Cookie", "JSESSIONID="+session);
		OkHttpUtils.post().url(url).headers(headers)
				.build().execute(new QDZListCarInfoCallback(){

			@Override
			public void onError(Call call, Exception e, int id) {
				ToastUtils.showToast(mActivity,"网络响应失败"+e.getMessage(), Toast.LENGTH_LONG);
				Logger.show(getTag(), e.getMessage());
				e.printStackTrace();
			}

			@Override
			public void onResponse(List<CarListInfoEntity> list, int id) {
				Logger.show(getTag(), "zbzl+onResponse");
				mTotalList = list;
				mCarList.clear();
				mCarList.addAll(mTotalList);
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
	}
	
	

	private void viewSetAdapter() {
		adapter = new CommonAdapter<CarListInfoEntity>(mCarList,mActivity,R.layout.item_carinspect_list) {

			@Override
			public void convert(ViewHolder holder, CarListInfoEntity t) {
				holder.setText(R.id.tv_hphm, t.getHphm())
					  .setText(R.id.tv_hpzl, convertCode(t.getHpzl()))
					  .setText(R.id.tv_lsh, t.getLsh())
					  .setText(R.id.tv_line_num, t.getJcxdh()+"号线")
					  .setText(R.id.tv_date, t.getDate());
				if(t.getCheckType()==1){
					if(t.getZjlb()==0){
						holder.setText(R.id.tv_checktype, "【综安】");
					}else{
						holder.setText(R.id.tv_checktype, "【综检】");
					}
				}else{
					holder.setText(R.id.tv_checktype, "【安检】");
				}
				
			}
		};
		mListView.setAdapter(adapter);
		mListView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(mCallback !=null){
					mCallback.onZbzlCarItemClick(mCarList.get(position),mCZType);
				}
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
	
	public OnZbzlCarListClick mCallback;
	public interface OnZbzlCarListClick{
		public void onZbzlCarItemClick(CarListInfoEntity carinfo,int type);
	}
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCallback = (OnZbzlCarListClick) mActivity;
		} catch (ClassCastException e) {
			e.printStackTrace();
			throw new ClassCastException(mActivity.toString()
					+ " must implement OnZbzlCarListClick");
		}
	}
}
