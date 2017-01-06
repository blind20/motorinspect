package com.shsy.motoinspect.entity;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class CarPhotoEntity  implements Parcelable{

	private String photoTypeCode;
	private String photoTypeName;
	private Bitmap thumbnailBmp;
	private String uploadPhotoFilePath;
	private String thumbnailPhotoFilePath;
	private String isMustFlag;
	
	public CarPhotoEntity() {
	}

	
	public CarPhotoEntity(String photoTypeCode, String photoTypeName, Bitmap bitmap, 
			String uploadPhotoFilePath,String thumbnailPhotoFilePath,String isMustFlag) {
		super();
		this.photoTypeCode = photoTypeCode;
		this.photoTypeName = photoTypeName;
		this.thumbnailBmp = bitmap;
		this.uploadPhotoFilePath = uploadPhotoFilePath;
		this.thumbnailPhotoFilePath = thumbnailPhotoFilePath;
		this.isMustFlag = isMustFlag;
	}

	public String getPhotoTypeCode() {
		return photoTypeCode;
	}
	public void setPhotoTypeCode(String photoTypeCode) {
		this.photoTypeCode = photoTypeCode;
	}
	public String getPhotoTypeName() {
		return photoTypeName;
	}
	public void setPhotoTypeName(String photoTypeName) {
		this.photoTypeName = photoTypeName;
	}
	public Bitmap getThumbnailBmp() {
		return thumbnailBmp;
	}
	public void setThumbnailBmp(Bitmap bitmap) {
		this.thumbnailBmp = bitmap;
	}

	public String getUploadPhotoFilePath() {
		return uploadPhotoFilePath;
	}
	public void setUploadPhotoFilePath(String uploadPhotoFilePath) {
		this.uploadPhotoFilePath = uploadPhotoFilePath;
	}

	
	public String getThumbnailPhotoFilePath() {
		return thumbnailPhotoFilePath;
	}

	public void setThumbnailPhotoFilePath(String thumbnailPhotoFilePath) {
		this.thumbnailPhotoFilePath = thumbnailPhotoFilePath;
	}


	public String getIsMustFlag() {
		return isMustFlag;
	}


	public void setIsMustFlag(String isMustFlag) {
		this.isMustFlag = isMustFlag;
	}



	public static final Parcelable.Creator<CarPhotoEntity> CREATOR = new Creator<CarPhotoEntity>() { 
		
        public CarPhotoEntity createFromParcel(Parcel source) { 
        	CarPhotoEntity cpe = new CarPhotoEntity(); 
        	cpe.photoTypeCode = source.readString(); 
        	cpe.photoTypeName = source.readString(); 
        	cpe.thumbnailBmp = Bitmap.CREATOR.createFromParcel(source);
        	cpe.uploadPhotoFilePath = source.readString();
        	cpe.thumbnailPhotoFilePath = source.readString();
        	cpe.isMustFlag = source.readString();
            return cpe; 
        } 
        
        public CarPhotoEntity[] newArray(int size) { 
            return new CarPhotoEntity[size]; 
        } 
    }; 

	@Override
	public int describeContents() {
		return 0;
	}


	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeString(photoTypeCode);
		parcel.writeString(photoTypeName);
		thumbnailBmp.writeToParcel(parcel, 0);
		parcel.writeString(uploadPhotoFilePath);
		parcel.writeString(thumbnailPhotoFilePath);
		parcel.writeString(isMustFlag);
	}
	
	
}
