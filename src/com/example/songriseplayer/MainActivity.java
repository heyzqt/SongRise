/*
 * Copyright (C) 2013 Andreas Stuetz <andreas.stuetz@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.songriseplayer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.Volley;
import com.astuetz.PagerSlidingTabStrip;
import com.astuetz.viewpager.extensions.sample.QuickContactFragment;
import com.astuetz.viewpager.extensions.sample.SuperAwesomeCardFragment;
import com.example.app.Constant;
import com.example.app.SongRisePlayerApp;
import com.example.songriseplayer.PlayService.MusicUpdateListener;
import com.example.utils.AccessTokenKeeper;
import com.example.utils.AppUtils;
import com.example.utils.Demo;
import com.example.utils.DownloadUtils;
import com.example.utils.L;
import com.example.utils.MediaUtils;
import com.example.utils.T;
import com.example.utils.User;
import com.example.vo.UsersAPI;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;

/**
 * songrise主界面
 * 
 * @author zq,zyq
 * 
 */
@SuppressLint("NewApi")
public class MainActivity extends BaseActivity implements OnClickListener {

	private final Handler handler = new Handler();

	private PagerSlidingTabStrip tabs;
	private ViewPager pager;
	private MyPagerAdapter adapter;

	private Drawable oldBackground = null;
	private int currentColor = 0xFF666666;

	private MyMusicListFragment myMusicFragment;
	private NetMusicListFragment netMusicFragment;

	private ImageView imageview1_search;
	private ImageView img_user_menu;
	// private SlidingMenu mMenu;
	private DrawerLayout mDrawerMenu;
	private LinearLayout linearlayout1;
	// public static final String TAG = "CodingPlayer"; // 可供全局使用的TAG

	public ArrayList music_song_list; // 从歌单管理界面传递过来的歌单列表
	// 强转从歌单界面传过来的list
	public List<List<Map<String, String>>> listSave = new ArrayList<List<Map<String, String>>>();
	public int listSize; // 保存歌单管理界面传递过来的list的长度

	// 侧滑菜单上的控件 ---zyq
	private LinearLayout Liner_user;
	private ImageView imageview_user;
	private TextView text_userame;

	private RelativeLayout Rl_wifi;
	private ImageView kaiguan;

	private RelativeLayout Rl_sleep;
	private RelativeLayout Rl_lock;
	private RelativeLayout Rl_setting;
	private RelativeLayout Rl_scan;

	private TextView text_tuichu;
	private ImageView image_tuichu;

	private TextView text_sleep_time;
	private Handler mHandler = new Handler();// 全局handle
	int i = 0;// 倒计时的整个时间数
	int i_min = 0;
	int i_second = 0;
	String url, url1;

	private Oauth2AccessToken mAccessToken;
	private UsersAPI mUsersAPI;// zyq

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		pager = (ViewPager) findViewById(R.id.pager);
		adapter = new MyPagerAdapter(getSupportFragmentManager());
		imageview1_search = (ImageView) findViewById(R.id.imageview1_search);
		img_user_menu = (ImageView) findViewById(R.id.img_user_menu);
		mDrawerMenu = (DrawerLayout) findViewById(R.id.id_menu);
		imageview_user = (ImageView) findViewById(R.id.imageview_user);
		linearlayout1 = (LinearLayout) findViewById(R.id.linearlayout1);

		pager.setAdapter(adapter);

		final int pageMargin = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
						.getDisplayMetrics());
		pager.setPageMargin(pageMargin);

		tabs.setViewPager(pager);
		tabs.setTextColor(Color.rgb(255, 255, 255));

		imageview1_search.setOnClickListener(this);
		imageview_user.setOnClickListener(this);
		img_user_menu.setOnClickListener(this);

		// System.out.println("MainActivity==="+playService);
		// changeColor(currentColor);

		// bindPlayService();
		
		//设置标题栏颜色
		AppUtils.setSystemStatusBar(MainActivity.this);

		// zyq
		initview();
		init1View();

	}

	// public int getListSize() {
	// return listSize;
	// }
	//
	// public void setListSize(int listSize) {
	// this.listSize = listSize;
	// }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case R.id.action_contact:

			// //跳转到搜索界面
			// startActivity(new
			// Intent(MainActivity.this,SearchMusicActivity.class));
			// return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		//L.e("mainactivity---onpause");
		playService.isMainActivity=false;
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		
		//L.e("mainactivity---onstop");
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		//L.e("mainactivity---ondestroy");
		
//		SongRisePlayerApp app = (SongRisePlayerApp) getApplication();
//		// 保存用户数据
//		SharedPreferences.Editor editor = app.sp.edit();
//		editor.putInt(Constant.CURRENT_POSITION,
//				playService.getCurrentPosition());
//		editor.putInt(Constant.PLAY_MODE, playService.getPlay_Mode());
//	    editor.commit();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("currentColor", currentColor);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		currentColor = savedInstanceState.getInt("currentColor");
	}

	private Drawable.Callback drawableCallback = new Drawable.Callback() {
		@Override
		public void invalidateDrawable(Drawable who) {
			getActionBar().setBackgroundDrawable(who);
		}

		@Override
		public void scheduleDrawable(Drawable who, Runnable what, long when) {
			handler.postAtTime(what, when);
		}

		@Override
		public void unscheduleDrawable(Drawable who, Runnable what) {
			handler.removeCallbacks(what);
		}
	};

	public class MyPagerAdapter extends FragmentPagerAdapter {

		private final String[] TITLES = { "我的音乐", "网络推荐" };

		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return TITLES[position];
		}

		@Override
		public int getCount() {
			return TITLES.length;
		}

		@Override
		public Fragment getItem(int position) {
			if (position == 0) {
				if (myMusicFragment == null) {
					myMusicFragment = MyMusicListFragment.newInstance();
				}
				return myMusicFragment;
			} else if (position == 1) {
				if (netMusicFragment == null) {
					netMusicFragment = NetMusicListFragment.newInstance();
				}
				return netMusicFragment;
			}
			return null;
		}

	}

	// 更新进度条
	@Override
	public void publish(int progress) {
		// TODO Auto-generated method stub

	}

	// 切换状态播放位置
	@Override
	public void change(int position) {
		// TODO Auto-generated method stub zx 
		
		//L.e("change()  更新UI");
		
		//L.e("MainActivity====="+playService.isMainActivity);
		
		if (pager.getCurrentItem() == 0) {

			L.e("musicfragment");
			myMusicFragment.loadData();
			myMusicFragment.changeUIStatusOnPlay(position);
			if(netMusicFragment.imageview_no_internet.getVisibility()==View.VISIBLE){
				netMusicFragment.initNetData();
			}
			try {
				netMusicFragment.changeUIStatusOnPlay();
				if(playService.getCurrentPlayMusic()==PlayService.NET_MUSIC){
					//netMusicFragment.setSongMg();
					netMusicFragment.setFirstView();
				}
				else{
					
					//L.e("MainAcitivity,change,setFirstView()");
					netMusicFragment.setFirstView();
				}
				//netMusicFragment.setSongMg();
			} catch (Exception e) {
				// TODO: handle exception
				L.e("change ui 报错 myMusicFragment");
			}
			
			
		} else if (pager.getCurrentItem() == 1) {
			
			try {
				L.e("netMusicFragment");
				myMusicFragment.changeUIStatusOnPlay(position);
				netMusicFragment.changeUIStatusOnPlay();
				if(netMusicFragment.imageview_no_internet.getVisibility()==View.VISIBLE){
					netMusicFragment.initNetData();
				}
				if(playService.getCurrentPlayMusic()==PlayService.NET_MUSIC){
					//netMusicFragment.setSongMg();
					netMusicFragment.setFirstView();
					//netMusicFragment.initNetData();
				}
				else{
					netMusicFragment.setFirstView();
				}
			} catch (Exception e) {
				// TODO: handle exception
				L.e("changeUI报错 netMusicFragment");
			}
			
			//netMusicFragment.setSongMg();
		}
	}

	/**
	 * 获取从歌单管理界面返回的list
	 * 
	 * @return List<List<Map<String, String>>>
	 * 
	 *         zq
	 */
	public List<List<Map<String, String>>> getSongListData() {

		try {

			Bundle bundle = getIntent().getExtras();
			music_song_list = bundle.getParcelableArrayList("songlist");

			// list2=
			// (List<Object>)list.get(0);//强转成你自己定义的list，这样list2就是你传过来的那个list了。
			listSave = (ArrayList<List<Map<String, String>>>) music_song_list
					.get(0);
			// listSize = listSave.size();

			// 转换成全局通用的list格式,去掉songmap_checked
			for (int i = 0; i < listSave.size(); i++) {
				// s 使用startactivityforresult来接收数据
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return listSave;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.imageview1_search: { // 搜索按钮 跳转到搜索界面
			startActivity(new Intent(this, SearchMusicActivity.class));
			break;
		}

		// 侧边栏的点击事件---zyq
		case R.id.imageview_user: { // 菜单-个人中心按钮-跳转到个人中心
			String uid1 = SongRisePlayerApp.sp.getString("userid", "0");
			if (uid1.equals("0")) {
				// 若已经登陆跳转到个人中心，未登录进入登陆界面
				startActivity(new Intent(this, UserLoginActivity.class));
			} else {
				startActivity(new Intent(this, UserCenterActivity.class));
			}
			break;
		}
		case R.id.relativelayout_Scan: {// 扫描音乐---zyq
			//myMusicFragment.loadData();
			//DownloadUtils.getInstance().FileScanner();
			//扫描本地歌曲记录
			MediaScannerConnection.scanFile(this, DownloadUtils.getInstance().FileScanner(), null,
					new MediaScannerConnection.OnScanCompletedListener() {

						@Override
						public void onScanCompleted(String path, Uri uri) {
							// TODO Auto-generated method stub
							Log.i("haha", uri + "----");
						}

					});
			T.showShort(this, "扫描完成");
			break;
		}
		case R.id.img_user_menu: { // 个人中心按钮 跳转到个人中心
			String uid = SongRisePlayerApp.sp.getString("userid", "0");
			if (uid.equals("0")) {
				// 若已经登陆跳转到个人中心，未登录进入登陆界面
				startActivity(new Intent(this, UserLoginActivity.class));
			} else {
				startActivity(new Intent(this, UserCenterActivity.class));
			}
			break;
		}
		case R.id.Liner_user: {// ---zyq
			String uid = SongRisePlayerApp.sp.getString("userid", "0");
			if (uid.equals("0")) {
				// 若已经登陆跳转到个人中心，未登录进入登陆界面
				startActivity(new Intent(this, UserLoginActivity.class));
			} else {
				startActivity(new Intent(this, UserCenterActivity.class));
			}
			break;
		}
		case R.id.relativelayout_Lock: {// 锁屏播放设置---zyq
			break;
		}
		case R.id.relativelayout_Shezhi: {// 进入设置界面--zyq
			startActivity(new Intent(this, SettingActivity.class));
			break;
		}
		case R.id.relativelayout_sleep: {// 睡眠设置---zyq
			startActivity(new Intent(this, SleepActivity.class));
			break;
		}
		case R.id.relativelayout_wifi: {// 仅wifi联网开关 ---zyq
			String wifiOnly = SongRisePlayerApp.sp.getString("wifi_only", "no");
			if (wifiOnly.equals("yes")) {
				Editor editor2 = SongRisePlayerApp.sp.edit();
				kaiguan.setImageResource(R.drawable.icon_setting_uncheck);
				editor2.putString("wifi_only", "no");
				editor2.commit();
			}
			if (wifiOnly.equals("no")) {
				Editor editor3 = SongRisePlayerApp.sp.edit();
				kaiguan.setImageResource(R.drawable.icon_setting_checked);
				editor3.putString("wifi_only", "yes");
				editor3.commit();
			}

			/**
			 * 检测网络连接方式 ----zyq请勿删除，我还有用 private static boolean isWifi(Context
			 * mContext) { ConnectivityManager connectivityManager =
			 * (ConnectivityManager) mContext
			 * .getSystemService(Context.CONNECTIVITY_SERVICE); NetworkInfo
			 * activeNetInfo = connectivityManager.getActiveNetworkInfo(); if
			 * (activeNetInfo != null && activeNetInfo.getType() ==
			 * ConnectivityManager.TYPE_WIFI) { return true; } return false; }
			 * 调用方式：if(SongRisePlayerApp.sp.getString("wifi_only",
			 * "yes").equals("yes")) { if(isWifi(MainActivity.this)){
			 * Toast.makeText(MainActivity.this, "wifi连接",
			 * Toast.LENGTH_LONG).show(); } else{ //数据联网
			 * Toast.makeText(MainActivity.this, "数据网络连接",
			 * Toast.LENGTH_LONG).show();} }
			 */
			break;
		}
		case R.id.textView_Tuichu: {// 点击退出应用---zyq
			exitApp();
			break;
		}
		case R.id.imageView_Tuichu: {// 点击退出应用 ---zyq
			exitApp();
			break;
		}

		default:
			break;
		}
	}

	/**
	 * ---zyq
	 */
	public void exitApp() {
		// 退出整个应用---zyq
		int currentVersion = android.os.Build.VERSION.SDK_INT;
		if (currentVersion > android.os.Build.VERSION_CODES.ECLAIR_MR1) {
			Intent startMain = new Intent(Intent.ACTION_MAIN);
			startMain.addCategory(Intent.CATEGORY_HOME);
			startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(startMain);
			System.exit(0);
		} else {// android2.1
			ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
			am.restartPackage(getPackageName());
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub

		if (requestCode == Constant.REQUEST_CODE_1) {
			if (resultCode == Constant.RESULT_OK) {
				
				Bundle bundle = data.getExtras();
				music_song_list = bundle.getParcelableArrayList("songlist");
				if (music_song_list != null && music_song_list.size() > 0) {
					listSave = (ArrayList<List<Map<String, String>>>) music_song_list
							.get(0);
				}

				try {
					if (listSave.size() == playService.songlist.size()) {
						// 没有删除歌单的情况
						// L.e("没有删除歌单");
					} else {
						// 删除歌单的情况
						// 方法一
						playService.songlist = new ArrayList<List<Map<String, String>>>();
						for (int i = 0; i < listSave.size(); i++) {
							List<Map<String, String>> list_child = new ArrayList<Map<String, String>>();
							Map<String, String> list_map = new HashMap<String, String>();
							String values = listSave.get(i).get(0)
									.get(Constant.SONGMAP_KEY);
							list_map.put(Constant.SONGMAP_KEY, values);
							list_child.add(list_map);
							playService.songlist.add(list_child);
						}

						// 方法二
						// int [] delete = bundle.getIntArray("delete");
					}

				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				// 将listSave的值赋给songlist
				// playService.songlist=listSave;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	// @Override
	// public void getSongListDataChange(Bundle bundle) {
	// // TODO Auto-generated method stub
	// getSongListData();
	// }

	/**
	 * 初始化侧边栏数据 ---zyq
	 */
	public void initview() {

		// 侧边栏的实例化
		Liner_user = (LinearLayout) findViewById(R.id.Liner_user);
		imageview_user = (ImageView) findViewById(R.id.imageview_user);
		text_userame = (TextView) findViewById(R.id.TextView_username);

		Rl_wifi = (RelativeLayout) findViewById(R.id.relativelayout_wifi);
		kaiguan = (ImageView) findViewById(R.id.imageView_wifiKai);

		Rl_sleep = (RelativeLayout) findViewById(R.id.relativelayout_sleep);
		text_sleep_time = (TextView) findViewById(R.id.TextView_sleep_time);

		Rl_lock = (RelativeLayout) findViewById(R.id.relativelayout_Lock);
		Rl_scan = (RelativeLayout) findViewById(R.id.relativelayout_Scan);
		Rl_setting = (RelativeLayout) findViewById(R.id.relativelayout_Shezhi);

		// initSleepTime();

		text_tuichu = (TextView) findViewById(R.id.textView_Tuichu);
		image_tuichu = (ImageView) findViewById(R.id.imageView_Tuichu);

		pager.setAdapter(adapter);

		final int pageMargin = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
						.getDisplayMetrics());
		pager.setPageMargin(pageMargin);

		tabs.setViewPager(pager);
		tabs.setTextColor(Color.rgb(255, 255, 255));

		imageview1_search.setOnClickListener(this);

		// 侧边栏注册监听器
		imageview_user.setOnClickListener(this);
		Liner_user.setOnClickListener(this);
		Rl_wifi.setOnClickListener(this);
		Rl_lock.setOnClickListener(this);
		Rl_scan.setOnClickListener(this);
		Rl_setting.setOnClickListener(this);
		Rl_sleep.setOnClickListener(this);
		text_tuichu.setOnClickListener(this);
		image_tuichu.setOnClickListener(this);

		mAccessToken = AccessTokenKeeper.readAccessToken(this);
		// 获取用户信息接口
		mUsersAPI = new UsersAPI(this, Constant.APP_KEY, mAccessToken);
		if (mAccessToken != null && mAccessToken.isSessionValid()) {
			long uid = Long.parseLong(mAccessToken.getUid());
			mUsersAPI.show(uid, mListener);
		}

		String wifiOnly = SongRisePlayerApp.sp.getString("wifi_only", "no");
		if (wifiOnly.equals("yes")) {
			Editor editor2 = SongRisePlayerApp.sp.edit();
			kaiguan.setImageResource(R.drawable.icon_setting_uncheck);
			editor2.putString("wifi_only", "no");
			editor2.commit();
		}
		if (wifiOnly.equals("no")) {
			Editor editor3 = SongRisePlayerApp.sp.edit();
			kaiguan.setImageResource(R.drawable.icon_setting_checked);
			editor3.putString("wifi_only", "yes");
			editor3.commit();
		}
	}

	/**
	 * 微博 OpenAPI 回调接口---zyq
	 */
	private RequestListener mListener = new RequestListener() {
		@Override
		public void onComplete(String response) {
			if (!TextUtils.isEmpty(response)) {
				// LogUtil.i(TAG, response);
				// 调用 User#parse 将JSON串解析成User对象
				User user = User.parse(response);
				if (user != null) {
					// name.setText(user.screen_name + user.profile_image_url
					// + "zyqx---->" + user.id + "个性签名" + user.description
					// + "性别：" + user.gender);
					String urls = user.avatar_large;

					String weiboStr = SongRisePlayerApp.sp.getString("weibos",
							"no");
					String useridStr = SongRisePlayerApp.sp.getString("userid",
							"0");
					if (weiboStr.equals("yes")) {
						Editor editor2 = SongRisePlayerApp.sp.edit();
						// 向共享池存放用户的登陆信息
						editor2.putString("weibos", "no");
						editor2.commit();
						ImageLoad(urls);

						text_userame.setText(user.screen_name);

						imageview_user.buildDrawingCache();
						Bitmap bmap1 = ((BitmapDrawable) imageview_user
								.getDrawable()).getBitmap();
						ByteArrayOutputStream stream = new ByteArrayOutputStream();
						bmap1.compress(Bitmap.CompressFormat.PNG, 60, stream);
						byte[] bytes = stream.toByteArray();
						String img1 = new String(Base64.encodeToString(bytes,
								Base64.DEFAULT));
						AsyncHttpClient client = new AsyncHttpClient();
						RequestParams params = new RequestParams();
						params.put("img", img1);
						params.put("openid", user.idstr);
						params.put("userid", useridStr);
						params.put("relatype", "1");
						String sexx = "男";
						if (user.gender.equals("f")) {
							sexx = "女";
						}
						params.put("sex", sexx);
						params.put("name", user.screen_name);
						url = getResources().getString(R.string.ip_address);
						String url1 = url + "Login/add_user_qq";
						if (SongRisePlayerApp.sp.getString("wifi_only", "no")
								.equals("no")
								|| (SongRisePlayerApp.sp.getString("wifi_only",
										"no").equals("yes") && (isWifi(MainActivity.this)))) {// wifi联网
							client.post(url1, params,
									new AsyncHttpResponseHandler() {

										@Override
										public void onFailure(int arg0,
												Header[] arg1, byte[] arg2,
												Throwable arg3) {
											// TODO Auto-generated method stub

										}

										@Override
										public void onSuccess(int arg0,
												Header[] arg1, byte[] arg2) {
											// TODO Auto-generated method stub
											JSONObject lan;
											try {
												lan = new JSONObject(
														new String(arg2));
												String userid = lan
														.getString("userid");
												String userphone = lan
														.getString("18308230989");
												Editor editor = SongRisePlayerApp.sp
														.edit();
												// 向共享池存放用户的登陆信息
												editor.putString("userid",
														userid);
												editor.putString("userphone",
														userphone);
												editor.commit();
												Toast.makeText(
														MainActivity.this,
														"userid" + userid,
														Toast.LENGTH_LONG)
														.show();
											} catch (JSONException e) {
												// TODO Auto-generated catch
												// block
												e.printStackTrace();
											}
										}
									});
						}

					}

				} else {
					// Toast.makeText(WBAuthActivity.this, response,
					// Toast.LENGTH_LONG).show();
				}
			}
		}

		@Override
		public void onWeiboException(WeiboException e) {
			// ErrorInfo info = ErrorInfo.parse(e.getMessage());
			// Toast.makeText(WBAuthActivity.this, info.toString(),
			// Toast.LENGTH_LONG).show();
		}
	};

	/**
	 * 加载侧边栏数据---zyq
	 */
	public void init1View() {
		String weiboStr = SongRisePlayerApp.sp.getString("weibos", "no");

		Intent intentqq;
		intentqq = getIntent();
		// String weiboStr="112";
		String openidStr = intentqq.getStringExtra("openid");
		if ((openidStr == null || openidStr.length() < 3 || openidStr.isEmpty())
				&& weiboStr.equals("no")) {// 输入电话号码登陆进入主页时
			String uid = SongRisePlayerApp.sp.getString("userid", "0");
			if (uid.equals(0)) {
				text_userame.setText("登陆/注册");
			} else {
				url = getResources().getString(R.string.ip_address);
				String url1 = url + "User/select_user";
				AsyncHttpClient client = new AsyncHttpClient();
				// 新建传参对象
				RequestParams params = new RequestParams();
				// 传入参数
				params.put("userid", uid);// 从共享池获取
				if (SongRisePlayerApp.sp.getString("wifi_only", "no").equals(
						"no")
						|| (SongRisePlayerApp.sp.getString("wifi_only", "no")
								.equals("yes") && (isWifi(MainActivity.this)))) {// wifi联网
					client.post(url1, params, new AsyncHttpResponseHandler() {

						@Override
						public void onFailure(int arg0, Header[] arg1,
								byte[] arg2, Throwable arg3) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onSuccess(int arg0, Header[] arg1,
								byte[] arg2) {
							// TODO Auto-generated method stub
							JSONObject lan;
							try {
								lan = new JSONObject(new String(arg2));
								String succ = lan.getString("success");
								if (succ.equals("yes")) {
									// text_name.setText(name);
									String name1 = lan.getString("username");
									String image1 = lan.getString("userimg");

									text_userame.setText(name1);

									String url_img = getResources().getString(
											R.string.ip_img);
									String url_img1 = url_img + image1;
									ImageLoad(url_img1);

								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();

							}
						}
					});
				}
			}
		} else {// qq第三方登陆取值
			url = getResources().getString(R.string.ip_address);
			String url1 = url + "User/select_user_qq";
			AsyncHttpClient client = new AsyncHttpClient();
			// 新建传参对象
			RequestParams params = new RequestParams();
			// 传入参数
			params.put("openid", openidStr);// 从共享池获取
			if (SongRisePlayerApp.sp.getString("wifi_only", "no").equals("no")
					|| (SongRisePlayerApp.sp.getString("wifi_only", "no")
							.equals("yes") && (isWifi(MainActivity.this)))) {// wifi联网
				client.post(url1, params, new AsyncHttpResponseHandler() {

					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
							Throwable arg3) {
						// TODO Auto-generated method stub
						// Toast.makeText(MainActivity.this, "网络连接失败！",
						// Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						// TODO Auto-generated method stub
						JSONObject lan;
						try {
							lan = new JSONObject(new String(arg2));
							String succ = lan.getString("success");
							if (succ.equals("yes")) {
								// text_name.setText(name);
								String name1 = lan.getString("username");
								String image1 = lan.getString("userimg");
								String userid = lan.getString("userid");
								String userphone = lan.getString("userphone");
								Editor editor = SongRisePlayerApp.sp.edit();
								// 向共享池存放用户的登陆信息
								editor.putString("userid", userid);
								editor.putString("userphone", userphone);
								editor.commit();
								text_userame.setText(name1);

								String url_img = getResources().getString(
										R.string.ip_img);
								String url_img1 = url_img + image1;
								ImageLoad(url_img1);

							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
			}
		}

	}

	/**
	 * ---zyq
	 */
	public void initSleepTime() {
		// 动态更新定时关闭时间---zyq
		// 从SharedPreferences获取数据sleep:
		String sleepTime = SongRisePlayerApp.sp.getString("sleep", "-1");
		int st = Integer.parseInt(sleepTime);
		if (st <= 0) {
			text_sleep_time.setText("关");
		} else {
			i = st;
			i_second = i % 60;
			i_min = i / 60;
			text_sleep_time.setText(i_min + ":" + i_second);
			new Thread(new ClassCut()).start();// 开启倒计时
		}
	}

	/**
	 * ---zyq
	 */
	@Override
	protected void onResume() {
		// ---zyq
		super.onResume();
		initSleepTime();
		init1View();
	}

	/**
	 * ---zyq联网方式判断函数
	 */
	private static boolean isWifi(Context mContext) {
		ConnectivityManager connectivityManager = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null
				&& activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			return true;
		}
		return false;
	}

	/**
	 * 倒计时线程内部类
	 * 
	 * @author 张艳琴
	 * 
	 */
	class ClassCut implements Runnable {
		@Override
		public void run() {
			while (i > 0) {// 整个倒计时执行的循环
				String sleepTime = SongRisePlayerApp.sp
						.getString("sleep", "-1");
				int st = Integer.parseInt(sleepTime);
				i = st;
				i--;
				mHandler.post(new Runnable() {// 通过它在UI主线程中修改显示的剩余时间
					@Override
					public void run() {
						// TODO Auto-generated method stub
						i_second = i % 60;
						i_min = i / 60;
						text_sleep_time.setText(i_min + ":" + i_second);// 显示剩余时间
						Editor editor = SongRisePlayerApp.sp.edit();
						editor.putString("sleep", i + "");
						editor.commit();
					}
				});
				try {

					Thread.sleep(1000);// 线程休眠一秒钟 这个就是倒计时的间隔时间

				} catch (InterruptedException e) {

					e.printStackTrace();
				}

			}
			// 下面是倒计时结束逻辑
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					text_sleep_time.setText("关");// 一轮倒计时结束 修改剩余时间为-1
				}
			});
			i = -1;// 修改倒计时剩余时间变量为60秒
		}

	}

	/**
	 * 获取返回键的监听 双击返回键返回到桌面---zyq 张艳琴
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {//
		// TODO Auto-generated method stub

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			ExitApp2(); // 调用双击退出函数
		}
		return false;

	}

	private long exitTime = 0;

	/**
	 * 双击返回键返回到桌面函数---zyq 张艳琴
	 */
	public void ExitApp2() {
		if ((System.currentTimeMillis() - exitTime) > 2000) {
			exitTime = System.currentTimeMillis();
			Toast.makeText(MainActivity.this, "再按一次返回桌面", Toast.LENGTH_SHORT)
					.show();
		} else {
			Intent startMain = new Intent(Intent.ACTION_MAIN);
			startMain.addCategory(Intent.CATEGORY_HOME);
			startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(startMain);
		}
	}

	protected void ImageLoad(String imgurl) {
		// TODO Auto-generated method stub
		RequestQueue mQueue = Volley.newRequestQueue(MainActivity.this);
		ImageLoader img = new ImageLoader(mQueue, new ImageLoader.ImageCache() {
			@Override
			public void putBitmap(String url, Bitmap bitmap) {
			}

			@Override
			public Bitmap getBitmap(String url) {
				return null;
			}
		});
		ImageListener listener = ImageLoader.getImageListener(imageview_user,
				R.drawable.img_avatar_default, R.drawable.img_avatar_default);
		img.get(imgurl, listener);
	}

}