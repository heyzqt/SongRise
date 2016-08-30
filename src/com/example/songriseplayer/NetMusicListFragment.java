package com.example.songriseplayer;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.app.Constant;
import com.example.app.SongRisePlayerApp;
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
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

/*
 * 网络推荐排行榜
 * @author:
 */

public class NetMusicListFragment extends Fragment implements
		OnItemClickListener, OnClickListener {

	private MainActivity mainActivity;
	private ListView list_main;
	private View v;
	private ArrayList<HashMap<String, Object>> listdata = new ArrayList<HashMap<String, Object>>();
	private int[] image;
	private String[] title;
	private SimpleAdapter adapter;
	private ImageView image_sp;
	private ImageView image_next;
	private TextView tv_songname;
	private TextView tv_singername;
	private int code;
	private int currentPostion;
	private String msg[] = new String[2];
	private LinearLayout layout_go;
	private String rank_first[] = new String[3];
	private String rank_sec[] = new String[3];
	private String rank_trd[] = new String[3];
	private String appid = "16950";
	private String secret = "eac11e3d0cab4def9da973eb78f97512";
	private AsyncHttpResponseHandler resHandler1;
	private AsyncHttpResponseHandler resHandler2;
	private AsyncHttpResponseHandler resHandler3;
	private LinearLayout load_layout;

	private ImageView img_album;
	private Mp3Info mp3Info;
	private SongRisePlayerApp app;
	public ImageView imageview_no_internet;

	// 连接服务，获取服务实例
	public static NetMusicListFragment newInstance() {
		NetMusicListFragment net = new NetMusicListFragment();
		return net;
	}

	@Override
	public void onAttach(Context context) {
		// TODO Auto-generated method stub
		super.onAttach(context);
		mainActivity = (MainActivity) context;
	}

	// 初始化界面组件
	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.rank_main, null);
		mainActivity.bindPlayService();

		// 初始化组件
		initView(view);
		// 请求网络数据
		initNetData();
		// //为列表填装数据
		// setListdata();
		// //设置事件
		// initEvent();
		list_main.setOnItemClickListener(this);
		return view;
	}

	public void initNetData() {

		if (IsConnectNet.checkNetworkAvailable(mainActivity)) {
			imageview_no_internet.setVisibility(View.GONE);
			load_layout.setVisibility(View.VISIBLE);
			list_main.setVisibility(View.GONE);

			resHandler1 = new Asynchttp();
			resHandler2 = new Asynchttp();
			resHandler3 = new Asynchttp();
			((Asynchttp) resHandler1).setCode(1);
			new ShowApiRequest("http://route.showapi.com/213-4", appid, secret)
					.setResponseHandler(resHandler1).addTextPara("topid", "5")
					.post();

			((Asynchttp) resHandler2).setCode(2);
			new ShowApiRequest("http://route.showapi.com/213-4", appid, secret)
					.setResponseHandler(resHandler2).addTextPara("topid", "26")
					.post();

			((Asynchttp) resHandler3).setCode(3);
			new ShowApiRequest("http://route.showapi.com/213-4", appid, secret)
					.setResponseHandler(resHandler3).addTextPara("topid", "23")
					.post();
		}

		else {
			imageview_no_internet.setVisibility(View.VISIBLE);
			Toast.makeText(mainActivity, "唉，没有网哦-。-", Toast.LENGTH_SHORT)
					.show();
		}

	}

	// 获取网络数据线程实现类
	private class Asynchttp extends AsyncHttpResponseHandler {
		private int net_code;

		public void setCode(int code) {
			net_code = code;
		}

		@Override
		public void onFailure(int arg0, Header[] arg1, byte[] arg2,
				Throwable arg3) {
			arg3.printStackTrace();
			Toast.makeText(mainActivity, "网络异常,请检查网络连接", Toast.LENGTH_SHORT)
					.show();
		}

		@Override
		public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
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
			if (net_code == 1) {
				JSONObject jOb_1 = null;
				try {
					jOb_1 = jOa.getJSONObject(0);
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				try {
					String s = jOb_1.getString("songname").toString().trim()
							+ "(";
					rank_first[0] = "1:" + s.substring(0, s.indexOf("("));
				} catch (Exception e) {
					rank_first[0] = "1:" + "未知歌曲";

				}

				try {
					jOb_1 = jOa.getJSONObject(1);
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				try {
					String s = jOb_1.getString("songname").toString().trim()
							+ "(";
					rank_sec[0] = "2:" + s.substring(0, s.indexOf("("));
				} catch (Exception e) {
					rank_sec[0] = "2:" + "未知歌曲";

				}

				try {
					jOb_1 = jOa.getJSONObject(2);
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				try {
					String s = jOb_1.getString("songname").toString().trim()
							+ "(";
					rank_trd[0] = "3:" + s.substring(0, s.indexOf("("));
				} catch (Exception e) {
					rank_trd[0] = "3:" + "未知歌曲";

				}
			} else if (net_code == 2) {
				JSONObject jOb_1 = null;
				try {
					jOb_1 = jOa.getJSONObject(0);
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				try {
					String s = jOb_1.getString("songname").toString().trim()
							+ "(";
					rank_first[1] = "1:" + s.substring(0, s.indexOf("("));
				} catch (Exception e) {
					rank_first[1] = "1:" + "未知歌曲";

				}

				try {
					jOb_1 = jOa.getJSONObject(1);
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				try {
					String s = jOb_1.getString("songname").toString().trim()
							+ "(";
					rank_sec[1] = "2:" + s.substring(0, s.indexOf("("));
				} catch (Exception e) {
					rank_sec[1] = "2:" + "未知歌曲";

				}

				try {
					jOb_1 = jOa.getJSONObject(2);
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				try {
					String s = jOb_1.getString("songname").toString().trim()
							+ "(";
					rank_trd[1] = "3:" + s.substring(0, s.indexOf("("));
				} catch (Exception e) {
					rank_trd[1] = "3:" + "未知歌曲";

				}
			} else if (net_code == 3) {
				JSONObject jOb_1 = null;
				try {
					jOb_1 = jOa.getJSONObject(0);
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				try {
					String s = jOb_1.getString("songname").toString().trim()
							+ "(";
					rank_first[2] = "1:" + s.substring(0, s.indexOf("("));
				} catch (Exception e) {
					rank_first[2] = "1:" + "未知歌曲";

				}

				try {
					jOb_1 = jOa.getJSONObject(1);
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				try {
					String s = jOb_1.getString("songname").toString().trim()
							+ "(";
					rank_sec[2] = "2:" + s.substring(0, s.indexOf("("));
				} catch (Exception e) {
					rank_sec[2] = "2:" + "未知歌曲";

				}

				try {
					jOb_1 = jOa.getJSONObject(2);
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				try {
					String s = jOb_1.getString("songname").toString().trim()
							+ "(";
					rank_trd[2] = "3:" + s.substring(0, s.indexOf("("));
				} catch (Exception e) {
					rank_trd[2] = "3:" + "未知歌曲";
				}
			}
			for (int i = 0; i < 3; i++) {
				if (rank_first[0] == null || rank_first[1] == null
						|| rank_first[2] == null) {
					break;
				} else {
					// Log.i("124","1:"+String.valueOf(net_code)+rank_first[i]);
					// Log.i("124","2:"+String.valueOf(net_code)+rank_sec[i]);
					// Log.i("124","3:"+String.valueOf(net_code)+rank_trd[i]);
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("image", image[i]);
					map.put("title", title[i]);
					map.put("tv_first", rank_first[i]);
					map.put("tv_sec", rank_sec[i]);
					map.put("tv_trd", rank_trd[i]);
					listdata.add(map);
					adapter = new SimpleAdapter(getActivity(), listdata,
							R.layout.rank_listitem, new String[] { "image",
									"title", "tv_first", "tv_sec", "tv_trd" },
							new int[] { R.id.image_1, R.id.tv_title,
									R.id.tv_first, R.id.tv_sec, R.id.tv_trd });
					list_main.setAdapter(adapter);
					load_layout.setVisibility(View.GONE);
					list_main.setVisibility(View.VISIBLE);
				}
			}
		}
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
		// Log.i("124","net_setFisrtView:"+String.valueOf(app.getCode()));
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
			Bitmap albumBitmap = MediaUtils.getArtWork(mainActivity,
					mp3Info.getId(), mp3Info.getAlbumId(), true, true);
			img_album.setImageBitmap(albumBitmap);
		} else if (app.sp.getInt(Constant.CURRENT_PLAY_MUSIC, 0) == 1) {
			Log.i("125", "netMusic");
			if (app.getCode() == -1) {
				image_sp.setImageResource(R.drawable.img_appwidget_pause1);
			} else if (app.getCode() == 1) {
				image_sp.setImageResource(R.drawable.img_appwidget_play1);
			}
			Log.i("125", "app.getSongName()：" + app.getSongName());
			tv_songname.setText(app.getSongName());
			tv_singername.setText(app.getSingerName());
			DownloadUtils.getInstance().ShowImg(app.getAlbum(), img_album,
					handler, app.getSongName());
		}
	}

	private void initView(View view) {
		app = (SongRisePlayerApp) mainActivity.getApplication();
		img_album = (ImageView) view.findViewById(R.id.Imageview1_album);
		load_layout = (LinearLayout) view.findViewById(R.id.rank_load_layout);
		tv_songname = (TextView) view.findViewById(R.id.textview1_title);
		tv_singername = (TextView) view
				.findViewById(R.id.textview2_singer_name);
		image_sp = (ImageView) view.findViewById(R.id.image_sp);
		image_sp.setOnClickListener(this);
		image_next = (ImageView) view.findViewById(R.id.image_next);
		image_next.setOnClickListener(this);
		image_next.setImageResource(R.drawable.img_appwidget_play_next);
		list_main = (ListView) view.findViewById(R.id.list_main);
		layout_go = (LinearLayout) view.findViewById(R.id.layout_go);
		imageview_no_internet = (ImageView) view
				.findViewById(R.id.imageview_no_internet);
		layout_go.setOnClickListener(this);
		// image = new int[] { R.drawable.a1, R.drawable.a2, R.drawable.a3 };
		image = new int[] { R.drawable.local_music, R.drawable.hot_music,
				R.drawable.soar_music };
		title = new String[] { "Raise内地榜", "Raise热歌榜", "Raise销量榜" };
		try {
			mainActivity.playService.initTextView(tv_songname, tv_singername);
		} catch (Exception e) {
			// TODO: handle exception
			L.e("netfragment-----bug");
		}
		// Log.i("124","playcode:"+String.valueOf(mainActivity.playService.getCurrentPlayMusic()));
		// L.e("netfragment,oncreate,setFirstView()");
		setFirstView();
	}

	// 列表单击跳转事件
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent(getActivity(), Rank_DetailActivity.class);
		Bundle bundle = new Bundle();

		if (listdata.get(position).get("title").equals("Raise内地榜")) {
			bundle.putString("name", title[0]);
			bundle.putString("title", "内地榜");
		} else if (listdata.get(position).get("title").equals("Raise热歌榜")) {
			bundle.putString("name", title[1]);
			bundle.putString("title", "热歌榜");
		} else if (listdata.get(position).get("title").equals("Raise销量榜")) {
			bundle.putString("name", title[2]);
			bundle.putString("title", "销量榜");
		}
		intent.putExtras(bundle);
		startActivity(intent);
	}

	// public void setSongMg() {
	// msg = mainActivity.playService.getPlayingSongMsg();
	//
	// L.e("getplayingmusic" + msg[0]);
	//
	// if (msg != null) {
	// tv_songname.setText(msg[0]);
	// } else {
	// tv_songname.setText("歌曲");
	// }
	// if (msg != null) {
	// tv_singername.setText(msg[1]);
	// } else {
	// tv_singername.setText("歌手");
	// }
	// //app.SetSongMsg(msg[0], msg[1]);
	// setFirstView();
	//
	// // 专辑
	// img_album.setImageResource(R.drawable.music_icon);
	//
	// }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 设置播放/暂停的图标并完成播放/暂停功能
		case R.id.image_sp:
			mainActivity.playService.initTextView(tv_songname, tv_singername);
			// Log.i("127", "sp_code:" + String.valueOf(app.getCode()));
			if (app.sp.getInt(Constant.CURRENT_PLAY_MUSIC, 0) == mainActivity.playService.NET_MUSIC) {
				if (app.getCode() == 1 && mainActivity.playService.isPlaying()) {
					mainActivity.playService.pause();
					image_sp.setImageResource(R.drawable.img_appwidget_pause1);
					// Log.i("127", "net pause");
				} else {
					// Log.i("127", "net start");
					image_sp.setImageResource(R.drawable.img_appwidget_play1);
					if (app.getCode() == -1
							&& mainActivity.playService.isPause()) {
						mainActivity.playService.startMusic();

					} else {
						// Log.i("127", "net start else");

						mainActivity.playService.playMusic(-1);

					}
				}
			} else {
				if ((app.getCode() == 1 && mainActivity.playService.isPlaying())) {
					image_sp.setImageResource(R.drawable.img_appwidget_pause1);
					mainActivity.playService.pause();
					// isPause = true;
				} else {
					image_sp.setImageResource(R.drawable.img_appwidget_play1);
					if (mainActivity.playService.isPause()) {
						mainActivity.playService.start();
					} else {
						// mainActivity.playService.play(mainActivity.playService
						// .getCurrentPosition());
						mainActivity.playService
								.setCurrentPlayMusic(PlayService.LOCAL_MUSIC);
						mainActivity.playService
								.playMusic(mainActivity.playService
										.getCurrentPosition());
					}
				}
			}
			break;
		// 下一曲
		case R.id.image_next:

			mainActivity.playService.initTextView(tv_songname, tv_singername);
			if (app.sp.getInt(Constant.CURRENT_PLAY_MUSIC, 0) == mainActivity.playService.LOCAL_MUSIC) {

				mainActivity.playService.next();
				// L.e("netfragment,next's onclick,setFirstView()");
				setFirstView();

			} else if (app.sp.getInt(Constant.CURRENT_PLAY_MUSIC, 0) == mainActivity.playService.NET_MUSIC) {
				mainActivity.playService.next();
				setFirstView();
				image_sp.setImageResource(R.drawable.img_appwidget_play1);
			}
			break;
		// 跳转到播放界面
		case R.id.layout_go:
			if (mainActivity.playService.getCurrentPlayMusic() == PlayService.LOCAL_MUSIC) {
				Intent intent = new Intent(mainActivity, PlayActivity.class);
				startActivity(intent);
			} else if (mainActivity.playService.getCurrentPlayMusic() == PlayService.NET_MUSIC) {
				Intent intent = new Intent(mainActivity,
						PlayInternetMusicActivity.class);
				startActivity(intent);
			}
//			Intent intent = new Intent(mainActivity,
//					PlayInternetMusicActivity.class);
//			startActivity(intent);
		default:
			break;
		}
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		// L.e("netfragment,onstart,setFirstView()");
		setFirstView();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mainActivity.unbindPlayService();
	}

	public void changeUIStatusOnPlay() {

		mainActivity.playService.isMainActivity = true;
		// code 1 播放 -1 暂停
		if (SongRisePlayerApp.sp_msg.getInt("code", -1) == 1) {
			image_sp.setImageResource(R.drawable.img_appwidget_pause);
		} else {
			image_sp.setImageResource(R.drawable.img_appwidget_play);
		}
		// setSongMg();
		// setFirstView();
	}
}
