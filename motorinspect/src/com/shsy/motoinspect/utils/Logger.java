package com.shsy.motoinspect.utils;

import com.shsy.motoinspect.CommonConstants;

import android.util.Log;


public class Logger {

	/**
	 * 
	 * @param TAG
	 * @param msg
	 */
	public static void show(String TAG, String msg) {
		if (!CommonConstants.isShowLog) {
			return;
		}
		show(TAG, msg, Log.INFO);
	}

	/**
	 * 
	 * @param TAG
	 * @param msg
	 * @param level
	 */
	public static void show(String TAG, String msg, int level) {
		if (!CommonConstants.isShowLog) {
			return;
		}
		switch (level) {
		case Log.VERBOSE:
			Log.v(TAG, msg);
			break;
		case Log.DEBUG:
			Log.d(TAG, msg);
			break;
		case Log.INFO:
			Log.i(TAG, msg);
			break;
		case Log.WARN:
			Log.w(TAG, msg);
			break;
		case Log.ERROR:
			Log.e(TAG, msg);
			break;
		default:
			Log.i(TAG, msg);
			break;
		}
	}

}
