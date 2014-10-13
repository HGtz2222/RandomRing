package com.tz.randomring;

import android.app.Activity;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity {

    public final static String B_PHONE_STATE = TelephonyManager.ACTION_PHONE_STATE_CHANGED;
	private BroadcastReceiverMgr mBroadcastReceiver; 
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
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
		Ring.test_scanMedia(this);
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
}
