package com.example.songriseplayer;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.app.SongRisePlayerApp;
import com.example.songriseplayer.MainActivity.ClassCut;
import com.example.utils.AppUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 用户注册界面
 * 
 * @author zyq
 * 
 */

public class UserRegisterActivity extends Activity implements OnClickListener {
	ImageView image_back;
	EditText ed_phone;
	EditText ed_yzm;
	EditText ed_psw;
	EditText ed_psw1;
	EditText ed_name;
	TextView te_yzm;
	Button bt_rg;
	String url;
	// String appkey = "8ef6c8c2097f";
	// String apppsw = "b5cfcdc1e0cc39daa6ff2e41a2185c9f";
	String appkey = "10ee065930b5c";
	String apppsw = "606014c1382cab9a3bc94d4bdc57ec75";

	private Handler mHandler = new Handler();// 全局handle
	int i = 0;// 倒计时的整个时间数

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_register_activity);
		image_back = (ImageView) findViewById(R.id.image_rg_back);
		ed_phone = (EditText) findViewById(R.id.edit_phone_new);
		ed_psw = (EditText) findViewById(R.id.edit_psw_new);
		ed_psw1 = (EditText) findViewById(R.id.edit_psw_new1);
		ed_name = (EditText) findViewById(R.id.edit_reg_name);
		ed_yzm = (EditText) findViewById(R.id.edit_yanz);
		te_yzm = (TextView) findViewById(R.id.text_yanz);
		bt_rg = (Button) findViewById(R.id.Button_Register);

		image_back.setOnClickListener(this);
		te_yzm.setOnClickListener(this);
		ed_psw.setOnClickListener(this);
		bt_rg.setOnClickListener(this);
		initview();

		// 设置标题栏颜色
		AppUtils.setSystemStatusBar(UserRegisterActivity.this);

	}

	private void initview() {
		// TODO Auto-generated method stub
		SMSSDK.initSDK(this, appkey, apppsw);
		final Handler handler = new Handler();
		EventHandler eventHandler = new EventHandler() {
			public void afterEvent(int event, int result, Object data) {
				Message msg = new Message();
				msg.arg1 = event;
				msg.arg2 = result;
				msg.obj = data;
				handler.sendMessage(msg);
			}
		};
		SMSSDK.registerEventHandler(eventHandler);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.text_yanz: {// 点击向指定号码发送验证码短信

			if (i <= 0) {
				te_yzm.setClickable(false);
				i = 60;
				new Thread(new ClassCut()).start();// 开启倒计时
				if (IsPhoneNum(ed_phone.getText().toString().trim())) {

					if (ed_phone.getText().toString().trim().isEmpty()) {
						Toast.makeText(getApplicationContext(), "电话号码不能为空！",
								Toast.LENGTH_LONG).show();
					} else {
						String phone = ed_phone.getText().toString().trim();
						SMSSDK.getVerificationCode("86", phone);
						i = 60;
						new Thread(new ClassCut()).start();// 开启倒计时
						// 发送网络请求验证电话号码是否存在

						AsyncHttpClient client = new AsyncHttpClient();
						RequestParams params = new RequestParams();
						params.put("phone", ed_phone.getText().toString()
								.trim());

						url = getResources().getString(R.string.ip_address);
						String url1 = url + "Login/select_user1";
						if (SongRisePlayerApp.sp.getString("wifi_only", "no")
								.equals("no")
								|| (SongRisePlayerApp.sp.getString("wifi_only",
										"no").equals("yes") && (isWifi(UserRegisterActivity.this)))) {// wifi联网

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
											try {
												JSONObject lan = new JSONObject(
														new String(arg2));
												String err = lan
														.getString("error");
												if (err.equals("yes")) {
													Toast.makeText(
															getApplicationContext(),
															"用户已存在！",
															Toast.LENGTH_LONG)
															.show();
												}
												String uid = lan
														.getString("counts");
												Editor editor = SongRisePlayerApp.sp
														.edit();
												editor.putString("userid", uid);
												editor.commit();
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
					Toast.makeText(getApplicationContext(), "输入正确的手机号码",
							Toast.LENGTH_LONG).show();
				}
			}

			break;
		}
		case R.id.image_rg_back: {// 点击返回上一个界面
			this.finish();
			break;
		}
		case R.id.Button_Register: {
			String psw = ed_psw.getText().toString().trim();
			String psw1 = ed_psw1.getText().toString().trim();
			if (!psw.equals(psw1)) {
				Toast.makeText(UserRegisterActivity.this, "两次密码不一致",
						Toast.LENGTH_SHORT).show();
				ed_psw1.setText("");

			} else {// 向服务器发送数据

				AsyncHttpClient client = new AsyncHttpClient();
				RequestParams params = new RequestParams();
				params.put("name", ed_name.getText().toString().trim());
				params.put("phone", ed_phone.getText().toString().trim());
				params.put("userpassword", ed_psw.getText().toString().trim());

				url = getResources().getString(R.string.ip_address);
				String url1 = url + "Login/add_user";

				if (SongRisePlayerApp.sp.getString("wifi_only", "no").equals(
						"no")
						|| (SongRisePlayerApp.sp.getString("wifi_only", "no")
								.equals("yes") && (isWifi(UserRegisterActivity.this)))) {// wifi联网
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
								String userid = lan.getString("userid");
								Editor editor = SongRisePlayerApp.sp.edit();
								editor.putString("userid", userid);
								editor.putString("userphone", ed_phone
										.getText().toString().trim());
								editor.commit();

							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});
				}
				Intent in = new Intent();
				in.setClass(UserRegisterActivity.this, MainActivity.class);
				startActivity(in);
			}
			break;
		}
		case R.id.edit_psw_new: {

			if (HasCode(ed_yzm.getText().toString().trim())) {
				String phone = ed_phone.getText().toString().trim();
				String Code = ed_yzm.getText().toString().trim();
				SMSSDK.submitVerificationCode("86", phone, Code);

				EventHandler eh = new EventHandler() {

					public void afterEvent(int event, int result, Object data) {

						if (result == SMSSDK.RESULT_COMPLETE) {
							runOnUiThread(new Runnable() {
								public void run() {
									te_yzm.setText("验证成功！");
								}
							});
						} else {
							runOnUiThread(new Runnable() {
								public void run() {
									Toast.makeText(UserRegisterActivity.this,
											"验证码错误", Toast.LENGTH_SHORT).show();
									// btn_.setClickable(false);
								}
							});
							((Throwable) data).printStackTrace();
						}
					}
				};

				SMSSDK.registerEventHandler(eh);
			}

			break;
		}
		default:
			break;
		}

	}

	private boolean HasCode(String trim) {// 验证码为空
		// TODO Auto-generated method stub

		return true;
	}

	private boolean IsPhoneNum(String string) {// 电话号码格式不对
		// TODO Auto-generated method stub

		return true;
	}

	protected void onDestroy() {
		SMSSDK.unregisterAllEventHandler();
		super.onDestroy();
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
				i--;
				mHandler.post(new Runnable() {// 通过它在UI主线程中修改显示的剩余时间
					@Override
					public void run() {
						// TODO Auto-generated method stub

						te_yzm.setText("重新获取/" + i + "S");// 显示剩余时间
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
					te_yzm.setText("重新发送验证码");// 一轮倒计时结束 修改剩余时间为-1
					te_yzm.setClickable(true);
				}
			});
			i = 0;// 修改倒计时剩余时间变量为60秒
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
