package com.brentandjody.prayerlist;

import com.brentandjody.prayerlist.Database;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class ListChooser_Activity extends Activity { 
	// implements TextWatcher {
	
	static final int SELECTED_COLOR = R.color.secondary_color;
	static final int UNSELECTED_COLOR = R.color.hint;
	
	private EditText newList;
	private SubLists subLists;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_chooser);
        subLists = new SubLists(this);
        ListView listsView = (ListView) findViewById(R.id.lists);
        LayoutInflater inflater = this.getLayoutInflater();
        View footerView = inflater.inflate(R.layout.listfooter_list_chooser, null, false);
        listsView.addFooterView(footerView);
        newList = (EditText) findViewById(R.id.newList);
        Button createButton = (Button) findViewById(R.id.createButton);
        // Create a new list
        createButton.setOnClickListener(new View.OnClickListener() {
  		    @Override
  		    public void onClick(View view) {
  		    	String listName = newList.getText().toString();
  		    	if (listName.isEmpty()) return;
  		    	//avoid duplicates
  		    	SubList subList = subLists.find(listName);
  		    	if (subList == null) {
  		    	  subList = new SubList(listName);
  		    	}
  		    	//add to db
  		    	subLists.add(new SubList(listName));
  	      	  	newList.setText("");
  		    }
  	  	});
        String[] from = new String[] {Database.KEY_SUBLIST_NAME};
        int[] to = new int[] {android.R.id.text1};
        SimpleCursorAdapter adapter  = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, 
        		subLists.getCursor(SubList.SORT_BY_MOSTUSED), from, to, CursorAdapter.NO_SELECTION);
        listsView.setAdapter(adapter);

//        newList.addTextChangedListener(this);

        
	}
	 
//	@Override
//	public void onBackPressed() {
//	    setResult(RESULT_OK, new Intent());
//	    super.onBackPressed();
//	}
//	
//	@Override
//	public void afterTextChanged(Editable e) {
//		String filterText = e.toString().toLowerCase();
//		String buttonText;
//		ToggleButton button;
//		boolean matchFound = false;
//		for(int i = 0; i < tagButtons.getChildCount(); i++) {
//			button = ((ToggleButton) tagButtons.getChildAt(i));
//			buttonText = button.getText().toString();
//			if (filterText.equals(buttonText)) {
//				matchFound = true;
//			}
//			if (buttonText.contains(filterText)) {
//				button.setVisibility(View.VISIBLE);
//			} else {
//				button.setVisibility(View.GONE);
//			}
//		}		
//		createButton.setEnabled(!matchFound);
//	}

//	@Override
//	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,int arg3) {
//	}
//
//	@Override
//	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
//	}

}
