package com.example.songriseplayer;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.app.Constant;
import com.example.app.SongRisePlayerApp;
import com.example.utils.AccessTokenKeeper;
import com.example.utils.AppUtils;
import com.example.utils.ErrorInfo;
import com.example.utils.L;
import com.example.utils.User;
import com.example.vo.QqUserInfo;
import com.example.vo.QqUtil;
import com.example.vo.UsersAPI;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;

import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.utils.LogUtil;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQAuth;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 用户登录界面
 * 
 * @author:张艳琴 用户输入电话号码进行登录、qq第三方登陆(上传了从qq获取的信息至服务器)，并保存了userid/tel在本地共享池
 *             后期完善：微博第三方登陆（因为开发者审核还未通过，清明节完成）
 */
public class UserLoginActivity extends Activity implements OnClickListener {
	Button bt_login;
	ImageView image_qq;
	ImageView image_weixin;
	ImageView image_sina;

	EditText ed_name;
	EditText ed_psw;

	TextView text_forget;
	TextView text_rg;
	// qq第三方登陆部分
	public static String mAppid;

	public static QQAuth mQQAuth;
	private UserInfo mInfo;
	private Tencent mTencent;
	private final String APP_ID = "1105213097";// 自己的APP_ID
	Bitmap bitmap1;
	String name1;
	String sex1;
	// String userid;
	QqUserInfo Qquser;

	String url;

	// 微博第三方登陆部分
	private static final String TAG = "weibosdk";
	// private TextView mTokenText;
	private AuthInfo mAuthInfo;
	private Oauth2AccessToken mAccessToken;
	private SsoHandler mSsoHandler;

	// private UsersAPI mUsersAPI;//zyq

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_login_activity);
		bt_login = (Button) findViewById(R.id.Button_Login);
		image_qq = (ImageView) findViewById(R.id.ImageView_qq);
		image_weixin = (ImageView) findViewById(R.id.ImageView_weixin);
		image_sina = (ImageView) findViewById(R.id.ImageView_sina);

		ed_name = (EditText) findViewById(R.id.edit_name);
		ed_psw = (EditText) findViewById(R.id.edit_psw);

		text_forget = (TextView) findViewById(R.id.text_login_forget);
		text_rg = (TextView) findViewById(R.id.text_login_rg);

		text_forget.setOnClickListener(this);
		text_rg.setOnClickListener(this);
		bt_login.setOnClickListener(this);
		image_qq.setOnClickListener(this);
		image_weixin.setOnClickListener(this);
		image_sina.setOnClickListener(this);
		updateLoginButton();
		Qquser = new QqUserInfo();
		// 初始化微博第三方登陆
		mAuthInfo = new AuthInfo(this, Constant.APP_KEY, Constant.REDIRECT_URL,
				Constant.SCOPE);
		mSsoHandler = new SsoHandler(UserLoginActivity.this, mAuthInfo);
		// 获取当前已保存过的 Token----zyq
		mAccessToken = AccessTokenKeeper.readAccessToken(this);
		// 获取用户信息接口
		// mUsersAPI = new UsersAPI(this, Constant.APP_KEY, mAccessToken);
		// 从 SharedPreferences 中读取上次已保存好 AccessToken 等信息，
		// 第一次启动本应用，AccessToken 不可用

		mAccessToken = AccessTokenKeeper.readAccessToken(this);
		if (mAccessToken.isSessionValid()) {
			updateTokenView(true);
		}

		Editor editor3 = SongRisePlayerApp.sp.edit();
		editor3.putString("weibos", "no");
		editor3.commit();
		
		//设置标题栏颜色
		AppUtils.setSystemStatusBar(UserLoginActivity.this);

	}

	@Override
	protected void onStart() {
		// Log.d(TAG, "-->onStart");
		final Context context = UserLoginActivity.this;
		final Context ctxContext = context.getApplicationContext();
		mAppid = APP_ID;
		mQQAuth = QQAuth.createInstance(mAppid, ctxContext);
		mTencent = Tencent.createInstance(mAppid, UserLoginActivity.this);
		super.onStart();
	}

	private void updateLoginButton() {
		if (mQQAuth != null && mQQAuth.isSessionValid()) {

		} else {

		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Context context = v.getContext();
		Class<?> cls = null;
		switch (v.getId()) {
		case R.id.text_login_forget: {// 忘记密码，向注册的电话号码发送重置密码的短信
			break;
		}
		case R.id.text_login_rg: {// 进入注册界面
			startActivity(new Intent(UserLoginActivity.this,
					UserRegisterActivity.class));
			break;
		}
		case R.id.Button_Login: {// 进入主界面，向服务器端发送电话号码和密码，进入主界面
			final String tel = ed_name.getText().toString().trim();
			String psw = ed_psw.getText().toString().trim();
			if (tel.isEmpty() || psw.isEmpty()) {
				Toast.makeText(this, "请输入电话号码或密码", Toast.LENGTH_LONG).show();
			} else {
				Pattern p = Pattern
						.compile("^((13\\d{9}$)|(15[0,1,2,3,4,5,6,7,8,9]\\d{8}$)|(18[0,1,2,3,4,5,6,7,8,9]\\d{8}$)|(147\\d{8})$)");
				Matcher m = p.matcher(tel);
				if (!m.matches()) {
					Toast.makeText(this, "请输入正确的电话号码", Toast.LENGTH_LONG)
							.show();
					ed_name.setText("");
				} else {
					url = getResources().getString(R.string.ip_address);
					String url1 = url + "Login/user_login";
					AsyncHttpClient client = new AsyncHttpClient();
					// 新建传参对象
					RequestParams params = new RequestParams();
					// 传入参数
					params.put("userphone", tel);
					params.put("userpassword", psw);
					if (SongRisePlayerApp.sp.getString("wifi_only", "no")
							.equals("no")
							|| (SongRisePlayerApp.sp.getString("wifi_only",
									"no").equals("yes") && (isWifi(UserLoginActivity.this)))) {// wifi联网
						client.post(url1, params,
								new AsyncHttpResponseHandler() {

									@Override
									public void onFailure(int arg0,
											Header[] arg1, byte[] arg2,
											Throwable arg3) {
										// TODO Auto-generated method stub
										Toast.makeText(UserLoginActivity.this,
												"网络连接失败", 0).show();
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
													.getString("error");
											if (errors.equals("200")) {
												String succe = lan
														.getString("success");
												if (succe.equals("yes")) {
													String uuid = lan
															.getString("userid");
													Editor editor = SongRisePlayerApp.sp
															.edit();
													// 向共享池存放用户的登陆信息
													editor.putString("userid",
															uuid);
													editor.putString(
															"userphone", tel);
													editor.commit();
													startActivity(new Intent(
															UserLoginActivity.this,
															MainActivity.class));
												} else {
													Toast.makeText(
															UserLoginActivity.this,
															"密码不正确 ",
															Toast.LENGTH_LONG)
															.show();
													ed_psw.setText("");
												}
											} else {
												Toast.makeText(
														UserLoginActivity.this,
														"用户不存在",
														Toast.LENGTH_LONG)
														.show();
												ed_psw.setText("");
											}

										} catch (JSONException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									}
								});

						L.e("网络请求后");
					}

				}
			}

			break;
		}
		case R.id.ImageView_qq: {// qq第三方登陆
			onClickLogin();
			Editor editor = SongRisePlayerApp.sp.edit();
			// 向共享池存放用户的登陆信息
			editor.putString("userid", "0");
			editor.putString("userphone", "0");
			editor.commit();
			return;
		}
		case R.id.ImageView_weixin: {// 微信第三方登陆
			break;
		}
		case R.id.ImageView_sina: {// 新浪第三方登陆
			// startActivity(new
			// Intent(UserLoginActivity.this,WBAuthActivity.class));
			// Login/select_user_count
			AsyncHttpClient client = new AsyncHttpClient();
			RequestParams params = new RequestParams();
			url = getResources().getString(R.string.ip_address);
			String url1 = url + "Login/select_user_count";
			if (SongRisePlayerApp.sp.getString("wifi_only", "no").equals("no")
					|| (SongRisePlayerApp.sp.getString("wifi_only", "no")
							.equals("yes") && (isWifi(UserLoginActivity.this)))) {// wifi联网
				client.post(url1, params, new AsyncHttpResponseHandler() {

					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
							Throwable arg3) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						// TODO Auto-generated method stub
						JSONObject lan;
						try {
							lan = new JSONObject(new String(arg2));
							String counts = lan.getString("counts");
							Editor editor = SongRisePlayerApp.sp.edit();
							// 向共享池存放用户的登陆信息
							editor.putString("userid", counts);
							editor.putString("userphone", "0");
							editor.commit();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
			}

			mSsoHandler.authorize(new AuthListener());
			break;

		}

		default:
			break;
		}
		if (cls != null) {
			Intent intent = new Intent(context, cls);
			context.startActivity(intent);
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

	private void updateUserInfo() {
		if (mQQAuth != null && mQQAuth.isSessionValid()) {
			IUiListener listener = new IUiListener() {

				@Override
				public void onError(UiError e) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onComplete(final Object response) {
					Message msg = new Message();
					msg.obj = response;
					msg.what = 0;
					mHandler.sendMessage(msg);
					new Thread() {

						@Override
						public void run() {
							JSONObject json = (JSONObject) response;
							if (json.has("figureurl")) {
								Bitmap bitmap = null;
								try {
									bitmap = QqUtil.getbitmap(json
											.getString("figureurl_qq_2"));
									bitmap1 = bitmap;// 自己添加的
									name1 = json.getString("nickname");
									sex1 = json.getString("gender");
									Qquser.setBitmap(bitmap);
									Qquser.setNickname(name1);
									Qquser.setSex(sex1);
									sendQqinfo(Qquser);
								} catch (JSONException e) {

								}
								Message msg = new Message();
								msg.obj = bitmap;
								msg.what = 1;
								mHandler.sendMessage(msg);
							}
						}

					}.start();
				}

				@Override
				public void onCancel() {
				}
			};
			mInfo = new UserInfo(this, mQQAuth.getQQToken());
			mInfo.getUserInfo(listener);

		} else {
			// mUserInfo.setText("");
			// mUserInfo.setVisibility(android.view.View.GONE);
			// mUserLogo.setVisibility(android.view.View.GONE);
		}
	}

	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				JSONObject response = (JSONObject) msg.obj;
				if (response.has("nickname")) {
					try {
						// mUserInfo.setVisibility(android.view.View.VISIBLE);
						// mUserInfo.setText(response.getString("nickname"));//获取昵称
						name1 = response.getString("nickname");
						sex1 = response.getString("gender");
						Qquser.setNickname(name1);
						Qquser.setSex(sex1);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} else if (msg.what == 1) {
				Bitmap bitmap = (Bitmap) msg.obj;
				// mUserLogo.setImageBitmap(bitmap);//获取头像
				// mUserLogo.setVisibility(android.view.View.VISIBLE);
				if (bitmap != null) {
					bitmap1 = bitmap;
					Qquser.setBitmap(bitmap);
					sendQqinfo(Qquser);
				}
			}
		}

	};

	private void onClickLogin() {
		if (!mQQAuth.isSessionValid()) {
			IUiListener listener = new BaseUiListener() {
				@Override
				protected void doComplete(JSONObject values) {
					updateUserInfo();
					updateLoginButton();
				}
			};
			mQQAuth.login(this, "all", listener);
			mTencent.login(this, "all", listener);
		} else {
			mQQAuth.logout(this);
			updateUserInfo();
			updateLoginButton();
		}
	}

	private class BaseUiListener implements IUiListener {

		@Override
		public void onComplete(Object response) {
			// Util.showResultDialog(MainActivity.this, response.toString(),
			// "登录成功");//登陆成功！
			String openidString = "12";
			try {
				openidString = ((JSONObject) response).getString("openid");
				Qquser.setOpenid(openidString);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Intent intent1 = new Intent();
			intent1.putExtra("openid", openidString);
			intent1.setClass(UserLoginActivity.this, MainActivity.class);
			startActivity(intent1);
			doComplete((JSONObject) response);
			if (bitmap1 != null) {
				Qquser.setBitmap(bitmap1);
				sendQqinfo(Qquser);
			}
		}

		protected void doComplete(JSONObject values) {

		}

		@Override
		public void onError(UiError e) {
			QqUtil.toastMessage(UserLoginActivity.this, "onError: "
					+ e.errorDetail);
			QqUtil.dismissDialog();
		}

		@Override
		public void onCancel() {
			QqUtil.toastMessage(UserLoginActivity.this, "onCancel: ");
			QqUtil.dismissDialog();
		}
	}

	public void sendQqinfo(QqUserInfo qqinfo) {
		Bitmap bm = qqinfo.getBitmap();
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 60, stream);
		byte[] bytes = stream.toByteArray();
		String img = new String(Base64.encodeToString(bytes, Base64.DEFAULT));
		String nicknames = qqinfo.getNickname();
		String sexs = qqinfo.getSex();
		final String openids = qqinfo.getOpenid();

		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		params.put("img", img);
		params.put("openid", openids);
		// params.put("userid", "58");
		params.put("relatype", "0");
		// String sexx="男";

		params.put("sex", sexs);
		params.put("name", nicknames);
		url = getResources().getString(R.string.ip_address);
		String url1 = url + "Login/add_user_qq";
		if (SongRisePlayerApp.sp.getString("wifi_only", "no").equals("no")
				|| (SongRisePlayerApp.sp.getString("wifi_only", "no").equals(
						"yes") && (isWifi(UserLoginActivity.this)))) {// wifi联网
			client.post(url1, params, new AsyncHttpResponseHandler() {

				@Override
				public void onFailure(int arg0, Header[] arg1, byte[] arg2,
						Throwable arg3) {
					// TODO Auto-generated method stub
					// Toast.makeText(MainActivity.this,"userid+++",
					// Toast.LENGTH_LONG).show();
				}

				@Override
				public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
					// TODO Auto-generated method stub
					JSONObject lan;
					try {
						lan = new JSONObject(new String(arg2));
						String userid = lan.getString("userid");
						String userphone = lan.getString("18308230989");
						Editor editor = SongRisePlayerApp.sp.edit();
						// 向共享池存放用户的登陆信息
						editor.putString("userid", userid);
						editor.putString("userphone", userphone);
						editor.commit();
						Toast.makeText(UserLoginActivity.this,
								"userid" + userid, Toast.LENGTH_LONG).show();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		if (bitmap1 != null)
			sendQqinfo(Qquser);
		super.onPause();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if (bitmap1 != null)
			sendQqinfo(Qquser);
	}

	/**
	 * 显示当前 Token 信息。
	 * 
	 * @param hasExisted
	 *            配置文件中是否已存在 token 信息并且合法
	 */
	private void updateTokenView(boolean hasExisted) {
		String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
				.format(new java.util.Date(mAccessToken.getExpiresTime()));
		String format = "R.string.weibosdk_demo_token_to_string_format_1";
		// mTokenText.setText(String.format(format, mAccessToken.getToken(),
		// date));

		String message = String.format(format, mAccessToken.getToken(), date);
		if (hasExisted) {
			message = "weibosdk_demo_token_has_existed" + "\n" + message;
		}
		// mTokenText.setText(message);
	}

	/**
	 * 当 SSO 授权 Activity 退出时，该函数被调用。
	 * 
	 * @see {@link Activity#onActivityResult}
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// SSO 授权回调
		// 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResults
		if (mSsoHandler != null) {
			mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
		mAccessToken = AccessTokenKeeper.readAccessToken(this);
		// 获取用户信息接口
		// mUsersAPI = new UsersAPI(this, Constant.APP_KEY, mAccessToken);

	}

	// 微博第三方登陆监听
	/**
	 * 微博认证授权回调类。 1. SSO 授权时，需要在 {@link #onActivityResult} 中调用
	 * {@link SsoHandler#authorizeCallBack} 后， 该回调才会被执行。 2. 非 SSO
	 * 授权时，当授权结束后，该回调就会被执行。 当授权成功后，请保存该 access_token、expires_in、uid 等信息到
	 * SharedPreferences 中。
	 */
	class AuthListener implements WeiboAuthListener {

		@Override
		public void onComplete(Bundle values) {
			// 从 Bundle 中解析 Token
			mAccessToken = Oauth2AccessToken.parseAccessToken(values);
			// 从这里获取用户输入的 电话号码信息
			String phoneNum = mAccessToken.getPhoneNum();
			if (mAccessToken.isSessionValid()) {
				// 显示 Token
				updateTokenView(false);

				// 保存 Token 到 SharedPreferences
				AccessTokenKeeper.writeAccessToken(UserLoginActivity.this,
						mAccessToken);
				// Toast.makeText(UserLoginActivity.this,
				// "微博登陆成功",Toast.LENGTH_LONG).show();

				Editor editor3 = SongRisePlayerApp.sp.edit();
				editor3.putString("weibos", "yes");
				editor3.commit();
				startActivity(new Intent(UserLoginActivity.this,
						MainActivity.class));

			} else {

				String code = values.getString("code");
				// String message =
				// getString(R.string.weibosdk_demo_toast_auth_failed);
				String message = "weibosdk_demo_toast_auth_failed";
				if (!TextUtils.isEmpty(code)) {
					message = message + "\nObtained the code123: " + code;
				}
				Toast.makeText(UserLoginActivity.this, message,
						Toast.LENGTH_LONG).show();
			}
		}

		@Override
		public void onCancel() {
			Toast.makeText(UserLoginActivity.this,
					" R.string.weibosdk_demo_toast_auth_canceled",
					Toast.LENGTH_LONG).show();
		}

		@Override
		public void onWeiboException(WeiboException e) {
			Toast.makeText(UserLoginActivity.this,
					"Auth exception : " + e.getMessage(), Toast.LENGTH_LONG)
					.show();
		}
	}

}
