package unimelb.mit.mobile.scavengerhunt;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

/**
 * This shows how to draw polylines on a map.
 */
public class SearchingMessage extends FragmentActivity
			implements
			ConnectionCallbacks,
			OnConnectionFailedListener,
			LocationListener,
			OnMyLocationButtonClickListener {
		    
    private GoogleMap mMap;
    
    private LatLng TargetLocation;
    private double latitudeTargetLocation;
    private double longitudeTargetLocation;
    private Polyline lineWithTarget;
    private Vibrator v;
    private LocationRequest mLocationRequest;
    private TextView mMessageView;   
    private String hint1;
    private String hint2;
    private String hint3;
    private String messageFrom;
    private String message;
    private String messageText;
    private String id;
    private int showedHint = 1;
    private GoogleApiClient mGoogleApiClient;

    protected void createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(3000);
        mLocationRequest.setFastestInterval(16);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searching_message);
        mMessageView = (TextView) findViewById(R.id.DistanceText);
        
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
        	id = extras.getString("message_id");
        	MessageDAO messageDAO = new MessageDAO();
        	Message msg=messageDAO.getMessagesById(id);
        	msg.setStatus(MessageState.UNDISCOVERED);
        	messageDAO.updateMessage(msg);
            hint1 = extras.getString("hint1");
            hint2 = extras.getString("hint2");
            hint3 = extras.getString("hint3");
            messageText = extras.getString("message");
            messageFrom = extras.getString("messageFrom");            
            latitudeTargetLocation = Double.parseDouble(extras.getString("targetLatitude"));
            longitudeTargetLocation = Double.parseDouble(extras.getString("targetLongitude"));
        }
        
        /*latitudeTargetLocation=-37.796092;
        longitudeTargetLocation=144.960700;*/
        
        CameraPosition targetCameraPosition =
                new CameraPosition.Builder().target(new LatLng(latitudeTargetLocation, longitudeTargetLocation))
                        .zoom(15.5f)
                        .bearing(0)
                        .tilt(50)
                        .build();

        TargetLocation = new LatLng(latitudeTargetLocation, longitudeTargetLocation);
        
        setUpMapIfNeeded();
        
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(targetCameraPosition));
        v = (Vibrator) SearchingMessage.this.getSystemService(Context.VIBRATOR_SERVICE);
        
        
        
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }
    
    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
                
                mMap.setMyLocationEnabled(true);
                mMap.setOnMyLocationButtonClickListener(this);
            }
        }
    }

    private void setUpMap() {    	
        LatLng Test2 = new LatLng(-37.788800, 144.947700);
        
    	lineWithTarget=mMap.addPolyline((new PolylineOptions())
                .add(TargetLocation, Test2)
                .color(Color.RED));
    	mMap.addMarker(new MarkerOptions()
		        .position(TargetLocation)
		        .title("Hidden Message")
		        .snippet("Catch me!")
		        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(SYDNEY));
    }



    /**
     * Button to get current Location. This demonstrates how to get the current Location as required
     * without needing to register a LocationListener.
     */
    public void showMyLocation(View view) {
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            String msg = "Location = " + mLastLocation;
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Implementation of {@link LocationListener}.
     */
    @Override
    public void onLocationChanged(Location location) {
        //mMessageView.setText("Location = " + location);
        
        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
        lineWithTarget.remove();
    	lineWithTarget=mMap.addPolyline((new PolylineOptions())
                .add(TargetLocation, currentLocation)
                .color(Color.RED)); 
    	
    	//Calculate the original distance
        Location targetLocation = new Location("targetLocation");        
        
        targetLocation.setLatitude(latitudeTargetLocation);
        targetLocation.setLongitude(longitudeTargetLocation);
        
        float distance = location.distanceTo(targetLocation);
        //String message = "";        
        
        if (distance<40){
        	//message = "You got it. Press the button to see the message!";
        	Intent intent = new Intent(this, Congratulations.class);
        	intent.putExtra("messageFrom", messageFrom);
    		intent.putExtra("message", messageText);
    		intent.putExtra("message_id", id);
    		startActivity(intent);
        	v.cancel();
        }
        else if (distance<100)
        {
        	message = "You are almost there. Distance: " + distance;
        	v.vibrate(1000);
        	try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	v.cancel();
        }
        else if (distance<200)
        {
        	message = "You are getting closer. Distance: " + distance;
        	v.vibrate(1000);
        	try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	v.cancel();
        }
        else{        	//
        	message = "Keep searching. Distance: " + distance;
        	v.cancel();
        }
        mMessageView.setText(message);
    }

    /**
     * Callback called when connected to GCore. Implementation of {@link ConnectionCallbacks}.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        //need to use mRequestingLocationUpdates Denis
        // http://developer.android.com/training/location/receive-location-updates.html
        startLocationUpdates();
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    /**
     * Callback called when disconnected from GCore. Implementation of {@link ConnectionCallbacks}.
     */
    @Override
    public void onDisconnected() {
        // Do nothing
    }

    /**
     * Implementation of {@link OnConnectionFailedListener}.
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Do nothing
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "Showing current location.", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }
    
    public void showHint(View view){
    	String message ="";
    	if (showedHint==1){
    		message = "Hint 1: " + hint1;
    	}
    	else if (showedHint ==2){
    		message = "Hint 2: " + hint2;
    	}
    	else if (showedHint ==3){
    		message = "Hint 3: " + hint3;
    		showedHint=0;
    	}
    	showedHint = showedHint +1;
    	
    	Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

}
