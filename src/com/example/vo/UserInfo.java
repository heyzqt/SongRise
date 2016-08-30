package com.example.vo;

import java.sql.Date;

/*
 * 用户对象类
 * @author:zq
 */
public class UserInfo {

	private long id; // 用户id
	private String password; // 用户密码
	private String name; // 用户昵称
	private String image; // 用户头像
	private int sex; // 用户性别 1为女 0为男
	private Date birthday; // 用户生日
	private String location; // 用户所在地区
	private String message; // 用户个性签名

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "UserInfo[id=" + id + ",password=" + password + ",name=" + name
				+ ",image=" + image + ",sex=" + sex + ",birthday=" + birthday
				+ ",location=" + location + ",message" + "]";
	}
}
