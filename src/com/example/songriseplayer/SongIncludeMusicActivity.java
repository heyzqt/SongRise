package com.example.songriseplayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.adapter.MyMusicListAdapter;
import com.example.app.Constant;
import com.example.app.SongRisePlayerApp;
import com.example.dialog.CustomDialog;
import com.example.dialog.SelectDialog;
import com.example.utils.AppUtils;
import com.example.utils.L;
import com.example.utils.MediaUtils;
import com.example.utils.T;
import com.example.vo.Mp3Info;
import com.example.vo.SongAndMusicInfo;
import com.example.vo.SongListInfo;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 
 * 指定歌单收藏歌曲界面
 * 
 * @author zq
 * 
 */
public class SongIncludeMusicActivity extends BaseActivity implements
		OnClickListener, OnItemClickListener {

	// 声明界面控件
	private ImageView imageview_add_music_back;
	private ImageView imageview_add_music_menu;
	private ImageView img_add_music_album;
	private ImageView img_add_music_play;
	private ImageView img_add_music_next;
	private TextView text_add_songlist_name;
	private TextView text_add_music_title;
	private TextView text_add_music_singer_name;
	private Button button_add_music_local;
	private Button button_add_music_search;
	private ListView listview_add_music;
	private LinearLayout linear_add_music_gone;
	private ImageView img_play_song_include;
	private TextView text_count_song_include;
	private RelativeLayout rela_set_song_include;

	// 声明必须的对象
	private SongRisePlayerApp app;
	private ArrayList<Mp3Info> mp3Infos; // 本地歌曲
	private ArrayList<Mp3Info> song_include_mp3Infos; // 这个歌单里的歌曲
	private MyMusicListAdapter adapter;
	private String songlist_name; // 保存歌单名
	private int songlist_position; // 保存歌单在songlist的位置
	private long song_id; // 当前歌单id

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_music);
		initview();

		// 设置标题栏颜色
		AppUtils.setSystemStatusBar(SongIncludeMusicActivity.this);
	}

	private void initview() {

		// 注册控件
		imageview_add_music_back = (ImageView) findViewById(R.id.imageview_add_music_back);
		imageview_add_music_menu = (ImageView) findViewById(R.id.imageview_add_music_menu);
		img_add_music_album = (ImageView) findViewById(R.id.img_add_music_album);
		img_add_music_play = (ImageView) findViewById(R.id.img_add_music_play);
		img_add_music_next = (ImageView) findViewById(R.id.img_add_music_next);
		text_add_songlist_name = (TextView) findViewById(R.id.text_add_songlist_name);
		text_add_music_title = (TextView) findViewById(R.id.text_add_music_title);
		text_add_music_singer_name = (TextView) findViewById(R.id.text_add_music_singer_name);
		button_add_music_local = (Button) findViewById(R.id.button_add_music_local);
		button_add_music_search = (Button) findViewById(R.id.button_add_music_search);
		listview_add_music = (ListView) findViewById(R.id.listview_add_music);
		linear_add_music_gone = (LinearLayout) findViewById(R.id.linear_add_music_gone);
		text_count_song_include = (TextView) findViewById(R.id.text_count_song_include);
		rela_set_song_include = (RelativeLayout) findViewById(R.id.rela_set_song_include);
		img_play_song_include = (ImageView) findViewById(R.id.img_play_song_include);

		app = (SongRisePlayerApp) getApplication();

		imageview_add_music_back.setOnClickListener(this);
		imageview_add_music_menu.setOnClickListener(this);
		img_add_music_album.setOnClickListener(this);
		img_add_music_play.setOnClickListener(this);
		img_add_music_next.setOnClickListener(this);
		button_add_music_local.setOnClickListener(this);
		button_add_music_search.setOnClickListener(this);
		listview_add_music.setOnItemClickListener(this);
		rela_set_song_include.setOnClickListener(this);
		img_play_song_include.setOnClickListener(this);

		// 获取intent过来的歌单数据
		Intent getIntent = getIntent();
		songlist_name = getIntent.getStringExtra(Constant.SONGMAP_KEY);
		songlist_position = getIntent.getIntExtra("songlist_position", 0);
		text_add_songlist_name.setText(songlist_name);

		// 从数据库查询歌单-歌曲信息表信息
		try {
			//L.e("songlistname==="+songlist_name);
			
			// 先找到歌单id
			SongListInfo songlistInfo = app.dbUtils.findFirst(Selector.from(
					SongListInfo.class).where("title", "=", songlist_name));

			song_id = songlistInfo.getId();

			//L.e("歌单信息===" + songlistInfo);
			List<SongAndMusicInfo> list = app.dbUtils.findAll(Selector.from(
					SongAndMusicInfo.class).where("songlistInfoId", "=",
					songlistInfo.getId()));
			//L.e("歌单歌曲表信息====" + list);
			if (list == null || list.size() == 0) {
				linear_add_music_gone.setVisibility(View.VISIBLE);
				listview_add_music.setVisibility(View.GONE);
				return;
			}
			linear_add_music_gone.setVisibility(View.GONE);
			listview_add_music.setVisibility(View.VISIBLE);

			/*
			 * 在歌单-歌曲表中找到所有歌曲id 根据歌曲id在歌曲表中找到所有歌曲
			 */
			mp3Infos = MediaUtils.getMp3Infos(this);
			song_include_mp3Infos = new ArrayList<Mp3Info>();
			ArrayList<SongAndMusicInfo> songAndMusicInfos = (ArrayList<SongAndMusicInfo>) list;
			text_count_song_include.setText(songAndMusicInfos.size() + "首");

			/*
			 * 非常重要!!!!!! 两个数组相互比较
			 */
			for (int i = 0; i < songAndMusicInfos.size(); i++) {

				// 找到保存在此歌单中的歌曲
				for (int j = 0; j < mp3Infos.size(); j++) {

					if (songAndMusicInfos.get(i).getMp3InfoId() == mp3Infos
							.get(j).getId()) {
						Mp3Info mp3Info = new Mp3Info();
						mp3Info = mp3Infos.get(j);
						song_include_mp3Infos.add(mp3Info);
						break;
					}
				}
				// //下面的比较方法有问题
				// if (mp3Info.getId() ==
				// songAndMusicInfos.get(j).getMp3InfoId()) {
				// song_include_mp3Infos.add(mp3Info);
				// j++;
				// L.e("j="+j);
				// }
				// if (j == songAndMusicInfos.size()){
				// L.e("break j="+j);
				// break;
				// }
			}
			//L.e("song_include_mp3Infos"+song_include_mp3Infos);

			// 阻止listview_add_music当歌曲过多时，scrollview自动滑动到最底部
			listview_add_music.setFocusable(false);
			adapter = new MyMusicListAdapter(this, song_include_mp3Infos);
			listview_add_music.setAdapter(adapter);
			setListViewHeightBasedOnChildren(listview_add_music);

		} catch (DbException e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	/**
	 * 动态改变listview的长度
	 * 
	 * @param listView
	 */
	public void setListViewHeightBasedOnChildren(ListView listView) {
		// 获取ListView对应的Adapter
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}

		int totalHeight = 0;
		for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
			// listAdapter.getCount()返回数据项的数目
			View listItem = listAdapter.getView(i, null, listView);
			// 计算子项View 的宽高
			listItem.measure(0, 0);
			// 统计所有子项的总高度
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		// listView.getDividerHeight()获取子项间分隔符占用的高度
		// params.height最后得到整个ListView完整显示需要的高度
		listView.setLayoutParams(params);
	}

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
	public void publish(int progress) {
		// TODO Auto-generated method stub

	}

	@Override
	public void change(int position) {
		// TODO Auto-generated method stub

		// 更新界面底部播放器的界面
		Mp3Info mp3Info = playService.mp3Infos.get(position);
		text_add_music_title.setText(mp3Info.getTitle());
		text_add_music_singer_name.setText(mp3Info.getArtist());
		if (playService.isPlaying()) {
			img_add_music_play.setImageResource(R.drawable.img_appwidget_pause);
		} else {
			img_add_music_play.setImageResource(R.drawable.img_appwidget_play);
		}
		Bitmap albumBitmap = MediaUtils.getArtWork(this, mp3Info.getId(),
				mp3Info.getAlbumId(), true, true);
		img_add_music_album.setImageBitmap(albumBitmap);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.imageview_add_music_back: // 返回按钮
			finish();
			break;
		case R.id.rela_set_song_include: { // 管理按钮
			Intent intent = new Intent(SongIncludeMusicActivity.this,
					SongIncludeSetActivity.class);
			intent.putExtra(Constant.SONGLIST_ID, song_id);
			startActivity(intent);
			finish();
			break;
		}
		case R.id.img_play_song_include: { // 播放全部按钮
			
			if(song_include_mp3Infos==null||song_include_mp3Infos.size()==0){
				T.showShort(this, "歌曲列表为空");
				break;
			}
			
			if (playService.getChangePlayList() != PlayService.CURRENT_SONGLIST_PLAY) {

				playService
						.setChangePlayList(PlayService.CURRENT_SONGLIST_PLAY);
			}
			playService.setMp3Infos(song_include_mp3Infos);
			playService.play(0);
			break;
		}
		case R.id.button_add_music_local: { // 从本地添加歌曲按钮
			Intent intent = new Intent(this, AddMusicLocalActivity.class);
			// 将歌单id传过去
			try {
				SongListInfo songListInfo = app.dbUtils.findFirst(Selector
						.from(SongListInfo.class).where("title", "=",
								songlist_name));
				intent.putExtra(Constant.SONGLIST_ID, songListInfo.getId());
				startActivity(intent);
			} catch (DbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finish();
			break;
		}
		case R.id.button_add_music_search: // 从搜索列表添加歌曲
			startActivity(new Intent(this, SearchMusicActivity.class));
			finish();
			break;
		case R.id.img_add_music_album: // 跳转到歌曲播放界面
			Intent intent = new Intent(this, PlayActivity.class);
			startActivity(intent);
			break;
		case R.id.img_add_music_next: { // 下一首歌曲
			playService.next();
			break;
		}
		case R.id.img_add_music_play: { // 播放暂停
			if (playService.isPlaying()) {
				playService.pause();
				img_add_music_play
						.setImageResource(R.drawable.img_appwidget_play);
			} else {
				if (playService.isPause()) {
					playService.start();
				} else {
					playService.play(playService.getCurrentPosition());
				}
				img_add_music_play
						.setImageResource(R.drawable.img_appwidget_pause);
			}
			break;
		}
		case R.id.imageview_add_music_menu: { // 歌单-歌曲界面菜单按钮
			final SelectDialog menuDialog = new SelectDialog(this,
					R.style.song_include_music_menu_dialog);// 创建Dialog并设置样式主题

			menuDialog.setCanceledOnTouchOutside(true);// 设置点击Dialog外部任意区域关闭Dialog
			menuDialog.show();

			// Window win = menuDialog.getWindow();
			// LayoutParams params = new LayoutParams();
			// Display d=getWindowManager().getDefaultDisplay();
			// params.x=(int)(d.getWidth()*0.9);
			// params.y=(int)(d.getHeight()*0.1);
			// params.x = 250;// 设置x坐标
			// params.y = -300;// 设置y坐标
			// win.setAttributes(params);
			// p.width = (int)(d.getWidth()*0.9);

			WindowManager dialogManager = getWindowManager();
			Window dialogWindow = menuDialog.getWindow();
			Display d = dialogManager.getDefaultDisplay(); // 获取屏幕宽高
			LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的数值
			// p.width = (int)(d.getWidth()*0.9); //宽度设置为屏幕的0.9
			// p.height=(int)(d.getHeight()*0.6); //高度设置为屏幕的0.6;
			// p.width=450;
			// p.height=400;
			p.y = (int) (d.getHeight() * (-0.35));
			p.x = (int) (d.getWidth() * 1);
			// p.x=300;
			// p.y=-350;
			dialogWindow.setAttributes(p); // 设置生效

			TextView song_include_music_add = (TextView) menuDialog
					.findViewById(R.id.song_include_music_add);
			TextView song_include_music_rename = (TextView) menuDialog
					.findViewById(R.id.song_include_music_rename);

			// 添加歌曲
			song_include_music_add.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					// T.showShort(getApplication(), "添加歌曲");
					menuDialog.dismiss();
					addMusicDialog();
				}
			});

			// 重命名歌单
			song_include_music_rename.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					// T.showShort(getApplication(), "重命名歌单");
					menuDialog.dismiss();
					renameSonglistDialog();
				}
			});

			break;
		}
		default:
			break;
		}
	}

	/**
	 * 添加歌曲对话框
	 */
	private void addMusicDialog() {
		LayoutInflater inflaterDl = LayoutInflater
				.from(SongIncludeMusicActivity.this);
		LinearLayout layout = (LinearLayout) inflaterDl.inflate(
				R.layout.add_music_dialog, null);

		// 对话框
		final Dialog dialog = new AlertDialog.Builder(
				SongIncludeMusicActivity.this).create();

		dialog.show();
		dialog.getWindow().setContentView(layout);
		// 自定义对话框的宽高
		// WindowManager dialogManager=getWindowManager();
		// Window dialogWindow = dialog.getWindow();
		// Display d=dialogManager.getDefaultDisplay(); //获取屏幕宽高
		// LayoutParams p=dialogWindow.getAttributes(); //获取对话框当前的数值
		// p.width = (int)(d.getWidth()*0.9); //宽度设置为屏幕的0.9
		// p.height=(int)(d.getHeight()*0.6); //高度设置为屏幕的0.6;
		// p.width=450;
		// p.height=400;
		// p.y=(int)(d.getHeight()*0.8);
		// p.y=(int)(d.getWidth()*);
		// dialogWindow.setAttributes(p); // 设置生效

		// 获取当前屏幕的数据
		// WindowManager m = getWindowManager();
		// Display d = m.getDefaultDisplay(); //为获取屏幕宽、高
		// LayoutParams p = getWindow().getAttributes(); //获取对话框当前的参数值
		// p.height = (int) (d.getHeight() * 0.6); //高度设置为屏幕的0.6
		// p.width = (int) (d.getWidth() * 0.9); //宽度设置为屏幕的0.9
		// getWindow().setAttributes(p); //设置生效

		LinearLayout linearlayout_add_music_cancel_dialog = (LinearLayout) layout
				.findViewById(R.id.linearlayout_add_music_cancel_dialog);
		TextView text_add_local_dialog = (TextView) layout
				.findViewById(R.id.text_add_local_dialog);
		TextView text_add_search_dialog = (TextView) layout
				.findViewById(R.id.text_add_search_dialog);

		// 跳转到添加本地歌曲
		text_add_local_dialog.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// T.showShort(app, "添加本地歌曲");
				Intent intent = new Intent(SongIncludeMusicActivity.this,
						AddMusicLocalActivity.class);
				// 将歌单id传过去
				// try {
				// SongListInfo songListInfo = app.dbUtils.findFirst(Selector
				// .from(SongListInfo.class).where("title", "=",
				// songlist_name));
				// L.e(""+songListInfo);
				intent.putExtra(Constant.SONGLIST_ID, song_id);
				startActivity(intent);
				finish();
				// } catch (DbException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }
				dialog.dismiss(); // 对话框消失
				finish();
			}
		});

		// 跳转到搜索界面按钮
		text_add_search_dialog.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(SongIncludeMusicActivity.this,
						SearchMusicActivity.class);
				startActivity(intent);
				dialog.dismiss(); // 对话框消失
			}
		});

		// 取消按钮
		linearlayout_add_music_cancel_dialog
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss(); // 对话框消失
					}
				});

	}

	/**
	 * 重命名歌单
	 */
	private void renameSonglistDialog() {
		// 自定义添加歌单对话框

		final CustomDialog dialog = new CustomDialog(this, R.style.MyDialog);

		dialog.show();

		Button confirm = (Button) dialog.findViewById(R.id.positiveButton);
		Button cancel = (Button) dialog.findViewById(R.id.negativeButton);
		TextView text = (TextView) dialog.findViewById(R.id.title);
		text.setText("重命名");
		final EditText edt = (EditText) dialog
				.findViewById(R.id.edittext_song_list_name);

		confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String str = edt.getText().toString();
				if (str.equals("")) {
					T.showShort(app, R.string.enter_failure_text);
				} else {

					try {

						// 查询数据库中所有歌单列表名
						SongListInfo songListInfo = app.dbUtils
								.findFirst(Selector.from(SongListInfo.class)
										.where("title", "=", str));

						if (songListInfo != null) {
							T.showShort(app, "歌单名已存在,请重新输入");
						} else {
							// 获取当前的SongListInfo来更新title字段
							SongListInfo add_songInfo = app.dbUtils
									.findFirst(Selector
											.from(SongListInfo.class)
											.where("title", "=", songlist_name));
							// 歌单不重名时添加
							add_songInfo.setTitle(str);
							app.dbUtils.update(add_songInfo, "title");
							text_add_songlist_name.setText(str);

							// 更新songlist
							playService.songlist.get(songlist_position).get(0)
									.put(Constant.SONGMAP_KEY, str);
						}

					} catch (DbException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
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
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		// L.e(""+song_include_mp3Infos);
		if (playService.getChangePlayList() != PlayService.CURRENT_SONGLIST_PLAY) {

			playService.setChangePlayList(PlayService.CURRENT_SONGLIST_PLAY);
			//playService.setMp3Infos(song_include_mp3Infos);
			//playService.setSonglistname(songlist_name);
		}
		//自定义歌单列表名字不同
		playService.setMp3Infos(song_include_mp3Infos);
		playService.setSonglistname(songlist_name);
		playService.play(position);
	}

	// /**
	// * 保存歌曲的播放时间
	// */
	// private void savePlayRecord() {
	// // 获取当前正在播放的音乐对象
	// Mp3Info mp3Info = playService.getMp3Infos().get(
	// playService.getCurrentPosition());
	//
	// try {
	//
	// Mp3Info playRecordMp3Info = app.dbUtils.findFirst(Selector.from(
	// Mp3Info.class).where("mp3InfoId", "=",
	// mp3Info.getMp3InfoId()));
	//
	// if (playRecordMp3Info == null) {
	//
	// mp3Info.setPlayTime(System.currentTimeMillis());
	// app.dbUtils.save(mp3Info);
	//
	// } else {
	//
	// playRecordMp3Info.setPlayTime(System.currentTimeMillis());
	// app.dbUtils.update(playRecordMp3Info, "playTime");
	//
	// }
	// } catch (Exception e) {
	// // TODO: handle exception
	// e.printStackTrace();
	// }
	// }

}
