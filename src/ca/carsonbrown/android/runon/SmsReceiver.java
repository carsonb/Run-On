/**
 * 
 */
package ca.carsonbrown.android.runon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.telephony.SmsMessage;

/**
 * @author Carson Brown carson@carsonbrown.ca
 *
 */
public class SmsReceiver extends BroadcastReceiver {

private static final String TAG = "SmsReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		SmsMessage[] messages = null;
		if (bundle != null) {
			// Retrieve the SMS message received
			Object[] pdus = (Object[]) bundle.get("pdus");
			messages = new SmsMessage[pdus.length];
			for (int i = 0; i < messages.length; i++) {
				messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
				//is Run On enabled?
				if (getSharedPreferences(context).getBoolean("enable", false)) {
					//send to the SpeakSmsService
					Intent speakIntent = new Intent(context, SpeakSmsService.class);
					speakIntent.putExtra("originatingAddress", messages[i].getOriginatingAddress());
					speakIntent.putExtra("messageBody", messages[i].getMessageBody());
					context.startService(speakIntent);
				}
			}
		}
	}
	
	private SharedPreferences getSharedPreferences(Context context) {
		String packageName = 
		context.getPackageName();
		Resources res = context.getResources();
		String entryName = res.getResourceEntryName(R.xml.preferences);
		return context.getSharedPreferences(packageName+"_"+entryName, 0);
	}

}
