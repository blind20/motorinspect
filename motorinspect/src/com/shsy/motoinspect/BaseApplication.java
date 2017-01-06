package com.shsy.motoinspect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.shsy.motoinspect.network.CookieJarImpl;
import com.shsy.motoinspect.network.PersistentCookieStore;
import com.shsy.motorinspect.R;
import com.zhy.http.okhttp.OkHttpUtils;

import android.app.Application;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;


public class BaseApplication extends Application {
	
	private static BaseApplication instance;
	
	/*private BaseApplication(){
		
	}
	
	public static BaseApplication  getInstance(){
		if(instance == null){
			return instance;
		}
		return instance;
	}*/
	private PersistentCookieStore persistentCookieStore;
	public String[][] mOuterCheckArray = new String[6][];
	
	@Override
	public void onCreate() {
		super.onCreate();

		/*OkHttpClient okHttpClient = new OkHttpClient.Builder()
					.cookieJar(new CookieJar() {
						private final HashMap<String, List<Cookie>> cookieStore = new HashMap<String, List<Cookie>>();
						
						@Override
						public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
							cookieStore.put(url.host(), cookies);
						}
						
						@Override
						public List<Cookie> loadForRequest(HttpUrl url) {
							List<Cookie> cookies = cookieStore.get(url.host());
				            return cookies != null  ? cookies : new ArrayList<Cookie>();
						}
					})
					.connectTimeout(10000L, TimeUnit.MILLISECONDS)
					.readTimeout(10000L, TimeUnit.MILLISECONDS)
					.build();*/
		
		persistentCookieStore = new PersistentCookieStore(getApplicationContext());
        CookieJarImpl cookieJarImpl = new CookieJarImpl(persistentCookieStore);
		
        OkHttpClient okHttpClient = new OkHttpClient.Builder().cookieJar(cookieJarImpl)
							        		.connectTimeout(5000L, TimeUnit.MILLISECONDS)
											.readTimeout(10000L, TimeUnit.MILLISECONDS)
											.build();
		
		OkHttpUtils.initClient(okHttpClient);
	}


	public String[] initOuterCheckItems(int i){
		switch(i){
			case 1:
				return getResources().getStringArray(R.array.checkitems1);
			case 2:
				return getResources().getStringArray(R.array.checkitems2);
			case 3:
				return getResources().getStringArray(R.array.checkitems3);
			case 4:
				return getResources().getStringArray(R.array.checkitems4);
			case 6:
				return getResources().getStringArray(R.array.checkitems6);
			case 7:
				return getResources().getStringArray(R.array.checkitems7);
		}
		return null;
	}


	public PersistentCookieStore getPersistentCookieStore() {
		return persistentCookieStore;
	}
	
	
}
