package ca.carsonbrown.android.runon;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * @author Carson Brown carson@carsonbrown.ca
 *
 */
public class RunOnActivity extends Activity implements OnClickListener {

	
	private Button mToggleActivateButton;
	private SharedPreferences mSharedPrefs;
	
	private static final String APP_ACTIVE = "enable";
	private static final String TAG = "RunOnActivity";
	private static final int MENU_SETTINGS = 1;
	private static final int MENU_ABOUT = 2;
	
	private static final int MY_DATA_CHECK_CODE = 23561263;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		initPrefs();
		
		mToggleActivateButton = (Button) findViewById(R.id.toggle_activate_button);
		mToggleActivateButton.setOnClickListener(this);
		
		Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);
		
	}
	
	/**
     * This is the callback from the TTS engine check, if a TTS is installed we
     * create a new TTS instance (which in turn calls onInit), if not then we will
     * create an intent to go off and install a TTS engine
     * @param requestCode int Request code returned from the check for TTS engine.
     * @param resultCode int Result code returned from the check for TTS engine.
     * @param data Intent Intent returned from the TTS check.
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == MY_DATA_CHECK_CODE)
        {
            if (resultCode != TextToSpeech.Engine.CHECK_VOICE_DATA_PASS)
            {
                // missing data, install it
            	//TODO Tell the user that we're going to the Market to get a TTS engine
                Intent installIntent = new Intent();
                installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);
            }
        }
    }
	
	//initialize preferences if they are not already set up
	private void initPrefs() {
		if (!mSharedPrefs.contains(APP_ACTIVE)) {
			//create the preference store
			mSharedPrefs.edit().putBoolean(APP_ACTIVE, false).commit();
			//TODO add shared prefs from settings activity?
			//mSharedPrefs.edit().commit();
		}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_SETTINGS, 0, R.string.menu_settings).setIcon(R.drawable.ic_menu_preferences).setShortcut('7', 's');
		menu.add(0, MENU_ABOUT, 0, R.string.menu_about).setIcon(R.drawable.ic_menu_info_details).setShortcut('2', 'a');
		return true;
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_SETTINGS:
			Intent settingsIntent = new Intent(getApplicationContext(), Settings.class);
			startActivityForResult(settingsIntent, 0);
			return true;
		case MENU_ABOUT:
			//TODO open the about activity
			return true;
		}
		return false;
	}

	@Override
	protected void onResume() {
		super.onResume();
		setToggleButtonText();
	}

	@Override
	public void onClick(View v) {
		if (v.equals((View)mToggleActivateButton)) {
			//toggle Run On on/off
			toggleApp();
		}
	}
	
	private void toggleApp() {
		if (!mSharedPrefs.getBoolean(APP_ACTIVE, false)) {
			mSharedPrefs.edit().putBoolean(APP_ACTIVE, true).commit();
		} else {
			mSharedPrefs.edit().putBoolean(APP_ACTIVE, false).commit();
		}
		setToggleButtonText();
		Log.v(TAG, "RunOn is now " + mSharedPrefs.getBoolean(APP_ACTIVE, false));
	}
	
	private void setToggleButtonText() {
		if (mSharedPrefs.getBoolean(APP_ACTIVE, false)) {
			mToggleActivateButton.setText(R.string.deactivate_app);
		} else {
			mToggleActivateButton.setText(R.string.activate_app);
		}
	}

}
