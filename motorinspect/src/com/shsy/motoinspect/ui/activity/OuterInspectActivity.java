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
import com.shsy.motoinspect.ui.fragment.MyDialogFragment;
import com.shsy.motoinspect.ui.fragment.OuterCheckFrm;
import com.shsy.motoinspect.ui.fragment.OuterPhotoFrm;
import com.shsy.motoinspect.ui.fragment.OuterPhotoFrm2;
import com.shsy.motoinspect.ui.fragment.OuterItemFailReasonFrm;
import com.shsy.motoinspect.utils.Logger;
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
OuterCheckItemsFrm.OnClickCheckItemListener,OuterPhotoFrm.OnPhotoItemListener,
OuterPhotoFrm2.OnChassisPhotoItemListener{

	protected TitleBarView mTitleBarView;
	
	private TabPageIndicator mTabPageIndicator;
	private ViewPager mViewPager;
	private TabAdapter mAdapter ;
	private List<Fragment> mFragments;
	private FragmentManager mManager;
	
	
	private List<CheckItemEntity> mItemsCheckFail;
	private CarListInfoEntity carInfo;
	
	private List<CarPhotoEntity> mPohtos;
	
	private int mOutCheckType;
	private String jylsh;
	
	private BaseApplication app ;
	private SparseArray<CheckItemEntity> sparseArray;
	
	private boolean isUploadSuccess = true;
	private ProgressDialog mProgressDlg ;
	
	
	
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
		mOutCheckType = getIntent().getExtras().getInt(OuterCheckFrm.OUTCHECKTYPE);
		
		mProgressDlg = new ProgressDialog(this);
		
		Logger.show(getClass().getName(), "mOutCheckType:"+mOutCheckType);
		sparseArray = new SparseArray<CheckItemEntity>();
		
		
		carInfo = getCarInfoFromSel();
		jylsh = carInfo.getLsh();
//		mPohtos = initPhotos();
		mPohtos = new ArrayList<CarPhotoEntity>();
		
		mTitleBarView.setTitle(carInfo.getHphm());
		mTitleBarView.setBtnLeftOnclickListener(this);
		mTitleBarView.setBtnRightOnclickListener(this);
		
		mManager = getSupportFragmentManager();
		
		mFragments = initFragments(mOutCheckType);
		mAdapter = new TabAdapter(mManager,mFragments,mOutCheckType);
		mViewPager.setAdapter(mAdapter);
	}
	
	

	private CarListInfoEntity getCarInfoFromSel() {
		return getIntent().getExtras().getParcelable(CommonConstants.BUNDLE_TO_OUTER);
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
					list = initDatas(checkitems,start);
					Bundle bundle = new Bundle();
					bundle.putString("jylsh", jylsh);
					OuterCheckItemsFrm outerCheckItemsFrm = new OuterCheckItemsFrm(list);
					outerCheckItemsFrm.setArguments(bundle);
					fms.add(outerCheckItemsFrm);
					len = checkitems.length;
					setValueToSpareArray(list);
				}
				//mPohtos引用取得拍摄的照片种类
				Bundle bundle = new Bundle();
				bundle.putString("jylsh", jylsh);
				OuterPhotoFrm outerPhotoFrm = new OuterPhotoFrm(mPohtos);
				outerPhotoFrm.setArguments(bundle);
				fms.add(outerPhotoFrm);
				break;
	
			case CommonConstants.CHASSIS:
				List<CheckItemEntity> chassisList = new ArrayList<CheckItemEntity>();
				String[] chassisItems = app.initOuterCheckItems(7);
				chassisList = initDatas(chassisItems,46);
				
				Bundle bundle2 = new Bundle();
				bundle2.putString("jylsh", jylsh);
				OuterCheckItemsFrm chassis = new OuterCheckItemsFrm(chassisList);
				chassis.setArguments(bundle2);
				
				fms.add(chassis);
				setValueToSpareArray(chassisList);
				break;
	
			case CommonConstants.DYNAMIC:
				List<CheckItemEntity> dynamicList = new ArrayList<CheckItemEntity>();
				String[] dynamicLtems = app.initOuterCheckItems(6);
				dynamicList = initDatas(dynamicLtems,42);
				
				Bundle bundle3 = new Bundle();
				bundle3.putString("jylsh", jylsh);
				OuterCheckItemsFrm dynamic = new OuterCheckItemsFrm(dynamicList);
				dynamic.setArguments(bundle3);
				
				fms.add(new OuterCheckItemsFrm(dynamicList));
				setValueToSpareArray(dynamicList);
				
				fms.add(new OuterPhotoFrm2(mPohtos));
				break;
			
			case CommonConstants.REPHOTO:
				OuterPhotoFrm rePhoto = new OuterPhotoFrm(mPohtos);
				Bundle rePhotoBundle = new Bundle();
				rePhotoBundle.putBoolean("isrephoto", true);
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
	 *需要拍摄的照片有那几张mPohtos
	 *初始的时候只有“添加图片”按钮
	 *mPohtos被引用 fms.add(new OuterPhotoFrm(mPohtos))
	 * @return
	 */
	private List<CarPhotoEntity> initPhotos() {
		List<CarPhotoEntity> list = new ArrayList<CarPhotoEntity>();
		Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_photo_add);
        CarPhotoEntity addPhoto = new CarPhotoEntity("", "", bmp,"","",OuterPhotoFrm.PHOTO_NOT_MUST);
		list.add(addPhoto);
		return list;
	}

	
	
	/**
	 * 根据类型初始化查验项目
	 * @return
	 */
	private List<CheckItemEntity> initDatas(String[] items,int start) {
		List<CheckItemEntity> list = new ArrayList<CheckItemEntity>();
		int len = items.length ;
		for(int i=0; i<len; i++){
			CheckItemEntity checkitem = new CheckItemEntity();
			checkitem.setSeq(start+i);
			checkitem.setTextCheckItem(items[i]);
			checkitem.setCheckflag(1);
			list.add(checkitem);
		}
		if(list.get(len-1).getSeq()==42){
			list.get(len-1).setSeq(80);
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
			String txt = "正在上传请等待...";
			showProgressDlg(mOutCheckType,txt);
			upload(mOutCheckType);
			break;
		}
	}
	
	

	private void upload(int mOutCheckType) {
		if(CommonConstants.APPEARANCE == mOutCheckType){
			
			uploadCheckItems(mOutCheckType);
			uploadPhoto();
			
		}else if(mOutCheckType == CommonConstants.CHASSIS){
			
			uploadCheckItems(mOutCheckType);
			
		}else if(mOutCheckType == CommonConstants.REPHOTO){
			uploadPhoto();
			
		}else if(mOutCheckType == CommonConstants.DYNAMIC){
			uploadCheckItems(mOutCheckType);
			uploadPhoto();
		}
	}


	private void showProgressDlg(int mOutCheckType,String txt) {
		
		mProgressDlg.setMessage(txt);
		mProgressDlg.show();
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
		
		
		Map<String, String> headers = new HashMap<String, String>();
		String session = (String) SharedPreferenceUtils.get(this, CommonConstants.JSESSIONID, "");
		if(TextUtils.isEmpty(session)){
			return;
		}
		headers.put("Cookie", "JSESSIONID="+session);
		OkHttpUtils.post()
		.url(url).headers(headers)
		.params(putCheckItemParams(type)).build().execute(new StringCallback() {
			
			@Override
			public void onResponse(String response, int id) {
				
					if(mOutCheckType == CommonConstants.CHASSIS){
						mProgressDlg.dismiss();
						OuterInspectActivity.this.finish();
						showToast("检验项目已上传");
					}else if(CommonConstants.DYNAMIC == mOutCheckType || 
							CommonConstants.APPEARANCE == mOutCheckType){
						showToast("检验项目已上传");
					}
					
			}
			
			@Override
			public void onError(Call call, Exception e, int id) {
				Logger.show(getClass().getName(), "onError "+e.toString());
				showToast("检验项目上传失败,重新发送");
				isUploadSuccess = false;
				mProgressDlg.dismiss();
			}
		});
	}
	

	/**
	 *上传照片
	 *
	 */
	private int mCount = 0;
	
	private void uploadPhoto() {
		
		final String url = ToolUtils.uploadPhotoUrl(OuterInspectActivity.this);
		String session = (String) SharedPreferenceUtils.get(this, CommonConstants.JSESSIONID, "");

		if (TextUtils.isEmpty(url) || TextUtils.isEmpty(session)) {
			return;
		}
		
		final Map<String, String> headers = new HashMap<String, String>();
		headers.put("APP-Key", "APP-Secret222");
		headers.put("APP-Secret", "APP-Secret111");
		headers.put("Cookie", "JSESSIONID=" + session);
		
		final List<CarPhotoEntity> photos = mPohtos;
		
		//不上传照片的情况
		if(photos==null || photos.size()==0){
			OuterInspectActivity.this.finish();
			return;
		}
		
		for(int i = 0; i < photos.size(); i++) {
			mCount = i;
			File file = getPhotoFile(photos, i);
			Map<String, String> params = putPhotoParams(photos, i);
			
			OkHttpUtils.post().addFile("photo", "image"+i, file).url(url).headers(headers).params(params)
			.build().execute(new StringCallback() {
				
				@Override
				public void onResponse(String response, int id) {
					Logger.show("photoresponse", "photoresponse="+response);
					if (mCount == (photos.size()-1) && isUploadSuccess) {
						OuterInspectActivity.this.finish();
						showToast("检测照片已上传");
						mProgressDlg.dismiss();
					}
				}

				@Override
				public void onError(Call call, Exception e, int id) {
					e.printStackTrace();
					isUploadSuccess = false;
					showToast("照片上传失败,重新上传");
					mProgressDlg.dismiss();
				}
			});
		}
		
		
	}




	private File getPhotoFile(List<CarPhotoEntity> photos,int position){
		CarPhotoEntity carPhoto = photos.get(position);
		String filepath = carPhoto.getUploadPhotoFilePath();
		
		File file = new File(filepath);
		Logger.show(getClass().getName(), "getPhotoFile"+filepath);
		Logger.show(getClass().getName(), "filesize="+file.length());
		return file;
		
	}
	
	private Map<String,String> putPhotoParams(List<CarPhotoEntity> photos,int position){
		
		CarPhotoEntity carPhoto = photos.get(position);
		
		Map<String,String> map = new HashMap<String,String>();
		
		map.put("jyjgbh",carInfo.getJyjgbh());
		map.put("jcxdh",carInfo.getJcxdh());
		map.put("jylsh", carInfo.getLsh());
		map.put("hphm",carInfo.getHphm());
		map.put("hpzl",carInfo.getHpzl());
		map.put("clsbdh",carInfo.getClsbdh());
		map.put("jycs",Integer.toString(carInfo.getJycs()));
		
		map.put("pssj",ToolUtils.getCurDate() );
		
		if(CommonConstants.DYNAMIC == mOutCheckType){
			map.put("jyxm","DC");
		}else{
			map.put("jyxm","F1");
		}
		
		map.put("zpzl",carPhoto.getPhotoTypeCode());
		
		return map;
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
		Logger.show(TAG, "********");
		Logger.show(TAG, "item="+item.getSeq());
	}



	@Override
	public void OnAddPhotoItem(CarPhotoEntity carPhotoEntity) {
		mPohtos.add(carPhotoEntity);
	}



	@Override
	public void OnAddChassisPhotoItem(CarPhotoEntity carPhotoEntity) {
		mPohtos.add(carPhotoEntity);
	}


	
	
}
