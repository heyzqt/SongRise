package com.example.songriseplayer;

import com.example.vo.SearchResult;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;

/*
 * 下载弹出的对话框类
 */
public class DownloadDialogFragment extends DialogFragment {

	private SearchResult searchResult;
	private MainActivity mainActivity;

	public static DownloadDialogFragment newInstance(SearchResult searchResult) {
		DownloadDialogFragment downloadDialogFragment = new DownloadDialogFragment();
		downloadDialogFragment.searchResult = searchResult;
		return downloadDialogFragment;
	}

	private String[] items;

	// @Override
	// public void onAttach(Context context) {
	// // TODO Auto-generated method stub
	// super.onAttach(context);
	// mainActivity = (MainActivity) getActivity();
	// items = new String[] { "下载", "取消" };
	// }
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		items = new String[] { "下载", "取消" };
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return super.onCreateDialog(savedInstanceState);
	}
}
