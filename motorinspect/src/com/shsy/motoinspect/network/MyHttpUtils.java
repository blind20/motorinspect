package com.shsy.motoinspect.network;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.shsy.motoinspect.CommonConstants;
import com.shsy.motoinspect.utils.SharedPreferenceUtils;
import com.shsy.motoinspect.utils.ToastUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.Callback;
import com.zhy.http.okhttp.callback.StringCallback;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Response;

public class MyHttpUtils {
	
	private static Context mContext;
	private static String mUrl;
	
	
	private static MyHttpUtils instance;
	
	private MyHttpUtils(Context context,String url){
		mContext = context;
		mUrl = url;
	}
	
	public static synchronized MyHttpUtils getInstance(Context context,String url) {
		if (instance == null) {
			instance = new MyHttpUtils(context,url);
		}
		return instance;
	}
	
	public static void postHttpParam(String[] keys,String[] values,StringCallback callback){
		Map<String, String> headers = new HashMap<String, String>();
		String session = (String) SharedPreferenceUtils.get(mContext, CommonConstants.JSESSIONID, "");
		if(TextUtils.isEmpty(session)){
			return;
		}
		headers.put("Cookie", "JSESSIONID="+session);
		
		PostFormBuilder builder = OkHttpUtils.post().url(mUrl).headers(headers);
		for(int i=0;i<keys.length;i++){
			builder.addParams(keys[i], values[i]);
		}
		
		builder.build().execute(callback);
	}

}
