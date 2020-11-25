package com.shsy.motoinspect.network;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.shsy.motoinspect.CommonConstants;
import com.shsy.motoinspect.utils.SharedPreferenceUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.StringCallback;

import android.content.Context;
import android.text.TextUtils;

public class MyHttpUtils {
	
	private static Context mContext;
	
	
	private static MyHttpUtils instance;
	
	private MyHttpUtils(Context context){
		mContext = context;
	}
	
	public static synchronized MyHttpUtils getInstance(Context context) {
		if (instance == null) {
			instance = new MyHttpUtils(context);
		}
		return instance;
	}
	
	public void postHttpByParam(String url,Map<String, String> map,StringCallback callback){
		Map<String, String> headers = new HashMap<String, String>();
		String session = (String) SharedPreferenceUtils.get(mContext, CommonConstants.JSESSIONID, "");
		if(TextUtils.isEmpty(session)){
			return;
		}
		headers.put("Cookie", "JSESSIONID="+session);
		PostFormBuilder builder = OkHttpUtils.post().url(url).headers(headers);
		for(Map.Entry<String, String> entry: map.entrySet()){
			builder.addParams(entry.getKey(),entry.getValue());
		}
		builder.build().execute(callback);
	}

	public void postHttpFile(String url,File file,Map<String, String> map,StringCallback callback){
		String session = (String) SharedPreferenceUtils.get(mContext, CommonConstants.JSESSIONID, "");
		if(TextUtils.isEmpty(session)){
			return;
		}
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Cookie", "JSESSIONID="+session);
		PostFormBuilder builder = OkHttpUtils.post().addFile("photo", "image", file).url(url).headers(headers);
		for(Map.Entry<String, String> entry: map.entrySet()){
			builder.addParams(entry.getKey(),entry.getValue());
		}
		builder.build().execute(callback);
	}
	
	public void postHttpVideo(String url,File file,Map<String, String> map,StringCallback callback){
		String session = (String) SharedPreferenceUtils.get(mContext, CommonConstants.JSESSIONID, "");
		if(TextUtils.isEmpty(session)){
			return;
		}
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Cookie", "JSESSIONID="+session);
		PostFormBuilder builder = OkHttpUtils.post().addFile("videoFile", "videoFile", file).url(url).headers(headers);
		for(Map.Entry<String, String> entry: map.entrySet()){
			builder.addParams(entry.getKey(),entry.getValue());
		}
		builder.build().connTimeOut(50000).readTimeOut(50000).writeTimeOut(500000).execute(callback);
	}
}
