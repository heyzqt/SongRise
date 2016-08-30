package com.example.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.io.ObjectInputStream.GetField;
import java.lang.annotation.Target;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.renderscript.Element;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.example.app.Constant;
import com.example.songriseplayer.MainActivity;
import com.example.songriseplayer.R;
import com.example.vo.Mp3Info;
import com.example.vo.SearchResult;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.show.api.ShowApiRequest;

/*
 * 下载操作类
 * 调用此类前需要先获取 音乐ID：MusicId、 歌曲下载地址：url
 */
public class DownloadUtils {

	private static DownloadUtils sInstance;

	private ExecutorService mThreadPool;

	private static Context context;

	public static String mp3InfoshowId = null; // 歌曲id showApi
	public static int music_count = 0; // 返回的歌曲记录条数

	public DownloadUtils(Context context) {
		this.context = context;
	}

	// lilei
	/**
	 * 获取SD卡中已下载的图片
	 * 
	 * @param name
	 * @return
	 */
	public Bitmap GetImgFromSDCard(String name) {

		String path = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/SongRise/Img/" + name + ".png";
		File file = new File(path);
		if (file.exists()) {

			Log.i("haha", "File Is Exist");

			Bitmap bit = new BitmapFactory().decodeFile(path);

			return bit;
		} else {
			Log.i("haha", "File is Not Exist");
			return null;
		}
	}

	/**
	 * 图片显示方法
	 * @param url 图片路径
	 * @param img 待显示的imgview控件
	 * @param hand 消息接收
	 * @param MusicName 歌曲名
	 */
	public void ShowImg(String url,ImageView img,final Handler hand,String MusicName)
	{
		
		File file = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/SongRise/Img");
		if (!file.exists())
			file.mkdirs();

		String path = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/SongRise/Img/"+MusicName+".png";
		
		File mfile = new File(path);
		
		if(mfile.exists())
		{
			Bitmap bit = BitmapFactory.decodeFile(path);
			img.setImageBitmap(bit);
		}
		
		else
		{
			File EndFile = new File(file,MusicName + ".png");
			
			AsyncHttpClient client = new AsyncHttpClient();

			try {
				client.get(
						url,
						new FileAsyncHttpResponseHandler(EndFile) {

							@Override
							public void onSuccess(int arg0, Header[] arg1, File arg2) {
								// TODO Auto-generated method stub
								Log.i("haha", "Image DownLoad Success");
								
								Message msg = new Message();
								msg.arg1=1;
								hand.sendMessage(msg);
								
							}

							@Override
							public void onFailure(int arg0, Header[] arg1,
									Throwable arg2, File arg3) {
								// TODO Auto-generated method stub
								Log.i("haha", "Image DownLoad Default");
							}
						});
			} catch (Exception e) {
				// TODO: handle exception
				L.e("showImg======"+"没有获取图片链接");
				img.setImageResource(R.drawable.music_icon);
			}
		}	
	}
	
	/**
	 * 图片显示方法 通知栏
	 * @param url 图片路径
	 * @param img 待显示的imgview控件
	 * @param hand 消息接收
	 * @param MusicName 歌曲名
	 */
	public void ShowImg1(String url,RemoteViews img,final Handler hand,String MusicName)
	{
		
		File file = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/SongRise/Img");
		if (!file.exists())
			file.mkdirs();

		String path = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/SongRise/Img/"+MusicName+".png";
		
		File mfile = new File(path);
		
		if(mfile.exists())
		{
			Bitmap bit = BitmapFactory.decodeFile(path);
			img.setImageViewBitmap(R.id.custom_song_icon, bit);
			//img.setImageBitmap(bit);
		}
		
		else
		{
			File EndFile = new File(file,MusicName + ".png");
			
			AsyncHttpClient client = new AsyncHttpClient();

			try {
				client.get(
						url,
						new FileAsyncHttpResponseHandler(EndFile) {

							@Override
							public void onSuccess(int arg0, Header[] arg1, File arg2) {
								// TODO Auto-generated method stub
								Log.i("haha", "Image DownLoad Success");
								
								Message msg = new Message();
								msg.arg1=1;
								hand.sendMessage(msg);
								
							}

							@Override
							public void onFailure(int arg0, Header[] arg1,
									Throwable arg2, File arg3) {
								// TODO Auto-generated method stub
								Log.i("haha", "Image DownLoad Default");
							}
						});
			} catch (Exception e) {
				// TODO: handle exception
				L.e("showImg======"+"没有获取图片链接");
				img.setImageViewResource(R.id.custom_song_icon,R.drawable.music_icon);
				//img.setImageResource(R.drawable.music_icon);
			}
		}	
	}
	
	
	
	/**
	 * 扫描SongRise/Music中的歌曲
	 * 
	 * @return
	 */
	public String[] FileNameScanner() {

		String[] filesStr = null;

		// 扫描SongRise/Music文件夹下的音乐文件
		// 1.判断SD卡状态是否可用
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {

			// 2.获取根路径
			String dir = Environment.getExternalStorageDirectory()
					.getAbsolutePath();
			String mfile = "/SongRise/Music";
			File file = new File(dir + mfile);
			if (!file.exists()) {
				file.mkdirs();
				Log.e("TAG", "/SongRise/mp3文件不存在");
				return null;
			}

			// 3.读取文件
			File path = new File(dir + mfile);
			File[] files = path.listFiles();// 读取

			// 保存文件名
			filesStr = new String[files.length];
			if (path.exists()) {
				int i = 0;
				for (File f : files) {

					String check_name = f.getName();
					filesStr[i] = check_name;
					i++;
					// L.e("filename===="+check_name);
				}
			}

		}

		return filesStr;

	}

	/**
	 * 扫描SongRise/Music中的歌曲
	 * 
	 * @return
	 */
	public String[] FileScanner() {

		String[] filesStr = null;
		String[] pathsStr = null;

		// 扫描SongRise/Music文件夹下的音乐文件
		// 1.判断SD卡状态是否可用
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {

			// 2.获取根路径
			String dir = Environment.getExternalStorageDirectory()
					.getAbsolutePath();
			String mfile = "/SongRise/Music";
			File file = new File(dir + mfile);
			if (!file.exists()) {
				file.mkdirs();
				Log.e("TAG", "/SongRise/mp3文件不存在");
				return null;
			}

			// 3.读取文件
			File path = new File(dir + mfile);
			File[] files = path.listFiles();// 读取

			// 保存文件名
			filesStr = new String[files.length];
			if (path.exists()) {
				int i = 0;
				for (File f : files) {

					String check_name = f.getName();
					filesStr[i] = check_name;
					i++;
					L.e("filename====" + check_name);
				}
			}

		}

		// 保存String[]对象
		if (filesStr != null) {
			pathsStr = new String[filesStr.length];
			for (int i = 0; i < filesStr.length; i++) {
				pathsStr[i] = Environment.getExternalStorageDirectory()
						.getAbsolutePath()
						+ "/SongRise/Music/"
						+ filesStr[i].toString();
				L.e("filename===" + filesStr[i]);
			}

		}
		return pathsStr;

	}

	/*
	 * 获取下载实例
	 */
	public synchronized static DownloadUtils getInstance() {
		if (sInstance == null) {
			try {
				sInstance = new DownloadUtils();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return sInstance;
	}

	private DownloadUtils() throws ParserConfigurationException {
		mThreadPool = Executors.newSingleThreadExecutor();
	}

	/**
	 * 检查专辑图片是否存在
	 * 
	 * @param musicname
	 * @param singername
	 */
	public boolean checkMusicImgExist(String musicname) {

		// 1.判断SD卡状态是否可用
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {

			// 2.获取根路径
			String dir = Environment.getExternalStorageDirectory()
					.getAbsolutePath();
			String mfile = "/SongRise/Img";
			File file = new File(dir + mfile);
			if (!file.exists()) {
				file.mkdirs();
				Log.e("TAG", "/SongRise/Img文件不存在");
				return false;
			}

			// 3.读取文件
			File path = new File(dir + mfile);
			File[] files = path.listFiles();// 读取
			if (path.exists()) {
				String music = musicname + ".png";
				for (File f : files) {

					String check_name = f.getName();
					if (check_name.equals(music)) {
						return true;
					}
				}
			}

		}
		return false;

	}

	/**
	 * 下载歌曲图
	 * 
	 * @param Url
	 * @param MusicName
	 */
	public void DownLoadImage(String Url, String MusicName) {
		// TODO Auto-generated method stub

		File file = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/SongRise/Img");
		if (!file.exists())
			file.mkdirs();
		File EndFile = new File(file, MusicName + ".png");

		AsyncHttpClient client = new AsyncHttpClient();

		client.get(
				"http://i.gtimg.cn/music/photo/mid_album_300/s/d/002IFHKY18Kssd.jpg",
				new FileAsyncHttpResponseHandler(EndFile) {

					@Override
					public void onSuccess(int arg0, Header[] arg1, File arg2) {
						// TODO Auto-generated method stub
						Log.i("haha", "Image DownLoad Success");
					}

					@Override
					public void onFailure(int arg0, Header[] arg1,
							Throwable arg2, File arg3) {
						// TODO Auto-generated method stub
						Log.i("haha", "Image DownLoad Default");
					}
				});
	}

	/*
	 * 
	 * 从服务器获取APPID和AppSecret
	 */

	private Map<String, String> GetApiInfo() {
		final Map<String, String> map1 = new HashMap<String, String>();
		AsyncHttpClient client = new AsyncHttpClient();
		client.post("服务器的URL", new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				// TODO Auto-generated method stub
				map1.put("AppID", "");
				map1.put("AppSecret", "");

			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub

			}
		});
		return map1;
	}

	/**
	 * 判断当前歌曲是否已下载
	 * 
	 * @param musicname
	 * @param singername
	 * @return
	 */
	public static boolean checkMusicExist(String musicname) {

		// 1.判断SD卡状态是否可用
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {

			// 2.获取根路径
			String dir = Environment.getExternalStorageDirectory()
					.getAbsolutePath();
			String mfile = "/SongRise/Music";
			File file = new File(dir + mfile);
			if (!file.exists()) {
				file.mkdirs();
				Log.e("TAG", "/SongRise/mp3文件不存在");
				return false;
			}

			// 3.读取文件
			File path = new File(dir + mfile);
			File[] files = path.listFiles();// 读取
			if (path.exists()) {
				String music = musicname + ".mp3";
				for (File f : files) {

					String check_name = f.getName();
					if (check_name.equals(music)) {
						return true;
					}
				}
			}

		}
		return false;
	}

	/*
	 * 歌曲下载类 Music Download url : 下载地址
	 */
	public void DownLoadMusic(final Context context, String url,
			final String MusicName) {

		AsyncHttpClient client = new AsyncHttpClient();

		String dir = Environment.getExternalStorageDirectory()
				.getAbsolutePath();
		String mfile = "/SongRise/Music";
		File file = new File(dir + mfile);
		if (!file.exists()) {
			file.mkdirs();
			File refile = new File(file, MusicName + ".mp3");
			Log.i("haha", file.mkdirs() + "");
		}
		File refile = new File(file, MusicName + ".mp3");
		Log.i("haha", dir + "");
		// "http://tsmusic24.tc.qq.com/151784.mp3"
		client.get(url, new FileAsyncHttpResponseHandler(refile) {
			@Override
			public void onSuccess(int arg0, Header[] arg1, File arg2) {
				// TODO Auto-generated method stub
				Log.i("haha", "file --Success");
				T.showShort(context, "下载成功");

				// //给歌曲加一个是下载歌曲的标记
				// Mp3Info mp3Info = playService.mp3Infos.get(playService
				// .getCurrentPosition());
				// try {
				//
				// Mp3Info likeMp3Info = app.dbUtils.findFirst(Selector.from(
				// Mp3Info.class).where("mp3InfoId", "=", getId(mp3Info)));
				//
				// if (likeMp3Info == null) {
				//
				// mp3Info.setMp3InfoId(mp3Info.getId());
				// mp3Info.setIsLike(1);
				// app.dbUtils.save(mp3Info);
				// } else {
				// int isLike = likeMp3Info.getIsLike();
				// if (isLike == 1) {
				// likeMp3Info.setIsLike(0);
				// } else {
				// likeMp3Info.setIsLike(1);
				// }
				// app.dbUtils.update(likeMp3Info, "isDownload");
				//
				// }
				//
				// } catch (DbException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, Throwable arg2,
					File arg3) {
				// TODO Auto-generated method stub
				Log.i("haha", "DF" + arg2 + "----" + arg3 + "");
				T.showShort(context, "下载失败");
			}
		});
	}

	/**
	 * // 判断当前歌词是否存在于SD卡中
	 * 
	 * @param musicname
	 * @param singername
	 * @return
	 */
	public static boolean checkMusicLrcExist(String musicname, String singername) {

		// 1.判断SD卡状态是否可用
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {

			// 2.获取根路径
			String dir = Environment.getExternalStorageDirectory()
					.getAbsolutePath();
			String mfile = "/SongRise/LRC";
			File file = new File(dir + mfile);
			if (!file.exists()) {
				file.mkdirs();
				Log.e("TAG", "/SongRise/LRC文件不存在");
				return false;
			}

			// 3.读取文件
			File path = new File(dir + mfile);
			File[] files = path.listFiles();// 读取
			if (path.exists()) {
				String music = musicname + "_" + singername + ".LRC";
				for (File f : files) {

					String check_name = f.getName();
					if (check_name.equals(music)) {
						return true;
					}
				}
			}

		}
		return false;

	}

	// /**
	// * 歌词下载方法
	// * @param MusicID 解析后获取的歌曲ID
	// * @param MusicName 歌曲名
	// * @param singername 歌手名
	// */
	// public static void LrcDownload(String MusicID, final String
	// MusicName,final String singername) {
	// String dir = Environment.getExternalStorageDirectory()
	// .getAbsolutePath();
	// String mfile = "/SongRise/LRC";
	// File file = new File(dir + mfile);
	// if (!file.exists()) {
	// file.mkdirs();
	// final File refile = new File(file, MusicName+"_"+singername+ ".LRC");
	// //Log.i("haha", file.mkdirs() + ".." + refile);
	// }
	// final File refile = new File(file, MusicName+"_"+singername+ ".LRC");
	// final AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
	// @SuppressWarnings("resource")
	// @Override
	// public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
	// // TODO Auto-generated method stub
	//
	// try {
	// JSONObject root = new JSONObject(new String(arg2, "utf-8"));
	//
	// if (root.getInt("showapi_res_code") != 0) {
	// Log.i("haha",
	// "Error:" + "--"
	// + root.getString("showapi_res_error"));
	// }
	//
	// else {
	//
	// JSONObject body = root
	// .getJSONObject("showapi_res_body");
	//
	// String lrc = body.getString("lyric");
	//
	// String re1 = lrc.replace("&#58;", ":");
	//
	// String re2 = re1.replace("&#10;", "\r\n");
	//
	// String re3 = re2.replace("&#32;", " ");
	//
	// String re4 = re3.replace("&#45;", "-");
	//
	// String re5 = re4.replace("&#13;", "");
	//
	// String re6 = re5.replace("&#40;", "");
	//
	// String re7 = re6.replace("&#41;", "");
	//
	// String filrc = re7.replace("&#46;", ".");
	//
	//
	// if (!refile.exists()) {
	// refile.createNewFile();
	// }
	//
	// FileWriter fw = null;
	// BufferedWriter bw = null;
	// try {
	//
	// fw = new FileWriter(refile, false);
	//
	// bw = new BufferedWriter(fw);
	// bw.write(filrc);
	// bw.newLine();
	// bw.flush();
	// bw.close();
	// fw.close();
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// try {
	// bw.close();
	// fw.close();
	// } catch (IOException e1) {
	// // TODO Auto-generated catch block
	// }
	// }
	//
	// }
	//
	// } catch (UnsupportedEncodingException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (JSONException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// L.e("下载失败");
	// }
	//
	// }
	//
	// @Override
	// public void onFailure(int arg0, Header[] arg1, byte[] arg2,
	// Throwable arg3) {
	// // TODO Auto-generated method stub
	// Log.i("haha", "Lrc--fail");
	// }
	// };
	//
	// // Map<String,String> map1 = GetApiInfo();
	// Map<String, String> map1 = new HashMap<String, String>();
	// map1.put("AppID", "16947");
	// map1.put("AppSecret", "2e8da330ca5e4724911164d9053bedda");
	// new ShowApiRequest("http://route.showapi.com/213-2", map1.get("AppID"),
	// map1.get("AppSecret")).setResponseHandler(res)
	// .addTextPara("musicid", MusicID).post();
	//
	// }

}
