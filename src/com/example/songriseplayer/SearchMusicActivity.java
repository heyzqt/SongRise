package com.example.songriseplayer;

import java.util.HashMap;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

/*
 * 搜索界面
 * @author:
 */
public class SearchMusicActivity extends BaseActivity implements
		OnClickListener {

	private static final String Searchresult = null;
	private LinearLayout l_back;
	private Button bt1, bt2, bt3, bt4, bt5, bt6, bt7, bt8, bt9;
	private View v;
	private int bt_hot[];
	private AsyncHttpResponseHandler resHandler;
	private String appid = "16950";
	private String secret = "eac11e3d0cab4def9da973eb78f97512";
	private EditText ed_searchkey;
	private ImageView image_search;

	private LinearLayout layout_go;

	private ImageView image_sp;
	private ImageView image_next;
	private ImageView img_album;
	private TextView tv_songname;
	private TextView tv_singername;
	private Context context;
	public int code;
	private int currentPostion;
	private String msg[] = new String[2];
	private LinearLayout rank_playall;
	private Mp3Info mp3Info;
	private LinearLayout layout_load;
	private LinearLayout layout_search;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.search_music_activity);
		bindPlayService();
		initView();
		initEvent();

		// 设置标题栏颜色
		AppUtils.setSystemStatusBar(SearchMusicActivity.this);
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
		if (app.sp.getInt(Constant.CURRENT_PLAY_MUSIC, 0) == 0) {
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
		} else if (app.sp.getInt(Constant.CURRENT_PLAY_MUSIC, 0) == 1) {
			if (app.getCode() == -1) {
				image_sp.setImageResource(R.drawable.img_appwidget_pause1);
			} else if (app.getCode() == 1) {
				image_sp.setImageResource(R.drawable.img_appwidget_play1);
			}
			tv_songname.setText(app.getSongName());
			tv_singername.setText(app.getSingerName());
			DownloadUtils.getInstance().ShowImg(app.getAlbum(),img_album, handler,app.getSongName());
		}
	}

	private void initView() {
		app = (SongRisePlayerApp) getApplication();
		v = findViewById(R.id.search_input);
		v.getBackground().setAlpha(100);
		v = findViewById(R.id.search_hot);
		v.getBackground().setAlpha(20);

		layout_load = (LinearLayout) findViewById(R.id.load_layout);
		layout_search = (LinearLayout) findViewById(R.id.search_layout);

		tv_songname = (TextView) findViewById(R.id.textview1_title);
		tv_singername = (TextView) findViewById(R.id.textview2_singer_name);

		img_album = (ImageView) findViewById(R.id.imageview1_album);
		image_sp = (ImageView) findViewById(R.id.image_sp);

		image_next = (ImageView) findViewById(R.id.image_next);
		image_next.setImageResource(R.drawable.img_appwidget_play_next);
		image_next.setOnClickListener(this);
		image_sp.setOnClickListener(this);
		l_back = (LinearLayout) findViewById(R.id.search_back);
		l_back.setOnClickListener(this);
		image_search = (ImageView) findViewById(R.id.image_search);
		image_search.setOnClickListener(this);
		resHandler = new Asynchttp();
		context = this;
		layout_go = (LinearLayout) findViewById(R.id.layout_go);
		layout_go.setOnClickListener(this);
		bt1 = (Button) findViewById(R.id.button1);
		bt2 = (Button) findViewById(R.id.button2);
		bt3 = (Button) findViewById(R.id.button3);
		bt4 = (Button) findViewById(R.id.button4);
		bt5 = (Button) findViewById(R.id.button5);
		bt6 = (Button) findViewById(R.id.button6);
		bt7 = (Button) findViewById(R.id.button7);
		bt8 = (Button) findViewById(R.id.button8);
		bt9 = (Button) findViewById(R.id.button9);
		bt1.setOnClickListener(this);
		bt2.setOnClickListener(this);
		bt3.setOnClickListener(this);
		bt4.setOnClickListener(this);
		bt5.setOnClickListener(this);
		bt6.setOnClickListener(this);
		bt7.setOnClickListener(this);
		bt8.setOnClickListener(this);
		bt9.setOnClickListener(this);
		bt_hot = new int[] { R.id.button1, R.id.button2, R.id.button3,
				R.id.button4, R.id.button5, R.id.button6, R.id.button7,
				R.id.button8, R.id.button9, };
		ed_searchkey = (EditText) findViewById(R.id.search_input);
		try {
			playService.initTextView(tv_songname, tv_singername);
		} catch (Exception e) {
			// TODO: handle exception
			L.e("initTextView---bug");
		}
		setFirstView();
	}

	private void initEvent() {
		if (IsConnectNet.checkNetworkAvailable(this)) {
			layout_load.setVisibility(View.VISIBLE);
			layout_search.setVisibility(View.GONE);
			new ShowApiRequest("http://route.showapi.com/213-4", appid, secret)
					.setResponseHandler(resHandler).addTextPara("topid", "26")
					.addTextPara("page", "").post();
		} else {
			Toast.makeText(this, "唉，没有网哦-。-", Toast.LENGTH_SHORT).show();

		}
	}

	private class Asynchttp extends AsyncHttpResponseHandler {
		@Override
		public void onFailure(int arg0, Header[] arg1, byte[] arg2,
				Throwable arg3) {
			arg3.printStackTrace();
		}

		@Override
		public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
			layout_load.setVisibility(View.GONE);
			layout_search.setVisibility(View.VISIBLE);
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
			String songname[] = new String[20];
			int j = 0;
			for (int i = 0; i < 20; i++) {

				JSONObject jOb_1 = null;
				try {
					jOb_1 = jOa.getJSONObject(i);
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				try {
					String s = jOb_1.getString("songname").toString().trim()
							+ "(";
					songname[j] = s.substring(0, s.indexOf("("));
				} catch (Exception e) {
					j--;
				}
				j++;

			}
			bt1.setText(songname[0]);
			bt2.setText(songname[1]);
			bt3.setText(songname[2]);
			bt4.setText(songname[3]);
			bt5.setText(songname[4]);
			bt6.setText(songname[5]);
			bt7.setText(songname[6]);
			bt8.setText(songname[7]);
			bt9.setText(songname[8]);
			playService.initTextView(tv_songname, tv_singername);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.search_back:
			finish();
			break;

		case R.id.image_search:

			if (IsConnectNet.checkNetworkAvailable(context)) {
				if (ed_searchkey.getText().toString().equals("")) {
					Toast.makeText(context, "请输入搜索条件", Toast.LENGTH_SHORT)
							.show();
				} else {
					Intent intent = new Intent(SearchMusicActivity.this,
							SearchresultActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("key", ed_searchkey.getText().toString());
					intent.putExtras(bundle);
					startActivity(intent);
				}

			} else {
				Toast.makeText(this, "唉，没有网的日子好难过-。-", Toast.LENGTH_SHORT)
						.show();
			}
			break;

		// 播放/暂停图标设置与播放/暂停歌曲
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
		// 下一曲
		case R.id.image_next:
			playService.initTextView(tv_songname, tv_singername);
			if (app.sp.getInt(Constant.CURRENT_PLAY_MUSIC, 0) == playService.LOCAL_MUSIC) {
				playService.next();
			} else if (app.sp.getInt(Constant.CURRENT_PLAY_MUSIC, 0) == playService.NET_MUSIC) {
				playService.next();
				// setSongMg();
				setFirstView();
			}
			image_sp.setImageResource(R.drawable.img_appwidget_play1);

			break;

		case R.id.layout_go:
			// 跳转到播放界面
//			Intent intent = new Intent(this, PlayInternetMusicActivity.class);
//			startActivity(intent);
			if (playService.getCurrentPlayMusic() == PlayService.LOCAL_MUSIC) {
				Intent intent = new Intent(this, PlayActivity.class);
				startActivity(intent);
			} else if (playService.getCurrentPlayMusic() == PlayService.NET_MUSIC) {
				Intent intent = new Intent(this,
						PlayInternetMusicActivity.class);
				startActivity(intent);
			}
			break;
		case R.id.button1:
			setHotSearch(bt1);
			break;
		case R.id.button2:
			setHotSearch(bt2);
			break;
		case R.id.button3:
			setHotSearch(bt3);
			break;
		case R.id.button4:
			setHotSearch(bt4);
			break;
		case R.id.button5:
			setHotSearch(bt5);
			break;
		case R.id.button6:
			setHotSearch(bt6);
			break;
		case R.id.button7:
			setHotSearch(bt7);
			break;
		case R.id.button8:
			setHotSearch(bt8);
			break;
		case R.id.button9:
			setHotSearch(bt9);
			break;
		default:
			break;
		}
	}

	public void setHotSearch(Button bt) {
		if (bt.getText().toString().equals("")) {
			Toast.makeText(context, "请输入搜索条件", Toast.LENGTH_SHORT).show();
		} else {
			Intent intent = new Intent(SearchMusicActivity.this,
					SearchresultActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("key", bt.getText().toString());
			intent.putExtras(bundle);
			startActivity(intent);
		}
	}

	// public void setSongMg()
	// {
	// msg=playService.getPlayingSongMsg();
	// if(msg!=null)
	// {
	// tv_songname.setText(msg[0]);
	// }
	// else
	// {
	// tv_songname.setText("-");
	// }
	// if(msg!=null)
	// {
	// tv_singername.setText(msg[1]);
	// }
	// else
	// {
	// tv_singername.setText("-");
	// }
	// app.SetSongMsg(msg[0],msg[1]);
	// }
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

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unbindPlayService();
	}

}
