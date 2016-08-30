package com.example.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.app.SongRisePlayerApp;
import com.example.songriseplayer.R;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

/*
 * 程序操作类
 * @author:zq
 */
public class AppUtils {

	
	// 隐藏输入法
	public static void hideInputMethod(View view) {
		InputMethodManager imm = (InputMethodManager) SongRisePlayerApp.context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm.isActive()) {
			imm.hideSoftInputFromWindow(view.getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		}

	}
	
	/**
	 * 设置系统状态栏颜色
	 * @param a
	 */
	public static void setSystemStatusBar(Activity a){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			setTranslucentStatus(true,a.getWindow());
		}

		SystemBarTintManager tintManager = new SystemBarTintManager(a);
		tintManager.setStatusBarTintEnabled(true);
		tintManager.setStatusBarTintResource(R.color.statusbar_bg);
	}
	
	/**
	 * 设置系统状态栏的参数
	 * @param on
	 * @param win
	 */
    @TargetApi(19) 
	public static void setTranslucentStatus(boolean on,Window win) {
		//Window win = getWindow();
		WindowManager.LayoutParams winParams = win.getAttributes();
		final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
		if (on) {
			winParams.flags |= bits;
		} else {
			winParams.flags &= ~bits;
		}
		win.setAttributes(winParams);
	}

	/**
	 * 联网方式判断函数
	 * @param mContext
	 * @return 0 连接wifi 1连接数据流量 2无网络连接
	 * @author zyq
	 */
	private static int isWifi(Context mContext) {
		ConnectivityManager connectivityManager = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null
				&& activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			return 0;	//连接wifi
		}
		if(activeNetInfo==null){
			return 2;	//无网络连接
		}
		return 1;		//连接数据流量
	}
	
	
	/**
	 * 将字符串转换为字符串数组
	 * 
	 * @param values
	 *            字符串数组
	 * @param key
	 *            map的key值
	 * @return List<List<Map<String, String>>>
	 */
	public static List<List<Map<String, String>>> getSharedPreference(
			String values, String key) {
		String regularEx = "#";
		String[] str = null;
		List<List<Map<String, String>>> list = new ArrayList<List<Map<String, String>>>();
		// 转换成了字符串数组
		if (values != null && values.length() > 0) {
			str = values.split(regularEx);
			for (int i = 0; i < str.length; i++) {
				List<Map<String, String>> list_child = new ArrayList<Map<String, String>>();
				Map<String, String> list_map = new HashMap<String, String>();
				list_map.put(key, str[i]);
				list_child.add(list_map);
				list.add(list_child);
			}
			return list;
		}

		return null;
	}

	/**
	 * 将List转换为字符串，每个词组间以#分隔
	 * 
	 * @param values
	 *            List<List<Map<String, String>>>
	 * @param key
	 *            map的key
	 * @return
	 */
	public static String setSharedPreference(
			List<List<Map<String, String>>> values, String key) {
		String regularEx = "#";
		String str = "";
		if (values != null && values.size() > 0) {
			for (int i = 0; i < values.size(); i++) {
				str = str + values.get(i).get(0).get(key) + regularEx;
			}
			return str;
		}
		return null;
	}

}
