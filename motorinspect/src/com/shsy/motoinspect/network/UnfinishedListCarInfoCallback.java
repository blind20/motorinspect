package com.shsy.motoinspect.network;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.shsy.motoinspect.entity.CarListInfoEntity;
import com.shsy.motoinspect.utils.Logger;
import com.zhy.http.okhttp.callback.Callback;

import okhttp3.Response;

public abstract class UnfinishedListCarInfoCallback extends Callback<List<CarListInfoEntity>> {

	

	@Override
	public List<CarListInfoEntity> parseNetworkResponse(Response response, int id) {
		List<CarListInfoEntity> cars = new ArrayList<CarListInfoEntity>() ;
		try {
			String carListStr = response.body().string();
			JSONObject jsonobject = new JSONObject(carListStr);
			JSONArray jsons = jsonobject.getJSONArray("rows");
			for (int i = 0; i < jsons.length(); i++) {
				CarListInfoEntity carInfo = new CarListInfoEntity();
				carInfo.setLsh(jsons.getJSONObject(i).getString("jylsh"));
				carInfo.setHphm(jsons.getJSONObject(i).getString("hphm"));
				carInfo.setHpzl(jsons.getJSONObject(i).getString("hpzl"));
				carInfo.setDate(jsons.getJSONObject(i).getString("dlsj"));
				carInfo.setJyjgbh(jsons.getJSONObject(i).getString("jyjgbh"));
				carInfo.setJycs(jsons.getJSONObject(i).getInt("jycs"));
				carInfo.setJyxm(jsons.getJSONObject(i).getString("jyxm"));
				carInfo.setJcxdh(jsons.getJSONObject(i).getString("jcxdh"));
				carInfo.setClsbdh(jsons.getJSONObject(i).getString("clsbdh"));
				carInfo.setId(jsons.getJSONObject(i).getString("id"));
				carInfo.setCheckType(jsons.getJSONObject(i).getInt("checkType"));
				carInfo.setFjjyxm(jsons.getJSONObject(i).getString("fjjyxm"));
				
				Object zjlb = (jsons.getJSONObject(i)).get("zjlb");
				if(zjlb!=null){
					if(zjlb.toString().equals("null")){
						carInfo.setZjlb(0);
					}else{
						carInfo.setZjlb(jsons.getJSONObject(i).getInt("zjlb"));
					}
				}else{
					carInfo.setZjlb(0);
				}
				
				
				cars.add(carInfo);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			Logger.show("UnfinishedListCarInfoCallback", "JSONException="+e.getMessage());
		}catch (Exception e1) {
			Logger.show("UnfinishedListCarInfoCallback", "Exception="+e1.getMessage());
		}
		
		return cars;
	}

}
