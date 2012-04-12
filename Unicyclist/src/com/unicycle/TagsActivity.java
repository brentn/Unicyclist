package com.unicycle;

import java.util.Iterator;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TagsActivity extends Activity {
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tag_picker);
        
        LinearLayout tagButtons = (LinearLayout) findViewById(R.id.byUsage);
        
        Location location = ((UnicyclistApplication) getApplication()).getCurrentLocation();
        Tags db = new Tags(getApplicationContext());
        
        for(Iterator<Tag> i = db.getAllTags(Tags.SORT_BY_USAGE).iterator(); i.hasNext(); ) {
      	  Tag tag = i.next();
      	  Button button = new Button(this);
      	  button.setBackgroundResource(R.drawable.black_button);
      	  button.setLines(1);
      	  button.setTextSize(20);
      	  button.setMaxWidth(200);
      	  button.setTextColor(Color.WHITE);
      	  button.setText(tag.getName());
      	  if (location.getTagString().contains(tag.getName())) {
      		  button.setTextColor(Color.parseColor("#99CC00"));
      	  }
      	  
      	  TextView spacer = new TextView(this);
      	  spacer.setWidth(5);
      	  spacer.setText("");

      	  tagButtons.addView(button);
      	  tagButtons.addView(spacer);
      	}

        db.close();
        
	}

}
