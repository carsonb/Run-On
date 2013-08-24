package ca.carsonbrown.android.runon;

import android.app.backup.BackupAgentHelper;
import android.app.backup.SharedPreferencesBackupHelper;

/**
 * Created by carson on 2013-08-24.
 */
public class BackupAgent extends BackupAgentHelper {

    static final String PREFS_DEFAULT = "ca.carsonbrown.android.runon_preferences";

    static final String PREFS_BACKUP_KEY = "preferences_backup_key";

    @Override
    public void onCreate() {
        SharedPreferencesBackupHelper helper = new SharedPreferencesBackupHelper(this, PREFS_DEFAULT);
        addHelper(PREFS_BACKUP_KEY, helper);
    }
}
