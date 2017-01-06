package com.shsy.motoinspect.entity;

public class MenuItemEntity {
	public int iconRes;
	public String title;
	public String subTitle;

	public MenuItemEntity(int iconRes, String title, String subTitle) {
		this.iconRes = iconRes;
		this.title = title;
		this.subTitle = subTitle;
	}
}