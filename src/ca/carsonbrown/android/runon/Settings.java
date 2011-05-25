/**
 * 
 */
package ca.carsonbrown.android.runon;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;

/**
 * @author Carson Brown carson@carsonbrown.ca
 * 
 */

//TODO move to PreferenceFragment for forwards compatibility
public class Settings extends PreferenceActivity implements OnPreferenceClickListener {
	private static final String TAG = "Settings";
	
	Preference mTestSpeech;
	Preference mTtsSettings;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.preferences);
		mTestSpeech = (Preference) findPreference("test_speech");
		mTestSpeech.setOnPreferenceClickListener(this);
		mTtsSettings = (Preference) findPreference("tts_settings");
		mTtsSettings.setOnPreferenceClickListener(this);
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
		if (preference.equals(mTestSpeech)) {
			Intent speakIntent = new Intent(this, SpeakSmsService.class);
			speakIntent.putExtra("originatingAddress", "5551234");
			speakIntent.putExtra("messageBody", "This is a test.");
			getApplicationContext().startService(speakIntent);
		} else if (preference.equals(mTtsSettings)) {
			ComponentName componentToLaunch = new ComponentName(
			        "com.android.settings",
			        "com.android.settings.TextToSpeechSettings");
			Intent intent = new Intent();
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			intent.setComponent(componentToLaunch);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		}
		return true;
	}
}
