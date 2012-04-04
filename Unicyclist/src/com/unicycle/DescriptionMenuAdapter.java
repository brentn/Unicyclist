package com.unicycle;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DescriptionMenuAdapter extends BaseAdapter {
    private Context mContext;
    private String[] menuItems;
    
    public DescriptionMenuAdapter(Context c,String[] menuItemsText) {
        mContext = c;
        menuItems = menuItemsText;
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
    	tv.setTextColor(Color.parseColor("#33b5e5"));
    	tv.setTextScaleX(0.9f);
    	
    	return tv;
    }
}