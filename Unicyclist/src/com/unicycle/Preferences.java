package com.unicycle;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

import android.os.Bundle;
import android.os.Environment;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.widget.Toast;

public class Preferences extends PreferenceActivity {
	
    private final String currentDBPath = "/data/com.unicycle/databases/";
    private final String backupDBPath = "/com.unicycle/backup/";

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
				Locations locations = new Locations(getApplicationContext());
				Tags tags = new Tags(getApplicationContext());
				Trails trails = new Trails(getApplicationContext());
				copyDatabaseToSD(locations.databaseName());
				copyDatabaseToSD(tags.databaseName());
				copyDatabaseToSD(trails.databaseName());
				Toast.makeText(getApplicationContext(), "Backup Complete", Toast.LENGTH_SHORT).show();
				return true;
			}
		});
        
        restore.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				boolean result;
				Locations locations = new Locations(getApplicationContext());
				Tags tags = new Tags(getApplicationContext());
				Trails trails = new Trails(getApplicationContext());
				result = (restoreDatabaseFromSD(locations.databaseName()) && 
						restoreDatabaseFromSD(tags.databaseName()) &&
						restoreDatabaseFromSD(trails.databaseName()));
    	        // Access the copied database so SQLiteHelper will cache it and mark
    	        // it as created.
 				locations.getWritableDatabase().close();
 				tags.getWritableDatabase().close();
 				trails.getWritableDatabase().close();
 				if (result) {
 					Toast.makeText(getApplicationContext(), "Restore Complete", Toast.LENGTH_SHORT).show();
 				} else {
 					Toast.makeText(getApplicationContext(), "Restore Failed!", Toast.LENGTH_LONG).show();
 				}
				return result;
			}
		});
    }

    private void copyDatabaseToSD(String dbname) {
    	try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                File backupPath = new File(sd, backupDBPath);
                backupPath.mkdirs();
                File currentDB = new File(data, currentDBPath + dbname);
                File backupDB = new File(sd, backupDBPath + dbname);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
        } catch (Exception e) {
        }
    }
    
    private boolean restoreDatabaseFromSD(String dbname) {
	    try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();
    	    File backupDB = new File(sd, backupDBPath + dbname);
    	    File currentDB = new File(data, currentDBPath + dbname);
    	    if (backupDB.exists()) {
    	        FileUtils.copyFile(new FileInputStream(backupDB), new FileOutputStream(currentDB));
    	        return true;
    	    }
    	    return false;
	    } catch (Exception e) {
	    	return false;
	    }
    }
    
    

    
} 