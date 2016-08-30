package com.example.songriseplayer;

import java.util.Calendar;
import java.util.Random;
import java.util.TimeZone;

import com.example.app.Constant;
import com.example.app.SongRisePlayerApp;
import com.example.utils.AppUtils;
import com.example.utils.L;
import com.example.utils.MediaUtils;
import com.example.vo.Mp3Info;
import com.nostra13.universalimageloader.core.assist.LoadedFrom;

import android.app.Activity;
import android.gesture.GestureOverlayView.OnGestureListener;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class suoping_ac extends BaseActivity implements OnClickListener,
		OnCompletionListener {
	private GestureDetector ges;
	private ImageView image_sp;
	private ImageView image_next;
	private ImageView image_previous;
	private TextView tv_songname;
	private TextView tv_singername;
	private TextView tv_time;
	private TextView tv_date;
	private SongRisePlayerApp app;
	private Mp3Info mp3Info;
	private MediaPlayer lock_media;

	// private PlayService playService=new PlayService();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.lockscreen);
		this.getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
						| WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
		ges = new GestureDetector(suoping_ac.this, onGestureListener);
		bindPlayService();
		initView();
		
		// 设置标题栏颜色
		AppUtils.setSystemStatusBar(suoping_ac.this);
	}

	private void initView() {
		app = (SongRisePlayerApp) getApplication();
		image_sp = (ImageView) findViewById(R.id.image_sp);
		image_next = (ImageView) findViewById(R.id.image_next);
		image_previous = (ImageView) findViewById(R.id.image_pre);
		tv_songname = (TextView) findViewById(R.id.tv_songname);
		tv_singername = (TextView) findViewById(R.id.tv_singername);
		tv_time = (TextView) findViewById(R.id.tv_time);
		tv_date = (TextView) findViewById(R.id.tv_date);
		image_sp.setOnClickListener(this);
		image_previous.setOnClickListener(this);
		image_next.setOnClickListener(this);
		setFirstView();
		// playService.initTextView(tv_songname, tv_singername);
		// lock_media=playService.getMedia();
		// Log.i("128","media:"+String.valueOf(lock_media));
		// lock_media.setOnCompletionListener(this);
	}

	public String getDate() {
		String mMonth;
		String mDay;
		String mWay;
		final Calendar c = Calendar.getInstance();
		c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
		mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份
		mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码

		mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
		if ("1".equals(mWay)) {
			mWay = "天";
		} else if ("2".equals(mWay)) {
			mWay = "一";
		} else if ("3".equals(mWay)) {
			mWay = "二";
		} else if ("4".equals(mWay)) {
			mWay = "三";
		} else if ("5".equals(mWay)) {
			mWay = "四";
		} else if ("6".equals(mWay)) {
			mWay = "五";
		} else if ("7".equals(mWay)) {
			mWay = "六";
		}
		return mMonth + "月" + mDay + "日" + "   星期" + mWay;
	}

	public String getTime() {
		final Calendar c = Calendar.getInstance();
		String hour;
		String minute;
		hour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
		minute = String.valueOf(c.get(Calendar.MINUTE));
		return hour + ":" + minute;
	}

	public void setFirstView() {
		Log.i("124", "rank_setFisrtView:" + String.valueOf(app.getCode()));
		if (app.sp.getInt(Constant.CURRENT_PLAY_MUSIC, 0) == 0) {
			Log.i("125", "rank_detail LocalMusic");
			if (app.getCode() == -1) {
				image_sp.setImageResource(R.drawable.img_play);
			} else if (app.getCode() == 1) {
				image_sp.setImageResource(R.drawable.img_pause);
			}
			mp3Info = app.getCurrentLocalMp3Infos().get(
					app.getCurrentPositionLocal());
			// 歌曲名
			tv_songname.setText(mp3Info.getTitle());
			// 歌手名
			tv_singername.setText(mp3Info.getArtist());
			// 专辑处理
			// Bitmap albumBitmap = MediaUtils.getArtWork(this,
			// mp3Info.getId(), mp3Info.getAlbumId(), true, true);
			// img_album.setImageBitmap(albumBitmap);
		}

		else if (app.sp.getInt(Constant.CURRENT_PLAY_MUSIC, 0) == 1) {
			Log.i("125", "rank_detail netMusic");
			if (app.getCode() == -1) {
				image_sp.setImageResource(R.drawable.img_play);
			} else if (app.getCode() == 1) {
				image_sp.setImageResource(R.drawable.img_pause);
			}
			tv_songname.setText(app.getSongName());
			tv_singername.setText(app.getSingerName());
		}
	}

	private GestureDetector.OnGestureListener onGestureListener = new GestureDetector.SimpleOnGestureListener() {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			float x = e2.getX() - e1.getX();
			// float y = e2.getY() - e1.getY();

			if (x > 0) {
				doResult(1);
			} else if (x < 0) {
				doResult(0);
			}
			return true;
		}
	};

	public boolean onTouchEvent(MotionEvent event) {

		return ges.onTouchEvent(event);

	};

	public void doResult(int action) {

		switch (action) {
		case 1:
			finish();
			break;

		case 0:
			System.out.println("go left");
			break;

		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		// super.onBackPressed();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 设置播放/暂停图标以及实现播放与暂停功能
		case R.id.image_sp:
			// Log.i("126","pinter:"+String.valueOf(app.sp.getInt(Constant.CURRENT_PLAY_MUSIC,0)));
			// Log.i("128","code:"+String.valueOf(app.getCode()));

			if (app.sp.getInt(Constant.CURRENT_PLAY_MUSIC, 0) == playService.NET_MUSIC) {
				if (app.getCode() == 1 && playService.isPlaying()) {
					playService.pause();
					image_sp.setImageResource(R.drawable.img_play);
				} else {
					image_sp.setImageResource(R.drawable.img_pause);
					if (app.getCode() == -1 && playService.isPause()) {
						playService.startMusic();
					} else {
						playService.playMusic(-1);
					}
				}
			}

			else {
				if ((app.getCode() == 1 && playService.isPlaying())) {
					image_sp.setImageResource(R.drawable.img_play);
					playService.pause();
					// isPause = true;
				} else {
					image_sp.setImageResource(R.drawable.img_pause);
					if (playService.isPause()) {
						playService.start();
					} else {
						// mainActivity.playService.play(mainActivity.playService
						// .getCurrentPosition());
						playService
								.setCurrentPlayMusic(PlayService.LOCAL_MUSIC);
						playService.playMusic(playService.getCurrentPosition());
					}
				}
			}
			break;
		// 上一曲
		case R.id.image_pre:

			if (app.sp.getInt(Constant.CURRENT_PLAY_MUSIC, 0) == playService.LOCAL_MUSIC) {
				image_sp.setImageResource(R.drawable.img_pause);
				playService.prev();
			} else if (app.sp.getInt(Constant.CURRENT_PLAY_MUSIC, 0) == playService.NET_MUSIC) {
				int c_positton1 = app.getPosition();
				app.setPosition(c_positton1 - 1);
				playService.setCurrentPlayMusic(1);
				playService.playMusic(-1);
				image_sp.setImageResource(R.drawable.img_pause);
			}
			setFirstView();
			break;

		// 下一曲
		case R.id.image_next:
			if (app.sp.getInt(Constant.CURRENT_PLAY_MUSIC, 0) == playService.LOCAL_MUSIC) {
				playService.next();
			} else if (app.sp.getInt(Constant.CURRENT_PLAY_MUSIC, 0) == playService.NET_MUSIC) {
				playService.next();
			}
			setFirstView();
			break;
		default:
			break;
		}

	}

	@Override
	public void publish(int progress) {
		// TODO Auto-generated method stub

	}

	@Override
	public void change(int position) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onResume() {
		super.onResume();
		try {
			lock_media = playService.getMedia();
		} catch (Exception e) {
			// TODO: handle exception
			L.e("mediaplayer=====null");
		}
		if (lock_media != null) {
			lock_media.setOnCompletionListener(this);
		}
		Log.i("128", "media:" + String.valueOf(lock_media));
		tv_time.setText(getTime());
		tv_date.setText(getDate());
	}

	// 同步图标
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		setFirstView();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unbindPlayService();
	}

	private Random random = new Random();

	@Override
	public void onCompletion(MediaPlayer mp) {
		// 网络歌曲的情况下
		if (app.sp.getInt(Constant.CURRENT_PLAY_MUSIC, 0) == 1) {
			int position = app.getPosition();
			app.setPosition(position + 1);
			try {
				tv_singername.setText(app.getSingerName());
				tv_songname.setText(app.getSongName());
			} catch (Exception e) {
				Log.i("124", "!!");
			}
			playService.playMusic(-1);
		}
		// 本地歌曲
		else if (app.sp.getInt(Constant.CURRENT_PLAY_MUSIC, 0) == 0) {

			switch (playService.getPlay_Mode()) {
			case PlayService.ORDER_PLAY:
				playService.next();
				break;
			case PlayService.RANDOM_PLAY:
				playService.play(random.nextInt(playService.mp3Infos.size()));
				break;
			case PlayService.SINGLE_PLAY:
				playService.play(playService.getCurrentPosition());
				break;
			default:
				break;
			}
			setFirstView();
		}
	}
}
