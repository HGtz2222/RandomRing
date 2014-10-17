package com.tz.randomring;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

public class RingInfo {

	public ArrayList<HashMap<String, Object>> getData(Context context) {
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		Cursor cursor = context.getContentResolver().query(MyContentProvider.CONTENT_URI, 
				new String[]{"title", "data"}, null, null, null);
		if (cursor == null){
			Log.e("tz", "cursor null");
			return list;
		}
		if (cursor.moveToFirst()){
			do{
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("title", cursor.getString(0));
				map.put("data", cursor.getString(1));
				list.add(map);
			}while(cursor.moveToNext());
		}
		return list;
	}

	public void setData(Context context, ArrayList<HashMap<String, Object>> data) {
		// 先清空数据库; 
		// context.getContentResolver().delete(MyContentProvider.CONTENT_URI, null, null);
		// 再插入数据库; 
		for (int i = 0; i < data.size(); ++i){
			HashMap<String, Object> map = data.get(i);
			Boolean isSelected = (Boolean)map.get("isSelected");
			if (isSelected != null && !isSelected){
				continue;
			}
			ContentValues contentValues = new ContentValues();
			contentValues.put(MyContentProvider.TITLE, (String)map.get("title"));
			contentValues.put(MyContentProvider.DATA, (String)map.get("data"));
			context.getContentResolver().insert(MyContentProvider.CONTENT_URI, contentValues);
		}
	}

	public ArrayList<HashMap<String, Object>> getAllData(Context context) {
		Log.i("tz", "getAllData start");
		// 1. 枚举出所有已经选择的铃声; 
		ArrayList<HashMap<String, Object>> selectedList = getData(context);
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		ContentResolver cr = context.getContentResolver();
		// 2. 枚举出所有sd卡中的铃声; 
		Cursor cursor_sd = cr.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Audio.Media._ID,
                        MediaStore.Audio.Media.DATA,
                        MediaStore.Audio.Media.TITLE }, null,
                        null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
		if (cursor_sd == null){
			return null; 
		}
		while (cursor_sd.moveToNext()) {
            Log.e("tz", cursor_sd.getString(0) + ", " + cursor_sd.getString(1) + ", " + cursor_sd.getString(2));
            if (cursor_sd.getString(2).equals("sdcard")){
            	continue;
            }
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("id", cursor_sd.getString(0));
            map.put("data", cursor_sd.getString(1));
            map.put("title", cursor_sd.getString(2));
            map.put("isSelected", isSelected(cursor_sd.getString(1), selectedList)); // 根据保存结果, 来确定这一项是否为false
            list.add(map);
        }
		cursor_sd.close();
		// 3. 枚举出所有的媒体库中的铃声;
		Cursor cursor = cr.query(MediaStore.Audio.Media.INTERNAL_CONTENT_URI,
                new String[] { MediaStore.Audio.Media._ID,
                        MediaStore.Audio.Media.DATA,
                        MediaStore.Audio.Media.TITLE }, "is_ringtone != ?",
                new String[] { "0" }, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
		if (cursor == null){
			return null; 
		}
		while (cursor.moveToNext()) {
            //Log.e("tz", cursor.getString(0) + ", " + cursor.getString(1) + ", " + cursor.getString(2));
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("id", cursor.getString(0));
            map.put("data", cursor.getString(1));
            map.put("title", cursor.getString(2));
            map.put("isSelected", isSelected(cursor.getString(1), selectedList)); // 根据保存结果, 来确定这一项是否为false
            list.add(map);
        }
		cursor.close();
		
		Log.i("tz", "getAllData finish");
		return list;
	}

	private Boolean isSelected(String curRing, ArrayList<HashMap<String, Object>> selectedList) {
		for(int i = 0; i < selectedList.size(); ++i){
			HashMap<String, Object> map = selectedList.get(i);
			String tmpRing = (String)map.get("data");
			if (tmpRing.equals(curRing)){
				return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}

	public void insert(Context context, HashMap<String, Object> map) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(MyContentProvider.TITLE, (String)map.get("title"));
		contentValues.put(MyContentProvider.DATA, (String)map.get("data"));
		context.getContentResolver().insert(MyContentProvider.CONTENT_URI, contentValues);
	}

	public void remove(Context context, HashMap<String, Object> map) {
		String ringData = (String)map.get("data");
		context.getContentResolver().delete(MyContentProvider.CONTENT_URI, null, new String[]{ringData});
	}

	public void setAllData(Context context) {
		// 1. 获取出所有已经选择好的铃声; 
		ArrayList<HashMap<String, Object>> selectedList = getData(context);
		// 2. 获取出所有可供选择的铃声; 
		ArrayList<HashMap<String, Object>> allList = getAllData(context);
		// 3. 将所有未选择的铃声, 插入到数据库中; 
		for (int i = 0; i < allList.size(); ++i){
			HashMap<String, Object> map = allList.get(i);
			if (isSelected((String)map.get("data"), selectedList)){
				continue;
			}
			insert(context, map);
		}
		return ; 
	}

	public void removeAllData(Context context) {
		context.getContentResolver().delete(MyContentProvider.CONTENT_URI, MyContentProvider.CLEAR_ALL_DATA, new String[]{"*"});
	}

}
