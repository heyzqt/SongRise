package com.example.songriseplayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.adapter.SongListAdapter;
import com.example.adapter.SongListManageAdapter;
import com.example.app.Constant;
import com.example.app.SongRisePlayerApp;
import com.example.utils.AppUtils;
import com.example.utils.L;
import com.example.utils.T;
import com.example.vo.SongAndMusicInfo;
import com.example.vo.SongListInfo;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class SongListManageActivity extends BaseActivity implements
		OnItemClickListener, OnClickListener {

	private SongListManageAdapter songListManageAdapter;
	private ListView listview_songlist;
	private ImageView imageview_back;
	private LinearLayout linearlayout1_delete;
	private List<List<Map<String, String>>> songlist = new ArrayList<List<Map<String, String>>>();
	private List<Map<String, String>> songlist_child = new ArrayList<Map<String, String>>();
	private int[] delete; // 记录被点击的item
	private int[] delete_id; // 标记被选中的项
	private int deleteCount; // 标记被选中项的数目
	private ArrayList music_song_list; // 从主页传递过来的歌单列表
	// private ArrayList<Map<String, String>> listSave = new
	// ArrayList<Map<String, String>>();
	private int listSize; // 保存传递过来的list的长度
	private String[] list_title;

	private SongRisePlayerApp app;
	ArrayList<SongAndMusicInfo> songAndMusicInfos = new ArrayList<SongAndMusicInfo>();

	// private int listSize; // 保存传递过来的list的长度

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
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.song_list_manage_activity);

		listview_songlist = (ListView) findViewById(R.id.listview_manage);
		imageview_back = (ImageView) findViewById(R.id.imageview_back);
		linearlayout1_delete = (LinearLayout) findViewById(R.id.linearlayout1_delete);
		listview_songlist.setOnItemClickListener(this);
		imageview_back.setOnClickListener(this);
		linearlayout1_delete.setOnClickListener(this);
		app = (SongRisePlayerApp) getApplication();

		// 设置标题栏颜色
		AppUtils.setSystemStatusBar(SongListManageActivity.this);

		loadData();
	}

	public void loadData() {
		/*
		 * 获取从主界面传过来的歌单列表
		 */
		// L.e("歌单管理intent==="+getIntent());
		Bundle bundle = getIntent().getExtras();
		music_song_list = bundle.getParcelableArrayList("songlist");
		// list2= (List<Object>)
		// list.get(0);//强转成你自己定义的list，这样list2就是你传过来的那个list了
		//L.e("music_song_list本地==="+music_song_list);
		// L.e("music_song_list.get(0)==="+music_song_list.get(0));
		songlist = (ArrayList<List<Map<String, String>>>) music_song_list
				.get(0);
		listSize = songlist.size();

		// 将PlayService的songlist封装到歌曲管理的songlist中
		// for (int i = 0; i < listSize; i++) {
		// songlist_child = new ArrayList<Map<String, String>>();
		// for (int j = 0; j < 1; j++) {
		// Map<String, String> songmap_name = new HashMap<String, String>();
		// Map<String, String> songmap_checked = new HashMap<String, String>();
		//
		// songmap_name.put("songlist_name",
		// "" + listSave.get(i).get("songlist_name"));
		// songmap_checked.put("songlist_checked", "false");
		// songlist_child.add(songmap_name);
		// songlist_child.add(songmap_checked);
		// }
		// songlist.add(songlist_child);
		// // System.out.println(songlist);
		// }
		for (int i = 0; i < listSize; i++) {
			Map<String, String> songmap_checked = new HashMap<String, String>();
			songmap_checked.put("songlist_checked", "false");
			songlist.get(i).add(songmap_checked);
		}

		delete = new int[listSize];
		for (int i = 0; i < listSize; i++) {
			delete[i] = -1;
		}

		songListManageAdapter = new SongListManageAdapter(this, songlist);
		listview_songlist.setAdapter(songListManageAdapter);
		
//		//数据库的歌单数据
//		try {
//			List<SongListInfo> list=app.dbUtils.findAll(SongListInfo.class);
//			if(list==null||list.size()==0){
//				return;
//			}else{
//				ArrayList<SongListInfo> songlist=(ArrayList<SongListInfo>) list;
//				for(int i=0;i<songlist.size();i++){
//					L.e("数据库songlist==="+songlist.get(i));
//				}
//			}
//			List<SongAndMusicInfo> songAndMusicInfos = app.dbUtils
//					.findAll(SongAndMusicInfo.class);
//			if (songAndMusicInfos != null && songAndMusicInfos.size() != 0) {
//				L.e("歌单歌曲表，songAndMusicInfos=====" + songAndMusicInfos);
//			} else {
//				L.e("歌单歌曲表为空");
//			}
//			
//		} catch (DbException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		// TODO Auto-generated method stub
		TextView textview_is_checked = (TextView) view
				.findViewById(R.id.textview_is_checked);
		ImageView imageview_circle = (ImageView) view
				.findViewById(R.id.imageview_circle);
		TextView textview_songlist_name = (TextView) view
				.findViewById(R.id.textview_song_list_name);

		String str = textview_is_checked.getText().toString();
		String strName = textview_songlist_name.getText().toString();

		if (playService.getChangePlayList() == playService.CURRENT_SONGLIST_PLAY
				&& strName.equals(app.sp.getString("songlistname", ""))) {
			// 除开当前播放的歌单不能删,其他都能删
			T.showShort(this, "当前歌单正在播放,不能删除");
		} else {
			// 随意删除歌单
			if (str.equals("false")) {
				imageview_circle
						.setImageResource(R.drawable.img_circle_checked);
				textview_is_checked.setText("true");
				delete[position] = position;
			} else {
				imageview_circle.setImageResource(R.drawable.img_circle);
				textview_is_checked.setText("false");
				delete[position] = -1;
			}
		}

//		// 随意删除歌单
//		if (str.equals("false")) {
//			imageview_circle
//					.setImageResource(R.drawable.img_circle_checked);
//			textview_is_checked.setText("true");
//			delete[position] = position;
//		} else {
//			imageview_circle.setImageResource(R.drawable.img_circle);
//			textview_is_checked.setText("false");
//			delete[position] = -1;
//		}
		
		//
		// //如果有正在播放的歌单
		// if(playService.getChangePlayList()!=playService.CURRENT_SONGLIST_PLAY){
		// //随意删除歌单
		// if (str.equals("false")) {
		// imageview_circle.setImageResource(R.drawable.img_circle_checked);
		// textview_is_checked.setText("true");
		// delete[position] = position;
		// } else {
		// imageview_circle.setImageResource(R.drawable.img_circle);
		// textview_is_checked.setText("false");
		// delete[position] = -1;
		// }
		// }
		// else{
		// if(strName.equals(playService.getSonglistname())){
		// //除开当前播放的歌单不能删,其他都能删
		// T.showShort(this, "当前歌单正在播放,不能删除");
		// }
		// else{
		// //可以删除
		// if (str.equals("false")) {
		// imageview_circle.setImageResource(R.drawable.img_circle_checked);
		// textview_is_checked.setText("true");
		// delete[position] = position;
		// } else {
		// imageview_circle.setImageResource(R.drawable.img_circle);
		// textview_is_checked.setText("false");
		// delete[position] = -1;
		// }
		// }
		// }

	}

	// 捕获手机的back键
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, MainActivity.class);
		Bundle bundle = new Bundle();

		// 这个list能用putParcelableArrayList()传递过去
		// 所以把真正要传的songlist包进去传过去
		ArrayList list = new ArrayList();
		list.add(songlist);
		bundle.putParcelableArrayList(Constant.SONGLIST, list);
		intent.putExtras(bundle);
		setResult(Constant.RESULT_OK, intent);
		//L.e("歌单管理===" + songlist);
		super.onBackPressed();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.imageview_back: { // 返回主页

			Intent intent = new Intent(this, MainActivity.class);
			Bundle bundle = new Bundle();

			// 方法一
			// 这个list能用putParcelableArrayList()传递过去
			// 所以把真正要传的songlist包进去传过去
			ArrayList list = new ArrayList();
			list.add(songlist);
			bundle.putParcelableArrayList(Constant.SONGLIST, list);
			intent.putExtras(bundle);
			// startActivity(intent);

			// 方法二
			// bundle.putInt("deletecount", deleteCount);
			// bundle.putIntArray("delete", delete);
			// bundle.putIntArray("delete_id", delete_id);
			// intent.putExtras(bundle);

			setResult(Constant.RESULT_OK, intent);
			finish();
			break;
		}

		case R.id.linearlayout1_delete: { // 删除歌单

			if (songlist != null || songlist.size() > 0) {

				try {

					/*
					 * 删除被点击的项 1.记录要被删除的有多少项 2.将要被删除的项下标放入int数组 3.删除list中对应的项
					 */
					int length = 0;
					for (int i = 0; i < delete.length; i++) {
						if (delete[i] != -1) {
							deleteCount++;
							// delete_id[length]=delete[i];
							// length++;
						}
					}

					delete_id = new int[deleteCount];
					for (int i = 0; i < delete.length; i++) {
						if (delete[i] != -1) {
							delete_id[length] = delete[i];
							length++;
						}
					}

					list_title = new String[deleteCount];
					// 保存要被删除的歌单title
					for (int i = 0; i < delete_id.length; i++) {
						list_title[i] = songlist.get(delete_id[i]).get(0)
								.get(Constant.SONGMAP_KEY);
					}

//					 for(int i=0;i<list_title.length;i++){
//						 L.e("被删除的歌单==="+list_title[i]);
//					 }

					/*
					 * 非常重要的删除思想
					 */
					for (int i = 0; i < delete_id.length; i++) {
						songlist.remove(delete_id[i] - i);
					}
					
//					L.e("被删除后的songlist==="+songlist);
//					L.e("delete_id,");
//					for(int i=0;i<delete_id.length;i++){
//						L.e("delete_id"+"  "+i+"   "+delete_id[i]);
//					}

					// 1.删除歌单-歌曲表所有此歌单信息
					// 2.删除歌单表此歌单信息

					try {
						for (int i = 0; i < delete_id.length; i++) {

							// 删除id会有问题,因为数据库的id是固定的，而list的id会随删除而改变
							// app.dbUtils.deleteById(SongListInfo.class,
							// delete_id[i] + 1);

							SongListInfo songlist_delete = app.dbUtils
									.findFirst(Selector
											.from(SongListInfo.class)
											.where("title", "=", list_title[i]));

							//L.e("songlist_delete===="+songlist_delete);
							// 先删除歌单-歌曲表中所有歌单信息
							// songAndMusicInfo=app.dbUtils.findFirst(Selector.from(SongAndMusicInfo.class).where("songlistInfoId",
							// "=", delete_id[i]));
							List<SongAndMusicInfo> list = app.dbUtils
									.findAll(Selector.from(
											SongAndMusicInfo.class).where(
											"songlistInfoId", "=",
											songlist_delete.getId()));
							songAndMusicInfos = (ArrayList<SongAndMusicInfo>) list;
							
							//L.e("songAndMusicInfos==="+songAndMusicInfos);
							for (int j = 0; j < songAndMusicInfos.size(); j++) {
								app.dbUtils.deleteById(SongAndMusicInfo.class,
										songAndMusicInfos.get(j).getId());
							}
							
							// 再删除歌单信息
							// 改用记录歌单名的方式来删除
							app.dbUtils.deleteById(SongListInfo.class,
									songlist_delete.getId());

						}
					} catch (DbException e) {
						// TODO: handle exception
						L.e("删除歌单歌曲信息bug");
					}

					// 清除被删除的项的记录
					for (int i = 0; i < delete.length; i++) {
						delete[i] = -1;
					}
					deleteCount = 0;
					songListManageAdapter.notifyDataSetChanged();
					//L.e("点击删除按钮===="+songlist);
					
					
//					try {
//						List<SongListInfo> songlistinfos = app.dbUtils
//								.findAll(SongListInfo.class);
//						if (songlistinfos != null && songlistinfos.size() != 0) {
//							L.e("歌单表，SongListInfo=====" + songlistinfos);
//						} else {
//							L.e("歌单表为空");
//						}
//						List<SongAndMusicInfo> songAndMusicInfos = app.dbUtils
//								.findAll(SongAndMusicInfo.class);
//						if (songAndMusicInfos != null && songAndMusicInfos.size() != 0) {
//							L.e("歌单歌曲表，songAndMusicInfos=====" + songAndMusicInfos);
//						} else {
//							L.e("歌单歌曲表为空");
//						}
//					} catch (DbException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//						L.e("获取歌单表或歌曲表失败");
//					}

				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					L.e("删除按钮处bug");
				}
			}

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
