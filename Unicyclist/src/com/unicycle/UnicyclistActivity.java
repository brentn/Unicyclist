package com.unicycle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class UnicyclistActivity extends Activity {

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        
        Button locationsButton = (Button) findViewById(R.id.locationsButton);
        locationsButton.setOnClickListener(new OnClickListener() {
        	@Override
        	public void onClick(View view) {
        		Intent intent = new Intent(UnicyclistActivity.this, LocationsActivity.class);
        		UnicyclistActivity.this.startActivity(intent);       		
         	}
        });
	}

}
