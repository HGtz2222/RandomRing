package com.tz.randomring;

import java.io.File;

import android.content.ContentValues;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.MediaStore;

public class Ring {
	public static void set(Context context, String path){
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
		Uri newUri = context.getContentResolver().insert(uri, values);
		RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE, newUri);
	}
}
