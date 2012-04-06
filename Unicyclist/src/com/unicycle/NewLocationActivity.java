package com.unicycle;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;
import android.widget.ViewFlipper;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.OverlayItem;


public class NewLocationActivity extends MapActivity {

	private MapView locationPicker=null;
	private LocationPickerOverlay locationPickerOverlay;
	private MyLocationOverlay me=null;
	private MyLocationListener locationListener;
	private LocationManager locationManager;
	private ToggleButton satButton;
	private ToggleButton gpsButton;
	private ViewFlipper flipper;
	private EditText name;
	private EditText description;
	private EditText directions;
	private RatingBar rating;
	private Button addButton;
	private Button cancelButton;
	private double latitude;
	private double longitude;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_location);
        
        //Find view variables
        locationPicker = (MapView) findViewById(R.id.locationPicker);
        satButton = (ToggleButton) findViewById(R.id.satButton);
        gpsButton = (ToggleButton) findViewById(R.id.gpsButton);
        addButton = (Button) findViewById(R.id.addButton);
        cancelButton = (Button) findViewById(R.id.cancelButton);
        flipper = (ViewFlipper) findViewById(R.id.newlocationflipper);
        name = (EditText) findViewById(R.id.name);
        description = (EditText) findViewById(R.id.description);
        directions = (EditText) findViewById(R.id.directions);
        rating = (RatingBar) findViewById(R.id.rating);
        Drawable marker = getResources().getDrawable(R.drawable.marker);
        marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());
        

        //Set up Listeners
        satButton.setOnClickListener(new OnClickListener() {
        	@Override
        	public void onClick(View arg0) {
        		locationPicker.setSatellite(satButton.isChecked());
        	}
        });
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
        	public void onClick(View arg0) {
        		Intent _result = new Intent();
        		_result.putExtra("name",name.getText().toString());
        		_result.putExtra("latitude", latitude);
        		_result.putExtra("longitude",longitude);
        		_result.putExtra("description",description.getText().toString());
        		_result.putExtra("directions",directions.getText().toString());
        		_result.putExtra("rating",(int) (rating.getRating() * 2));
        		setResult(Activity.RESULT_OK,_result);
        		NewLocationActivity.this.finish();
        	}
        });
        cancelButton.setOnClickListener(new OnClickListener() {
        	@Override
        	public void onClick(View arg0) {
        		Intent _result = new Intent();
        		setResult(Activity.RESULT_CANCELED,_result);
        		NewLocationActivity.this.finish();
        	}
        });
        
        //Set up Map
        locationPicker.setBuiltInZoomControls(true);
        locationPicker.getController().setZoom(11);
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		locationListener = new MyLocationListener();
        locationPicker.setSatellite(false);
        locationPickerOverlay = new LocationPickerOverlay(marker,this);
        locationPicker.getOverlays().add(locationPickerOverlay);
        me = new MyLocationOverlay(this, locationPicker);
        locationPicker.getOverlays().add(me);
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
	  	   
	  	   locationPicker.getController().animateTo(myGeoPoint);;
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
    
    private GeoPoint getPoint(double lat, double lon) {
        return(new GeoPoint((int)(lat*1000000.0),
                              (int)(lon*1000000.0)));
    }
    
    private class LocationPickerOverlay extends ItemizedOverlay<OverlayItem> {
        private List<OverlayItem> items=new ArrayList<OverlayItem>();
        private Drawable marker=null;
        private OverlayItem inDrag=null;
        private ImageView dragImage=null;
        private int xDragImageOffset=0;
        private int yDragImageOffset=0;
        private int xDragTouchOffset=0;
        private int yDragTouchOffset=0;
        private Context mContext;
        
        public LocationPickerOverlay(Drawable marker,Context c) {
          super(marker);
          this.marker=marker;
          
          mContext = c;
          dragImage=(ImageView)findViewById(R.id.drag);
          xDragImageOffset=dragImage.getDrawable().getIntrinsicWidth()/2;
          yDragImageOffset=dragImage.getDrawable().getIntrinsicHeight();

          items.add(new OverlayItem(locationPicker.getMapCenter(),"New Location",""));

          populate();
        }
        
        @Override
        protected OverlayItem createItem(int i) {
          return(items.get(i));
        }
        
        @Override
        public void draw(Canvas canvas, MapView mapView,
                          boolean shadow) {
          super.draw(canvas, mapView, shadow);
          
          boundCenterBottom(marker);
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
              
              locationPicker.getProjection().toPixels(item.getPoint(), p);
              
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
	            
	            GeoPoint pt=locationPicker.getProjection().fromPixels(x-xDragTouchOffset,
	                                                       y-yDragTouchOffset);
	            OverlayItem toDrop=new OverlayItem(pt, inDrag.getTitle(),
	                                               inDrag.getSnippet());
	            
	            items.add(toDrop);
	            populate();
	            
	            inDrag=null;
	            result=true;
	            if ((event.getEventTime() - event.getDownTime()) < 100) {
	                new AlertDialog.Builder(mContext)
	                .setIcon(android.R.drawable.ic_dialog_map)
	                .setTitle(R.string.location_picker)
	                .setMessage(R.string.is_this_location_correct)
	                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

	                    @Override
	                    public void onClick(DialogInterface dialog, int which) {
	                    	GeoPoint pt = locationPicker.getProjection().fromPixels(x-xDragTouchOffset,y-yDragTouchOffset);
	    	            	latitude = (pt.getLatitudeE6()/1e6);
	    	            	longitude = (pt.getLongitudeE6()/1e6);
	    	            	flipper.setInAnimation(AnimationUtils.loadAnimation(getBaseContext(),android.R.anim.fade_in));
	    	                flipper.setOutAnimation(AnimationUtils.loadAnimation(getBaseContext(), android.R.anim.fade_out));
	    	            	flipper.showNext();
	                    }

	                })
	                .setNegativeButton(R.string.no, null)
	                .show();
	            }
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
}


