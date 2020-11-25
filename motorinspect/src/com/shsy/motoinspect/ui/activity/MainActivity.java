package com.shsy.motoinspect.ui.activity;

import com.shsy.motoinspect.BaseActivity;
import com.shsy.motoinspect.BaseApplication;
import com.shsy.motoinspect.CommonConstants;
import com.shsy.motoinspect.entity.CarListInfoEntity;
import com.shsy.motoinspect.ui.fragment.DownLineWithReUpLineFrm;
import com.shsy.motoinspect.ui.fragment.LoginFrm;
import com.shsy.motoinspect.ui.fragment.ZbzlCarListFrm;
import com.shsy.motoinspect.ui.fragment.ZbzlCarListFrm.OnZbzlCarListClick;
import com.shsy.motoinspect.ui.fragment.ZbzlCheckFrm;
import com.shsy.motoinspect.ui.fragment.PersonInfoFrm;
import com.shsy.motoinspect.ui.fragment.PullCarToLineFrm;
import com.shsy.motoinspect.ui.fragment.QDZCheckFrm;
import com.shsy.motoinspect.ui.fragment.ReCheckCarInfoFrm;
import com.shsy.motoinspect.ui.fragment.ReCheckListFrm;
import com.shsy.motoinspect.ui.fragment.RePhotoFrm;
import com.shsy.motoinspect.ui.fragment.ResetWorkFrm;
import com.shsy.motoinspect.ui.fragment.SystemSettingFrm;
import com.shsy.motoinspect.ui.fragment.NavigationFrm;
import com.shsy.motoinspect.ui.fragment.OuterCheckFrm;
import com.shsy.motoinspect.ui.fragment.UnfinishedCarListFrm;
import com.shsy.motoinspect.ui.fragment.VehCheckProcessFrm;
import com.shsy.motoinspect.utils.Logger;
import com.shsy.motoinspect.utils.SharedPreferenceUtils;
import com.shsy.motoinspect.utils.ToolUtils;
import com.shsy.motorinspect.R;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import okhttp3.Call;

public class MainActivity extends BaseActivity implements View.OnClickListener,
													NavigationFrm.OnMenuSelListener,
													OuterCheckFrm.OnOuterCheckBackListener,
													PullCarToLineFrm.OnPullBackListener,
													PullCarToLineFrm.OnPullVehLsnr,
													ResetWorkFrm.OnResetBackListener,
													RePhotoFrm.OnRePhotoBackListener,
													ReCheckListFrm.OnReCheckListClick,
													ZbzlCarListFrm.OnZbzlCarListClick,
													UnfinishedCarListFrm.OnUnfinishedCarListClick,
													PersonInfoFrm.OnItemSelListener{

	private ImageButton buttom_waijian;
	private ImageButton buttom_yinche;
	private ImageButton buttom_personinfo;
	
	private FragmentTransaction ft;
	
	private int chooseIndex=-1;
	private long firstTime=0;
	
	private boolean isRecycle = false;
	public final static String CHOOSEINDEX = "chooseIndex";
	
	public static final String CHECKTYPE = "CHECKITEMTYPE";
	
	public static final String NAVI_TYPE = "NAVITYPE";
	public static final int NAVI_TYPE_1= 0x200;
	public static final int NAVI_TYPE_2= 0x201;
	
	private BaseApplication app;
	
	//标记是否从其他fragment点击返回按钮
	private boolean isBack = false;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app = (BaseApplication) getApplication();
		
		/*int count = (Integer)SharedPreferenceUtils.get(this, CommonConstants.COUNT, 0);
		if(count==0){
			SharedPreferenceUtils.put(this, CommonConstants.UUID, ToolUtils.getUUID(this));
			SharedPreferenceUtils.put(this, CommonConstants.COUNT, ++count);
		}*/
		
		if(savedInstanceState == null){
			onClick(buttom_personinfo);
		}
		
	}

	@Override
	public void findView() {
		buttom_waijian = (ImageButton) findViewById(R.id.buttom_waijian);
		buttom_yinche = (ImageButton) findViewById(R.id.buttom_yinche);
		buttom_personinfo = (ImageButton) findViewById(R.id.buttom_personinfo);
	}
	
	@Override
	public void initParam() {
		buttom_waijian.setOnClickListener(this);
		buttom_yinche.setOnClickListener(this);
		buttom_personinfo.setOnClickListener(this);
		
	}
	
	

	@Override
	public void onClick(View v) {
		ft = getSupportFragmentManager().beginTransaction();
		switch (v.getId()) {
		
			case R.id.buttom_waijian:
				if(chooseIndex != 0 || isBack){
					chooseIndex = 0;
					tabBgChange(chooseIndex);
					Bundle bundle = new Bundle();
					bundle.putInt(NAVI_TYPE, this.NAVI_TYPE_1);
					ft.replace(R.id.fl_carinfo, NavigationFrm.instantiate(MainActivity.this, NavigationFrm.class.getName(),bundle));
					isBack = false;
				}
				break;
				
			case R.id.buttom_yinche:
				
				if(chooseIndex != 1 || isBack){
					chooseIndex = 1;
					tabBgChange(chooseIndex);
					Bundle bundle = new Bundle();
					bundle.putInt(NAVI_TYPE, this.NAVI_TYPE_2);
					ft.replace(R.id.fl_carinfo, NavigationFrm.instantiate(MainActivity.this, NavigationFrm.class.getName(),bundle));
					isBack = false;
				}
				break;
				
			case R.id.buttom_personinfo:
				
				if(chooseIndex != 2 || isBack){
					chooseIndex = 2;
					tabBgChange(chooseIndex);
					ft.replace(R.id.fl_carinfo, PersonInfoFrm.instantiate(MainActivity.this, PersonInfoFrm.class.getName(),null),"personInfoFrm");
					isBack = false;
				}
				break;
		}
		ft.commit();
	}


	private void tabBgChange(int chooseIndex2) {
		
		switch(chooseIndex2){
			case 0:
				buttom_waijian.setEnabled(false);
				buttom_yinche.setEnabled(true);
				buttom_personinfo.setEnabled(true);
				break;
			
			case 1:
				buttom_waijian.setEnabled(true);
				buttom_yinche.setEnabled(false);
				buttom_personinfo.setEnabled(true);
				break;
				
			case 2:
				buttom_waijian.setEnabled(true);
				buttom_yinche.setEnabled(true);
				buttom_personinfo.setEnabled(false);
				break;
				
		}
		
	}
	
	
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		isRecycle = true;
		outState.putBoolean(CommonConstants.ISRECYCLE, isRecycle);
		outState.putInt(MainActivity.CHOOSEINDEX, chooseIndex);
	}
	
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		isRecycle = savedInstanceState.getBoolean(CommonConstants.ISRECYCLE);
		chooseIndex = savedInstanceState.getInt(MainActivity.CHOOSEINDEX);
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
		if(isRecycle){
			
			switch (chooseIndex) {
				case 0:
					onClick(buttom_waijian);
					break;
					
				case 1:
					onClick(buttom_yinche);
					break;
					
				case 2:
					tabBgChange(chooseIndex);
					break;
			}
		}
	}
	
	

	@Override
	public int getLayoutResID() {
		return R.layout.aty_main;
	}

	@Override
	public void onBackPressed() {
		long secondTime = System.currentTimeMillis();
		if(secondTime - firstTime > 1000){
			showToast("再按一次退出客户端");
		}else{
			finish();
		}
	}

	@Override
	public void onMenuItemSel(String menuTitle) {
		ft = getSupportFragmentManager().beginTransaction();
		Bundle bundle = new Bundle();
		bundle.putString(NavigationFrm.TO_VEHLISTATY, menuTitle);
		if(menuTitle.equals(getString(R.string.outer_nav1_menu1))
				||menuTitle.equals(getString(R.string.outer_nav1_menu2))
				||menuTitle.equals(getString(R.string.outer_nav1_menu3))){
			
			ft.replace(R.id.fl_carinfo, OuterCheckFrm.instantiate(MainActivity.this, OuterCheckFrm.class.getName(),bundle));
			
		}else if(menuTitle.equals(getString(R.string.outer_nav2_menu2))){
			
			ft.replace(R.id.fl_carinfo, ResetWorkFrm.instantiate(MainActivity.this, ResetWorkFrm.class.getName(),bundle));
			
		}else if(menuTitle.equals(getString(R.string.outer_nav2_menu1))
				||menuTitle.equals(getString(R.string.outer_nav2_menu3))){
			
			ft.replace(R.id.fl_carinfo, PullCarToLineFrm.instantiate(MainActivity.this, PullCarToLineFrm.class.getName(),bundle));
			
		}else if(menuTitle.equals(getString(R.string.outer_nav1_menu4))){
			
			ft.replace(R.id.fl_carinfo, RePhotoFrm.instantiate(MainActivity.this, RePhotoFrm.class.getName(),bundle));
		}else if(menuTitle.equals(getString(R.string.outer_nav2_menu4))||menuTitle.equals(getString(R.string.outer_nav2_menu5))){
			
			ft.replace(R.id.fl_carinfo, ZbzlCarListFrm.instantiate(MainActivity.this, ZbzlCarListFrm.class.getName(),bundle));
			ft.addToBackStack(null);
		}else if(menuTitle.equals(getString(R.string.outer_nav1_menu5))){
			
			ft.replace(R.id.fl_carinfo, ReCheckListFrm.instantiate(MainActivity.this, ReCheckListFrm.class.getName(),bundle));
			ft.addToBackStack(null);
		}else if(menuTitle.equals(getString(R.string.outer_nav1_menu6))){
			
			ft.replace(R.id.fl_carinfo, UnfinishedCarListFrm.instantiate(MainActivity.this, UnfinishedCarListFrm.class.getName(),bundle));
			ft.addToBackStack(null);
		}else if(menuTitle.equals(getString(R.string.outer_nav2_menu6))){
			
			ft.replace(R.id.fl_carinfo, DownLineWithReUpLineFrm.instantiate(MainActivity.this, DownLineWithReUpLineFrm.class.getName(),bundle));
			ft.addToBackStack(null);
		}
		
		ft.commit();
	}

	@Override
	public void onOuterCheckBack() {
		isBack = true;
		onClick(buttom_waijian);
	}

	@Override
	public void onPullBack() {
		isBack = true;
		onClick(buttom_yinche);
	}

	@Override
	public void onResetBack() {
		onPullBack();
	}

	@Override
	public void onRePhotoBack() {
		onOuterCheckBack();
	}

	/**
	 * type称重类型：0为整备质量称重，1为驱动轴称重
	 */
	@Override
	public void onZbzlCarItemClick(CarListInfoEntity carinfo,int type) {
		Bundle bundle = new Bundle();
		if(carinfo!=null){
			bundle.putParcelable("carinfo", carinfo);
		}
		ft = getSupportFragmentManager().beginTransaction();
		if(type==0){
			ft.replace(R.id.fl_carinfo, ZbzlCheckFrm.instantiate(MainActivity.this, ZbzlCheckFrm.class.getName(),bundle));
		}else{
			ft.replace(R.id.fl_carinfo, QDZCheckFrm.instantiate(MainActivity.this, QDZCheckFrm.class.getName(),bundle));
		}
		ft.addToBackStack(null);
		ft.commit();
	}

	@Override
	public void onReCheckItemClick(CarListInfoEntity carinfo) {
		Bundle bundle = new Bundle();
		if(carinfo!=null){
			bundle.putParcelable("carinfo", carinfo);
		}
		ft = getSupportFragmentManager().beginTransaction();
		ft.replace(R.id.fl_carinfo, ReCheckCarInfoFrm.instantiate(MainActivity.this, ReCheckCarInfoFrm.class.getName(),bundle));
		ft.addToBackStack(null);
		ft.commit();
	}

	@Override
	public void onItemSel(int position) {
		ft = getSupportFragmentManager().beginTransaction();
		ft.replace(R.id.fl_carinfo, SystemSettingFrm.instantiate(MainActivity.this, SystemSettingFrm.class.getName(),null));
		ft.addToBackStack(null);
		ft.commit();
	}

	@Override
	public void onUnfinishedCarListClick(CarListInfoEntity carinfo) {
		Bundle bundle = new Bundle();
		if(carinfo!=null){
			bundle.putParcelable("carinfo", carinfo);
		}
		ft = getSupportFragmentManager().beginTransaction();
		ft.replace(R.id.fl_carinfo, VehCheckProcessFrm.instantiate(MainActivity.this, VehCheckProcessFrm.class.getName(),bundle));
		ft.addToBackStack(null);
		ft.commit();
	}

	@Override
	public void onPullVeh() {
		Bundle bundle = new Bundle();
		ft = getSupportFragmentManager().beginTransaction();
		ft.replace(R.id.fl_carinfo, DownLineWithReUpLineFrm.instantiate(MainActivity.this, DownLineWithReUpLineFrm.class.getName(),bundle));
		ft.addToBackStack(null);
		ft.commit();
	}

}
