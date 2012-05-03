package com.unicycle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Tags extends SQLiteOpenHelper {

	public static final int SORT_BY_NAME = 1;
	public static final int SORT_BY_USAGE = 2;

	private static final int DATABASE_VERSION = 3;
	private static final String DATABASE_NAME = "tags";

	//table names
	private static final String TABLE_TAGS = "tags";
	private static final String TABLE_LOCATION_TAGS = "locationTags";
	private static final String TABLE_TRAIL_TAGS = "trailTags";
	private static final String TABLE_FEATURE_TAGS = "featureTags";

	//column names
	private static final String KEY_TAG_ID = "id";
	private static final String KEY_TAG_NAME = "name";
	private static final String KEY_TAG_USAGE = "usage";
	
	private static final String KEY_LOCATION_TAG_ID = "id";
	private static final String KEY_LOCATION_TAG_TAGID = "tagId";
	private static final String KEY_LOCATION_TAG_LOCATIONID = "locationId";
	
	private static final String KEY_TRAIL_TAG_ID = "id";
	private static final String KEY_TRAIL_TAG_TAGID = "tagId";
	private static final String KEY_TRAIL_TAG_TRAILID = "trailId";
	
	private static final String KEY_FEATURE_TAG_ID = "id";
	private static final String KEY_FEATURE_TAG_TAGID = "tagId";
	private static final String KEY_FEATURE_TAG_FEATUREID = "featureId";
	
	private Context mContext;

	public Tags(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		mContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_TAGS_TABLE = "CREATE TABLE " + TABLE_TAGS + "("
				+ KEY_TAG_ID + " INTEGER PRIMARY KEY," + KEY_TAG_NAME + " TEXT," + KEY_TAG_USAGE + " INTEGER)";
		String CREATE_LOCATION_TAGS_TABLE = "CREATE TABLE " + TABLE_LOCATION_TAGS + "("
				+ KEY_LOCATION_TAG_ID + " INTEGER PRIMARY KEY, "+ KEY_LOCATION_TAG_LOCATIONID + " INTEGER, " + KEY_LOCATION_TAG_TAGID + " INTEGER)";
		String CREATE_TRAIL_TAGS_TABLE = "CREATE TABLE " + TABLE_TRAIL_TAGS + "("
				+ KEY_TRAIL_TAG_ID + " INTEGER PRIMARY KEY, "+ KEY_TRAIL_TAG_TRAILID + " INTEGER, " + KEY_TRAIL_TAG_TAGID + " INTEGER)";
		String CREATE_FEATURE_TAGS_TABLE = "CREATE TABLE " + TABLE_FEATURE_TAGS + "("
				+ KEY_FEATURE_TAG_ID + " INTEGER PRIMARY KEY, "+ KEY_FEATURE_TAG_FEATUREID + " INTEGER, " + KEY_FEATURE_TAG_TAGID + " INTEGER)";
		db.execSQL(CREATE_TAGS_TABLE);
		db.execSQL(CREATE_LOCATION_TAGS_TABLE);
		db.execSQL(CREATE_TRAIL_TAGS_TABLE);
		db.execSQL(CREATE_FEATURE_TAGS_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TAGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION_TAGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRAIL_TAGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FEATURE_TAGS);
        onCreate(db);
	}
	
	public String databaseName() {
		return DATABASE_NAME;
	}
	
	private int addTag(Tag tag) {
		//Only if it doesn't exist.  Otherwise return ID of existing tag.
		Tag target = findTagByName(tag.getName());
		if ( target != null ) {
			return target.getId();
		} else {
			SQLiteDatabase db = this.getWritableDatabase();
			ContentValues values = new ContentValues();
	        values.put(KEY_TAG_NAME, tag.getName());  
	        values.put(KEY_TAG_USAGE, tag.getUsage());
	        int id = (int) db.insert(TABLE_TAGS, null, values);
	        tag.setId(id);
	        db.close();
	        return id;
		}
	}
	
	public int addTagFor(Object o, Tag tag) {
		int result = -1;
		String table="";
		String objectKey="";
		String tagKey="";
		int id = -1;
		if (o instanceof Location) {
			id = ((Location) o).getId();
			table=TABLE_LOCATION_TAGS;
			objectKey=KEY_LOCATION_TAG_LOCATIONID;
			tagKey=KEY_LOCATION_TAG_TAGID;
		} else if (o instanceof Trail) {
			id = ((Trail) o).getId();
			table=TABLE_TRAIL_TAGS;
			objectKey=KEY_TRAIL_TAG_TRAILID;
			tagKey=KEY_TRAIL_TAG_TAGID;
		} else if (o instanceof Feature) {
			id = ((Feature) o).getId();
			table=TABLE_FEATURE_TAGS;
			objectKey=KEY_FEATURE_TAG_FEATUREID;
			tagKey=KEY_FEATURE_TAG_TAGID;
		}
		if (id == -1) {return -1;} //return if no ID has been set
		addTag(tag); //this will not add duplicate
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(objectKey,id);
		values.put(tagKey, tag.getId());
		result = (int) db.insert(table, null, values);
		db.close();
		//record another use of this tag
		tag.setUsage(tag.getUsage()+1);
		this.updateTag(tag);
		return result;
	}
	
    public void addTags(Object o, List<Tag> tags) {
    	Iterator<Tag> i = tags.iterator();
    	while (i.hasNext()) {
    		this.addTagFor(o, (Tag) i.next());
    		i.remove();
    	}
    }    
	
	public Tag findTagByName(String name) {
		Tag tag = null;
		String query = "SELECT * FROM " + TABLE_TAGS + " WHERE " + KEY_TAG_NAME + " =  '" + name + "'";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(query, null);
		if (cursor.moveToFirst()) {
			tag = getTag(Integer.parseInt(cursor.getString(0)));
		}
		cursor.close();
		db.close();
		return tag;
	}
	
	public int findTag(Object o, Tag tag) {
		int result = -1;
		String query = "";
		if (o instanceof Location) {
			query = "SELECT * FROM " + TABLE_LOCATION_TAGS 
					+ " WHERE " + KEY_LOCATION_TAG_LOCATIONID + " = " + Integer.toString(((Location) o).getId())
					+ " AND " + KEY_LOCATION_TAG_TAGID + " = " + Integer.toString(tag.getId());
		} else if (o instanceof Trail) {
			query = "SELECT * FROM " + TABLE_TRAIL_TAGS 
					+ " WHERE " + KEY_TRAIL_TAG_TRAILID + " = " + Integer.toString(((Trail) o).getId())
					+ " AND " + KEY_TRAIL_TAG_TAGID + " = " + Integer.toString(tag.getId());
		} else if (o instanceof Feature) {
			query = "SELECT * FROM " + TABLE_FEATURE_TAGS 
					+ " WHERE " + KEY_FEATURE_TAG_FEATUREID + " = " + Integer.toString(((Feature) o).getId())
					+ " AND " + KEY_LOCATION_TAG_TAGID + " = " + Integer.toString(tag.getId());
		}
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(query, null);
		if (cursor.moveToFirst()) {
			result = Integer.parseInt(cursor.getString(0));
		}
		db.close();
		return result;
	}
	
	public Tag getTag(int id) {
		Tag tag = null;
		String query = "SELECT " + KEY_TAG_ID + ", " + KEY_TAG_NAME + ", " + KEY_TAG_USAGE 
				+ " FROM " + TABLE_TAGS + " WHERE " + KEY_TAG_ID + " = " + Integer.toString(id) + " LIMIT 1";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(query, null);
		if (cursor.moveToFirst()) {
			tag = new Tag();
			tag.setId(Integer.parseInt(cursor.getString(0)));
			tag.setName(cursor.getString(1));
			tag.setUsage(Integer.parseInt(cursor.getString(2)));
		}
		db.close();
		return tag;
	}
	
    public List<Tag> getAllTags(int sortBy) {
	    List<Tag> tagList = new ArrayList<Tag>();
	    String query;
	    switch (sortBy) {
	    case SORT_BY_NAME: 
	    	query = "SELECT " + KEY_TAG_ID + ", " + KEY_TAG_NAME + ", " + KEY_TAG_USAGE
	    	+ " FROM " + TABLE_TAGS + " ORDER BY " + KEY_TAG_NAME;
	    	break;
	    case SORT_BY_USAGE:
	    	query = "SELECT " + KEY_TAG_ID + ", " + KEY_TAG_NAME + ", " + KEY_TAG_USAGE
	    	+ " FROM " + TABLE_TAGS + " ORDER BY " + KEY_TAG_USAGE + " DESC";
	    	break;
    	default:
    		query = "SELECT " + KEY_TAG_ID + ", " + KEY_TAG_NAME + ", " + KEY_TAG_USAGE
    		+ " FROM " + TABLE_TAGS;
    		break;
	    };
	    SQLiteDatabase db = this.getReadableDatabase();
	    Cursor cursor = db.rawQuery(query, null);
	    if (cursor.moveToFirst()) {
	        do {
	        	int id = Integer.parseInt(cursor.getString(0));
	        	Tag tag = new Tag();
	        	tag.setId(id);
	        	tag.setName(cursor.getString(1));
	        	tag.setUsage(Integer.parseInt(cursor.getString(2)));
	        	tagList.add(tag);
	        } while (cursor.moveToNext());
	    }
	    db.close();
	    return tagList;
    }
    
    public List<Tag> getTagsFor(Object o) {
    	List<Tag> tagList = new ArrayList<Tag>();
    	String query = "";
    	if (o instanceof Location) {
	    	query = "SELECT t." + KEY_TAG_ID + ", t." + KEY_TAG_NAME + ", t." + KEY_TAG_USAGE
	    			+ " FROM " + TABLE_TAGS + " t"
	    			+ " INNER JOIN " + TABLE_LOCATION_TAGS + " lt ON t." + KEY_TAG_ID + " = lt." + KEY_LOCATION_TAG_TAGID 
	    			+ " WHERE lt." + KEY_LOCATION_TAG_LOCATIONID + " = " + Integer.toString(((Location) o).getId())
	    			+ " ORDER BY t." + KEY_TAG_USAGE + " DESC";
    	} else if (o instanceof Trail) {
        	query = "SELECT t." + KEY_TAG_ID + ", t." + KEY_TAG_NAME + ", t." + KEY_TAG_USAGE
        			+ " FROM " + TABLE_TAGS + " t"
        			+ " INNER JOIN " + TABLE_TRAIL_TAGS + " tt ON t." + KEY_TAG_ID + " = tt." + KEY_TRAIL_TAG_TAGID 
        			+ " WHERE tt." + KEY_TRAIL_TAG_TRAILID + " = " + Integer.toString(((Trail) o).getId())
        			+ " ORDER BY t." + KEY_TAG_USAGE + " DESC";    		
    	} else if (o instanceof Feature) {
        	query = "SELECT t." + KEY_TAG_ID + ", t." + KEY_TAG_NAME + ", t." + KEY_TAG_USAGE
        			+ " FROM " + TABLE_TAGS + " t"
        			+ " INNER JOIN " + TABLE_FEATURE_TAGS + " ft ON t." + KEY_TAG_ID + " = ft." + KEY_FEATURE_TAG_TAGID 
        			+ " WHERE ft." + KEY_FEATURE_TAG_FEATUREID + " = " + Integer.toString(((Feature) o).getId())
        			+ " ORDER BY t." + KEY_TAG_USAGE + " DESC";    		    		
    	}
    	SQLiteDatabase db = this.getReadableDatabase();
    	Cursor cursor = db.rawQuery(query, null);
    	if (cursor.moveToFirst()) {
    		do {
	        	int id = Integer.parseInt(cursor.getString(0));
	        	Tag tag = new Tag();
	        	tag.setId(id);
	        	tag.setName(cursor.getString(1));
	        	tag.setUsage(Integer.parseInt(cursor.getString(2)));
	        	tagList.add(tag);    			
    		} while (cursor.moveToNext());
    	}
    	db.close();
    	return tagList;
    }
    
    public String getTagStringFor(Object o) {
    	String result = "";
    	List<Tag> tagList = null;
    	if (o instanceof Location) {
    		tagList = ((Location) o).getTags();
    	} else if (o instanceof Trail) {
    		tagList = ((Trail) o).getTags();
    	} else if (o instanceof Feature) {
    		tagList = ((Feature) o).getTags();
    	}
    	if (tagList != null) {
    		Iterator<Tag> i = tagList.iterator();
    		while (i.hasNext()) {
    			result = result + i.next().getName() + "      ";
    		}
    	}
		return result.trim();
    }
    
    
    public List<Location> getLocationsForTag(Tag tag) {
    	List<Location> locationList = new ArrayList<Location>();
    	String query = "SELECT " + KEY_LOCATION_TAG_LOCATIONID 
    			+ " FROM " + TABLE_LOCATION_TAGS
    			+ " WHERE " + KEY_LOCATION_TAG_TAGID + " = " + Integer.toString(tag.getId());
    	SQLiteDatabase db = this.getReadableDatabase();
    	Cursor cursor = db.rawQuery(query, null);
    	Locations locations = new Locations(mContext);
    	if (cursor.moveToFirst()) {
    		do {
    			int id = Integer.parseInt(cursor.getString(0));
    			locationList.add(locations.getLocation(id));
    		} while (cursor.moveToNext());
    	}
    	db.close();
    	return locationList;
    }
    
    public List<Trail> getTrailsForTag(Tag tag) {
    	List<Trail> trailList = new ArrayList<Trail>();
    	String query = "SELECT " + KEY_TRAIL_TAG_TRAILID 
    			+ " FROM " + TABLE_TRAIL_TAGS
    			+ " WHERE " + KEY_TRAIL_TAG_TAGID + " = " + Integer.toString(tag.getId());
    	SQLiteDatabase db = this.getReadableDatabase();
    	Cursor cursor = db.rawQuery(query, null);
    	Trails trails = new Trails(mContext);
    	if (cursor.moveToFirst()) {
    		do {
    			int id = Integer.parseInt(cursor.getString(0));
    			trailList.add(trails.getTrail(id));
    		} while (cursor.moveToNext());
    	}
    	db.close();
    	return trailList;
    }
    
    public int updateTag(Tag tag) {
    	int id = tag.getId();
    	SQLiteDatabase db = this.getWritableDatabase();
    	ContentValues values = new ContentValues();
    	values.put(KEY_TAG_NAME, tag.getName());
    	values.put(KEY_TAG_USAGE, tag.getUsage());
    	int result = db.update(TABLE_TAGS, values, KEY_TAG_ID + " = ?", new String[] {String.valueOf(id) });
    	db.close();
    	return result;
    }
    
    public void removeTagFor(Object o, Tag tag) {
    	int id = findTag(o, tag);
    	if ( id >= 0 ) {
    		SQLiteDatabase db = this.getWritableDatabase();
    		if (o instanceof Location) {
    			db.delete(TABLE_LOCATION_TAGS, KEY_LOCATION_TAG_ID + " = ?", new String[] {String.valueOf(id) });
    		} else if (o instanceof Trail) {
    			db.delete(TABLE_TRAIL_TAGS, KEY_TRAIL_TAG_ID + " = ?", new String[] {String.valueOf(id) });
    		} else if (o instanceof Feature) {
    			db.delete(TABLE_FEATURE_TAGS, KEY_FEATURE_TAG_ID + " = ?", new String[] {String.valueOf(id) });
    		}
    		db.close();
    		//reduce usage count for tag
    		tag.setUsage(tag.getUsage()-1);
    		updateTag(tag);
    	}
    }
    
    public void removeTagsFor(Object o) {
    	List<Tag> tagList = new ArrayList<Tag>();
    	if (o instanceof Location) {
    		tagList = ((Location) o).getTags();
    	} else if (o instanceof Trail) {
    		tagList = ((Trail) o).getTags();
    	} else if (o instanceof Feature) {
    		tagList = ((Feature) o).getTags();
    	}
        for(Iterator<Tag> i = tagList.iterator(); i.hasNext(); ) {
      	  Tag tag = i.next();
      	  removeTagFor(o,tag);
        }
    }
    
    public void deleteTag(Tag tag) {
    	SQLiteDatabase db = this.getWritableDatabase();
    	db.delete(TABLE_TAGS, KEY_TAG_ID + "=?", new String[] {Integer.toString(tag.getId()) });
    	db.close();
    }
    	
    public ViewGroup getTagsView(final Activity activity,final Object o) {
    	List<Tag> _tags = new ArrayList<Tag>();
    	int _tagType = 0;
    	if (o instanceof Location) {
    		_tags = ((Location) o).getTags();
    		_tagType = Tag.LOCATION_TAG;
    	} else if (o instanceof Trail) {
    		_tags = ((Trail) o ).getTags();
    		_tagType = Tag.TRAIL_TAG;
    	} else if (o instanceof Feature) {
    		_tags = ((Feature) o).getTags();
    		_tagType = Tag.FEATURE_TAG;
    	}
    	final int _finalTagType = _tagType;
        OnClickListener editTags = new OnClickListener() {
        	public void onClick(View view) {
        		Intent intent = new Intent(activity, TagPickerActivity.class);
        		if (o instanceof Location) {
        			intent.putExtra("objectType", UnicyclistActivity.LOCATION_TYPE);
        		} else if (o instanceof Trail) {
        			intent.putExtra("objectType", UnicyclistActivity.TRAIL_TYPE);
        		} else if (o instanceof Feature) {
        			intent.putExtra("objectType", UnicyclistActivity.FEATURE_TYPE);
        		}
        		activity.startActivityForResult(intent,UnicyclistActivity.SELECT_TAGS);
        	}
        };
    	HorizontalScrollView view = new HorizontalScrollView(mContext);
    	if (_tags.size() == 0) {
    		TextView noTags = new TextView(mContext);
    		noTags.setText(mContext.getString(R.string.click_to_add_tags));
    		noTags.setClickable(true);
    		noTags.setOnClickListener(editTags);
    		noTags.setPadding(0, 0, 0, 10);
    		noTags.setGravity(Gravity.CENTER);
    		noTags.setTextColor(Color.parseColor("#FFBB33"));
    		view.addView(noTags);
    	} else {
		    	LinearLayout llview = new LinearLayout(mContext);
	    		TextView spacer = new TextView(mContext);
		    	Iterator<Tag> i = _tags.iterator();
		    	while (i.hasNext()) {
		    			final String name = i.next().getName();
			    		TextView tagText = new TextView(mContext);
			    		tagText.setTextColor(Color.parseColor("#99CC00"));
			    		tagText.setClickable(true);
			    		tagText.setText(name);
			    		tagText.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								((TextView) v).setTextSize(30);
								Intent intent = new Intent(activity, TagActivity.class);
								intent.putExtra("tagType", _finalTagType);
								intent.putExtra("tagName", name);
								if (activity instanceof LocationActivity) {
									((LocationActivity) activity).showTagProgress();
								} else	if (activity instanceof TrailActivity) {
									((TrailActivity) activity).showTagProgress();
								}
				        		activity.startActivity(intent);
							}
			    		});
			    		spacer = new TextView(mContext);
			    		spacer.setText("     ");
		    		llview.addView(tagText);
		    		llview.addView(spacer);
		    	}
		    	ImageButton editButton = new ImageButton(mContext);
		    	editButton.setOnClickListener(editTags);
		    	editButton.setBackgroundResource(R.drawable.ic_menu_edit);
	   		LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(36, 36);
	    	llview.addView(editButton,layout);
	    	view.addView(llview);
    	}
    	return view;
    }
       
}
