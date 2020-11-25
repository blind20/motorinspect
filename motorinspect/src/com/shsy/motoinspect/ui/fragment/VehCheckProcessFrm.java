package com.shsy.motoinspect.ui.fragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.shsy.motoinspect.BaseFragment;
import com.shsy.motoinspect.CommonConstants;
import com.shsy.motoinspect.adapter.GridCheckBoxAdapter;
import com.shsy.motoinspect.common.CommonAdapter;
import com.shsy.motoinspect.common.TitleBarView;
import com.shsy.motoinspect.common.ViewHolder;
import com.shsy.motoinspect.entity.CarListInfoEntity;
import com.shsy.motoinspect.entity.JyxmInfo;
import com.shsy.motoinspect.entity.LineInfo;
import com.shsy.motoinspect.entity.VehCheckProcess;
import com.shsy.motoinspect.network.MyHttpUtils;
import com.shsy.motoinspect.utils.CommonDialog;
import com.shsy.motoinspect.utils.Logger;
import com.shsy.motoinspect.utils.ToastUtils;
import com.shsy.motoinspect.utils.ToolUtils;
import com.shsy.motoinspect.utils.CommonDialog.OnDialogListener;
import com.shsy.motorinspect.R;
import com.zhy.http.okhttp.callback.StringCallback;

import android.R.color;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import okhttp3.Call;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.ListView;

public class VehCheckProcessFrm extends BaseFragment {
	
	private boolean isRecycle =false;
	
	private TitleBarView mTitleBarView;
	
	private CommonAdapter<VehCheckProcess> mAdapter;
	private ListView mListView;
	
	private String TAG = getClass().getName();
	
	private ProgressDialog mProgressDlg;
	private CarListInfoEntity carInfo;
	
	
	private TextView tv_to_line_type;
	private TextView tv_to_line_jzflag;
	private TextView tv_from_line_num;
	private TextView tv_from_line_type;
	
	private String to_jcxdh ;
	private String to_type ;
	private String from_jcxdh ;
	private String from_type ;
	
	private List<VehCheckProcess> mProcessList;
	@Override
	public int getLayoutResID() {
		return R.layout.frm_check_process;
	}

	@Override
	public void initParam() {
		initView();
		initData();
		viewSetAdapter();
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
	}
	

	
	private void initView() {
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
		mListView = (ListView) mRootView.findViewById(R.id.lv_carinspect);
		
		mProgressDlg = new ProgressDialog(mActivity);
		
		
	}
	
	private void initData() {
		mProcessList = new ArrayList<VehCheckProcess>();
		getCheckProcessNetwork();
	}
	
	
	
	
	
	private void viewSetAdapter() {
		mAdapter = new CommonAdapter<VehCheckProcess>(mProcessList,mActivity,R.layout.item_check_process) {
			@Override
			public void convert(ViewHolder holder, VehCheckProcess t) {
				if(t.getJycs()==-1){
					holder.setText(R.id.tv_jyxm, "检验项目")
					.setText(R.id.tv_jycs, "检验次数")
					.setText(R.id.tv_kssj, "开始时间")
					.setText(R.id.tv_jssj, "结束时间")
					.setText(R.id.tv_status, "检测状态");
					
				}else{
					holder.setText(R.id.tv_jyxm, convertJyxm(t.getJyxm()))
					.setText(R.id.tv_jycs, Integer.toString(t.getJycs()));
					if(t.getKssj()!=null){
						holder.setText(R.id.tv_kssj, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(t.getKssj()));
					}else{
						holder.setText(R.id.tv_kssj, "--");
					}
					if(t.getJssj()!=null){
						holder.setText(R.id.tv_jssj, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(t.getJssj()));
					}else{
						holder.setText(R.id.tv_jssj, "--");
					}
					holder.setText(R.id.tv_status, showJyzt(t.getJyzt()));
				}
				if(!TextUtils.isEmpty(t.getJyzt())&& t.getJycs()!=-1&&t.getJyzt().equals("2")){
					holder.getConvertView().setBackgroundResource(android.R.color.holo_orange_light);
				}else{
					holder.getConvertView().setBackgroundResource(android.R.color.white);
				}
				
			}
		};
		mListView.setAdapter(mAdapter);
	}
	

	private void getCheckProcessNetwork() {
		
		String url = ToolUtils.getProcess(mActivity);
		Map<String, String> map = new HashMap<String, String>();
		map.put("jylsh", carInfo.getLsh());
		MyHttpUtils.getInstance(mActivity).postHttpByParam(url, map, new StringCallback() {
			
			@Override
			public void onResponse(String response, int id) {
				Logger.show(getTag(), "res="+response);
				mProcessList = getProcessByRes(response);
				viewSetAdapter();
			}
			
			@Override
			public void onError(Call call, Exception e, int id) {
				ToastUtils.showToast(mActivity, "错误信息:"+e.getMessage(), Toast.LENGTH_LONG);
				Logger.show(getTag(), e.getMessage());
				e.printStackTrace();
			}
		});
	}
	
	
	
	
	private List<VehCheckProcess> getProcessByRes(String response){
		List<VehCheckProcess> list= new ArrayList<VehCheckProcess>();
		try {
			JSONArray ja = new JSONArray(response);
			for (int i = 0; i < ja.length(); i++) {
				JSONObject jo = ja.getJSONObject(i);
				VehCheckProcess checkprocess = new VehCheckProcess();
				String jylsh = jo.getString("jylsh");
				String hphm = jo.getString("hphm");
				String hpzl = jo.getString("hpzl");
				String clsbdh = jo.getString("clsbdh");
				String jyxm = jo.getString("jyxm");
				
				if(!isAnjianJyxm(jyxm)){
					continue;
				}
				
				int jycs = jo.getInt("jycs");
				String kssj = jo.getString("kssj");
				String jssj = jo.getString("jssj");
				checkprocess.setJylsh(jylsh);
				checkprocess.setHphm(hphm);
				checkprocess.setHpzl(hpzl);
				checkprocess.setClsbdh(clsbdh);
				checkprocess.setJyxm(jyxm);
				checkprocess.setJycs(jycs);
				checkprocess.setJyzt(calcJyzt(kssj, jssj));
				
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		        Date dateTime = null;
				try {
					if (!TextUtils.isEmpty(kssj) && !kssj.equals("null")) {
						dateTime = simpleDateFormat.parse(kssj);
						checkprocess.setKssj(dateTime);
					}
					if (!TextUtils.isEmpty(jssj) && !jssj.equals("null")) {
						dateTime = simpleDateFormat.parse(jssj);
						checkprocess.setJssj(dateTime);
					}
				} catch (ParseException e) {
					e.printStackTrace();
					Log.e("ParseException", "ParseException="+e.getMessage());
				}
				
				list.add(checkprocess);
			} 
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("getProcessByRes", "exception="+e.getMessage());
		}
		Collections.sort(list);
		Collections.reverse(list);
		list.add(0,new VehCheckProcess(null,null,null,null,"检验项目",null,null,-1));
		return list;
	}
	
	private String calcJyzt(String kssj,String jssj){
		if(!TextUtils.isEmpty(kssj) && !kssj.equals("null") && !TextUtils.isEmpty(jssj) && !jssj.equals("null")){
			return "1";//已完成
		}else if(TextUtils.isEmpty(kssj) || kssj.equals("null") ){
			if(TextUtils.isEmpty(jssj) || jssj.equals("null")){
				return "0";
			}else{
				return "-1";
			}
		}else if(TextUtils.isEmpty(jssj) || jssj.equals("null") ){
			if(TextUtils.isEmpty(kssj) || jssj.equals("kssj")){
				return "0";
			}else{
				return "2";
			}
		}
		return "1";
	}
	
	private String showJyzt(String jyzt){
		if(jyzt.equals("1")){
			return "已完成";
		}else if(jyzt.equals("0")){
			return "未开始";
		}else if(jyzt.equals("2")){
			return "未结束";
		}else{
			return "异常";
		}
	}
	
	private String convertJyxm(String jyxm){
		return ToolUtils.getLableByCode(mActivity, jyxm, R.array.jyxm, R.array.jyxmzl);
	}
	private boolean isAnjianJyxm(String jyxm){
		String[] jyxms = getResources().getStringArray(R.array.jyxmzl);
		if(jyxm.equals("Z1")||jyxm.equals("M1")){
			return false;
		}
		for(String xm:jyxms){
			if(xm.equals(jyxm)){
				return true;
			}else{
				continue;
			}
		}
		return false;
	}
	
	
	private void backKeyDown(){
		FragmentManager manager = getActivity().getSupportFragmentManager();
		if(manager.getBackStackEntryCount() != 0){
			manager.popBackStack();
		}
	}
}
