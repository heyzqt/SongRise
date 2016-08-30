package com.example.songriseplayer;

import java.util.ArrayList;
import java.util.HashMap;
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
import android.widget.TextView;

/**
 * 本地添加歌曲界面
 * 
 * @author zq
 * 
 */
public class AddMusicLocalActivity extends Activity implements OnClickListener,
		OnItemClickListener {

	// 声明控件
	private TextView text_add_local_choice;
	private ImageView img_add_local_back;
	private LinearLayout linearlayout_add_local;
	private ListView listview_add_local;

	// 声明必须的对象
	private ArrayList<Mp3Info> mp3Infos;
	private MusicManageListAdapter mAdapter;
	private ArrayList<Map<String, String>> checkList; // 保存歌曲checked看是否被选中
	private int mCount; // 本地歌曲数量
	private long[] add_music_id; // 记录添加歌曲的下标
	private long songId; // 保存当前歌单的id
	private int count; // 记录添加歌曲的数量
	private SongRisePlayerApp app;
	private String songlist_name; // 记录当前歌单的名字

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_music_local_list);

		init();
		// loadData();
	}

	/**
	 * 初始化界面
	 */
	private void init() {
		text_add_local_choice = (TextView) findViewById(R.id.text_add_local_choice);
		img_add_local_back = (ImageView) findViewById(R.id.img_add_local_back);
		linearlayout_add_local = (LinearLayout) findViewById(R.id.linearlayout_add_local);
		listview_add_local = (ListView) findViewById(R.id.listview_add_local);
		text_add_local_choice.setOnClickListener(this);
		img_add_local_back.setOnClickListener(this);
		linearlayout_add_local.setOnClickListener(this);
		listview_add_local.setOnItemClickListener(this);

		app = (SongRisePlayerApp) getApplication();
		Intent getIntent = getIntent();
		songId = getIntent.getLongExtra(Constant.SONGLIST_ID, -1);

		// 记录当前歌单的名字
		try {
			songlist_name = app.dbUtils.findById(SongListInfo.class, songId)
					.getTitle();
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//设置标题栏颜色
		AppUtils.setSystemStatusBar(AddMusicLocalActivity.this);
	}

	/**
	 * 加载数据
	 */
	private void loadData() {

		mp3Infos = MediaUtils.getMp3Infos(this);
		mCount = mp3Infos.size();
		checkList = new ArrayList<Map<String, String>>();
		// 将歌曲默认标记为未选中
		for (int i = 0; i < mCount; i++) {
			Map<String, String> songmap_checked = new HashMap<String, String>();
			songmap_checked.put(Constant.SONGLIST_CHECKED, "false");
			checkList.add(songmap_checked);
		}

		mAdapter = new MusicManageListAdapter(this, mp3Infos, checkList);
		listview_add_local.setAdapter(mAdapter);
	}

	// Activity创建或者从被覆盖、后台重新回到前台时被调用
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// 绑定服务
		// bindPlayService();
		loadData();
	}

	// Activity被覆盖到下面或者锁屏时被调用
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		// unbindPlayService();
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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.text_add_local_choice: { // 歌曲全选按钮

			L.e("" + checkList);
			String str = text_add_local_choice.getText().toString();
			if (str.equals("全选")) {
				text_add_local_choice.setText("取消全选");
				for (int i = 0; i < checkList.size(); i++) {
					if (checkList.get(i).get(Constant.SONGLIST_CHECKED)
							.equals("false")) {
						checkList.get(i).put(Constant.SONGLIST_CHECKED, "true");
					}
					continue;
				}
				mAdapter.notifyDataSetChanged();
			} else {
				text_add_local_choice.setText("全选");
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
			L.e("" + checkList);
			break;
		}
		case R.id.img_add_local_back: { // 返回按钮
			Intent back_intent = new Intent(this,
					SongIncludeMusicActivity.class);
			back_intent.putExtra(Constant.SONGMAP_KEY, songlist_name);
			startActivity(back_intent);
			finish();
			break;
		}
		case R.id.linearlayout_add_local: { // 将歌曲加入歌单

			// 通过checkList来保存歌曲id
			add_music_id = new long[mCount];
			for (int i = 0; i < mCount; i++) {
				add_music_id[i] = -1;
			}

			int x = 0;
			for (int i = 0; i < checkList.size(); i++) {
				// L.e("" + checkList.get(i).get(Constant.SONGLIST_CHECKED));
				if (checkList.get(i).get(Constant.SONGLIST_CHECKED)
						.equals("true")) {

					// 保存歌曲id
					add_music_id[x] = mp3Infos.get(i).getId();
					x++;
				}
			}

			// 将选中的歌曲加入歌单-歌曲信息表
			// 往数据库存放相对应的信息
			for (int i = 0; i < add_music_id.length; i++) {
				try {
					// 判断这首歌是否已存在在歌单-歌曲表中
					SongAndMusicInfo songAndMusicInfo = app.dbUtils
							.findFirst(Selector.from(SongAndMusicInfo.class)
									.where("songlistInfoId", "=", songId)
									.and("mp3InfoId", "=", add_music_id[i]));
					// L.e(songAndMusicInfo+"");

					if (songAndMusicInfo == null && add_music_id[i] != -1) {
						SongAndMusicInfo addSongAndMusicInfo = new SongAndMusicInfo();
						addSongAndMusicInfo.setMp3InfoId(add_music_id[i]);
						addSongAndMusicInfo.setSonglistInfoId(songId);
						app.dbUtils.save(addSongAndMusicInfo);
						// L.e("save");
						count++;
					}

				} catch (DbException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			// 更新歌单表中，歌曲数量
			try {
				int num = app.dbUtils.findAll(
						Selector.from(SongAndMusicInfo.class).where(
								"songlistInfoId", "=", songId)).size();
				SongListInfo songListInfo = app.dbUtils.findById(
						SongListInfo.class, songId);
				songListInfo.setCount(num);
				app.dbUtils.update(songListInfo, "count");
				// L.e("count===" + songListInfo.getCount());
			} catch (DbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			T.showShort(app, "成功添加" + count + "首歌曲");
			Intent add_intent = new Intent(this, SongIncludeMusicActivity.class);
			add_intent.putExtra(Constant.SONGMAP_KEY, songlist_name);
			startActivity(add_intent);
			finish();
			break;
		}
		default:
			break;
		}
	}
}
