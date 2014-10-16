package com.tz.randomring;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.IntentFilter;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

public class MainActivity extends Activity {

    public final static String B_PHONE_STATE = TelephonyManager.ACTION_PHONE_STATE_CHANGED;
	private BroadcastReceiverMgr mBroadcastReceiver;
	private RingInfo ringInfo;
	private SelectRingsDialogAdapter sa; 
	private ListView ringList;
	private Button btnInsert;
	private ArrayList<HashMap<String, Object>> data;
	
	private void initUI(){
		ringList = (ListView)findViewById(R.id.ringlist);
		btnInsert = (Button)findViewById(R.id.btn_insert);
	}
	
	private void initListener(){
		btnInsert.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				refreshSDCard();
				MyDialog.selectRings(MainActivity.this);
			}
		});
	}
	
	private void initData(){
		ringInfo = new RingInfo();
		data = ringInfo.getData(this);
		sa = new SelectRingsDialogAdapter(this, data, R.layout.vlist, 
				new String[]{"title"}, 
				new int[]{R.id.tv_title, R.id.btn_del});
		ringList.setAdapter(sa);
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
	
	public void refreshData(){
		ArrayList<HashMap<String, Object>> tmp = ringInfo.getData(this);
		sa.setData(tmp);
		sa.notifyDataSetChanged();
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
        
		registerIt();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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
}
