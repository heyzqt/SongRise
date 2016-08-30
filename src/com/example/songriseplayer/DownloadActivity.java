package com.example.songriseplayer;

import java.io.File;
import java.util.ArrayList;

import com.example.adapter.MyMusicListAdapter;
import com.example.app.SongRisePlayerApp;
import com.example.utils.AppUtils;
import com.example.utils.DownloadUtils;
import com.example.utils.L;
import com.example.utils.MediaUtils;
import com.example.utils.T;
import com.example.vo.Mp3Info;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class DownloadActivity extends BaseActivity implements
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
	private String[] filenames;  //歌曲名集合

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_music_download);
		listview_my_music = (ListView) findViewById(R.id.listview_download_music);
		imageview_back = (ImageView) findViewById(R.id.imageview_download_back);
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
		AppUtils.setSystemStatusBar(DownloadActivity.this);

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// 绑定服务
		bindPlayService();
		loadData();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		unbindPlayService();
	}

	/*
	 * 加载本地列表
	 */
	public void loadData() {

		//获取SongRise/Music中的音乐文件名集合
		filenames=DownloadUtils.getInstance().FileNameScanner();
		if(filenames==null){
			return;
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
		
		mymusiclistadapter = new MyMusicListAdapter(this, mp3Infos);
		listview_my_music.setAdapter(mymusiclistadapter);

	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		if (playService.getChangePlayList() != PlayService.DOWNLOAD_MUSIC_LIST) {

			playService.setChangePlayList(PlayService.DOWNLOAD_MUSIC_LIST);
			playService.setMp3Infos(mp3Infos);

		}
		// 下载歌曲添加记录，重新设置mp3Infos的值
		playService.setMp3Infos(mp3Infos);
		playService.setCurrentPlayMusic(playService.LOCAL_MUSIC);
		playService.playMusic(position);

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
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.imageview_download_back: // 返回主界面
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

}
