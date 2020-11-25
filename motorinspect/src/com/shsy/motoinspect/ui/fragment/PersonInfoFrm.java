package com.shsy.motoinspect.ui.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.shsy.motoinspect.BaseApplication;
import com.shsy.motoinspect.BaseFragment;
import com.shsy.motoinspect.CommonConstants;
import com.shsy.motoinspect.common.CommonAdapter;
import com.shsy.motoinspect.common.ViewHolder;
import com.shsy.motoinspect.entity.CarListInfoEntity;
import com.shsy.motoinspect.network.ListCarInfoCallback;
import com.shsy.motoinspect.network.PersistentCookieStore;
import com.shsy.motoinspect.ui.activity.SettingsActivity;
import com.shsy.motoinspect.ui.fragment.LoginFrm.OnLoginListener;
import com.shsy.motoinspect.ui.fragment.NavigationFrm.OnMenuSelListener;
import com.shsy.motoinspect.utils.Logger;
import com.shsy.motoinspect.utils.PhoneUtils;
import com.shsy.motoinspect.utils.SharedPreferenceUtils;
import com.shsy.motoinspect.utils.ToastUtils;
import com.shsy.motoinspect.utils.ToolUtils;
import com.shsy.motorinspect.R;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.HttpUrl;


public class PersonInfoFrm extends BaseFragment implements View.OnClickListener{


	private ListView mListView;
	private TextView tv_usrname;
	private ImageView iv_head;
	
	
	private String mCurrentUsr = null;
	
	@Override 
	public int getLayoutResID() {
		return R.layout.frm_personinfo;
	}


	@Override
	public void initParam() {

		initView();
		
		getCurrentUsr();
		
		tv_usrname.setOnClickListener(this);
		iv_head.setOnClickListener(this);
		
		mListView.setAdapter(new CommonAdapter<ItemHolder>(initItems(),
				mActivity,R.layout.item_dialog_define) {

					@Override
					public void convert(ViewHolder holder, ItemHolder t) {

						holder.setText(R.id.item_text, t.text)
						.setImageResource(R.id.item_imageview, t.ImageID);
					}
		});
		
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(position == 0){
					Intent intent = new Intent(mActivity,SettingsActivity.class);
					intent.putExtra(CommonConstants.TO_SETTING, position);
					startActivity(intent);
				}else if(position == 1){
					mCallback.onItemSel(position);
				}
				else if(position == 2){
					String version = PhoneUtils.getVersionName(mActivity);
					ToastUtils.showToast(mActivity, version, Toast.LENGTH_SHORT);
				}
			}
		});
	}
	

	
	
	private void getCurrentUsr() {
		
		String sessionId = (String) SharedPreferenceUtils.get(mActivity, CommonConstants.JSESSIONID, "");
		final String userPref = (String) SharedPreferenceUtils.get(mActivity, CommonConstants.USERNAME, "");
		String url = ToolUtils.getCurrentUserUrl(mActivity);
		if(TextUtils.isEmpty(sessionId)|| TextUtils.isEmpty(userPref)|| TextUtils.isEmpty(url)){
			return;
		}
		
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Cookie", "JSESSIONID="+sessionId);

		OkHttpUtils.post().url(url).headers(headers)
				.build().execute(new StringCallback() {
					
					@Override
					public void onResponse(String response, int id) {
						try {
							JSONObject jo = new JSONObject(response);
							String usr = jo.optString("userName");
							if (TextUtils.isEmpty(usr)) {
								return;
							}
							if (usr.equals(userPref)) {
								Logger.show("getCurrentUsr", "usr = " + usr);
								mCurrentUsr = (String) usr;
								tv_usrname.setText(mCurrentUsr);
							} else {
								mCurrentUsr = null;
							}
						} catch (JSONException e) {
							mCurrentUsr = null;
							e.printStackTrace();
						}
					}
					
					@Override
					public void onError(Call call, Exception e, int id) {
						mCurrentUsr = null;
					}
				});
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == CommonConstants.REQUEST_LOGIN){
			if(TextUtils.isEmpty(data.getStringExtra(CommonConstants.USERNAME))){
				return;
			}
			tv_usrname.setText(data.getStringExtra(CommonConstants.USERNAME));
		}
	}
	
	
	
	
	public void initView() {
		mListView = (ListView) mRootView.findViewById(R.id.listview);
		tv_usrname = (TextView) mRootView.findViewById(R.id.tv_usrname);
		iv_head = (ImageView) mRootView.findViewById(R.id.iv_head);
		
	}
	
	

	public List<ItemHolder> initItems(){
		List<ItemHolder> list = new ArrayList<PersonInfoFrm.ItemHolder>();
		
		String[] itemTexts = getResources().getStringArray(R.array.pernfrm_item);
		String[] itemImgs = getResources().getStringArray(R.array.pernfrm_item_img);
		for(int i=0;i<itemTexts.length;i++){
			int resId = getResources().getIdentifier(itemImgs[i], "drawable", "com.shsy.motorinspect");
			list.add(new ItemHolder(itemTexts[i], resId));
		}
		
		return list;
	}
	
	
	
	public static class ItemHolder{  
        public String text;  
        public int ImageID;  
        public ItemHolder(String title,int imageID){  
            this.text = title;  
            this.ImageID = imageID;  
        }  
    }



	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		
			case R.id.tv_usrname:
			case R.id.iv_head:
				toLoginFrm();
				break;
		}
	}
	
	private void toLoginFrm() {
		Intent intent = new Intent(mActivity,SettingsActivity.class);
		intent.putExtra(CommonConstants.TO_SETTING, -1);
		startActivityForResult(intent, CommonConstants.REQUEST_LOGIN);
	}
	
	
	/**
	 * 回调接口定义及attach
	 */
	protected OnItemSelListener mCallback;
	public interface OnItemSelListener{
		public void onItemSel(int position);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCallback = (OnItemSelListener) mActivity;
		} catch (ClassCastException e) {
			e.printStackTrace();
			throw new ClassCastException(mActivity.toString()
					+ " must implement OnItemSelListener");
		}
	}
	
}
