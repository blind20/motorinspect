package com.shsy.motoinspect.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class CarListInfoEntity implements Parcelable{
	
	
	private String hphm;
	private String hpzl;
	private String lsh;
	private String date;
	private int flag;
	private String jyjgbh;
	private int jycs;
	private String jyxm;
	private String jcxdh;
	private String clsbdh;
	private String id;
	private int checkType;
	private int zjlb;
	private String fjjyxm;
	
	public CarListInfoEntity(){
		
	}

	public CarListInfoEntity(String hphm, String hpzl, String lsh, String date, int flag, String jyjgbh, int jycs,
			String jyxm, String jcxdh, String clsbdh,String id,int checkType,int zjlb,String fjjyxm) {
		super();
		this.hphm = hphm;
		this.hpzl = hpzl;
		this.lsh = lsh;
		this.date = date;
		this.flag = flag;
		this.jyjgbh = jyjgbh;
		this.jycs = jycs;
		this.jyxm = jyxm;
		this.jcxdh = jcxdh;
		this.clsbdh = clsbdh;
		this.id = id;
		this.checkType = checkType;
		this.zjlb = zjlb;
		this.fjjyxm = fjjyxm;
	}

	
	

	public String getHphm() {
		return hphm;
	}

	public void setHphm(String hphm) {
		this.hphm = hphm;
	}

	public String getHpzl() {
		return hpzl;
	}

	public void setHpzl(String hpzl) {
		this.hpzl = hpzl;
	}

	public String getLsh() {
		return lsh;
	}

	public void setLsh(String lsh) {
		this.lsh = lsh;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public String getJyjgbh() {
		return jyjgbh;
	}

	public void setJyjgbh(String jyjgbh) {
		this.jyjgbh = jyjgbh;
	}

	public int getJycs() {
		return jycs;
	}

	public void setJycs(int jycs) {
		this.jycs = jycs;
	}

	public String getJyxm() {
		return jyxm;
	}

	public void setJyxm(String jyxm) {
		this.jyxm = jyxm;
	}

	public String getJcxdh() {
		return jcxdh;
	}

	public void setJcxdh(String jcxdh) {
		this.jcxdh = jcxdh;
	}

	public String getClsbdh() {
		return clsbdh;
	}

	public void setClsbdh(String clsbdh) {
		this.clsbdh = clsbdh;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public int getCheckType() {
		return checkType;
	}
	
	public void setCheckType(int checkType) {
		this.checkType = checkType;
	}
	

	public int getZjlb() {
		return zjlb;
	}

	public void setZjlb(int zjlb) {
		this.zjlb = zjlb;
	}

	public String getFjjyxm() {
		return fjjyxm;
	}

	public void setFjjyxm(String fjjyxm) {
		this.fjjyxm = fjjyxm;
	}



	public final static Parcelable.Creator<CarListInfoEntity> CREATOR =new Creator<CarListInfoEntity>() {
		
		@Override
		public CarListInfoEntity[] newArray(int size) {
			return new CarListInfoEntity[size];
		}
		
		@Override
		public CarListInfoEntity createFromParcel(Parcel source) {
			CarListInfoEntity car = new CarListInfoEntity();
			car.hphm = source.readString();
			car.hpzl=source.readString();
			car.lsh = source.readString();
			car.date = source.readString();
			car.flag = source.readInt();
			car.jyjgbh = source.readString();
			car.jycs = source.readInt();
			car.jyxm = source.readString();
			car.jcxdh = source.readString();
			car.clsbdh = source.readString();
			car.id = source.readString();
			car.checkType = source.readInt();
			car.zjlb = source.readInt();
			car.fjjyxm = source.readString();
			return car;
		}
	};
	

	@Override
	public int describeContents() {
		return 0;
	}
	

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(hphm);
		dest.writeString(hpzl);
		dest.writeString(lsh);
		dest.writeString(date);
		dest.writeInt(flag);
		dest.writeString(jyjgbh);
		dest.writeInt(jycs);
		dest.writeString(jyxm);
		dest.writeString(jcxdh);
		dest.writeString(clsbdh);
		dest.writeString(id);
		dest.writeInt(checkType);
		dest.writeInt(zjlb);
		dest.writeString(fjjyxm);
	}
	
	

	
}
