package com.example.songriseplayer;

import java.util.ArrayList;

import com.example.adapter.MyMusicListAdapter;
import com.example.app.Constant;
import com.example.app.SongRisePlayerApp;
import com.example.utils.AppUtils;
import com.example.utils.DownloadUtils;
import com.example.utils.L;
import com.example.utils.MediaUtils;
import com.example.utils.T;
import com.example.vo.Mp3Info;
import com.lidroid.xutils.db.sqlite.Selector;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 本地歌曲列表
 * 
 * @author zq
 * 
 */
public class LocalMusicActivity extends BaseActivity implements
		OnItemClickListener, OnClickListener {

	private ListView listview_my_music;
	private ArrayList<Mp3Info> mp3Infos;
	private MyMusicListAdapter mymusiclistadapter;
	private ImageView imageview_back;
	private ImageView imageview1_album;
	private ImageView imageview2_play_pause;
	private ImageView imageview3_next;
	private TextView textview1_title;
	private TextView textview2_singer_name;
	private SongRisePlayerApp app;
	private String musicname; // 保存当前播放的歌曲名

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_local_music);
		listview_my_music = (ListView) findViewById(R.id.listview_local_music);
		imageview_back = (ImageView) findViewById(R.id.imageview_local_back);
		imageview1_album = (ImageView) findViewById(R.id.imageview1_album);
		imageview2_play_pause = (ImageView) findViewById(R.id.imageview2_play_pause);
		imageview3_next = (ImageView) findViewById(R.id.imageview3_local_next);
		textview1_title = (TextView) findViewById(R.id.textview1_local_title);
		textview2_singer_name = (TextView) findViewById(R.id.textview2_local_singer_name);

		listview_my_music.setOnItemClickListener(this);
		imageview_back.setOnClickListener(this);
		imageview1_album.setOnClickListener(this);
		imageview2_play_pause.setOnClickListener(this);
		imageview3_next.setOnClickListener(this);

		app = (SongRisePlayerApp) getApplication();

		// 设置标题栏颜色
		AppUtils.setSystemStatusBar(LocalMusicActivity.this);

	}
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.arg1 == 1) {
				imageview1_album.setImageBitmap(DownloadUtils.getInstance()
						.GetImgFromSDCard(app.getSongName()));
			}
		};
	};

	// Activity创建或者从被覆盖、后台重新回到前台时被调用
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// 绑定服务
		bindPlayService();
		loadData();
		// System.out.println("localMusic====="+playService);
	}

	// Activity被覆盖到下面或者锁屏时被调用
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		unbindPlayService();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	/*
	 * 加载本地列表
	 */
	public void loadData() {

		mp3Infos = MediaUtils.getMp3Infos(this);
		// mp3Infos=this.playService.mp3Infos;
		mymusiclistadapter = new MyMusicListAdapter(this, mp3Infos);
		listview_my_music.setAdapter(mymusiclistadapter);

	}

	// // 回调播放状态下的UI设置
	// public void changeUIStatusOnPlay(int position) {
	//
	// // 填充我的音乐列表和播放的音乐是两个不同的mp3Infos
	// if (position >= 0 && position < playService.mp3Infos.size()) {
	//
	// try {
	// Mp3Info mp3Info = playService.mp3Infos.get(position);
	// textview1_title.setText(mp3Info.getTitle());
	// textview2_singer_name.setText(mp3Info.getArtist());
	// Bitmap albumBitmap = MediaUtils.getArtWork(this,
	// mp3Info.getId(), mp3Info.getAlbumId(), true, true);
	// imageview1_album.setImageBitmap(albumBitmap);
	// } catch (Exception e) {
	// // TODO: handle exception
	// e.printStackTrace();
	// }
	//
	// if (playService.isPlaying()) {
	// imageview2_play_pause
	// .setImageResource(R.drawable.img_appwidget_pause);
	// } else {
	// imageview2_play_pause
	// .setImageResource(R.drawable.img_appwidget_play);
	// }
	//
	// // this.position=position;
	// }
	// }

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		if (playService.getChangePlayList() != PlayService.MY_MUSIC_LIST) {

			playService.setChangePlayList(PlayService.MY_MUSIC_LIST);
			playService.setMp3Infos(mp3Infos);

		}
		// 下载歌曲添加记录，重新设置mp3Infos的值
		playService.setMp3Infos(mp3Infos);
		playService.setCurrentPlayMusic(playService.LOCAL_MUSIC);
		playService.playMusic(position);
		// playService.play(position);

	}

	@Override
	public void publish(int progress) {
		// TODO Auto-generated method stub

	}

	@Override
	public void change(int position) {
		// TODO Auto-generated method stub

		// changeUIStatusOnPlay(position);

		if (playService.getCurrentPlayMusic() == PlayService.LOCAL_MUSIC) {

			// 填充我的音乐列表和播放的音乐是两个不同的mp3Infos
			if (position >= 0 && position < playService.mp3Infos.size()) {

				try {
					Mp3Info mp3Info = playService.mp3Infos.get(position);
					textview1_title.setText(mp3Info.getTitle());
					textview2_singer_name.setText(mp3Info.getArtist());
					Bitmap albumBitmap = MediaUtils.getArtWork(this,
							mp3Info.getId(), mp3Info.getAlbumId(), true, true);
					imageview1_album.setImageBitmap(albumBitmap);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}

				if (playService.isPlaying()) {
					imageview2_play_pause
							.setImageResource(R.drawable.img_appwidget_pause);
				} else {
					imageview2_play_pause
							.setImageResource(R.drawable.img_appwidget_play);
				}

			}

		} else if (playService.getCurrentPlayMusic() == PlayService.NET_MUSIC) {

			// code 1 播放 -1 暂停
			if (SongRisePlayerApp.sp_msg.getInt("code", -1) == 1) {
				imageview2_play_pause
						.setImageResource(R.drawable.img_appwidget_pause);
			} else {
				imageview2_play_pause
						.setImageResource(R.drawable.img_appwidget_play);
			}

			String[] str = playService.getPlayingSongMsg();
			textview1_title.setText(str[0]);
			textview2_singer_name.setText(str[1]);

			// 设置专辑图片
			// 从文件中找专辑图片,若无设置为默认图片
			//imageview1_album.setImageResource(R.drawable.music_icon);
			DownloadUtils.getInstance().ShowImg(app.getAlbum(), imageview1_album,
					handler, app.getSongName());

		}

		// Mp3Info mp3Info = playService.mp3Infos.get(position);
		// musicname = mp3Info.getTitle();
		// // System.out.println(playService.mp3Infos);
		// textview1_title.setText(mp3Info.getTitle());
		// textview2_singer_name.setText(mp3Info.getArtist());
		// if (playService.isPlaying()) {
		// imageview2_play_pause
		// .setImageResource(R.drawable.img_appwidget_pause);
		// } else {
		// imageview2_play_pause
		// .setImageResource(R.drawable.img_appwidget_play);
		// }
		// Bitmap albumBitmap = MediaUtils.getArtWork(this, mp3Info.getId(),
		// mp3Info.getAlbumId(), true, true);
		// imageview1_album.setImageBitmap(albumBitmap);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.imageview_local_back: // 返回主界面
			finish();
			break;
		case R.id.imageview1_album: // 跳转到歌曲播放界面

			if (playService.getCurrentPlayMusic() == PlayService.LOCAL_MUSIC) {
				Intent intent = new Intent(this, PlayActivity.class);
				startActivity(intent);
			} else if (playService.getCurrentPlayMusic() == PlayService.NET_MUSIC) {
				Intent intent = new Intent(this,
						PlayInternetMusicActivity.class);
				startActivity(intent);
			}

			// // 当歌曲不存在时,不能跳转
			// boolean check = false;
			// for (int i = 0; i < playService.mp3Infos.size(); i++) {
			// if (musicname.equals(playService.mp3Infos.get(i).getTitle())) {
			// check = true;
			// break;
			// }
			// }
			//
			// if (check) {
			// Intent intent = new Intent(this, PlayActivity.class);
			// startActivity(intent);
			// } else {
			// T.showShort(this, "歌曲不存在");
			// }
			break;
		case R.id.imageview2_play_pause: { // 播放暂停

			if (playService.getCurrentPlayMusic() == PlayService.LOCAL_MUSIC) {
				if (playService.isPlaying()) {
					imageview2_play_pause
							.setImageResource(R.drawable.img_appwidget_play);
					playService.pause();
				} else {
					imageview2_play_pause
							.setImageResource(R.drawable.img_appwidget_pause);
					if (playService.isPause()) {
						playService.start();
					} else {
						playService
								.setCurrentPlayMusic(PlayService.LOCAL_MUSIC);
						playService.playMusic(playService.getCurrentPosition());
					}
				}
			} else if (playService.getCurrentPlayMusic() == PlayService.NET_MUSIC) {

				if ((app.getCode() == 1 && playService.isPlaying())) {
					imageview2_play_pause
							.setImageResource(R.drawable.img_appwidget_pause1);
					playService.pause();
				} else {
					imageview2_play_pause
							.setImageResource(R.drawable.img_appwidget_play1);
					if (playService.isPause()) {
						playService.startMusic();
					} else {

						playService.setCurrentPlayMusic(PlayService.NET_MUSIC);
						playService.playMusic(-1);
					}
				}
			}
			// if (playService.isPlaying()) {
			// playService.pause();
			// imageview2_play_pause
			// .setImageResource(R.drawable.img_appwidget_play);
			// } else {
			// if (playService.isPause()) {
			// playService.start();
			// } else {
			// // playService.play(playService.getCurrentPosition());
			// playService.playMusic(playService.getCurrentPosition());
			// }
			// imageview2_play_pause
			// .setImageResource(R.drawable.img_appwidget_pause);
			// }
			break;
		}
		case R.id.imageview3_local_next: { // 下一首歌曲
			playService.next();
			// if (playService.getCurrentPlayMusic() == PlayService.LOCAL_MUSIC)
			// {
			// playService.next();
			// } else if (playService.getCurrentPlayMusic() ==
			// PlayService.NET_MUSIC) {
			//
			// int position_local = app.getPosition() + 1;
			// app.setPosition(position_local);
			// playService.playMusic(Constant.NULL_NET_MUSIC);
			//
			// }
			break;
		}
		default:
			break;
		}
	}
}
