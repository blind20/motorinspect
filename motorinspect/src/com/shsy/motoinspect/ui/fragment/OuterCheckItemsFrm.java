package com.shsy.motoinspect.ui.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.shsy.motoinspect.BaseFragment;
import com.shsy.motoinspect.CommonConstants;
import com.shsy.motoinspect.common.CommonAdapter;
import com.shsy.motoinspect.common.ViewHolder;
import com.shsy.motoinspect.entity.CheckItemEntity;
import com.shsy.motoinspect.network.MyHttpUtils;
import com.shsy.motoinspect.ui.activity.SettingsActivity;
import com.shsy.motoinspect.utils.Logger;
import com.shsy.motoinspect.utils.ToastUtils;
import com.shsy.motoinspect.utils.ToolUtils;
import com.shsy.motorinspect.R;
import com.zhy.http.okhttp.callback.StringCallback;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import okhttp3.Call;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class OuterCheckItemsFrm extends BaseFragment {

	public static final int REQ_CHECKITEM = 0x110;
	
	private ListView mListView;
	private List<CheckItemEntity> mSubDatas;
	private CommonAdapter<CheckItemEntity> adapter;
	
	private RelativeLayout rl_itemfail_reason;
	private TextView tv_itemfail_reason;
	
	private boolean isRecycle = false;
	
	private int mPosition;
	public static final int REQ_ITEMFAIL_REASON = 0x100;
	public static final int ITEMFAIL = 0x120;
	
	private ViewHolder mHolder;
	private CheckItemEntity mEntity;
	private String jylsh;
	private int type;
	
	OnClickCheckItemListener mCallback;
	public interface OnClickCheckItemListener{
		public void onClickCheckItem(CheckItemEntity item);
	}

	
	
	public OuterCheckItemsFrm(){}
	
	public OuterCheckItemsFrm(List<CheckItemEntity> datas) {
		mSubDatas = datas;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		jylsh = getArguments().getString("jylsh");
		type = getArguments().getInt("type");
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(savedInstanceState != null ){
			isRecycle = savedInstanceState.getBoolean("isRecycle");
			if(isRecycle){
				ArrayList list = savedInstanceState.getParcelableArrayList("msubdatas");
				mSubDatas = (List<CheckItemEntity>) list.get(0);
			}
		}
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	


	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		isRecycle = true;
		ArrayList list = new ArrayList();
		list.add(mSubDatas);
		outState.putBoolean("isRecycle", isRecycle);
		outState.putParcelableArrayList("msubdatas", list);
	}
	
	

	@Override
	public void initParam() {
		findView();
		if(!TextUtils.isEmpty(jylsh)){
			initViewByDefalut(mSubDatas,type);
		}
	}



	@Override
	public int getLayoutResID() {
		return R.layout.listview_common;
	}
	
	
	private void findView() {
		mListView = (ListView) mRootView.findViewById(R.id.listview);
		
	}
	

	private void initViewByDefalut(final List<CheckItemEntity> list, final int type) {
		String cytype = null;
		switch (type) {
			case CommonConstants.APPEARANCE:
				cytype = CommonConstants.WGJYXM;
				break;
			case CommonConstants.DYNAMIC:
				cytype = CommonConstants.DTDPJYXM;
				break;
			case CommonConstants.CHASSIS:
				cytype = CommonConstants.DPJYXM;
				break;
		}
		String url = ToolUtils.getChekcItemUrl(mActivity);
		Map<String, String> param = new HashMap<String, String>();
		param.put("jylsh", jylsh);
		param.put("type", cytype);
		MyHttpUtils.getInstance(mActivity).postHttpByParam(url, param, new StringCallback() {
			@Override
			public void onResponse(String response, int id) {
				if(!TextUtils.isEmpty(response)){
					String[] cyxm = response.split(",");
					getDefaultCyxm(list,cyxm);
				}
				viewSetAdapter();
			}
			
			@Override
			public void onError(Call call, Exception e, int id) {
				ToastUtils.showToast(mActivity, "网络问题,获取不到平台必检项目", Toast.LENGTH_LONG);
				viewSetAdapter();
				//测试开始======================================================
				/*String response ="01,05,06,13,21,32,40,42,43,46,48,80";
				if(!TextUtils.isEmpty(response)){
					String[] cyxm = response.split(",");
					getDefaultCyxm(list,cyxm);
				}
				viewSetAdapter();*/
				//测试结束======================================================
			}
		});
	}
	
	protected void getDefaultCyxm(List<CheckItemEntity> list, String[] cyxm) {
		for(int i=0;i<list.size();i++){
			int sequence = list.get(i).getSeq();
			for(String seq:cyxm){
				int index = Integer.parseInt(seq);
				if(sequence == index){
					list.get(i).setCheckflag(CommonConstants.CHECKPASS);
					break;
				}
			}
		}
	}

	private void viewSetAdapter() {
		
		adapter = new CommonAdapter<CheckItemEntity>(mSubDatas,mActivity, R.layout.item_outercheckitems_list) {
			@Override
			public void convert( final ViewHolder holder, final CheckItemEntity entity) {
				
				holder.setText(R.id.tv_seq, Integer.toString(entity.getSeq()));
				holder.setText(R.id.tv_checkitem, entity.getTextCheckItem());
				
				if(CommonConstants.CHECKPASS == entity.getCheckflag()){
					holder.setImageResource(R.id.iv_checkflag,R.drawable.checkflg_pass);
				}else if(CommonConstants.NOTCHECK == entity.getCheckflag()){
					holder.setImageResource(R.id.iv_checkflag,R.drawable.checkflg_notcheck);
				}else{
					holder.setImageResource(R.id.iv_checkflag,R.drawable.checkflg_fail);
				}
				
				final RelativeLayout rl_itemfail_reason = holder.getView(R.id.rl_itemfail_reason);
				final TextView tv_itemfail_reason =  holder.getView(R.id.tv_itemfail_reason);;
				if(entity.getFailReason() !=null){
					Logger.show("entity", "entity:"+entity.getSeq());
					rl_itemfail_reason.setVisibility(View.VISIBLE);
					tv_itemfail_reason.setText(entity.getFailReason());
				}else{
					rl_itemfail_reason.setVisibility(View.GONE);
				}
				
				
				holder.setButtonListen(R.id.btn_editor,new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						mHolder = holder;
						mEntity = entity;
						Intent intent = new Intent(mContext, SettingsActivity.class);
						intent.putExtra(CommonConstants.TO_SETTING, ITEMFAIL);
						startActivityForResult(intent, REQ_ITEMFAIL_REASON);
					}
				});
			}
		};
		
		mListView.setAdapter(adapter);
		
		mListView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				mPosition = position;
				MyDialogFragment myDialog = MyDialogFragment.newInstance(MyDialogFragment.DLG_CHECK);
				myDialog.setTargetFragment(OuterCheckItemsFrm.this, OuterCheckItemsFrm.REQ_CHECKITEM);
				myDialog.show(getFragmentManager(), mSubDatas.get(position).getTextCheckItem());
			}
			
		});
	}
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == OuterCheckItemsFrm.REQ_CHECKITEM){
			int checkRst = data.getIntExtra(MyDialogFragment.RES_CHECKRST, 0);
			mSubDatas.get(mPosition).setCheckflag(checkRst);
			
			adapter.notifyDataSetChanged();
			mCallback.onClickCheckItem(mSubDatas.get(mPosition));
		}else if(requestCode == OuterCheckItemsFrm.REQ_ITEMFAIL_REASON){
			String txt = data.getStringExtra(OuterItemFailReasonFrm.ITEMFAIL_REASON);
			if(TextUtils.isEmpty(txt)){
				return;
			}
			mEntity.setFailReason(txt);
			mHolder.setViewVisibleOrGone(R.id.rl_itemfail_reason, View.VISIBLE);
			mHolder.setText(R.id.tv_itemfail_reason, txt);
			adapter.notifyDataSetChanged();
		}
		
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCallback = (OnClickCheckItemListener) mActivity;
		} catch (ClassCastException e) {
			e.printStackTrace();
			throw new ClassCastException(mActivity.toString()
					+ " must implement OnClickCheckItemListener");
		}
	}

}
