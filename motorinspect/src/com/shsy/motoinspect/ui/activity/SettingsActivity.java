package com.shsy.motoinspect.ui.activity;

import com.shsy.motoinspect.BaseActivity;
import com.shsy.motoinspect.CommonConstants;
import com.shsy.motoinspect.common.TitleBarView;
import com.shsy.motoinspect.ui.fragment.SettingLoginFrm;
import com.shsy.motoinspect.ui.fragment.SettingServerFrm;
import com.shsy.motoinspect.ui.fragment.OuterCheckFrm;
import com.shsy.motoinspect.ui.fragment.OuterCheckItemsFrm;
import com.shsy.motoinspect.ui.fragment.OuterItemFailReasonFrm;
import com.shsy.motoinspect.utils.Logger;
import com.shsy.motorinspect.R;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

public class SettingsActivity extends BaseActivity implements View.OnClickListener{

	private TitleBarView mTitleBarView;
	private FragmentTransaction ft;
	private int mExtra;
	
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
		
		mExtra = getIntent().getIntExtra(CommonConstants.TO_SETTING, -1);
		ft = getSupportFragmentManager().beginTransaction();
		String title;
		switch(mExtra){
			case -1:
				title = getResources().getString(R.string.login);
				mTitleBarView.setTitle(title);
				mTitleBarView.setBtnLeftOnclickListener(this);
				ft.replace(R.id.fl_settings, SettingLoginFrm.instantiate(SettingsActivity.this, SettingLoginFrm.class.getName(),null),"SettingLoginFrm");
				break;
				
			case 0:
				title = getResources().getString(R.string.server);
				mTitleBarView.setTitle(title);
				mTitleBarView.setBtnLeftOnclickListener(this);
				ft.replace(R.id.fl_settings, SettingServerFrm.instantiate(SettingsActivity.this, SettingServerFrm.class.getName(),null),"SettingServerFrm");
				break;
				
			case OuterCheckItemsFrm.ITEMFAIL:
				title = "";
				mTitleBarView.setTitle(title);
				mTitleBarView.setBtnLeftOnclickListener(this);
				ft.replace(R.id.fl_settings, OuterItemFailReasonFrm.instantiate(SettingsActivity.this, OuterItemFailReasonFrm.class.getName(),null),"OuterResultFrm");
				break;
			
		}
		ft.commit();
	}
	
	

	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
			case R.id.title_left:
				if(mExtra == -1){
					Intent intent = new Intent();
					intent.putExtra(CommonConstants.USERNAME, "");
					setResult(CommonConstants.REQUEST_LOGIN, intent);
					finish();
				}else if(mExtra == OuterCheckItemsFrm.ITEMFAIL){
					Logger.show("back", "back");
					Intent intent = new Intent();
					intent.putExtra(OuterItemFailReasonFrm.ITEMFAIL_REASON, "");
					setResult(OuterCheckItemsFrm.REQ_ITEMFAIL_REASON, intent);
					finish();
				}else{
					finish();
				}
				break;
				
			default:
				break;
		}
		
	}

}
