package il.ac.shenkar.friendlylivetranslator_5;

import java.util.ArrayList;

import com.google.android.gcm.GCMRegistrar;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class Utils 
{
	public static String USER_NAME = "";
	public static String[] removeUsernameFromArray(String[] user)
	{
		ArrayList<String> userList = new ArrayList<String>();
		for(int i=0;i<user.length;i++){
			Log.d("i="+i, user[i]);
			if(USER_NAME.equals(user[i])==false)
			{
				userList.add(user[i]);
			}
		}
		String returnArray[] = new String[userList.size()];
		for(int i=0;i<returnArray.length;i++)
		{
			returnArray[i] = userList.get(i).toString();
		}
		return returnArray;
	}
	
	public static void showToast(Context ctx,String message)
	{
		Toast.makeText(ctx, message, Toast.LENGTH_LONG).show();
	}
	
	/**
	 * This function is used to register Android device on App42 to integrate
	 * PushNotification
	 **/
	public static void registerWithApp42(String projectNo, Context context) 
	{
		GCMIntentService.setProjectNO(projectNo);
		final String deviceRegId = GCMRegistrar.getRegistrationId(context);
		if (deviceRegId.equals("")) 
		{
			GCMRegistrar.register(context, projectNo);

		} 
		else if (!GCMRegistrar.isRegisteredOnServer(context)) 
		{
			((App42PhonegapPush) context).registerUser(USER_NAME);
			((App42PhonegapPush) context).registerForApp42Push(deviceRegId);
			((App42PhonegapPush) context).push();
		}
	}
}