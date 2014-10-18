package com.tz.randomring;

import java.io.IOException;

import android.media.MediaPlayer;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

public class MediaPlayerSingleton {
	private static MediaPlayer mediaPlayer = new MediaPlayer();
	private static View lastView;
	private static ImageButton lastPlaying;
	
	public static void playRing(String path, View view){
		if (view.equals(MediaPlayerSingleton.lastView)){
			play(path, true); // 暂停播放; 
			MediaPlayerSingleton.lastView = null;
		}else{
			play(path, false);  // 播放新铃声; 
			MediaPlayerSingleton.lastView = view;
		}
	}
	
	public static void playRing(String path, View view, ImageButton btnPlaying) {
		if (view.equals(MediaPlayerSingleton.lastView)){
			play(path, true); // 暂停播放; 
			MediaPlayerSingleton.lastView = null;
		}else{
			play(path, false);  // 播放新铃声; 
			MediaPlayerSingleton.lastView = view;
			btnPlaying.setVisibility(ImageButton.VISIBLE);
			if (lastPlaying != null){
				lastPlaying.setVisibility(ImageButton.INVISIBLE);
			}
			lastPlaying = btnPlaying;
		}
	}
	
	public static void play(String path, boolean isPause){
		Log.e("tz", "isPlaying ");
		if (mediaPlayer.isPlaying()) {  
		   mediaPlayer.reset();//重置为初始状态  
		}
		if (isPause){
			return ; 
		}
		try {
			mediaPlayer.reset();
			mediaPlayer.setDataSource(path);
			mediaPlayer.prepare();//缓冲   
			mediaPlayer.start();//开始或恢复播放
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}  
	}
}
