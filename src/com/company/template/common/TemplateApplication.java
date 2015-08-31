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
package com.company.template.common;
import android.app.Application;
import android.content.Context;


/*
 * How to use: 
 * This class is Application class, which acts like a manager of every actions.
 * > It provide context for every tasks that need context,
 * except the display dialog or message that require activity context.
 * > You should keep controlling every common object of your application here (for accessing every where)
 * for example: user data, google analytic
 * > should destroy data when finish
 */

public class TemplateApplication extends Application
{
	public static final String TAG=TemplateApplication.class.getSimpleName();
	private static TemplateApplication _instance=null; 
	public static Context appContext()
	{
		if(_instance == null)
			throw new IllegalStateException("Application not created yet!");
		return _instance.getApplicationContext();
	}
	public static TemplateApplication getInstance()
	{
		if(_instance == null)
			throw new IllegalStateException("Application not created yet!");
		return _instance;
	}
	@Override
	public void onCreate() 
	{
		super.onCreate();
		_instance = this;
	}
	
}
