package com.example.vo;

import android.graphics.Bitmap;

/**
 * 
 * @author 张艳琴 从qq获取的第三方信息实体
 */
public class QqUserInfo {
	String openid;
	String sex;
	String nickname;
	Bitmap bitmap;

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

}
