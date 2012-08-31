package ref.src.activity;

import ref.src.io.MessageManager;
import ref.src.io.TemplateApi;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

/*
 * Author: Nguyen Tien Long
 */

/*
 * Sample activity: It show the way you :
 * 	1. Push a button, calling an API method
 *  2. API send a notification back to handler
 *  3. Handler check what kind of API, call right function,
 *  4. Inside the function, must analyze the message and update data 
 */
public class TemplateActivity extends Activity {
    /** Called when the activity is first created. */
	private TextView mText;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mText=(TextView) findViewById(R.id.text_res);
        findViewById(R.id.bt_send).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v)
			{
				//Calling api from UI
				TemplateApi.templateApi(myViewUpdateHandler, "ABC", "123");
			}
		});
    }
 
    
    //---------------------------Api handler
	public  Handler myViewUpdateHandler=new Handler(){
		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
				case TemplateApi.API_TYPE_ABC:
					doSth(msg.getData());
					break;
				case TemplateApi.API_TYPE_XYZ:
					doSthElse(msg.getData());
					break;
				default:
					break;
			}
		}
	};
	/*
	 * First function: let the manager handle network error message  
	 */
	private void doSth(Bundle data)
	{
		String message=data.getString(TemplateApi.API_MESSAGE);
		MessageManager m=new MessageManager() {
			@Override
			public void onTokenExpire()
			{
			}
			@Override
			public void onSuccess()
			{
				mText.setText("Success");
			}
		};
		m.analyzeMessage(message);
	}
	/*
	 * Second function: If you want to do sth when network error, show it
	 */
	private void doSthElse(Bundle data)
	{
		String message=data.getString(TemplateApi.API_MESSAGE);
		MessageManager m=new MessageManager() 
		{
			/*
			 * self handling the onNetworkError message
			 */
			@Override
			public void onNetworkError()
			{
				mText.setVisibility(View.INVISIBLE);
			}
			@Override
			public void onTokenExpire()
			{
			}
			@Override
			public void onSuccess()
			{
				mText.setText("Success");
			}
		};
		m.analyzeMessage(message);
	}
}