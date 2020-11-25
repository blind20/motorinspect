package com.shsy.motoinspect.entity;

import android.graphics.Bitmap;

public class VideoInfo {
	
	private String videoName;
	private String videoCode;
	private String videoFilePath;
	private Bitmap thumbnailBmp;
	
	public String getVideoName() {
		return videoName;
	}
	public String getVideoCode() {
		return videoCode;
	}
	public String getVideoFilePath() {
		return videoFilePath;
	}
	public Bitmap getThumbnailBmp() {
		return thumbnailBmp;
	}
	public void setVideoName(String videoName) {
		this.videoName = videoName;
	}
	public void setVideoCode(String videoCode) {
		this.videoCode = videoCode;
	}
	public void setVideoFilePath(String videoFilePath) {
		this.videoFilePath = videoFilePath;
	}
	public void setThumbnailBmp(Bitmap thumbnailBmp) {
		this.thumbnailBmp = thumbnailBmp;
	}
	@Override
	public String toString() {
		return "VideoInfo [videoName=" + videoName + ", videoCode=" + videoCode + ", videoFilePath=" + videoFilePath
				+ "]";
	}
	
	
}
