package com.example.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.example.adapter.SongListManageAdapter.ViewHolder;
import com.example.app.Constant;
import com.example.songriseplayer.R;
import com.example.vo.Mp3Info;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 添加本地歌曲到歌单的适配器
 * 
 * @author zq
 * 
 */
public class MusicManageListAdapter extends BaseAdapter {

	private ArrayList<Mp3Info> mp3Infos;
	private ArrayList<Map<String, String>> check_list;
	private Context context;

	public MusicManageListAdapter(Context context, ArrayList<Mp3Info> mp3Infos,
			ArrayList<Map<String, String>> check_list) {
		this.context = context;
		this.mp3Infos = mp3Infos;
		this.check_list = check_list;
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
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_add_local_music_list, null);
			vh = new ViewHolder();
			vh.textview1_title = (TextView) convertView
					.findViewById(R.id.text_add_local_title);
			vh.textview2_singer = (TextView) convertView
					.findViewById(R.id.text_add_local_singer);
			vh.imageview_circle = (ImageView) convertView
					.findViewById(R.id.img_add_local_circle);
			vh.textview3_is_Checked = (TextView) convertView
					.findViewById(R.id.text_add_local_is_checked);
			convertView.setTag(vh);
		}
		vh = (ViewHolder) convertView.getTag();
		vh.textview1_title.setText(mp3Infos.get(position).getTitle());
		vh.textview2_singer.setText(mp3Infos.get(position).getArtist());
		vh.textview3_is_Checked.setText(check_list.get(position).get(
				Constant.SONGLIST_CHECKED));
		if (vh.textview3_is_Checked.getText().toString().equals("false")) {
			vh.imageview_circle.setImageResource(R.drawable.img_circle);
		} else {
			vh.imageview_circle.setImageResource(R.drawable.img_circle_checked);
		}
		return convertView;
	}

	static class ViewHolder {

		TextView textview1_title;
		TextView textview2_singer;
		TextView textview3_is_Checked;
		ImageView imageview_circle;
	}

}
