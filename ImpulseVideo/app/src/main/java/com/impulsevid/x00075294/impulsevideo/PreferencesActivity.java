package com.impulsevid.x00075294.impulsevideo;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.util.Log;

/***************************************************************************************
 *    Title: PreferenceActivity
 *    Author: Google
 *    Date: 13/7/17
 *    Code version: 1.0
 *    Availability: https://developer.android.com/reference/android/preference/PreferenceActivity.html
 *
 ***************************************************************************************/
//Auto generated from activity creation
public class PreferencesActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();

    }

    public static class MyPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref);
            final SharedPreferences sharedPrefs = getActivity().getSharedPreferences(LaunchActivity.SHAREDPREFFILE, Context.MODE_PRIVATE);
            sharedPrefs.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

                }
            });
        }
        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen()
                    .getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen()
                    .getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
        }
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
            if (s.equals("data_switch")) {
                boolean test = sharedPreferences.getBoolean(s, false);
                Log.v("OOO", "Value:" + test);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("emails", "BOOYA");
                editor.putBoolean(s,test);
                editor.commit();
            }
        }
    }
}
