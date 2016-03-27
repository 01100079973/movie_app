package com.example.eman.movies;


import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * A fragment for the Settings Activity
 */
public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preference);
    }
}
