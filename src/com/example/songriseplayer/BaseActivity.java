package com.example.songriseplayer;

import java.security.PublicKey;

import com.example.app.SongRisePlayerApp;
import com.example.songriseplayer.PlayService.PlayBinder;
import com.example.utils.L;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

@SuppressLint("NewApi")
public abstract class BaseActivity extends FragmentActivity {

	protected PlayService playService;
	protected SongRisePlayerApp app;

	private boolean isBound = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		app = (SongRisePlayerApp) getApplication();
	}

	private ServiceConnection conn = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			playService = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			PlayService.PlayBinder playBinder = (PlayBinder) service;
			playService = playBinder.getPlayService();

			playService.setMusicUpdateListener(musicUpdateListener);
			musicUpdateListener.onChange(playService.getCurrentPosition());
		}
	};

	// 绑定服务
	public void bindPlayService() {

		if (!isBound) {
			Intent intent = new Intent(this, PlayService.class);
			bindService(intent, conn, Context.BIND_AUTO_CREATE);
			isBound = true;
			//L.e("bindPlayService()绑定服务");
		}

	}

	// 解绑服务
	public void unbindPlayService() {
		if (isBound) {
			unbindService(conn);
			isBound = false;
			// Log.e(MainActivity.TAG, "unbindPlayService()解绑服务");
		}
	}

	private PlayService.MusicUpdateListener musicUpdateListener = new PlayService.MusicUpdateListener() {

		@Override
		public void onPublish(int progress) {
			// TODO Auto-generated method stub
			publish(progress);
		}

		@Override
		public void onChange(int position) {
			// TODO Auto-generated method stub
			change(position);
		}
	};

	public abstract void publish(int progress);

	public abstract void change(int position);

}
