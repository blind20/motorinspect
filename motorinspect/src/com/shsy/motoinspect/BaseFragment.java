package com.shsy.motoinspect;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseFragment extends Fragment {

	
	protected View mRootView;
	protected Activity mActivity;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRootView = inflater.inflate(getLayoutResID(), container, false);
		initParam();
		return mRootView;
	}
	

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.mActivity = activity;
	}

	public abstract int getLayoutResID();
	public abstract void initParam() ;

	
}
