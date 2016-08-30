package com.example.vo;

/*
 * 音乐对象类
 * @author:zq
 */
public class Mp3Info {

	private long id; // 歌曲id
	private long mp3InfoId; // 收藏音乐时用于保存原始id
	private int isLike; // 1 喜欢 0默认
	private long playTime; // 最近播放时间
	private String title; // 歌名
	private String artist; // 歌手名
	private String album; // 专辑名
	private long albumId; // 专辑id
	private long duration; // 时长
	private long size; // 大小
	private String url; // 路径
	private int isMusic; // 是否是音乐
	private int isDownload;   //1是已下载 0是未下载

	
	
	
	public int getIsDownload() {
		return isDownload;
	}

	public void setIsDownload(int isDownload) {
		this.isDownload = isDownload;
	}

	public int getIsLike() {
		return isLike;
	}

	public void setIsLike(int isLike) {
		this.isLike = isLike;
	}

	public long getPlayTime() {
		return playTime;
	}

	public void setPlayTime(long playTime) {
		this.playTime = playTime;
	}

	public long getMp3InfoId() {
		return mp3InfoId;
	}

	public void setMp3InfoId(long mp3InfoId) {
		this.mp3InfoId = mp3InfoId;
	}

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

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public String getAlbum() {
		return album;
	}

	public void setAlbum(String album) {
		this.album = album;
	}

	public long getAlbumId() {
		return albumId;
	}

	public void setAlbumId(long albumId) {
		this.albumId = albumId;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getIsMusic() {
		return isMusic;
	}

	public void setIsMusic(int isMusic) {
		this.isMusic = isMusic;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Mp3Info[id=" + id + ",title=" + title + ",artist=" + artist
				+ ",album=" + album + ",albumId=" + albumId + ",duration="
				+ duration + ",size=" + size + ",url+" + url + ",isMusic="
				+ isMusic + ",mp3InfoId=" + mp3InfoId + ",isLike=" + isLike
				+ ",playTime=" + playTime + ",isDownload="+isDownload+"]";
	}
}
