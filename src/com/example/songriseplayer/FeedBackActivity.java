package com.example.songriseplayer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.app.SongRisePlayerApp;
import com.example.utils.AppUtils;
//import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * 用户反馈界面
 * 
 * @author 张艳琴 编辑反馈消息并，给服务器端发送
 */
public class FeedBackActivity extends Activity implements OnClickListener {
	ImageView back;
	EditText ed_phone;
	EditText ed_content;
	Button submit;
	String url, url1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.acvity_feed_back);
		back = (ImageView) findViewById(R.id.imageview_feed_back);
		submit = (Button) findViewById(R.id.bt_back_submit);
		ed_phone = (EditText) findViewById(R.id.edit_back_phone);
		ed_content = (EditText) findViewById(R.id.edit_back_content);

		back.setOnClickListener(this);
		submit.setOnClickListener(this);
		ed_phone.setOnClickListener(this);
		ed_content.setOnClickListener(this);

		// 设置标题栏颜色
		AppUtils.setSystemStatusBar(FeedBackActivity.this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.imageview_feed_back: {
			this.finish();
			break;
		}
		case R.id.bt_back_submit: {// 提交
			String tel = ed_phone.getText().toString().trim();
			String content = ed_content.getText().toString().trim();
			if (content.isEmpty()) {
				Toast.makeText(FeedBackActivity.this, "内容不能为空！",
						Toast.LENGTH_LONG).show();
			}
			if (tel.isEmpty()) {
				Toast.makeText(FeedBackActivity.this, "电话号码不能为空！",
						Toast.LENGTH_LONG).show();
			} else {
				Pattern p = Pattern
						.compile("^((13\\d{9}$)|(15[0,1,2,3,4,5,6,7,8,9]\\d{8}$)|(18[0,2,4,5,6,7,8,9]\\d{8}$)|(147\\d{8})$)");
				Matcher m = p.matcher(tel);
				if (!m.matches()) {
					Toast.makeText(this, "电话号码格式不正确", Toast.LENGTH_LONG).show();
					ed_phone.setText("");
				} else {
					url = getResources().getString(R.string.ip_address);
					String url1 = url + "Back/adds";
					AsyncHttpClient client = new AsyncHttpClient();
					// 新建传参对象
					RequestParams params = new RequestParams();
					// 传入参数
					params.put("backphone", tel);
					String uid = SongRisePlayerApp.sp.getString("userid", "0");
					params.put("userid", uid);// 需要从共享池获取
					params.put(" backcontent", content);
					if (SongRisePlayerApp.sp.getString("wifi_only", "no")
							.equals("no")
							|| (SongRisePlayerApp.sp.getString("wifi_only",
									"no").equals("yes") && (isWifi(FeedBackActivity.this)))) {// wifi联网
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
											lan = new JSONObject(new String(
													arg2));
											String errors = lan
													.getString("success");
											if (errors.equals("yes")) {
												Toast.makeText(
														FeedBackActivity.this,
														"反馈成功！",
														Toast.LENGTH_LONG)
														.show();
												FeedBackActivity.this.finish();
											} else {
												Toast.makeText(
														FeedBackActivity.this,
														"系统忙，稍后再试！",
														Toast.LENGTH_LONG)
														.show();
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

			break;
		}

		default:
			break;
		}

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
}
