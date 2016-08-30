package com.example.songriseplayer;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.app.SongRisePlayerApp;
import com.example.utils.DownloadUtils;
import com.example.utils.L;
import com.example.utils.MediaUtils;
import com.example.vo.Mp3Info;
import com.example.vo.Mp3InfoShowApi;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.show.api.ShowApiRequest;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;

/**
 * 启动界面
 * 
 * @author zq,zyq
 * 
 */
public class SplashActivity extends Activity {

	private final static int START_ACTIVITY = 0x1;
	private final static int SAVE_MUSIC = 0x2;
	private final static int SAVE_MUSIC_NO_INTERNET = 0x3;
	private ArrayList<Mp3Info> mp3Infos;
	private SongRisePlayerApp app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash_layout);

		init();
	}

	private void init() {

		app = (SongRisePlayerApp) getApplicationContext();
		startService(new Intent(this, PlayService.class));
		handler.sendEmptyMessageDelayed(START_ACTIVITY, 2000);
		
		
		// 扫描本地歌曲 保存本地歌曲在showapi的music---id
		mp3Infos = MediaUtils.getMp3Infos(this);
		for (int i = 0; i < mp3Infos.size(); i++) {
			Mp3Info mp3Info = mp3Infos.get(i);
			MusicShowId(mp3Info.getTitle(), mp3Info.getArtist());
		}
		
		//扫描网络音乐 
		

		// zyq
		Editor editor1 = SongRisePlayerApp.sp.edit();
		editor1.putString("sleep", "-1");
		editor1.putString("wifi_only", "no");
		editor1.putString("weibos", "no");
		editor1.commit();
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case START_ACTIVITY:
				startActivity(new Intent(SplashActivity.this,
						MainActivity.class));
				finish();
				break;
			case SAVE_MUSIC: {

				// 解析message数据
				Bundle getBundle = msg.getData();
				String mp3InfoshowId = getBundle.getString("mp3InfoshowId");
				String musicname = getBundle.getString("musicname");
				String singername = getBundle.getString("singername");
				Mp3InfoShowApi mp3InfoShowApi = new Mp3InfoShowApi();
				if(mp3InfoshowId==null){
					
					//L.e("mp3InfoshowId为空");
					mp3InfoShowApi.setMp3InfoshowId(Long.parseLong("0"));
				}
				else{
					mp3InfoShowApi.setMp3InfoshowId(Long.parseLong(mp3InfoshowId));
				}
				mp3InfoShowApi.setMusicname(musicname);
				mp3InfoShowApi.setSingername(singername);
				try {
					// 遍历数据库,如果mp3InfoShowApi存在就update，不存在就save
					Mp3InfoShowApi find_mp3ShowApi=app.dbUtils.findFirst(Selector.from(Mp3InfoShowApi.class)
							.where("musicname", "=", musicname)
							.and("singername", "=", singername));
					//L.e("mp3InfoShowApi===" + mp3InfoShowApi);
					//L.e("find_mp3ShowApi===" + find_mp3ShowApi);
					if(find_mp3ShowApi==null){
						
						app.dbUtils.save(mp3InfoShowApi);
						//L.e("save");
					}
					else{
						find_mp3ShowApi.setMp3InfoshowId(mp3InfoShowApi.getMp3InfoshowId());
						app.dbUtils.update(find_mp3ShowApi, "mp3InfoshowId");
						//L.e("update");
					}
					
				} catch (DbException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					L.e("保存失败");
				}
				// saveMusic(mp3Info);
				break;
			}
			default:
				break;
			}

		}

	};

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
							//System.out.println("" + min_body);

							try {
								if (singername.equals("")) {
									//L.e(min_body.getString("singername") + "");
									DownloadUtils.mp3InfoshowId = min_body
											.getString("songid");
									break;
								} else if (min_body.getString("singername")
										.equals(singername)) {
									//L.e(min_body.getString("singername") + "");
									DownloadUtils.mp3InfoshowId = min_body
											.getString("songid");
									break;
								}
//								else {
//									//L.e(min_body.getString("singername") + "");
//									//DownloadUtils.mp3InfoshowId = "0";
//								}
							} catch (Exception e) {
								// TODO: handle exception
								//L.e("没有musicname字段或singername字段");
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

				//Log.e("TAG", "onsuccess方法中的mp3InfoshowId=" + DownloadUtils.mp3InfoshowId);
				Message msg = Message.obtain();
				msg.what = SAVE_MUSIC;
				Bundle bundle = new Bundle();
				bundle.putString("mp3InfoshowId", DownloadUtils.mp3InfoshowId);
				bundle.putString("musicname", musicname);
				bundle.putString("singername", singername);
				msg.setData(bundle);
				handler.sendMessage(msg);
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
				Log.e("TAG", "连接网络获取歌曲id失败----music=" + DownloadUtils.mp3InfoshowId);
				Message msg = Message.obtain();
				msg.what = SAVE_MUSIC;
				Bundle bundle = new Bundle();
				bundle.putString("mp3InfoshowId", DownloadUtils.mp3InfoshowId);
				bundle.putString("musicname", musicname);
				bundle.putString("singername", singername);
				msg.setData(bundle);
				handler.sendMessage(msg);
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
