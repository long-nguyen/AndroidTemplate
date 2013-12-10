package com.company.template.database;

import ref.src.activity.BuildConfig;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
	private static final String TAG = DatabaseHelper.class.getSimpleName();
	
	/**		
	 * Database version.
	 * <p>Used to tell app how to upgrade database.<br>
	 * Change <b>BOTH this value, onUpgrade, onCreate function and versionCode in Manifest.xml</b>
	 * when you want to upgrade database after release.<br>
	 * * versionCode used to prevent user install with lower DB_VERSION by mistake.<br></p>
	 * <p>Version Message:<br>
	 * 4 : Old version.<br>
	 * 5 : Add TRAVEL_SECRET in TABLE_TRAVEL <--Latest</p>
	 * */
	public static final int DB_VERSION=1;
	
	/** Database name */
	public static final String DB_NAME = "template_db";
	/** Table list--------------------------------- */
	/** Map Data table */
	public static final String TABLE_TEMPLATE_DATA = "db_template_data";

	public DatabaseHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}
	
	@Override
	/**
	 * Table created here
	 */
	public void onCreate(SQLiteDatabase db) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "DB created");
		}
		createTable(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "DB upgrade from " + oldVersion + "to " + newVersion);
		}
		switch (oldVersion) {
		case 4:
			// Do upgrade if DB_VERSION changed.
		case 5:

			break;
		default:
			break;
		}
	}
	
	private void createTable(SQLiteDatabase db){
		// Create  data table
	}
}
