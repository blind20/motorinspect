package com.shsy.motoinspect.ui.activity;

import com.shsy.motoinspect.BaseActivity;
import com.shsy.motoinspect.CommonConstants;
import com.shsy.motoinspect.common.TitleBarView;
import com.shsy.motoinspect.ui.fragment.NavigationFrm;
import com.shsy.motoinspect.ui.fragment.OuterCheckFrm;
import com.shsy.motorinspect.R;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

public class VehListActivity extends BaseActivity {

	private TitleBarView mTitleBarView;
	private FragmentTransaction ft;
	private String mExtra;
	
	private boolean isRecycle = false;
	
	@Override
	public int getLayoutResID() {
		return R.layout.aty_settings;
	}

	@Override
	public void findView() {
		mTitleBarView = new TitleBarView(this);
		mTitleBarView = (TitleBarView)findViewById(R.id.titlebar);
		mTitleBarView.setCommonTitle(View.VISIBLE, View.VISIBLE, View.GONE);
	}

	@Override
	public void initParam() {
		String mExtra = getIntent().getStringExtra(NavigationFrm.TO_VEHLISTATY);
		ft = getSupportFragmentManager().beginTransaction();
//		toVehListFrmByExtra(mExtra);
		Bundle bundle = new Bundle();
		bundle.putString(NavigationFrm.TO_VEHLISTATY, mExtra);
		ft.replace(R.id.fl_settings, OuterCheckFrm.instantiate(VehListActivity.this, OuterCheckFrm.class.getName(),bundle));
		ft.commit();
	}
	
	private void toVehListFrmByExtra(String extra) {
		if(extra.equals(getString(R.string.outer_nav1_menu1))){
			
		}else if(extra.equals(getString(R.string.outer_nav1_menu2))){
			
		}else if(extra.equals(getString(R.string.outer_nav1_menu3))){
			
		}else if(extra.equals(getString(R.string.outer_nav2_menu1))){
			
		}else if(extra.equals(getString(R.string.outer_nav2_menu2))){
			
		}else if(extra.equals(getString(R.string.outer_nav2_menu3))){
			
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		isRecycle = true;
		outState.putBoolean(CommonConstants.ISRECYCLE, isRecycle);
		outState.putString(NavigationFrm.TO_VEHLISTATY, mExtra);
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		isRecycle = savedInstanceState.getBoolean(CommonConstants.ISRECYCLE);
		mExtra = savedInstanceState.getString(NavigationFrm.TO_VEHLISTATY);
	}

}
