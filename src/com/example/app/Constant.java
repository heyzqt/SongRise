package com.example.app;

import com.example.songriseplayer.PlayService;

/**
 * 常量池类
 * 
 * @author zq
 * 
 */

public class Constant {

	public static final String SP_NAME = "SongRiseMusic"; // sharepreferences私有属性文件名
	public static final String DB_NAME = "SongRise.db"; // 数据库名称
	public static final String CURRENT_POSITION = "currentPosition"; // sharepreferences播放歌曲位置key
	public static final String PLAY_MODE = "play_mode"; // sharepreferences播放模式key
	public static final String SONGLIST = "songlist"; // sharepreferences歌单数据key
	public static final String SONGMAP_KEY = "songlist_name"; // songlist中map的key值
	public static final String SONGLIST_ID = "songlistId"; // songlist的id
	public static final String SONGLIST_CHECKED = "songlist_checked"; // songlist的checked标记

	public static final int PLAY_RECORD_NUM = 10; // 最近播放显示的最大条数
	public static final int RESULT_OK = 0; // 接收intent回传数据标志
	public static final int REQUEST_CODE_1 = 1; // 请求码 1
	public static final String CURRENT_PLAY_MUSIC="current_play_music";  //判断当前播放歌曲是本地还是网络
	public static final int NULL_NET_MUSIC=-1;
	public static final String CURRENT_MUSIC_MODE="current_music_mode";  //当前音乐播放列表
	public static final String CODE="code";

	// 微博app KEY
	// @author zyq
	public static final String APP_KEY = "821733501";
	public static final String REDIRECT_URL = "http://www.sina.com";// 微博回调地址
	public static final int IO_BUFFER_SIZE = 2 * 1024;
	public static final String SCOPE = "email,direct_messages_read,direct_messages_write,"
			+ "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
			+ "follow_app_official_microblog," + "invitation_write";// zyq

	// 百度音乐
	public static final String BAIDU_URL = "http://music.baidu.com/";
	// 热歌榜
	public static final String BAIDU_DAYHOT = "top/dayhot/?pst=shouyeTop";
	// 搜索
	public static final String BAIDU_SEARCH = "";

	public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:44.0) Gecko/20100101 Firefox/44.0";

	// 成功标记
	public static final int SUCCESS = 1;
	// 失败标记
	public static final int FAILED = 2;
	// 放歌曲的文件夹和放歌词的文件夹路径
	public static final String DIR_MUSIC = "/songrise_music";
	public static final String DIR_LRC = "/songrise_music/lrc";
}
