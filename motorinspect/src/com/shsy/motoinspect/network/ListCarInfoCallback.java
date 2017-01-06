package com.shsy.motoinspect.network;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import com.shsy.motoinspect.CommonConstants;
import com.shsy.motoinspect.entity.CarListInfoEntity;
import com.zhy.http.okhttp.callback.Callback;

import okhttp3.Response;

public abstract class ListCarInfoCallback extends Callback<List<CarListInfoEntity>> {

	

	@Override
	public List<CarListInfoEntity> parseNetworkResponse(Response response, int id) throws Exception {
		String carListStr = response.body().string();
		List<CarListInfoEntity> cars = new ArrayList<CarListInfoEntity>() ;
		JSONArray jsons = new JSONArray(carListStr);
		for (int i = 0; i < jsons.length(); i++) {
			CarListInfoEntity carInfo = new CarListInfoEntity();
			carInfo.setLsh(jsons.getJSONObject(i).getString("jylsh"));
			carInfo.setHphm(jsons.getJSONObject(i).getString("hphm"));
			carInfo.setHpzl(jsons.getJSONObject(i).getString("hpzl"));
			carInfo.setDate(jsons.getJSONObject(i).getString("dlsj"));
			carInfo.setFlag(CommonConstants.NOTPULLCAR);
			carInfo.setJyjgbh(jsons.getJSONObject(i).getString("jyjgbh"));
			carInfo.setJycs(jsons.getJSONObject(i).getInt("jycs"));
			carInfo.setJyxm(jsons.getJSONObject(i).getString("jyxm"));
			carInfo.setJcxdh(jsons.getJSONObject(i).getString("jcxdh"));
			carInfo.setClsbdh(jsons.getJSONObject(i).getString("clsbdh"));
			carInfo.setId(jsons.getJSONObject(i).getString("id"));
			cars.add(carInfo);
		}
		
		return cars;
	}

}
