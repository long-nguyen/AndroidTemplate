/**
 * <Class purpose>
 * 
 * @author <author>, 
 * http://activeuser.co
 * ========================================================================
 * Copyright 2015 Active User Co.Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
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
