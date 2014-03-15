package ca.carsonbrown.android.runon;

import android.app.Activity;
import android.app.backup.BackupManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.LinearGradient;
import android.graphics.PixelFormat;
import android.graphics.Shader;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ToggleButton;

/**
 * @author Carson Brown carson@carsonbrown.ca
 *
 */
public class RunOnActivity extends Activity implements OnClickListener {

	
	private ToggleButton mToggleActivateButton;
	private SharedPreferences mSharedPrefs;

	private static final String TAG = "RunOnActivity";
	private static final int MENU_SETTINGS = 1;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		//Make gradients look less banded
		getWindow().setFormat(PixelFormat.RGBA_8888);
		
		ShapeDrawable.ShaderFactory sf = new ShapeDrawable.ShaderFactory() {
            @Override
            public Shader resize(int width, int height) {
                return new LinearGradient(0, 0, 0, height,
                    new int[]{0xff999999, 0xff666666, 0xff333333},
                    new float[]{0.0f, 0.67f, 1.0f}, Shader.TileMode.REPEAT);
            }
        };

        PaintDrawable p = new PaintDrawable();
        p.setShape(new RectShape());
        p.setShaderFactory(sf);

        findViewById(R.id.main_layout).setBackground(p);
		
		mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		initPrefs();
		
		mToggleActivateButton = (ToggleButton) findViewById(R.id.toggle_activate_button);
		mToggleActivateButton.setOnClickListener(this);
    }
	
	//initialize preferences if they are not already set up
	private void initPrefs() {
		if (!mSharedPrefs.contains(getString(R.string.enable_key))) {
			//create the preference store
			mSharedPrefs.edit().putBoolean(getString(R.string.enable_key), true).commit();
			mSharedPrefs.edit().commit();
		}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu, menu);
	    return true;
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_settings:
			Intent settingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);
			startActivityForResult(settingsIntent, 0);
			return true;
		}
		return false;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		//update button based on changed preference state
		mToggleActivateButton.setChecked(mSharedPrefs.getBoolean(getString(R.string.enable_key), true));
	}

	@Override
	public void onClick(View v) {
		if (v.equals((View)mToggleActivateButton)) {
			//toggle Run On on/off
			toggleApp();
		}
	}
	
	private void toggleApp() {
		if (mToggleActivateButton.isChecked()) {
			mSharedPrefs.edit().putBoolean(getString(R.string.enable_key), true).commit();
		} else {
			mSharedPrefs.edit().putBoolean(getString(R.string.enable_key), false).commit();
		}
		Log.v(TAG, "RunOn is now " + mSharedPrefs.getBoolean(getString(R.string.enable_key), false));

        BackupManager bm = new BackupManager(this);
        bm.dataChanged();
	}
	
	public static String getVersionNumber(Context context) {
        String version = "?";
        try {
                PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                version = pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
                Log.e(TAG, "Package name not found to retrieve version number", e);
        }
        return version;
}

}
