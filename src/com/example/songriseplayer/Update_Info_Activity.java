package com.example.songriseplayer;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.app.SongRisePlayerApp;
import com.example.utils.AppUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
 * 用户修改个性签名界面
 * 
 * @author 张艳琴 功能：用户编辑并向服务器以及上一个界面发送个性签名并保存 后期完善：保存个性签名至本地
 */
public class Update_Info_Activity extends Activity implements OnClickListener {
	ImageView back;
	EditText ed_info;
	Button submit;
	String url, url1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info_update);
		back = (ImageView) findViewById(R.id.imageview_info_back);
		ed_info = (EditText) findViewById(R.id.edit_info_new);
		submit = (Button) findViewById(R.id.button_info_submit);

		back.setOnClickListener(this);
		submit.setOnClickListener(this);

		// 设置标题栏颜色
		AppUtils.setSystemStatusBar(Update_Info_Activity.this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.imageview_info_back: {
			this.finish();
			break;
		}
		case R.id.button_info_submit: {
			final String info = ed_info.getText().toString().trim();
			String uid = SongRisePlayerApp.sp.getString("userid", "0");
			url = getResources().getString(R.string.ip_address);
			String url1 = url + "User/update_info";
			AsyncHttpClient client = new AsyncHttpClient();
			// 新建传参对象
			RequestParams params = new RequestParams();
			// 传入参数
			params.put("userid", uid);// 需要从共享池获取
			params.put("userinfo", info);
			if (SongRisePlayerApp.sp.getString("wifi_only", "no").equals("no")
					|| (SongRisePlayerApp.sp.getString("wifi_only", "no")
							.equals("yes") && (isWifi(Update_Info_Activity.this)))) {// wifi联网
				client.post(url1, params, new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						// TODO Auto-generated method stub
						JSONObject lan;
						try {
							lan = new JSONObject(new String(arg2));
							String succ = lan.getString("success");
							if (succ.equals("yes")) {
								Intent intent = new Intent();
								intent.putExtra("infos", info);
								Update_Info_Activity.this.setResult(1, intent);
								Update_Info_Activity.this.finish();// 结束焦点
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
							Throwable arg3) {
						// TODO Auto-generated method stub

					}
				});
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
