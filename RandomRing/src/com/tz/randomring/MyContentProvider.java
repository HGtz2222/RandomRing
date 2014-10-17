package com.tz.randomring;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

public class MyContentProvider extends ContentProvider{
	public static final Uri CONTENT_URI = Uri.parse("content://com.tz.randomring");
	
	private final static String DATABASE_NAME = "Rings.db";
	private final static int DATABASE_VERSION = 1;
	private final static String TABLE_NAME = "Rings";
	
	public final static String TITLE = "TITLE";
	public final static String DATA = "DATA";
	public final static String ID = "_id";
	public final static String CLEAR_ALL_DATA = "clearAllData";
	
	private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("create table " + TABLE_NAME + "( _id INTEGER PRIMARY KEY AUTOINCREMENT, TITLE TEXT, DATA TEXT);");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
			Log.e("tz", "onUpgrade");
			db.execSQL("drop table if exists " + TABLE_NAME);
			onCreate(db);
		}
	}

	private DatabaseHelper dbHelper;
	private SQLiteDatabase sqlDB;
	
	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		Log.e("tz", "MyContentProvider delete " + whereArgs[0]);
		sqlDB = dbHelper.getWritableDatabase();
		if (where != null && where.equals(CLEAR_ALL_DATA)){
			sqlDB.delete(TABLE_NAME, null, null);
		}else{
			sqlDB.delete(TABLE_NAME, DATA + "=?", whereArgs);	
		}
		return 0;
	}

	@Override
	public String getType(Uri arg0) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues contentValues) {
		Log.e("tz", "MyContentProvider insert " + contentValues.getAsString(MyContentProvider.DATA));
		sqlDB = dbHelper.getWritableDatabase();
		long row = sqlDB.insert(TABLE_NAME, "", contentValues);
		if (row > 0){
			Uri rowUri = ContentUris.appendId(CONTENT_URI.buildUpon(), row).build();
			getContext().getContentResolver().notifyChange(rowUri, null);
			return rowUri;
		}
		throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public boolean onCreate() {
		dbHelper = new DatabaseHelper(getContext());
        return (dbHelper == null) ? false : true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
			String sortOrder) {
		Log.e("tz", "MyContentProvider query");
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		qb.setTables(TABLE_NAME);
		Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) {
		Log.e("tz", "MyContentProvider update");
		return 0;
	}

}
