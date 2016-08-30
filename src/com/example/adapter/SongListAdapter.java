package com.example.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.example.app.Constant;
import com.example.songriseplayer.R;
import com.example.vo.Mp3Info;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * songlist的歌单适配器
 * 
 * @author zq
 * 
 */
public class SongListAdapter extends BaseAdapter {

	private List<List<Map<String, String>>> songlist;
	private Context context;

	public SongListAdapter(Context ctx, List<List<Map<String, String>>> songlist) {
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
					R.layout.item_song_list, null);
			vh = new ViewHolder();
			vh.textview = (TextView) convertView.findViewById(R.id.textview);
			convertView.setTag(vh);
		}
		vh = (ViewHolder) convertView.getTag();
		vh.textview.setText(songlist.get(position).get(0)
				.get(Constant.SONGMAP_KEY));
		return convertView;
	}

	static class ViewHolder {

		TextView textview;
	}
}
