package ca.carsonbrown.android.runon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		initPrefs();
		
		mToggleActivateButton = (Button) findViewById(R.id.toggle_activate_button);
		mToggleActivateButton.setOnClickListener(this);
		
		TtsProviderFactory ttsProviderImpl = TtsProviderFactory.getInstance();
		if (ttsProviderImpl != null) {
		    ttsProviderImpl.init(getApplicationContext());
		}
		
	}
	
	//initialize preferences if they are not already set up
	private void initPrefs() {
		if (!mSharedPrefs.contains(APP_ACTIVE)) {
			//create the preference store
			mSharedPrefs.edit().putBoolean(APP_ACTIVE, false).commit();
			//TODO add shared prefs from settings activity
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

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		setToggleButtonText();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
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
