package com.example.vo;

/**
 * 歌单信息表
 * 
 * @author zq
 * 
 */
public class SongListInfo {

	private long id; // 歌单id
	private String title; // 歌单名
	private int count; // 歌单所包含的歌曲数量

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "SongListInfo[id=" + id + ",title=" + title + ",count=" + count
				+ "]";
	}

}
