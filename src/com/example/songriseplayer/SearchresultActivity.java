package com.example.songriseplayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.app.Constant;
import com.example.app.SongRisePlayerApp;
import com.example.utils.AppUtils;
import com.example.utils.DownloadUtils;
import com.example.utils.L;
import com.example.utils.MediaUtils;
import com.example.vo.Mp3Info;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.show.api.ShowApiRequest;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore.Audio.Media;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SeekBar.OnSeekBarChangeListener;

//搜索结果界面
public class SearchresultActivity extends BaseActivity implements OnClickListener
,OnItemClickListener{

	private ListView list_result;
	private String search_url="http://route.showapi.com/213-1";
	private String key;
	private AsyncHttpResponseHandler resHandler;
	private ListView listview_music;
	private ArrayList<HashMap<String,Object>> listdata=new 
			ArrayList<HashMap<String,Object>>();
	private SimpleAdapter adapter;
	private Context context;
	private ImageView search_result_back;
	private String appid="16950";
	private String secret="eac11e3d0cab4def9da973eb78f97512";
	private LinearLayout layout_load;

	private String []searchsongurl;
	private String songname[];
	private String singername[];
	private String songrank[];
	public  int code;
	private ImageView image_sp;
	private ImageView image_next;
	private ImageView img_album;
	private int currentPostion;
	private Thread th;
	private TextView tv_songname;
	private TextView tv_singername;
//	public Intel_playservice playService;

	private String msg[]=new String[2];
	private MainActivity mainActivity=new MainActivity();
	private LinearLayout layout_go;
	private LinearLayout rank_playall;
	private Mp3Info mp3Info;
	private SongRisePlayerApp app;
	private String album[];
	
	 private Handler handler=new Handler()
	    {
	    	public void handleMessage(Message msg) {
	    		if(msg.arg1==1)
	    		{
	    			img_album.setImageBitmap(DownloadUtils.getInstance().GetImgFromSDCard(app.getSongName()));
	    		}
	    	};
	    };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.search_result);
		bindPlayService();
		initView();
		searchSong(key);
		
		// 设置标题栏颜色
		AppUtils.setSystemStatusBar(SearchresultActivity.this);
	}

	public void setFirstView()
	{
		if(app.sp.getInt(Constant.CURRENT_PLAY_MUSIC,0)==0)
		{
			 if(app.getCode()==-1)
			 {
			 	image_sp.setImageResource(R.drawable.img_appwidget_pause1);
			 }
			 else if(app.getCode()==1)
			 {
			 	image_sp.setImageResource(R.drawable.img_appwidget_play1);
		     }
			mp3Info=app.getCurrentLocalMp3Infos().get(app.getCurrentPositionLocal());
			//歌曲名
			tv_songname.setText(mp3Info.getTitle());
			//歌手名
			tv_singername.setText(mp3Info.getArtist());
			//专辑处理
			Bitmap albumBitmap = MediaUtils.getArtWork(this,
					mp3Info.getId(), mp3Info.getAlbumId(), true, true);
			img_album.setImageBitmap(albumBitmap);	
		}
		else if(app.sp.getInt(Constant.CURRENT_PLAY_MUSIC,0)==1)
		{
		   if(app.getCode()==-1)
		 {
		 	image_sp.setImageResource(R.drawable.img_appwidget_pause1);
		 }
		 else if(app.getCode()==1)
		 {
		 	image_sp.setImageResource(R.drawable.img_appwidget_play1);
	     }
		 tv_songname.setText(app.getSongName());
		 tv_singername.setText(app.getSingerName());
		 DownloadUtils.getInstance().ShowImg(app.getAlbum(),img_album, handler,app.getSongName());
	   }
	}
	
	//初始化界面
	private void initView() {
		app=(SongRisePlayerApp) getApplication();
		img_album=(ImageView) findViewById(R.id.imageview1_album);
		list_result=(ListView) findViewById(R.id.list_result);
		resHandler=new Asynchttp();
		Intent intent=getIntent();
		Bundle bundle=intent.getExtras();
		key=bundle.getString("key");
		tv_songname=(TextView) findViewById(R.id.textview1_title);
		tv_singername=(TextView) findViewById(R.id.textview2_singer_name);
		search_result_back=(ImageView) findViewById(R.id.search_result_back);
		search_result_back.setOnClickListener(this);
		layout_load=(LinearLayout) findViewById(R.id.load_layout);
		layout_load.setVisibility(View.VISIBLE);
		list_result.setVisibility(View.GONE);
		layout_go=(LinearLayout) findViewById(R.id.layout_go);
		layout_go.setOnClickListener(this);
		image_sp=(ImageView) findViewById(R.id.image_sp);
		//image_sp.setImageResource(R.drawable.pause);
		image_next=(ImageView) findViewById(R.id.image_next);
		image_next.setImageResource(R.drawable.img_appwidget_play_next);
		image_next.setOnClickListener(this);
		list_result.setOnItemClickListener(this);
		image_sp.setOnClickListener(this);
		context=this;
		try {
			playService.initTextView(tv_songname, tv_singername);	
		} catch (Exception e) {
			// TODO: handle exception
			L.e("searchresult----inittextview------bug");
		}
		setFirstView();
	}


	//开启搜索实现类(show_api)
	private void searchSong(String key) {
		  new ShowApiRequest( "http://route.showapi.com/213-1", appid, secret)
        .setResponseHandler(resHandler)
        .addTextPara("keyword", key)
                    .addTextPara("page", "").post();
		
	}
	//搜索功能实现线程类
	private  class Asynchttp extends AsyncHttpResponseHandler
	{
		@Override
		public void onFailure(int arg0, Header[] arg1, byte[] arg2,
				Throwable arg3) {
			arg3.printStackTrace();
			
		}
		@Override
		public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
		
			layout_load.setVisibility(View.GONE);
			list_result.setVisibility(View.VISIBLE);
			String json=new String(arg2);
			JSONObject jOb2;
			JSONObject jOb3;
			JSONArray jOa = null;
			try
			{
			JSONObject jOb;
			jOb = new JSONObject(json);
			jOb2=jOb.getJSONObject("showapi_res_body");
			jOb3=jOb2.getJSONObject("pagebean");
			jOa=jOb3.getJSONArray("contentlist");
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			 songname=new String[jOa.length()];
			 singername=new String[jOa.length()];
			 songrank=new String[jOa.length()];
			 searchsongurl=new String[jOa.length()];
			 
			 album=new String[jOa.length()];
			for(int i=0;i<jOa.length();i++)
			{
				JSONObject jOb_1 = null;
				try {
					jOb_1 = jOa.getJSONObject(i);
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
			    songrank[i]=String.valueOf(i+1);
				try
				{
				  String s1=jOb_1.getString("singername").toString().trim()+"(";
				  singername[i]=s1.substring(0,s1.indexOf("("));
				}
				catch(Exception e)
			   {
//					singername[i]="未知歌手";
					singername[i]=key;
				}
				try
				{
					  String s2=jOb_1.getString("songname").toString().trim()+"(";
					  songname[i]=s2.substring(0,s2.indexOf("("));
				}
				catch(Exception e)
				{
//					songname[i]="未知歌曲";

					songname[i]=key;
				}
				try
				{	
					searchsongurl[i]=jOb_1.getString("downUrl").toString().trim();
				}
				catch(Exception e)
				{
					
				}
				try
				{	
					album[i]=jOb_1.getString("albumpic_big").toString().trim();
				}
				catch(Exception e)
				{
					
				}
			}
			for(int i=0;i<songname.length;i++)
			{			
				HashMap<String,Object> map=new HashMap<String,Object>();
				map.put("singername",singername[i]);
				map.put("songname",songname[i]);
				map.put("songrank",songrank[i]);
				listdata.add(map);
			}
			adapter=new SimpleAdapter(context,listdata,R.layout.detail_item,
					new String[]{"songrank","songname","singername"},new int[]{R.id.detail_rank,R.id.detail_item_musicname,
					R.id.detail_item_singer});
			list_result.setAdapter(adapter);
			Intent intent=new Intent(SearchresultActivity.this,PlayService.class);
			Bundle bundle=new Bundle();
			bundle.putStringArray("url_array",searchsongurl);
			bundle.putInt("play_code",1);
			bundle.putStringArray("singername",singername);
			bundle.putStringArray("songname", songname);
			bundle.putStringArray("album",album);
			intent.putExtras(bundle);
			startService(intent);
			playService.initTextView(tv_songname, tv_singername);
		}		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.search_result_back:
			finish();
//			if(isServiceWork(this,"com.example.songriseplayer.SearchresultActivity")==true)
//			{
//				Toast.makeText(this,"服务运行中",Toast.LENGTH_SHORT).show();
//			}
//			else
//			{
//				Toast.makeText(this,"服务退出",Toast.LENGTH_SHORT).show();
//
//			}
			//player.stop();
			break;
		//播放/暂停图标设置与播放/暂停歌曲
		case R.id.image_sp:

			
			playService.initTextView(tv_songname,tv_singername);
			if(app.sp.getInt(Constant.CURRENT_PLAY_MUSIC,0)==playService.NET_MUSIC)
			{
			  	if(app.getCode()==1 && playService.isPlaying())
				{
					playService.pause();
				    image_sp.setImageResource(R.drawable.img_appwidget_pause1);
				}
				else
				{
					image_sp.setImageResource(R.drawable.img_appwidget_play1);	
			      if(app.getCode()==-1 && playService.isPause())
				   {
			    	  playService.startMusic();
				   }
			      else
			      {
			    	 playService.playMusic(-1);
			    	 playService.initTextView(tv_songname, tv_singername);
			      }
				}
			}
			
			else 
			{
				if ((app.getCode()==1 && playService.isPlaying())) {
					image_sp.setImageResource(R.drawable.img_appwidget_pause1);
					playService.pause();
					// isPause = true;
				} 
				else 
				{
					  image_sp.setImageResource(R.drawable.img_appwidget_play1);
					if (playService.isPause()) {
						playService.start();
					} else {
						// mainActivity.playService.play(mainActivity.playService
						// .getCurrentPosition());
						playService
								.setCurrentPlayMusic(PlayService.LOCAL_MUSIC);
						playService.playMusic(playService
								.getCurrentPosition());
					}
				}
			}
			break;
			//下一曲
		case R.id.image_next:
			playService.initTextView(tv_songname,tv_singername);
			if(app.sp.getInt(Constant.CURRENT_PLAY_MUSIC,0)==playService.LOCAL_MUSIC)
			{
				playService.next();
			}
			else if(app.sp.getInt(Constant.CURRENT_PLAY_MUSIC,0)==playService.NET_MUSIC)
			{
				playService.next();
				//setSongMg();
				setFirstView();
			    image_sp.setImageResource(R.drawable.img_appwidget_play1);
			}
			image_sp.setImageResource(R.drawable.img_appwidget_play1);	

			break;
		case R.id.layout_go:
			//跳转到播放界面
//			Intent intent=new Intent(SearchresultActivity.this,PlayInternetMusicActivity.class);
//			startActivity(intent);
			if (playService.getCurrentPlayMusic() == PlayService.LOCAL_MUSIC) {
				Intent intent = new Intent(this, PlayActivity.class);
				startActivity(intent);
			} else if (playService.getCurrentPlayMusic() == PlayService.NET_MUSIC) {
				Intent intent = new Intent(this,
						PlayInternetMusicActivity.class);
				startActivity(intent);
			}
		default:
			break;
		}
	}

//	public void setSongMg()
//	{
//		msg=playService.getPlayingSongMsg();
//		if(songname!=null)
//		{
//		    tv_songname.setText(msg[0]);
//		}
//		else
//		{
//			tv_songname.setText("-");
//		}
//		if(singername!=null)
//		{
//		    tv_singername.setText(msg[1]);
//		}
//		else
//		{
//			tv_singername.setText("-");
//		}
//		app.SetSongMsg(msg[0],msg[1]);
//	}
	//列表单击事件
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		playService.initTextView(tv_songname,tv_singername);
		image_sp.setImageResource(R.drawable.img_appwidget_play1);
		app.setPosition(position);
		if(playService!=null)
		{
			Log.i("124","1_connection");
			playService.setCurrentPlayMusic(1);
			
			playService.setFirstMsg();
			playService.playMusic(-1);
		}
		else
		{
			Log.i("124","1_unconnection");
		}
		//setSongMg();
		setFirstView();
		
	}
	

	//判断服务是否运行实现方法

	public boolean isServiceWork(Context mContext, String serviceName) {  
	    boolean isWork = false;  
	    ActivityManager myAM = (ActivityManager) mContext  
	            .getSystemService(Context.ACTIVITY_SERVICE);  
	    List<RunningServiceInfo> myList = myAM.getRunningServices(40);  
	    if (myList.size() <= 0) {  
	        return false;  
	    }  
	    for (int i = 0; i < myList.size(); i++) {  
	        String mName = myList.get(i).service.getClassName().toString();  
	        if (mName.equals(serviceName)) {  
	            isWork = true;  
	            break;  
	        }  
	    }  
	    return isWork;  
	} 
	//同步图标
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		setFirstView();
	}
	@Override
	public void publish(int progress) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void change(int position) {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unbindPlayService();
	}

}
