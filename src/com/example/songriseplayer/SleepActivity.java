package com.example.songriseplayer;

import java.util.Calendar;

import com.example.app.SongRisePlayerApp;
import com.example.utils.AppUtils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

/**
 * 控制应用定时关闭类《新增加的》
 * 
 * @author 张艳琴 功能：用户选择或者自定义软件的关闭时间，并在侧边栏倒计时关闭时间
 */
public class SleepActivity extends Activity implements OnClickListener {
	LinearLayout shutdown;
	LinearLayout sleep_10;
	LinearLayout sleep_20;
	LinearLayout sleep_30;
	LinearLayout sleep_60;
	LinearLayout sleep_diy;

	ImageView back;
	ImageView image_shutdown;
	ImageView image_sleep_10;
	ImageView image_sleep_20;
	ImageView image_sleep_30;
	ImageView image_sleep_60;
	ImageView image_sleep_diy;

	private TimePickerDialog dialog2;
	private Calendar calendar2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sleep_activity);

		// 设置标题栏颜色
		AppUtils.setSystemStatusBar(SleepActivity.this);

		Editor editor = SongRisePlayerApp.sp.edit();
		editor.putString("sleep", "-1");
		editor.commit();

		shutdown = (LinearLayout) findViewById(R.id.liner_time_shudown);
		sleep_10 = (LinearLayout) findViewById(R.id.liner_time_10min);
		sleep_20 = (LinearLayout) findViewById(R.id.liner_time_20min);
		sleep_30 = (LinearLayout) findViewById(R.id.liner_time_30min);
		sleep_60 = (LinearLayout) findViewById(R.id.liner_time_60min);
		sleep_diy = (LinearLayout) findViewById(R.id.liner_time_diy);

		image_shutdown = (ImageView) findViewById(R.id.image_time_shudown);
		image_sleep_10 = (ImageView) findViewById(R.id.image_time_10);
		image_sleep_20 = (ImageView) findViewById(R.id.image_time_20min);
		image_sleep_30 = (ImageView) findViewById(R.id.image_time_30min);
		image_sleep_60 = (ImageView) findViewById(R.id.image_time_60min);
		image_sleep_diy = (ImageView) findViewById(R.id.image_time_diy);

		back = (ImageView) findViewById(R.id.imageview_back);

		shutdown.setOnClickListener(this);
		sleep_10.setOnClickListener(this);
		sleep_20.setOnClickListener(this);
		sleep_30.setOnClickListener(this);
		sleep_60.setOnClickListener(this);
		sleep_diy.setOnClickListener(this);
		back.setOnClickListener(this);
		
		// 设置标题栏颜色
		AppUtils.setSystemStatusBar(SleepActivity.this);

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.liner_time_shudown: {
			image_shutdown.setImageResource(R.drawable.iconfont_xiaogougou);
			image_sleep_10.setImageResource(R.drawable.iconfont_yuanquan);
			image_sleep_20.setImageResource(R.drawable.iconfont_yuanquan);
			image_sleep_30.setImageResource(R.drawable.iconfont_yuanquan);
			image_sleep_60.setImageResource(R.drawable.iconfont_yuanquan);
			image_sleep_diy.setImageResource(R.drawable.iconfont_yuanquan);
			addTime(-1);
			break;
		}
		case R.id.liner_time_10min: {
			image_shutdown.setImageResource(R.drawable.iconfont_yuanquan);
			image_sleep_20.setImageResource(R.drawable.iconfont_yuanquan);
			image_sleep_30.setImageResource(R.drawable.iconfont_yuanquan);
			image_sleep_60.setImageResource(R.drawable.iconfont_yuanquan);
			image_sleep_diy.setImageResource(R.drawable.iconfont_yuanquan);
			image_sleep_10.setImageResource(R.drawable.iconfont_xiaogougou);
			Toast.makeText(SleepActivity.this, "将在10分后暂停播放音乐并退出",
					Toast.LENGTH_SHORT).show();
			exitApp(10);
			break;
		}
		case R.id.liner_time_20min: {
			image_shutdown.setImageResource(R.drawable.iconfont_yuanquan);
			image_sleep_10.setImageResource(R.drawable.iconfont_yuanquan);
			image_sleep_30.setImageResource(R.drawable.iconfont_yuanquan);
			image_sleep_60.setImageResource(R.drawable.iconfont_yuanquan);
			image_sleep_diy.setImageResource(R.drawable.iconfont_yuanquan);
			image_sleep_20.setImageResource(R.drawable.iconfont_xiaogougou);
			Toast.makeText(SleepActivity.this, "将在20分后暂停播放音乐并退出",
					Toast.LENGTH_SHORT).show();
			exitApp(20);
			break;
		}
		case R.id.liner_time_30min: {
			image_shutdown.setImageResource(R.drawable.iconfont_yuanquan);
			image_sleep_20.setImageResource(R.drawable.iconfont_yuanquan);
			image_sleep_10.setImageResource(R.drawable.iconfont_yuanquan);
			image_sleep_60.setImageResource(R.drawable.iconfont_yuanquan);
			image_sleep_diy.setImageResource(R.drawable.iconfont_yuanquan);
			image_sleep_30.setImageResource(R.drawable.iconfont_xiaogougou);
			Toast.makeText(SleepActivity.this, "将在30分后暂停播放音乐并退出",
					Toast.LENGTH_SHORT).show();
			exitApp(30);
			break;
		}
		case R.id.liner_time_60min: {
			image_shutdown.setImageResource(R.drawable.iconfont_yuanquan);
			image_sleep_20.setImageResource(R.drawable.iconfont_yuanquan);
			image_sleep_30.setImageResource(R.drawable.iconfont_yuanquan);
			image_sleep_10.setImageResource(R.drawable.iconfont_yuanquan);
			image_sleep_diy.setImageResource(R.drawable.iconfont_yuanquan);
			image_sleep_60.setImageResource(R.drawable.iconfont_xiaogougou);
			Toast.makeText(SleepActivity.this, "将在60分后暂停播放音乐并退出",
					Toast.LENGTH_SHORT).show();
			exitApp(60);
			break;
		}
		case R.id.liner_time_diy: {
			image_shutdown.setImageResource(R.drawable.iconfont_yuanquan);
			image_sleep_20.setImageResource(R.drawable.iconfont_yuanquan);
			image_sleep_30.setImageResource(R.drawable.iconfont_yuanquan);
			image_sleep_60.setImageResource(R.drawable.iconfont_yuanquan);
			image_sleep_10.setImageResource(R.drawable.iconfont_yuanquan);
			image_sleep_diy.setImageResource(R.drawable.iconfont_xiaogougou);

			calendar2 = Calendar.getInstance();
			dialog2 = new TimePickerDialog(SleepActivity.this,
					new OnTimeSetListener() {

						@Override
						public void onTimeSet(TimePicker arg0, int hour, int min) {
							Toast.makeText(SleepActivity.this,
									"将在" + hour * 60 + min + "分后停止播放音乐",
									Toast.LENGTH_SHORT).show();
							exitApp(hour * 60 + min);
							SleepActivity.this.finish();
						}
					}, calendar2.get(0) - 1, calendar2.get(0) - 1, true);
			dialog2.setTitle("设置时分");
			dialog2.setCanceledOnTouchOutside(true);
			dialog2.show();
			break;
		}
		case R.id.imageview_back: {
			this.finish();
			break;
		}
		}

	}

	public void exitApp(final int min) {
		addTime(min);
		new Thread() {
			@Override
			public void run() {

				synchronized (this) {
					try {
						wait(1000 * min * 60); // 60*min秒
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				// finish();
				int currentVersion = android.os.Build.VERSION.SDK_INT;
				if (currentVersion > android.os.Build.VERSION_CODES.ECLAIR_MR1) {
					Intent startMain = new Intent(Intent.ACTION_MAIN);
					startMain.addCategory(Intent.CATEGORY_HOME);
					startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(startMain);
					System.exit(0);
				} else {// android2.1
					ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
					am.restartPackage(getPackageName());
				}
			}
		}.start();

	}

	public void addTime(int SleepTime) {
		Editor editor = SongRisePlayerApp.sp.edit();
		int sleepTime = SleepTime;
		editor.putString("sleep", SleepTime * 60 + "");
		editor.commit();

		// 从SharedPreferences获取数据:
		// String name1=SongRisePlayerApp.sp.getString("name", "defaultname");
		// String age1=SongRisePlayerApp.sp.getString("age", "0");
		// Toast.makeText(SleepActivity.this, "年龄为："+age1+"\n姓名为："+name1,
		// Toast.LENGTH_LONG).show();
	}
}
