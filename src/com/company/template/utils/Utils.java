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
package com.company.template.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.company.template.common.Const;
import com.company.template.common.TemplateApplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


/*
 * Author : Nguyen Tien Long
 */

/*
 * Utilities class
 */

public class Utils {
	private static final Utils instance = new Utils();
	public static Utils getInstance() {
		return instance;
	}
	public void createAlertDialog(final Context mC, String title, String message) {
		try {
			AlertDialog.Builder builder = new AlertDialog.Builder(mC)
					.setMessage(message).setNeutralButton("OK", null);
			if (title != null)
				builder.setTitle(title);
			builder.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean checkNetworkStatus() {
		final ConnectivityManager conMgr = (ConnectivityManager) TemplateApplication
				.appContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
		if (activeNetwork != null
				&& activeNetwork.getState() == NetworkInfo.State.CONNECTED) {
			return true;
		}
		return false;
	}

	/*
	 * For saving preference value
	 */
	public static void saveParam(Context context, String value, String key) {
		SharedPreferences.Editor prefs = context.getSharedPreferences(
				Const.PKG_NAME + ".pref", Context.MODE_PRIVATE).edit();
		prefs.putString(key, value);
		prefs.commit();
	}
	/*
	 * For saving preference value
	 */
	public static String loadParam(Context context, String key) {
		SharedPreferences prefs = context.getSharedPreferences(Const.PKG_NAME
				+ ".pref", Context.MODE_PRIVATE);
		String value = prefs.getString(key, null);
		return value;
	}
	/*
	 * For clearing preference value
	 */
	public static boolean clearParam(Context context, String key) {
		SharedPreferences.Editor prefs = context.getSharedPreferences(
				Const.PKG_NAME + ".pref", Context.MODE_PRIVATE).edit();
		prefs.remove(key);
		return prefs.commit();
	}
	/*
	 * Getting image from url
	 */
	public static Drawable fetchImage(String url) {
		try {
			URL _url = new URL(url);
			InputStream is = (InputStream) _url.getContent();
			Drawable d = Drawable.createFromStream(is, "src");
			return d;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static String getDateTimeDefaultSave() 
	{
		SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd");
		Date now = new Date();
		String dateTime = sdfdate.format(now);
		return dateTime;
	}

}
