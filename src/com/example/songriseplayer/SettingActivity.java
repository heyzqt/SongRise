package com.example.songriseplayer;

import java.io.File;

import com.example.app.SongRisePlayerApp;
import com.example.utils.AppUtils;
import com.example.utils.DataCleanManager;
import com.example.utils.L;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * 系统设置类
 * 
 * @author 张艳琴 功能：关于我们入口，反馈入口，清理缓存 未实现功能：边听边存、选择歌曲保存位置
 */
public class SettingActivity extends Activity implements OnClickListener {

	ImageView back;
	// LinearLayout liner_save;//保存位置
	LinearLayout liner_delete;// 清理缓存
	// LinearLayout liner_listing;//边听边存
	LinearLayout liner_about;// 关于我们
	LinearLayout liner_message;// 意见反馈

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_activity);
		back = (ImageView) findViewById(R.id.imageview_Setting_back);
		// liner_save=(LinearLayout) findViewById(R.id.liner_sdcard_save);
		liner_delete = (LinearLayout) findViewById(R.id.liner_settting_delete);
		// liner_listing=(LinearLayout)
		// findViewById(R.id.liner_setting_listing);
		liner_about = (LinearLayout) findViewById(R.id.liner_setting_about);
		liner_message = (LinearLayout) findViewById(R.id.liner_setting_message);
		back.setOnClickListener(this);
		// liner_save.setOnClickListener(this);
		liner_delete.setOnClickListener(this);
		// liner_listing.setOnClickListener(this);
		liner_about.setOnClickListener(this);
		liner_message.setOnClickListener(this);

		// 设置标题栏颜色
		AppUtils.setSystemStatusBar(SettingActivity.this);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.imageview_Setting_back: {
			this.finish();
			break;
		}
		// case R.id.liner_sdcard_save:{//歌曲存储位置
		// break;
		// }
		case R.id.liner_setting_about: {// 关于我们
			startActivity(new Intent(this, AboutUsActivity.class));
			break;
		}
		// case R.id.liner_setting_listing:{//边听边存
		// break;
		// }
		case R.id.liner_setting_message: {// 意见反馈
			String uid = SongRisePlayerApp.sp.getString("userid", "0");
			if (uid.equals("0")) {
				Toast.makeText(this, "请先登陆", Toast.LENGTH_SHORT).show();
			} else {
				startActivity(new Intent(this, FeedBackActivity.class));
			}
			break;
		}
		case R.id.liner_settting_delete: {// 清除缓存
			// deleteFilesByDirectory(context.getCacheDir());

			LayoutInflater inflaterDl = LayoutInflater.from(this);
			LinearLayout layout = (LinearLayout) inflaterDl.inflate(
					R.layout.check_dialog, null);

			// 对话框
			final Dialog dialog = new AlertDialog.Builder(SettingActivity.this)
					.create();
			dialog.show();
			dialog.getWindow().setContentView(layout);

			// 取消按钮
			Button btnCancel = (Button) layout.findViewById(R.id.Button_qx);
			btnCancel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// Toast.makeText(SettingActivity.this, "cancel",
					// Toast.LENGTH_SHORT).show();
					dialog.dismiss();
				}
			});

			// 确定按钮
			Button btnOK = (Button) layout.findViewById(R.id.Button_ok);
			btnOK.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// deleteFilesByDirectory(context.getCacheDir());
					DataCleanManager.cleanInternalCache(SettingActivity.this);
					Editor editor = SongRisePlayerApp.sp.edit();
					editor.clear();
					editor.commit();
					Toast.makeText(SettingActivity.this, "缓存已清除",
							Toast.LENGTH_LONG).show();
					dialog.dismiss();
				}
			});
			break;
		}
		default:
			break;
		}

	}

}
