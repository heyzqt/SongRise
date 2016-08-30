package com.example.app;

import java.util.ArrayList;
import java.util.List;

import com.example.adapter.MyMusicListAdapter;
import com.example.songriseplayer.PlayService;
import com.example.utils.DownloadUtils;
import com.example.utils.L;
import com.example.utils.MediaUtils;
import com.example.vo.Mp3Info;
import com.example.vo.SongAndMusicInfo;
import com.example.vo.SongListInfo;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.View;

/**
 * 
 * @author zq,xft
 * 
 */
public class SongRisePlayerApp extends Application {

	public static SharedPreferences sp;
	public static DbUtils dbUtils;
	public static Context context;

	// xft
	public static SharedPreferences sp_msg;
	private SharedPreferences.Editor ed_msg;
	public SharedPreferences sp_song;
	public Editor ed_song;
	public SharedPreferences sp_end;
	public Editor ed_end;
	public int position = -1;
	public int last_position;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		sp = getSharedPreferences(Constant.SP_NAME, Context.MODE_PRIVATE);
		dbUtils = DbUtils.create(getApplicationContext(), Constant.DB_NAME);
		context = getApplicationContext();

		// xft
		sp_msg = getSharedPreferences("msg", Context.MODE_PRIVATE);
		ed_msg = sp_msg.edit();
		ed_msg.putInt("code", -1);
		ed_msg.commit();

		sp_song = getSharedPreferences("songMsg", Activity.MODE_PRIVATE);
		ed_song = sp_song.edit();
		ed_song.putString("songname", "-");
		ed_song.putString("singername", "-");
		ed_song.commit();
		sp_end = getSharedPreferences("end_msg", Activity.MODE_PRIVATE);
		ed_end = sp_end.edit();

	}

	public ArrayList<Mp3Info> getCurrentLocalMp3Infos() {

		int x = sp.getInt(Constant.CURRENT_MUSIC_MODE,
				PlayService.MY_MUSIC_LIST);
		// int x=sp.getInt(Constant.PLAY_MODE, PlayService.MY_MUSIC_LIST);
		ArrayList<Mp3Info> mp3Infos = new ArrayList<Mp3Info>();
		switch (x) {
		case PlayService.MY_MUSIC_LIST: // 我的音乐
			mp3Infos = MediaUtils.getMp3Infos(this);
			return mp3Infos;
		case PlayService.LIKE_MUSIC_LIST: { // 我的收藏

			List<Mp3Info> list;
			//L.e("我的收藏");
			try {
				list = dbUtils.findAll(Selector.from(Mp3Info.class).where(
						"isLike", "=", "1"));
				if (list == null || list.size() == 0) {
					return null;
				}
				mp3Infos = (ArrayList<Mp3Info>) list;

				return mp3Infos;
			} catch (DbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}

		}

		case PlayService.PLAY_RECORD_MUSIC_LIST: { // 最近播放

			try {
				// 查询最近播放的记录
				List<Mp3Info> list = dbUtils.findAll(Selector
						.from(Mp3Info.class).where("playTime", "!=", 0)
						.orderBy("playTime", true)
						.limit(Constant.PLAY_RECORD_NUM));

				if (list == null || list.size() == 0) {
					return null;
				} else {

					mp3Infos = (ArrayList<Mp3Info>) list;
					return mp3Infos;
				}

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				return null;
			}

		}
		case PlayService.DOWNLOAD_MUSIC_LIST:{		//下载的歌曲
			//获取SongRise/Music中的音乐文件名集合
			String [] filenames;
			filenames=DownloadUtils.getInstance().FileNameScanner();
			if(filenames==null){
				return null;
			}
			ArrayList<Mp3Info> local_mp3Infos=MediaUtils.getMp3Infos(this);
			mp3Infos=new ArrayList<Mp3Info>();
			
			for(int i=0;i<filenames.length;i++){
				String filename=filenames[i];
				for(int j=0;j<local_mp3Infos.size();j++){
					Mp3Info mp3Info = local_mp3Infos.get(j);
					if(filename.equals(mp3Info.getTitle()+".mp3")){
						mp3Infos.add(mp3Info);
						break;
					}
				}
			}
			return mp3Infos;
		}
		case PlayService.CURRENT_SONGLIST_PLAY: { // 自定义歌单歌曲

			// 获取歌单名和歌单id
			String songlist_name = sp.getString("songlistname", "");
			//L.e("歌单名"+songlist_name);
			ArrayList<Mp3Info> song_mp3Infos = new ArrayList<Mp3Info>();
			if(songlist_name==null||songlist_name==""){
				return song_mp3Infos;
			}

			//ArrayList<Mp3Info> song_mp3Infos = new ArrayList<Mp3Info>();
			
			// 从数据库查询歌单-歌曲信息表信息
			try {
				// 先找到歌单id
				SongListInfo songlistInfo = dbUtils.findFirst(Selector.from(
						SongListInfo.class).where("title", "=", songlist_name));

				List<SongAndMusicInfo> list = dbUtils.findAll(Selector.from(
						SongAndMusicInfo.class).where("songlistInfoId", "=",
						songlistInfo.getId()));

				if(list==null||list.size()==0){
					return null;
				}
				/*
				 * 在歌单-歌曲表中找到所有歌曲id 根据歌曲id在歌曲表中找到所有歌曲
				 */
				mp3Infos = MediaUtils.getMp3Infos(this);
				song_mp3Infos = new ArrayList<Mp3Info>();
				ArrayList<SongAndMusicInfo> songAndMusicInfos = (ArrayList<SongAndMusicInfo>) list;

				/*
				 * 非常重要!!!!!! 两个数组相互比较
				 */
				for (int i = 0; i < songAndMusicInfos.size(); i++) {

					// 找到保存在此歌单中的歌曲
					for (int j = 0; j < mp3Infos.size(); j++) {

						if (songAndMusicInfos.get(i).getMp3InfoId() == mp3Infos
								.get(j).getId()) {
							Mp3Info mp3Info = new Mp3Info();
							mp3Info = mp3Infos.get(j);
							song_mp3Infos.add(mp3Info);
							break;
						}
					}
				}

			} catch (DbException e) {
				// TODO: handle exception
				e.printStackTrace();
			}

			return song_mp3Infos;
		}

		default:
			break;
		}
		return null;
	}

	public int getCurrentPositionLocal() {
		return sp.getInt(Constant.CURRENT_POSITION, 0);
	}

	// xft
	public int getPosition() {
		int position = sp_msg.getInt("position", 0);
		return position;
	}

	public void setPosition(int position) {
		ed_msg.putInt("position", position);
		ed_msg.commit();
	}

	public int getCode() {
		int code = sp_msg.getInt("code", 0);
		return code;
	}

	public void setCode(int code) {
		ed_msg.putInt("code", code);
		ed_msg.commit();
	}

	public String getSongName() {
		if (!sp_end.getString(
				"firstsongname_"
						+ String.valueOf(sp_msg.getInt("position", position)),
				"").equals("")) {
			return sp_end.getString(
					"firstsongname_"
							+ String.valueOf(sp_msg.getInt("position", 0)), "");
		} else {
			String songname = sp_song.getString("songname", "");
			return songname;
		}
	}

	public String getSingerName() {
		Log.i("125",
				"position:" + String.valueOf(sp_msg.getInt("position", -1)));
		// Log.i("125","fisrtsingername："+sp_end.getString("fisrtsingername_2",
		// ""));
		if (!sp_end.getString(
				"firstsingername_"
						+ String.valueOf(sp_msg.getInt("position", position)),
				"").equals("")) {
			Log.i("125", "!null:");
			return sp_end.getString(
					"firstsingername_"
							+ String.valueOf(sp_msg.getInt("position", 0)), "");
		} else {
			Log.i("125", "null:");
			String singername = sp_song.getString("singername", "");
			return singername;
		}
	}
	
	public String getAlbum()
	{
		if(!sp_end.getString("firstalbum_"+String.valueOf(sp_msg.getInt("position",position)),"").equals(""))
		{
			return sp_end.getString("firstalbum_"+String.valueOf(sp_msg.getInt("position",0)),"");
		}
		else
		{
		   
		   return null;
		}
	}

//	public void SetSongMsg(String songname, String singername) {
//		ed_song.putString("songname", songname);
//		ed_song.putString("singername", singername);
//		ed_song.commit();
//	}

	public void setEndMsg(String[] url,String[] songname,String[] singername,String[] firstalbum,String[] url_tag
			,String[] songname_tag,String[] singername_tag,String[] firstalbum_tag)
	{
		   for(int i=0;i<url.length;i++)
		   {
			   ed_end.putString(url_tag[i],url[i]);

	     	   ed_end.commit();
		   }
		   for(int i=0;i<songname.length;i++)
		   {
			   ed_end.putString(songname_tag[i],songname[i]);
			   ed_end.commit();
		   }
		   for(int i=0;i<singername.length;i++)
		   {
			   ed_end.putString(singername_tag[i],singername[i]);
			   ed_end.commit();
		   }
		   for(int i=0;i<firstalbum.length;i++)
		   {
			   ed_end.putString(firstalbum_tag[i],firstalbum[i]);
			   ed_end.commit();
		   }
	}
	
	

}
