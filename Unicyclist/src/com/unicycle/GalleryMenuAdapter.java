package com.unicycle;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class GalleryMenuAdapter extends BaseAdapter {
    private Context mContext;
    private String[] menuItems;
    private String color;
    
    public GalleryMenuAdapter(Context c,String[] menuItemsText, String menuColor) {
        mContext = c;
        menuItems = menuItemsText;
        color = menuColor;
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

    	Typeface tf = Typeface.createFromAsset(mContext.getAssets(),"fonts/Roboto-Thin.ttf");
    	TextView tv = new TextView(mContext);
    	tv.setText(menuItems[position]);
    	tv.setMinWidth(275);
    	tv.setPadding(40, 0, 40,0);
    	tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,50);
    	tv.setTypeface(tf);
    	tv.setGravity(Gravity.CENTER);
    	tv.setTextColor(Color.parseColor(color));
   	
    	return tv;
    }
}