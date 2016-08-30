package com.example.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.example.songriseplayer.R;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 歌单管理适配器
 * 
 * @author zq
 * 
 */
public class SongListManageAdapter extends BaseAdapter {

	// private ArrayList<Map<String, String>> songlist;
	private List<List<Map<String, String>>> songlist;
	private Context context;

	public SongListManageAdapter(Context ctx,
			List<List<Map<String, String>>> songlist) {
		this.context = ctx;
		this.songlist = songlist;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return songlist.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return songlist.get(position);
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
					R.layout.item_song_list_manage, null);
			vh = new ViewHolder();
			vh.textview = (TextView) convertView
					.findViewById(R.id.textview_song_list_name);
			// vh.imageview=(ImageView)
			// convertView.findViewById(R.id.imageview_songlist);
			vh.imageview_circle = (ImageView) convertView
					.findViewById(R.id.imageview_circle);
			vh.is_Checked = (TextView) convertView
					.findViewById(R.id.textview_is_checked);
			convertView.setTag(vh);
		}
		vh = (ViewHolder) convertView.getTag();
		vh.textview.setText(songlist.get(position).get(0).get("songlist_name"));
		vh.is_Checked.setText(songlist.get(position).get(1)
				.get("songlist_checked"));
		if (vh.is_Checked.getText().toString().equals("false")) {
			vh.imageview_circle.setImageResource(R.drawable.img_circle);
		} else {
			vh.imageview_circle.setImageResource(R.drawable.img_circle_checked);
		}
		return convertView;
	}

	static class ViewHolder {

		TextView textview;
		ImageView imageview_circle;
		TextView is_Checked;
	}

}
