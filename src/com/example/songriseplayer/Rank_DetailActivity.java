package com.example.songriseplayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import com.example.app.Constant;
import com.example.app.SongRisePlayerApp;
import com.example.utils.AppUtils;
import com.example.utils.DownloadUtils;
import com.example.utils.L;
import com.example.utils.MediaUtils;
import com.example.vo.Mp3Info;
import com.loopj.android.http.AsyncHttpResponseHandler;
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
import android.media.MediaPlayer.OnCompletionListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.DocumentsContract.Document;
import android.renderscript.Element;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

//XFT 排行榜界面
public class Rank_DetailActivity extends BaseActivity implements
		View.OnClickListener, OnItemClickListener {

	private String appid = "16950";
	private String secret = "eac11e3d0cab4def9da973eb78f97512";
	private LinearLayout load_layout;
	private ListView listview_music;
	private ArrayList<HashMap<String, Object>> listdata = new ArrayList<HashMap<String, Object>>();

	private Context context;
	private SimpleAdapter adapter;
	private View v;
	private String NAME;
	private AsyncHttpResponseHandler resHandler;
	private ImageView img_back;
	private TextView tv_title;
	private LinearLayout rank_toplay;

	private String[] songurl;
	private ImageView image_sp;
	private ImageView image_next;
	private ImageView img_album;
	private String songnametag[] = null;
	private String singernametag[] = null;

	private String songname[];
	private String singername[];
	private String songrank[];
	private TextView tv_songname;
	private TextView tv_singername;
	String msg[] = new String[2];
	private LinearLayout rank_playall;
	private Mp3Info mp3Info;
	private SongRisePlayerApp app;
	private String album[];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.rank_detail);
		bindPlayService();
		initView();
		context = this;
		// 获取上一个页面传递的值
		NAME = getBundleData();
		// 初始化线程获取网络数据
		resHandler = new Asynchttp();
		LoadData();

		// 设置标题栏颜色
		AppUtils.setSystemStatusBar(Rank_DetailActivity.this);
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.arg1 == 1) {
				img_album.setImageBitmap(DownloadUtils.getInstance()
						.GetImgFromSDCard(app.getSongName()));
			}
		};
	};

	public void setFirstView() {
		Log.i("124", "rank_setFisrtView:" + String.valueOf(app.getCode()));
		if (app.sp.getInt(Constant.CURRENT_PLAY_MUSIC, 0) == 0) {
			Log.i("125", "rank_detail LocalMusic");
			if (app.getCode() == -1) {
				image_sp.setImageResource(R.drawable.img_appwidget_pause1);
			} else if (app.getCode() == 1) {
				image_sp.setImageResource(R.drawable.img_appwidget_play1);
			}
			mp3Info = app.getCurrentLocalMp3Infos().get(
					app.getCurrentPositionLocal());
			// 歌曲名
			tv_songname.setText(mp3Info.getTitle());
			// 歌手名
			tv_singername.setText(mp3Info.getArtist());
			// 专辑处理
			Bitmap albumBitmap = MediaUtils.getArtWork(this, mp3Info.getId(),
					mp3Info.getAlbumId(), true, true);
			img_album.setImageBitmap(albumBitmap);
		}

		else if (app.sp.getInt(Constant.CURRENT_PLAY_MUSIC, 0) == 1) {
			Log.i("125", "rank_detail netMusic");
			if (app.getCode() == -1) {
				image_sp.setImageResource(R.drawable.img_appwidget_pause1);
			} else if (app.getCode() == 1) {
				image_sp.setImageResource(R.drawable.img_appwidget_play1);
			}
			tv_songname.setText(app.getSongName());
			tv_singername.setText(app.getSingerName());

			try {
				L.e(app.getAlbum());
			} catch (Exception e) {
				// TODO: handle exception
				L.e("url=====null");
			}
			DownloadUtils.getInstance().ShowImg(app.getAlbum(), img_album,
					handler, app.getSongName());
		}
	}

	// 初始化界面
	private void initView() {

		app = (SongRisePlayerApp) getApplication();
		tv_songname = (TextView) findViewById(R.id.textview1_title);
		tv_singername = (TextView) findViewById(R.id.textview2_singer_name);
		load_layout = (LinearLayout) findViewById(R.id.load_layout);
		listview_music = (ListView) findViewById(R.id.listview_music);
		img_back = (ImageView) findViewById(R.id.image_back);
		img_back.setOnClickListener(this);
		rank_playall = (LinearLayout) findViewById(R.id.rank_playall);
		rank_playall.setOnClickListener(this);
		tv_title = (TextView) findViewById(R.id.tv_title);
		rank_toplay = (LinearLayout) findViewById(R.id.layout_go);
		rank_toplay.setOnClickListener(this);
		v = findViewById(R.id.tool);
		v.getBackground().setAlpha(120);
		// v = findViewById(R.id.title);
		// v.getBackground().setAlpha(120);
		image_sp = (ImageView) findViewById(R.id.image_sp);
		image_next = (ImageView) findViewById(R.id.image_next);
		image_next.setImageResource(R.drawable.img_appwidget_play_next);
		image_next.setOnClickListener(this);
		listview_music.setOnItemClickListener(this);
		image_sp.setOnClickListener(this);
		img_album = (ImageView) findViewById(R.id.imageview1_album);
		// setFirstView();

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.image_back:
			finish();
			break;
		// 设置播放/暂停的图标并完成播放/暂停功能
		case R.id.image_sp:
			playService.initTextView(tv_songname, tv_singername);
			if (app.sp.getInt(Constant.CURRENT_PLAY_MUSIC, 0) == playService.NET_MUSIC) {
				if (app.getCode() == 1 && playService.isPlaying()) {
					playService.pause();
					image_sp.setImageResource(R.drawable.img_appwidget_pause1);
				} else {
					image_sp.setImageResource(R.drawable.img_appwidget_play1);
					if (app.getCode() == -1 && playService.isPause()) {
						playService.startMusic();
					} else {
						playService.playMusic(-1);
						playService.initTextView(tv_songname, tv_singername);
					}
				}
			}

			else {
				if ((app.getCode() == 1 && playService.isPlaying())) {
					image_sp.setImageResource(R.drawable.img_appwidget_pause1);
					playService.pause();
					// isPause = true;
				} else {
					image_sp.setImageResource(R.drawable.img_appwidget_play1);
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

		case R.id.image_next:
			playService.initTextView(tv_songname, tv_singername);
			if (app.sp.getInt(Constant.CURRENT_PLAY_MUSIC, 0) == playService.LOCAL_MUSIC) {
				playService.next();
				setFirstView();
			} else if (app.sp.getInt(Constant.CURRENT_PLAY_MUSIC, 0) == playService.NET_MUSIC) {
				playService.next();
				// setSongMg();
				setFirstView();
			}
			image_sp.setImageResource(R.drawable.img_appwidget_play1);
			break;
		case R.id.rank_playall:
			playService.initTextView(tv_songname, tv_singername);
			if (playService != null) {
				Log.i("124", "1_connection");
				playService.setCurrentPlayMusic(1);
				playService.setFirstMsg();
				playService.initTextView(tv_songname, tv_singername);
				playService.playAll();
			} else {
				Log.i("124", "1_unconnection");
			}
			image_sp.setImageResource(R.drawable.img_appwidget_play1);
			// setSongMg();
			setFirstView();
			break;
		// 跳转到播放界面
		case R.id.layout_go:
			// Intent intent = new Intent(Rank_DetailActivity.this,
			// PlayInternetMusicActivity.class);
			// startActivity(intent);
			if (playService.getCurrentPlayMusic() == PlayService.LOCAL_MUSIC) {
				Intent intent = new Intent(this, PlayActivity.class);
				startActivity(intent);
			} else if (playService.getCurrentPlayMusic() == PlayService.NET_MUSIC) {
				Intent intent = new Intent(this,
						PlayInternetMusicActivity.class);
				startActivity(intent);
			}
		default:
			break;
		}
	}

	// 获取上个界面传过来的数据
	private String getBundleData() {
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		String name = bundle.getString("name");
		tv_title.setText(bundle.getString("title"));
		return name;
	}

	// 加载网络数据(show_api)
	private void LoadData() {

		load_layout.setVisibility(View.VISIBLE);
		listview_music.setVisibility(View.GONE);
		if (NAME.equals("Raise内地榜")) {
			new ShowApiRequest("http://route.showapi.com/213-4", appid, secret)
					.setResponseHandler(resHandler).addTextPara("topid", "5")
					.post();
		} else if (NAME.equals("Raise热歌榜")) {
			new ShowApiRequest("http://route.showapi.com/213-4", appid, secret)
					.setResponseHandler(resHandler).addTextPara("topid", "26")
					.post();
		}
		if (NAME.equals("Raise销量榜")) {
			new ShowApiRequest("http://route.showapi.com/213-4", appid, secret)
					.setResponseHandler(resHandler).addTextPara("topid", "23")
					.post();
		}
	}

	// 获取网络数据线程实现类
	private class Asynchttp extends AsyncHttpResponseHandler {

		@Override
		public void onFailure(int arg0, Header[] arg1, byte[] arg2,
				Throwable arg3) {
			arg3.printStackTrace();

		}

		@Override
		public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
			load_layout.setVisibility(View.GONE);
			listview_music.setVisibility(View.VISIBLE);
			String json = new String(arg2);
			JSONObject jOb2;
			JSONObject jOb3;
			JSONArray jOa = null;

			try {
				JSONObject jOb;
				jOb = new JSONObject(json);
				jOb2 = jOb.getJSONObject("showapi_res_body");
				jOb3 = jOb2.getJSONObject("pagebean");
				jOa = jOb3.getJSONArray("songlist");

			} catch (Exception e) {
				e.printStackTrace();
			}
			songname = new String[jOa.length()];
			singername = new String[jOa.length()];
			songrank = new String[jOa.length()];
			songurl = new String[jOa.length()];

			album = new String[jOa.length()];
			for (int i = 0; i < jOa.length(); i++) {
				JSONObject jOb_1 = null;
				try {
					jOb_1 = jOa.getJSONObject(i);
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				songrank[i] = String.valueOf(i + 1);
				try {
					String s = jOb_1.getString("singername").toString().trim()
							+ "(";
					singername[i] = s.substring(0, s.indexOf("("));
				} catch (Exception e) {
					singername[i] = "未知歌手";
				}
				try {
					String s = jOb_1.getString("songname").toString().trim()
							+ "(";
					songname[i] = s.substring(0, s.indexOf("("));

				} catch (Exception e) {
					songname[i] = "未知歌曲";

				}
				try {
					songurl[i] = jOb_1.getString("downUrl").toString().trim();
				} catch (Exception e) {

				}
				try {
					Log.i("123", "singername:" + jOb_1.getString("singername"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				try {
					Log.i("123", "songname:" + jOb_1.getString("songname"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				try {
					album[i] = jOb_1.getString("albumpic_big").toString()
							.trim();
				} catch (Exception e) {

				}
			}
			// editor.commit();
			for (int i = 0; i < songname.length; i++) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("singername", singername[i]);
				map.put("songname", songname[i]);
				map.put("songrank", songrank[i]);
				listdata.add(map);
			}
			adapter = new SimpleAdapter(context, listdata,
					R.layout.detail_item, new String[] { "songrank",
							"songname", "singername" }, new int[] {
							R.id.detail_rank, R.id.detail_item_musicname,
							R.id.detail_item_singer });
			listview_music.setAdapter(adapter);

			Intent intent = new Intent(Rank_DetailActivity.this,
					PlayService.class);
			Bundle bundle = new Bundle();
			bundle.putStringArray("url_array", songurl);
			bundle.putInt("play_code", 1);
			bundle.putStringArray("singername", singername);
			bundle.putStringArray("songname", songname);
			bundle.putStringArray("album", album);
			intent.putExtras(bundle);
			startService(intent);
			playService.initTextView(tv_songname, tv_singername);

		}
	}

	// public void setSongMg() {
	// msg = playService.getPlayingSongMsg();
	// if (songname != null) {
	// tv_songname.setText(msg[0]);
	// } else {
	// tv_songname.setText("歌曲");
	// }
	// if (singername != null) {
	// tv_singername.setText(msg[1]);
	// } else {
	// tv_singername.setText("歌手");
	// }
	// app.SetSongMsg(msg[0], msg[1]);
	// }

	// 列表单击播放事件
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		playService.initTextView(tv_songname, tv_singername);
		image_sp.setImageResource(R.drawable.img_appwidget_play1);
		app.setPosition(position);
		if (playService != null) {
			Log.i("124", "1_connection");
			playService.setCurrentPlayMusic(1);
			playService.setFirstMsg();
			playService.playMusic(-1);
		} else {
			Log.i("124", "1_unconnection");
		}
		// setSongMg();
		setFirstView();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

	}

	// 同步图标
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		setFirstView();
	}

	@Override
	public void publish(int progress) {
		// TODO Auto-generated method stub

	}

	@Override
	public void change(int position) {
		// TODO Auto-generated method stub

		setFirstView();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unbindPlayService();
	}

}
