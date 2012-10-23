package com.brentandjody.prayerlist;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.support.v4.widget.CursorAdapter;
import android.widget.TextView;

public class RequestListAdapter extends CursorAdapter {
	
	private Context _context;
	private Cursor _cursor;

	public RequestListAdapter(Context context,Cursor c) {
		super(context, c, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		_context = context;
		_cursor = c;
	}
	
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
    	_cursor = cursor;
        TextView title = (TextView) view.findViewById(R.id.title);
        CheckBox checkbox = (CheckBox) view.findViewById(R.id.checkBox);
        title.setText(cursor.getString(cursor.getColumnIndex(Database.KEY_REQUEST_DESCRIPTION)));
        checkbox.setChecked(cursor.getInt(cursor.getColumnIndex(Database.KEY_REQUEST_CHECKED))==1);
        checkbox.setTag(cursor.getInt(cursor.getColumnIndex(Database.KEY_SUBLIST_ID))); //hide the ID in the object
        checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
	 		@Override
			public void onCheckedChanged(CompoundButton checkbox, boolean checked) {
				int requestId = (Integer) checkbox.getTag();
				PrayerRequests prayerRequests = new PrayerRequests(_context);
				PrayerRequest request = prayerRequests.get(requestId);
				if (request != null) {
				    request.setChecked(checked);
				    prayerRequests.update(request);
				}
			}
		});
//TODO:		//Only show the animation once
//		if (view.getTag()==null || ((Boolean)view.getTag())==false) {
			Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
			animation.setStartOffset(50*cursor.getPosition());
			view.startAnimation(animation);
			view.setTag(true);
//		} else {
//			view.clearAnimation();
//		}
     }
    
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.listitem_request, parent, false);
        bindView(v, context, cursor);
        return v;
    }
    
    public int getIdOfCurrentRequest() {
    	if (_cursor == null) {
    		return -1;
    	}
    	return _cursor.getInt(_cursor.getColumnIndex(Database.KEY_SUBLIST_ID));
    }

}
