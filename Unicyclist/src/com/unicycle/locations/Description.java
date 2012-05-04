package com.unicycle.locations;

import com.unicycle.R;
import com.unicycle.R.drawable;
import com.unicycle.R.string;
import com.unicycle.locations.trails.Trail;
import com.unicycle.locations.trails.Trails;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class Description {

	//class used to display and manage Description and Directions fields in objects
	private Context mContext;
	private ViewFlipper page;
	private TextView descriptionView;
	private TextView directionsView;
	private Object object;
	private EditText input;
	
	public Description(Context context) {
		mContext = context;
	}
	
	public ViewGroup getView(Object o) {
		object = o;
		String description="";
		String directions="";
		int color=Color.parseColor("#FFFFFF");
		if (o instanceof Location) {
			description=((Location) o).getDescription();
			directions=((Location) o).getDirections();
			color=Color.parseColor("#33B5E5");
		}
		if (o instanceof Trail) {
			description=((Trail) o).getDescription();
			directions=((Trail) o).getDirections();
			color=Color.parseColor("#AA66CC");
		}

		RelativeLayout view = new RelativeLayout(mContext);
		ImageView background = new ImageView(mContext);
			background.setImageResource(R.drawable.background);
			background.setScaleType(ScaleType.FIT_START);
			background.setAdjustViewBounds(true);
		page = new ViewFlipper(mContext);
	    	page.setInAnimation(AnimationUtils.loadAnimation(mContext,android.R.anim.fade_in));
	    	page.setOutAnimation(AnimationUtils.loadAnimation(mContext, android.R.anim.fade_out));
		Gallery menu = new Gallery(mContext);
			menu.setId(1);
			menu.setAdapter(new MenuAdapter(mContext, new String[] {mContext.getString(R.string.description),mContext.getString(R.string.directions)}, color));
			menu.setOnItemSelectedListener( new OnItemSelectedListener() {
	            @Override
		        public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
		        	page.setDisplayedChild(position);
		        }
		        @Override
		        public void onNothingSelected(AdapterView<?> arg0) {
		            // Do nothing
		        }
	        });
		ScrollView scrollView1 = new ScrollView(mContext);
		descriptionView = new TextView(mContext);
			descriptionView.setText(description);
			descriptionView.setMinLines(6);
			descriptionView.setTextColor(Color.WHITE);
			descriptionView.setPadding(5,5,5,5);
			descriptionView.setLongClickable(true);
			descriptionView.setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View arg0) {
					AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
					alert.setTitle(mContext.getString(R.string.description));
					// Set an EditText view to get user input 
					input = new EditText(mContext);
					input.setText(descriptionView.getText());
					input.setLines(6);
					input.setGravity(Gravity.TOP);
					alert.setView(input);
					alert.setPositiveButton(mContext.getString(R.string.update), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						descriptionView.setText(input.getText().toString());
						if (object instanceof Location) {
							((Location) object).setDescription(input.getText().toString());
							Locations db = new Locations(mContext);
							db.updateLocation((Location) object);
							db.close();
						} else if (object instanceof Trail) {
							((Trail) object).setDescription(input.getText().toString());
							Trails db = new Trails(mContext);
							db.updateTrail((Trail)object);
							db.close();
						}
					  }
					});

					alert.setNegativeButton(mContext.getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
					  public void onClick(DialogInterface dialog, int whichButton) {
					    // Canceled.
					  }
					});
					alert.show();
					return false;
				}
			});
		ScrollView scrollView2 = new ScrollView(mContext);
		directionsView = new TextView(mContext);
			directionsView.setText(directions);
			directionsView.setLines(6);
			directionsView.setTextColor(Color.parseColor("#FFBB33"));
			directionsView.setPadding(5, 5, 5, 5);
			directionsView.setLongClickable(true);
			directionsView.setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View arg0) {
					AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
					alert.setTitle(mContext.getString(R.string.directions));
					// Set an EditText view to get user input 
					input = new EditText(mContext);
					input.setText(directionsView.getText());
					input.setLines(6);
					input.setGravity(Gravity.TOP);
					alert.setView(input);
					alert.setPositiveButton(mContext.getString(R.string.update), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						directionsView.setText(input.getText().toString());
						if (object instanceof Location) {
							((Location) object).setDirections(input.getText().toString());
							Locations db = new Locations(mContext);
							db.updateLocation((Location) object);
							db.close();
						} else if (object instanceof Trail) {
							((Trail) object).setDirections(input.getText().toString());
							Trails db = new Trails(mContext);
							db.updateTrail((Trail)object);
							db.close();
						}
					  }
					});
					alert.setNegativeButton(mContext.getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
					  public void onClick(DialogInterface dialog, int whichButton) {
					    // Canceled.
					  }
					});
					alert.show();
					return false;
				}
				
			});
		scrollView1.addView(descriptionView);
		scrollView2.addView(directionsView);
		LinearLayout.LayoutParams llayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,200);
		page.addView(scrollView1,llayout);
		page.addView(scrollView2,llayout);
		llayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		view.addView(background,llayout);
		view.addView(menu,llayout);
			RelativeLayout.LayoutParams rlayout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
			rlayout.addRule(RelativeLayout.BELOW,menu.getId());
		view.addView(page,rlayout);
		return view;
	}
	
	private class MenuAdapter extends BaseAdapter {
        private Context mContext;
        private String[] menuItems;
        private int menuColor;
        
        public MenuAdapter(Context c,String[] menuItemsText, int color) {
            mContext = c;
            menuItems = menuItemsText;
            menuColor = color;
        }

        public int getCount() {
            return menuItems.length;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

        	TextView tv = new TextView(mContext);
        	tv.setText(menuItems[position]);
        	tv.setMinWidth(300);
        	tv.setPadding(0, 0, 0,0);
        	tv.setTextSize(30);
        	tv.setGravity(Gravity.CENTER);
        	tv.setTextColor(menuColor);
        	tv.setTextScaleX(0.9f);
        	
        	return tv;
        }
    }	   
}
