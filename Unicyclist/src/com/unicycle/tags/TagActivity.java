package com.unicycle.tags;

import java.util.List;

import com.unicycle.R;
import com.unicycle.UnicyclistApplication;
import com.unicycle.R.id;
import com.unicycle.R.layout;
import com.unicycle.locations.Location;
import com.unicycle.locations.LocationsListAdapter;
import com.unicycle.locations.trails.Trail;
import com.unicycle.locations.trails.TrailsListAdapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class TagActivity extends Activity {
	
	private Tag _tag;
	ProgressDialog pd = null;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int tagType = getIntent().getIntExtra("tagType",0);
        setContentView(R.layout.tag);
        
        Tags tags = new Tags(TagActivity.this);
        String name = getIntent().getStringExtra("tagName");
        _tag = tags.findTagByName(name);

        //get views
        TextView tagName = (TextView) findViewById(R.id.tagName);
        ListView locationsView = (ListView) findViewById(R.id.locationsList);
        ListView trailsView = (ListView) findViewById(R.id.trailsList);
        ListView featuresView = (ListView) findViewById(R.id.featuresList);

        tagName.setText(_tag.getName());

        //get list
        switch (tagType) {
        case Tag.LOCATION_TAG:
            ((ViewGroup) findViewById(R.id.locations)).setVisibility(ViewGroup.VISIBLE);
            List<Location> locationsList = tags.getLocationsForTag(_tag);
            LocationsListAdapter locationsListAdapter = new LocationsListAdapter(((UnicyclistApplication) getApplication()).getMyLocation(),TagActivity.this, R.layout.locations_list_item, locationsList);
            locationsView.setAdapter(locationsListAdapter);
        	break;
        case Tag.TRAIL_TAG:
            ((ViewGroup) findViewById(R.id.trails)).setVisibility(ViewGroup.VISIBLE);
        	List<Trail> trailsList = tags.getTrailsForTag(_tag);
            TrailsListAdapter trailsListAdapter = new TrailsListAdapter(TagActivity.this, R.layout.trails_list_item, trailsList);
            trailsView.setAdapter(trailsListAdapter);
    		break;
        case Tag.FEATURE_TAG:
            ((ViewGroup) findViewById(R.id.tags)).setVisibility(ViewGroup.VISIBLE);
//      	List<Feature> featuresList = tags.getFeaturesForTag(_tag);
//      	FeaturesListAdapter featuresListAdapter = new FeaturesListAdapter(TagActivity.this, R.layout.features_list_item, featuresList);
//      	featuresView.setAdapter(featuresListAdapter);
        	break;
        }

	}
        
}
