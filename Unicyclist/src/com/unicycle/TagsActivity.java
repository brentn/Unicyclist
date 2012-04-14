package com.unicycle;

import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

public class TagsActivity extends Activity {
	
	static final int SELECTED_COLOR = Color.parseColor("#669900");
	static final int UNSELECTED_COLOR = Color.BLACK;
	
	List<Tag> tagSet;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tag_picker);
        tagSet = ((UnicyclistApplication) getApplication()).getCurrentTagSet();
        LinearLayout tagButtons = (LinearLayout) findViewById(R.id.byUsage);
        
        final Tags db = new Tags(getApplicationContext());
        List<Tag> allTags = db.getAllTags(Tags.SORT_BY_USAGE);
        
        for(Iterator<Tag> i = allTags.iterator(); i.hasNext(); ) {
      	  Tag tag = i.next();
      	  ToggleButton button = new ToggleButton(this);
      	  button.setLines(1);
      	  button.setTextSize(20);
      	  button.setMaxWidth(200);
      	  button.setText(tag.getName());
      	  button.setTextOn(tag.getName());
      	  button.setTextOff(tag.getName());
      	  if (tagSet.contains(tag)) {
      		  button.setChecked(true);
      		  button.setTextColor(Color.parseColor("#669900"));
      	  }

          button.setOnClickListener(new OnClickListener() {
          	@Override
          	public void onClick(View view) {
      			Tag tag = db.findTagByName(((ToggleButton) view).getText().toString());
          		if (((ToggleButton) view).isChecked()) {
          			((ToggleButton) view).setTextColor(SELECTED_COLOR);
          			tagSet.add(tag);
          		} else {
          			((ToggleButton) view).setTextColor(UNSELECTED_COLOR);
          			tagSet.remove(tag);
          		}
          	}
          });

      	  tagButtons.addView(button);
      	}

        db.close();
        
	}
	
	protected void onPause() {
		super.onPause();
		Intent _result = new Intent();
		setResult(Activity.RESULT_OK,_result);
	}

}
