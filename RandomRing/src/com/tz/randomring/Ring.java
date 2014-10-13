package com.tz.randomring;

import java.io.File;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

public class Ring {
	private static int cnt = 0;
	
	public static void setAndPutMedia(Context context, String path){
		File sdfile = new File(path);
		ContentValues values = new ContentValues();
		values.put(MediaStore.MediaColumns.DATA, sdfile.getAbsolutePath());
		values.put(MediaStore.MediaColumns.TITLE, sdfile.getName());
		values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/*");
		values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
		values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
		values.put(MediaStore.Audio.Media.IS_ALARM, false);
		values.put(MediaStore.Audio.Media.IS_MUSIC, false);
		
		Uri uri = MediaStore.Audio.Media.getContentUriForPath(sdfile.getAbsolutePath());
		Log.e("tz", "uri " + uri.toString());
		context.getContentResolver().delete(uri,  
				MediaStore.MediaColumns.DATA + "=\"" + sdfile.getAbsolutePath() + "\"",  
				null);  
		Uri newUri = context.getContentResolver().insert(uri, values);
		Log.e("tz", "newUri " + newUri.toString());
		RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE, newUri);
	}
	
	public static void test_scanMedia(Context context){
		ContentResolver cr = context.getContentResolver();
		Cursor cursor = cr.query(MediaStore.Audio.Media.INTERNAL_CONTENT_URI,
                new String[] { MediaStore.Audio.Media._ID,
                        MediaStore.Audio.Media.DATA,
                        MediaStore.Audio.Media.TITLE }, "is_ringtone != ?",
                new String[] { "0" }, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
		if (cursor == null){
			Log.e("tz", "cursor null");
			return ; 
		}
//		while (cursor.moveToNext()) {
//            Log.e("tz", cursor.getString(0) + ", " + cursor.getString(1) + ", " + cursor.getString(2));
//        }
		cursor.moveToFirst();
		
		for(int i = 0; i < cnt; ++i){
			cursor.moveToNext();
		}
		++cnt;
		Log.e("tz------------", cnt + ", " + cursor.getString(1));
//		RingtoneManager.setActualDefaultRingtoneUri(context,
//                RingtoneManager.TYPE_RINGTONE, Uri.parse(cursor.getString(1)));
		setAndPutMedia(context, cursor.getString(1));
//		Uri uri = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE);
//		Log.e("tz", "----> " + uri.getPath());
	}
}
