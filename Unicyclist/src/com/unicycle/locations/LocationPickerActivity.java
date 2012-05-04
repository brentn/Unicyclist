package com.unicycle.locations;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.OverlayItem;
import com.unicycle.R;
import com.unicycle.R.drawable;
import com.unicycle.R.id;
import com.unicycle.R.layout;

public class LocationPickerActivity extends MapActivity {
	
	private MapView map;
	private ToggleButton satButton;
	private ToggleButton gpsButton;
	private LocationManager locationManager;
	private MyLocationListener locationListener;
	private MapOverlay mapOverlay;
	private MyLocationOverlay me;
	private boolean satelliteWas;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_picker);
        
        GeoPoint center = new GeoPoint((int) (getIntent().getDoubleExtra("latitude", 0)*1e6),(int) (getIntent().getDoubleExtra("longitude",0)*1e6));
        
        map = (MapView) findViewById(R.id.map);
        satButton = (ToggleButton) findViewById(R.id.satButton);
        gpsButton = (ToggleButton) findViewById(R.id.gpsButton);
        Button addButton = (Button) findViewById(R.id.addButton);
        Button cancelButton = (Button) findViewById(R.id.cancelButton);
        Drawable marker = getResources().getDrawable(R.drawable.ic_red_pin);
        marker.setBounds(0, -marker.getIntrinsicHeight(), marker.getIntrinsicWidth(), 0);
        satelliteWas = map.isSatellite();

        //Set up Listeners
        satButton.setChecked(false);
        satButton.setOnClickListener(new OnClickListener() {
        	@Override
        	public void onClick(View arg0) {
        		map.setSatellite(satButton.isChecked());
        	}
        });
        gpsButton.setChecked(false);
        gpsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (gpsButton.isChecked()) {
					locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
				} else {
					locationManager.removeUpdates(locationListener);
				}
			}
        });
        addButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				GeoPoint pt = mapOverlay.items.get(0).getPoint();
        		Intent _result = new Intent();
        		_result.putExtra("latitude", (pt.getLatitudeE6()/1e6));
        		_result.putExtra("longitude",(pt.getLongitudeE6()/1e6));
        		setResult(Activity.RESULT_OK,_result);
        		LocationPickerActivity.this.finish();
			}
        });
        cancelButton.setOnClickListener(new OnClickListener() {
        	@Override
        	public void onClick(View view) {
        		Intent _result = new Intent();
        		setResult(Activity.RESULT_CANCELED, _result);
        		LocationPickerActivity.this.finish();
        	}
        });
        
        //Set up Map
        map.setBuiltInZoomControls(true);
        map.getController().setZoom(getIntent().getIntExtra("zoom", 11));
        map.getController().animateTo(center);
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		locationListener = new MyLocationListener();
        map.setSatellite(false);
        mapOverlay = new MapOverlay(marker);
        map.getOverlays().add(mapOverlay);
        me = new MyLocationOverlay(this, map);
        map.getOverlays().add(me);
    }

	@Override
    protected boolean isRouteDisplayed() {
        return false;
 	}

    private class MyLocationListener implements LocationListener{

    	  public void onLocationChanged(android.location.Location argLocation) {
  	  	   // TODO Auto-generated method stub
  	  	   GeoPoint myGeoPoint = new GeoPoint(
  	  	    (int)(argLocation.getLatitude()*1000000),
  	  	    (int)(argLocation.getLongitude()*1000000));
  	  	   
  	  	   map.getController().animateTo(myGeoPoint);;
  	  	  }
  	
  	  	  public void onProviderDisabled(String provider) {
  	  	   // TODO Auto-generated method stub
  	  	  }
  	
  	  	  public void onProviderEnabled(String provider) {
  	  	   // TODO Auto-generated method stub
  	  	  }
  	
  	  	  public void onStatusChanged(String provider,
  	  	    int status, Bundle extras) {
  	  	   // TODO Auto-generated method stub
  	  	  }
  	 }
      
      private class MapOverlay extends ItemizedOverlay<OverlayItem> {
          private List<OverlayItem> items=new ArrayList<OverlayItem>();
          private Drawable marker=null;
          private OverlayItem inDrag=null;
          private ImageView dragImage=null;
          private int xDragImageOffset=0;
          private int yDragImageOffset=0;
          private int xDragTouchOffset=0;
          private int yDragTouchOffset=0;
          
          public MapOverlay(Drawable marker) {
            super(marker);
            this.marker=marker;
            
            dragImage=(ImageView)findViewById(R.id.drag);
            xDragImageOffset=0;
            yDragImageOffset=dragImage.getDrawable().getIntrinsicHeight();

            items.add(new OverlayItem(map.getMapCenter(),"New Location",""));

            populate();
          }
          
          @Override
          protected OverlayItem createItem(int i) {
            return(items.get(i));
          }
          
          @Override
          public void draw(Canvas canvas, MapView mapView, boolean shadow) {
            super.draw(canvas, mapView, false);
            marker.setBounds(0, -marker.getIntrinsicHeight(), marker.getIntrinsicWidth(), 0);
          }
          
          @Override
          public int size() {
            return(items.size());
          }
          
          @Override
          public boolean onTouchEvent(MotionEvent event, MapView mapView) {
            final int action=event.getAction();
            final int x=(int)event.getX();
            final int y=(int)event.getY();
            boolean result=false;
            
            if (action==MotionEvent.ACTION_DOWN) {
              for (OverlayItem item : items) {
                Point p=new Point(0,0);
                
                map.getProjection().toPixels(item.getPoint(), p);
                
                if (hitTest(item, marker, x-p.x, y-p.y)) {
              	  
                  result=true;
                  inDrag=item;
                  items.remove(inDrag);
                  populate();

                  xDragTouchOffset=0;
                  yDragTouchOffset=0;
                  
                  setDragImagePosition(p.x, p.y);
                  dragImage.setVisibility(View.VISIBLE);

                  xDragTouchOffset=x-p.x;
                  yDragTouchOffset=y-p.y;
                  
                  break;
                }
              }
            }
            else if (action==MotionEvent.ACTION_MOVE && inDrag!=null) {
              setDragImagePosition(x, y);
              result=true;
            }
            else if (action==MotionEvent.ACTION_UP && inDrag!=null) {
  	            dragImage.setVisibility(View.GONE);
  	            
  	            GeoPoint pt=map.getProjection().fromPixels(x-xDragTouchOffset,
  	                                                       y-yDragTouchOffset);
  	            OverlayItem toDrop=new OverlayItem(pt, inDrag.getTitle(),
  	                                               inDrag.getSnippet());
  	            
  	            items.add(toDrop);
  	            populate();
  	            
  	            inDrag=null;
  	            result=true;
            }
            return(result || super.onTouchEvent(event, mapView));
          }
          
          private void setDragImagePosition(int x, int y) {
            RelativeLayout.LayoutParams lp=
              (RelativeLayout.LayoutParams)dragImage.getLayoutParams();
                  
            lp.setMargins(x-xDragImageOffset-xDragTouchOffset,
                            y-yDragImageOffset-yDragTouchOffset, 0, 0);
            dragImage.setLayoutParams(lp);
          }
        }
      
      @Override
      protected void onStop() {
    	  map.setSatellite(satelliteWas);
    	  super.onStop();
      }


}
