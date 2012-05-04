package com.unicycle.comments;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.unicycle.R;
import com.unicycle.R.drawable;
import com.unicycle.R.id;
import com.unicycle.R.layout;
import com.unicycle.R.string;
import com.unicycle.locations.Location;
import com.unicycle.locations.LocationActivity;
import com.unicycle.locations.features.Feature;
import com.unicycle.locations.trails.Trail;
import com.unicycle.locations.trails.TrailActivity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Comments extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 2;
	private static final String DATABASE_NAME = "comments";
	
	//table names
	private static final String TABLE_COMMENTS = "comments";
	private static final String TABLE_LOCATION_COMMENTS = "location_comments";
	private static final String TABLE_TRAIL_COMMENTS = "trail_comments";
	private static final String TABLE_FEATURE_COMMENTS = "feature_comments";
	
	//column names
	private static final String KEY_COMMENT_ID = "id";
	private static final String KEY_COMMENT_USER = "user";
	private static final String KEY_COMMENT_DATE="date";
	private static final String KEY_COMMENT_COMMENT = "comment";
	private static final String KEY_LOCATION_COMMENT_ID = "id";
	private static final String KEY_LOCATION_COMMENT_LOCATIONID = "locationId";
	private static final String KEY_LOCATION_COMMENT_COMMENTID = "commentId";
	private static final String KEY_TRAIL_COMMENT_ID = "id";
	private static final String KEY_TRAIL_COMMENT_TRAILID = "trailId";
	private static final String KEY_TRAIL_COMMENT_COMMENTID = "commentId";
	private static final String KEY_FEATURE_COMMENT_ID = "id";
	private static final String KEY_FEATURE_COMMENT_FEATUREID = "featureId";
	private static final String KEY_FEATURE_COMMENT_COMMENTID = "commentId";
	
	private CommentsListAdapter commentsListAdapter;
	private List<Comment> commentsList;
	private Context mContext;

    public Comments(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_COMMENTS_TABLE = "CREATE TABLE " + TABLE_COMMENTS + " (" + KEY_COMMENT_ID + " INTEGER PRIMARY KEY, "
				+ KEY_COMMENT_USER + " TEXT, " + KEY_COMMENT_DATE + " INTEGER, " + KEY_COMMENT_COMMENT + " TEXT)";
		String CREATE_LOCATION_COMMENTS_TABLE = "CREATE TABLE " + TABLE_LOCATION_COMMENTS + " (" + KEY_LOCATION_COMMENT_ID
				+ " INTEGER PRIMARY KEY, " + KEY_LOCATION_COMMENT_LOCATIONID + " INTEGER, " + KEY_LOCATION_COMMENT_COMMENTID + " INTEGER)";
		String CREATE_TRAIL_COMMENTS_TABLE = "CREATE TABLE " + TABLE_TRAIL_COMMENTS + " (" + KEY_TRAIL_COMMENT_ID
				+ " INTEGER PRIMARY KEY, " + KEY_TRAIL_COMMENT_TRAILID + " INTEGER, " + KEY_TRAIL_COMMENT_COMMENTID + " INTEGER)";
		String CREATE_FEATURE_COMMENTS_TABLE = "CREATE TABLE " + TABLE_FEATURE_COMMENTS + " (" + KEY_FEATURE_COMMENT_ID
				+ " INTEGER PRIMARY KEY, " + KEY_FEATURE_COMMENT_FEATUREID + " INTEGER, " + KEY_FEATURE_COMMENT_COMMENTID + " INTEGER)";
		db.execSQL(CREATE_COMMENTS_TABLE);
		db.execSQL(CREATE_LOCATION_COMMENTS_TABLE);
		db.execSQL(CREATE_TRAIL_COMMENTS_TABLE);
		db.execSQL(CREATE_FEATURE_COMMENTS_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMMENTS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION_COMMENTS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRAIL_COMMENTS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_FEATURE_COMMENTS);
		onCreate(db);
	}

	public String databaseName() {
		return DATABASE_NAME;
	}
	
	private int addComment(Comment comment) {
		SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_COMMENT_DATE, comment.getDate().getTime());
        values.put(KEY_COMMENT_USER, comment.getUser());
        values.put(KEY_COMMENT_COMMENT, comment.getComment());
        int id = (int) db.insert(TABLE_COMMENTS, null, values);
        comment.setId(id);
        db.close();
        return id;
	}
	
	public void addCommentFor(Object o, Comment comment) {
		ContentValues values = new ContentValues();
		SQLiteDatabase db;
		if (o instanceof Location) {
			values.put(KEY_LOCATION_COMMENT_LOCATIONID,((Location) o).getId());
			values.put(KEY_LOCATION_COMMENT_COMMENTID, addComment(comment));
			db = this.getWritableDatabase();
			db.insert(TABLE_LOCATION_COMMENTS, null, values);
			db.close();
		} else if (o instanceof Trail) {
			values.put(KEY_TRAIL_COMMENT_TRAILID, ((Trail) o).getId());
			values.put(KEY_TRAIL_COMMENT_COMMENTID, addComment(comment));
			db = this.getWritableDatabase();
			db.insert(TABLE_TRAIL_COMMENTS, null, values);
			db.close();
		} 
	}
	
	public Comment getComment(int id) {
		Comment result = null;
		String query = "SELECT " + KEY_COMMENT_ID + ", " + KEY_COMMENT_USER + ", " + KEY_COMMENT_DATE + "," + KEY_COMMENT_COMMENT
				+ " FROM " + TABLE_COMMENTS
				+ " WHERE " + KEY_COMMENT_ID + " = " + id + " LIMIT 1";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(query, null);
		if (cursor.moveToFirst()) {
			result = new Comment(cursor.getInt(0), new Date(Long.parseLong(cursor.getString(2))), 
					cursor.getString(1), cursor.getString(3));
		}
		db.close();
		return result;
	}
	
	public List<Comment> getCommentsFor(Object o) {
		List<Comment> commentList = new ArrayList<Comment>();
		SQLiteDatabase db = this.getReadableDatabase();
		String query = "";
		if (o instanceof Location) {
			query = "SELECT c." + KEY_COMMENT_ID + ", c." +KEY_COMMENT_DATE + ", c." + KEY_COMMENT_COMMENT + ", c." + KEY_COMMENT_USER
				+ " FROM " + TABLE_COMMENTS + " c"
				+ " JOIN " + TABLE_LOCATION_COMMENTS + " lc ON  c." + KEY_COMMENT_ID + " = lc." + KEY_LOCATION_COMMENT_COMMENTID
				+ " WHERE lc." + KEY_LOCATION_COMMENT_LOCATIONID + " = " + ((Location)o).getId()
				+ " ORDER BY " + KEY_COMMENT_DATE + " DESC";
		} else if (o instanceof Trail) {
			query = "SELECT c." + KEY_COMMENT_ID + ", c." +KEY_COMMENT_DATE + ", c." + KEY_COMMENT_COMMENT + ", c." + KEY_COMMENT_USER
				+ " FROM " + TABLE_COMMENTS + " c"
				+ " JOIN " + TABLE_TRAIL_COMMENTS + " lc ON  c." + KEY_COMMENT_ID + " = lc." + KEY_TRAIL_COMMENT_COMMENTID
				+ " WHERE lc." + KEY_TRAIL_COMMENT_TRAILID + " = " + ((Trail)o).getId()
				+ " ORDER BY " + KEY_COMMENT_DATE + " DESC";			
		}
		Cursor cursor = db.rawQuery(query, null);
		if (cursor.moveToFirst()) {
			do {
				Date date = new Date(Long.parseLong(cursor.getString(1)));
				Comment comment = new Comment(cursor.getInt(0),date,cursor.getString(2),cursor.getString(3));
				commentList.add(comment);
			} while (cursor.moveToNext());
		}
		db.close();
		return commentList;
	}
	
	public void removeCommentFor(Object o, Comment comment) {
    	if ( comment.getId() >= 0 ) {
    		SQLiteDatabase db = this.getWritableDatabase();
    		if (o instanceof Location) {
    			db.delete(TABLE_LOCATION_COMMENTS, KEY_LOCATION_COMMENT_ID + " = ?", new String[] {String.valueOf(comment.getId()) });
    		} else if (o instanceof Trail) {
    			db.delete(TABLE_TRAIL_COMMENTS, KEY_TRAIL_COMMENT_ID + " = ?", new String[] {String.valueOf(comment.getId()) });
    		} else if (o instanceof Feature) {
    			db.delete(TABLE_FEATURE_COMMENTS, KEY_FEATURE_COMMENT_ID + " = ?", new String[] {String.valueOf(comment.getId()) });
    		}
    		db.close();
    	}

	}
	
	public  RelativeLayout getCommentsView(final Object o) {
		RelativeLayout view = new RelativeLayout(mContext);
		commentsList = getCommentsFor(o);
		commentsListAdapter = new CommentsListAdapter(mContext,R.layout.comments_list_item,commentsList);
			ImageView background = new ImageView(mContext);
			background.setImageResource(R.drawable.background);
			background.setScaleType(ScaleType.FIT_START);
			LinearLayout comments = new LinearLayout(mContext);
				comments.setOrientation(LinearLayout.VERTICAL);
				comments.setPadding(8,20,8,0);
				LinearLayout titleBar = new LinearLayout(mContext);
					ImageView addButton = new ImageView(mContext);
					addButton.setImageResource(R.drawable.ic_menu_add);
					titleBar.setClickable(true);
					titleBar.setOnClickListener( new OnClickListener() {
						@Override
						public void onClick(View view) {
							newComment(o);
							commentsListAdapter.notifyDataSetChanged();
						}
					});
					TextView title = new TextView(mContext);
					title.setText(mContext.getString(R.string.comments));
					title.setPadding(10,5,0,0);
					title.setTextSize(18);
					title.setTextColor(Color.WHITE);
					if (mContext instanceof LocationActivity) {
						title.setTextColor(Color.parseColor("#33b5e5"));
					} else if (mContext instanceof TrailActivity) {
						title.setTextColor(Color.parseColor("#AA66CC"));
					}
					LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
					titleBar.addView(title,layout);
					layout = new LinearLayout.LayoutParams(48,48);
					titleBar.addView(addButton,layout);
				TextView divider = new TextView(mContext);
				divider.setBackgroundResource(R.drawable.divider);
				ListView commentList = new ListView(mContext);
				commentList.setBackgroundColor(Color.TRANSPARENT);
				commentList.setCacheColorHint(Color.TRANSPARENT);
				commentList.addHeaderView(titleBar);
				commentList.addFooterView(divider);
				commentList.setAdapter(commentsListAdapter);
				comments.addView(commentList);
			view.addView(background);
			view.addView(comments);
		return view;
	}
	
	public void newComment(final Object o) {
		AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
		alert.setTitle("New Comment");
		// Set an EditText view to get user input 
		final EditText input = new EditText(mContext);
		input.setLines(6);
		input.setGravity(Gravity.TOP);
		alert.setView(input);
		alert.setPositiveButton(mContext.getResources().getString(R.string.submit), new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			if (input.getText().toString().trim().length() > 0) {
				SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mContext);
				Comment comment = new Comment(input.getText().toString(),settings.getString("username",""));
				if (o instanceof Location) {
					addCommentFor((Location)o,comment);
					((Location)o).getComments().add(comment);
				} else if (o instanceof Trail) {
					addCommentFor((Trail)o,comment);
					((Trail)o).getComments().add(comment);
				}
				commentsList.add(0,comment);
			}
		  }
		});
		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
		    // Canceled.
		  }
		});
		alert.show();
	}
	
	private class CommentsListAdapter extends ArrayAdapter<Comment> {
		
		private List<Comment> _comments;

		public CommentsListAdapter(Context context, int textViewResourceId,	List<Comment> comments) {
			super(context, textViewResourceId, comments);
			_comments = comments;
		}
		
		@Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	        View v = convertView;
	        if (v == null) {
	            LayoutInflater vi = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            v = vi.inflate(R.layout.comments_list_item, null);
	        }
	      
	        Comment comment = _comments.get(position);
	        if (comment != null) {
	        	TextView user = (TextView) v.findViewById(R.id.user);
	        	TextView date = (TextView) v.findViewById(R.id.date);
	        	TextView commentText = (TextView) v.findViewById(R.id.comment);
	        	
	        	if (user != null) {
	        		user.setText(comment.getUser());
	        	}
	        	
	        	if (date != null) {
	        		date.setText(new SimpleDateFormat("MMM dd, yyyy").format(comment.getDate()));
	        	}
	        	
	        	if (commentText != null) {
	        		commentText.setText(comment.getComment());
	        	}
	        }
	        return v;
	    }
    }
}
	
