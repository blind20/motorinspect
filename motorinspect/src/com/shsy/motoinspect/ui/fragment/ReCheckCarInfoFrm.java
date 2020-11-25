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
import com.shsy.motoinspect.adapter.GridCheckBoxAdapter;
import com.shsy.motoinspect.common.TitleBarView;
import com.shsy.motoinspect.entity.CarListInfoEntity;
import com.shsy.motoinspect.entity.JyxmInfo;
import com.shsy.motoinspect.entity.LineInfo;
import com.shsy.motoinspect.network.MyHttpUtils;
import com.shsy.motoinspect.utils.CommonDialog;
import com.shsy.motoinspect.utils.Logger;
import com.shsy.motoinspect.utils.ToastUtils;
import com.shsy.motoinspect.utils.ToolUtils;
import com.shsy.motoinspect.utils.CommonDialog.OnDialogListener;
import com.shsy.motorinspect.R;
import com.zhy.http.okhttp.callback.StringCallback;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
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

public class ReCheckCarInfoFrm extends BaseFragment implements View.OnClickListener{
	
	private boolean isRecycle =false;
	
	private TitleBarView mTitleBarView;
	
	private String TAG = getClass().getName();
	
	private ProgressDialog mProgressDlg;
	private CarListInfoEntity carInfo;
	
	private Spinner sp_to_line;
	private ArrayAdapter<String> spAdapter;
	
	private GridView mGridView;
	private GridCheckBoxAdapter gridAdapter;
	
	private List<JyxmInfo> mJyxmList;
	
	private List<LineInfo> mLineList;
	private String[] mLines;
	
	private Button btn_recheck;
	private CheckBox cb_weight;
	private Integer reloginWeigth;
	
	private TextView tv_to_line_type;
	private TextView tv_to_line_jzflag;
	private String to_jcxdh ;
	private String to_type ;
	private boolean to_jzflag = false;
	
	private TextView tv_from_line_num;
	private TextView tv_from_line_type;
	private TextView tv_from_line_jzflag;
	private String from_jcxdh ;
	private String from_type ;
	private boolean from_jzflag = false;
	
	
	@Override
	public int getLayoutResID() {
		return R.layout.frm_recheck_car;
	}

	@Override
	public void initParam() {
		initView();
		initData();
		viewSetAdapter();
//		getCheckEventStateNetwork(carInfo.getLsh());
		getLineNetwork();
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
		mTitleBarView.getTitleRight().setText("换线");
		mTitleBarView.setBtnLeftOnclickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				backKeyDown();
			}
		});
		
		
		mProgressDlg = new ProgressDialog(mActivity);
		sp_to_line=(Spinner)mRootView.findViewById(R.id.sp_to_line); 
		mGridView = (GridView)mRootView.findViewById(R.id.grid_check);
		tv_to_line_type = (TextView)mRootView.findViewById(R.id.tv_to_line_type); 
		tv_to_line_jzflag = (TextView)mRootView.findViewById(R.id.tv_to_line_jzflag);
		tv_from_line_num = (TextView)mRootView.findViewById(R.id.tv_from_line_num);
		tv_from_line_type = (TextView)mRootView.findViewById(R.id.tv_from_line_type); 
		tv_from_line_jzflag = (TextView)mRootView.findViewById(R.id.tv_from_line_jzflag);
		
		btn_recheck = (Button)mRootView.findViewById(R.id.btn_recheck); 
		btn_recheck.setOnClickListener(this);
		cb_weight =  (CheckBox)mRootView.findViewById(R.id.cb_weight);
		
		cb_weight.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked && mJyxmList.size()> 0) {
					for (JyxmInfo jyxminfo : mJyxmList) {
						if (jyxminfo.getJyxmzl().equals("B0")) {
							jyxminfo.setSelected(true);
							gridAdapter.setData(mJyxmList);
							gridAdapter.notifyDataSetChanged();
							break;
						}
					}
				}
			}
		});
	}
	
	private void initData() {
		tv_from_line_num.setText(carInfo.getJcxdh()+"号线");
		//列出所有登录检验项目:不合格项目强制勾选
		mJyxmList = initLoginJyxm(getLoginJyxmsByCarInfo(carInfo),getFjjyxmsByCarInfo(carInfo));
		
		mLineList = new ArrayList<LineInfo>();
	}
	
	private List<JyxmInfo> initLoginJyxm(String[] jyxmzlArray,String[] fjjyxms){
		String[] jyxms = getResources().getStringArray(R.array.jyxm);
		String[] jyxmzls = getResources().getStringArray(R.array.jyxmzl);
		List<JyxmInfo> list = new ArrayList<JyxmInfo>();
		for(int i=0;i<jyxmzls.length;i++){
			for(int j=0;j<jyxmzlArray.length;j++){
				if(jyxmzls[i].equals(jyxmzlArray[j])){
					JyxmInfo jyxmInfo = new JyxmInfo();
					jyxmInfo.setJyxm(jyxms[i]);
					jyxmInfo.setJyxmzl(jyxmzls[i]);
					jyxmInfo.setSelected(false);
					list.add(jyxmInfo);
					continue;
				}
			}
		}
		if(fjjyxms==null){
			return list;
		}else{
			for(JyxmInfo jyxmInfo : list){
				for(String fjjyxm: fjjyxms){
					if(jyxmInfo.getJyxmzl().equals(fjjyxm)){
						jyxmInfo.setSelected(true);
						continue;
					}
				}
			}
		}
		return list;
	}
	
	
	
	private void viewSetAdapter() {
		gridAdapter = new GridCheckBoxAdapter(mJyxmList,mActivity);
		mGridView.setAdapter(gridAdapter);
		mGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				GridCheckBoxAdapter.ViewHolder holder = (GridCheckBoxAdapter.ViewHolder)view.getTag();
				holder.cb.toggle();
				mJyxmList.get(position).setSelected(holder.cb.isChecked());
			}
		});
	}
	
	
	
	

	@Override
	public void onClick(View v) {
		if(v.getId()==R.id.btn_recheck){
			if(SingleClick.isSingle()){
				Logger.show(TAG, "间隔短于3秒算同一次点击");
				ToastUtils.showToast(mActivity, "间隔短于3秒算同一次点击", Toast.LENGTH_SHORT);
			}else{
				new AlertDialog.Builder(mActivity).setMessage("是否确定复检")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						onClickReCheckEvent();
					}
				}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				})
				.show();
				
			}
			
		}
		
	}

	private void onClickReCheckEvent() {
		String fjjyxm=getJyxmFromList(mJyxmList,true);
		if(!checkFjjyxmEmpty(fjjyxm)){
			if(fjjyxm.contains("L")&& !to_jzflag){
				ToastUtils.showToast(mActivity, "复检项目中包含了加载项目,但你选择的检测线是非加载检测线", Toast.LENGTH_LONG);
				return;
			}
			Logger.show(TAG, "按钮点击");
			checkReCheckJyxm(carInfo.getJcxdh(),to_jcxdh,fjjyxm);
			submitCarReCheck(carInfo,fjjyxm);
		}
	}

	
	private boolean checkFjjyxmEmpty(String fjjyxm) {
		if(TextUtils.isEmpty(fjjyxm)){
			Logger.show(getTag(), "fjjyxm="+fjjyxm);
			ToastUtils.showToast(mActivity, "没有选择复检项目", Toast.LENGTH_LONG);
			return true;
		}else{
			return false;
		}
	}
	
	
	/**
	 * 换线检测并且登录检验项目含有B:实现B0-B4都勾选
	 * @param from_jcxdh
	 * @param to_jcxdh
	 * @param jyxmzl
	 */
	private void checkReCheckJyxm(String from_jcxdh,String to_jcxdh,String jyxmzl) {
		if(!from_jcxdh.equals(to_jcxdh)){
			
			if(!TextUtils.isEmpty(jyxmzl)&&jyxmzl.contains("B")){
				for(JyxmInfo jyxmInfo: mJyxmList){
					if(jyxmInfo.getJyxmzl().contains("B")){
						jyxmInfo.setSelected(true);
					}
				}
				gridAdapter.setData(mJyxmList);
				gridAdapter.notifyDataSetChanged();
				cb_weight.setChecked(true);
			}
		}
		
	}

	private void getLineNetwork() {
		
		String url = ToolUtils.getLines(mActivity);
		Map<String, String> map = new HashMap<String, String>();
		MyHttpUtils.getInstance(mActivity).postHttpByParam(url, map, new StringCallback() {
			
			@Override
			public void onResponse(String response, int id) {
				Logger.show(getTag(), "res="+response);
				try {
					JSONArray jsons = new JSONArray(response);
					for(int i=0;i<jsons.length();i++){
						JSONObject jo = jsons.getJSONObject(i);
						LineInfo lineInfo = new LineInfo();
						lineInfo.setJcxdh(jo.getString("jcxdh"));
						lineInfo.setType(jo.getString("type"));
						lineInfo.setJzFlag(jo.getBoolean("jzFlag"));
						mLineList.add(lineInfo);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				setCurLineText(carInfo,mLineList);
				spinnerBindData(mLineList);
			}
			
			@Override
			public void onError(Call call, Exception e, int id) {
				ToastUtils.showToast(mActivity, "错误信息:"+e.getMessage(), Toast.LENGTH_LONG);
				Logger.show(getTag(), e.getMessage());
				e.printStackTrace();
			}
		});
	}
	
	private void getCheckEventStateNetwork(String jylsh) {
		
		String url = ToolUtils.getCheckEvents(mActivity);
		Map<String, String> map = new HashMap<String, String>();
		map.put("jylsh", jylsh);
		MyHttpUtils.getInstance(mActivity).postHttpByParam(url, map, new StringCallback() {
			
			@Override
			public void onResponse(String response, int id) {
				Logger.show(getTag(), "res="+response);
				try {
					JSONArray jsons = new JSONArray(response);
					if(jsons!=null &&jsons.length()>0){
						new AlertDialog.Builder(mActivity).setMessage("该车存在未完成联网失败事件,请联系大厅前台确认")
						.setPositiveButton("确定", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
							}
						}).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public void onError(Call call, Exception e, int id) {
				ToastUtils.showToast(mActivity, "获取联网事件失败错误信息,"+e.getMessage(), Toast.LENGTH_SHORT);
				Logger.show(getTag(), e.getMessage());
				e.printStackTrace();
			}
		});
	}
	
	
	private void spinnerBindData(List<LineInfo> list){
		
		if(list.size()==0 || list.isEmpty()){
			return;
		}
		mLines = new String[list.size()+1];
		int index=0;
		for(LineInfo lineInfo: list){
			mLines[++index] = lineInfo.getJcxdh();
		}
		mLines[0] = "请选择线号";
		
		spAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_dropdown,mLines);
		sp_to_line.setAdapter(spAdapter);
		
		sp_to_line.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if(position>0){
					to_jcxdh = mLineList.get(position-1).getJcxdh();
					to_type = mLineList.get(position-1).getType();
					to_jzflag = mLineList.get(position-1).getJzFlag();
					tv_to_line_type.setText(to_type.equals(CommonConstants.CYLINDERLINE)?getString(R.string.line_cylinder):getString(R.string.line_plate));
					tv_to_line_jzflag.setText(to_jzflag?getString(R.string.line_is_jz):getString(R.string.line_not_jz));
					
					String fjjyxmzl=getJyxmFromList(mJyxmList,true);
					checkReCheckJyxm(carInfo.getJcxdh(), to_jcxdh, fjjyxmzl);
				}
				if(position==0){
					tv_to_line_type.setText("");
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}
	
	
	private void setCurLineText(CarListInfoEntity car,List<LineInfo> list){
		if(car ==null || list.isEmpty()){
			return;
		}
		String jcxdh = car.getJcxdh();
		String lineTyep="" ;
		boolean jzFlag=false;
		for(LineInfo line : list){
			if(line.getJcxdh().equals(jcxdh)){
				lineTyep = line.getType();
				jzFlag = line.getJzFlag();
				break;
			}
		}
		tv_from_line_type.setText(lineTyep.equals(CommonConstants.CYLINDERLINE)?getString(R.string.line_cylinder):getString(R.string.line_plate));
		tv_from_line_jzflag.setText(jzFlag?getString(R.string.line_is_jz):getString(R.string.line_not_jz));
	}
	
	
	private void submitCarReCheck(CarListInfoEntity car,String fjjyxm){
		mProgressDlg.show();
		reloginWeigth = cb_weight.isChecked()?1:0;
		String url = ToolUtils.relogin(mActivity);
		Map<String, String> map = new HashMap<String, String>();
		map.put("jylsh", car.getLsh());
		map.put("id", car.getId());
		map.put("fjjyxm", fjjyxm);
		map.put("jcxdh", mLines[sp_to_line.getSelectedItemPosition()]);
		map.put("reloginWeigth", String.valueOf(reloginWeigth));
		MyHttpUtils.getInstance(mActivity).postHttpByParam(url, map, new StringCallback() {
			
			@Override
			public void onResponse(String response, int id) {
				mProgressDlg.dismiss();
				try {
					JSONObject jo = new JSONObject(response);
					String state = jo.getString("state");
					if(state.equals("OK")){
						showSubmitOkDlg(null);
					}else{
						String msg = jo.getString("message");
						if(TextUtils.isEmpty(msg)){
							msg ="返回信息为空";
						}
						showSubmitOkDlg(msg);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public void onError(Call call, Exception e, int id) {
				mProgressDlg.dismiss();
				ToastUtils.showToast(mActivity, "错误信息:"+e.getMessage(), Toast.LENGTH_LONG);
				Logger.show(getTag(), e.getMessage());
				e.printStackTrace();
			}
		});
	}
	
	
	private String[] getLoginJyxmsByCarInfo(CarListInfoEntity car){
		String jyxm = car.getJyxm();
		if(TextUtils.isEmpty(jyxm)){
			return null;
		}
		return jyxm.split(",");
	}
	
	private String[] getFjjyxmsByCarInfo(CarListInfoEntity car){
		String fjjyxm = car.getFjjyxm();
		Logger.show(TAG, "fjjyxm="+fjjyxm);
		if(TextUtils.isEmpty(fjjyxm)){
			return null;
		}
		return fjjyxm.split(",");
	}
	
	/**
	 * @param list  所有登录检验项目LIST
	 * @param isFjjyxm  是否取出复检检验项目
	 * @return 1返回所有登录检验项目2返回复检检验项目
	 */
	private String getJyxmFromList(List<JyxmInfo> list,boolean isFjjyxm){
		String jyxm="";
		if(isFjjyxm){
			for (JyxmInfo jyxmInfo : list) {
				if(jyxmInfo.getSelected()){
					jyxm = jyxm+","+ jyxmInfo.getJyxmzl();
				}
			}
		}else{
			for (JyxmInfo jyxmInfo : list) {
				jyxm = jyxm+","+ jyxmInfo.getJyxmzl();
			}
		}
		return jyxm.length()>1?jyxm.substring(1):jyxm;
	}
	
	
	private void showSubmitOkDlg(String msg) {
		if(msg==null){
			new AlertDialog.Builder(mActivity).setMessage("复检请求成功")
			.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					backKeyDown();
				}
			}).show();
		}else{
			new AlertDialog.Builder(mActivity).setMessage(msg)
			.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
//					backKeyDown();
				}
			}).show();
		}
		
    }
	
	
	
	
	private void backKeyDown(){
		FragmentManager manager = getActivity().getSupportFragmentManager();
		if(manager.getBackStackEntryCount() != 0){
			manager.popBackStack();
		}
	}
	
	
	public static class SingleClick {
		private static final int DEFAULT_TIME = 3000;
		private static long lastTime;

		public static boolean isSingle() {
			boolean isSingle;
			long currentTime = System.currentTimeMillis();
			if (currentTime - lastTime <= DEFAULT_TIME) {
				isSingle = true;
			} else {
				isSingle = false;
			}
			lastTime = currentTime;

			return isSingle;
		}
	}
	
}
