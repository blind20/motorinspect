package com.shsy.motoinspect.adapter;


import java.util.List;

import com.shsy.motoinspect.CommonConstants;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

public class TabAdapter extends FragmentPagerAdapter {
	
//	public static String[] TITLES = new String[]
//			{ "�������", "��ȫװ��", "����", "������", "����" };
	
	public static String[] APPEARANCETITLES = new String[]
			{ "Ψһ��", "��������", "��ۼ��" ,"��ȫװ��" ,"����"};
	public static String[] CHASSISTITLES = new String[]
			{ "���̲������"};
	public static String[] DYNAMICTITLES = new String[]
			{ "���̶�̬����","���̶�̬����"};
	public static String[] REPHOTOTITLES = new String[]
			{ "���Ļ���"};
	public String[] TITLES;
	
	public List<Fragment> mFragments;
	public Fragment currentFragment;

	public TabAdapter(FragmentManager fm) {
		super(fm);
	}
	
	public TabAdapter(FragmentManager fm,List<Fragment> fms,int type) {
		super(fm);
		this.mFragments = fms;
		switch (type) {
			case CommonConstants.APPEARANCE:
				TITLES = APPEARANCETITLES;
				break;

			case CommonConstants.CHASSIS:
				TITLES = CHASSISTITLES;
				break;
				
			case CommonConstants.DYNAMIC:
				TITLES = DYNAMICTITLES;
				break;
			
			case CommonConstants.REPHOTO:
				TITLES = REPHOTOTITLES;
				break;
			
		}
	}

	@Override
	public Fragment getItem(int arg0) {
		return mFragments.get(arg0);
	}

	@Override
	public int getCount() {
		if(TITLES == null){
			return 0;
		}
		return TITLES.length;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		if(TITLES == null){
			return null;
		}
		return TITLES[position];
	}
	
	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object) {
		currentFragment = (Fragment) object;
		super.setPrimaryItem(container, position, object);
	}
	
	
}
