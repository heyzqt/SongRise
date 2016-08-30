package com.example.utils;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import com.example.songriseplayer.R;
import com.example.vo.Mp3Info;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;

/*
 * 音乐文件操作类
 * @author:zq
 */
public class MediaUtils {
	
	
	// 获取专辑封面的Uri
	private static Uri albumArtUri = Uri
			.parse("content://media/external/audio/albumart");

	
	/*
	 * 从数据库中查询歌曲信息，保存在list中
	 * 新方法
	 * 
	 * @return
	 */
	public static ArrayList<Mp3Info> getMp3Infos(Context context) {

		String title, artist, album, url;
		long id, albumId, size, duration;
		int isMusic;

//		 Cursor cursor = context.getContentResolver().query(
//		 MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,
//		 MediaStore.Audio.Media.DURATION + ">180000", null,
//		 MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
//
		Cursor cursor =context.getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,
				null, null,
				MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

//		Cursor cursor =context.getContentResolver().query(
//				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,
//				null, null,
//				null);


		ArrayList<Mp3Info> mp3Infos = new ArrayList<Mp3Info>();

		// for(int i=0;i<cursor.getColumnCount();i++){
		// L.e("数据库第"+i+"列的名称 : "+cursor.getColumnName(i));
		// }

//		   cursor.moveToFirst();
		
		for (int i = 0; i < cursor.getCount(); i++) {
			cursor.moveToNext();
			Mp3Info mp3Info = new Mp3Info();
			
			id = cursor.getLong(cursor
					.getColumnIndex(MediaStore.Audio.Media._ID)); // 音乐ID
	
				title = cursor.getString(cursor
						.getColumnIndex(MediaStore.Audio.Media.TITLE));// 歌名
          
							artist = cursor.getString(cursor
						.getColumnIndex(MediaStore.Audio.Media.ARTIST));// 歌手		
				album = cursor.getString(cursor
						.getColumnIndex(MediaStore.Audio.Media.ALBUM));// 专辑名
			albumId = cursor.getLong(cursor
					.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
	
			size = cursor.getLong(cursor
					.getColumnIndex(MediaStore.Audio.Media.SIZE)); // 文件大小
			
			duration = cursor.getLong(cursor
					.getColumnIndex(MediaStore.Audio.Media.DURATION));// 音乐时长
			url = cursor.getString(cursor
					.getColumnIndex(MediaStore.Audio.Media.DATA)); // 文件路径
		
			isMusic = cursor.getInt(cursor
					.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));// 是否为音乐
			
			// int isLike=cursor.getInt(cursor.getColumnIndex("isLike"));
			// //是否喜欢此音乐

			if (!artist.equals("<unknown>")&&size>180000) { // 只把音乐添加到合集当中

				mp3Info.setId(id);
				mp3Info.setTitle(title);		
				mp3Info.setArtist(artist);
				mp3Info.setAlbum(album);
				mp3Info.setDuration(duration);
				mp3Info.setAlbumId(albumId);
				mp3Info.setIsMusic(isMusic);
				mp3Info.setUrl(url);
				// mp3Info.setIsLike(isLike);
				mp3Infos.add(mp3Info);
			}
		}
		cursor.close();
		return mp3Infos;
	}

	
/*
 * 		原方法	
 */
//	/**
//	 * 获取本地歌曲Mp3Infos对象
//	 * 从数据库中查询歌曲信息，保存在list中
//	 * @param context
//	 * @return
//	 */
//	public static ArrayList<Mp3Info> getMp3Infos(Context context) {
//		Cursor cursor = context.getContentResolver().query(
//				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,
//				MediaStore.Audio.Media.DURATION + ">180000", null,
//				MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
//
//		ArrayList<Mp3Info> mp3Infos = new ArrayList<Mp3Info>();
//		
//		for (int i = 0; i < cursor.getCount(); i++) {
//			cursor.moveToNext();
//			Mp3Info mp3Info = new Mp3Info();
//			long id = cursor.getLong(cursor
//					.getColumnIndex(MediaStore.Audio.Media._ID)); // 音乐ID
//			String title = cursor.getString(cursor
//					.getColumnIndex(MediaStore.Audio.Media.TITLE));// 歌名
//			String artist = cursor.getString(cursor
//					.getColumnIndex(MediaStore.Audio.Media.ARTIST));// 歌手
//			String album = cursor.getString(cursor
//					.getColumnIndex(MediaStore.Audio.Media.ALBUM));// 专辑名
//			long albumId = cursor.getLong(cursor
//					.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
//			long size = cursor.getLong(cursor
//					.getColumnIndex(MediaStore.Audio.Media.SIZE)); // 文件大小
//			long duration = cursor.getLong(cursor
//					.getColumnIndex(MediaStore.Audio.Media.DURATION));// 音乐时长
//			String url = cursor.getString(cursor
//					.getColumnIndex(MediaStore.Audio.Media.DATA)); // 文件路径
//			int isMusic = cursor.getInt(cursor
//					.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));// 是否为音乐
//			// int isLike=cursor.getInt(cursor.getColumnIndex("isLike"));
//			// //是否喜欢此音乐
//
//			if (isMusic != 0) { // 只把音乐添加到合集当中
//				mp3Info.setId(id);
//				mp3Info.setTitle(title);
//				mp3Info.setArtist(artist);
//				mp3Info.setAlbum(album);
//				mp3Info.setDuration(duration);
//				mp3Info.setAlbumId(albumId);
//				mp3Info.setIsMusic(isMusic);
//				mp3Info.setUrl(url);
//				// mp3Info.setIsLike(isLike);
//				mp3Infos.add(mp3Info);
//			}
//		}
//		cursor.close();
//		return mp3Infos;
//	}

	/**
	 * 
	 * 格式化时间：将毫秒转换为分:秒格式
	 * @param time
	 * @return
	 */

	public static String formatTime(long time) {
		String min = time / (1000 * 60) + "";
		String sec = time % (1000 * 60) + "";
		if (min.length() < 2) {
			min = "0" + time / (1000 * 60) + "";
		} else {
			min = time / (1000 * 60) + "";
		}
		if (sec.length() == 4) {
			sec = "0" + (time % (1000 * 60) / 1000) + "";
		} else if (sec.length() == 3) {
			sec = "00" + (time % (1000 * 60) / 1000) + "";
		} else if (sec.length() == 2) {
			sec = "000" + (time % (1000 * 60) / 1000) + "";
		} else if (sec.length() == 1) {
			sec = "0000" + (time % (1000 * 60) / 1000) + "";
		}
		return min + ":" + sec.trim().substring(0, 2);
	}

	/*
	 * 获取专辑默认图片
	 * 
	 * @param context
	 * 
	 * @return
	 */
	public static Bitmap getDefaultArtwork(Context context, boolean small) {
		Options opts = new Options();
		opts.inPreferredConfig = Bitmap.Config.RGB_565;
		if (small) { // 返回小图片
			return BitmapFactory.decodeStream(context.getResources()
					.openRawResource(R.drawable.music_icon), null, opts);
		}
		return BitmapFactory.decodeStream(context.getResources()
				.openRawResource(R.drawable.music_icon), null, opts);
	}

	/*
	 * 从文件当中获取专辑封面位图
	 * 
	 * @param context
	 * 
	 * @param songid
	 * 
	 * @param albumid
	 * 
	 * @return
	 */
	private static Bitmap getArtworkFromFile(Context context, long songid,
			long albumid) {
		Bitmap bm = null;
		if (albumid < 0 && songid < 0) {
			throw new IllegalArgumentException(
					"Must specify an album or a song id");
		}
		try {
			Options options = new Options();
			FileDescriptor fd = null;
			if (albumid < 0) {
				Uri uri = Uri.parse("content://media/external/audio/media"
						+ songid + "/albumart");
				ParcelFileDescriptor pfd = context.getContentResolver()
						.openFileDescriptor(uri, "r");
				if (pfd != null) {
					fd = pfd.getFileDescriptor();
				}
			} else {
				Uri uri = ContentUris.withAppendedId(albumArtUri, albumid);
				ParcelFileDescriptor pfd = context.getContentResolver()
						.openFileDescriptor(uri, "r");
				if (pfd != null) {
					fd = pfd.getFileDescriptor();
				}
			}
			options.inSampleSize = 1;
			// 只进行大小判断
			options.inJustDecodeBounds = true;
			// 调用此方法得到options图片大小
			BitmapFactory.decodeFileDescriptor(fd, null, options);
			// 目标是在800pixel的画面上显示
			// 所以需要调用computeSampleSize得到图片缩放的比例
			options.inSampleSize = 100;
			// 得到缩放比例后，开始读入Bitmap数据
			options.inJustDecodeBounds = false;
			options.inDither = false;
			options.inPreferredConfig = Bitmap.Config.ARGB_8888;
			// 根据options参数,减少所需要的内存
			bm = BitmapFactory.decodeFileDescriptor(fd, null, options);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return bm;
	}

	/*
	 * 从文件当中获取专辑封面位图对象
	 * 
	 * @param context
	 * 
	 * @param songid
	 * 
	 * @param albumid
	 * 
	 * @param allowdefault
	 * 
	 * @return
	 */
	public static Bitmap getArtWork(Context context, long song_id,
			long album_id, boolean allowdefault, boolean small) {
		if (album_id < 0) {
			if (song_id < 0) {
				Bitmap bm = getArtworkFromFile(context, song_id, -1);
				if (bm != null) {
					return bm;
				}
			}
			if (allowdefault) {
				return getDefaultArtwork(context, small);
			}
			return null;
		}
		ContentResolver res = context.getContentResolver();
		Uri uri = ContentUris.withAppendedId(albumArtUri, album_id);
		if (uri != null) {
			InputStream in = null;
			try {
				in = res.openInputStream(uri);
				Options options = new Options();
				// 先制定原始大小
				options.inSampleSize = 1;
				// 只进行大小判断
				options.inJustDecodeBounds = true;
				// 调用此方法得到options得到图片大小
				BitmapFactory.decodeStream(in, null, options);
				/** 我们的目标是在你N pixel的画面显示。所以需要调用computerSampleSize得到图片缩放的比例 **/
				/** 这里的terget为800是根据默认专辑图片大小决定的，800只是测试数字但是试验后发现完美的结合 **/
				if (small) {
					options.inSampleSize = computeSampleSize(options, 40);
				} else {
					options.inSampleSize = computeSampleSize(options, 600);
				}
				// 得到缩放比例后，开始读入bitmap数据
				options.inJustDecodeBounds = false;
				options.inDither = false;
				options.inPreferredConfig = Bitmap.Config.ARGB_8888;
				in = res.openInputStream(uri);
				return BitmapFactory.decodeStream(in, null, options);
			} catch (FileNotFoundException e) {
				Bitmap bm = getArtworkFromFile(context, song_id, album_id);
				if (bm != null) {
					if (bm.getConfig() == null) {
						bm = bm.copy(Bitmap.Config.RGB_565, false);
						if (bm == null && allowdefault) {
							return getDefaultArtwork(context, small);
						}
					}
				} else if (allowdefault) {
					bm = getDefaultArtwork(context, small);
				}
				return bm;
			} finally {
				try {
					if (in != null) {
						in.close();
					}
				} catch (IOException e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	/*
	 * 对图片进行合适的缩放
	 * 
	 * @param options
	 * 
	 * @param target
	 * 
	 * @return
	 */
	public static int computeSampleSize(Options options, int target) {
		int w = options.outWidth;
		int h = options.outHeight;
		int candidateW = w / target;
		int candidateH = h / target;
		int candidate = Math.max(candidateW, candidateH);
		if (candidate == 0) {
			return 1;
		}
		if (candidate > 1) {
			if ((w > target) && (w / candidate) < target) {
				candidate -= 1;
			}
		}
		if (candidate > 1) {
			if ((h > target) && (h / candidate) < target) {
				candidate -= 1;
			}
		}
		return candidate;
	}

}
