package com.tz.randomring;

import java.util.ArrayList;
import java.util.HashMap;

import net.youmi.android.banner.AdSize;
import net.youmi.android.banner.AdView;
import net.youmi.android.banner.AdViewListener;
import android.app.Activity;
import android.content.IntentFilter;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {

    public final static String B_PHONE_STATE = TelephonyManager.ACTION_PHONE_STATE_CHANGED;
	private BroadcastReceiverMgr mBroadcastReceiver;
	private RingInfo ringInfo;
	private SelectRingsDialogAdapter sa; 
	private ListView ringList;
	private ImageButton btnInsert;
	private ImageButton btnMenu; 
	private TextView tv_empty_tip;
	private ArrayList<HashMap<String, Object>> data;
	
	private void initUI(){
		ringList = (ListView)findViewById(R.id.ringlist);
		btnInsert = (ImageButton)findViewById(R.id.btn_insert);
		btnMenu = (ImageButton)findViewById(R.id.btn_menu);
		tv_empty_tip = (TextView)findViewById(R.id.empty_tip);
	}
	
	private void initListener(){
		btnInsert.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				refreshSDCard();
				MyDialog.selectRings(MainActivity.this);
			}
		});
		btnMenu.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				openOptionsMenu();
			}
		});
	}
	
	private void initData(){
		ringInfo = new RingInfo();
		data = ringInfo.getData(this);
		sa = new SelectRingsDialogAdapter(this, data, R.layout.vlist, 
				new String[]{"title"}, 
				new int[]{R.id.tv_title, R.id.btn_playing, R.id.btn_del});
		ringList.setAdapter(sa);
		refreshTip(data.size() == 0);
	}
	
	private void refreshSDCard(){
		Log.e("tz", "Scanned start");
		MediaScannerConnection.scanFile(this,  
                new String[] { Environment.getExternalStorageDirectory().getAbsolutePath() }, null,  
                new MediaScannerConnection.OnScanCompletedListener() {  
            public void onScanCompleted(String path, Uri uri) {  
                Log.e("tz", "Scanned " + path + ":");  
                Log.e("tz", "-> uri=" + uri);  
            }  
       }); 
	}
	
	private void refreshTip(boolean isEmpty){
		if (isEmpty){
			tv_empty_tip.setText(R.string.empty_list_tip);
		}else{
			tv_empty_tip.setText(R.string.not_empty_list_tip);
		}
	}
	
	public void refreshData(){
		// 先将旧的项关掉; 
		if (lastPath != null){
			int index = getIndexOfRingData(lastPath);
			setPlayingStatus(index, false);
		}
		data = ringInfo.getData(this);
		sa.setData(data);
		sa.notifyDataSetChanged();
		refreshTip(data.size() == 0);
		// 再设置新的播放项; 
		if (lastPath != null){
			int index = getIndexOfRingData(lastPath);
			setPlayingStatus(index, true);
			if (index == -1){
				// 正在播放的音乐已经被删除
				MediaPlayerSingleton.play(lastPath, true);
			}
		}
	}
	
	public void setAllData(){
		ringInfo.setAllData(this);
	}
	
	public ArrayList<HashMap<String, Object>> getData(){
		return ringInfo.getData(this);
	}
	
	public ArrayList<HashMap<String, Object>> getAllData(){
		return ringInfo.getAllData(this);
	}
	
	public void setData(ArrayList<HashMap<String, Object>> data){
		ringInfo.setData(this, data);
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initUI();
        initListener();
        initData();
        initAdBar();
		//registerIt();  // 注册BroadcastReceiver已经通过manifest完成了, 不需要调用注册函数; 
    }

    @Override
	protected void onPause() {
		super.onPause();
		MediaPlayerSingleton.play(lastPath, true);
		//unregisterIt();
	}

	@Override
	protected void onResume() {
		super.onResume();
		//registerIt();
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.main, menu);
		menu.add(Menu.NONE, Menu.FIRST + 1, 1, getResources().getString(R.string.clear_all_rings));
        return true;
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == Menu.FIRST + 1){
			clearAllRings();
		}
		return true;
	}

	private void clearAllRings() {
		Log.e("tz", "clearAllRings");
		// 1. 清空数据库所有的值;
		ringInfo.removeAllData(this);
		// 2. 重新查询数据库, 获取到当前的data; 
		refreshData();
	}

	@Override
	public void onBackPressed() {
		Log.e("tz", "onBackPressed");
		super.onBackPressed();
	}
    
    public void registerIt() {  
        Log.i("tz", "registerIt");  
        mBroadcastReceiver = new BroadcastReceiverMgr();  
        IntentFilter intentFilter = new IntentFilter();  
        intentFilter.addAction(B_PHONE_STATE);  
        intentFilter.setPriority(Integer.MAX_VALUE);  
        registerReceiver(mBroadcastReceiver, intentFilter);  
    }  
      
    public void unregisterIt() {  
        Log.i("tz", "unregisterIt");  
        unregisterReceiver(mBroadcastReceiver);  
    }

	public void updateDB(HashMap<String, Object> map) {
		Boolean isSelected = (Boolean)map.get("isSelected");
		if (isSelected){
			// 插入数据;
			ringInfo.insert(this, map);
		}else{
			// 删除数据; 
			ringInfo.remove(this, map);
		}
	}

	private String lastPath; 
	
	public void playRing(String path, int index) {
		// 用来处理播放铃声; 
		// 通过铃声播放类来控制播放状态; 
		if (path.equals(lastPath)){
			// 当前点击的和上次点击的为相同铃声, 停止播放; 
			MediaPlayerSingleton.play(path, true);
			setPlayingStatus(index, false);
			lastPath = null; // 清空lastPath; 
			return ; 
		}
		// 如果上次播放的铃声和本次选择的不一致, 则关闭上次的播放图标; 
		if (lastPath != null){
			int ringIndex = getIndexOfRingData(lastPath);
			if (ringIndex != -1){
				// 上一个铃声还存在, 则把上一个家伙干掉; 
				setPlayingStatus(ringIndex, false);
			}
		}
		// 播放当前铃声; 
		setPlayingStatus(index, true);
		MediaPlayerSingleton.play(path, false);
		lastPath = path;
	}

	private void setPlayingStatus(int index, boolean isPlaying){
		if (index < 0){
			return ; 
		}
		LinearLayout layout = (LinearLayout)ringList.getChildAt(index);
		if (layout == null){
			return ; 
		}
		ImageButton btnPlaying = (ImageButton)layout.findViewById(R.id.btn_playing);
		btnPlaying.setVisibility(isPlaying ? ImageButton.VISIBLE : ImageButton.INVISIBLE);
	}
	
	private int getIndexOfRingData(String lastPath) {
		for (int i = 0; i < data.size(); ++i){
			HashMap<String, Object> map = data.get(i);
			String path = (String)map.get("data");
			if (lastPath.equals(path)){
				return i; 
			}
		}
		return -1;
	}  
	
	private void initAdBar(){
		// 实例化广告条
		AdView adView = new AdView(this, AdSize.FIT_SCREEN);
		// 获取要嵌入广告条的布局
		LinearLayout adLayout=(LinearLayout)findViewById(R.id.adLayout);
		// 将广告条加入到布局中
		adLayout.addView(adView);
		
		adView.setAdListener(new AdViewListener() {

		    @Override
		    public void onSwitchedAd(AdView adView) {
		        // 切换广告并展示
		    	Log.e("tz", "切换广告");
		    }

		    @Override
		    public void onReceivedAd(AdView adView) {
		        // 请求广告成功
		    	Log.e("tz", "请求广告成功");
		    }

		    @Override
		    public void onFailedToReceivedAd(AdView adView) {
		        // 请求广告失败
		    	Log.e("tz", "请求广告失败");
		    }
		});

	}
}
