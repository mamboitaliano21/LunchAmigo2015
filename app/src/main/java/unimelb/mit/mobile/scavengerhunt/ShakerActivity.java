package unimelb.mit.mobile.scavengerhunt;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import unimelb.mit.mobile.scavengerhunt.R;
import unimelb.mit.mobile.scavengerhunt.ShakeDetector.OnShakeListener;


public class ShakerActivity extends Activity {
	
	/* initialize the class */
	private ShakeDetector mShakeDetector;
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private Button bananaButton;
	private Boolean firstTime; /* prevent toomuch timer */
	private LocationListener locationListener; /* gps */
	private LocationManager locationManager;
	private double mLongitude;
	private double mLatitude;
	
	//For DB Management
	private UserDAO userDAO;
	private User amigoUser;
	
	//For SharedPrefrence(Gelocationlatlong) (persisting state ofu users between activities
	SharedPreferences sharedpreferences;
	private static final String AUTHPREFS = "authPrefs" ;
	private static final String GEOLOCATIONKEY = "geoKey"; 
	private CharSequence geoLocation;
	
	Boolean userUpdateOK;
	

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shaker);

		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		bananaButton = (Button) findViewById(R.id.bananaButton);
		firstTime = true;
		
		//store the geolocation on this
		sharedpreferences = getSharedPreferences(AUTHPREFS, Context.MODE_PRIVATE);
		
		
		// Acquire a reference to the system Location Manager
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

		// Define a listener that responds to location updates
		locationListener = new LocationListener() {
		    public void onLocationChanged(Location location) {
		      // Called when a new location is found by the network location provider.
		      //makeUseOfNewLocation(location);
				mLatitude = location.getLatitude();
				mLongitude = location.getLongitude();

		    }

		    public void onStatusChanged(String provider, int status, Bundle extras) {

		    }

		    public void onProviderEnabled(String provider) {}

		    public void onProviderDisabled(String provider) {}
		  };

		// Register the listener with the Location Manager to receive location updates

		  if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
		  {
			  locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
		  }

		if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
		    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
		}

		mShakeDetector = new ShakeDetector(new OnShakeListener() {
			
			@Override
			public void onShake() {
				// TODO Auto-generated method stub
				if (firstTime == true) {
					findFriends();
				
				}
			}
		});
		

		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.shaker, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onResume() {
		super.onResume();
		mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);

		if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
		{
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10, 0, locationListener);
		}
		if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {	
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10, 0, locationListener);		

		}
		firstTime = true;
		
	}
	
	@Override
	public void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(mShakeDetector);
		locationManager.removeUpdates(locationListener);

	}
	
	public void bananaButton(View view) {
		//to make sure multiple shake can only be registered as once !
		if (firstTime == true) {
			findFriends();
		}
	}
	
	/* todo server stuff */
	public void findFriends() {
		
		CountDownTimer cdt = new CountDownTimer(6000, 1000) {

			public void onTick(long millisUntilFinished) {
				bananaButton.setBackgroundResource(R.drawable.banana_half);

			}

			public void onFinish() {
				Double latitude = mLatitude;
				Double longitude = mLongitude;
			    geoLocation = latitude.toString() + "," + longitude.toString();
				
				userDAO = new UserDAO();
				//SharedPreferences sharedPrefrence = PreferenceManager.getDefaultSharedPreferences(this);

				//String userName = sharedPreference.getString("emailKey",null);
				amigoUser = userDAO.getUser(getSharedPreferences("authPrefs", MODE_PRIVATE).getString("emailKey",null));
				//TODO
				if(amigoUser == null){
					finish();
				}
				
				//Get Curren time
				 Date dNow = new Date( ); // Instantiate a Date object
				 Calendar cal = Calendar.getInstance();
				 cal.setTime(dNow);
				 cal.add(Calendar.HOUR, 1);
				 dNow = cal.getTime();	
				
				// append the changes locally
				amigoUser.setAvailability(true);
				amigoUser.setGeolocationLatLong((String) geoLocation);
				amigoUser.setAvailableUntil(dNow);
				storeGeoLocation();
				
				
				// send the changes to the server
				AsyncTask<User, Void, Boolean> updateUserToDB = new dbUpdateUserTask();
				try {
					userUpdateOK = updateUserToDB.execute(amigoUser).get();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				
				
					
				bananaButton.setBackgroundResource(R.drawable.banana);
				//
				locationManager.removeUpdates(locationListener);
				startIntent();
			}

		}.start();
		
		//to make sure multiple shake can only be registered as once !
		firstTime = false;

	}
	
	/* start the next intent */
	public void startIntent() {
		Intent intent = new Intent(this, ContactsActivity.class);
		startActivity(intent);
	}
	
	
	private void storeGeoLocation() {
		Editor editor = sharedpreferences.edit();
	    editor.putString(GEOLOCATIONKEY, (String) geoLocation);
	    editor.commit();
	}
	
	

}
