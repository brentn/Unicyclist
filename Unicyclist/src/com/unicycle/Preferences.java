package com.unicycle;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;

public class Preferences extends PreferenceActivity {
	 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
 
        
        super.onCreate(savedInstanceState);
 
        //add the prefernces.xml layout
        addPreferencesFromResource(R.xml.preferences);
//        setContentView(R.layout.preferences);
 
         
        //get the specified preferences using the key declared in preferences.xml
        CheckBoxPreference miles = (CheckBoxPreference) findPreference("miles");
        ListPreference distFilter = (ListPreference) findPreference("distance_filter");
        Preference backup = (Preference) findPreference("backup");
        Preference restore = (Preference) findPreference("restore");
 
        distFilter.setSummary(distFilter.getEntry());
 
        distFilter.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                preference.setSummary(((ListPreference) o).getEntry());
                return true;
            };
        });
        
        miles.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
        	@Override
        	public boolean onPreferenceChange(Preference preference, Object o) {
        		return true;
        	};
        });
        
        backup.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				DatabaseHandler db = new DatabaseHandler(preference.getContext());
				db.backup();
				return true;
			}
		});

        restore.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				DatabaseHandler db = new DatabaseHandler(preference.getContext());
				return db.restore();
			}
		});

 
    }
    
} 