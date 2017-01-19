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
import com.shsy.motoinspect.ui.activity.OuterInspectActivity;
import com.shsy.motoinspect.ui.fragment.ResetWorkFrm.OnResetBackListener;
import com.shsy.motoinspect.ui.fragment.ResetWorkFrm.WorkStation;
import com.shsy.motoinspect.utils.A2bigA;
import com.shsy.motoinspect.utils.Logger;
import com.shsy.motoinspect.utils.SharedPreferenceUtils;
import com.shsy.motoinspect.utils.ToastUtils;
import com.shsy.motoinspect.utils.ToolUtils;
import com.shsy.motorinspect.R;
import com.zhy.http.okhttp.OkHttpUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import okhttp3.Call;

public class RePhotoFrm extends BaseFragment {
	
	private TitleBarView mTitleBarView;
	private ListView mListView;
	private Button searchBtn;
	private EditText et_hphm;
	private TextView tv_hint;
	private ProgressDialog mProgressDlg;
	
	private List<CarListInfoEntity> mCarList;
	private CommonAdapter<CarListInfoEntity> adapter;

	private boolean isRecycle =false;
	
	
	public RePhotoFrm() {
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
	public int getLayoutResID() {
		return R.layout.frm_rephoto;
	}

	@Override
	public void initParam() {
		initView();
		initDatas();
		initListener();
	}

	private void initView() {
		mTitleBarView = new TitleBarView(mActivity);
		mTitleBarView = (TitleBarView) mRootView.findViewById(R.id.titlebar);
		mTitleBarView.setCommonTitle(View.VISIBLE, View.VISIBLE, View.GONE);
		mTitleBarView.setTitle(R.string.outer_nav1_menu4);
		
		mListView = (ListView) mRootView.findViewById(R.id.listview);
		searchBtn = (Button) mRootView.findViewById(R.id.btn_search);
		et_hphm = (EditText) mRootView.findViewById(R.id.et_hphm);
		et_hphm.setTransformationMethod(new A2bigA());
		tv_hint =  (TextView) mRootView.findViewById(R.id.tv_hint);
		tv_hint.setText(getString(R.string.rephoto_hint));
		tv_hint.setTextColor(getResources().getColor(R.color.deep_red));
	}

	
	private void initDatas() {
		mCarList = new ArrayList<CarListInfoEntity>();
	}
	
	
	private void initListener() {
		
		mTitleBarView.setBtnLeftOnclickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mCallback.onRePhotoBack();
			}
		});
		
		searchBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String hphm = et_hphm.getText().toString(); 
				if(hphm.length()<4){
					ToastUtils.showToast(mActivity, "ÇëÊäÈëÕýÈ·ºÅÅÆºÅÂë", 1);
					return;
				}
				String url = ToolUtils.getVehInOfHphmUrl(mActivity);
				getVehListByNetWork(hphm,url);
				showProgressDlg();
			}
		});
		
	}
	
	
	private void showProgressDlg(){
		mProgressDlg = new ProgressDialog(mActivity);
		mProgressDlg.setMessage("²éÕÒ¸ÃºÅÅÆ³µÁ¾...");
		mProgressDlg.show();
	}
	
	
	
	private void getVehListByNetWork(String hphm,String url) {
		Map<String, String> headers = new HashMap<String, String>();
		String session = (String) SharedPreferenceUtils.get(mActivity, CommonConstants.JSESSIONID, "");
		if (TextUtils.isEmpty(session)) {
			return;
		}
		headers.put("Cookie", "JSESSIONID=" + session);

		OkHttpUtils
		.post()
		.url(url)
		.headers(headers)
		.addParams("hphm", hphm).build()
		.execute(new ListCarInfoCallback() {

			@Override
			public void onError(Call call, Exception e, int id) {
				mProgressDlg.dismiss();
				tv_hint.setVisibility(View.VISIBLE);
				tv_hint.setTextColor(getResources().getColor(R.color.deep_red));
				tv_hint.setText(getString(R.string.network_error));
				mCarList.clear();
				viewSetAdapter(mCarList);
				e.printStackTrace();
			}

			@Override
			public void onResponse(List<CarListInfoEntity> list, int id) {
				tv_hint.setVisibility(View.VISIBLE);
				if(list.size()==0){
					tv_hint.setTextColor(getResources().getColor(R.color.deep_red));
					tv_hint.setText(getString(R.string.rephoto_hint1));
				}else{
					tv_hint.setTextColor(getResources().getColor(R.color.text_color_gray));
					tv_hint.setText(getString(R.string.rephoto_hint2));
				}
				mCarList.clear();
				mCarList.addAll(list);
				viewSetAdapter(mCarList);
			}

		});
	}
	
	
	
	private void viewSetAdapter(List<CarListInfoEntity> list) {
		mProgressDlg.dismiss();
		adapter = new CommonAdapter<CarListInfoEntity>(list,mActivity,R.layout.item_carinspect_list) {

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
				
				Intent intent = new Intent(mActivity, OuterInspectActivity.class);
				Bundle bundle = new Bundle();
				bundle.putParcelable(CommonConstants.BUNDLE_TO_OUTER, mCarList.get(position));
				bundle.putInt(OuterCheckFrm.OUTCHECKTYPE, CommonConstants.REPHOTO);
				intent.putExtras(bundle);
				startActivity(intent);
			}
			
		});
	}
	
	
	
	private String convertCode(String hpzlCode) {
		return ToolUtils.getLableByCode(mActivity, hpzlCode, R.array.hpzl, R.array.hpzl_code);
	}
	
	
	protected OnRePhotoBackListener mCallback;
	public interface OnRePhotoBackListener{
		public void onRePhotoBack();
	}
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCallback = (OnRePhotoBackListener) mActivity;
		} catch (ClassCastException e) {
			e.printStackTrace();
			throw new ClassCastException(mActivity.toString()
					+ " must implement OnRePhotoBackListener");
		}
	}
	
}
