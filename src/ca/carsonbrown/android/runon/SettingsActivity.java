/**
 * 
 */
package ca.carsonbrown.android.runon;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.MenuItem;

/**
 * @author Carson Brown carson@carsonbrown.ca
 * 
 */
public class SettingsActivity extends PreferenceActivity {
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
        getActionBar().setDisplayHomeAsUpEnabled(true);
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return false;
    }

}
