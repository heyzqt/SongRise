package com.example.vo;

/*
 * 保存网络音乐歌曲信息类
 * @author:zq
 */
public class SearchResult {

	private String musicName; // 歌曲名
	private String url; // 歌曲路径
	private String artist; // 歌手名
	private String album; // 专辑

	public String getMusicName() {
		return musicName;
	}

	public void setMusicName(String musicName) {
		this.musicName = musicName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
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

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Mp3Info[musicName=" + musicName + ",url=" + url + ",artist="
				+ artist + ",album=" + album + ",url=" + url + "]";
	}
}
