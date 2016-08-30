package com.example.adapter;

import java.util.ArrayList;

import com.example.songriseplayer.R;
import com.example.utils.L;
import com.example.utils.MediaUtils;
import com.example.vo.Mp3Info;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 音乐列表适配器类
 * 
 * @author zq
 * 
 */
public class MyMusicListAdapter extends BaseAdapter {

	private Context ctx;
	private ArrayList<Mp3Info> mp3Infos;

	public MyMusicListAdapter(Context ctx, ArrayList<Mp3Info> mp3Infos) {
		this.ctx = ctx;
		this.mp3Infos = mp3Infos;
	}

	public void setMp3Infos(ArrayList<Mp3Info> mp3Infos) {
		this.mp3Infos = mp3Infos;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mp3Infos.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mp3Infos.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder vh;
		if (convertView == null) {
			convertView = LayoutInflater.from(ctx).inflate(
					R.layout.item_music_list, null);
			vh = new ViewHolder();
			vh.textview1_title = (TextView) convertView
					.findViewById(R.id.textview1_title);
			vh.textview2_singer = (TextView) convertView
					.findViewById(R.id.textview2_singer);
			vh.textview3_time = (TextView) convertView
					.findViewById(R.id.textview3_time);
			vh.textview4_song_num = (TextView) convertView
					.findViewById(R.id.textview_song_num);
			if(ctx.getClass().getName()
					.equals("com.example.songriseplayer.SongIncludeMusicActivity"))
			{
				vh.textview1_title.setTextColor(Color.BLACK);
				vh.textview2_singer .setTextColor(Color.BLACK);
				vh.textview3_time .setTextColor(Color.BLACK);
				vh.textview4_song_num .setTextColor(Color.BLACK);
			}
			convertView.setTag(vh);
			
		}
		vh = (ViewHolder) convertView.getTag();
		Mp3Info mp3Info = mp3Infos.get(position);
		vh.textview1_title.setText(mp3Info.getTitle());
		vh.textview2_singer.setText(mp3Info.getArtist());
		vh.textview3_time.setText(MediaUtils.formatTime(mp3Info.getDuration()));
		position = position + 1;
		vh.textview4_song_num.setText(position + "");
//		if(ctx.getClass().getName()
//				.equals("com.example.songriseplayer.SongIncludeMusicActivity"))
//		{
//			vh.textview1_title.setTextColor(Color.BLACK);
//			vh.textview2_singer .setTextColor(Color.BLACK);
//			vh.textview3_time .setTextColor(Color.BLACK);
//			vh.textview4_song_num .setTextColor(Color.BLACK);
//		}
		
		return convertView;
	}

	static class ViewHolder {
		TextView textview1_title;
		TextView textview2_singer;
		TextView textview3_time;
		TextView textview4_song_num;
	}

}
