package com.shsy.motoinspect.ui.fragment;

import java.util.ArrayList;
import java.util.Collections;
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
import com.shsy.motoinspect.entity.LineInfo;
import com.shsy.motoinspect.network.ListCarInfoCallback;
import com.shsy.motoinspect.network.MyHttpUtils;
import com.shsy.motoinspect.ui.activity.OuterInspectActivity;
import com.shsy.motoinspect.ui.activity.SettingsActivity;
import com.shsy.motoinspect.ui.fragment.ReCheckCarInfoFrm.SingleClick;
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
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;
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
	public static final int REQ_PULLCAR_RECEIPT_DLG =0x120;
	
	public static final int TO_ROADTESTFRM = 0x100;
	public static final String ROADTEST_HPHM ="hphm";
	public static final String ROADTEST_JYLSH ="jylsh";
	
	private String[] mPeriods = {"三天内", "一周内", "两周内", "一月内", "三月内"};
	private List<CarListInfoEntity> mTotalList;
	
	private Integer mPosition;
	private String msgFormat;
	
	//检测线号列表对话框
	private int mSelectJcxdh;
	private int mLineIndex;
	
	private List<LineInfo> mLineList;
	
	private EditText et_hphm;
	private Button btn_search;
	private Button btn_clearup;
	
	private boolean mIsSelectLine;
	
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case REFRESH_COMPLETE:
				getCarListNetwork(mType);
				if (!mCarList.isEmpty()) {
					adpter.notifyDataSetChanged();
				}
				mSwipeLayout.setRefreshing(false);
				mTitleBarView.getTitleRight().setText(mPeriods[0]);
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
		return CommonConstants.ROADTEST;
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
		return R.layout.frm_pullcar;
	}

	public void initView(int type) {
		mTitleBarView = new TitleBarView(mActivity);
		mTitleBarView = (TitleBarView) mRootView.findViewById(R.id.titlebar);
		mTitleBarView.setCommonTitle(View.VISIBLE, View.VISIBLE, View.VISIBLE);
		mSwipeLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.id_swipe_ly);
		et_hphm = (EditText) mRootView.findViewById(R.id.et_hphm);
		btn_search = (Button) mRootView.findViewById(R.id.btn_search);
		btn_clearup = (Button) mRootView.findViewById(R.id.btn_clearup);
		switch (type) {
			case CommonConstants.PULLCAR:
				mTitleBarView.setTitle(R.string.pullcar);
				break;
	
			case CommonConstants.ROADTEST:
				mTitleBarView.setTitle(R.string.roadtest_pullcar);
				break;
		}
		
		mTitleBarView.setBtnLeftOnclickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mCallback.onPullBack();
			}
		});
		mTitleBarView.getTitleRight().setText(mPeriods[0]);
		mTitleBarView.setBtnRightOnclickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onPopupWindow();
			}
		});
		
		mListView = (ListView) mRootView.findViewById(R.id.lv_carinspect);
		mSwipeLayout.setOnRefreshListener(this);
		btn_search.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String hphm = et_hphm.getText().toString().trim().toUpperCase();
				if(hphm.length()<3){
					ToastUtils.showToast(mActivity, "请输入至少3个字符", 1);
					return;
				}
				mCarList = searchVeh(hphm);
				if(mCarList.size()>0){
					adpter.setDatas(mCarList);
					adpter.notifyDataSetChanged();
				}else{
					ToastUtils.showToast(mActivity, "搜索不到该车", 1);
				}
			}
		});
		btn_clearup.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				et_hphm.setText("");
			}
		});
	}
	
	private List<CarListInfoEntity> searchVeh(String hphm) {
		List<CarListInfoEntity> list = new ArrayList<CarListInfoEntity>();
		for(CarListInfoEntity car : mTotalList){
			if(car.getHphm().contains(hphm)){
				list.add(car);
			}
		}
		if(list!=null&&list.size()>0){
			Collections.reverse(list);
		}
		return list;
	}
	
	protected void onPopupWindow() {
		View popupView = mActivity.getLayoutInflater().inflate(R.layout.popupwindow, null);
		ListView lvPeriod = (ListView) popupView.findViewById(R.id.lv_period);
		lvPeriod.setAdapter(new ArrayAdapter<String>(mActivity, android.R.layout.simple_list_item_1, mPeriods));
		final PopupWindow window = new PopupWindow(popupView, 400, 600);
        window.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F8F8F8")));
        window.setFocusable(true);
        window.setOutsideTouchable(true);
        window.update();
        window.showAsDropDown(mTitleBarView.getTitleRight(), 0, 5);
        
        lvPeriod.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				int period = CommonConstants.DAYS3;
				switch (position) {
				case 0:
					period =CommonConstants.DAYS3;;
					mTitleBarView.getTitleRight().setText(mPeriods[0]);
					break;
				case 1:
					period =CommonConstants.DAYS7;
					mTitleBarView.getTitleRight().setText(mPeriods[1]);
					break;
				case 2:
					period =CommonConstants.DAYS14;
					mTitleBarView.getTitleRight().setText(mPeriods[2]);
					break;
				case 3:
					period =CommonConstants.DAYS30;
					mTitleBarView.getTitleRight().setText(mPeriods[3]);
					break;
				case 4:
					period =CommonConstants.DAYS90;
					mTitleBarView.getTitleRight().setText(mPeriods[4]);
					break;
				}
				mCarList = getListByPeriod(mTotalList, period);
//				ToastUtils.showToast(mActivity, "size="+mCarList.size(), Toast.LENGTH_LONG);
				if(mCarList.size()!=0){
					adpter.setDatas(mCarList);
					adpter.notifyDataSetChanged();
				}
				window.dismiss();
			}
		});
	}
	

	private void initDatas(int type) {
		mIsSelectLine = (Boolean) SharedPreferenceUtils.get(mActivity, CommonConstants.IS_SELECT_LINE, false); 
		mLineList = new ArrayList<LineInfo>();
		mCarList = new ArrayList<CarListInfoEntity>();
		mTotalList = new ArrayList<CarListInfoEntity>();
		getLineNetwork();
		getCarListNetwork(type);
	}

	
	private void getLineNetwork() {
		String url = ToolUtils.getLines(mActivity);
		Map<String, String> map = new HashMap<String, String>();
		MyHttpUtils.getInstance(mActivity).postHttpByParam(url, map, new StringCallback() {
			
			@Override
			public void onResponse(String response, int id) {
				Logger.show(getTag(), "getLineNetwork="+response);
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
			}
			
			@Override
			public void onError(Call call, Exception e, int id) {
				ToastUtils.showToast(mActivity, "获取不到检测线,原因:"+e.getMessage(), Toast.LENGTH_LONG);
				Logger.show(getTag(), e.getMessage());
				e.printStackTrace();
			}
		});
	}
	

	private String getAvailUrl(int type) {
		String url = null;
		switch (type) {
			case CommonConstants.PULLCAR:
				url = ToolUtils.pullCarsUrl(mActivity);
				break;
	
			case CommonConstants.ROADTEST:
				url = ToolUtils.getExternalRURL(mActivity);
				break;

		}
		return url;
	}


	private void getCarListNetwork(final int type) {
		final String url = getAvailUrl(type);
		Map<String, String> headers = new HashMap<String, String>();
		String session = (String) SharedPreferenceUtils.get(mActivity, CommonConstants.JSESSIONID, "");
		if(TextUtils.isEmpty(session)){
			return;
		}
		headers.put("Cookie", "JSESSIONID="+session);
		
		PostFormBuilder postFormBuilder = OkHttpUtils.post()
				.url(url).headers(headers);
		
		if(type ==CommonConstants.PULLCAR){
			postFormBuilder.addParams("status", Integer.toString(CommonConstants.WAITPULLCAR));
		}
		
		postFormBuilder.build()
		.execute(new ListCarInfoCallback(){

			@Override
			public void onError(Call call, Exception e, int id) {
				Logger.show(getTag(), e.getMessage());
				e.printStackTrace();
			}

			@Override
			public void onResponse(List<CarListInfoEntity> list, int id) {
				mTotalList = list;
				mCarList.clear();
				mCarList.addAll(getListByPeriod(mTotalList, CommonConstants.DAYS3));
				viewSetAdapter(type);
			}
			
		});
	}

	

	private void viewSetAdapter(final int type) {
		
		
		adpter = new CommonAdapter<CarListInfoEntity>(mCarList,mActivity,R.layout.item_pullcar_list) {

			@Override
			public void convert(ViewHolder holder, CarListInfoEntity t) {
				String lineNameFormat = getString(R.string.check_line_num);
				if(type == CommonConstants.PULLCAR){
					pullcarConvertView(lineNameFormat, holder, t);
				}else{
					roadtestConvertView(holder,t);
				}
				
			}

		};
		
		mListView.setAdapter(adpter);
		listviewItemClick(type);
	}
	


	/**
	 * 路试每个Item的渲染
	 * @param holder
	 * @param t
	 */
	private void roadtestConvertView(ViewHolder holder, CarListInfoEntity t) {
		holder.setText(R.id.tv_hphm, t.getHphm())
		  .setText(R.id.tv_hpzl, convertCode(t.getHpzl()))
		  .setText(R.id.tv_lsh, t.getLsh())
		  .setText(R.id.tv_date, t.getDate());
		holder.getView(R.id.tv_checkline).setVisibility(View.GONE);
		holder.getView(R.id.btn_pullcar).setVisibility(View.GONE);
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
	
	
	
	
	/**
	 * 引车上线每个Item的渲染,Button的点击事件
	 * @param lineNameFormat
	 * @param holder
	 * @param t
	 */
	private void pullcarConvertView(String lineNameFormat, final ViewHolder holder,
			final CarListInfoEntity t) {
		String sFinal = String.format(lineNameFormat, t.getJcxdh());
		
		holder.setText(R.id.tv_hphm, t.getHphm())
		  .setText(R.id.tv_hpzl, convertCode(t.getHpzl()))
		  .setText(R.id.tv_lsh, t.getLsh())
		  .setText(R.id.tv_date, t.getDate())
		  .setText(R.id.tv_checkline,sFinal);
		if(t.getCheckType()==1){
			if(t.getZjlb()==0){
				holder.setText(R.id.tv_checktype, "【综安】");
			}else{
				holder.setText(R.id.tv_checktype, "【综检】");
			}
		}else{
			holder.setText(R.id.tv_checktype, "【安检】");
		}
		
		msgFormat = getString(R.string.is_confirm_pullcar);

		holder.setButtonListen(R.id.btn_pullcar, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mPosition = holder.getPosition();
				
				if(mIsSelectLine){
					
					if(SingleClick.isSingle()){
						Logger.show("PullCarTOLineFrm", "间隔短于3秒算同一次点击");
						ToastUtils.showToast(mActivity, "间隔短于3秒算同一次点击", Toast.LENGTH_SHORT);
					}else{
						//新版本引车上线换线号,上传线号
						final CarListInfoEntity carInfo = mCarList.get(mPosition);
						selectLineDialog(mPosition,mLineList);
					}
				}else{
					//匹配老版本后台:默认线号,不上传线号
					MyDialogFragment dialog = MyDialogFragment.newInstance(MyDialogFragment.DLG_CONFIRM, 
																t.getHphm(),
																String.format(msgFormat, t.getHphm()),
																REQ_CONFIRM_DLG);
					dialog.setTargetFragment(PullCarToLineFrm.this, REQ_CONFIRM_DLG);
					dialog.show(getFragmentManager(), "");
				}
			}
		});
	}
	
	
	
	private void selectLineDialog(final int position,final List<LineInfo> list) {
		if(list==null||list.size()==0){
			ToastUtils.showToast(mActivity, "未获取检测线列表...", Toast.LENGTH_SHORT);
			return;
		}
		final String items[] = new String[list.size()];
		for(int i=0;i<list.size();i++){
			items[i]=list.get(i).getJcxdh()+"号线";
		}
		final String jcxdh = mCarList.get(position).getJcxdh();
		final String jyxm = mCarList.get(position).getJyxm();
		final String hphm = mCarList.get(position).getHphm();
		if(TextUtils.isEmpty(jcxdh)){
			mLineIndex = 0;
		}else{
			mLineIndex = Integer.parseInt(jcxdh)-1;
		}
		//如果列表对话框直接点确定,不会进入onclick,值为null
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		builder.setTitle(hphm+"请选择检测线号")
		.setSingleChoiceItems(items, mLineIndex, new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			mLineIndex = which;
			
		}}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		}).setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				LineInfo line = list.get(mLineIndex);
				if(jyxm.contains("L") && !line.getJzFlag()){
					ToastUtils.showToast(mActivity, "检验项目中包含了加载项目,但你选择的检测线是非加载检测线", Toast.LENGTH_LONG);
					return;
				}else{
					String url = ToolUtils.pushCarOnlineUrl(mActivity);
					pushCarOnLineNetwork(position,url,mType,line.getJcxdh());
				}
			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();
		dialog.setCanceledOnTouchOutside(true);
	}
	

	
	private void listviewItemClick(int type) {
		if(type == CommonConstants.PULLCAR){
			return;
		}else{
			mListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					Intent intent = new Intent(mActivity, SettingsActivity.class);
					Bundle bundle = new Bundle();
					bundle.putInt(CommonConstants.TO_SETTING, PullCarToLineFrm.TO_ROADTESTFRM);
					bundle.putString(PullCarToLineFrm.ROADTEST_HPHM, mCarList.get(position).getHphm());
					bundle.putString(PullCarToLineFrm.ROADTEST_JYLSH, mCarList.get(position).getLsh());
					intent.putExtras(bundle);
					startActivity(intent);
				}
			});;
		}
	}
	
	
	private void pushCarOnLineNetwork(final int position, String pushCarOnlineUrl,int type,String jcxdh) {
		Map<String, String> headers = new HashMap<String, String>();
		String session = (String) SharedPreferenceUtils.get(mActivity, CommonConstants.JSESSIONID, "");
		if(TextUtils.isEmpty(session)){
			return;
		}
		headers.put("Cookie", "JSESSIONID="+session);
		
		PostFormBuilder builder = OkHttpUtils.post().url(pushCarOnlineUrl).headers(headers)
				.addParams("id", mCarList.get(position).getId()+"");
		if(!TextUtils.isEmpty(jcxdh)){
			builder.addParams("jcxdh", jcxdh);
		}
		builder.build().execute(new StringCallback() {
			@Override
			public void onResponse(String response, int id) {
				try {
					JSONObject jo = new JSONObject(response);
					Integer state = (Integer) jo.get("state");
					if(CommonConstants.STATAS_SUCCESS==state){
						mCarList.remove(position);
						adpter.notifyDataSetChanged();
//						onPullVehLsnr.onPullVeh();
					}else{
						String message = jo.getString("message");
						MyDialogFragment dialog = MyDialogFragment.newInstance(MyDialogFragment.DLG_CONFIRM, 
																				mCarList.get(position).getHphm(),
																				message,
																				REQ_PULLCAR_RECEIPT_DLG);
						
						dialog.setTargetFragment(PullCarToLineFrm.this, REQ_PULLCAR_RECEIPT_DLG);
						dialog.show(getFragmentManager(), "");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onError(Call call, Exception e, int id) {
				Logger.show(getClass().getName(), "onItemClick onError " + e.getMessage());
				ToastUtils.showToast(mActivity, "该车引车上线失败"+e.getMessage(), Toast.LENGTH_LONG);
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
			//引车或者路试
			String url = ToolUtils.pushCarOnlineUrl(mActivity);
			Logger.show("pullcar==", "mPosition"+mPosition);
			pushCarOnLineNetwork(mPosition,url,mType,null);
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
	
	
	private List<CarListInfoEntity> getListByPeriod(List<CarListInfoEntity> list,int period){
		List<CarListInfoEntity> periodList = new ArrayList<CarListInfoEntity>();
		for(CarListInfoEntity carListInfoEntity:list){
			String date = carListInfoEntity.getDate();
			if(DateUtils.compareDate(date, period)){
				periodList.add(carListInfoEntity);
			}
		}
		Collections.reverse(periodList);
		return periodList;
	}
	
	
	
	protected OnPullBackListener mCallback;
	public interface OnPullBackListener{
		public void onPullBack();
	}
	protected OnPullVehLsnr onPullVehLsnr;
	public interface OnPullVehLsnr{
		public void onPullVeh();
	}
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCallback = (OnPullBackListener) mActivity;
			onPullVehLsnr = (OnPullVehLsnr) mActivity;
		} catch (ClassCastException e) {
			e.printStackTrace();
			throw new ClassCastException(mActivity.toString()
					+ " must implement OnCarItemSelListener");
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
