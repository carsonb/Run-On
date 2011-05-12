/**
 * 
 */
package ca.carsonbrown.android.runon;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.util.Log;

/**
 * @author Carson Brown carson@carsonbrown.ca
 * 
 */

//TODO move to PreferenceFragment for forwards compatibility
public class Settings extends PreferenceActivity implements OnPreferenceClickListener {
	private static final String TAG = "Settings";
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.preferences);
		Preference testSpeech = (Preference) findPreference("testSpeech");
		testSpeech.setOnPreferenceClickListener(this);
	}

//	@Override
//	public boolean onPreferenceClick(Preference preference) {
//		TtsProviderFactory ttsProviderImpl = TtsProviderFactory.getInstance();
//		if (ttsProviderImpl != null) {
//		    ttsProviderImpl.init(getApplicationContext());
//		    ttsProviderImpl.say("New text message from ");
//		} else {
//			Log.v(TAG, "Impl was null");
//		}
//		return true;
//	}
	
	@Override
	public boolean onPreferenceClick(Preference preference) {
		Intent speakIntent = new Intent(getApplicationContext(), SpeakSmsService.class);
		speakIntent.putExtra("originatingAddress", "5551234");
		speakIntent.putExtra("messageBody", "This is a test.");
		getApplicationContext().startService(speakIntent);
		return true;
	}
}
