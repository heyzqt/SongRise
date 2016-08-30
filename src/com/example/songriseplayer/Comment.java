package com.example.songriseplayer;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.ErrorListener;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.adapter.MyAdapter;
import com.example.app.SongRisePlayerApp;
import com.example.dialog.CommentDialog;
import com.example.dialog.CustomDialog;
import com.example.utils.AppUtils;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class Comment extends Activity {

	private LinearLayout addcomment;
	private ListView comment;
	private TextView title;
	private ImageView back;
	private List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();;
	private MyAdapter CommentAdapter;
	protected String content;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.song_comment);

		initview();
		getResourceFromSerVice();
		initevent();
	}

	// @Override
	// protected void onResume() {
	// // TODO Auto-generated method stub
	// super.onResume();
	// initview();
	// getResourceFromSerVice();
	// System.out.println("onresume");
	// }

	/*
	 * 从服务器获取评论列表
	 */
	private void getResourceFromSerVice() {
		// TODO 自动生成的方法存根

		AsyncHttpClient client = new AsyncHttpClient();

		data.clear();
		RequestParams rq = new RequestParams();
		//
		rq.put("songid", title.getText().toString().substring(3));

		if (SongRisePlayerApp.sp.getString("wifi_only", "no").equals("no")
				|| (SongRisePlayerApp.sp.getString("wifi_only", "no").equals(
						"yes") && (isWifi(Comment.this) == 1))) {

			client.post(getResources().getString(R.string.ip_address).trim()
					+ "Comment/selectlist".trim(), rq,
					new AsyncHttpResponseHandler() {

						@Override
						public void onSuccess(int arg0, Header[] arg1,
								byte[] arg2) {
							// TODO Auto-generated method stub
							try {

								JSONObject root = new JSONObject(new String(
										arg2));

								int num = root.getInt("number");

								if (num == 0) {
									Toast.makeText(Comment.this, "暂时没有评论..",
											Toast.LENGTH_SHORT).show();
								} else {
									JSONArray dle = root.getJSONArray("data");

									for (int i = 0; i < dle.length(); i++) {
										JSONObject co = dle.getJSONObject(i);
										Map<String, Object> map = new HashMap<String, Object>();
										map.put("comment_pic", getResources()
												.getString(R.string.ip_img)
												.trim()
												+ co.getString("userimg"));
										map.put("user_name",
												co.getString("username"));
										map.put("comment_time",
												co.getString("Commenttime"));
										map.put("comment_content",
												co.getString("commentall"));
										data.add(map);
									}
									comment.setVisibility(View.GONE);
									CommentAdapter.notifyDataSetChanged();
									comment.setVisibility(View.VISIBLE);
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

						@Override
						public void onFailure(int arg0, Header[] arg1,
								byte[] arg2, Throwable arg3) {
							// TODO Auto-generated method stub
							Toast.makeText(getApplicationContext(), "网络不太稳定..",
									Toast.LENGTH_LONG).show();
						}
					});
		} else {
			Toast.makeText(Comment.this, "请切换网络..", Toast.LENGTH_SHORT).show();
		}

	}

	/*
	 * 初始化点击事件
	 */
	private void initevent() {
		// TODO Auto-generated method stub
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		addcomment.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (isWifi(Comment.this) == 1 || isWifi(Comment.this) == 2) {
					String str = SongRisePlayerApp.sp.getString("userid", "0");
					if (!str.equals("0")) {
						CommentDialogShow();
					} else {
						CommentDialogShow();
						Toast.makeText(Comment.this, "没有登录", 1).show();
					}
				} else {
					Toast.makeText(Comment.this, "没有网络连接..", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});
	}

	/*
	 * 自定义dialog显示框
	 */
	protected void CommentDialogShow() {
		// TODO Auto-generated method stub

		final CommentDialog dialog = new CommentDialog(this,
				R.style.MyDialog);
		dialog.show();

		Button confirm = (Button) dialog.findViewById(R.id.positiveButton);
		Button cancel = (Button) dialog.findViewById(R.id.negativeButton);
		final EditText edt = (EditText) dialog
				.findViewById(R.id.edittext_song_list_name);		
		
		confirm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				content = edt.getText().toString().trim();

				Map<String, Object> map = new HashMap<String, Object>();

				// map.put("comment_pic","");
				// map.put("user_name",SongRisePlayerApp.sp.getString("username",""));
				// map.put("comment_time", "刚刚");
				map.put("comment_content", content);
				// data.add(map);
				// CommentAdapter.notifyDataSetChanged();

				SendToService(map);
				//dialog.dismiss();// pay attention
			}
		});
		
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dialog.dismiss();// pay attention
			}
		});
//		
//		AlertDialog.Builder buil = new AlertDialog.Builder(Comment.this);
//		buil.setTitle("评论：");
//		buil.setIcon(android.R.drawable.ic_dialog_info);
//		final EditText Edittext;
//		buil.setView(Edittext = new EditText(this));
//		buil.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//
//			@Override
//			public void onClick(DialogInterface arg0, int arg1) {
//				// TODO Auto-generated method stub
//				content = Edittext.getText().toString().trim();
//
//				Map<String, Object> map = new HashMap<String, Object>();
//
//				// map.put("comment_pic","");
//				// map.put("user_name",SongRisePlayerApp.sp.getString("username",""));
//				// map.put("comment_time", "刚刚");
//				map.put("comment_content", content);
//				// data.add(map);
//				// CommentAdapter.notifyDataSetChanged();
//
//				SendToService(map);
//			}
//		});
//		buil.setNegativeButton("取消", null).show();
	}

	/*
	 * 将评论发送到服务器
	 */
	protected void SendToService(Map map) {
		// TODO Auto-generated method stub
		// 服务器交互

		AsyncHttpClient client = new AsyncHttpClient();

		RequestParams rp = new RequestParams();
		rp.add("content", map.get("comment_content").toString());
		rp.add("userid", SongRisePlayerApp.sp.getString("userid", "1"));
		rp.add("songid", title.getText().toString().substring(3));

		client.post(getResources().getString(R.string.ip_address)
				+ "Comment/adds", rp, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				// TODO Auto-generated method stub
				Toast.makeText(Comment.this, "评论成功", Toast.LENGTH_SHORT).show();
				// getResourceFromSerVice();

				Map<String, Object> map1 = new HashMap<String, Object>();
				map1.put("comment_pic", "");
				map1.put("user_name",
						SongRisePlayerApp.sp.getString("username", ""));
				map1.put("comment_time", "刚刚");
				map1.put("comment_content", content);

				data.add(map1);
				comment.setVisibility(View.GONE);
				CommentAdapter.notifyDataSetChanged();
				comment.setVisibility(View.VISIBLE);

			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
				Toast.makeText(Comment.this, "网络有问题..请稍候再试", Toast.LENGTH_SHORT)
						.show();
			}
		});
	}

	/*
	 * 初始化控件
	 */
	private void initview() {
		// TODO Auto-generated method stub

		addcomment = (LinearLayout) findViewById(R.id.add_comment);
		comment = (ListView) findViewById(R.id.commentlist);
		title = (TextView) findViewById(R.id.comment_title);
		back = (ImageView) findViewById(R.id.comment_back);

		CommentAdapter = new MyAdapter(Comment.this, data,
				R.layout.comment_list, new String[] { "comment_pic",
						"user_name", "comment_time", "comment_content" },
				new int[] { R.id.comment_pic, R.id.user_name,
						R.id.comment_time, R.id.comment_content });
		comment.setAdapter(CommentAdapter);

		Intent in = getIntent();

		title.setText("评论:" + in.getStringExtra("MusicName"));

		// 设置标题栏颜色
		AppUtils.setSystemStatusBar(Comment.this);

	}

	private static int isWifi(Context mContext) {
		ConnectivityManager connectivityManager = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null
				&& activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			return 1;
			// wifi链接
		} else if (activeNetInfo == null) {
			return 0;
			// 没有网
		}
		return 2;
		// 数据链接
	}

}
