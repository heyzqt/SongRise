package com.example.songriseplayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.adapter.MusicManageListAdapter;
import com.example.adapter.MyMusicListAdapter;
import com.example.app.Constant;
import com.example.app.SongRisePlayerApp;
import com.example.utils.AppUtils;
import com.example.utils.L;
import com.example.utils.MediaUtils;
import com.example.utils.T;
import com.example.vo.Mp3Info;
import com.example.vo.SongAndMusicInfo;
import com.example.vo.SongListInfo;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 歌单-歌曲管理歌曲界面
 * 
 * @author zq
 * 
 */
public class SongIncludeSetActivity extends BaseActivity implements
		OnClickListener, OnItemClickListener {

	// 声明控件
	private TextView text_choice_all_not_song_include;
	private ImageView img_back_song_include;
	private ListView list_choice_song;
	private RelativeLayout rela_delete_song;
	// private RelativeLayout rela_load_song;
	private TextView text_song_include;

	// 声明必须的对象
	private ArrayList<Mp3Info> mp3Infos;
	private ArrayList<Mp3Info> songMp3Infos;
	private ArrayList<Map<String, String>> checkList; // 保存歌曲checked看是否被选中
	private int mCount; // 找到有列表中的歌曲数量
	private long[] music_id; // 记录本列表中所有歌曲的id
	private long[] delete_load_music_id; // 记录要删除的歌曲的id
	private long songId; // 保存当前歌单的id
	private int count; // 记录删除或下载歌曲的数量
	private SongRisePlayerApp app;
	private MusicManageListAdapter mAdapter;
	private String songlist_name; // 记录当前歌单的名字

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.song_include_set_layout);
		init();

		// 设置标题栏颜色
		AppUtils.setSystemStatusBar(SongIncludeSetActivity.this);
	}

	/**
	 * 初始化界面
	 */
	private void init() {
		text_choice_all_not_song_include = (TextView) findViewById(R.id.text_choice_all_not_song_include);
		img_back_song_include = (ImageView) findViewById(R.id.img_back_song_include);
		list_choice_song = (ListView) findViewById(R.id.list_choice_song);
		rela_delete_song = (RelativeLayout) findViewById(R.id.rela_delete_song);
		// rela_load_song = (RelativeLayout) findViewById(R.id.rela_load_song);
		text_song_include = (TextView) findViewById(R.id.text_song_include);

		text_choice_all_not_song_include.setOnClickListener(this);
		img_back_song_include.setOnClickListener(this);
		// rela_load_song.setOnClickListener(this);
		rela_delete_song.setOnClickListener(this);
		list_choice_song.setOnItemClickListener(this);

	}

	/**
	 * 加载数据
	 */
	private void loadData() {

		app = (SongRisePlayerApp) getApplication();
		Intent getIntent = getIntent();
		songId = getIntent.getLongExtra(Constant.SONGLIST_ID, -1);

		mp3Infos = MediaUtils.getMp3Infos(this);
		mCount = mp3Infos.size();
		checkList = new ArrayList<Map<String, String>>();

		try {

			// 记录当前歌单的名字
			songlist_name = app.dbUtils.findById(SongListInfo.class, songId)
					.getTitle();

			// L.e(""+songId);
			List<SongAndMusicInfo> list = app.dbUtils.findAll(Selector.from(
					SongAndMusicInfo.class)
					.where("songlistInfoId", "=", songId));

			if (list == null || list.size() == 0) {
				text_song_include.setVisibility(View.VISIBLE);
				list_choice_song.setVisibility(View.GONE);
				return;
			} else {
				text_song_include.setVisibility(View.GONE);
				list_choice_song.setVisibility(View.VISIBLE);
				// 根据歌单id找对应的歌曲id
				ArrayList<SongAndMusicInfo> songAndMusicInfos = (ArrayList<SongAndMusicInfo>) list;
				mCount = songAndMusicInfos.size();
				music_id = new long[mCount];
				for (int i = 0; i < songAndMusicInfos.size(); i++) {
					music_id[i] = songAndMusicInfos.get(i).getMp3InfoId();
				}

				// L.e(""+songAndMusicInfos);
				// for(int i=0;i<music_id.length;i++){
				// L.e(""+music_id[i]);
				// }

				// 填充listview
				/*
				 * 非常重要
				 */
				songMp3Infos = new ArrayList<Mp3Info>();
				for (int i = 0; i < music_id.length; i++) {

					for (int j = 0; j < mp3Infos.size(); j++) {
						Mp3Info mp3Info = mp3Infos.get(j);
						if (music_id[i] == mp3Info.getId()) {
							songMp3Infos.add(mp3Info);
							break;
						}
					}
				}

				// L.e(""+songMp3Infos);
				// 将歌曲默认标记为未选中
				for (int i = 0; i < mCount; i++) {
					Map<String, String> songmap_checked = new HashMap<String, String>();
					songmap_checked.put(Constant.SONGLIST_CHECKED, "false");
					checkList.add(songmap_checked);
				}

				mAdapter = new MusicManageListAdapter(
						SongIncludeSetActivity.this, songMp3Infos, checkList);
				list_choice_song.setAdapter(mAdapter);

			}
		} catch (DbException e) {
			// TODO: handle exception
			L.e("list获取失败");
		}
	}

	// Activity创建或者从被覆盖、后台重新回到前台时被调用
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		loadData();
		count = 0;
		bindPlayService();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		unbindPlayService();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		TextView textview_is_checked = (TextView) view
				.findViewById(R.id.text_add_local_is_checked);
		ImageView imageview_circle = (ImageView) view
				.findViewById(R.id.img_add_local_circle);

		String str = textview_is_checked.getText().toString();
		
		if (playService.getChangePlayList() == playService.CURRENT_SONGLIST_PLAY
				&& songlist_name.equals(app.sp.getString("songlistname", ""))) {
			// 除开当前播放的歌单不能删,其他都能删
			T.showShort(this, "当前歌单正在播放,不能删除");
		} else {
			// 随意删除歌单
			if (str.equals("false")) {
				imageview_circle.setImageResource(R.drawable.img_circle_checked);
				textview_is_checked.setText("true");
				checkList.get(position).put(Constant.SONGLIST_CHECKED, "true");
			} else {
				imageview_circle.setImageResource(R.drawable.img_circle);
				textview_is_checked.setText("false");
				checkList.get(position).put(Constant.SONGLIST_CHECKED, "false");
			}
		}	
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.img_back_song_include: // 返回

			Intent back_intent = new Intent(this,
					SongIncludeMusicActivity.class);
			back_intent.putExtra(Constant.SONGMAP_KEY, songlist_name);
			startActivity(back_intent);
			finish();
			break;
		case R.id.text_choice_all_not_song_include: { // 全选
			// L.e("" + checkList);
			String str = text_choice_all_not_song_include.getText().toString();
			if (str.equals("全选")) {
				text_choice_all_not_song_include.setText("取消全选");
				for (int i = 0; i < checkList.size(); i++) {
					if (checkList.get(i).get(Constant.SONGLIST_CHECKED)
							.equals("false")) {
						checkList.get(i).put(Constant.SONGLIST_CHECKED, "true");
					}
					continue;
				}
				mAdapter.notifyDataSetChanged();
			} else {
				text_choice_all_not_song_include.setText("全选");
				// 将未被选中的歌曲作为选中状态

				for (int i = 0; i < checkList.size(); i++) {
					if (checkList.get(i).get(Constant.SONGLIST_CHECKED)
							.equals("true")) {
						checkList.get(i)
								.put(Constant.SONGLIST_CHECKED, "false");
					}
					continue;
				}
				mAdapter.notifyDataSetChanged();
			}
			// L.e("" + checkList);
			break;
		}
		case R.id.rela_delete_song: { // 删除歌曲
			// for (int i = 0; i < checkList.size(); i++) {
			// L.e("" + checkList.get(i).get(Constant.SONGLIST_CHECKED));
			// }

			delete_load_music_id = new long[mCount];
			for (int i = 0; i < mCount; i++) {
				delete_load_music_id[i] = -1;
			}

			// 保存被选中的歌曲id
			for (int i = 0; i < mCount; i++) {

				if (checkList.get(i).get(Constant.SONGLIST_CHECKED)
						.equals("true")) {
					delete_load_music_id[i] = songMp3Infos.get(i).getId();
				}
			}

			/*
			 * 数据库操作 将对应的歌曲在歌单-歌曲表中删除
			 */
			for (int i = 0; i < mCount; i++) {
				try {

					// 判断这首歌是否已存在在歌单-歌曲表中
					if (delete_load_music_id[i] != -1) {

						SongAndMusicInfo delete_load_music = new SongAndMusicInfo();
						delete_load_music = app.dbUtils
								.findFirst(Selector
										.from(SongAndMusicInfo.class)
										.where("songlistInfoId", "=", songId)
										.and("mp3InfoId", "=",
												delete_load_music_id[i]));
						app.dbUtils.delete(delete_load_music);
						// L.e("delete");
						count++;
					}
				} catch (DbException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			// 更新歌单表中，歌曲数量
			try {
				SongListInfo songListInfo = app.dbUtils.findById(
						SongListInfo.class, songId);
				int x = mCount - count;
				songListInfo.setCount(x);
				app.dbUtils.update(songListInfo, "count");
				// L.e("count===" + count);
				// L.e(songListInfo+"");
			} catch (DbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			T.showShort(app, "成功删除" + count + "首歌曲");
			Intent delete_intent = new Intent(this,
					SongIncludeMusicActivity.class);
			delete_intent.putExtra(Constant.SONGMAP_KEY, songlist_name);
			startActivity(delete_intent);
			finish();
			break;
		}
		default:
			break;
		}
	}

	@Override
	public void publish(int progress) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void change(int position) {
		// TODO Auto-generated method stub
		
	}
}
