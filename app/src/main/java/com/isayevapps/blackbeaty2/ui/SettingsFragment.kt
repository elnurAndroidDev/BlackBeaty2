package com.isayevapps.blackbeaty2.ui

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.isayevapps.blackbeaty2.R

class SettingsFragment: PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }
}