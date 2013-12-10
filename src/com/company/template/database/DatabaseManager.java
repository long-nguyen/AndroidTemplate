package com.company.template.database;

import com.company.template.common.TemplateApplication;

public class DatabaseManager {
	private static final String TAG=DatabaseManager.class.getSimpleName();
	private static final boolean DEBUG=false;

	private DatabaseHelper mHelper = null;
	private static volatile DatabaseManager _instance;

	public synchronized final static DatabaseManager instance() {
		if (null == _instance) {
			_instance = new DatabaseManager();
		}
		return _instance;
	}
	
	public DatabaseManager(){
		mHelper = new DatabaseHelper(TemplateApplication.getInstance());
	}
	
	public  void deleteDatabase() {
		TemplateApplication.getInstance().deleteDatabase(DatabaseHelper.DB_NAME);
	}

	
}
