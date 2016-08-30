package com.example.songriseplayer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.Volley;
import com.example.app.SongRisePlayerApp;
import com.example.dialog.UserCenter_Person_Dialog;
import com.example.songriseplayer.R;
import com.example.utils.AppUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * 个人中心
 * 
 * @author 张艳琴 功能：修改头像（图库选择并裁剪上传需要引入上传图片的jar包），修改昵称、性别、生日等信息并上传。
 *         后期完善：保存信息至本地，图片缓存在本地
 */
public class UserCenterActivity extends Activity implements OnClickListener {

	ImageView image_back;
	ImageView image_setting;
	ImageView image_touxiang;

	TextView text_name;
	TextView text_birth;
	TextView text_sex;
	TextView text_info;

	private LinearLayout liner_center_touxiang;
	private LinearLayout liner_center_name;
	private LinearLayout liner_center_birth;
	private LinearLayout liner_center_sex;
	private LinearLayout liner_center_info;

	Button Bu_exit;
	private Calendar calendar2;// 用来装日期的
	private DatePickerDialog dialog2;
	private static int CAMERA_REQUEST_CODE = 2;
	private static int GALLERY_REQUEST_CODE = 3;
	private static int CROP_REQUEST_CODE = 4;
	String url, url1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_center_activity);
		image_back = (ImageView) findViewById(R.id.imageview_center_back);
		image_setting = (ImageView) findViewById(R.id.imageview_center_setting);
		image_touxiang = (ImageView) findViewById(R.id.image_center_toux);
		text_name = (TextView) findViewById(R.id.Text_center_name);
		text_birth = (TextView) findViewById(R.id.Text_center_birth);
		text_sex = (TextView) findViewById(R.id.Text_center_sex);
		text_info = (TextView) findViewById(R.id.Text_center_info);

		liner_center_touxiang = (LinearLayout) findViewById(R.id.liner_center_touxiang);
		liner_center_name = (LinearLayout) findViewById(R.id.liner_center_name);
		liner_center_birth = (LinearLayout) findViewById(R.id.liner_center_birth);
		liner_center_sex = (LinearLayout) findViewById(R.id.liner_center_sex);
		liner_center_info = (LinearLayout) findViewById(R.id.liner_center_info);

		Bu_exit = (Button) findViewById(R.id.button_center_exit);

		image_back.setOnClickListener(this);
		liner_center_touxiang.setOnClickListener(this);
		image_setting.setOnClickListener(this);
		liner_center_name.setOnClickListener(this);
		liner_center_birth.setOnClickListener(this);
		liner_center_sex.setOnClickListener(this);
		liner_center_info.setOnClickListener(this);
		Bu_exit.setOnClickListener(this);
		// initview();

		// saveBitmapToSharedPreferences();
		// getBitmapFromSharedPreferences();
		
		// 设置标题栏颜色
		AppUtils.setSystemStatusBar(UserCenterActivity.this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initview();
	}

	void initview() {

		String uid = SongRisePlayerApp.sp.getString("userid", "0");
		url = getResources().getString(R.string.ip_address);
		String url1 = url + "User/select_user";
		AsyncHttpClient client = new AsyncHttpClient();
		// 新建传参对象
		RequestParams params = new RequestParams();
		// 传入参数
		params.put("userid", uid);// 从共享池获取
		if (SongRisePlayerApp.sp.getString("wifi_only", "no").equals("no")
				|| (SongRisePlayerApp.sp.getString("wifi_only", "no").equals(
						"yes") && (isWifi(UserCenterActivity.this)))) {// wifi联网
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
						String succ = lan.getString("success");
						if (succ.equals("yes")) {
							// text_name.setText(name);
							String name1 = lan.getString("username");
							String sex1 = lan.getString("usersex");
							String birth1 = lan.getString("userbirth");
							String info1 = lan.getString("userinfo");
							String image1 = lan.getString("userimg");

							text_name.setText(name1);
							text_sex.setText(sex1);
							text_birth.setText(birth1);
							text_info.setText(info1);
							// text_name.setText(name1);
							String url_img = getResources().getString(
									R.string.ip_img);
							String url_img1 = url_img + image1;
							ImageLoad(url_img1);

							// saveBitmapToSharedPreferences();
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		}

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.imageview_center_back: {
			// saveBitmapToSharedPreferences();
			this.finish();
			break;
		}
		case R.id.imageview_center_setting: {
			startActivity(new Intent(this, SettingActivity.class));
			// saveBitmapToSharedPreferences();
			break;
		}
		case R.id.liner_center_touxiang: {// 修改头像
			updateimage();
			break;
		}
		case R.id.liner_center_name: {// 修改昵称
			final UserCenter_Person_Dialog dialog = new UserCenter_Person_Dialog(
					UserCenterActivity.this, R.style.Translucent_NoTitle);

			dialog.show();

			Button confirm = (Button) dialog.findViewById(R.id.Button_name_ok);
			Button cancel = (Button) dialog.findViewById(R.id.Button_name_qx);
			final EditText edt = (EditText) dialog.findViewById(R.id.edit_name);

			confirm.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					String str = edt.getText().toString();
					if (str.equals("")) {
						Toast.makeText(UserCenterActivity.this, "昵称不能为空！",
								Toast.LENGTH_SHORT).show();
					} else if (str.length() > 10) {
						Toast.makeText(UserCenterActivity.this, "昵称不能超过10个字符",
								Toast.LENGTH_SHORT).show();
					} else {
						final String name = edt.getText().toString().trim();

						String uid = SongRisePlayerApp.sp.getString("userid",
								"0");
						url = getResources().getString(R.string.ip_address);
						String url1 = url + "User/update_name";
						AsyncHttpClient client = new AsyncHttpClient();
						// 新建传参对象
						RequestParams params = new RequestParams();
						// 传入参数
						params.put("userid", uid);// 从共享池获取
						params.put("username", name);
						if (SongRisePlayerApp.sp.getString("wifi_only", "no")
								.equals("no")
								|| (SongRisePlayerApp.sp.getString("wifi_only",
										"no").equals("yes") && (isWifi(UserCenterActivity.this)))) {// wifi联网
							client.post(url1, params,
									new AsyncHttpResponseHandler() {

										@Override
										public void onFailure(int arg0,
												Header[] arg1, byte[] arg2,
												Throwable arg3) {
											dialog.dismiss();
											Toast.makeText(
													UserCenterActivity.this,
													"昵称修改失败！",
													Toast.LENGTH_SHORT).show();
											// TODO Auto-generated method stub

										}

										@Override
										public void onSuccess(int arg0,
												Header[] arg1, byte[] arg2) {
											// TODO Auto-generated method stub
											JSONObject lan;
											try {
												lan = new JSONObject(
														new String(arg2));
												String succ = lan
														.getString("success");
												// dialog.dismiss();
												if (succ.equals("yes")) {
													text_name.setText(name);
													dialog.dismiss();
												} else {
													dialog.dismiss();
													Toast.makeText(
															UserCenterActivity.this,
															"昵称修改失败！",
															Toast.LENGTH_SHORT)
															.show();
												}
											} catch (JSONException e) {
												// TODO Auto-generated catch
												// block
												dialog.dismiss();
												Toast.makeText(
														UserCenterActivity.this,
														"昵称修改失败！",
														Toast.LENGTH_SHORT)
														.show();
												e.printStackTrace();
											}
										}
									});
						}
						// dialog.dismiss();// pay attention
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
		case R.id.liner_center_birth: {// 修改生日
			calendar2 = Calendar.getInstance();
			dialog2 = new DatePickerDialog(UserCenterActivity.this,
					new OnDateSetListener() {

						@Override
						public void onDateSet(DatePicker view, int year,
								int month, int day) {
							month = month + 1;
							text_birth.setText(year + "-" + month + "-" + day);

							String uid = SongRisePlayerApp.sp.getString(
									"userid", "0");
							url = getResources().getString(R.string.ip_address);
							String url1 = url + "User/update_birth";
							AsyncHttpClient client = new AsyncHttpClient();
							// 新建传参对象
							RequestParams params = new RequestParams();
							// 传入参数
							params.put("userid", uid);// 从共享池获取
							params.put("userbirth", year + "-" + month + "-"
									+ day);
							if (SongRisePlayerApp.sp.getString("wifi_only",
									"no").equals("no")
									|| (SongRisePlayerApp.sp.getString(
											"wifi_only", "no").equals("yes") && (isWifi(UserCenterActivity.this)))) {// wifi联网
								client.post(url1, params,
										new AsyncHttpResponseHandler() {

											@Override
											public void onFailure(int arg0,
													Header[] arg1, byte[] arg2,
													Throwable arg3) {
												// TODO Auto-generated method
												// stub

											}

											@Override
											public void onSuccess(int arg0,
													Header[] arg1, byte[] arg2) {
												// TODO Auto-generated method
												// stub
												JSONObject lan;
												try {
													lan = new JSONObject(
															new String(arg2));
													String succ = lan
															.getString("success");
													if (succ.equals("yes")) {
													}
												} catch (JSONException e) {
													// TODO Auto-generated catch
													// block
													e.printStackTrace();
												}
											}
										});
							} // 结束

						}
					}, calendar2.get(Calendar.YEAR), calendar2
							.get(Calendar.MONTH), calendar2
							.get(Calendar.DAY_OF_MONTH));
			dialog2.setTitle("设置日期");
			dialog2.setCanceledOnTouchOutside(true);
			dialog2.show();

			break;
		}
		case R.id.liner_center_sex: {// 修改性别
			LayoutInflater inflaterDl = LayoutInflater.from(this);
			LinearLayout layout = (LinearLayout) inflaterDl.inflate(
					R.layout.sex_dailog, null);

			// 对话框
			final Dialog dialog = new AlertDialog.Builder(
					UserCenterActivity.this).create();

			LinearLayout liner_man = (LinearLayout) layout
					.findViewById(R.id.Liner_sex_man);
			final ImageView image_man = (ImageView) layout
					.findViewById(R.id.imageView_sex_man);
			LinearLayout liner_woman = (LinearLayout) layout
					.findViewById(R.id.Liner_sex_woman);
			final ImageView image_woman = (ImageView) layout
					.findViewById(R.id.imageView_sex_woman);
			TextView qx = (TextView) layout.findViewById(R.id.text_sex_qx);
			String sex = text_sex.getText().toString().trim();
			if (sex == "男") {
				image_woman.setVisibility(View.INVISIBLE);
				image_man.setVisibility(View.VISIBLE);
			} else {
				image_man.setVisibility(View.INVISIBLE);
				image_woman.setVisibility(View.VISIBLE);
			}
			dialog.show();
			dialog.getWindow().setContentView(layout);

			// 点击男
			liner_man.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					image_woman.setVisibility(View.INVISIBLE);
					image_man.setVisibility(View.VISIBLE);

					String uid = SongRisePlayerApp.sp.getString("userid", "0");
					url = getResources().getString(R.string.ip_address);
					String url1 = url + "User/update_sex";
					AsyncHttpClient client = new AsyncHttpClient();
					// 新建传参对象
					RequestParams params = new RequestParams();
					// 传入参数
					params.put("userid", uid);// 从共享池获取
					params.put("usersex", "男");
					if (SongRisePlayerApp.sp.getString("wifi_only", "no")
							.equals("no")
							|| (SongRisePlayerApp.sp.getString("wifi_only",
									"no").equals("yes") && (isWifi(UserCenterActivity.this)))) {// wifi联网
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
											String succ = lan
													.getString("success");
											if (succ.equals("yes")) {
												text_sex.setText("男");
												dialog.dismiss();
											}
										} catch (JSONException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									}
								});
					}
				}
			});
			// 点击女
			liner_woman.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					image_man.setVisibility(View.INVISIBLE);
					image_woman.setVisibility(View.VISIBLE);

					String uid = SongRisePlayerApp.sp.getString("userid", "0");
					url = getResources().getString(R.string.ip_address);
					String url1 = url + "User/update_sex";
					AsyncHttpClient client = new AsyncHttpClient();
					// 新建传参对象
					RequestParams params = new RequestParams();
					// 传入参数
					params.put("userid", uid);// 从共享池获取
					params.put("usersex", "女");
					if (SongRisePlayerApp.sp.getString("wifi_only", "no")
							.equals("no")
							|| (SongRisePlayerApp.sp.getString("wifi_only",
									"no").equals("yes") && (isWifi(UserCenterActivity.this)))) {// wifi联网
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
											String succ = lan
													.getString("success");
											if (succ.equals("yes")) {
												text_sex.setText("女");
											}
											dialog.dismiss();
										} catch (JSONException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									}
								});
					}

				}
			});
			// 点击取消
			qx.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			break;
		}
		case R.id.liner_center_info: {// 修改个性签名
			this.startActivityForResult(new Intent(this,
					Update_Info_Activity.class), 1);
			break;
		}
		case R.id.button_center_exit: {// 退出当前用户
			Editor ed = SongRisePlayerApp.sp.edit();
			ed.putString("userid", "0");
			ed.putString("userphone", "0");
			ed.commit();
			startActivity(new Intent(UserCenterActivity.this,
					MainActivity.class));
			this.finish();
			break;
		}
		default:
			break;
		}

	}

	private void updateimage() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		startActivityForResult(intent, GALLERY_REQUEST_CODE);
	}

	private Uri saveBitmap(Bitmap bm) {
		File tmpDir = new File(Environment.getExternalStorageDirectory()
				+ "/com.Tsstar.zyq");
		if (!tmpDir.exists()) {
			tmpDir.mkdir();
		}
		File img = new File(tmpDir.getAbsolutePath() + "avater.png");
		try {
			FileOutputStream fos = new FileOutputStream(img);
			bm.compress(Bitmap.CompressFormat.PNG, 85, fos);
			fos.flush();
			fos.close();
			return Uri.fromFile(img);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private Uri convertUri(Uri uri) {
		InputStream is = null;
		try {
			is = getContentResolver().openInputStream(uri);
			Bitmap bitmap = BitmapFactory.decodeStream(is);
			is.close();
			return saveBitmap(bitmap);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private void startImageZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 300);
		intent.putExtra("outputY", 300);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, CROP_REQUEST_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 1) {
			// 可从data中取出修改个性签名界面返回的值
			String info = data.getStringExtra("infos");
			text_info.setText(info);
		}
		if (requestCode == CAMERA_REQUEST_CODE) {
			if (data == null) {
				return;
			} else {
				Bundle extras = data.getExtras();
				if (extras != null) {
					Bitmap bm = extras.getParcelable("data");
					Uri uri = saveBitmap(bm);
					startImageZoom(uri);
				}
			}
		} else if (requestCode == GALLERY_REQUEST_CODE) {
			if (data == null) {
				return;
			}
			Uri uri;
			uri = data.getData();
			Uri fileUri = convertUri(uri);
			startImageZoom(fileUri);
		} else if (requestCode == CROP_REQUEST_CODE) {
			if (data == null) {
				return;
			}
			Bundle extras = data.getExtras();
			if (extras == null) {
				return;
			}
			Bitmap bm = extras.getParcelable("data");
			// ImageView imageView = (ImageView)findViewById(R.id.);
			image_touxiang.setImageBitmap(bm);
			sendImage(bm);
		}

	}

	private void sendImage(Bitmap bm) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 60, stream);
		byte[] bytes = stream.toByteArray();
		String img = new String(Base64.encodeToString(bytes, Base64.DEFAULT));

		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		final String uid = SongRisePlayerApp.sp.getString("userid", "0");
		params.put("img", img);
		params.put("userid", uid);
		url = getResources().getString(R.string.ip_address);
		String url1 = url + "User/update_img1";
		// saveBitmapToSharedPreferences();
		if (SongRisePlayerApp.sp.getString("wifi_only", "no").equals("no")
				|| (SongRisePlayerApp.sp.getString("wifi_only", "no").equals(
						"yes") && (isWifi(UserCenterActivity.this)))) {// wifi联网
			client.post(url1, params, new AsyncHttpResponseHandler() {

				@Override
				public void onFailure(int arg0, Header[] arg1, byte[] arg2,
						Throwable arg3) {
					// TODO Auto-generated method stub
					Toast.makeText(UserCenterActivity.this, "Upload Fail!",
							Toast.LENGTH_LONG).show();
				}

				@Override
				public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
					// TODO Auto-generated method stub

					JSONObject lan;
					try {
						lan = new JSONObject(new String(arg2));
						String succ = lan.getString("success");
						if (succ.equals("yes")) {
							String url_img = getResources().getString(
									R.string.ip_img);
							String url_img1 = url_img + uid + ".png";
							// ImageLoad(url_img1);
							Toast.makeText(UserCenterActivity.this, "头像上传成功!",
									Toast.LENGTH_LONG).show();
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			});
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

	private void saveBitmapToSharedPreferences() {
		Bitmap bitmap = ((BitmapDrawable) image_touxiang.getDrawable())
				.getBitmap();
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.PNG, 80, byteArrayOutputStream);
		byte[] byteArray = byteArrayOutputStream.toByteArray();
		String imageString = new String(Base64.encodeToString(byteArray,
				Base64.DEFAULT));
		Editor editor = SongRisePlayerApp.sp.edit();
		// 向共享池存放用户的头像信息
		editor.putString("image", imageString);
		editor.putString("username", text_name.getText().toString());
		editor.putString("userinfo", text_info.getText().toString());
		editor.putString("usersex", text_sex.getText().toString());
		editor.putString("userbirth", text_birth.getText().toString());
		editor.commit();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		// getBitmapFromSharedPreferences();
	}

	private void getBitmapFromSharedPreferences() {
		// SharedPreferences sharedPreferences=getSharedPreferences("testSP",
		// Context.MODE_PRIVATE);
		String imageString = SongRisePlayerApp.sp.getString("image", "");
		String nameString = SongRisePlayerApp.sp.getString("username", "");
		String sexString = SongRisePlayerApp.sp.getString("usersex", "男");
		String birthString = SongRisePlayerApp.sp.getString("userbirth", "");
		String infoString = SongRisePlayerApp.sp.getString("userinfo",
				"主人很懒，什么都没写");
		// String imageString=sharedPreferences.getString("image", "");
		byte[] byteArray = Base64.decode(imageString, Base64.DEFAULT);
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
				byteArray);
		Bitmap bitmap = BitmapFactory.decodeStream(byteArrayInputStream);
		image_touxiang.setImageBitmap(bitmap);
		text_birth.setText(birthString);
		text_name.setText(nameString);
		text_sex.setText(sexString);
		text_info.setText(infoString);
	}

	protected void ImageLoad(String imgurl) {
		// TODO Auto-generated method stub
		RequestQueue mQueue = Volley.newRequestQueue(UserCenterActivity.this);
		ImageLoader img = new ImageLoader(mQueue, new ImageLoader.ImageCache() {
			@Override
			public void putBitmap(String url, Bitmap bitmap) {
			}

			@Override
			public Bitmap getBitmap(String url) {
				return null;
			}
		});
		ImageListener listener = ImageLoader.getImageListener(image_touxiang,
				R.drawable.img_avatar_default, R.drawable.img_avatar_default);
		img.get(imgurl, listener);
	}

}
