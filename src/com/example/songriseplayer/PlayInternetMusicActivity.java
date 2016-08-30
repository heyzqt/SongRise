package com.example.songriseplayer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.app.Constant;
import com.example.app.SongRisePlayerApp;

import com.example.lyric.DefaultLrcParser;
import com.example.lyric.LrcRow;
import com.example.lyric.LrcView;
import com.example.lyric.LrcView.OnSeekToListener;
import com.example.songriseplayer.PlayActivity.MyPagerAdapter;
import com.example.utils.AppUtils;
import com.example.utils.DownloadUtils;
import com.example.utils.L;
import com.example.utils.MediaUtils;
import com.example.utils.T;
import com.example.vo.Mp3InfoShowApi;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.mob.tools.utils.SharePrefrenceHelper;
import com.show.api.ShowApiRequest;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

//播放排行榜网络歌曲界面
public class PlayInternetMusicActivity extends BaseActivity implements
		OnClickListener, OnSeekBarChangeListener {

	private SeekBar seekBar;
	private SharedPreferences shar;
	private Editor editor;
	private SharedPreferences shar_msg;
	private Editor editor_msg;
	private TextView starttime;
	private TextView endtime;
	private ImageView image_psp;
	private int p_code;
	private ImageView image_next;
	private ImageView image_previous;
	private int code;
	private SearchresultActivity sr;
	private Timer mtimer = new Timer();

	private SharedPreferences shar_song;
	private Context context;
	private SongRisePlayerApp app;
	private TextView edit_search;
	
	private ImageView Imageview1_play_mode;
	private ImageView Imageview4_favorite;

	// zq
	private ViewPager viewPager;
	private ArrayList<View> views = new ArrayList<View>();
	public LrcView mLrcView; // 歌词显示对象
	private Toast playServiceToast; // Toast当前拖拉seekbar的时间
	private ImageView imageview1_album;
	private TextView textview1_title;
	private static final int UPDATE_TIME = 0x1; // 更新时间的标记
	private static final int UPDATE_LYRIC = 0x2; // 更新歌词
	private ImageView ImageView6_download;
	private final static int SAVE_MUSIC = 0x3;
	private ImageView ImageView7_comment;

	// 通过计时器不断更新UI
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			
			//lilei
//            switch (msg.arg1) {  
//            case 1:  
//            	  Bitmap bit = DownloadUtils.getInstance().GetImgFromSDCard("后来");
//            	  imageview1_album.setImageBitmap(bit);
//                break;  
//            default:  
//                break;  
//            }
			if (msg.arg1 == 1) {
				imageview1_album.setImageBitmap(DownloadUtils.getInstance()
						.GetImgFromSDCard(app.getSongName()));
			}
			
			try {
				int position = playService.getPositon();

				int duration = playService.getNetDuration();
				int percent = playService.getPercent();
				// 设置seekbar与starttime与endtime
				if (duration > 0 && seekBar != null) {
					long pos = seekBar.getMax() * position / duration;
					seekBar.setProgress((int) pos);
					seekBar.setSecondaryProgress(percent);
					if (percent <= pos) {
						// T.showShort(PlayInternetMusicActivity.this, "缓冲中");
					}
					starttime.setText(getTimeFormat(position));
					endtime.setText(getTimeFormat(playService.getDuration()));

				}
			} catch (Exception e) {
				// TODO: handle exception
			}

		}
	};
	// 计时器实现类
	private TimerTask timertask = new TimerTask() {

		@Override
		public void run() {

			if (seekBar != null) {

				handler.sendEmptyMessage(0);

				// Log.i("124","timer");
				// L.e("seekbar不为空");
			}

			// musicUpdateListener != null && mediaPlayer != null
			// && mediaPlayer.isPlaying()
			// if (seekBar != null&& playService.isPlaying()) {
			//
			// handler.sendEmptyMessage(0);
			//
			// // Log.i("124","timer");
			// L.e("seekbar不为空");
			// }
			// else{
			// seekBar.setProgress(0);
			// seekBar.setSecondaryProgress(0);
			// starttime.setText(0);
			// endtime.setText(getTimeFormat(playService.getDuration()));
			// }
		}
	};

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_playinternetmusic);

		initView();
		initViewPager();
		bindPlayService();
		// 设置标题栏颜色
		AppUtils.setSystemStatusBar(PlayInternetMusicActivity.this);

	}

	// @Override
	// protected void onResume() {
	// // TODO Auto-generated method stub
	// super.onResume();
	// bindPlayService();
	// }
	//
	// @Override
	// protected void onPause() {
	// // TODO Auto-generated method stub
	// super.onPause();
	// unbindPlayService();
	// }

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mLrcView.reset();
		unbindPlayService();
	}

	// 初始化组件
	private void initView() {

		context = this;
		app = (SongRisePlayerApp) getApplication();
		seekBar = (SeekBar) findViewById(R.id.seekbar_internet);
		image_psp = (ImageView) findViewById(R.id.internet_play_pause);
		image_psp.setOnClickListener(this);
		image_next = (ImageView) findViewById(R.id.internet_next);
		image_next.setOnClickListener(this);
		image_previous = (ImageView) findViewById(R.id.internet_previous);
		image_previous.setOnClickListener(this);
		starttime = (TextView) findViewById(R.id.textview1_start_time);
		endtime = (TextView) findViewById(R.id.textview1_end_time);
		shar = getSharedPreferences("searchsongurl", Activity.MODE_PRIVATE);
		editor = shar.edit();
		shar_msg = getSharedPreferences("msg", Activity.MODE_PRIVATE);
		editor_msg = shar_msg.edit();
		// 初始播放/暂停图标
		if (app.getCode() == -1) {
			image_psp.setImageResource(R.drawable.img_play);
		} else {
			image_psp.setImageResource(R.drawable.img_pause);
		}
		image_psp.setOnClickListener(this);
		// 绑定服务
		// Intent intent=new Intent(this,Intel_playservice.class);
		// bindPlayService();
		seekBar.setOnSeekBarChangeListener(this);
		// 开启计时器，一秒更新一次
		mtimer.schedule(timertask, 0, 1000);
		edit_search = (TextView) findViewById(R.id.edit_search);
		// try {
		// if (playService.isPlaying() || playService.isPause()) {
		//
		// // handler.sendEmptyMessage(0);
		// mtimer.schedule(timertask, 0, 1000);
		// // Log.i("124","timer");
		// L.e("seekbar不为空");
		// } else {
		// seekBar.setProgress(0);
		// seekBar.setSecondaryProgress(0);
		// starttime.setText(0);
		// endtime.setText(getTimeFormat(playService.getDuration()));
		// }
		// } catch (Exception e) {
		// // TODO: handle exception
		// L.e("出错了");
		// }
		
		Imageview1_play_mode = (ImageView) findViewById(R.id.Imageview1_play_mode);
		Imageview4_favorite = (ImageView) findViewById(R.id.Imageview4_favorite);
		Imageview1_play_mode.setOnClickListener(this);
		Imageview4_favorite.setOnClickListener(this);
	}

	private void initViewPager() {

		viewPager = (ViewPager) findViewById(R.id.viewpager);
		ImageView6_download = (ImageView) findViewById(R.id.ImageView6_download);
		ImageView7_comment = (ImageView) findViewById(R.id.ImageView7_comment);
		ImageView6_download.setOnClickListener(this);
		ImageView7_comment.setOnClickListener(this);
		shar_song = getSharedPreferences("songMsg", Activity.MODE_PRIVATE);

		View album_image_view = getLayoutInflater().inflate(
				R.layout.album_image_layout, null);
		View lrc_view = getLayoutInflater().inflate(R.layout.lrc_layout, null);
		// 初始化专辑界面控件
		imageview1_album = (ImageView) album_image_view
				.findViewById(R.id.imageview1_album);
		textview1_title = (TextView) album_image_view
				.findViewById(R.id.textview1_title);

		textview1_title.setText(app.getSongName());
		edit_search.setText(app.getSongName());
		
		//lilei
		//DownloadUtils.getInstance().ShowImg("",imageview1_album,handler,"后来");
		
		
		// textview1_title.setText(shar.getString("songname", ""));

		// try {
		// L.e("songname===" + shar_song.getString("songname", "")
		// + ",singername===" + shar_song.getString("singername", ""));
		// } catch (Exception e) {
		// // TODO: handle exception
		// }

		// 初始化歌词界面控件
		mLrcView = (LrcView) lrc_view.findViewById(R.id.lrcView);
		mLrcView.setOnSeekToListener(onSeekToListener);

		views.add(album_image_view);
		views.add(lrc_view);
		viewPager.setAdapter(new MyPagerAdapter());

	}

	OnSeekToListener onSeekToListener = new OnSeekToListener() {

		@Override
		public void onSeekTo(int progress) {
			playService.seekTo(progress);

		}
	};

//	public void setSongMg() {
//
//		String[] msg = playService.getPlayingSongMsg();
//
//		// try {
//		// L.e("songname==="+msg[0]+",singername==="+msg[1]);
//		// } catch (Exception e) {
//		// // TODO: handle exception
//		// }
//
//		if (msg != null) {
//			textview1_title.setText(msg[0]);
//			edit_search.setText(msg[0]);
//		} else {
//			textview1_title.setText("歌名");
//			edit_search.setText("播放列表");
//		}
//
//		editor.putString("songname", msg[0]);
//		editor.putString("singername", msg[1]);
//		editor.commit();
//
//		// try {
//		// L.e("songname===" + shar_song.getString("songname", "")
//		// + ",singername===" + shar_song.getString("singername", ""));
//		// } catch (Exception e) {
//		// // TODO: handle exception
//		// }
//
//	}
	
	public void setFirstView() {
		// Log.i("124","net_setFisrtView:"+String.valueOf(app.getCode()));
		
			if (app.getCode() == -1) {
				image_psp.setImageResource(R.drawable.img_play);
			} else if (app.getCode() == 1) {
				image_psp.setImageResource(R.drawable.img_pause);
			}
			Log.i("125", "app.getSongName()：" + app.getSongName());
			textview1_title.setText(app.getSongName());
			edit_search.setText(app.getSongName());
			DownloadUtils.getInstance().ShowImg(app.getAlbum(), imageview1_album,
					handler, app.getSongName());
			
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 设置播放/暂停图标以及实现播放与暂停功能
		case R.id.internet_play_pause:

			if (app.getCode() == 1 && playService.isPlaying()) {
				playService.pause();
				image_psp.setImageResource(R.drawable.img_play);
			} else {

				image_psp.setImageResource(R.drawable.img_pause);
				if (app.getCode() == -1 && playService.isPause()) {
					playService.startMusic();
				} else {
					playService.playMusic(-1);
				}
			}

			break;
		// 上一曲
		case R.id.internet_previous:

			int c_positton1 = app.getPosition();
			app.setPosition(c_positton1 - 1);
			playService.setCurrentPlayMusic(1);
			playService.playMusic(-1);
			image_psp.setImageResource(R.drawable.img_pause);
			//setSongMg();
			setFirstView();

			break;
		// 下一曲
		case R.id.internet_next:

			int c_positton2 = app.getPosition();
			app.setPosition(c_positton2 + 1);
			image_psp.setImageResource(R.drawable.img_pause);
			playService.setCurrentPlayMusic(1);
			playService.playMusic(Constant.NULL_NET_MUSIC);
			//setSongMg();
			setFirstView();

			break;
		case R.id.ImageView6_download: {
			// 获取当前播放歌曲信息
			String[] msg = playService.getPlayingSongMsg();
			String musicname = msg[0];
			// String singername = msg[1];
			if (DownloadUtils.checkMusicExist(musicname)) {
				T.showShort(this, "歌曲已下载，请去本地列表播放");
				MediaScannerConnection.scanFile(this, DownloadUtils.getInstance().FileScanner(), null,
						new MediaScannerConnection.OnScanCompletedListener() {

							@Override
							public void onScanCompleted(String path, Uri uri) {
								// TODO Auto-generated method stub
								Log.i("haha", uri + "----");
							}

						});
			} else {
				// 获取歌曲url
				int position = app.getPosition();
				try {
					String url = app.sp_end.getString(
							"firsturl_" + String.valueOf(position), "");
					if (url != null) {
						Download(url, musicname);
					}
				} catch (Exception e) {
					L.e("网络界面,下载bug");
					T.showShort(this, "下载失败");
				}
			}
			break;
		}
		case R.id.ImageView7_comment: {
			Comment();
			break;
		}
		case R.id.Imageview1_play_mode:
			T.showShort(this, "网络歌曲无法切换播放模式");
			break;
		case R.id.Imageview4_favorite:
			T.showShort(this, "下载后才可收藏");
			break;
		default:
			break;
		}

	}

	// 求当前播放进度
	private String getTimeFormat(int time) {
		String timeStr = "00:00";
		int s = time / 1000; // 秒
		int h = s / 3600; // 求整数部分 ，小时
		int r = s % 3600; // 求余数
		int m = 0;
		if (r > 0) {
			m = r / 60; // 分
			r = r % 60; // 求分后的余数，即为秒
		}

		if (h < 10) {
			timeStr = "0" + h;
		} else {
			timeStr = "" + h;
		}

		if (m < 10) {
			timeStr = timeStr + ":" + "0" + m;
		} else {
			timeStr = timeStr + ":" + m;
		}

		if (r < 10) {
			timeStr = timeStr + ":" + "0" + r;
		} else {
			timeStr = timeStr + ":" + r;
		}

		timeStr = timeStr.substring(3);
		return timeStr;
	}

	/**
	 * 将播放进度的毫米数转换成时间格式 如 3000 --> 00:03
	 * 
	 * @param progress
	 * @return
	 */
	private String formatTimeFromProgress(int progress) {
		// 总的秒数
		int msecTotal = progress / 1000;
		int min = msecTotal / 60;
		int msec = msecTotal % 60;
		String minStr = min < 10 ? "0" + min : "" + min;
		String msecStr = msec < 10 ? "0" + msec : "" + msec;
		return minStr + ":" + msecStr;
	}

	// 设置seekbar拖动事件
	private int progress;

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {

		// 非常重要！ 让歌词随进度条进度滚动
		int scroll_progress= progress * playService.getDuration()
				/ seekBar.getMax();
		mLrcView.seekTo(scroll_progress, true, false);
		//L.e("网络歌曲progress===="+scroll_progress);

		if (fromUser) {			
			this.progress = progress * playService.getDuration()
					/ seekBar.getMax();
			if (playService.isPlaying() || playService.isPause()) {
				playService.if_change=1;//zyqx-2016-4-25
				playService.pause();
				playService.seekTo(this.progress);
				playService.start();
				showPlayerToast(formatTimeFromProgress(this.progress));
				playService.if_change=0;//zyqx-2016-4-25
			}
		}

	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// playService.setTo(progress);
		// image_psp.setImageResource(R.drawable.img_pause);
		if (!playService.isPlaying() && !playService.isPause()) {
			seekBar.setProgress(0);
		}
		else if(playService.isPlaying() && playService.isPause())
			{
			   seekBar.setProgress(progress);
			   playService.startMusic();
			   image_psp.setImageResource(R.drawable.img_pause);
			}
		 else {
			image_psp.setImageResource(R.drawable.img_pause);
		}

	}

	@Override
	public void publish(int progress) {
		// TODO Auto-generated method stub

	}

	@Override
	public void change(int position) {
		// TODO Auto-generated method stub

		
		//setSongMg();
		setFirstView();
		
		// 初始化歌词
		/*
		 * 1.先判断该歌曲的歌词是否存在 2.若不存在,则下载歌词
		 */

		String[] msg = playService.getPlayingSongMsg();

		String musicname = msg[0];
		String singername = msg[1];

		// L.e("songname===" + msg[0] + ",singername===" + msg[1]);

		if (DownloadUtils.checkMusicLrcExist(musicname, singername)) {
			L.e("歌词已存在");

			try {
				mLrcView.setLrcRows(getLrcRows(musicname, singername));
			} catch (Exception e) {
				// TODO: handle exception
				L.e("歌词已存在bug......");
			}

		} else {

			/*
			 * 获取歌词 1.查找数据库有没有该歌曲信息 1.1若有,就update数据 1.2若无,就save数据
			 * 
			 * 2.获取mp3InfoshowId
			 * 
			 * 3.根据获取的mp3InfoshowId下载歌词
			 */

			// 先从showapi获取歌曲musicid
			MusicShowId(msg[0], msg[1]);

		}
	}

	/**
	 * 专辑和歌词的适配器
	 * 
	 * @author zq
	 * 
	 */
	class MyPagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return views.size();
		}

		// 判断视图是否为返回的对象
		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1; // 这行代码很重要，它用于判断你当前要显示的页面
		}

		// 实例化选项卡
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(views.get(position));
			return views.get(position);
		}

		// 删除选项卡
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(views.get(position));
		}

	}

	/*
	 * 歌词歌曲的下载的url和歌词Id是从上一个界面传过来的，若是本地播放的歌曲，则下载按钮设置为
	 * 
	 * by lilei
	 */

	private void Download(String url, String musicname) {
		// TODO Auto-generated method stub
		// String Url = "从上个界面获取";
		// String MusicId = "从上个界面获取";
		// String musicname = "";
		if (url.length() == 0 || url.equals(null)) {
			T.showShort(this, "歌曲已存在");
			
		} else {
			T.showShort(this, "开始下载");

			// 新方法
			DownloadUtils.getInstance().DownLoadMusic(this,url, musicname);

			// L.e("musicname====" + musicname);
			MediaScannerConnection.scanFile(this, DownloadUtils.getInstance().FileScanner(), null,
					new MediaScannerConnection.OnScanCompletedListener() {

						@Override
						public void onScanCompleted(String path, Uri uri) {
							// TODO Auto-generated method stub
							Log.i("haha", uri + "----");
						}

					});

			// MediaScannerConnection.scanFile(this, new String[] {
			//
			// Environment.getExternalStorageDirectory().getAbsolutePath()
			// + "/SongRise/Music/平凡之路.mp3",
			// Environment.getExternalStorageDirectory().getAbsolutePath()
			// + "/SongRise/Music/"+musicname+".png" },
			//
			// null, new MediaScannerConnection.OnScanCompletedListener() {
			//
			// @Override
			// public void onScanCompleted(String path, Uri uri) {
			// // TODO Auto-generated method stub
			// Log.i("haha", uri + "----");
			// }
			//
			// });

			// 原方法
			// DownloadUtils.getInstance().DownLoadMusic(url, musicname);
			// // DownloadUtils.getInstance().DownLoadMusic(
			// // "http://stream10.qqmusic.qq.com/34833285.mp3", "太早");
			// // DownloadUtils.getInstance().LrcDownload("151784", "太早");
			// MediaScannerConnection.scanFile(PlayInternetMusicActivity.this,
			// new String[] { Environment.getExternalStorageDirectory()
			// .getAbsolutePath() + "/SongRise/Music" }, null,
			// null);

		}
	}

	/*
	 * 跳转到评论界面
	 * 
	 * by lilei
	 */
	private void Comment() {
		// TODO Auto-generated method stub
		Intent in = new Intent();
		String MusicName = textview1_title.getText().toString();
		in.putExtra("MusicName", MusicName);
		in.setClass(PlayInternetMusicActivity.this, Comment.class);
		startActivity(in);
	}

	/**
	 * 歌词下载方法
	 * 
	 * @param MusicID
	 *            解析后获取的歌曲ID
	 * @param MusicName
	 *            歌曲名
	 * @param singername
	 *            歌手名
	 */
	public void LrcDownload(String MusicID, final String MusicName,
			final String singername) {
		String dir = Environment.getExternalStorageDirectory()
				.getAbsolutePath();
		String mfile = "/SongRise/LRC";
		File file = new File(dir + mfile);
		if (!file.exists()) {
			file.mkdirs();
			final File refile = new File(file, MusicName + "_" + singername
					+ ".LRC");
			// Log.i("haha", file.mkdirs() + ".." + refile);
		}
		final File refile = new File(file, MusicName + "_" + singername
				+ ".LRC");

		final AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
			@SuppressWarnings("resource")
			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				// TODO Auto-generated method stub

				try {
					JSONObject root = new JSONObject(new String(arg2, "utf-8"));

					if (root.getInt("showapi_res_code") != 0) {
						Log.i("haha",
								"Error:" + "--"
										+ root.getString("showapi_res_error"));
					}

					else {

						JSONObject body = root
								.getJSONObject("showapi_res_body");

						String lrc = body.getString("lyric");

						String re1 = lrc.replace("&#58;", ":");

						String re2 = re1.replace("&#10;", "\r\n");

						String re3 = re2.replace("&#32;", " ");

						String re4 = re3.replace("&#45;", "-");

						String re5 = re4.replace("&#13;", "");

						String re6 = re5.replace("&#40;", "");

						String re7 = re6.replace("&#41;", "");

						String filrc = re7.replace("&#46;", ".");

						if (!refile.exists()) {
							refile.createNewFile();
						}

						FileWriter fw = null;
						BufferedWriter bw = null;
						try {

							fw = new FileWriter(refile, false);

							bw = new BufferedWriter(fw);
							bw.write(filrc);
							bw.newLine();
							bw.flush();
							bw.close();
							fw.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							try {
								bw.close();
								fw.close();
							} catch (IOException e1) {
								// TODO Auto-generated catch block
							}
						}

					}

				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					L.e("下载失败");
				}

				// Log.e("TAG", "onsuccess方法中的mp3InfoshowId=" +
				// DownloadUtils.mp3InfoshowId);
				L.e("LrcDownload---onsuccess歌词下载完成");
				Message msg = Message.obtain();
				msg.what = UPDATE_LYRIC;
				Bundle bundle = new Bundle();
				// bundle.putString("mp3InfoshowId",
				// DownloadUtils.mp3InfoshowId);
				bundle.putString("musicname", MusicName);
				bundle.putString("singername", singername);
				msg.setData(bundle);
				handler_music_lrc.sendMessage(msg);

				// handler_music_lrc.sendEmptyMessage(UPDATE_LYRIC);
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
				Log.i("haha", "歌词下载失败");
				Message msg = Message.obtain();
				msg.what = UPDATE_LYRIC;
				Bundle bundle = new Bundle();
				// bundle.putString("mp3InfoshowId",
				// DownloadUtils.mp3InfoshowId);
				bundle.putString("musicname", MusicName);
				bundle.putString("singername", singername);
				msg.setData(bundle);
				handler_music_lrc.sendMessage(msg);
				// handler_music_lrc.sendEmptyMessage(UPDATE_LYRIC);
			}
		};

		// Map<String,String> map1 = GetApiInfo();
		Map<String, String> map1 = new HashMap<String, String>();
		map1.put("AppID", "16947");
		map1.put("AppSecret", "2e8da330ca5e4724911164d9053bedda");
		new ShowApiRequest("http://route.showapi.com/213-2", map1.get("AppID"),
				map1.get("AppSecret")).setResponseHandler(res)
				.addTextPara("musicid", MusicID).post();

	}

	// 歌词操作线程
	private Handler handler_music_lrc = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case SAVE_MUSIC: // 保存歌曲的showapi的musicid

				// 解析message数据
				Bundle getBundle1 = msg.getData();
				String mp3InfoshowId = getBundle1.getString("mp3InfoshowId");
				String musicname_save = getBundle1.getString("musicname");
				String singername_save = getBundle1.getString("singername");
				Mp3InfoShowApi mp3InfoShowApi = new Mp3InfoShowApi();
				if (mp3InfoshowId == null) {
					mp3InfoShowApi.setMp3InfoshowId(Long.parseLong("0"));
				} else {
					mp3InfoShowApi.setMp3InfoshowId(Long
							.parseLong(mp3InfoshowId));
				}
				mp3InfoShowApi.setMusicname(musicname_save);
				mp3InfoShowApi.setSingername(singername_save);
				try {
					// 遍历数据库,如果mp3InfoShowApi存在就update，不存在就save
					Mp3InfoShowApi find_mp3ShowApi = app.dbUtils
							.findFirst(Selector.from(Mp3InfoShowApi.class)
									.where("musicname", "=", musicname_save)
									.and("singername", "=", singername_save));
					// L.e("mp3InfoShowApi===" + mp3InfoShowApi);
					// L.e("find_mp3ShowApi===" + find_mp3ShowApi);
					if (find_mp3ShowApi == null) {

						app.dbUtils.save(mp3InfoShowApi);
						// L.e("save");
					} else {
						find_mp3ShowApi.setMp3InfoshowId(mp3InfoShowApi
								.getMp3InfoshowId());
						app.dbUtils.update(find_mp3ShowApi, "mp3InfoshowId");
						// L.e("update");
					}

					// Mp3InfoShowApi已存在进入数据库的情况
					// Mp3InfoshowId也存放好后
					// 再来更新歌词

					// 从数据库取出当前歌曲mp3InfoshowId
					try {

						// 遍历数据库,如果mp3InfoShowApi_lyr存在就update，不存在就save
						Mp3InfoShowApi find_mp3ShowApi_lyr = app.dbUtils
								.findFirst(Selector
										.from(Mp3InfoShowApi.class)
										.where("musicname", "=", musicname_save)
										.and("singername", "=", singername_save));

						// L.e("find_mp3ShowApi===" + find_mp3ShowApi_lyr);

						if (find_mp3ShowApi_lyr.getMp3InfoshowId() == 0) {
							mLrcView.setLrcRows(null);
							return;
						} else {
							// 下载歌词
							LrcDownload(mp3InfoShowApi.getMp3InfoshowId() + "",
									find_mp3ShowApi_lyr.getMusicname(),
									find_mp3ShowApi_lyr.getSingername());

						}

					} catch (DbException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						mLrcView.setLrcRows(getLrcRows(musicname_save,
								singername_save));
						L.e("查找find_mp3ShowApi_lyr失败");
					}

				} catch (DbException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					L.e("未找到mp3InfoShowApi");
					return;
				}

				break;
			case UPDATE_LYRIC: // 更新歌词状态
				// 解析message数据
				Bundle getBundle = msg.getData();
				// String musicid = getBundle.getString("musicid");
				String musicname = getBundle.getString("musicname");
				String singername = getBundle.getString("singername");

				if (DownloadUtils.mp3InfoshowId == null
						|| DownloadUtils.mp3InfoshowId.equals("0")) {
					L.e("handlemessage----musicid没有获取到");
					// DownloadUtils.mp3InfoshowId = "";
					break;
				}

				try {
					mLrcView.setLrcRows(getLrcRows(musicname, singername));
				} catch (Exception e) {
					// TODO: handle exception
					L.e("未获取到歌词");
				}
				break;

			default:
				break;
			}
		}

	};

	private TextView playServiceToastTv;

	/**
	 * 显示当前拖动歌曲进度条的位置(以时间显示)
	 * 
	 * @param text
	 */
	private void showPlayerToast(String text) {

		if (playServiceToast == null) {
			// Log.e("hello_showPlayerToast", text);
			playServiceToast = new Toast(this);
			playServiceToastTv = (TextView) LayoutInflater.from(this).inflate(
					R.layout.toast, null);
			playServiceToast.setView(playServiceToastTv);
			playServiceToast.setDuration(Toast.LENGTH_SHORT);
		}
		playServiceToastTv.setText(text);

		// 设置Toast的位置
		// 获取屏幕高度
		Display display = getWindowManager().getDefaultDisplay();
		int height = display.getHeight();
		playServiceToast.setGravity(Gravity.BOTTOM, 0, height / 40 * 11);
		playServiceToast.show();
	}

	/**
	 * 获取歌词List集合
	 * 
	 * @return
	 * 
	 */
	private List<LrcRow> getLrcRows(String musicname, String singername) {
		List<LrcRow> rows = null;
		// InputStream is = getResources().openRawResource(R.raw.daerwen_lyric);
		String dir = Environment.getExternalStorageDirectory()
				.getAbsolutePath();
		String mfilePath = "/SongRise/LRC";
		File path = new File(dir + mfilePath);
		File[] files = path.listFiles();// 读取

		File mfile = null;

		String file_name = musicname + "_" + singername + ".LRC";
		for (File f : files) {
			if (f.getName().equals(file_name)) {
				Log.e("TAG", "找到" + file_name + "文件");
				mfile = f;
				break;
			}
		}

		FileInputStream fis;
		try {
			fis = new FileInputStream(mfile);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			L.e("歌词文件没找到");
			return null;
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		String line;
		StringBuffer sb = new StringBuffer();
		try {
			while ((line = br.readLine()) != null) {
				sb.append(line + "\n");
			}
			System.out.println(sb.toString());
			rows = DefaultLrcParser.getIstance().getLrcRows(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return rows;
	}

	/**
	 * 歌曲id查询(在showApi中查询,此id是showapi的歌曲id)
	 * 
	 * @param musicname
	 * @param singername
	 * 
	 * @author zq
	 */
	public void MusicShowId(final String musicname, final String singername) {

		final AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] responseBody) {
				// TODO Auto-generated method stub

				try {
					// System.out.println("response is :"
					// + new String(responseBody, "utf-8"));

					// 在此对返回内容做处理

					JSONObject root = new JSONObject(new String(responseBody,
							"utf-8"));

					if (root.getInt("showapi_res_code") != 0) {
						Log.i("haha",
								"Error:" + "--"
										+ root.getString("showapi_res_error"));
						Log.e("TAG", "资源错误");
						return;
					}

					else {

						JSONObject body = root
								.getJSONObject("showapi_res_body");
						JSONObject body_child = body.getJSONObject("pagebean");
						JSONArray body_child_contentlist = body_child
								.getJSONArray("contentlist");
						// 找到对应歌曲名和对应歌手名的歌曲id
						DownloadUtils.music_count = body_child_contentlist
								.length();
						// L.e(""+body_child_contentlist);
						if (body_child_contentlist.length() == 0) {
							return;
						}
						for (int i = 0; i < body_child_contentlist.length(); i++) {
							JSONObject min_body = (JSONObject) body_child_contentlist
									.get(i);
							// System.out.println("" + min_body);

							try {
								if (singername.equals("")) {
									// L.e(min_body.getString("singername") +
									// "");
									DownloadUtils.mp3InfoshowId = min_body
											.getString("songid");
									break;
								} else if (min_body.getString("singername")
										.equals(singername)) {
									// L.e(min_body.getString("singername") +
									// "");
									DownloadUtils.mp3InfoshowId = min_body
											.getString("songid");
									break;
								}
								// else {
								// //L.e(min_body.getString("singername") + "");
								// //DownloadUtils.mp3InfoshowId = "0";
								// }
							} catch (Exception e) {
								// TODO: handle exception
								// L.e("没有musicname字段或singername字段");
								DownloadUtils.mp3InfoshowId = "0";
								break;
							}
						}
					}

				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					Log.e("TAG", "success--bug");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.e("TAG", "json出错");
				}

				// Log.e("TAG", "onsuccess方法中的mp3InfoshowId=" +
				// DownloadUtils.mp3InfoshowId);
				Message msg = Message.obtain();
				msg.what = SAVE_MUSIC;
				Bundle bundle = new Bundle();
				bundle.putString("mp3InfoshowId", DownloadUtils.mp3InfoshowId);
				bundle.putString("musicname", musicname);
				bundle.putString("singername", singername);
				msg.setData(bundle);
				handler_music_lrc.sendMessage(msg);
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
				Log.e("TAG", "连接网络获取歌曲id失败----music="
						+ DownloadUtils.mp3InfoshowId);
				Message msg = Message.obtain();
				msg.what = SAVE_MUSIC;
				Bundle bundle = new Bundle();
				bundle.putString("mp3InfoshowId", DownloadUtils.mp3InfoshowId);
				bundle.putString("musicname", musicname);
				bundle.putString("singername", singername);
				msg.setData(bundle);
				handler_music_lrc.sendMessage(msg);
			}

		};

		try {
			new ShowApiRequest("http://route.showapi.com/213-1", "16947",
					"2e8da330ca5e4724911164d9053bedda")
					.setResponseHandler(responseHandler)
					.addTextPara("keyword", musicname).addTextPara("page", "1")
					.post();

		} catch (Exception e) {
			// TODO: handle exception
			Log.e("TAG", "发送失败");
		}
	}
	
}
