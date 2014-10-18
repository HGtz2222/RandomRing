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
		// �Ƚ��ɵ���ص�; 
		if (lastPath != null){
			int index = getIndexOfRingData(lastPath);
			setPlayingStatus(index, false);
		}
		data = ringInfo.getData(this);
		sa.setData(data);
		sa.notifyDataSetChanged();
		refreshTip(data.size() == 0);
		// �������µĲ�����; 
		if (lastPath != null){
			int index = getIndexOfRingData(lastPath);
			setPlayingStatus(index, true);
			if (index == -1){
				// ���ڲ��ŵ������Ѿ���ɾ��
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
		//registerIt();  // ע��BroadcastReceiver�Ѿ�ͨ��manifest�����, ����Ҫ����ע�ắ��; 
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
		// 1. ������ݿ����е�ֵ;
		ringInfo.removeAllData(this);
		// 2. ���²�ѯ���ݿ�, ��ȡ����ǰ��data; 
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
			// ��������;
			ringInfo.insert(this, map);
		}else{
			// ɾ������; 
			ringInfo.remove(this, map);
		}
	}

	private String lastPath; 
	
	public void playRing(String path, int index) {
		// ��������������; 
		// ͨ�����������������Ʋ���״̬; 
		if (path.equals(lastPath)){
			// ��ǰ����ĺ��ϴε����Ϊ��ͬ����, ֹͣ����; 
			MediaPlayerSingleton.play(path, true);
			setPlayingStatus(index, false);
			lastPath = null; // ���lastPath; 
			return ; 
		}
		// ����ϴβ��ŵ������ͱ���ѡ��Ĳ�һ��, ��ر��ϴεĲ���ͼ��; 
		if (lastPath != null){
			int ringIndex = getIndexOfRingData(lastPath);
			if (ringIndex != -1){
				// ��һ������������, �����һ���һ�ɵ�; 
				setPlayingStatus(ringIndex, false);
			}
		}
		// ���ŵ�ǰ����; 
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
		// ʵ���������
		AdView adView = new AdView(this, AdSize.FIT_SCREEN);
		// ��ȡҪǶ�������Ĳ���
		LinearLayout adLayout=(LinearLayout)findViewById(R.id.adLayout);
		// ����������뵽������
		adLayout.addView(adView);
		
		adView.setAdListener(new AdViewListener() {

		    @Override
		    public void onSwitchedAd(AdView adView) {
		        // �л���沢չʾ
		    	Log.e("tz", "�л����");
		    }

		    @Override
		    public void onReceivedAd(AdView adView) {
		        // ������ɹ�
		    	Log.e("tz", "������ɹ�");
		    }

		    @Override
		    public void onFailedToReceivedAd(AdView adView) {
		        // ������ʧ��
		    	Log.e("tz", "������ʧ��");
		    }
		});

	}
}
