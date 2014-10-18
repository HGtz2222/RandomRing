package com.tz.randomring;

import net.youmi.android.AdManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends Activity{
	private final int SPLASH_DISPLAY_LENGHT = 2500;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_activity);
		new Handler().postDelayed(new Runnable() {  
            public void run() {  
                Intent mainIntent = new Intent(SplashActivity.this,  
                        MainActivity.class);  
                SplashActivity.this.startActivity(mainIntent);  
                SplashActivity.this.finish();  
            }  
  
        }, SPLASH_DISPLAY_LENGHT);  
		
		initAd();
	}

	private void initAd(){
		AdManager.getInstance(this).init("8210c280e7b1f36d", "70495a9de61f0e75", false);
		//SpotManager.getInstance(this).loadSpotAds();// TODO 暂时关闭插屏广告; 
	}
}
