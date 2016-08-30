package com.example.adapter;

import java.util.ArrayList;
import java.util.zip.Inflater;

import com.example.adapter.MyMusicListAdapter.ViewHolder;
import com.example.songriseplayer.R;
import com.example.utils.MediaUtils;
import com.example.vo.Mp3Info;
import com.example.vo.SearchResult;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 网络音乐列表适配器
 * 
 * @author zq
 * 
 */
public class NetMusicAdapter extends BaseAdapter {

	private Context ctx;
	private ArrayList<SearchResult> searchResults;

	public NetMusicAdapter(Context ctx, ArrayList<SearchResult> searchResults) {
		this.ctx = ctx;
		this.searchResults = searchResults;
	}

	public ArrayList<SearchResult> getSearchResults() {
		return searchResults;
	}

	public void setSearchResults(ArrayList<SearchResult> searchResults) {
		this.searchResults = searchResults;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return searchResults.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return searchResults.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder vh;
		if (convertView == null) {
			convertView = LayoutInflater.from(ctx).inflate(
					R.layout.net_item_music_list, null);
			vh = new ViewHolder();
			vh.textview1_title = (TextView) convertView
					.findViewById(R.id.textview1_title);
			vh.textview2_singer = (TextView) convertView
					.findViewById(R.id.textview2_singer);
			convertView.setTag(vh);
		}
		vh = (ViewHolder) convertView.getTag();
		SearchResult searchResult = searchResults.get(position);
		vh.textview1_title.setText(searchResult.getMusicName());
		vh.textview2_singer.setText(searchResult.getArtist());
		return convertView;
	}

	static class ViewHolder {
		TextView textview1_title;
		TextView textview2_singer;
		TextView textview3_time;
	}

}
