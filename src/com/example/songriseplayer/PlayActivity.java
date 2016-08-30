package com.example.songriseplayer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.app.SongRisePlayerApp;
import com.example.lyric.DefaultLrcParser;
import com.example.lyric.LrcRow;
import com.example.lyric.LrcView;
import com.example.lyric.LrcView.OnSeekToListener;
import com.example.utils.AppUtils;
import com.example.utils.DownloadUtils;
import com.example.utils.L;
import com.example.utils.MediaUtils;
import com.example.utils.T;
import com.example.vo.Mp3Info;
import com.example.vo.Mp3InfoShowApi;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.show.api.ShowApiRequest;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
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
import android.widget.*;
import android.widget.SeekBar.OnSeekBarChangeListener;

/**
 * 播放界面
 * 
 * @author zq,lilei
 * 
 */
public class PlayActivity extends BaseActivity implements OnClickListener,
		OnSeekBarChangeListener {

	private TextView textview1_start_time;
	private TextView textview1_end_time;
	// private TextView textview1_title;
	private ImageView imageview1_album;
	private ImageView imageview1_play_mode;
	private ImageView imageView2_play_pause;
	private ImageView imageview3_previous;
	private ImageView imageview1_next;
	private ImageView imageview4_favorite;
	private SeekBar seekbar;
	private ViewPager viewPager;
	private ArrayList<View> views = new ArrayList<View>();

	private SongRisePlayerApp app;

	private static final int UPDATE_TIME = 0x1; // 更新时间的标记
	private static final int UPDATE_LYRIC = 0x2; // 更新歌词

	public LrcView mLrcView; // 歌词显示对象
	private Toast playServiceToast; // Toast当前拖拉seekbar的时间
	private TextView edit_search;

	// lilei
	private ImageView imageview6_download;
	private ImageView imageview7_comment;
	private ImageView imageview_back;
	private ImageView imageview_search;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_music_play);

		init();
		initViewPager();

	}

	private void init() {
		textview1_start_time = (TextView) findViewById(R.id.textview1_start_time);
		textview1_end_time = (TextView) findViewById(R.id.textview1_end_time);
		// textview1_title=(TextView) findViewById(R.id.textview1_title);
		// imageview1_album=(ImageView)findViewById(R.id.imageview1_album);
		imageview1_play_mode = (ImageView) findViewById(R.id.Imageview1_play_mode);
		imageView2_play_pause = (ImageView) findViewById(R.id.ImageView2_play_pause);
		imageview3_previous = (ImageView) findViewById(R.id.Imageview3_previous);
		imageview1_next = (ImageView) findViewById(R.id.Imageview1_next);
		imageview4_favorite = (ImageView) findViewById(R.id.Imageview4_favorite);
		seekbar = (SeekBar) findViewById(R.id.seekbar);
		edit_search = (TextView) findViewById(R.id.edit_search);

		// lilei
		imageview6_download = (ImageView) findViewById(R.id.ImageView6_download);
		imageview7_comment = (ImageView) findViewById(R.id.ImageView7_comment);
		imageview_back = (ImageView) findViewById(R.id.imageview_back);
		imageview_search = (ImageView) findViewById(R.id.imageview_search);

		viewPager = (ViewPager) findViewById(R.id.viewpager);

		imageView2_play_pause.setOnClickListener(this);
		imageview1_play_mode.setOnClickListener(this);
		imageview3_previous.setOnClickListener(this);
		imageview1_next.setOnClickListener(this);
		seekbar.setOnSeekBarChangeListener(this);
		imageview4_favorite.setOnClickListener(this);

		// lilei
		imageview6_download.setOnClickListener(this);
		imageview7_comment.setOnClickListener(this);
		imageview_back.setOnClickListener(this);
		imageview_search.setOnClickListener(this);

		myHandler = new MyHandler(this);

		app = (SongRisePlayerApp) getApplication();

		// 设置标题栏颜色
		AppUtils.setSystemStatusBar(PlayActivity.this);
	}

	private void initViewPager() {
		View album_image_view = getLayoutInflater().inflate(
				R.layout.album_image_layout, null);
		View lrc_view = getLayoutInflater().inflate(R.layout.lrc_layout, null);
		// 初始化专辑界面控件
		imageview1_album = (ImageView) album_image_view
				.findViewById(R.id.imageview1_album);
		// textview1_title = (TextView) album_image_view
		// .findViewById(R.id.textview1_title);
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
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mLrcView.reset();
	}

	private MyHandler myHandler;

	private class MyHandler extends Handler {

		private PlayActivity playActivity;

		public MyHandler(PlayActivity playActivity) {
			this.playActivity = playActivity;
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (playActivity != null) {
				switch (msg.what) {
				case UPDATE_TIME:
					textview1_start_time.setText(MediaUtils
							.formatTime(msg.arg1));
					break;
				default:
					break;
				}
			}
		}
	}

	@Override
	public void publish(int progress) {
		// TODO Auto-generated method stub

		// 不能直接调用线程
		// textview1_start_time.setText(MediaUtils.formatTime(progress));
		Message msg = myHandler.obtainMessage(UPDATE_TIME);
		msg.arg1 = progress;
		myHandler.sendMessage(msg);
		seekbar.setProgress(progress);

	}

	@Override
	public void change(int position) {
		// TODO Auto-generated method stub

		// L.e("change方法");

		Mp3Info mp3Info = playService.mp3Infos.get(position);
		// textview1_title.setText(mp3Info.getTitle());
		edit_search.setText(mp3Info.getTitle());
		textview1_end_time
				.setText(MediaUtils.formatTime(mp3Info.getDuration()));

		if (playService.isPlaying()) {
			imageView2_play_pause.setImageResource(R.drawable.img_pause);
		} else {
			imageView2_play_pause.setImageResource(R.drawable.img_play);
		}
		Bitmap albumBitmap = MediaUtils.getArtWork(this, mp3Info.getId(),
				mp3Info.getAlbumId(), true, false);
		imageview1_album.setImageBitmap(albumBitmap);

		seekbar.setProgress(0);
		seekbar.setMax((int) mp3Info.getDuration());

		switch (playService.getPlay_Mode()) {
		case PlayService.ORDER_PLAY:
			imageview1_play_mode
					.setImageResource(R.drawable.img_playmode_repeat_playinglist_1);
			imageview1_play_mode.setTag(PlayService.ORDER_PLAY);
			break;
		case PlayService.RANDOM_PLAY:
			imageview1_play_mode
					.setImageResource(R.drawable.img_playmode_shuffle_playinglist_1);
			imageview1_play_mode.setTag(PlayService.RANDOM_PLAY);
			break;
		case PlayService.SINGLE_PLAY:
			imageview1_play_mode
					.setImageResource(R.drawable.img_playmode_repeatone_playinglist_1);
			imageview1_play_mode.setTag(PlayService.SINGLE_PLAY);
			break;
		}

		// 初始化收藏状态
		try {

			// 注意这里mp3Info.getMp3InfoId(), mp3Info.getId()的判断
			Mp3Info likeMp3Info = app.dbUtils.findFirst(Selector.from(
					Mp3Info.class).where("mp3InfoId", "=", getId(mp3Info)));
			if (likeMp3Info != null) {
				if (likeMp3Info.getIsLike() == 0) {
					imageview4_favorite.setImageResource(R.drawable.xin_bai);
				} else {
					imageview4_favorite.setImageResource(R.drawable.xin_hong);
				}
			} else {
				imageview4_favorite.setImageResource(R.drawable.xin_bai);
			}
		} catch (Exception e) {
			// TODO: handle exception
			L.e("初始化收藏状态出错");
		}

		// 初始化歌词
		/*
		 * 1.先判断该歌曲的歌词是否存在 2.若不存在,则下载歌词
		 */
		// 获取歌词名
		String musicname = mp3Info.getTitle();
		String singername = mp3Info.getArtist();

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
			 * 下载歌词 1.获取mp3InfoshowId 2.根据获取的mp3InfoshowId下载歌词
			 */

			// 从数据库取出当前歌曲mp3InfoshowId

			Mp3InfoShowApi mp3InfoShowApi;
			try {
				mp3InfoShowApi = app.dbUtils.findFirst(Selector
						.from(Mp3InfoShowApi.class)
						.where("musicname", "=", musicname)
						.and("singername", "=", singername));
				if (mp3InfoShowApi.getMp3InfoshowId() == 0) {
					L.e("mp3InfoshowId====0");
					mLrcView.setLrcRows(null);
					return;
				} else {

					L.e("下载歌词");
					L.e(mp3InfoShowApi + "");
					// 下载歌词
					LrcDownload(mp3InfoShowApi.getMp3InfoshowId() + "",
							mp3InfoShowApi.getMusicname(),
							mp3InfoShowApi.getSingername());

					// try {
					// mLrcView.setLrcRows(getLrcRows(musicname, singername));
					// } catch (Exception e) {
					// // TODO: handle exception
					// L.e("歌词还未下载完成----等待");
					// T.showShort(this, "歌词正在下载----等待");
					// }

				}
			} catch (DbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				L.e("未找到mp3InfoShowApi");
				mLrcView.setLrcRows(getLrcRows(musicname, singername));
				return;
			}

		}
	}

	// 歌词操作线程
	private Handler handler_music_lrc = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			// super.handleMessage(msg);
			switch (msg.what) {
			case UPDATE_LYRIC:

				// 解析message数据
				Bundle getBundle = msg.getData();
				// String musicid = getBundle.getString("musicid");
				String musicname = getBundle.getString("musicname");
				String singername = getBundle.getString("singername");
				//
				// String strid = DownloadUtils.getInstance().MusicShowId(
				// musicname, singername);
				//
				// if (strid == null) {
				// L.e("strid为空");
				// return;
				// }

				// if (DownloadUtils.music_count == 0) {
				// L.e("handlemessage----没有获取到音乐数据");
				// DownloadUtils.musicid = "";
				// break;
				// }
				if (DownloadUtils.mp3InfoshowId == null
						|| DownloadUtils.mp3InfoshowId.equals("0")) {
					L.e("handlemessage----musicid没有获取到");
					// DownloadUtils.mp3InfoshowId = "";
					break;
				}

				// L.e("Download----musicid===" + DownloadUtils.musicid);
				// DownloadUtils.getInstance().LrcDownload(strid, musicname,
				// singername);
				try {
					mLrcView.setLrcRows(getLrcRows(musicname, singername));
				} catch (Exception e) {
					// TODO: handle exception
					L.e("未获取到歌词");
				}

				// Log.e("TAG", "下载成功" + DownloadUtils.musicid);
				// DownloadUtils.musicid = "";
				break;
			default:
				break;
			}
		}

	};

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.Imageview1_next: // 下一首歌曲
			playService.next();
			break;
		case R.id.Imageview3_previous: // 上一首歌曲
			playService.prev();
			break;
		case R.id.ImageView2_play_pause: { // 暂停播放歌曲

			// 设置歌词
			// mLrcView.setLrcRows(getLrcRows());
			if (playService.isPlaying()) {
				imageView2_play_pause.setImageResource(R.drawable.img_play);
				playService.pause();
			} else {
				if (playService.isPause()) {
					imageView2_play_pause
							.setImageResource(R.drawable.img_pause);
					playService.start();
				} else {
					playService.play(playService.getCurrentPosition());
				}
			}

			break;
		}
		case R.id.Imageview1_play_mode: { // 切换播放模式

			int mode = (Integer) imageview1_play_mode.getTag();
			switch (mode) {
			case PlayService.ORDER_PLAY:
				imageview1_play_mode
						.setImageResource(R.drawable.img_playmode_shuffle_playinglist_1);
				imageview1_play_mode.setTag(PlayService.RANDOM_PLAY);
				playService.setPlay_Mode(PlayService.RANDOM_PLAY);
				T.showShort(PlayActivity.this, getString(R.string.random_play));
				break;
			case PlayService.RANDOM_PLAY:
				imageview1_play_mode
						.setImageResource(R.drawable.img_playmode_repeatone_playinglist_1);
				imageview1_play_mode.setTag(PlayService.SINGLE_PLAY);
				playService.setPlay_Mode(PlayService.SINGLE_PLAY);
				T.showShort(PlayActivity.this, getString(R.string.single_play));
				break;
			case PlayService.SINGLE_PLAY:
				imageview1_play_mode
						.setImageResource(R.drawable.img_playmode_repeat_playinglist_1);
				imageview1_play_mode.setTag(PlayService.ORDER_PLAY);
				playService.setPlay_Mode(PlayService.ORDER_PLAY);
				T.showShort(PlayActivity.this, getString(R.string.order_play));
				break;

			}
			break;
		}
		case R.id.Imageview4_favorite: { // 收藏音乐

			// 重点理解 难点！！！！
			Mp3Info mp3Info = playService.mp3Infos.get(playService
					.getCurrentPosition());
			try {

				Mp3Info likeMp3Info = app.dbUtils.findFirst(Selector.from(
						Mp3Info.class).where("mp3InfoId", "=", getId(mp3Info)));

				if (likeMp3Info == null) {

					mp3Info.setMp3InfoId(mp3Info.getId());
					mp3Info.setIsLike(1);
					app.dbUtils.save(mp3Info);
					imageview4_favorite.setImageResource(R.drawable.xin_hong);
				} else {
					int isLike = likeMp3Info.getIsLike();
					if (isLike == 1) {
						likeMp3Info.setIsLike(0);
						imageview4_favorite
								.setImageResource(R.drawable.xin_bai);
					} else {
						likeMp3Info.setIsLike(1);
						imageview4_favorite
								.setImageResource(R.drawable.xin_hong);
					}
					app.dbUtils.update(likeMp3Info, "isLike");

				}

			} catch (DbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		}
		case R.id.ImageView7_comment: // 评论按钮
			Comment();
			break;
		case R.id.ImageView6_download: // 下载按钮
			Download();
			break;
		case R.id.imageview_back: // 返回按钮
			finish();
			break;
		case R.id.imageview_search: // 搜索按钮
			startActivity(new Intent(this, SearchMusicActivity.class));
			break;
		default:
			break;
		}
	}

	/**
	 * 在不同的播放列表状态下,获取的id值不同
	 * 
	 * @param mp3Info
	 * @return
	 */
	private long getId(Mp3Info mp3Info) {

		// 初始收藏状态
		long id = 0;

		switch (playService.getChangePlayList()) {
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
			id = mp3Info.getMp3InfoId();
			break;
		default:
			break;
		}
		return id;

	}
	
	// seekbar进度条变化实现的方法
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO Auto-generated method stub

		// 非常重要！ 让歌词随进度条进度滚动
		L.e("本地歌曲progress===="+progress);
		mLrcView.seekTo(progress, true, false);

		if (fromUser) {
			
			//判断歌曲是否需要play
			if(playService.isPlaying()||playService.isPause()){
				playService.if_change=1;//zyqx-2016-4-25
				playService.pause();
				playService.seekTo(progress);
				playService.start();
				showPlayerToast(formatTimeFromProgress(progress));
				playService.if_change=0;//zyqx-2016-4-25
			}
		}
		
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		// mLrcView.seekTo(seekBar.getProgress(), true, false);
		// L.e("start");
		L.e("playing==="+playService.isPlaying()+","+"pause==="+playService.isPause());
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		// mLrcView.seekTo(seekBar.getProgress(), true, false);
		// L.e("stop");
		
		if(!playService.isPlaying()&&!playService.isPause()){
			seekbar.setProgress(0);
		}
		else{
			imageView2_play_pause.setImageResource(R.drawable.img_pause);
		}
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

	private void Download() {
		// TODO Auto-generated method stub
		T.showLong(this, "歌曲已下载");
	}

	/*
	 * 跳转到评论界面
	 * 
	 * by lilei
	 */
	private void Comment() {
		// TODO Auto-generated method stub
		Intent in = new Intent();
		String MusicName = edit_search.getText().toString();
		in.putExtra("MusicName", MusicName);
		in.setClass(PlayActivity.this, Comment.class);
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

}
