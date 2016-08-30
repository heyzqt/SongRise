package com.example.vo;

/**
 * 歌单-歌曲信息表
 * 
 * @author zq
 * 
 */
public class SongAndMusicInfo {

	private long id; // 表的主键id
	private long songlistInfoId; // 歌单id
	private long mp3InfoId; // 歌曲id

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getSonglistInfoId() {
		return songlistInfoId;
	}

	public void setSonglistInfoId(long songlistInfoId) {
		this.songlistInfoId = songlistInfoId;
	}

	public long getMp3InfoId() {
		return mp3InfoId;
	}

	public void setMp3InfoId(long mp3InfoId) {
		this.mp3InfoId = mp3InfoId;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "SongAndMusicInfo[id=" + id + ",songlistInfoId="
				+ songlistInfoId + ",mp3InfoId=" + mp3InfoId + "]";
	}
}
