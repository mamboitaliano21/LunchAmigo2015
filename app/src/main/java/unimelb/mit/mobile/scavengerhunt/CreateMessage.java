package unimelb.mit.mobile.scavengerhunt;

import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
// need to use this
// http://stackoverflow.com/questions/24611977/android-locationclient-class-is-deprecated-but-used-in-documentation/25173057#25173057

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.model.LatLng;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class CreateMessage extends ActionBarActivity implements 
		ConnectionCallbacks,
		OnConnectionFailedListener,
		OnMyLocationButtonClickListener {

	private EditText messageText;
	private EditText messageHint1;
	private EditText messageHint2;
	private EditText messageHint3;
	private TextView latitude;
	private TextView longitude;

	private EditText recipientNameText;
	public static final String AUTHPREFS = "authPrefs" ;
	private String sender;
	private String receiver;
	private List<String> hints;
	
	//For location From Chavi
	private LatLng currentLocation;
    // Denis we are changing into Location Requst instead of client
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_message);
		
		messageText = (EditText) findViewById(R.id.messageText);
		messageHint1 = (EditText) findViewById(R.id.locHint1);
		messageHint2 = (EditText) findViewById(R.id.locHint2);
		messageHint3 = (EditText) findViewById(R.id.locHint3);
		recipientNameText = (EditText) findViewById(R.id.recipientUsernameText);
		
		latitude = (TextView) findViewById(R.id.msgLatitude);
		longitude = (TextView) findViewById(R.id.msgLongitude);

		
		
		//Get Receiver information from Contacts Activity
		receiver = getIntent().getStringExtra("recipientEmail");
		recipientNameText.setText(receiver);
		recipientNameText.setEnabled(false);
		
		
		//GetSender and Receiver from previous activity
		// Restore preferences to get username global variable
	       SharedPreferences settings = getSharedPreferences(AUTHPREFS, 0);
		
	    //Manage this error, what happens when there is not preference
	    //for this key and the default value is taken. This should not happen
		sender = settings.getString("userEmail", "abc@error.com");
		
		//Now the hints
		hints = new ArrayList<String>();


	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_message, menu);
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
	
	//Now persist message
	public void sendMessage(View v)
	{
		//Asych Task to persist Message		
		AsyncTask<Message, Void, Boolean> messageDB = new dbCreateMessage();
		boolean messageSavedOK = false;

		//Insert Message
		try {
			//Code for generating timestamps Refactor and put it in a general class
			Calendar c = Calendar.getInstance();
			c.setTime(new Date()); // sets calendar time/date
			c.add(Calendar.HOUR_OF_DAY, 1);
			Date myDate = c.getTime();  // adds one hour
			Timestamp stamp = new Timestamp(myDate.getTime());	
			
			//Get hints form screen
			hints.add(messageHint1.getText().toString());
			hints.add(messageHint2.getText().toString());
			hints.add(messageHint3.getText().toString());

			

			String messageLocation = null;

            if(mLastLocation != null)
			{
			messageLocation =  mLastLocation.getLatitude() + "," +  mLastLocation.getLongitude();
			}
			
			
			Message m = new Message(messageText.getText().toString(), sender, receiver, MessageState.UNREAD, MessageNotificationState.UNNOTIFIED, messageLocation, stamp, hints);
			
			messageSavedOK = messageDB.execute(m).get();
				
				if(messageSavedOK)
				{
					Toast.makeText(this, "Message was Saved!!!", Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(this, "Message Failed, Sudden Death!!!", Toast.LENGTH_LONG).show();
				}	
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
    /**
     * Callback called when connected to GCore. Implementation of {@link ConnectionCallbacks}.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //setUpMapIfNeeded();

    }
    
    @Override
    public void onPause() {
        super.onPause();

    }

	@Override
	public boolean onMyLocationButtonClick() {
		// TODO Auto-generated method stub
        Toast.makeText(this, "Showing current location.", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}
	
}
