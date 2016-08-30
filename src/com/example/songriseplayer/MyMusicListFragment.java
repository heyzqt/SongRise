package com.example.songriseplayer;

import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.astuetz.viewpager.extensions.sample.SuperAwesomeCardFragment;
import com.example.adapter.MyMusicListAdapter;
import com.example.adapter.SongListAdapter;
import com.example.app.Constant;
import com.example.app.SongRisePlayerApp;
import com.example.dialog.CustomDialog;
import com.example.songriseplayer.PlayService.MusicUpdateListener;
import com.example.utils.AppUtils;
import com.example.utils.DownloadUtils;
import com.example.utils.L;
import com.example.utils.MediaUtils;
import com.example.utils.T;
import com.example.vo.Mp3Info;
import com.example.vo.SongAndMusicInfo;
import com.example.vo.SongListInfo;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MyMusicListFragment extends Fragment implements
		OnItemClickListener, OnClickListener {

	// private ListView listview_my_music;
	private ArrayList<Mp3Info> mp3Infos;
	private MyMusicListAdapter mymusiclistadapter;
	private MainActivity mainActivity;

	private ImageView imageview1_album;
	private boolean isPlaying = false;

	private ImageView imageview2_play;
	private ImageView imageview3_next;
	private TextView textview1_title;
	private TextView textview2_singer_name;
	private TextView textview_songlist;
	private RelativeLayout relativelayout_music_local; // 本地音乐
	private RelativeLayout relativelayout_music_favorite; // 我的收藏
	private RelativeLayout relativelayout_music_recent_play; // 最近播放
	private RelativeLayout relativelayout_music_load;		//歌曲下载
	private LinearLayout linearlayout_songlist_item;	//当歌单无歌曲时

	// private boolean isPause = false; // 判断歌曲是否是播放后的暂停状态

	private SongListAdapter songListAdapter;
	private ListView listview_songlist;
	// private ArrayList<Map<String, String>> songlist_child = new
	// ArrayList<Map<String, String>>();
	// private List<List<Map<String, String>>> songlist = new
	// ArrayList<List<Map<String, String>>>();
	private ImageView imageview_add_songlist;
	private ImageView imageview_manage_songlist;

	// private static int count = 1;
	// private SongListManageActivity songListManageActivity;

	// private int position=0;

	public static MyMusicListFragment newInstance() {
		MyMusicListFragment my = new MyMusicListFragment();
		return my;
	}

	@Override
	public void onAttach(Context context) {
		// TODO Auto-generated method stub
		super.onAttach(context);
		mainActivity = (MainActivity) context;
	}

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.my_music_list_layout, null);
		// listview_my_music = (ListView)
		// view.findViewById(R.id.listview_local_music);
		imageview1_album = (ImageView) view.findViewById(R.id.imageview1_album);
		imageview2_play = (ImageView) view.findViewById(R.id.imageview2_play);
		imageview3_next = (ImageView) view.findViewById(R.id.imageview3_next);
		textview1_title = (TextView) view.findViewById(R.id.textview1_title);
		textview2_singer_name = (TextView) view
				.findViewById(R.id.textview2_singer_name);
		textview_songlist=(TextView) view.findViewById(R.id.textview_songlist);
		listview_songlist = (ListView) view
				.findViewById(R.id.listview_songlist);
		imageview_add_songlist = (ImageView) view
				.findViewById(R.id.imageview_add_songlist);
		imageview_manage_songlist = (ImageView) view
				.findViewById(R.id.imageview_manage_songlist);
		listview_songlist = (ListView) view
				.findViewById(R.id.listview_songlist);
		linearlayout_songlist_item = (LinearLayout) view.findViewById(R.id.linearlayout_songlist_item);
		linearlayout_songlist_item.setOnClickListener(this);

		// 注册主界面的4个按钮
		relativelayout_music_local = (RelativeLayout) view
				.findViewById(R.id.relativelayout_music_local);
		relativelayout_music_favorite = (RelativeLayout) view
				.findViewById(R.id.relativelayout_music_favorite);
		relativelayout_music_recent_play = (RelativeLayout) view
				.findViewById(R.id.relativelayout_music_recent_play);
		relativelayout_music_load = (RelativeLayout) view.findViewById(R.id.relativelayout_music_load);

		// listview_my_music.setOnItemClickListener(this);
		imageview2_play.setOnClickListener(this);
		imageview3_next.setOnClickListener(this);
		imageview1_album.setOnClickListener(this);
		imageview_add_songlist.setOnClickListener(this);
		imageview_manage_songlist.setOnClickListener(this);
		relativelayout_music_local.setOnClickListener(this);
		relativelayout_music_favorite.setOnClickListener(this);
		relativelayout_music_recent_play.setOnClickListener(this);
		relativelayout_music_load.setOnClickListener(this);
		listview_songlist.setOnItemClickListener(this);
		
		

//		try {
//			List<SongListInfo> songlistinfos = mainActivity.app.dbUtils
//					.findAll(SongListInfo.class);
//			if (songlistinfos != null && songlistinfos.size() != 0) {
//				L.e("歌单表，SongListInfo=====" + songlistinfos);
//			} else {
//				L.e("歌单表为空");
//			}
//			List<SongAndMusicInfo> songAndMusicInfos = mainActivity.app.dbUtils
//					.findAll(SongAndMusicInfo.class);
//			if (songAndMusicInfos != null && songAndMusicInfos.size() != 0) {
//				L.e("歌单歌曲表，songAndMusicInfos=====" + songAndMusicInfos);
//			} else {
//				L.e("歌单歌曲表为空");
//			}
//		} catch (DbException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			L.e("获取歌单表或歌曲表失败");
//		}

		return view;
	}

	// Activity创建或者从被覆盖、后台重新回到前台时被调用
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// 绑定服务
		mainActivity.bindPlayService();

		// int x = mainActivity.app.sp.getInt(Constant.CURRENT_PLAY_MUSIC,
		// PlayService.LOCAL_MUSIC);
		// if(x==0){
		// L.e("当前音乐播放状态==本地音乐");
		// }
		// else if(x==1){
		// L.e("当前音乐播放状态==网络音乐");
		// }
		//
		// if(x==PlayService.NET_MUSIC){
		// changeUIStatusOnPlay(Constant.NULL_NET_MUSIC);
		// }

	}

	// Activity被覆盖到下面或者锁屏时被调用
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mainActivity.unbindPlayService();
	}

	/*
	 * 加载本地列表
	 */
	public void loadData() {
		// mp3Infos = MediaUtils.getMp3Infos(mainActivity);
		// //mp3Infos=mainActivity.playService.mp3Infos;
		// mymusiclistadapter = new MyMusicListAdapter(mainActivity, mp3Infos);
		// listview_my_music.setAdapter(mymusiclistadapter);

		try {
			songListAdapter = new SongListAdapter(mainActivity,
					mainActivity.playService.songlist);
			listview_songlist.setAdapter(songListAdapter);
			setListViewHeight(listview_songlist);
			if(mainActivity.playService.songlist.size()==0){
				linearlayout_songlist_item.setVisibility(View.VISIBLE);
				listview_songlist.setVisibility(View.GONE);
				textview_songlist.setText(0+"个");
			}else{
				listview_songlist.setVisibility(View.VISIBLE);
				linearlayout_songlist_item.setVisibility(View.GONE);
				textview_songlist.setText(mainActivity.playService.songlist.size()+"个");
			}
		} catch (Exception e) {
			// TODO: handle exception
			L.e("MyMusicListFragment throw a bug");
		}
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub

		Intent intent = new Intent(mainActivity, SongIncludeMusicActivity.class);
		intent.putExtra(Constant.SONGMAP_KEY, mainActivity.playService.songlist
				.get(position).get(0).get(Constant.SONGMAP_KEY));
		intent.putExtra("songlist_position", position);
		startActivity(intent);

	}

	// 回调播放状态下的UI设置
	public void changeUIStatusOnPlay(int position) {

		mainActivity.playService.isMainActivity = true;

		if (mainActivity.playService.getCurrentPlayMusic() == PlayService.LOCAL_MUSIC) {

			//L.e("本地歌曲更新");
			// 填充我的音乐列表和播放的音乐是两个不同的mp3Infos
			if (position >= 0
					&& position < mainActivity.playService.mp3Infos.size()) {

				try {
					Mp3Info mp3Info = mainActivity.playService.mp3Infos
							.get(position);
					textview1_title.setText(mp3Info.getTitle());
					textview2_singer_name.setText(mp3Info.getArtist());
					Bitmap albumBitmap = MediaUtils.getArtWork(mainActivity,
							mp3Info.getId(), mp3Info.getAlbumId(), true, true);
					imageview1_album.setImageBitmap(albumBitmap);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}

				if (mainActivity.playService.isPlaying()) {
					imageview2_play
							.setImageResource(R.drawable.img_appwidget_pause);
				} else {
					imageview2_play
							.setImageResource(R.drawable.img_appwidget_play);
				}

			}

		} else if (mainActivity.playService.getCurrentPlayMusic() == PlayService.NET_MUSIC) {

			//L.e("网络歌曲更新");

			//L.e("我的音乐code===="+SongRisePlayerApp.sp_msg.getInt("code", -1));
			// code 1 播放 -1 暂停
			if (SongRisePlayerApp.sp_msg.getInt("code", -1) == 1) {
				imageview2_play
						.setImageResource(R.drawable.img_appwidget_pause);
			} else {
				imageview2_play.setImageResource(R.drawable.img_appwidget_play);
			}

			String[] str = mainActivity.playService.getPlayingSongMsg();
			textview1_title.setText(str[0]);
			textview2_singer_name.setText(str[1]);

			// 设置专辑图片
			// 从文件中找专辑图片,若无设置为默认图片
			//imageview1_album.setImageResource(R.drawable.music_icon);
			DownloadUtils.getInstance().ShowImg(mainActivity.app.getAlbum(), imageview1_album,
					handler, mainActivity.app.getSongName());

		}
	}
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.arg1 == 1) {
				imageview1_album.setImageBitmap(DownloadUtils.getInstance()
						.GetImgFromSDCard(mainActivity.app.getSongName()));
			}
		};
	};

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		case R.id.imageview2_play: { // 歌曲播放

			if (mainActivity.playService.getCurrentPlayMusic() == PlayService.LOCAL_MUSIC) {
				if (mainActivity.playService.isPlaying()) {
					imageview2_play
							.setImageResource(R.drawable.img_appwidget_play);
					mainActivity.playService.pause();
					// isPause = true;
				} else {
					imageview2_play
							.setImageResource(R.drawable.img_appwidget_pause);
					if (mainActivity.playService.isPause()) {
						mainActivity.playService.start();
					} else {
						mainActivity.playService
								.setCurrentPlayMusic(PlayService.LOCAL_MUSIC);
						mainActivity.playService
								.playMusic(mainActivity.playService
										.getCurrentPosition());
					}
				}
			} else if (mainActivity.playService.getCurrentPlayMusic() == PlayService.NET_MUSIC) {

				if ((mainActivity.app.getCode() == 1 && mainActivity.playService
						.isPlaying())) {
					imageview2_play
							.setImageResource(R.drawable.img_appwidget_pause1);
					mainActivity.playService.pause();
				} else {
					imageview2_play
							.setImageResource(R.drawable.img_appwidget_play1);
					if (mainActivity.playService.isPause()) {
						mainActivity.playService.startMusic();
					} else {

						mainActivity.playService
								.setCurrentPlayMusic(PlayService.NET_MUSIC);
						mainActivity.playService.playMusic(-1);
					}
				}

			}

		}
			break;
		case R.id.imageview3_next: {
			if (mainActivity.playService.getCurrentPlayMusic() == PlayService.LOCAL_MUSIC) {
				mainActivity.playService.next();
			} else if (mainActivity.playService.getCurrentPlayMusic() == PlayService.NET_MUSIC) {

				int position_local = mainActivity.app.getPosition() + 1;
				mainActivity.app.setPosition(position_local);
				mainActivity.playService.playMusic(Constant.NULL_NET_MUSIC);
				
			}
			break;
		}
		case R.id.imageview1_album: { // 跳转到播放界面
			if (mainActivity.playService.getCurrentPlayMusic() == PlayService.LOCAL_MUSIC) {
				Intent intent = new Intent(mainActivity, PlayActivity.class);
				startActivity(intent);
			} else if (mainActivity.playService.getCurrentPlayMusic() == PlayService.NET_MUSIC) {
				Intent intent = new Intent(mainActivity,
						PlayInternetMusicActivity.class);
				startActivity(intent);
			}

			break;
		}
		case R.id.relativelayout_music_local: { // 本地歌曲
			startActivity(new Intent(mainActivity, LocalMusicActivity.class));
			break;
		}
		case R.id.relativelayout_music_favorite: {
			startActivity(new Intent(mainActivity,
					MyLikeMusicListActivity.class));
			break;
		}
		case R.id.relativelayout_music_load:{
			startActivity(new Intent(mainActivity,
					DownloadActivity.class));
			break;
		}
		case R.id.imageview_add_songlist: { // 添加歌单

			// 自定义添加歌单对话框

			final CustomDialog dialog = new CustomDialog(mainActivity,
					R.style.MyDialog);
			dialog.show();

			Button confirm = (Button) dialog.findViewById(R.id.positiveButton);
			Button cancel = (Button) dialog.findViewById(R.id.negativeButton);
			final EditText edt = (EditText) dialog
					.findViewById(R.id.edittext_song_list_name);

			confirm.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					String str = edt.getText().toString();
					if (str.equals("")) {

						T.showShort(mainActivity, R.string.enter_failure_text);
					} else {

						try {
							// 查询数据库中所有歌单列表名
							SongListInfo songListInfo = mainActivity.app.dbUtils
									.findFirst(Selector
											.from(SongListInfo.class).where(
													"title", "=", str));
							if (songListInfo != null) {
								// 歌单重名时不能添加
								T.showShort(mainActivity, "歌单名已存在,请重新输入");
							} else {
								// 歌单不重名时添加 songlist
								List<Map<String, String>> list = new ArrayList<Map<String, String>>();
								Map<String, String> songmap = new HashMap<String, String>();
								songmap.put("songlist_name", "" + str);
								list.add(songmap);
								mainActivity.playService.songlist.add(list);

								// 歌单不重名时添加 SongListInfo表
								SongListInfo songListInfo_add = new SongListInfo();
								songListInfo_add.setTitle(str);
								mainActivity.app.dbUtils.save(songListInfo_add);
								textview_songlist.setText(mainActivity.playService.songlist.size()+"个");
							}
						} catch (DbException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						// songlist_child=new ArrayList<Map<String,String>>();
						// songlist_child.add(songmap);
						// try {
						// //L.e("MyMusicFragment==="+mainActivity.playService.songlist);
						// mainActivity.playService.songlist.add(songlist_child);
						// } catch (Exception e) {
						// // TODO: handle exception
						// }
						songListAdapter.notifyDataSetChanged();
						setListViewHeight(listview_songlist);
						if(mainActivity.playService.songlist.size()==0){
							linearlayout_songlist_item.setVisibility(View.VISIBLE);
							listview_songlist.setVisibility(View.GONE);
						}else{
							listview_songlist.setVisibility(View.VISIBLE);
							linearlayout_songlist_item.setVisibility(View.GONE);
						}
						dialog.dismiss();// pay attention
					}
				}
			});
			
			cancel.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dialog.dismiss();
				}
			});

			break;
		}
		case R.id.imageview_manage_songlist: { // 管理歌单

			// songListManageActivity=new SongListManageActivity();
			// songListManageActivity.loadData();
			Intent intent = new Intent(mainActivity,
					SongListManageActivity.class);

			Bundle bundle = new Bundle();
			// 这个list能用putParcelableArrayList()传递过去
			// 所以把真正要传的songlist包进去传过去
			ArrayList list = new ArrayList();
			list.add(mainActivity.playService.songlist);
			bundle.putParcelableArrayList("songlist", list);
			intent.putExtras(bundle);

			mainActivity
					.startActivityForResult(intent, Constant.REQUEST_CODE_1);
			// startActivity(intent);
			break;
		}
		case R.id.relativelayout_music_recent_play: { // 最近播放歌曲
			startActivity(new Intent(mainActivity,
					MyRecentMusicListActivity.class));
			break;
		}
		case R.id.linearlayout_songlist_item:{      //点击添加歌曲
			// 自定义添加歌单对话框

						final CustomDialog dialog = new CustomDialog(mainActivity,
								R.style.MyDialog);
						dialog.show();

						Button confirm = (Button) dialog.findViewById(R.id.positiveButton);
						Button cancel = (Button) dialog.findViewById(R.id.negativeButton);
						final EditText edt = (EditText) dialog
								.findViewById(R.id.edittext_song_list_name);

						confirm.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								// TODO Auto-generated method stub
								String str = edt.getText().toString();
								if (str.equals("")) {

									T.showShort(mainActivity, R.string.enter_failure_text);
								} else {

									try {
										// 查询数据库中所有歌单列表名
										SongListInfo songListInfo = mainActivity.app.dbUtils
												.findFirst(Selector
														.from(SongListInfo.class).where(
																"title", "=", str));
										if (songListInfo != null) {
											// 歌单重名时不能添加
											T.showShort(mainActivity, "歌单名已存在,请重新输入");
										} else {
											// 歌单不重名时添加 songlist
											List<Map<String, String>> list = new ArrayList<Map<String, String>>();
											Map<String, String> songmap = new HashMap<String, String>();
											songmap.put("songlist_name", "" + str);
											list.add(songmap);
											mainActivity.playService.songlist.add(list);

											// 歌单不重名时添加 SongListInfo表
											SongListInfo songListInfo_add = new SongListInfo();
											songListInfo_add.setTitle(str);
											mainActivity.app.dbUtils.save(songListInfo_add);
										}
									} catch (DbException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}

									// songlist_child=new ArrayList<Map<String,String>>();
									// songlist_child.add(songmap);
									// try {
									// //L.e("MyMusicFragment==="+mainActivity.playService.songlist);
									// mainActivity.playService.songlist.add(songlist_child);
									// } catch (Exception e) {
									// // TODO: handle exception
									// }
									songListAdapter.notifyDataSetChanged();
									setListViewHeight(listview_songlist);
									if(mainActivity.playService.songlist.size()==0){
										linearlayout_songlist_item.setVisibility(View.VISIBLE);
										listview_songlist.setVisibility(View.GONE);
									}else{
										listview_songlist.setVisibility(View.VISIBLE);
										linearlayout_songlist_item.setVisibility(View.GONE);
									}
									dialog.dismiss();// pay attention
								}
							}
						});

						cancel.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								// TODO Auto-generated method stub
								dialog.dismiss();// pay attention
							}
						});
			
			break;
		}
		default:
			break;
		}
	}

	/**
	 * 重新计算ListView的高度，解决ScrollView和ListView两个View都有滚动的效果，在嵌套使用时起冲突的问题
	 * 
	 * @param listView
	 */
	public void setListViewHeight(ListView listView) {

		// 获取ListView对应的Adapter

		ListAdapter listAdapter = listView.getAdapter();

		if (listAdapter == null) {
			return;
		}
		int totalHeight = 0;
		for (int i = 0, len = listAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0); // 计算子项View 的宽高
			totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + 100
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
	}

}
