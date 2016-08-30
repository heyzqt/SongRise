package com.example.songriseplayer;

import com.example.utils.AppUtils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * 关于我们界面
 * 
 * @author 张艳琴 功能：给客服打电话，检查更新（仅仅实现了查询，断点续传，下载还没完成）
 * 
 */
public class AboutUsActivity extends Activity implements OnClickListener {
	ImageView image_back;
	LinearLayout liner_check;
	LinearLayout liner_phone;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about_us);
		image_back = (ImageView) findViewById(R.id.imageview_about_back);
		liner_check = (LinearLayout) findViewById(R.id.liner_about_check);
		liner_phone = (LinearLayout) findViewById(R.id.Liner_about_phone);

		liner_check.setOnClickListener(this);
		liner_phone.setOnClickListener(this);
		image_back.setOnClickListener(this);
		
		//设置标题栏颜色
		AppUtils.setSystemStatusBar(AboutUsActivity.this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.imageview_about_back: {
			this.finish();
			break;
		}
		case R.id.Liner_about_phone: {// 给客服打电话
			String phoneNumber = "18408243864";
			Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
					+ phoneNumber));
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			break;
		}
		case R.id.liner_about_check: {// 检查更新
			// 还需写连接网络检查部分。
			Toast.makeText(AboutUsActivity.this, "已经是最新版本", Toast.LENGTH_SHORT)
					.show();
			break;
		}

		default:
			break;
		}

	}

}
