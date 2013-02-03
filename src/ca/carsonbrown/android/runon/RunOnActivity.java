package ca.carsonbrown.android.runon;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * @author Carson Brown carson@carsonbrown.ca
 *
 */
public class RunOnActivity extends Activity implements OnClickListener {

	
	private ToggleButton mToggleActivateButton;
	private SharedPreferences mSharedPrefs;
    private TextToSpeech mTts;

	private static final String TAG = "RunOnActivity";
	private static final int MENU_SETTINGS = 1;
	private static final int MENU_ABOUT = 2;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		//Make gradients look less banded
		getWindow().setFormat(PixelFormat.RGBA_8888);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DITHER);
		
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
        
        findViewById(R.id.main_layout).setBackgroundDrawable(p); 
		
		mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		initPrefs();
		
		mToggleActivateButton = (ToggleButton) findViewById(R.id.toggle_activate_button);
		mToggleActivateButton.setOnClickListener(this);


        //check if the current tts lang requires a download
		checkTTSLang();
	}

    private void checkTTSLang() {
        mTts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                boolean success = false;
                if (status == TextToSpeech.SUCCESS) {
                    Locale locale = Locale.getDefault();
                    switch (mTts.isLanguageAvailable(locale)) {
                        case TextToSpeech.LANG_AVAILABLE:
                        case TextToSpeech.LANG_COUNTRY_AVAILABLE:
                        case TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE:
                            success = true;
                            break;
                        case TextToSpeech.LANG_MISSING_DATA:
                        case TextToSpeech.LANG_NOT_SUPPORTED:
                        default:
                            success = false;
                            break;
                    }
                }
                //If we can use the default locale, awesome, otherwise, we'll just use US english
                Log.v(TAG, "Checking if default locale works for TTS: " + (success ? "YES" : "NO"));
                mSharedPrefs.edit().putBoolean(getString(R.string.default_locale_key), success);
                mSharedPrefs.edit().commit();

                mTts.stop();
                mTts.shutdown();
            }
        });
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
		case R.id.menu_about:
			// Build a dialog, design borrowed from Transdroid
            AlertDialog.Builder changesDialog = new AlertDialog.Builder(this, android.R.style.Theme_Holo_Dialog);
            changesDialog.setTitle(R.string.about_title);
            View changes = getLayoutInflater().inflate(R.layout.about, null);
            ((TextView)changes.findViewById(R.id.runon_version)).setText("Run On " + getVersionNumber(this));
            changesDialog.setView(changes);
            changesDialog.create();
            changesDialog.show();
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
