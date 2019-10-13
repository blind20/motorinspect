package com.shsy.motoinspect.ui.activity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Request.Builder;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.shsy.motoinspect.BaseActivity;
import com.shsy.motoinspect.BaseApplication;
import com.shsy.motoinspect.CommonConstants;
import com.shsy.motoinspect.adapter.TabAdapter;
import com.shsy.motoinspect.common.TitleBarView;
import com.shsy.motoinspect.entity.CarListInfoEntity;
import com.shsy.motoinspect.entity.CarPhotoEntity;
import com.shsy.motoinspect.entity.CheckItemEntity;
import com.shsy.motoinspect.network.ListCarInfoCallback;
import com.shsy.motoinspect.network.MyHttpUtils;
import com.shsy.motoinspect.ui.fragment.MyDialogFragment;
import com.shsy.motoinspect.ui.fragment.OuterCheckFrm;
import com.shsy.motoinspect.ui.fragment.OuterPhotoFrm;
import com.shsy.motoinspect.ui.fragment.OuterPhotoFrm2;
import com.shsy.motoinspect.ui.fragment.OuterItemFailReasonFrm;
import com.shsy.motoinspect.utils.Logger;
import com.shsy.motoinspect.utils.ProgressDlgUtil;
import com.shsy.motoinspect.utils.SharedPreferenceUtils;
import com.shsy.motoinspect.utils.ToastUtils;
import com.shsy.motoinspect.utils.ToolUtils;
import com.shsy.motoinspect.ui.fragment.OuterCheckItemsFrm;
import com.shsy.motorinspect.R;
import com.viewpagerindicator.TabPageIndicator;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.StringCallback;

public class OuterInspectActivity extends BaseActivity implements View.OnClickListener,
OuterCheckItemsFrm.OnClickCheckItemListener{

	protected TitleBarView mTitleBarView;
	
	private TabPageIndicator mTabPageIndicator;
	private ViewPager mViewPager;
	private TabAdapter mAdapter ;
	private List<Fragment> mFragments;
	private FragmentManager mManager;
	
	
	private List<CheckItemEntity> mItemsCheckFail;
	private CarListInfoEntity carInfo;
	
//	private List<CarPhotoEntity> mPohtos;
	
	private int mOutCheckType;
	private String jylsh;
	
	private BaseApplication app ;
	private SparseArray<CheckItemEntity> sparseArray;
	private String[] jcxmArray;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if(savedInstanceState == null){
			mTabPageIndicator.setViewPager(mViewPager,0);
		}else{
			mTabPageIndicator.setViewPager(mViewPager,0);
		}
		
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectDiskReads().detectDiskWrites().detectNetwork()
				.penaltyLog().build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
				.detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
				.penaltyLog().penaltyDeath().build());
		
	}
	


	@Override
	public int getLayoutResID() {
		return R.layout.aty_outerinspect;
	}


	@Override
	public void findView() {
		mTitleBarView = (TitleBarView)findViewById(R.id.titlebar);
		mTitleBarView.setCommonTitle(View.VISIBLE, View.VISIBLE, View.VISIBLE);
		
		mTabPageIndicator = (TabPageIndicator)findViewById(R.id.tabIndicator);
		mViewPager = (ViewPager)findViewById(R.id.viewpager);
		
	}

	
	@Override
	public void initParam() {
		app = (BaseApplication) getApplication();
		sparseArray = new SparseArray<CheckItemEntity>();
		
		getArgumentByIntent();
		
//		mPohtos = new ArrayList<CarPhotoEntity>();;
		
		mTitleBarView.setTitle(carInfo.getHphm());
		mTitleBarView.setBtnLeftOnclickListener(this);
		mTitleBarView.setBtnRightOnclickListener(this);
		
		mManager = getSupportFragmentManager();
		
		mFragments = initFragments(mOutCheckType);
		mAdapter = new TabAdapter(mManager,mFragments,mOutCheckType);
		mViewPager.setAdapter(mAdapter);
	}
	
	

	private void getArgumentByIntent() {
		mOutCheckType = getIntent().getExtras().getInt(OuterCheckFrm.OUTCHECKTYPE);
		carInfo = getIntent().getExtras().getParcelable(CommonConstants.BUNDLE_TO_OUTER);
		jylsh = carInfo.getLsh();
		jcxmArray = getIntent().getExtras().getStringArray("jcxms");
		if(jcxmArray == null){
			Logger.show("getArgumentByIntent", "getArgumentByIntent");
		}
		
	}



	
	private List<Fragment> initFragments(int type) {

		List<Fragment> fms = new ArrayList<Fragment>();
		
		switch (type) {
			case CommonConstants.APPEARANCE:
				int len = 0;
				int start =1;
				for(int i=1;i<=4;i++){
					List<CheckItemEntity> list = new ArrayList<CheckItemEntity>();
					start += len ;
					String[] checkitems = app.initOuterCheckItems(i);
					list = initDatas(checkitems,start,jcxmArray);
//					list = initDatas(checkitems,start);
					
					Bundle bundle = new Bundle();
					bundle.putString("jylsh", jylsh);
					bundle.putInt("type", type);
//					bundle.putStringArray("jcxms", jcxmArray);
					OuterCheckItemsFrm outerCheckItemsFrm = new OuterCheckItemsFrm(list);
					outerCheckItemsFrm.setArguments(bundle);
					fms.add(outerCheckItemsFrm);
					len = checkitems.length;
					setValueToSpareArray(list);
				}
				//mPohtos引用取得拍摄的照片种类
				Bundle bundle = new Bundle();
				bundle.putString("jylsh", jylsh);
				bundle.putParcelable(CommonConstants.BUNDLE_TO_OUTER, carInfo);
				OuterPhotoFrm outerPhotoFrm = new OuterPhotoFrm();
				outerPhotoFrm.setArguments(bundle);
				fms.add(outerPhotoFrm);
				break;
	
			case CommonConstants.CHASSIS:
				List<CheckItemEntity> chassisList = new ArrayList<CheckItemEntity>();
				String[] chassisItems = app.initOuterCheckItems(7);
				chassisList = initDatas(chassisItems,46,jcxmArray);
//				chassisList = initDatas(chassisItems,46);
				
				Bundle bundle2 = new Bundle();
				bundle2.putString("jylsh", jylsh);
				bundle2.putInt("type", type);
//				bundle2.putStringArray("jcxms", jcxmArray);
				OuterCheckItemsFrm chassis = new OuterCheckItemsFrm(chassisList);
				chassis.setArguments(bundle2);
				
				fms.add(chassis);
				setValueToSpareArray(chassisList);
				break;
	
			case CommonConstants.DYNAMIC:
				List<CheckItemEntity> dynamicList = new ArrayList<CheckItemEntity>();
				String[] dynamicLtems = app.initOuterCheckItems(6);
				dynamicList = initDatas(dynamicLtems,42,jcxmArray);
//				dynamicList = initDatas(dynamicLtems,42);
				
				Bundle bundle3 = new Bundle();
				bundle3.putString("jylsh", jylsh);
				bundle3.putInt("type", type);
//				bundle3.putStringArray("jcxms", jcxmArray);
				OuterCheckItemsFrm dynamic = new OuterCheckItemsFrm(dynamicList);
				dynamic.setArguments(bundle3);
				fms.add(dynamic);
				setValueToSpareArray(dynamicList);
				
				Bundle bundle4 = new Bundle();
				bundle4.putParcelable("carInfo", carInfo);
				OuterPhotoFrm2 outerPhotoFrm2 = new OuterPhotoFrm2();
				outerPhotoFrm2.setArguments(bundle4);
				fms.add(outerPhotoFrm2);
				break;
			
			case CommonConstants.REPHOTO:
				CarListInfoEntity carinfo = getIntent().getParcelableExtra(CommonConstants.BUNDLE_TO_OUTER);
				Bundle rePhotoBundle = new Bundle();
				rePhotoBundle.putBoolean("isrephoto", true);
				rePhotoBundle.putParcelable(CommonConstants.BUNDLE_TO_OUTER, carinfo);
				
				OuterPhotoFrm rePhoto = new OuterPhotoFrm();
				rePhoto.setArguments(rePhotoBundle);
				fms.add(rePhoto);;
				break;
		}
		return fms;

	}
	
	
	private void setValueToSpareArray(List<CheckItemEntity> list) {
		for(CheckItemEntity checkitem: list){
			sparseArray.put(checkitem.getSeq(), checkitem);
		}
	}



	
	
	/**
	 * 根据类型初始化查验项目
	 * @return
	 */
	private List<CheckItemEntity> initDatas(String[] items,int start,String[]jcxms) {
		List<CheckItemEntity> list = new ArrayList<CheckItemEntity>();
		int len = items.length ;
		for(int i=0; i<len; i++){
			CheckItemEntity checkitem = new CheckItemEntity();
			checkitem.setSeq(start+i);
			checkitem.setTextCheckItem(items[i]);
			checkitem.setCheckflag(CommonConstants.CHECKPASS);
			list.add(checkitem);
		}
		if(list.get(len-1).getSeq()==42){
			list.get(len-1).setSeq(80);
		}
		if(jcxms!=null && jcxms.length>0){
			for(int i=0;i<list.size();i++){
				for(int j=0;j<jcxms.length;j++){
					int index = Integer.parseInt(jcxms[j]);
					if(list.get(i).getSeq()==index){
						list.get(i).setCheckflag(CommonConstants.CHECKPASS);
						break;
					}
				}
			}
		}
		return list;
	}

	

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.title_left:
			finish();
			break;
			
		case R.id.title_right:
			upload(mOutCheckType);
			break;
		}
	}
	
	

	private void upload(int mOutCheckType) {
		if(mOutCheckType == CommonConstants.REPHOTO){
			finish();
		}else{
			uploadCheckItems(mOutCheckType);
		}
	}


	/**
	 *上传外观、底盘检查项目 
	 *
	 */
	private void uploadCheckItems(int type) {
		
		String url = null;
		switch (type) {
			case CommonConstants.APPEARANCE:
				url = ToolUtils.externalUrl(this);
				break;
	
			case CommonConstants.CHASSIS:
				url = ToolUtils.externalUrlC1(this);
				break;
				
			case CommonConstants.DYNAMIC:
				url = ToolUtils.externalUrlDC(this);
				break;
		}
		if(TextUtils.isEmpty(url)){
			return;
		}
		
		
		
		ProgressDlgUtil.showProgressDialog(this, "正在上传,请等待...");
		
		MyHttpUtils.getInstance(this).postHttpByParam(url, putCheckItemParams(type), new StringCallback(){
			
			@Override
			public void onResponse(String response, int id) {
				try {
					JSONObject jo = new JSONObject(response);
					Integer state = (Integer) jo.get("state");
					if (CommonConstants.STATAS_SUCCESS == state) {
						OuterInspectActivity.this.finish();
						ToastUtils.showToast(OuterInspectActivity.this, "上传成功", Toast.LENGTH_SHORT);
					} else {
						ToastUtils.showToast(OuterInspectActivity.this, "上传失败,请重新上传", Toast.LENGTH_SHORT);
					}
					ProgressDlgUtil.dismissProgressDialog();
				} catch (JSONException e) {
					e.printStackTrace();
					ToastUtils.showToast(OuterInspectActivity.this, "数据格式异常", Toast.LENGTH_SHORT);
					ProgressDlgUtil.dismissProgressDialog();
				}
			}
			
			@Override
			public void onError(Call call, Exception e, int id) {
				ToastUtils.showToast(OuterInspectActivity.this, "网络问题,上传失败", Toast.LENGTH_SHORT);
				ProgressDlgUtil.dismissProgressDialog();
			}
		});
	}
	

	
	
	private Map<String,String> putCheckItemParams(int mOutCheckType){
		
		Map<String,String> map = new HashMap<String,String>();
		
		map.put("jylsh", carInfo.getLsh());
		map.put("hphm",carInfo.getHphm());
		map.put("hpzl",carInfo.getHpzl());
		map.put("jyjgbh",carInfo.getJyjgbh());
		map.put("jycs",Integer.toString(carInfo.getJycs()));
		
		for(int i=0;i<sparseArray.size();i++){
			CheckItemEntity item = sparseArray.valueAt(i);
			if(item == null){
				continue;
			}
			int seq = item.getSeq();
			int flag = item.getCheckflag();
			map.put("Item"+seq, Integer.toString(flag));
			Logger.show(TAG, "item"+seq+":"+flag+"\n\r");
		}
		return map;
	}
	


	@Override
	public void onClickCheckItem(CheckItemEntity item) {
		sparseArray.put(item.getSeq(), item);
	}


	
}
