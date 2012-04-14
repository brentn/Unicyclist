package com.unicycle;

import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

public class TagsActivity extends Activity implements TextWatcher {
	
	static final int SELECTED_COLOR = Color.parseColor("#669900");
	static final int UNSELECTED_COLOR = Color.BLACK;
	
	private EditText filter;
	private LinearLayout tagButtons;
	private List<Tag> tagSet;
	private Button createButton;
	private View.OnClickListener buttonClickListener;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tag_picker);
        tagSet = ((UnicyclistApplication) getApplication()).getCurrentTagSet();
        tagButtons = (LinearLayout) findViewById(R.id.byUsage);
        filter = (EditText) findViewById(R.id.tagFilter);
        createButton = (Button) findViewById(R.id.createButton);
        
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
	      		  button.setTextColor(SELECTED_COLOR);
	      	  }
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
//TODO - ONLY HAPPENS FOR LOCATION TAGS          			
		        				db.removeLocationTag(((UnicyclistApplication) getApplication()).getCurrentLocation(), tag);
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
      			  ToggleButton button = new ToggleButton(getBaseContext());
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
//TODO - ONLY HAPPENS FOR LOCATION TAGS          			
		        				db.removeLocationTag(((UnicyclistApplication) getApplication()).getCurrentLocation(), tag);
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
//TODO - ONLY HAPPENS FOR LOCATION TAGS      			  
      			  db.addLocationTag(((UnicyclistApplication) getApplication()).getCurrentLocation(), tag);
      			  createButton.setEnabled(false);
      			  filter.setText("");
      		  }
      	  });
    	 
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
