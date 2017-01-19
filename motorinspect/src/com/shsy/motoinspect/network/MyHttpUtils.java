package com.shsy.motoinspect.network;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.shsy.motoinspect.CommonConstants;
import com.shsy.motoinspect.utils.SharedPreferenceUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;
import com.zhy.http.okhttp.callback.StringCallback;

import android.content.Context;
import android.text.TextUtils;
import okhttp3.Call;
import okhttp3.Response;

public class MyHttpUtils {
	
	private static Context mContext;
	private static String mUrl;
	private HashMap<String, Object> map;
	
	
	private static MyHttpUtils instance;
	
	private MyHttpUtils(Context context,String url,HashMap<String, Object>map){
		mContext = context;
		mUrl = url;
		this.map = map;
	}
	
	public static synchronized MyHttpUtils getInstance(Context context,String url, HashMap<String, Object> map) {
		if (instance == null) {
			instance = new MyHttpUtils(context,url,map);
		}
		return instance;
	}
	
	public static void postHttpParam(String key,String value){
		Map<String, String> headers = new HashMap<String, String>();
		String session = (String) SharedPreferenceUtils.get(mContext, CommonConstants.JSESSIONID, "");
		if(TextUtils.isEmpty(session)){
			return;
		}
		headers.put("Cookie", "JSESSIONID="+session);
		
		try {
			OkHttpUtils.post().url(mUrl).headers(headers)
			.addParams(key,value)
			.build().execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
