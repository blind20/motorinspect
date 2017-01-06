package com.shsy.motoinspect.ui.fragment;

import java.util.List;

import com.shsy.motoinspect.BaseFragment;
import com.shsy.motoinspect.entity.CheckItemEntity;
import com.shsy.motoinspect.utils.Logger;
import com.shsy.motoinspect.utils.ToastUtils;
import com.shsy.motorinspect.R;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class OuterItemFailReasonFrm extends BaseFragment {

	private EditText mEdtRemark;
	private Button mButton;
	private boolean isRecycle = false;
	
	public static final String ITEMFAIL_REASON="itemfail_reason";
	
	private List<CheckItemEntity> mFailItems;
	
	public OuterItemFailReasonFrm(){
		Logger.show(getClass().getName(), "OuterResultFrm");
	}
	

	
	public void setmFailItems(List<CheckItemEntity> items) {
		this.mFailItems = items;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Logger.show(getClass().getName(), "onCreateView");
		if(savedInstanceState != null ){
			isRecycle = savedInstanceState.getBoolean("isRecycle");
			if(isRecycle){
//				ArrayList list = savedInstanceState.getParcelableArrayList("msubdatas");
//				mSubDatas = (List<CheckItemEntity>) list.get(0);
			}
		}
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	


	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		isRecycle = true;
//		ArrayList list = new ArrayList();
//		list.add(mSubDatas);
//		outState.putBoolean("isRecycle", isRecycle);
//		outState.putParcelableArrayList("msubdatas", list);
	}
	
	

	@Override
	public void initParam() {
		findView();
	}


	@Override
	public int getLayoutResID() {
		return R.layout.frm_itemfail_reason;
	}
	
	
	
	private void findView() {
		mEdtRemark = (EditText) mRootView.findViewById(R.id.et_remark);
		mButton = (Button) mRootView.findViewById(R.id.btn_save);
		mButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String reason = mEdtRemark.getText().toString();
				if(TextUtils.isEmpty(reason)){
					ToastUtils.showToast(mActivity, getString(R.string.fail_reason), Toast.LENGTH_SHORT);
					return;
				}
				Intent intent = new Intent();
				intent.putExtra(ITEMFAIL_REASON, reason);
				getActivity().setResult(OuterCheckItemsFrm.REQ_ITEMFAIL_REASON, intent);
				getActivity().finish();
			}
		});
	}

	

	
}
