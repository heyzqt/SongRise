package com.example.dialog;

import com.example.songriseplayer.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

public class SelectDialog extends AlertDialog {

	public SelectDialog(Context context, int theme) {
		super(context, theme);
	}

	public SelectDialog(Context context) {
		super(context);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.song_include_music_menu_dialog);
	}
}
