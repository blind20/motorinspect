package com.shsy.motoinspect.ui.fragment;

import java.util.ArrayList;
import java.util.Collection;
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
import com.shsy.motoinspect.entity.LineInfo;
import com.shsy.motoinspect.network.ListCarInfoCallback;
import com.shsy.motoinspect.network.MyHttpUtils;
import com.shsy.motoinspect.network.UnfinishedListCarInfoCallback;
import com.shsy.motoinspect.utils.CommonDialog;
import com.shsy.motoinspect.utils.CommonDialog.OnDialogListener;
import com.shsy.motoinspect.utils.DateUtils;
import com.shsy.motoinspect.utils.Logger;
import com.shsy.motoinspect.utils.SharedPreferenceUtils;
import com.shsy.motoinspect.utils.ToastUtils;
import com.shsy.motoinspect.utils.ToolUtils;
import com.shsy.motorinspect.R;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.StringCallback;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import okhttp3.Call;

public class UnfinishedCarListFrm extends BaseFragment {
	
	private List<CarListInfoEntity> mCarList;
	private ListView mListView;
	private boolean isRecycle =false;
	
	private TitleBarView mTitleBarView;
    private CommonAdapter<CarListInfoEntity> adapter;
	
	private String TAG = getClass().getName();
	
	
	private boolean isOnCreate =false;
	private EditText et_hphm;
	private Button btn_search;
	private Button btn_clearup;
	
	@Override
	public int getLayoutResID() {
		return R.layout.frm_recheck_list;
	}

	@Override
	public void initParam() {
		initView();
		initDatas();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getArguments()!= null){
			String title = getArguments().getString(NavigationFrm.TO_VEHLISTATY);
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
			isOnCreate = false;
		}
		
	}
	

	
	
	private void initView() {
		mTitleBarView = new TitleBarView(mActivity);
		mTitleBarView = (TitleBarView) mRootView.findViewById(R.id.titlebar);
		mTitleBarView.setCommonTitle(View.VISIBLE, View.VISIBLE, View.GONE);
		et_hphm = (EditText) mRootView.findViewById(R.id.et_hphm);
		btn_search = (Button) mRootView.findViewById(R.id.btn_search);
		btn_clearup = (Button) mRootView.findViewById(R.id.btn_clearup);
		mListView = (ListView) mRootView.findViewById(R.id.lv_carinspect);
		mTitleBarView.setTitle(R.string.recheck_car_list);
		mTitleBarView.setBtnLeftOnclickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentManager manager = getActivity().getSupportFragmentManager();
				if(manager.getBackStackEntryCount() != 0){
					manager.popBackStack();
				}
			}
		});
		
		mTitleBarView.setBtnRightOnclickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showSearchDialog();
			}
		});
		btn_search.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String hphm = et_hphm.getText().toString().trim().toUpperCase();
				searcheCar(hphm);
			}
		});
		
		btn_clearup.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				et_hphm.setText("");
			}
		});
	}
	
	
	private void initDatas() {
		mCarList = new ArrayList<CarListInfoEntity>();
	}
	
	private String getUrl(){
		return ToolUtils.getVehChecking(mActivity);
	}
	
	
	
	private void getCarListNetwork(String url,final String hphm) {
		Logger.show(getTag(), "url="+url);
		Map<String, String> headers = new HashMap<String, String>();
		String session = (String) SharedPreferenceUtils.get(mActivity, CommonConstants.JSESSIONID, "");
		if(TextUtils.isEmpty(session)){
			return;
		}
		headers.put("Cookie", "JSESSIONID="+session);
		
		PostFormBuilder builder = OkHttpUtils.post().url(url).headers(headers);
		if(!TextUtils.isEmpty(hphm)){
			builder.addParams("statusArry", "0,1");
			builder.addParams("hphm", hphm);
			builder.addParams("page", "1");
			builder.addParams("rows", "20");
		}
		builder.build().execute(new UnfinishedListCarInfoCallback(){

			@Override
			public void onError(Call call, Exception e, int id) {
				ToastUtils.showToast(mActivity, mActivity.getString(R.string.not_connect_server), Toast.LENGTH_LONG);
				Logger.show(getTag(), e.getMessage());
				e.printStackTrace();
			}

			@Override
			public void onResponse(List<CarListInfoEntity> list, int id) {
				if(list!=null && list.size()>0){
					mCarList.clear();
					mCarList =list;
					viewSetAdapter();
				}else{
					ToastUtils.showToast(mActivity, "未搜索到该车", Toast.LENGTH_LONG);
				}
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
					mCallback.onUnfinishedCarListClick(mCarList.get(position));
				}
			}
		});
	}
	
	
	private String convertCode(String hpzlCode) {
		return ToolUtils.getLableByCode(mActivity, hpzlCode, R.array.hpzl, R.array.hpzl_code);
	}
	
	private void showSearchDialog(){
	    new CommonDialog(mActivity, R.layout.alert_edit_dialog,"输入号牌", getString(R.string.negative), getString(R.string.positive))
	    .setOnDiaLogListener(new OnDialogListener() {
			
			@Override
			public void dialogPositiveListener(View customView, DialogInterface dialogInterface, int which) {
				dialogInterface.dismiss();
				EditText et = (EditText) customView.findViewById(R.id.et_alert_content);
				searcheCar(et.getText().toString().trim().toUpperCase());
			}
			
			@Override
			public void dialogNegativeListener(View customView, DialogInterface dialogInterface, int which) {
				dialogInterface.dismiss();
			}
		}).showCustomDialog();
	}
	
	
	private void searcheCar(String hphm){
		if(hphm.length()<3){
			ToastUtils.showToast(mActivity, "号牌至少输入3位", Toast.LENGTH_SHORT);
			return;
		}
		getCarListNetwork(getUrl(), hphm);
	}

	
	
	public OnUnfinishedCarListClick mCallback;
	public interface OnUnfinishedCarListClick{
		public void onUnfinishedCarListClick(CarListInfoEntity carinfo);
	}
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCallback = (OnUnfinishedCarListClick) mActivity;
		} catch (ClassCastException e) {
			e.printStackTrace();
			throw new ClassCastException(mActivity.toString()
					+ " must implement OnUnfinishedCarListClick");
		}
	}
}
