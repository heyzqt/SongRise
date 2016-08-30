package com.example.songriseplayer;

import java.util.ArrayList;
import java.util.List;

import com.example.adapter.MyMusicListAdapter;
import com.example.app.SongRisePlayerApp;
import com.example.utils.AppUtils;
import com.example.utils.DownloadUtils;
import com.example.utils.L;
import com.example.utils.MediaUtils;
import com.example.vo.Mp3Info;
import com.lidroid.xutils.db.sqlite.Selector;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 
 * 收藏歌曲界面
 * 
 * @author zq
 * 
 */
public class MyLikeMusicListActivity extends BaseActivity implements
		OnItemClickListener, OnClickListener {

	private SongRisePlayerApp app;
	private ListView listview_like;
	private ArrayList<Mp3Info> likeMp3Infos;
	private MyMusicListAdapter adapter;
	private ImageView imageview_myfavorite_back;
	
	private ImageView imageview1_album;
	private ImageView imageview2_play_pause;
	private ImageView imageview3_next;
	private TextView textview1_title;
	private TextView textview2_singer_name;

	// private boolean isChange = false; // 判断当前播放列表是否为收藏列表

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_like_music_list);
		app = (SongRisePlayerApp) getApplication();
		listview_like = (ListView) findViewById(R.id.listView2);
		imageview_myfavorite_back = (ImageView) findViewById(R.id.imageview_myfavorite_back);
		listview_like.setOnItemClickListener(this);
		imageview_myfavorite_back.setOnClickListener(this);
		
		imageview1_album = (ImageView) findViewById(R.id.imageview1_album);
		imageview2_play_pause = (ImageView) findViewById(R.id.imageview2_play_pause);
		imageview3_next = (ImageView) findViewById(R.id.imageview3_local_next);
		textview1_title = (TextView) findViewById(R.id.textview1_local_title);
		textview2_singer_name = (TextView) findViewById(R.id.textview2_local_singer_name);

		imageview1_album.setOnClickListener(this);
		imageview2_play_pause.setOnClickListener(this);
		imageview3_next.setOnClickListener(this);

		
		initData();

		// 设置标题栏颜色
		AppUtils.setSystemStatusBar(MyLikeMusicListActivity.this);
	}

	/**
	 * 初始化我喜欢音乐列表数据
	 */
	private void initData() {
		try {
			List<Mp3Info> list = app.dbUtils.findAll(Selector.from(
					Mp3Info.class).where("isLike", "=", "1"));
			if (list == null || list.size() == 0) {
				return;
			}

			// app.dbUtils.deleteAll(Mp3Info.class);
			// likeMp3Infos = (ArrayList<Mp3Info>) app.dbUtils
			// .findAll(Mp3Info.class);
			likeMp3Infos = (ArrayList<Mp3Info>) list;
			// L.e("likeMp3Infos===" + likeMp3Infos);
			adapter = new MyMusicListAdapter(this, likeMp3Infos);
			listview_like.setAdapter(adapter);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		bindPlayService();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		unbindPlayService();
	}

	@Override
	public void publish(int progress) {
		// TODO Auto-generated method stub

	}

	@Override
	public void change(int position) {
		// TODO Auto-generated method stub
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
	}
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.arg1 == 1) {
				imageview1_album.setImageBitmap(DownloadUtils.getInstance()
						.GetImgFromSDCard(app.getSongName()));
			}
		};
	};

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		if (playService.getChangePlayList() != PlayService.LIKE_MUSIC_LIST) {

			playService.setChangePlayList(PlayService.LIKE_MUSIC_LIST);
			playService.setMp3Infos(likeMp3Infos);
		}
		playService.play(position);

		// 保存歌曲播放时间
		// savePlayRecord();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.imageview_myfavorite_back: { // 返回主界面
			this.finish();
			break;
		}
		case R.id.imageview1_album: // 跳转到歌曲播放界面

			if (playService.getCurrentPlayMusic() == PlayService.LOCAL_MUSIC) {
				Intent intent = new Intent(this, PlayActivity.class);
				startActivity(intent);
			} else if (playService.getCurrentPlayMusic() == PlayService.NET_MUSIC) {
				Intent intent = new Intent(this,
						PlayInternetMusicActivity.class);
				startActivity(intent);
			}
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
			break;
		}
		case R.id.imageview3_local_next: { // 下一首歌曲
			playService.next();
			break;
		}
		default:
			break;
		}
	}

	// /**
	// * 保存歌曲的播放时间
	// */
	// private void savePlayRecord() {
	// // 获取当前正在播放的音乐对象
	// Mp3Info mp3Info = playService.getMp3Infos().get(
	// playService.getCurrentPosition());
	//
	// //L.e("mp3Info==="+mp3Info);
	// //L.e(""+playService.getMp3Infos());
	// try {
	// //L.e("try");
	// Mp3Info playRecordMp3Info = app.dbUtils.findFirst(Selector.from(
	// Mp3Info.class).where("mp3InfoId", "=",
	// mp3Info.getMp3InfoId()));
	// //L.e("playRecordMp3Info==="+playRecordMp3Info);
	// if (playRecordMp3Info == null) {
	//
	// //L.e("save前");
	// mp3Info.setPlayTime(System.currentTimeMillis());
	// //L.e("playRecordMp3Info==="+playRecordMp3Info);
	// app.dbUtils.save(mp3Info);
	//
	//
	// //L.e("save");
	// //L.e("System.currentTimeMillis()==="+System.currentTimeMillis());
	// } else {
	//
	// //L.e("update前");
	// playRecordMp3Info.setPlayTime(System.currentTimeMillis());
	// //L.e("playRecordMp3Info==="+playRecordMp3Info);
	//
	// //表中其他记录并没有playTime这项
	// app.dbUtils.update(playRecordMp3Info, "playTime");
	//
	// //L.e("update");
	//
	// //L.e("System.currentTimeMillis()==="+System.currentTimeMillis());
	// }
	// //L.e("try end");
	// } catch (Exception e) {
	// // TODO: handle exception
	// e.printStackTrace();
	// }
	// }

}
