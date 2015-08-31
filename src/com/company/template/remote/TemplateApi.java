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
package com.company.template.remote;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.company.template.remote.HttpRequester.ExecutableRequest;
import com.company.template.remote.HttpRequester.HttpRequestListener;
import com.company.template.remote.HttpRequester.HttpResponseHolder;

/*
 * Author: Long Nguyen Tien
 */

/*
 * How to use: 
 * This class is bult to provide API functions, 
 * it checking the message response and decide the notification to sender,
 * notify sender by using message handler.
 */
public class TemplateApi
{
	public static final String TAG=TemplateApi.class.getSimpleName();
	/* Server base */
	public static final String TEST_SERVER = "";
	public static final String STAGING_SERVER = "";
	public static final String RELEASE_SERVER = "";
	public static final String USE_SERVER = TEST_SERVER;
	/* API base url */
	public static final String API_URL_ABC = "/api/abc";
	public static final String API_URL_XYZ = "/api/xyz";
	/* basic AUTH */
	public static final String BASIC_AUTH_USER = "";
	public static final String BASIC_AUTH_PASS = "";
	/* Error code */
	public static final String	ERROR_MSG_TOKENEXPIRE	= "api_token_expire";
	public static final String	ERROR_MSG_NETWORK= "api_error_network";
	public static final String	ERROR_MSG_DATA= "api_error_data";
	/*Message */
	public static final String	API_MESSAGE= "api_message";
	public static final int		API_TYPE_ABC= 0x001;
	public static final int		API_TYPE_XYZ= 0x002;

	
	
	public static void templateApi(final Handler handler,String para1,String para2)
	{
		/*
		 * Based on situation of API, send result message to caller
		 */
		try {
			//Select POST or GET 
			ExecutableRequest er = HttpRequester.newRequest(
					HttpRequester.REQUEST_POST, USE_SERVER 
					+ API_URL_ABC);
			er.setBasicAuthHeader(BASIC_AUTH_USER, BASIC_AUTH_PASS);
			er.addParam("para1", para1);
			if(para2!=null) er.addParam("para2", para2);
			Log.d(TAG,"--------> request :"+er.getURI().toString());
			er.execute(new HttpRequestListener() {
				@Override
				protected void onRequestComplete(HttpResponseHolder responseHolder) 
				{
					super.onRequestComplete(responseHolder);
					if(responseHolder==null||responseHolder.responseBody==null) return;
					Log.d(TAG,"------------->response : "+responseHolder.toString());
					JSONObject json = null;
					Bundle data=new Bundle();
					Message m=new Message();
					m.what=API_TYPE_ABC;			//API type
					try {
						json = new JSONObject(responseHolder.responseBody);
					}catch (JSONException e) 
					{
						e.printStackTrace();
						data.putString(API_MESSAGE, ERROR_MSG_NETWORK);			//Message type(message content)
						m.setData(data);
						handler.sendMessage(m);
						return;
					}
					if (responseHolder.returnCode != 200) 
					{
						if(checkTokenExpire(json))
						{
							data.putString(API_MESSAGE, ERROR_MSG_TOKENEXPIRE);
						}else{
							data.putString(API_MESSAGE, ERROR_MSG_DATA+ getError(json));
						}
						m.setData(data);
						handler.sendMessage(m);
						return;
					}
					data.putString(API_MESSAGE, json.toString());
					m.setData(data);
					handler.sendMessage(m);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private static boolean checkTokenExpire(JSONObject json)
	{
		/*
		 * This function is for checking token expire, based on Token expire code
		 */
//		try
//		{
//			JSONArray content=json.getJSONArray("content");
//			for(int i=0;i<content.length();i++)
//			{
//				JSONObject err=content.getJSONObject(i);
//				int errorCode=err.getInt("error_code");
//				String errorM=err.optString("error_message");
//				if(ERROR_EXPIRE==errorCode||"tokenが期限切れです".equals(errorM)){
//					return true;
//				}
//			}
//
//		} catch (JSONException e)
//		{
//			e.printStackTrace();
//		}
		return false;
	}
	private static String getError(JSONObject json)
	{
		/*
		 * This function is for getting specific error received
		 */
//		String error=ERROR_VARIABLE+" : ";
//		try
//		{
//			JSONArray content=json.getJSONArray("content");
//			for(int i=0;i<content.length();i++)
//			{
//				JSONObject err=content.getJSONObject(i);
//				error=error+err.getString("error_message");
//				//Log.d(TAG,"error_message "+err.getString("error_message"));
//			}
//		} catch (JSONException e)
//		{
//			e.printStackTrace();
//		}
		return null;
	}
}
