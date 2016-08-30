package com.example.songriseplayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.astuetz.viewpager.extensions.sample.BaseTools;
import com.example.adapter.MyMusicListAdapter;
import com.example.app.Constant;
import com.example.app.SongRisePlayerApp;
import com.example.songriseplayer.helper.HeadSetHelper;
import com.example.songriseplayer.helper.HeadSetHelper.OnHeadSetListener;
import com.example.utils.AppUtils;
import com.example.utils.DownloadUtils;
import com.example.utils.L;
import com.example.utils.MediaUtils;
import com.example.utils.T;
import com.example.vo.Mp3Info;
import com.example.vo.SongListInfo;
import com.lidroid.xutils.db.sqlite.Selector;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 音乐播放的服务类 实现功能： 1.播放 2.暂停 3.上一首 4.下一首 5.获取当前播放进度
 * 
 * @author zq,xft,zyq
 * 
 */
public class PlayService extends Service implements OnCompletionListener,
		OnErrorListener, OnBufferingUpdateListener {

	public ArrayList<Mp3Info> mp3Infos;
	private MediaPlayer mediaPlayer;
	private int currentPosition; // 当前播放的位置

	private MusicUpdateListener musicUpdateListener;
	private ExecutorService es = Executors.newSingleThreadExecutor(); // 创建线程池

	private boolean isPause = false;

	// 播放模式标记
	public static final int ORDER_PLAY = 1; // 顺序播放
	public static final int RANDOM_PLAY = 2; // 随机播放
	public static final int SINGLE_PLAY = 3; // 单曲循环
	private int play_mode = ORDER_PLAY;

	// 切换播放列表
	public static final int MY_MUSIC_LIST = 1; // 我的音乐
	public static final int LIKE_MUSIC_LIST = 2; // 我的收藏
	public static final int PLAY_RECORD_MUSIC_LIST = 3; // 最近播放
	public static final int DOWNLOAD_MUSIC_LIST = 4; // 下载的歌曲
	public static final int CURRENT_SONGLIST_PLAY = 5; // 自定义歌单歌曲
	private int changePlayList = MY_MUSIC_LIST;

	// 歌单数据
	public List<List<Map<String, String>>> songlist;

	private SongRisePlayerApp app;

	private PlayActivity playActivity;

	public static final int LOCAL_MUSIC = 0; // 本地音乐
	public static final int NET_MUSIC = 1; // 网络音乐
	private int currentPlayMusic; // 判断当前播放的是本地还是网络歌曲
	public boolean isMainActivity = false;

	private boolean startedApp = true; // 判断是否启动了app
	private String songlistname = ""; // 保存自定义歌单名

	// xft
	// shar与editor是对歌曲链接的操作
	// private SharedPreferences shar;
	// private Editor editor;
	// shar_msg与editor_msg是当前播放位置与播放状态的操作
	private SharedPreferences shar_msg;
	private Editor editor_msg;

	private String[] recieveurl;
	// 歌曲播放时的播放位置
	private int percent = 0;
	private int play_code;
	// 当前歌单的歌曲全部链接
	private String[] firsturl = null;
	private String[] firstsongname;
	private String[] firstsingername;
	// private String []c_songnametag;
	// private String []c_singernameTag;
	private String[] re_songname;
	private String[] re_singername;
	private TextView tv_songname;
	private TextView tv_singername;
	private String[] re_album;
	private String[] firstalbum;

	// zyqx
	private final static String TAG = "SyncService";
	int ifno;
	NotificationCompat.Builder mBuilder;
	public NotificationManager mNotificationManager;
	public ButtonBroadcastReceiver bReceiver;
	public final static String ACTION_BUTTON = "com.notifications.intent.action.ButtonClick";
	private HeadsetPlugReceiver headsetPlugReceiver;
	int if_change;// 是否改变进度条zyqx_2016-4-25
	int if_in;// 是否插入耳机zyqx_2016-4-25
	int is_answer = 0; // 判断是否接听电话
	//NotificationCompat.Builder mBuilder = new Builder(this);
	RemoteViews mRemoteViews ;

	// zyqx

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return new PlayBinder();
	}

	public PlayService() {
	}

	/*
	 * 自定义类继承Binder,供onBind()方法使用
	 */
	class PlayBinder extends Binder {
		public PlayService getPlayService() {
			return PlayService.this;
		}
	}

	// 保存自定义的歌单名
	public String getSonglistname() {
		return songlistname;
	}

	public void setSonglistname(String songlistname) {
		this.songlistname = songlistname;
		SharedPreferences.Editor editor = app.sp.edit();
		editor.putString("songlistname", songlistname);
		editor.commit();
	}

	public MediaPlayer getMediaPlayer() {
		return mediaPlayer;
	}

	public void setMediaPlayer(MediaPlayer mediaPlayer) {
		this.mediaPlayer = mediaPlayer;
	}

	public int getCurrentPlayMusic() {
		return currentPlayMusic;
	}

	public void setCurrentPlayMusic(int currentPlayMusic) {
		this.currentPlayMusic = currentPlayMusic;
		// 判断当前播放歌曲网络或本地
		SharedPreferences.Editor editor = app.sp.edit();
		editor.putInt(Constant.CURRENT_PLAY_MUSIC, currentPlayMusic);
		editor.commit();
	}

	public int getChangePlayList() {
		return changePlayList;
	}

	public void setChangePlayList(int changePlayList) {
		this.changePlayList = changePlayList;

		// 设置当前播放歌曲是哪个列表
		SharedPreferences.Editor editor = app.sp.edit();
		editor.putInt(Constant.CURRENT_MUSIC_MODE, changePlayList);
		editor.commit();
	}

	public void setPlay_Mode(int play_mode) {
		this.play_mode = play_mode;

		// 设置当前播放歌曲的模式
		SharedPreferences.Editor editor = app.sp.edit();
		editor.putInt(Constant.PLAY_MODE, play_mode);
		editor.commit();
	}

	public int getPlay_Mode() {
		return play_mode;
	}

	public void setMp3Infos(ArrayList<Mp3Info> mp3Infos) {
		this.mp3Infos = mp3Infos;
	}

	public ArrayList<Mp3Info> getMp3Infos() {
		return mp3Infos;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		// L.e("playservice oncreate");
		app = (SongRisePlayerApp) getApplication();
		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		registerReceiver(myre, filter);
		// 判断当前播放歌曲网络或本地
		currentPlayMusic = app.sp.getInt(Constant.CURRENT_PLAY_MUSIC,
				LOCAL_MUSIC);
		currentPosition = app.sp.getInt(Constant.CURRENT_POSITION, 0);
		// 当前歌曲播放模式
		play_mode = app.sp.getInt(Constant.PLAY_MODE, ORDER_PLAY);
		// 当前歌曲播放列表
		changePlayList = app.sp.getInt(Constant.CURRENT_MUSIC_MODE,
				MY_MUSIC_LIST);

		// 将这几行代码放在try catch之前执行
		// 因为当list空时 return直接跳出onCreate方法这几行代码放在之后将不会执行
		// mp3Infos = MediaUtils.getMp3Infos(this);
		// 当启动SongRise时判断歌曲列表
		if (startedApp == true) {
			mp3Infos = app.getCurrentLocalMp3Infos();
			startedApp = false;
			// L.e("首次启动程序");
			// L.e(""+mp3Infos);
		} else {
			try {
				mp3Infos = MediaUtils.getMp3Infos(this);
			} catch (Exception e) {
				// TODO: handle exception
				mp3Infos=null;
			}
		}

		// L.e("mp3Infos's length===" + mp3Infos.size());
		// L.e("本地歌曲mp3Infos:");
		// for (int i = 0; i < mp3Infos.size(); i++) {
		// L.e("" + mp3Infos.get(i));
		// }

		mediaPlayer = new MediaPlayer();
		// 一定要注册监听事件！！！！
		mediaPlayer.setOnCompletionListener(this);
		mediaPlayer.setOnErrorListener(this);
		es.execute(updateStatusRunnable);

		initView();

		playActivity = new PlayActivity();

		// zyqx
		ifno = 1;
		 mBuilder = new Builder(this);
		 mRemoteViews = new RemoteViews(getPackageName(),
					R.layout.view_custom_button);
		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		initButtonReceiver();
		showButtonNotify();
		/* register receiver */
		registerHeadsetPlugReceiver();
		HeadSetHelper.getInstance().setOnHeadSetListener(headSetListener);
		HeadSetHelper.getInstance().open(this);

		TelephonyManager telephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		telephony.listen(new OnePhoneStateListener(),
				PhoneStateListener.LISTEN_CALL_STATE);
		
		// zyqx

		// 方法一
		// //从sharepreference获取歌单数据
		// String values=app.sp.getString(Constant.SONGLIST, null);
		//
		// //将字符串转换为list
		// if(values==null||values.equals("")){
		// songlist=new ArrayList<List<Map<String,String>>>();
		// }
		// else{
		// songlist=AppUtils.getSharedPreference(values,"songlist_name");
		// }

		// 方法二
		// 从数据库获取歌单数据信息
		try {

			List<SongListInfo> list = app.dbUtils.findAll(SongListInfo.class);
			if (list == null || list.size() == 0) {
				// L.e("show list==1==" + list);

				/*
				 * 重要!!!!
				 */
				// 就算songlist为空也要初始化 否则会报空指针错误
				songlist = new ArrayList<List<Map<String, String>>>();
				return;
			}

			ArrayList<SongListInfo> songlistInfos = (ArrayList<SongListInfo>) list;
			songlist = new ArrayList<List<Map<String, String>>>();
			for (int i = 0; i < songlistInfos.size(); i++) {
				List<Map<String, String>> list_child = new ArrayList<Map<String, String>>();
				Map<String, String> list_map = new HashMap<String, String>();
				list_map.put(Constant.SONGMAP_KEY, songlistInfos.get(i)
						.getTitle());
				list_child.add(list_map);
				songlist.add(list_child);
			}

		} catch (Exception e) {
			// TODO: handle exception
			L.e("PlayService songlist throw a bug");
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (es != null && !es.isShutdown()) {
			es.shutdown();
			es = null;
		}

		// zyqx
		if (bReceiver != null) {
			unregisterReceiver(bReceiver);
		}
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notificationManager.cancel(200);
		unregisterReceiver(headsetPlugReceiver);
		HeadSetHelper.getInstance().close(this);

	}

	// 创建Runnable线程，实时更新歌曲的进度条
	Runnable updateStatusRunnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (true) {
				if (musicUpdateListener != null && mediaPlayer != null
						&& mediaPlayer.isPlaying()) {
					musicUpdateListener.onPublish(getCurrentProgress());
					// Log.i(MainActivity.TAG,"updateStatusRunnable线程===="+updateStatusRunnable);
				}
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO: handle exception
				}
			}
		}
	};

	public int getCurrentPosition() {
		return currentPosition;
	}

	// 播放 一首歌从头开始播放
	public void play(int position) {

		Mp3Info mp3Info = null;
		if (position < 0 || position >= mp3Infos.size()) {
			position = 0;
		}
		mp3Info = mp3Infos.get(position);
		try {
			mediaPlayer.reset();
			mediaPlayer.setDataSource(this, Uri.parse(mp3Info.getUrl()));
			mediaPlayer.prepare();
			mediaPlayer.start();
			currentPosition = position;
			isPause = false;
			// 保存用户数据
			SharedPreferences.Editor editor = app.sp.edit();
			editor.putInt(Constant.CURRENT_POSITION, currentPosition);
			editor.commit();
			app.setCode(1);

			setCurrentPlayMusic(PlayService.LOCAL_MUSIC);

		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (musicUpdateListener != null) {
			musicUpdateListener.onChange(currentPosition);
		}

		// 当歌单不为最近播放列表时,保存歌曲最近播放时间
		if (changePlayList != PLAY_RECORD_MUSIC_LIST) {
			// L.e("记录最近播放时间");
			savePlayRecord();
		}

		// /zyqx
		if (ifno == 1 && if_change == 0) {
			showButtonNotify();
		}

	}

	// 暂停
	public void pause() {
		if (mediaPlayer.isPlaying()) {
			mediaPlayer.pause();
			isPause = true;
			app.setCode(-1);

			if (isMainActivity == true) {
				// L.e("isMainActivity===true---pause");
				if (musicUpdateListener != null) {
					musicUpdateListener.onChange(currentPosition);
				}
			}
		}

		// /zyqx
		if (ifno == 1 && if_change == 0) {
			showButtonNotify();
		}

	}

	// 上一首
	public void prev() {
		if (currentPosition == 0) {
			currentPosition = mp3Infos.size() - 1;
		} else {
			currentPosition--;
		}
		play(currentPosition);
	}

	// 上一首 本地和网络公用
	public void prevMusic() {
		if (currentPlayMusic == NET_MUSIC) {

			int c_positton1 = app.getPosition();
			app.setPosition(c_positton1 - 1);
			playMusic(-1);

		} else {
			if (play_mode == RANDOM_PLAY) {
				play(random.nextInt(mp3Infos.size()));
			} else {
				if (currentPosition == 0) {
					currentPosition = mp3Infos.size() - 1;
				} else {
					currentPosition--;
				}
				play(currentPosition);
			}
		}
	}

	// 下一首 本地和网络公用
	public void next() {

		if (currentPlayMusic == NET_MUSIC) {

			int position_local = app.getPosition() + 1;
			app.setPosition(position_local);
			playMusic(-1);
			app.setCode(1);

		} else {
			if (play_mode == RANDOM_PLAY) {
				play(random.nextInt(mp3Infos.size()));
			} else {
				if (currentPosition == mp3Infos.size() - 1) {
					currentPosition = 0;
				} else {
					currentPosition++;
				}
				// else if (currentPosition == 0 && isPlaying() == false) {
				// currentPosition = 0;
				// }
				play(currentPosition);
			}
		}

	}

	// 播放 一首歌从暂停开始播放
	public void start() {

		if (!mediaPlayer.isPlaying() && mediaPlayer != null) {
			mediaPlayer.start();
			app.setCode(1);
			isPause = false;

			if (isMainActivity == true) {
				// L.e("isMainActivity===true---start");
				if (musicUpdateListener != null) {
					musicUpdateListener.onChange(currentPosition);
				}
			}

		}

		// zyqx
		if (ifno == 1 && if_change == 0) {
			showButtonNotify();
		}

	}

	/**
	 * 保存歌曲最近播放时间
	 */
	private void savePlayRecord() {
		// 获取当前正在播放的音乐对象
		Mp3Info mp3Info = mp3Infos.get(currentPosition);

		try {

			Mp3Info playRecordMp3Info = app.dbUtils.findFirst(Selector.from(
					Mp3Info.class).where("mp3InfoId", "=", getId(mp3Info)));
			// L.e(""+playRecordMp3Info);

			if (playRecordMp3Info == null) {

				mp3Info.setPlayTime(System.currentTimeMillis());
				mp3Info.setMp3InfoId(getId(mp3Info));
				app.dbUtils.save(mp3Info);
				// L.e("save");

			} else {

				playRecordMp3Info.setPlayTime(System.currentTimeMillis());
				app.dbUtils.update(playRecordMp3Info, "playTime");
				// L.e("update");

			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	/**
	 * 重要方法，区别当前的所取的id应该是mp3Info.getId(); 还是直接就是mp3Info.getMp3InfoId();
	 * 
	 * 在不同的播放列表状态下,获取的id值不同
	 * 
	 * @param mp3Info
	 * @return
	 */
	private long getId(Mp3Info mp3Info) {

		// 初始收藏状态
		long id = 0;

		switch (changePlayList) {
		case PlayService.MY_MUSIC_LIST:
			id = mp3Info.getId();
			break;
		case PlayService.LIKE_MUSIC_LIST:
			id = mp3Info.getMp3InfoId();
			break;
		case PlayService.PLAY_RECORD_MUSIC_LIST:
			id = mp3Info.getMp3InfoId();
			break;
		case PlayService.CURRENT_SONGLIST_PLAY:
			id = mp3Info.getId();
			break;
		default:
			break;
		}
		return id;

	}

	// 获取当前播放进度条的位置
	public int getCurrentProgress() {
		if (mediaPlayer != null && mediaPlayer.isPlaying()) {
			// Log.i(MainActivity.TAG,"currentPosition="+currentPosition);
			return mediaPlayer.getCurrentPosition();
		}
		return 0;
	}

	// 获取当前歌曲的时长
	public int getDuration() {
		if (mediaPlayer.isPlaying() || isPause) {
			return mediaPlayer.getDuration();
		} else {
			return 0;
		}
	}

	// 指定进度条位置
	public void seekTo(int msec) {
		mediaPlayer.seekTo(msec);
	}

	// 更新状态接口
	public interface MusicUpdateListener {
		public void onPublish(int progress); // 更新进度条位置

		public void onChange(int position); // 改变歌曲播放位置
	}

	// set接口引用
	public void setMusicUpdateListener(MusicUpdateListener musicUpdateListener) {
		// L.e("setMusicUpdateListener");
		this.musicUpdateListener = musicUpdateListener;
	}

	// 判断当前歌曲是否在播放
	public boolean isPlaying() {
		if (mediaPlayer != null) {
			return mediaPlayer.isPlaying();
		}
		return false;
	}

	// 判断当前歌曲是否暂停
	public boolean isPause() {
		return isPause;
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		// TODO Auto-generated method stub
		L.e("mediaplayer---error");
		// mp.release();
		mp.reset();
		return false;
	}

	private Random random = new Random();

	// 歌曲完成后自动调用的方法
	@Override
	public void onCompletion(MediaPlayer mp) {
		// TODO Auto-generated method stub

		// L.e("mediaplayer---oncompletion");

		// xft
		// Log.i("128", "currentPlayMusic:" + String.valueOf(currentPlayMusic));
		if (currentPlayMusic == NET_MUSIC) {
			int position = shar_msg.getInt("position", 0);
			app.setPosition(position + 1);
			try {
				tv_singername.setText(app.getSingerName());
				tv_songname.setText(app.getSongName());
			} catch (Exception e) {
				Log.i("124", "!!");
			}
			playMusic(-1);
		}

		else if (currentPlayMusic == LOCAL_MUSIC) {

			/*
			 * 会报错 reset方法 因为歌词时间和歌曲时间不一致
			 */
			try {
				L.e(playActivity.mLrcView + "");
				playActivity.mLrcView.reset();
			} catch (Exception e) {
				// TODO: handle exception
				L.e("歌词出错");
			}

			switch (play_mode) {
			case ORDER_PLAY:
				next();
				break;
			case RANDOM_PLAY:
				play(random.nextInt(mp3Infos.size()));
				break;
			case SINGLE_PLAY:
				play(currentPosition);
				break;
			default:
				break;
			}
		}

	}

	/*
	 * 
	 * @author:xft
	 */

	public void initTextView(TextView songname, TextView singername) {
		this.tv_songname = songname;
		this.tv_singername = singername;
	}

	// 接受Activity传过来的数据
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub

		// L.e("调用---onStartCommand");

		try {
			Bundle bundle = intent.getExtras();

			recieveurl = bundle.getStringArray("url_array");
			re_singername = bundle.getStringArray("singername");
			re_songname = bundle.getStringArray("songname");
			// play_code的1和2分别标记排行榜歌单和搜索歌单
			play_code = bundle.getInt("play_code");
			re_album = bundle.getStringArray("album");

			// Log.i("124", "onStartCommand");

		} catch (Exception e) {
			// Log.i("124", " recieve is null");
		}
		return super.onStartCommand(intent, flags, startId);
		// showButtonNotify();
		// return flags;
	}

	// @Override
	// public void onPrepared(MediaPlayer mp) {
	// // TODO Auto-generated method stub
	//
	// mp.start();
	// Log.e("mediaPlayer", "onPrepared");
	// }

	// 缓冲长度
	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		// TODO Auto-generated method stub
		this.percent = percent;
	}

	// 初始化
	public void initView() {
		mediaPlayer.setOnBufferingUpdateListener(this);
		shar_msg = getSharedPreferences("msg", Activity.MODE_PRIVATE);
		editor_msg = shar_msg.edit();
	}

	// 播放
	public void play(String url) {

		if (IsConnectNet.checkNetworkAvailable(getApplicationContext())) {

			try {
				mediaPlayer.reset();
				mediaPlayer.setDataSource(url);
				mediaPlayer.prepare();
				mediaPlayer.start();

			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (musicUpdateListener != null) {
				musicUpdateListener.onChange(Constant.NULL_NET_MUSIC);
			}
		} else {
			Toast.makeText(getApplicationContext(), "唉，没有网哦-。-",
					Toast.LENGTH_SHORT).show();

		}
	}

	// 播放
	public void playMusic(int localPosition) {

		if (currentPlayMusic == LOCAL_MUSIC) {

			play(localPosition);

		} else if (currentPlayMusic == NET_MUSIC) {

			int position = app.getPosition();
			try {
				String url = app.sp_end.getString(
						"firsturl_" + String.valueOf(position), "");
				if (url != null) {
					app.setCode(1);
					play(url);
				}
			} catch (Exception e) {
				Log.i("124", "播放异常");
			}
		}

		// /zyqx
		if (ifno == 1 && if_change == 0) {
			showButtonNotify();
		}
	}

	// 暂停
	public void pauseMusic() {
		// Log.i("124", "pauseMusic");

		if (mediaPlayer != null && mediaPlayer.isPlaying()) {
			mediaPlayer.pause();
			isPause = true;
			app.setCode(-1);
		}

		if (isMainActivity == true) {
			// L.e("isMainActivity===true---pause");
			if (musicUpdateListener != null) {
				if (currentPlayMusic == LOCAL_MUSIC) {
					musicUpdateListener.onChange(currentPosition);
				} else {
					musicUpdateListener.onChange(Constant.NULL_NET_MUSIC);
				}

			}
		}

		// /zyqx
		if (ifno == 1 && if_change == 0) {
			showButtonNotify();
		}

	}

	// 获取正在播放的歌曲的信息---本地
	public Mp3Info getLocalPlayingSongMsg() {
		return mp3Infos.get(currentPosition);
	}

	// 获取正在播放的歌曲的信息---网络
	public String[] getPlayingSongMsg() {

		// String song_msg[] = new String[2];
		// int position = shar_msg.getInt("position", 0);
		// if (firstsongname != null && firstsingername != null && position !=
		// -1) {
		// song_msg[0] = firstsongname[position];
		// song_msg[1] = firstsingername[position];
		// } else {
		// song_msg[0] = "歌名";
		// song_msg[1] = "歌手";
		// }
		// return song_msg;

		String song_msg[] = new String[2];
		if (!app.getSongName().equals("歌名")
				&& !app.getSingerName().equals("歌手") && app.getPosition() != -1) {
			song_msg[0] = app.getSongName();
			song_msg[1] = app.getSingerName();
		} else {
			song_msg[0] = "歌名";
			song_msg[1] = "歌手";
		}
		return song_msg;
	}

	// 开始播放
	public void startMusic() {

		if (currentPlayMusic == LOCAL_MUSIC) {

			start();

		} else if (currentPlayMusic == NET_MUSIC) {

			try {
				// mediaPlayer.prepare();
				mediaPlayer.start();
				app.setCode(1);
				isPause = false;
			} catch (Exception e) {
				// TODO: handle exception
				L.e("start没执行");
			}

			if (isMainActivity = true) {
				// L.e("更新界面");
				if (musicUpdateListener != null) {
					musicUpdateListener.onChange(Constant.NULL_NET_MUSIC);
				}
			}

			// /zyqx
			if (ifno == 1 && if_change == 0) {
				showButtonNotify();
			}
		}

	}

	public void playAll() {
		setFirstMsg();
		app.setPosition(0);
		app.setCode(1);
		int netposition = app.getPosition();
		// Log.i("124","startMusic："+String.valueOf(netposition));
		String url = app.sp_end.getString(
				"firsturl_" + String.valueOf(netposition), "");
		play(url);
	}

	public MediaPlayer getMedia() {
		if (mediaPlayer != null) {
			return mediaPlayer;
		} else {
			return null;
		}
	}

	// 判断URL是否为空
	public int getUrlNum() {
		if (firsturl == null) {
			return -1;
		} else {
			return 1;
		}
	}

	// 切换到当前播放列表
	public void setFirstMsg() {
		firsturl = recieveurl;
		firstsingername = re_singername;
		firstsongname = re_songname;
		firstalbum = re_album;
		
		String firsturl_tag[] = new String[firsturl.length];
		for (int i = 0; i < firsturl_tag.length; i++) {
			String tag = String.valueOf(i);
			firsturl_tag[i] = "firsturl_" + tag;
		}

		String firstsingername_tag[] = new String[firstsingername.length];
		for (int i = 0; i < firstsingername_tag.length; i++) {
			String tag = String.valueOf(i);
			firstsingername_tag[i] = "firstsingername_" + tag;
		}

		String firstsongname_tag[] = new String[firstsongname.length];
		for (int i = 0; i < firstsongname_tag.length; i++) {
			String tag = String.valueOf(i);
			firstsongname_tag[i] = "firstsongname_" + tag;
		}

		String firstalbum_tag[] = new String[firstalbum.length];
		for (int i = 0; i < firstalbum_tag.length; i++) {
			String tag = String.valueOf(i);
			firstalbum_tag[i] = "firstalbum_" + tag;
		}
		app.setEndMsg(firsturl, firstsongname, firstsingername, firstalbum,
				firsturl_tag, firstsongname_tag, firstsingername_tag,
				firstalbum_tag);
	}

	// 判断media是否在播放
	public int getState() {
		if (mediaPlayer.isPlaying()) {
			return 1;
		} else {
			return -1;
		}
	}

	// 获取播放位置
	public int getPositon() {
		// return mediaPlayer.getCurrentPosition();
		if (mediaPlayer.isPlaying() || isPause) {
			return mediaPlayer.getCurrentPosition();
		} else {
			// Log.i("127","service getPosition:error");
			return 0;
		}
	}

	// 获取歌曲长度
	public int getNetDuration() {
		// return mediaPlayer.getDuration();
		if (mediaPlayer.isPlaying() || isPause) {
			return mediaPlayer.getDuration();
		} else {
			return 0;
		}
	}

	public void stopMusic() {
		mediaPlayer.stop();
	}

	// 获取缓冲长度
	public int getPercent() {
		// return this.percent;
		if (mediaPlayer.isPlaying() || isPause) {
			return this.percent;
		} else {
			return 0;
		}
	}

	// 设置seekbar拖动
	public void setTo(int msec) {
		mediaPlayer.seekTo(msec);
	}

	// 监听锁屏
	private BroadcastReceiver myre = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Log.i("127", "onreceive");
			if (action.equals(Intent.ACTION_SCREEN_OFF)) {
				Intent lockscreen = new Intent(PlayService.this,
						suoping_ac.class);
				lockscreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(lockscreen);
			}
		}

	};

	/**
	 * @author 张艳琴
	 */
	public void showButtonNotify() {
		// zyqx_2016-4-25(覆盖原来的)
		
		NotificationCompat.Builder mBuilder = new Builder(this);
		 RemoteViews mRemoteViews = new RemoteViews(getPackageName(),
				R.layout.view_custom_button);
		
//		Handler handler = new Handler() {
//			public void handleMessage(Message msg) {
//				if (msg.arg1 == 1) {
////					mRemoteViews.setImageBitmap(DownloadUtils.getInstance()
////							.GetImgFromSDCard(app.getSongName()));
//					
//					
//					mRemoteViews.setImageViewBitmap(R.id.custom_song_icon,
//							DownloadUtils.getInstance()
//							.GetImgFromSDCard(app.getSongName()));
//				}
//			};
//		};
//			
	
		if (currentPlayMusic == NET_MUSIC) {// 当前为网络歌曲播放
			String[] msg = getPlayingSongMsg();
			// 设置通知栏歌曲的图片
//			mRemoteViews.setImageViewResource(R.id.custom_song_icon,
//					R.drawable.music_icon);
//			mRemoteViews.setImageViewBitmap(R.id.custom_song_icon,
//					DownloadUtils.getInstance()
//					.GetImgFromSDCard(app.getSongName()));
//			DownloadUtils.getInstance().ShowImg(app.getAlbum(), imageview1_album,
//					handler, app.getSongName());
			
			// 设置专辑图片
			// 从文件中找专辑图片,若无设置为默认图片
			//imageview1_album.setImageResource(R.drawable.music_icon);
			DownloadUtils.getInstance().ShowImg1(app.getAlbum(), mRemoteViews,
					handler, app.getSongName());
			mRemoteViews.setTextViewText(R.id.tv_custom_song_singer,
					app.getSingerName() + "");// 通知栏歌手名字
			mRemoteViews.setTextViewText(R.id.tv_custom_song_name,
					app.getSongName() + "");// 通知栏歌曲名字
		} else {// 当前播放的是本地音乐
			Bitmap albumBitmap = MediaUtils.getArtWork(this,
					mp3Infos.get(currentPosition).getId(),
					mp3Infos.get(currentPosition).getAlbumId(), true, false);
			// imageview1_album.setImageBitmap(albumBitmap);
			mRemoteViews.setImageViewBitmap(R.id.custom_song_icon, albumBitmap);// 通知栏歌曲图片
			mRemoteViews.setTextViewText(R.id.tv_custom_song_singer, mp3Infos
					.get(currentPosition).getArtist() + "");// 通知栏歌手名字
			mRemoteViews.setTextViewText(R.id.tv_custom_song_name, mp3Infos
					.get(currentPosition).getTitle() + "");// 通知栏歌曲名字
		}
		if (BaseTools.getSystemVersion() <= 9) {
			mRemoteViews.setViewVisibility(R.id.ll_custom_button, View.GONE);
		} else {
			mRemoteViews.setViewVisibility(R.id.ll_custom_button, View.VISIBLE);

			if (PlayService.this.isPlaying()) {
				mRemoteViews.setImageViewResource(R.id.btn_custom_play,
						R.drawable.img_appwidget_pause_tongzhi);// 切换暂停和播放图片
			} else {
				mRemoteViews.setImageViewResource(R.id.btn_custom_play,
						R.drawable.img_appwidget_play_tongzhi);// 切换暂停和播放图片
			}
		}
		// showNotify();
		Intent buttonIntent = new Intent(ACTION_BUTTON);
		buttonIntent.putExtra(INTENT_BUTTONID_TAG, BUTTON_PREV_ID);
		PendingIntent intent_prev = PendingIntent.getBroadcast(this, 1,
				buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		mRemoteViews.setOnClickPendingIntent(R.id.btn_custom_prev, intent_prev);

		buttonIntent.putExtra(INTENT_BUTTONID_TAG, BUTTON_PALY_ID);
		PendingIntent intent_paly = PendingIntent.getBroadcast(this, 2,
				buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		mRemoteViews.setOnClickPendingIntent(R.id.btn_custom_play, intent_paly);

		buttonIntent.putExtra(INTENT_BUTTONID_TAG, BUTTON_NEXT_ID);
		PendingIntent intent_next = PendingIntent.getBroadcast(this, 3,
				buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		mRemoteViews.setOnClickPendingIntent(R.id.btn_custom_next, intent_next);

		buttonIntent.putExtra(INTENT_BUTTONID_TAG, BUTTON_CLEAN);
		PendingIntent clean = PendingIntent.getBroadcast(this, 4, buttonIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		mRemoteViews.setOnClickPendingIntent(R.id.clean, clean);

		mBuilder.setContent(mRemoteViews)
				.setContentIntent(
						getDefalutIntent(Notification.FLAG_ONGOING_EVENT))
				.setWhen(System.currentTimeMillis()).setTicker("SongRise")
				.setPriority(Notification.PRIORITY_DEFAULT).setOngoing(true)
				.setAutoCancel(true).setSmallIcon(R.drawable.ic_launcher);
		
		
	
		
		
		Notification notify = mBuilder.build();
		notify.flags = Notification.FLAG_ONGOING_EVENT;
		mNotificationManager.notify(200, notify);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		showButtonNotify();
	}

	public PendingIntent getDefalutIntent(int flags) {
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 1,
				new Intent(this, MainActivity.class), flags);
		return pendingIntent;
	}

	public void initButtonReceiver() {
		bReceiver = new ButtonBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ACTION_BUTTON);
		registerReceiver(bReceiver, intentFilter);
	}

	public final static String INTENT_BUTTONID_TAG = "ButtonId";

	public final static int BUTTON_PREV_ID = 1;

	public final static int BUTTON_PALY_ID = 2;

	public final static int BUTTON_NEXT_ID = 3;
	public final static int BUTTON_CLEAN = 4;

	public class ButtonBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if (action.equals(ACTION_BUTTON)) {

				int buttonId = intent.getIntExtra(INTENT_BUTTONID_TAG, 0);
				switch (buttonId) {
				case BUTTON_PREV_ID:
					if (currentPlayMusic == LOCAL_MUSIC) {
						prevMusic();// zyqx_2016-4-25
					} else {
						int c_positton1 = app.getPosition();
						app.setPosition(c_positton1 - 1);
						setCurrentPlayMusic(1);
						playMusic(-1);
					}
					break;
				case BUTTON_PALY_ID:
					if (isPlaying()) {
						pauseMusic();
					} else {
						if (isPause()) {
							startMusic();
						} else {
							if (currentPlayMusic == LOCAL_MUSIC) {
								playMusic(currentPosition);
							} else {
								playMusic(-1);
							}
						}
					}
					break;
				case BUTTON_NEXT_ID:
					next();
					
					break;
				case BUTTON_CLEAN:
					ifno = 0;
					NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
					notificationManager.cancel(200);
					break;
				default:
					break;
				}
			}
		}
	}
	
	
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.arg1 == 1) {
//				mRemoteViews.setImageBitmap(DownloadUtils.getInstance()
//						.GetImgFromSDCard(app.getSongName()));
				
				
				mRemoteViews.setImageViewBitmap(R.id.custom_song_icon,
						DownloadUtils.getInstance()
						.GetImgFromSDCard(app.getSongName()));
			}
		};
	};

	/**
	 * 
	 * @author 张艳琴 检测耳机插入拔出 插入耳机： 直接开始播放音乐 拔出耳机：停止播放音乐。 zyqx_2016-4-25（直接覆盖原来的）
	 */
	class HeadsetPlugReceiver extends BroadcastReceiver {
		private static final String TAG = "HeadsetPlugReceiver";

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.hasExtra("state")) {
				if (intent.getIntExtra("state", 0) == 0) {// 拔出耳机
					if (PlayService.this.isPlaying()) {
						// PlayService.this.pause();
						pauseMusic();
					}
					if (if_in == 1)// zyqx_2016-4-25
						Toast.makeText(context, "拔出耳机", Toast.LENGTH_LONG)
								.show();
					if_in = 1;// zyqx_2016-4-25
				} else if (intent.getIntExtra("state", 0) == 1) {// 耳机插入
					// if (PlayService.this.isPause()) {
					// // PlayService.this.start();
					// startMusic();
					// } else {
					// if (currentPlayMusic == LOCAL_MUSIC) {
					// playMusic(currentPosition);
					// } else {
					// playMusic(-1);
					// }
					// }
					//
					if (if_in == 1)// zyqx_2016-4-25
						T.showShort(context, "插入耳机");
					if_in = 1;// zyqx_2016-4-25
				}
			}
		}
	}

	/**
	 * @author 张艳琴 检测耳机的单击、双击事件 单击暂停、双击播放下一曲
	 */
	private void registerHeadsetPlugReceiver() {
		headsetPlugReceiver = new HeadsetPlugReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("android.intent.action.HEADSET_PLUG");
		registerReceiver(headsetPlugReceiver, intentFilter);
	}

	OnHeadSetListener headSetListener = new OnHeadSetListener() {

		@Override
		public void onDoubleClick() {
			// Toast.makeText(getBaseContext(), "双击", Toast.LENGTH_LONG).show();
			// PlayService.this.next();
			next();
		}

		@Override
		public void onClick() {
			// Toast.makeText(getBaseContext(), "单击", Toast.LENGTH_LONG).show();
			if (PlayService.this.isPlaying()) {
				PlayService.this.pause();
			} else {
				if (PlayService.this.isPause()) {
					// PlayService.this.start();
					startMusic();
				} else {
					// PlayService.this.play(currentPosition);
					if (currentPlayMusic == LOCAL_MUSIC) {
						playMusic(currentPosition);
					} else {
						playMusic(-1);
					}
				}
			}
		}
	};

	/**
	 * 
	 * 电话接听时，歌曲自动暂停 挂断电话时 ，歌曲自动从断开的地方开始播放
	 * 
	 * @author lenovo
	 * 
	 */
	class OnePhoneStateListener extends PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			Log.i(TAG, "[Listener]电话号码:" + incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:
				Log.i(TAG, "[Listener]等待接电话:" + incomingNumber);
				if (isPlaying()) {
					is_answer = 1;
					pauseMusic();
				}
				break;
			case TelephonyManager.CALL_STATE_IDLE:
				Log.i(TAG, "[Listener]电话挂断:" + incomingNumber);
				if (isPause() && is_answer == 1) {
					is_answer = 0;
					startMusic();
				}
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				Log.i(TAG, "[Listener]通话中:" + incomingNumber);
				if (isPlaying()) {
					is_answer = 1;
					pauseMusic();
				}
				break;
			}
			super.onCallStateChanged(state, incomingNumber);
		}
	}

}
