package com.shsy.motoinspect.ui.fragment;

import java.util.List;

import com.shsy.motoinspect.BaseFragment;
import com.shsy.motoinspect.entity.CheckItemEntity;
import com.shsy.motoinspect.ui.fragment.OuterCheckItemsFrm.OnClickCheckItemListener;
import com.shsy.motoinspect.utils.Logger;
import com.shsy.motoinspect.utils.ToastUtils;
import com.shsy.motorinspect.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class OuterItemFailReasonFrm extends BaseFragment {
	private Button btn_upload_weight;
	private EditText et_dimense_length;
	private EditText et_dimense_width;
	private EditText et_dimense_height;
	private EditText et_curb_weight;
	
	private String length;
	private String width;
	private String height;
	private String weight;
	
	public static final String ITEMFAIL_REASON="itemfail_reason";
	
	private List<CheckItemEntity> mFailItems;
	
	OnClickDimenseListener mCallback;
	public interface OnClickDimenseListener{
		public void onClickDimense(String length,String width,String height,String weight);
	}
	
	
	public OuterItemFailReasonFrm(){
		Logger.show(getClass().getName(), "OuterResultFrm");
	}
	


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Logger.show(getClass().getName(), "onCreateView");
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	


	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
	}
	
	

	@Override
	public void initParam() {
		findView();
		initView();
	}


	private void initView() {
		et_dimense_length.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				length = et_dimense_length.getText().toString();
				mCallback.onClickDimense(length, width, height, weight);
			}
		});
		
		et_dimense_width.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				width = et_dimense_width.getText().toString();
				mCallback.onClickDimense(length, width, height, weight);
			}
		});
		
		et_dimense_height.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				height = et_dimense_height.getText().toString();
				mCallback.onClickDimense(length, width, height, weight);
			}
		});
		
		et_curb_weight.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				weight = et_curb_weight.getText().toString();
				mCallback.onClickDimense(length, width, height, weight);
			}
		});
		
		
		
		
		/*btn_upload_weight.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String length = et_dimense_length.getText().toString();
				String width = et_dimense_width.getText().toString();
				String height = et_dimense_height.getText().toString();
				String weight = et_curb_weight.getText().toString();
				mCallback.onClickDimense(length, width, height, weight);
			}
		});*/
		
		
	}



	@Override
	public int getLayoutResID() {
		return R.layout.frm_dimens_weight;
	}
	
	
	
	private void findView() {
		et_dimense_length = (EditText)mRootView.findViewById(R.id.et_dimense_length);
		et_dimense_width = (EditText)mRootView.findViewById(R.id.et_dimense_width);
		et_dimense_height = (EditText)mRootView.findViewById(R.id.et_dimense_height);
		et_curb_weight = (EditText)mRootView.findViewById(R.id.et_curb_weight);
		btn_upload_weight = (Button)mRootView.findViewById(R.id.btn_upload_weight);
	}

	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCallback = (OnClickDimenseListener) mActivity;
		} catch (ClassCastException e) {
			e.printStackTrace();
			throw new ClassCastException(mActivity.toString()
					+ " must implement OnClickDimenseListener");
		}
	}

	
}
