package ddnet.mobile;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity {
    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        if (!getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            CheckBoxPreference preference = (CheckBoxPreference)findPreference(getString(R.string.useFlashPreferenceKey));
            preference.setEnabled(false);
            preference.setChecked(false);
        }

        if (!getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_AUTOFOCUS)) {
            CheckBoxPreference preference = (CheckBoxPreference)findPreference(getString(R.string.autoFocusPreferenceKey));
            preference.setEnabled(false);
            preference.setChecked(false);
        }
    }
}
