package com.shsy.motoinspect.common;


import com.shsy.motoinspect.utils.DensityUtil;
import com.shsy.motorinspect.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TitleBarView extends RelativeLayout {

	private Context mContext;
	private Button title_left;
	private TextView title_center;
	private Button title_right;
	
	public TitleBarView(Context context){
		super(context);
		mContext=context;
		initView();
	}
	
	public TitleBarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext=context;
		initView();
	}
	
	private void initView(){
		LayoutInflater.from(mContext).inflate(R.layout.common_title_bar, this);
		title_left=(Button) findViewById(R.id.title_left);
		title_center=(TextView) findViewById(R.id.title_center);
		title_right=(Button) findViewById(R.id.title_right);
		Log.i("TitleBarView", "TitleBarView initView");
	}
	
	
	
	public void setCommonTitle(int LeftVisibility,int centerVisibility,int rightVisibility){
		title_left.setVisibility(LeftVisibility);
		title_center.setVisibility(centerVisibility);
		title_right.setVisibility(rightVisibility);
		
	}
	
	
	
	public void setBtn(Button Btn,int icon,int txtRes){
		Drawable img=mContext.getResources().getDrawable(icon);
		int height=DensityUtil.dip2px(mContext, 20);
		int width=img.getIntrinsicWidth()*height/img.getIntrinsicHeight();
		img.setBounds(0, 0, width, height);
		Btn.setText(txtRes);
		Btn.setCompoundDrawables(img, null, null, null);
	}
	
	public void setBtn(Button Btn,int txtRes){
		Btn.setText(txtRes);
	}
	
	
	
	public void setTitle(int resId){
		title_center.setText(resId);
	}

	public void setTitle(String title){
		title_center.setText(title);
	}
	
	public void setBtnLeftOnclickListener(OnClickListener listener){
		title_left.setOnClickListener(listener);
	}
	
	public void setBtnRightOnclickListener(OnClickListener listener){
		title_right.setOnClickListener(listener);
	}
	
	public Button getTitleLeft(){
		return title_left;
	}
	
	public Button getTitleRight(){
		return title_right;
	}
	
	public void destoryView(){
		title_left.setText(null);
		title_center.setText(null);
		title_right.setText(null);
	}

}
