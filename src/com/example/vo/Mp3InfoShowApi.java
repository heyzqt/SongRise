package com.example.vo;

/**
 * showApi的music数据
 * 
 * @author zq
 * 
 */
public class Mp3InfoShowApi {

	private long id; // 存放本地播放列表所有歌曲的showAPI----musicid
	private long mp3InfoshowId;
	private String musicname;
	private String singername;

	

	public long getMp3InfoshowId() {
		return mp3InfoshowId;
	}

	public void setMp3InfoshowId(long mp3InfoshowId) {
		this.mp3InfoshowId = mp3InfoshowId;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getMusicname() {
		return musicname;
	}

	public void setMusicname(String musicname) {
		this.musicname = musicname;
	}

	public String getSingername() {
		return singername;
	}

	public void setSingername(String singername) {
		this.singername = singername;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Mp3InfoShowApi[id=" + id + ",mp3InfoshowId=" + mp3InfoshowId
				+ ",musicname=" + musicname + ",singername=" + singername + "]";
	}
}
