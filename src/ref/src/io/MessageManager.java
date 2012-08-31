package ref.src.io;

import ref.src.logic.TemplateApplication;
import android.widget.Toast;

/*
 * Author: Nguyen Tien Long
 */

/*
 * Abstract class for handling API message,
 *  it analyze  message and decide which function to call
 *  Class which extends this class must implement it own success function( for example: update data)
 */


public abstract class MessageManager
{
	/*
	 * Analyze message and show result
	 */
	public void analyzeMessage(String message)
	{
		if(message.contains(TemplateApi.ERROR_MSG_TOKENEXPIRE)) onTokenExpire();
		else if(message.contains(TemplateApi.ERROR_MSG_NETWORK)) onNetworkError();
		else if(message.contains(TemplateApi.ERROR_MSG_DATA)) onDataError();
	}
	/*
	 * Show network error message
	 */
	public void onNetworkError()
	{
		Toast.makeText(TemplateApplication.appContext()	, "Network error!", Toast.LENGTH_SHORT).show();
	}
	/*
	 * Show data error message
	 */
	public void onDataError()
	{
		Toast.makeText(TemplateApplication.appContext()	, "Data error!", Toast.LENGTH_SHORT).show();
	}
	/*
	 * Show token expire error message
	 */
	public abstract void onTokenExpire();
	public abstract void onSuccess();
}