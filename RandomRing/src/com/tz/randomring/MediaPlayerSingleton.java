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
			play(path, true); // ��ͣ����; 
			MediaPlayerSingleton.lastView = null;
		}else{
			play(path, false);  // ����������; 
			MediaPlayerSingleton.lastView = view;
		}
	}
	
	public static void playRing(String path, View view, ImageButton btnPlaying) {
		if (view.equals(MediaPlayerSingleton.lastView)){
			play(path, true); // ��ͣ����; 
			MediaPlayerSingleton.lastView = null;
		}else{
			play(path, false);  // ����������; 
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
		   mediaPlayer.reset();//����Ϊ��ʼ״̬  
		}
		if (isPause){
			return ; 
		}
		try {
			mediaPlayer.reset();
			mediaPlayer.setDataSource(path);
			mediaPlayer.prepare();//����   
			mediaPlayer.start();//��ʼ��ָ�����
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
