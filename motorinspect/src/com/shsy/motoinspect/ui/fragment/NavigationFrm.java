package com.shsy.motoinspect.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import com.shsy.motoinspect.BaseFragment;
import com.shsy.motoinspect.common.CommonAdapter;
import com.shsy.motoinspect.common.TitleBarView;
import com.shsy.motoinspect.common.ViewHolder;
import com.shsy.motoinspect.entity.CheckItemEntity;
import com.shsy.motoinspect.entity.MenuItemEntity;
import com.shsy.motoinspect.ui.activity.MainActivity;
import com.shsy.motoinspect.ui.activity.VehListActivity;
import com.shsy.motoinspect.utils.Logger;
import com.shsy.motoinspect.utils.ToastUtils;
import com.shsy.motorinspect.R;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class NavigationFrm extends BaseFragment {

	private TitleBarView mTitleBarView;
	private ListView mListView;
	private boolean isRecycle =false;
	
	private CommonAdapter<MenuItemEntity> adapter;
	//标记是外观导航还是引车导航
	private int mNaviType = -1;
	public final static String mNavigation ="navigation";
	public final static String TO_VEHLISTATY ="to_veh_list_aty";
	
	public NavigationFrm(){
	}
	

	
	public void setmFailItems(List<CheckItemEntity> items) {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getArguments()==null){
			return;
		}
		mNaviType = getArguments().getInt(MainActivity.NAVI_TYPE);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(savedInstanceState != null ){
			isRecycle = savedInstanceState.getBoolean("isRecycle");
			if(isRecycle){
				mNaviType = savedInstanceState.getInt(this.mNavigation);
			}
		}
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	


	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		isRecycle = true;
		outState.putBoolean("isRecycle", isRecycle);
		outState.putInt(this.mNavigation, mNaviType);
	}
	
	

	@Override
	public void initParam() {
		initView(mNaviType);
		List<MenuItemEntity> list = initDatas(mNaviType);
		viewSetAdapter(list,mNaviType);
	}


	


	private List<MenuItemEntity> initDatas(int type) {
		List<MenuItemEntity> list = new ArrayList<MenuItemEntity>();
		if(type == MainActivity.NAVI_TYPE_1){
			list.add(new MenuItemEntity(R.drawable.main_card_memory_icon,getString(R.string.outer_nav1_menu1),getString(R.string.outer_nav1_menu1_des)));
			list.add(new MenuItemEntity(R.drawable.main_card_file_icon,getString(R.string.outer_nav1_menu2),getString(R.string.outer_nav1_menu2_des)));
			list.add(new MenuItemEntity(R.drawable.main_card_trash_icon,getString(R.string.outer_nav1_menu3),getString(R.string.outer_nav1_menu3_des)));
		}
		if(type == MainActivity.NAVI_TYPE_2){
			list.add(new MenuItemEntity(R.drawable.main_card_memory_icon,getString(R.string.outer_nav2_menu1),getString(R.string.outer_nav2_menu1_des)));
			list.add(new MenuItemEntity(R.drawable.main_card_file_icon,getString(R.string.outer_nav2_menu2),getString(R.string.outer_nav2_menu2_des)));
			list.add(new MenuItemEntity(R.drawable.main_card_trash_icon,getString(R.string.outer_nav2_menu3),getString(R.string.outer_nav2_menu3_des)));
		}
		return list;
	}

	
	
	private void viewSetAdapter(final List<MenuItemEntity> list,final int type) {
		
		adapter = new CommonAdapter<MenuItemEntity>(list,mActivity,R.layout.item_frm_menuitem_list) {
			
			@Override
			public void convert(ViewHolder holder, MenuItemEntity entity) {
				if(type == MainActivity.NAVI_TYPE_2){
					holder.setImageResource(R.id.iv_card_bg, R.drawable.main_card_file_hexagon_bg1);
				}
				 
				holder.setImageResource(R.id.iv_card_in, entity.iconRes)
						.setText(R.id.tv_menu_title, entity.title)
						.setText(R.id.tv_menu_subtitle, entity.subTitle);
			}
		};
		mListView.setAdapter(adapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
					String title = list.get(position).title;
					mCallback.onMenuItemSel(title);
				}
		});
	}
	

	
	private void initView(int type) {
		mTitleBarView = new TitleBarView(mActivity);
		mTitleBarView = (TitleBarView) mRootView.findViewById(R.id.titlebar);
		mTitleBarView.setCommonTitle(View.GONE, View.VISIBLE, View.GONE);
		
		if(type == MainActivity.NAVI_TYPE_1){
			mTitleBarView.setTitle(R.string.outer_nav1_menu1);
		}else if(type == MainActivity.NAVI_TYPE_2){
			mTitleBarView.setTitle(R.string.outer_nav2_menu1);
		}
		
		mListView = (ListView) mRootView.findViewById(R.id.listview);
		
	}



	@Override
	public int getLayoutResID() {
		return R.layout.listview_comm_addtitle;
	}
	
	
	/**
	 * 回调接口定义及attach
	 */
	protected OnMenuSelListener mCallback;
	public interface OnMenuSelListener{
		public void onMenuItemSel(String menuTitle);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCallback = (OnMenuSelListener) mActivity;
		} catch (ClassCastException e) {
			e.printStackTrace();
			throw new ClassCastException(mActivity.toString()
					+ " must implement OnCarItemSelListener");
		}
	}
	
}
