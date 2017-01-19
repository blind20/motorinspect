package com.shsy.motoinspect.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class ToastUtils {
	
	private static Toast mToast;
	
	/**
	 * 
	 */
	public static void showToast(Context context, CharSequence text, int duration) {
		if(mToast == null) {
			mToast = Toast.makeText(context, text, duration);
		} else {
			mToast.setText(text);
			mToast.setDuration(duration);
		}
		mToast.setGravity(Gravity.CENTER, 0, 0);
		mToast.show();
	}

}
