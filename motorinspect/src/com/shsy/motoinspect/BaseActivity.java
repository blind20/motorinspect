package com.shsy.motoinspect;

import com.shsy.motoinspect.utils.Logger;
import com.shsy.motoinspect.utils.ToastUtils;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import android.widget.Toast;


public abstract class BaseActivity extends FragmentActivity {

	protected String TAG;

//	protected BaseApplication application;
	protected SharedPreferences sp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TAG = this.getClass().getSimpleName();
		
		
//		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(getLayoutResID());
		
		
//		application = (BaseApplication) getApplication();
//		sp = getSharedPreferences(CommonConstants.SP_NAME, MODE_PRIVATE);
		
		
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
//			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);  
//		    getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//		}
		findView();
		initParam();
	}
	
	public abstract int getLayoutResID();
	
	public abstract void findView() ;
	
	public abstract void initParam() ;
	
	
	protected void intent2Activity(Class<? extends Activity> tarActivity) {
		Intent intent = new Intent(this, tarActivity);
		startActivity(intent);
	}
	
	protected void showToast(String msg) {
		ToastUtils.showToast(this, msg, Toast.LENGTH_SHORT);
	}

	protected void showLog(String msg) {
		Logger.show(TAG, msg);
	}

}
