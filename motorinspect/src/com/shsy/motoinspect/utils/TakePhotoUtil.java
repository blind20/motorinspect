package com.shsy.motoinspect.utils;

import java.util.ArrayList;
import java.util.List;

import com.shsy.motoinspect.entity.CarPhotoEntity;
import com.shsy.motoinspect.ui.fragment.OuterPhotoFrm;
import com.shsy.motorinspect.R;

import android.content.Context;

public class TakePhotoUtil {


	public static List<CarPhotoEntity> initFullPhotoList(Context con){
//		Context con = ContextUtil.getInstance();
		List<CarPhotoEntity> list = new ArrayList<CarPhotoEntity>();
		String[] photoNames=con.getResources().getStringArray(R.array.photoname);
		String[] photoCodes=con.getResources().getStringArray(R.array.photocode);
		for(int i=0;i<photoNames.length;i++){
			CarPhotoEntity carPhoto = new CarPhotoEntity(photoCodes[i],photoNames[i],null,"","",OuterPhotoFrm.PHOTO_NOT_MUST);
			list.add(carPhoto);
		}
		return list;
	}
}
