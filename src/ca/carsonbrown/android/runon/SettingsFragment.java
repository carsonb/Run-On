/**
 * 
 */
package ca.carsonbrown.android.runon;

import java.util.Arrays;
import java.util.List;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;

/**
 * @author Carson Brown carson@carsonbrown.ca
 *
 */
public class SettingsFragment extends PreferenceFragment implements OnPreferenceClickListener, OnSharedPreferenceChangeListener {

	private static final String TAG = "Settings";
	
	private Preference mTestSpeech;
	private Preference mTtsSettings;
	private SharedPreferences mSharedPreferences;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.preferences);
		
		//Listeners for special actions
		mTestSpeech = (Preference) findPreference(getString(R.string.test_speech_key));
		mTestSpeech.setOnPreferenceClickListener(this);
		mTtsSettings = (Preference) findPreference(getString(R.string.tts_settings_key));
		mTtsSettings.setOnPreferenceClickListener(this);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		mSharedPreferences = getPreferenceScreen().getSharedPreferences();
		//So we can show changed description text when user changes preferences
		mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
		
		updateEnablePreference();
		updateRunPolicyPreference();
		updateRunPolicyPreference();
		updateSaySenderPreference();
		updateSayMessagePreference();
	}
	
	private void updateEnablePreference() {
		String key = getString(R.string.enable_key);
		Preference preference = findPreference(key);
		if (mSharedPreferences.getBoolean(key, false)) {
			preference.setSummary(R.string.run_on_enabled);
		} else {
			preference.setSummary(R.string.run_on_disabled);
		}
	}
	
	private void updateRunPolicyPreference() {
		String key = getString(R.string.run_policy_key);
		ListPreference preference = (ListPreference)findPreference(key);
		preference.setSummary(preference.getEntry());
	}
	
	private void updateSaySenderPreference() {
		String key = getString(R.string.say_sender_key);
		ListPreference preference = (ListPreference)findPreference(key);
		preference.setSummary(preference.getEntry());
	}
	
	private void updateSayMessagePreference() {
		String key = getString(R.string.say_message_key);
		Preference preference = findPreference(key);
		if (mSharedPreferences.getBoolean(key, false)) {
			preference.setSummary(R.string.say_message_enabled);
		} else {
			preference.setSummary(R.string.say_message_disabled);
		}
	}
	
	@Override
	public void onPause() {
	    super.onPause();
	    mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
	    mSharedPreferences = null;
	}
	
	@Override
	public boolean onPreferenceClick(Preference preference) {
		if (preference.equals(mTestSpeech)) {
			Intent speakIntent = new Intent(getActivity(), SpeakSmsService.class);
			speakIntent.putExtra("originatingAddress", getString(R.string.tts_test_number));
			speakIntent.putExtra("messageBody", getString(R.string.tts_test_message));
			getActivity().getApplicationContext().startService(speakIntent);
		} else if (preference.equals(mTtsSettings)) {
			Intent intent = new Intent();
            intent.setAction("com.android.settings.TTS_SETTINGS");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(intent);
		}
		return true;
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (key.equals(getString(R.string.enable_key))) {
			updateEnablePreference();
		} else if (key.equals(getString(R.string.run_policy_key))) {
			updateRunPolicyPreference();
		} else if (key.equals(getString(R.string.say_sender_key))) {
			updateSaySenderPreference();
		} else if (key.equals(getString(R.string.say_message_key))) {
			updateSayMessagePreference();
		}
	}
}
