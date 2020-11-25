package com.shsy.motoinspect.utils;

import com.shsy.motorinspect.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CommonDialog {
	
	private Context context;
    private String dialogTitle;
    private String dialogMessage;
    private String positiveText;
    private String negativeText;
 
    private View dialogView;
    private OnDialogListener listener;

	public CommonDialog(Context context, int customeLayoutId, String dialogTitle, 
			String positiveText, String negativeText) {
		this.context = context;
        this.dialogTitle = dialogTitle;
        this.positiveText = positiveText;
        this.negativeText = negativeText;
        this.dialogView=View.inflate(context,customeLayoutId,null);
    }


    public CommonDialog(Context context, String dialogMessage, String dialogTitle, String positiveText, String negativeText) {
        this.context = context;
        this.dialogTitle = dialogTitle;
        this.dialogMessage = dialogMessage;
        this.positiveText = positiveText;
        this.negativeText = negativeText;
    }

    public View getDialogView() {
        return dialogView;
    }
 
    public void setDialogView(View dialogView) {
        this.dialogView = dialogView;
    }

    public void showDialog(){
    	AlertDialog.Builder builder = new AlertDialog.Builder(context);
		TextView tvTitle = (TextView) dialogView.findViewById(R.id.tv_alert_title);
		TextView tvContent = (TextView) dialogView.findViewById(R.id.tv_alert_content);
		Button buttonCancle = (Button) dialogView.findViewById(R.id.btn_alert_cancel);
		Button buttonOk = (Button) dialogView.findViewById(R.id.btn_alert_ok);
    	final AlertDialog dialog = builder.create();
		dialog.setCancelable(false);
    	
    	tvTitle.setText(dialogTitle);
    	tvContent.setText(dialogMessage);
		buttonOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (listener!=null){
                    listener.dialogPositiveListener(dialogView,dialog,DialogInterface.BUTTON_POSITIVE);
                }
			}
		});
    	buttonCancle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (listener!=null){
                    listener.dialogNegativeListener(dialogView,dialog,DialogInterface.BUTTON_NEGATIVE);
                }
			}
		});
		dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		dialog.show();
		dialog.getWindow().setLayout(DensityUtil.dip2px(context, 290), LinearLayout.LayoutParams.WRAP_CONTENT);
		dialog.getWindow().setWindowAnimations(R.style.AnimMM);
		dialog.setContentView(dialogView);
    }
    
    public void showCustomDialog(){
    	AlertDialog.Builder builder = new AlertDialog.Builder(context);
		TextView tvTitle = (TextView) dialogView.findViewById(R.id.tv_alert_title);
		EditText edittext = (EditText) dialogView.findViewById(R.id.et_alert_content);
		Button buttonCancle = (Button) dialogView.findViewById(R.id.btn_alert_cancel);
		Button buttonOk = (Button) dialogView.findViewById(R.id.btn_alert_ok);
    	final AlertDialog dialog = builder.create();
		dialog.setCancelable(false);
    	
    	tvTitle.setText(dialogTitle);
		buttonOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (listener!=null){
                    listener.dialogPositiveListener(dialogView,dialog,DialogInterface.BUTTON_POSITIVE);
                }
			}
		});
    	buttonCancle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (listener!=null){
                    listener.dialogNegativeListener(dialogView,dialog,DialogInterface.BUTTON_NEGATIVE);
                }
			}
		});
		
		edittext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
		    @Override
		    public void onFocusChange(View view, boolean focused) {
		        if (focused) {
		        	dialog.getWindow()
		                   .clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);   
		        }
		    }
		});

		dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		dialog.show();
		dialog.getWindow().setLayout(DensityUtil.dip2px(context, 290), LinearLayout.LayoutParams.WRAP_CONTENT);
		dialog.getWindow().setWindowAnimations(R.style.AnimMM);
		dialog.setContentView(dialogView);
    }

    
    
    public CommonDialog setOnDiaLogListener(OnDialogListener listener){
        this.listener=listener;
        return this;//把当前对象返回,用于链式编程
    }

    
    public interface OnDialogListener{
        public void dialogPositiveListener(View customView, DialogInterface dialogInterface, int which);
        public void dialogNegativeListener(View customView, DialogInterface dialogInterface, int which);
    }

}
