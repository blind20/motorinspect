package com.shsy.motoinspect.entity;


import android.os.Parcel;
import android.os.Parcelable;

public class CheckItemEntity implements Parcelable{
	
	private int seq;
	private String textCheckItem;
	private int checkflag;
	private String failReason;
	
	
	public CheckItemEntity(){}
	
	public CheckItemEntity(int seq, String textCheckItem, int checkflag, String failReason) {
		this.seq = seq;
		this.textCheckItem = textCheckItem;
		this.checkflag = checkflag;
		this.failReason = failReason;
	}

	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

	public String getTextCheckItem() {
		return textCheckItem;
	}

	public void setTextCheckItem(String textCheckItem) {
		this.textCheckItem = textCheckItem;
	}

	public int getCheckflag() {
		return checkflag;
	}

	public void setCheckflag(int checkflag) {
		this.checkflag = checkflag;
	}
	
	
	
	public String getFailReason() {
		return failReason;
	}

	public void setFailReason(String failReason) {
		this.failReason = failReason;
	}



	public final static Parcelable.Creator<CheckItemEntity> CREATOR =new Creator<CheckItemEntity>() {
		
		@Override
		public CheckItemEntity[] newArray(int size) {
			return new CheckItemEntity[size];
		}
		
		@Override
		public CheckItemEntity createFromParcel(Parcel source) {
			CheckItemEntity checkItem = new CheckItemEntity();
			checkItem.seq = source.readInt();
			checkItem.textCheckItem = source.readString();
			checkItem.checkflag = source.readInt();
			checkItem.failReason =source.readString();
			return checkItem;
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeInt(seq);
		parcel.writeString(textCheckItem);
		parcel.writeInt(checkflag);
		parcel.writeString(failReason);
	}
	
	
	

}
