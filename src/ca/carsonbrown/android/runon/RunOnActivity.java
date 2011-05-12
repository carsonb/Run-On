package ca.carsonbrown.android.runon;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
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
	
	private static final String APP_ACTIVE = "app_active";
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
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
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
			mToggleActivateButton.setText(R.string.deactivate_app);
		} else {
			mSharedPrefs.edit().putBoolean(APP_ACTIVE, false).commit();
			mToggleActivateButton.setText(R.string.activate_app);
		}
		Log.v(TAG, "RunOn is now " + mSharedPrefs.getBoolean(APP_ACTIVE, false));
	}

}
