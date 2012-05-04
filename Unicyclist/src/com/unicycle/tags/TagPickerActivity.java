package com.unicycle.tags;

import java.util.Iterator;
import java.util.List;

import com.unicycle.R;
import com.unicycle.UnicyclistActivity;
import com.unicycle.UnicyclistApplication;
import com.unicycle.R.id;
import com.unicycle.R.layout;
import com.unicycle.locations.Location;
import com.unicycle.locations.features.Feature;
import com.unicycle.locations.trails.Trail;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

public class TagPickerActivity extends Activity implements TextWatcher {
	
	static final int SELECTED_COLOR = Color.parseColor("#669900");
	static final int UNSELECTED_COLOR = Color.BLACK;
	
	private EditText filter;
	private LinearLayout tagButtons;
	private List<Tag> tagSet;
	private Button createButton;
	private Object currentObject;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tag_picker);
        switch (getIntent().getExtras().getInt("objectType")) {
        case UnicyclistActivity.LOCATION_TYPE:
        	currentObject = ((UnicyclistApplication) getApplication()).getCurrentLocation();
        	tagSet = ((Location) currentObject).getTags();
        	break;
        case UnicyclistActivity.TRAIL_TYPE:
        	currentObject = ((UnicyclistApplication) getApplication()).getCurrentTrail();
        	tagSet = ((Trail) currentObject).getTags();
        	break;
        case UnicyclistActivity.FEATURE_TYPE:
        	currentObject = ((UnicyclistApplication) getApplication()).getCurrentFeature();
        	tagSet = ((Feature) currentObject).getTags();
        	break;
        }
        tagButtons = (LinearLayout) findViewById(R.id.byUsage);
        filter = (EditText) findViewById(R.id.tagFilter);
        createButton = (Button) findViewById(R.id.createButton);
        
        final Tags db = new Tags(TagPickerActivity.this);
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
	      		  button.setTextColor(SELECTED_COLOR);
	      	  }
	          button.setOnClickListener(new View.OnClickListener() {
		        	@Override
		        	public void onClick(View view) {
		    			Tag tag = db.findTagByName(((ToggleButton) view).getText().toString());
		        		if (((ToggleButton) view).isChecked()) {
		        			((ToggleButton) view).setTextColor(SELECTED_COLOR);
		        			db.addTagFor(currentObject, tag);
		        			tagSet.add(tag);
		        		} else {
		        			((ToggleButton) view).setTextColor(UNSELECTED_COLOR);
		        			tagSet.remove(tag);
		        				db.removeTagFor(currentObject, tag);
		        			if (tag.getUsage() == 0) {
		        				db.deleteTag(tag);
		        				((LinearLayout) view.getParent()).removeView(view);
		        				((ToggleButton) view).setVisibility(View.GONE);
		        			}
		        		}
		        	}
	          });
	      	  tagButtons.addView(button);
      	}
        
        db.close();

        filter.addTextChangedListener(this);
        
    	  createButton.setOnClickListener(new View.OnClickListener() {
      		  @Override
      		  public void onClick(View view) {
      			  Tag tag = new Tag(filter.getText().toString().toLowerCase());
      			  ToggleButton button = new ToggleButton(TagPickerActivity.this);
      	      	  button.setLines(1);
      	      	  button.setTextSize(20);
      	      	  button.setMaxWidth(200);
      	      	  button.setText(tag.getName());
      	      	  button.setTextOn(tag.getName());
      	      	  button.setTextOff(tag.getName());
      	      	  button.setChecked(true);
      	      	  button.setTextColor(SELECTED_COLOR);
      	      	  button.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
		    			Tag tag = db.findTagByName(((ToggleButton) view).getText().toString());
		        		if (((ToggleButton) view).isChecked()) {
		        			((ToggleButton) view).setTextColor(SELECTED_COLOR);
		        			tagSet.add(tag);
		        		} else {
		        			((ToggleButton) view).setTextColor(UNSELECTED_COLOR);
		        			tagSet.remove(tag);
		        				db.removeTagFor(currentObject, tag);
		        			if (tag.getUsage() == 0) {
		        				db.deleteTag(tag);
		        				((LinearLayout) view.getParent()).removeView(view);
		        				((ToggleButton) view).setVisibility(View.GONE);
		        			}
		        		}
		        	}
				});
      	      	  tagButtons.addView(button);
      			  tagSet.add(tag);
      			  db.addTagFor(currentObject, tag);
      			  createButton.setEnabled(false);
      			  filter.setText("");
      		  }
      	  });
    	 
	}
	
	@Override
	public void onBackPressed() {
	    setResult(RESULT_OK, new Intent());
	    super.onBackPressed();
	}

	@Override
	public void afterTextChanged(Editable e) {
		
		String filterText = e.toString().toLowerCase();
		String buttonText;
		ToggleButton button;
		boolean matchFound = false;
		for(int i = 0; i < tagButtons.getChildCount(); i++) {
			button = ((ToggleButton) tagButtons.getChildAt(i));
			buttonText = button.getText().toString();
			if (filterText.equals(buttonText)) {
				matchFound = true;
			}
			if (buttonText.contains(filterText)) {
				button.setVisibility(View.VISIBLE);
			} else {
				button.setVisibility(View.GONE);
			}
		}		
		createButton.setEnabled(!matchFound);
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
		
	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}
	
}
